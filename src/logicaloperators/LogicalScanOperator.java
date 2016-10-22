package logicaloperators;

import models.Table;
import planbuilders.PhysicalPlanBuilder;

/**
 * Logical Scan Operator class
 * It does not have any children and directly extends the Logical Operator class
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class LogicalScanOperator extends LogicalOperator {
    public Table currentTable = null;

    public LogicalScanOperator(Table t) {
        this.currentTable = t;
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        pb.visit(this);
    }
}
