package operators;

import java.io.IOException;
import java.util.List;

import fileformats.FileTupleWriter;
import fileformats.TupleWriter;
import models.Tuple;

/**
 * Base Abstract class for all Operators
 * 
 * @author Saarthak Chandra - sc2776 Shweta Shrivastava - ss3646 Vikas P
 *         Nelamangala - vpn6
 * 
 */
public abstract class Operator {
	public abstract Tuple getNextTuple();

	public abstract List<String> getSchema();

	public abstract void reset();

	public List<String> schema = null;

	/**
	 * Print Every table row If we are calling the FileTupleWriter then we also
	 * need to close the buffer For BinaryTupleWriter, it is taken care inside
	 * the functionality
	 * 
	 * @param writer
	 *            The FileWriter or BinaryWriter object which will print the
	 *            results
	 * @throws IOException
	 */
	public void dump(TupleWriter writer) throws IOException {
		Tuple tuple = null;
		boolean isLast = false;
		while (true) {
			// writer.dump(tuple);
			tuple = getNextTuple();
			if (tuple == null) {
				isLast = true;
				writer.dump(tuple, isLast);
				break;
			}
			writer.dump(tuple, isLast);
		}

		if (writer instanceof FileTupleWriter) {
			((FileTupleWriter) writer).close();
		}
	}

}
