package models;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import catalog.DBCatalog;
import fileformats.BinaryTupleReader;
import fileformats.FileTupleReader;
import fileformats.TupleReader;
import utils.PropertyFileReader;

/**
 * Table object to handle all table level operations Every table will have a
 * name and schema associated with it We also create a buffer through which we
 * connect to the data file of the table and read the tuples
 *
 * @author Saarthak Chandra - sc2776 Shweta Shrivastava - ss3646 Vikas P
 *         Nelamangala - vpn6
 *
 */
public class Table {

	PropertyFileReader reader = PropertyFileReader.getInstance();
	public String tableName;
	public List<String> tableSchema = null;
	private TupleReader ftr;

	/**
	 * Constructor class for table object
	 *
	 * @param tableName
	 *            Name of the table
	 * @param tableSchema
	 *            Schema of the table
	 * @throws IOException
	 */
	public Table(String tableName, List<String> tableSchema) throws IOException {
		this.tableName = tableName;
		this.tableSchema = tableSchema;
		if (reader.getProperty("isBinary").equalsIgnoreCase("true"))
			this.ftr = new BinaryTupleReader(tableName);
		else
			this.ftr = new FileTupleReader(tableName);
	}

	/**
	 * Get next tuple from the buffer
	 * Calls the getNextTuple of the File/Binary Reader object
	 *
	 * @return Next tuple
	 */
	public Tuple getNextTuple() {
		return ftr.getNextTuple();
	}

	/**
	 * Method to reset the read head in a given table In order to reset, close
	 * the existing buffer and reset it again
	 * Calls the reset of the File/Binary Reader object
	 */
	public void reset() {
		ftr.reset();
	}

}
