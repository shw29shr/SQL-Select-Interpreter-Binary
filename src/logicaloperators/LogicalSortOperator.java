package logicaloperators;

import net.sf.jsqlparser.statement.select.OrderByElement;
import planbuilders.PhysicalPlanBuilder;

import java.util.ArrayList;


/**
 * Class for Logical Sort Operator.
 * It extends Logical Unary Operator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class LogicalSortOperator extends LogicalUnaryOperator {
    public ArrayList<OrderByElement> orderElements = null;


    public LogicalSortOperator(ArrayList<OrderByElement> oe, LogicalOperator onlyChild) {
        super(onlyChild);
        this.orderElements = oe;

    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        pb.visit(this);
    }
}
