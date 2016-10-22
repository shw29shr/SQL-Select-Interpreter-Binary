package logicaloperators;

import net.sf.jsqlparser.expression.Expression;
import planbuilders.PhysicalPlanBuilder;


/**
 * Class for Logical Select Operator
 * The SelectOperator will only be created when query has a WHERE condition in it
 * Logical SelectOperator extends the Logical Unary Operator and has one child which is the ScanOperator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalSelectOperator extends LogicalUnaryOperator {

    public Expression expression = null;

    public LogicalSelectOperator(LogicalOperator lo, Expression e) {
        super(lo);
        this.expression = e;
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        //Need to fill this.
        pb.visit(this);
    }
}
