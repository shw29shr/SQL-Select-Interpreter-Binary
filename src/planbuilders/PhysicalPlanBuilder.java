package planbuilders;

import logicaloperators.*;
import operators.*;

/**
 * Class for Physical Plan Builder.
 * Implements visit methods of all Logical Operators.
 * Each visit method in turn provides concrete implementations of Operators.
 * Physical Operators are called inside Logical Operators.
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class PhysicalPlanBuilder {
    private Operator physicalOperator = null;


    public void visit(LogicalScanOperator scanOperator) {
        physicalOperator = new ScanOperator(scanOperator.currentTable);
    }

    public void visit(LogicalSelectOperator lso) {
        //Every select operator has one child scan operator.
        //Call accept and visit on its child first.
        physicalOperator = null;
        lso.onlyChild.accept(this);
        Operator child = physicalOperator;
        physicalOperator = new SelectOperator((ScanOperator) child, lso.expression);
    }

    public void visit(LogicalProjectOperator lpo) {
        // ProjectOperator extends the UnaryOperator and has one child which maybe the ScanOperator or SelectOperator.
        // If query has a WHERE clause, the SelectOperator is the child. If it doesn't, ScanOperator is the child
        physicalOperator = null;
        lpo.onlyChild.accept(this);
        Operator child = physicalOperator;

        physicalOperator = new ProjectOperator(lpo.selectItemList, child);
    }

    public void visit(LogicalJoinOperator ljo) {
        Operator leftChild, rightChild;

        //Evaluate left sub tree.
        physicalOperator = null;
        ljo.left.accept(this);
        leftChild = physicalOperator;

        //Now evaluate right sub tree
        physicalOperator = null;
        ljo.right.accept(this);
        rightChild = physicalOperator;

        // Use Existing TNLJ JOIN Operator
        // Need to implement different JOIN implementations here......
        //physicalOperator=null;
        physicalOperator = new JoinOperator(leftChild, ljo.expr, rightChild);

    }

    public void visit(LogicalSortOperator lso) {
        // Act on its child.
        physicalOperator = null;
        lso.onlyChild.accept(this);
        Operator child = physicalOperator;
        physicalOperator = new SortOperator(lso.orderElements, child);
    }

    public void visit(LogicalDuplicateEliminationOperator ldeo) {
        physicalOperator = null;
        ldeo.onlyChild.accept(this);
        Operator child = physicalOperator;
        physicalOperator = new DuplicateEliminationOperator(child);
    }

    public void visit(LogicalHashDuplicateEliminationOperator hdeo) {
        physicalOperator = null;
        hdeo.onlyChild.accept(this);
        Operator child = physicalOperator;
        physicalOperator = new HashDuplicateEliminationOperator(child);
    }

    // Returns the Physical Operator
    public Operator getPhysicalOperator() {
        return physicalOperator;
    }

}
