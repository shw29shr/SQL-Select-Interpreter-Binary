package fileformats;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import catalog.DBCatalog;
import models.Tuple;
import utils.PropertyFileReader;

/**
 * Concrete implementation for TupleReader
 * To read tuples from binary files
 * 
 * @author
 * Saarthak Chandra - sc2776 
 * Shweta Shrivastava - ss3646 
 * Vikas P Nelamangala - vpn6
 *
 */

public class BinaryTupleReader implements TupleReader {

	private String fileName;
	private FileInputStream fin;
	private FileChannel tableChannel = null;
	private int bytesRead;
	private int counter = 0;
	private int numAttr = 0;
	private int numRows = 0;
	private int rowsToRead = 0;
	private int valuesRead = 0;

	private int bufferSize;
	private ByteBuffer buffer;
	// This holds the size in bytes of the data type we are writing, like its 4
	// for int (1 int is 4 bytes)
	private int variableSize;

	Logger logger = Logger.getLogger(BinaryTupleReader.class);
	PropertyFileReader reader = PropertyFileReader.getInstance();

	/**
	 * 
	 * Constructor to create the BinaryTupleReader object that will return the
	 * next tuple The Reader will create a file channel and then buffers of size
	 * - 4096, as per the property file. Then it will read integers from the
	 * buffer while the buffer has remaining data, and keep returning tuples to
	 * the caller.
	 * 
	 * @param fileName
	 *            file with the table data
	 * @throws IOException
	 */
	public BinaryTupleReader(String fileName) throws IOException {
		this.fileName = fileName;
		createTableChannel(fileName);
		this.bufferSize = new Integer(reader.getProperty("bufferSize"));
		this.variableSize = new Integer(reader.getProperty("variableSize"));
		this.buffer = ByteBuffer.allocate(this.bufferSize);
		this.bytesRead = (this.tableChannel).read(this.buffer);
		this.buffer.flip();
	}

	/**
	 * 
	 * Creates a table channel on the table file we need to read
	 * 
	 * @param fileName
	 *            File to read the table data from
	 * @throws IOException
	 */
	public void createTableChannel(String fileName) {
		try {

			String actualTableName = ((DBCatalog.aliases).containsKey(fileName)) ? (DBCatalog.aliases).get(fileName)
					: fileName.trim();

			this.fin = new FileInputStream((DBCatalog.dataDirectory) + actualTableName);
			this.tableChannel = fin.getChannel();
			// fc.read( buffer );
			// return fc;
		} catch (Exception e) {
			if (logger.isTraceEnabled())
				e.printStackTrace();
		}
	}

	/**
	 * Returns the next tuple, from the buffer by checking the number of
	 * attributes, and returning those many int values from the buffer
	 * 
	 */
	public Tuple getNextTuple() {
		Tuple row = null;
		try {
			int[] tupleValues;// = new int[numAttr];

			if (bytesRead != -1) {
				// while(true){

				if (!this.buffer.hasRemaining()) {
					this.buffer.rewind(); // make buffer ready for writing
					// this.buffer.clear();
					bytesRead = this.tableChannel.read(this.buffer);
					this.buffer.flip();

					if (!this.buffer.hasRemaining() || bytesRead == -1) {
						return row;
					}
				}

				if (this.buffer.hasRemaining()) {

					while (true) {

						if (counter == 0 || (counter % (this.bufferSize / this.variableSize)) == 0) {
							// System.out.println(this.buffer.get(0));
							numAttr = this.buffer.getInt();
							if (logger.isTraceEnabled())
								System.out.println(numAttr + " --- attr ");
						} else if (counter == 1 || (counter % (this.bufferSize / this.variableSize)) == 1) {
							numRows = this.buffer.getInt();
							rowsToRead = numRows * numAttr;
							valuesRead = 0;
							if (logger.isTraceEnabled())
								System.out.println(numRows + " --- rows ");
							
						} else if (rowsToRead > 0 && valuesRead < rowsToRead) {
							tupleValues = new int[numAttr];
							for (int i = 0; i < numAttr; i++) {
								tupleValues[i] = this.buffer.getInt();
								valuesRead++;
							}

							if (logger.isTraceEnabled())
							 printTuple(tupleValues);

							row = new Tuple(tupleValues);
							counter++;
							break;
						}

						counter++;

						if (!this.buffer.hasRemaining()) {
							this.buffer.rewind(); // make buffer ready for
							// writing
							// this.buffer.clear();
							bytesRead = this.tableChannel.read(this.buffer);
							this.buffer.flip();

							if (!this.buffer.hasRemaining() || bytesRead == -1) {
								break;
							}
						}
					}
					// counter++;
					// break;
				}

			}

		} catch (IOException ex) {
			if (logger.isDebugEnabled())
				ex.printStackTrace();
		}

		return row;
	}

	/**
	 * Used while debugging, to print the tuple values being returned.
	 * 
	 * @param tupleValues
	 *            array containing the tuple values, which are returned to the
	 *            caller of getNextTuple
	 * 
	 */
	private void printTuple(int[] tupleValues) {
		for (int i = 0; i < numAttr; i++) {
			System.out.print(tupleValues[i] + " ");
		}
		System.out.println();
	}

	/**
	 * Close the File channel after the table fileChannel has been fully read We
	 * create a new table channel, assign a buffer, and prepare it for the next
	 * read.
	 */
	public void reset() {
		try {
			this.tableChannel.close();
			this.fin.close();
			this.buffer = ByteBuffer.allocate(4096);
			createTableChannel(this.fileName);
			this.bytesRead = (this.tableChannel).read(this.buffer);
			this.buffer.flip();
		} catch (IOException e) {
			if (logger.isDebugEnabled())
				e.printStackTrace();
		}

		// Reset all the variables, for the next reading
		this.counter = 0;
		this.numAttr = 0;
		this.numRows = 0;
		this.rowsToRead = 0;
		this.valuesRead = 0;
	}
}