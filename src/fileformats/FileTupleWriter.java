package fileformats;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import models.Tuple;

/**
 * Concrete implementation for TupleWriter To write output tuples as text/csv
 * files
 * 
 * @author Saarthak Chandra - sc2776 Shweta Shrivastava - ss3646 Vikas P
 *         Nelamangala - vpn6
 *
 */

public class FileTupleWriter implements TupleWriter {

	private String fileName;
	private PrintStream printstream;

	/**
	 * Constructor for FileTupleWriter Takes the complete fileName for output
	 * and then creates an Output stream on top of it
	 * 
	 * @param fileName
	 *            output filename
	 */
	public FileTupleWriter(String fileName) {
		// TODO Auto-generated constructor stub
		this.fileName = fileName;
		createOutputPrintStream(this.fileName);
	}

	/**
	 * Create the output print stream
	 * 
	 * @param fileName
	 *            fileName for the output file
	 */
	public void createOutputPrintStream(String fileName) {

		File file = new File(fileName);
		try {
			this.printstream = new PrintStream(new BufferedOutputStream(new FileOutputStream(file)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Private function used to build a comma separated values string out of the
	 * array of integers representing the tuple
	 * 
	 * @param values
	 *            Integer array representing the tuple values
	 * @return String which has comma separated values
	 */

	private String printTuple(int[] values) {
		if (values.length < 1)
			return "";
		StringBuilder sb = new StringBuilder(String.valueOf(values[0]));
		int i = 1;
		while (i < values.length) {
			sb.append(',');
			sb.append(String.valueOf(values[i++]));
		}
		return sb.toString();
	}

	/**
	 * Write out the tuples in the print stream
	 * 
	 * @param tuple
	 *            The tuple that needs to be written out
	 */
	@Override
	public void dump(Tuple tuple, Boolean isLast) {
		// TODO Auto-generated method stub
		try {
			if (!isLast) {
				String str = printTuple(tuple.values) + "\n";
				this.printstream.write(str.getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Close the existing printstream when all rows have finished writing out
	 * Called by the Operator when its evaluation is done
	 */
	public void close() {
		this.printstream.close();
	}

}
