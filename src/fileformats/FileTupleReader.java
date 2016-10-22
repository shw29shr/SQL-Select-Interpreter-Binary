package fileformats;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import catalog.DBCatalog;
import models.Tuple;

/**
 * Concrete implementation for TupleReader
 * To read tuples from text/csv files
 * 
 * @author
 * Saarthak Chandra - sc2776 
 * Shweta Shrivastava - ss3646 
 * Vikas P Nelamangala - vpn6
 *
 */
public class FileTupleReader implements TupleReader {

	private String fileName;
	private BufferedReader tableBuffer = null;

	/**
	 * Constructor to create the FileTupleReader object that will return the
	 * next tuple, takes the filename and creates a buffer on top of it
	 * 
	 * @param fileName
	 * 				file with the table data
	 */
	public FileTupleReader(String fileName) {
		this.fileName = fileName;
		this.tableBuffer = createTableBuffer(fileName);
	}

	/**
	 * Creates a buffer reader on the table file we need to read
	 * 
	 * @param fileName
	 *            File to read the table data from
	 * @return Reader buffer
	 */
	public static BufferedReader createTableBuffer(String fileName) {
		try {
			String actualTableName = ((DBCatalog.aliases).containsKey(fileName)) ? (DBCatalog.aliases).get(fileName)
					: fileName.trim();
			return new BufferedReader(new FileReader((DBCatalog.dataDirectory) + actualTableName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the next tuple, from the buffer by calling the readLine() on the buffer, 
	 * It then splits the read row to generate an array on integers to represent the tuple
	 */
	public Tuple getNextTuple() {
		String row = null;
		try {
			row = tableBuffer.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (row == null)
			return null;
		String[] rowvalues = row.split(",");
		int[] tupleValues = new int[rowvalues.length];
		for (int i = 0; i < rowvalues.length; i++) {
			tupleValues[i] = Integer.parseInt(rowvalues[i]);
		}
		Tuple tup = new Tuple(tupleValues);
		return tup;
	};
	
	/**
	 * Reset the table buffer by forst closing the existing buffer
	 * and then reopening a new one
	 */
	public void reset() {
		try {
			tableBuffer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tableBuffer = createTableBuffer(fileName);
	}

}
