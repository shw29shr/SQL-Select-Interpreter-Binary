package operators;

import expvisitors.ExpressionEvaluator;
import expvisitors.JoinVisitor;
import models.Tuple;
import net.sf.jsqlparser.expression.Expression;

import java.util.List;

/**
 * Class for Join Operator Join Operator is created when query has more than one
 * table which are being joined by a given join condition Join Operator extends
 * the Binary operator class and has two child operators
 *
 * @author Saarthak Chandra - sc2776
 *         Shweta Shrivastava - ss3646
 *         Vikas P Nelamangala - vpn6
 */
public class JoinOperator extends BinaryOperator {
    Expression joinCondition;
    Tuple leftTuple = null;
    Tuple rightTuple = null;
    Tuple joinedTuple = null;
    List<String> leftOperatorSchema = null;
    List<String> rightOperatorSchema = null;
    JoinVisitor joinVisitor;

    /**
     * Constructor for Join operator Takes the left and right child operators of
     * the join Extracts the next tuple and schema for either child operator and
     * stores it
     *
     * @param leftChild     The operator which represents the left child of the join
     *                      operator
     * @param joinCondition The expression which represents the Join condition
     * @param rightChild    The operator which represents the right child of the join
     *                      operator
     */
    public JoinOperator(Operator leftChild, Expression joinCondition, Operator rightChild) {
        super(leftChild, rightChild);
        this.joinCondition = joinCondition;
        leftTuple = leftChild.getNextTuple();
        rightTuple = rightChild.getNextTuple();
        leftOperatorSchema = leftChild.getSchema();
        rightOperatorSchema = rightChild.getSchema();
    }

    /**
     * Concatenate two tuples
     *
     * @param tuple1 First tuple for concatenation
     * @param tuple2 Second tuple for concatenation
     * @return The concatenated tuple
     */
    public Tuple concatenateTuples(Tuple tuple1, Tuple tuple2) {
        // System.out.println("Concatenate Tuples called......");
        int size1 = tuple1.getSize();
        int size2 = tuple2.getSize();
        int[] fields = new int[size1 + size2];
        int k = 0;
        int x = 0;
        while (x < size1) {
            fields[k] = tuple1.values[x];
            x++;
            k++;
        }
        int m = 0;
        while (m < size2) {
            fields[k] = tuple2.values[m];
            m++;
            k++;
        }
        return new Tuple(fields);
    }

    /**
     * Implementing the tuple-nested loop If join condition is null, simply take
     * cross product. Else evaluate the associated join condition first; if
     * true, only then concatenate the tuples
     *
     * @return The joined tuple
     */
    @Override
    public Tuple getNextTuple() {
        joinedTuple = null;
        while (leftTuple != null && rightTuple != null) {
            if (joinCondition == null)
                joinedTuple = concatenateTuples(leftTuple, rightTuple);
            else {
                joinVisitor = new JoinVisitor(leftTuple, rightTuple, leftOperatorSchema, rightOperatorSchema);
                if (ExpressionEvaluator.evaluateJoinExpression(joinCondition, joinVisitor))
                    joinedTuple = concatenateTuples(leftTuple, rightTuple);
            }

            // Now scan the table on the right iteratively by keeping the left
            // table fixed.
            // Reset the table on the right as soon as the end is reached
            if (leftTuple != null)
            // Keep checking the RHS table.
            {
                if (rightTuple != null)
                    rightTuple = rightchild.getNextTuple();
                if (rightTuple == null) {
                    leftTuple = leftchild.getNextTuple();
                    rightchild.reset();
                    rightTuple = rightchild.getNextTuple();
                }
            }
            if (joinedTuple != null)
                return joinedTuple;
        }

        return null;
    }

}
