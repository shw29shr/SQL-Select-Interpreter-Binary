package logicaloperators;

import net.sf.jsqlparser.expression.Expression;
import planbuilders.PhysicalPlanBuilder;

/**
 * Class for Logical Join Operator
 * Join Operator is created when query has more than one table
 * which are being joined by a given join condition
 * Logical Join Operator extends the Logical Binary operator class
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalJoinOperator extends LogicalBinaryOperator {

    public Expression expr = null;

    public LogicalJoinOperator(LogicalOperator left, LogicalOperator right, Expression e) {
        super(left, right);
        this.expr = e;
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        pb.visit(this);
    }
}
