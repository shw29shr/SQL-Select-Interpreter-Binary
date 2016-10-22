package utils;

import catalog.DBCatalog;
import helpers.SelectExecutorHelper;
import logicaloperators.*;
import models.Table;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import operators.Operator;
import planbuilders.PhysicalPlanBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class builds the tree, handles all the aliases as well. This class has a
 * constructor called for every single statement that is provided as input
 *
 * @authors Saarthak Chandra sc2776 Shweta Shrivastava ss3646 Vikas P
 * Nelamangala vpn6
 */

public class SelectExecutor {

    private static int i = 1;
    public Select selectStat;
    public Distinct distinctElements;
    public PlainSelect plainSelect;
    public List<SelectItem> selectElements;
    public FromItem from;
    public List<Join> joins;
    public Expression where;
    public ArrayList<OrderByElement> orderElements;
    public List<String> tableList = new ArrayList<String>();
    public List<Expression> ands = null;
    public HashMap<String, List<Expression>> selectCondition = null, joinCondition = null;
    // These lists hold, a table and its corresponding list of concatenated
    // conditions
    // Used in tree-building
    public HashMap<String, Expression> selectConditionList, joinConditionList;
    public Operator root = null;

    /**
     * Select Executor constructor initialization. Parses the input query
     * statements one by one, builds the operator tree and evaluates
     *
     * @param statement Input query statement
     * @throws IOException
     */
    public SelectExecutor(Statement statement) throws IOException {
        selectStat = (Select) statement;
        plainSelect = (PlainSelect) selectStat.getSelectBody();

        initializeSelectBodyParsing();
        // Get all the tables in the query, that will be after "FROM"
        addTablesToTableList(from);
        // If we have joins, we get the tables out of them too...
        if (joins != null)
            addTablesInJoinCondition(joins);

        // After where we have list of Ands, so we loop through that
        ands = SelectExecutorHelper.getListOfExpressionsAnds(where);
        // Collections.reverse(ands);

        addAndsToSelectJoinLists();

        for (String table : tableList) {
            addTableToSelectOrJoinList(table);
        }

        generateTree();

        clearArraysForNextIteration();
    }

    /**
     * Return the table object according to its position in the query FROM
     * clause
     *
     * @param tableIndex Index in the FROM clause
     * @return Table object
     * @throws IOException
     */
    private Table getTableObject(int tableIndex) throws IOException {
        Table table = DBCatalog.getTableObject(tableList.get(tableIndex));
        // System.out.println("Table Name="+table.tableName);
        return table;
    }

    /**
     * For each expression in the ands, we add the corresponding expressions to
     * the joiCondition/selectCondition Each AND condition can have multiple
     * tables, so we add them to list of select, if it has one condition, like
     * S.A=3 OR We add them to list of joins, if it has more than one condition,
     * like S.A=B.A
     */
    private void addAndsToSelectJoinLists() {
        for (Expression exp : ands) {
            List<String> tables = SelectExecutorHelper.getTabsInExpression(exp);
            int ctr = maxIndex(tables);
            if (tables == null)
                joinCondition.get(tableList.get(tableList.size() - selectCondition.size() - 1)).add(exp);

                // Only one table means we do a select
            else if (tables.size() == 1)
                selectCondition.get(tableList.get(ctr)).add(exp);
            else
                joinCondition.get(tableList.get(ctr)).add(exp);
        }
    }

    /**
     * Get the highest index of all the tables in the FROM clause
     *
     * @param tables The tables present in the FROM clause
     * @return The maximum index
     */
    private int maxIndex(List<String> tables) {
        if (tables == null)
            return tableList.size() - 1;
        int pos = 0;
        for (String tab : tables) {
            // pos = Math.max(pos, tableList.indexOf(tab));
            pos = (tableList.indexOf(tab) > pos) ? tableList.indexOf(tab) : pos;
        }
        return pos;
    }

    /**
     * Given the position of a table, return the select condition associated
     * with it
     *
     * @param pos Position/index
     * @return The associated select condition
     */
    private Expression getSelectCondition(int pos) {
        return selectConditionList.get(tableList.get(pos));
    }

    /**
     * Given the position of a table, return the join condition associated with
     * it
     *
     * @param pos Position/index
     * @return The associated join condition
     */
    private Expression getJoinCond(int pos) {
        return joinConditionList.get(tableList.get(pos));
    }

    /**
     * Add all the select/join operator nodes in the parse tree
     *
     * @param currentRoot Current temp root in the parse tree so far
     * @return
     * @throws IOException
     */
    private LogicalOperator processAllSelectAndJoins(LogicalOperator currentRoot) throws IOException {
        while (i < tableList.size()) {
            LogicalOperator scanOp = new LogicalScanOperator(getTableObject(i));
            if (getSelectCondition(i) != null) {
                scanOp = new LogicalSelectOperator((LogicalScanOperator) scanOp, getSelectCondition(i));
            }
            // Ensure left-associativity in join, we add left child as the
            // current table .
            // Then we add the right join with a scan parent (as tables are
            // always leaves and have a scan immediately above them)
            currentRoot = new LogicalJoinOperator(currentRoot, scanOp, getJoinCond(i));
            i++;

        }
        return currentRoot;
    }

    /**
     * Method which implements the core logic of the SQL interpreter Builds the
     * operator tree in a bottom up fashion Inline comments explain the logic
     *
     * @throws IOException
     */
    private void generateTree() throws IOException {
        // System.out.println("Inside Genreate Tree....");

        // base of our tree is the scan operator on a table

        LogicalOperator currentRoot = getSelectCondition(0) != null
                ? new LogicalSelectOperator(new LogicalScanOperator(getTableObject(0)), getSelectCondition(0))
                : new LogicalScanOperator(getTableObject(0));

        // Now the bottom part of the tree is done, we have all scan operators
        // on tables,
        // and we have each scan operator's parent as a select(with a
        // condition), or a join(with a condition)

        if (tableList.size() > 1) {
            currentRoot = processAllSelectAndJoins(currentRoot);
        }

        // After all the selects, and join are over, we look for order
        // by,distinct
        boolean orderAllSelectedColumns = SelectExecutorHelper.selectAllOrderColumns(selectElements, orderElements);

        if (orderElements != null) {
            if (orderAllSelectedColumns) {
                currentRoot = new LogicalProjectOperator(new LogicalSortOperator(orderElements, currentRoot),
                        selectElements);
            } else {
                currentRoot = new LogicalSortOperator(orderElements,
                        new LogicalProjectOperator(currentRoot, selectElements));
            }
        } else // no order-by so just select all columns
            currentRoot = new LogicalProjectOperator(currentRoot, selectElements);

        // Tree root can be distinct , so we finally check for that

		/*
         * For distinct elements, we need to ensure they are sorted. If we have
		 * already sorted(since we found a orderBy on the way up the tree), we
		 * do not add SortOperator Else,we add a SortOperator first and then do
		 * a duplicate
		 */

        if (distinctElements != null) {
            // We need to sort through and through
            currentRoot = new LogicalSortOperator(new ArrayList<OrderByElement>(), currentRoot);
            currentRoot = new LogicalDuplicateEliminationOperator(currentRoot);
            if (orderElements != null) {
                currentRoot = new LogicalSortOperator(orderElements, currentRoot);
            }
        }

        // Invoke P
        PhysicalPlanBuilder planObject = new PhysicalPlanBuilder();
        currentRoot.accept(planObject);
        root = planObject.getPhysicalOperator();
        // System.out.println("Came out of Generate Tree....");
    }

    /**
     * Here, we add all the tables into a list of tables called tableList, -
     * using the "from" elements of the query that jsqlParser returns We also
     * take care of adding aliases here
     *
     * @param fromItem List of fromItems from the query
     */
    private void addTablesToTableList(FromItem fromItem) {
        DBCatalog.aliases.clear(); // reset previously set aliases
        addTableNameToTableListAndCatalog(fromItem);
        selectCondition.put(SelectExecutorHelper.returnTableOrAlias(fromItem), new ArrayList<Expression>());
    }

    /**
     * Adds the first table in the FROM list of the query to the table list
     *
     * @param fromItem The first item in the FROM clause
     */
    private void addTableNameToTableListAndCatalog(FromItem fromItem) {
        String tabNameOrAlias = SelectExecutorHelper.returnTableOrAlias(fromItem);
        if (fromItem.getAlias() != null) { // check if we have an alias
            // System.out.println(item.toString());
            DBCatalog.aliases.put(fromItem.getAlias(), SelectExecutorHelper.getSingleTableName(fromItem));
        }

        tableList.add(tabNameOrAlias);
        joinCondition.put(tabNameOrAlias, new ArrayList<Expression>());
        addTableToSelectOrJoinCondition(tabNameOrAlias);
    }

    /**
     * Function to add all the tables, the ones that are involved in joins We
     * also add all the joinConditions
     *
     * @param joins List of Joins returned by the JSQLParser
     */
    private void addTablesInJoinCondition(List<Join> joins) {
        for (Join join : joins) { // loop through all the joins
            FromItem joinFromItem = join.getRightItem();
            addTableNameToTableListAndCatalog(joinFromItem);
        }

    }

    /**
     * Put into selectCondition list and joinConditionList what the table name
     * and corresponding expression is.
     *
     * @param tabNameOrAlias Table name / its alias
     */
    private void addTableToSelectOrJoinList(String tabNameOrAlias) {
        selectConditionList.put(tabNameOrAlias,
                SelectExecutorHelper.getConcatenatedAnds(selectCondition.get(tabNameOrAlias)));
        joinConditionList.put(tabNameOrAlias,
                SelectExecutorHelper.getConcatenatedAnds(joinCondition.get(tabNameOrAlias)));
    }

    /**
     * Adds the given table name to a potential list of select and joins
     *
     * @param tabNameOrAlias Table name / its alias
     */
    private void addTableToSelectOrJoinCondition(String tabNameOrAlias) {
        selectCondition.put(tabNameOrAlias, new ArrayList<Expression>());
        joinCondition.put(tabNameOrAlias, new ArrayList<Expression>());
    }

    /**
     * Use jsqlParser , to get out all the parts of the current statement being
     * executed Also initialize our select and join condition HashMaps
     */
    private void initializeSelectBodyParsing() {
        distinctElements = plainSelect.getDistinct();
        selectElements = plainSelect.getSelectItems();
        from = plainSelect.getFromItem();
        joins = plainSelect.getJoins();
        where = plainSelect.getWhere();
        orderElements = (ArrayList<OrderByElement>) plainSelect.getOrderByElements();

        selectCondition = new HashMap<String, List<Expression>>();
        joinCondition = new HashMap<String, List<Expression>>();

        selectConditionList = new HashMap<String, Expression>();
        joinConditionList = new HashMap<String, Expression>();
    }

    /**
     * Clear up the list of arrays, so we start afresh for the next statement
     * that is passed in
     */
    private void clearArraysForNextIteration() {
        selectCondition.clear();
        joinCondition.clear();
        selectConditionList.clear();
        joinConditionList.clear();
        // Since we have one table atleast, we check for other tables.. This is
        // an index into the array holding all tables
        i = 1;
    }

}
