package logicaloperators;

/**
 * Logical Unary Operator abstract class for operators which will have one child
 * Currently it is implemented by the Logical Select, Logical Sort, Logical Project, Logical DuplicateElimination and Logical HashDuplicateElimination Operators
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */

public abstract class LogicalUnaryOperator extends LogicalOperator {
    public LogicalOperator onlyChild = null;

    public LogicalUnaryOperator(LogicalOperator lo) {
        this.onlyChild = lo;
    }

}
