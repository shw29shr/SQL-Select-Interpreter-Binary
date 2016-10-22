package logicaloperators;

import planbuilders.PhysicalPlanBuilder;

/**
 * Logical Duplicate Elimination Operator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalDuplicateEliminationOperator extends LogicalUnaryOperator {


    public LogicalDuplicateEliminationOperator(LogicalOperator onlyChild) {
        super(onlyChild);
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {

        pb.visit(this);
    }
}
