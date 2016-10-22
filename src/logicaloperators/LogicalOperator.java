package logicaloperators;

import planbuilders.PhysicalPlanBuilder;

/**
 * Base Abstract class for all Logical Operators
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public abstract class LogicalOperator {
    public abstract void accept(PhysicalPlanBuilder pb);
}
