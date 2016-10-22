package logicaloperators;

import net.sf.jsqlparser.statement.select.SelectItem;
import planbuilders.PhysicalPlanBuilder;

import java.util.List;

/**
 * Class for Logical Project Operator
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public class LogicalProjectOperator extends LogicalUnaryOperator {

    public List<SelectItem> selectItemList = null;

    public LogicalProjectOperator(LogicalOperator onlyChild, List<SelectItem> selectItemList) {
        super(onlyChild);
        this.selectItemList = selectItemList;
    }

    @Override
    public void accept(PhysicalPlanBuilder pb) {
        // Need to fill this
        pb.visit(this);
    }
}
