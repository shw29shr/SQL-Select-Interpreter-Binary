package logicaloperators;

import planbuilders.PhysicalPlanBuilder;

/**
 * Class for Logical Hash Duplicate Elimination Operator.
 * It extends Logical Unary Operator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalHashDuplicateEliminationOperator extends LogicalUnaryOperator {

    public LogicalHashDuplicateEliminationOperator(LogicalOperator onlyChild) {
        super(onlyChild);
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {

        pb.visit(this);
    }
}
