package fileformats;

import models.Tuple;
import org.apache.log4j.Logger;
import utils.PropertyFileReader;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Concrete implementation for TupleWriter To write output tuples as binary
 * files
 *
 * @author Saarthak Chandra - sc2776 Shweta Shrivastava - ss3646 Vikas P
 *         Nelamangala - vpn6
 */

public class BinaryTupleWriter implements TupleWriter {

    PropertyFileReader reader = PropertyFileReader.getInstance();
    Logger logger = Logger.getLogger(BinaryTupleWriter.class);
    private String fileName;
    private FileOutputStream fout;
    private FileChannel fc;
    private ByteBuffer buffer;
    private int counter = 0;
    private int bufferSize;
    private int variableSize;
    private int tuplesInOnePage;
    private int numAttr;

    /**
     * Constructor for BinaryTupleWriter Takes the complete fileName for output
     * and then creates an Output channel on top of it
     *
     * @param fileName Filename to write the query output to
     */
    public BinaryTupleWriter(String fileName) {
        // TODO Auto-generated constructor stub
        this.fileName = fileName;
        createOutputChannel(this.fileName);
        this.bufferSize = new Integer(reader.getProperty("bufferSize"));
        this.variableSize = new Integer(reader.getProperty("variableSize"));
        this.buffer = ByteBuffer.allocate(this.bufferSize);
    }

    /**
     * Write the output to this file, by creating a FileChannle, as required by
     * JavaNIO
     *
     * @param fileName filename to write the query output to
     */
    public void createOutputChannel(String fileName) {
        try {
            this.fout = new FileOutputStream(fileName);
            this.fc = this.fout.getChannel();
        } catch (FileNotFoundException ex) {
            if (logger.isDebugEnabled())
                ex.printStackTrace();
        }
    }

    /**
     * Write the actual tuple to the BinaryOutput format to the file name
     * provided to its constructor
     *
     * @param tuple the tuple to be written
     * @throws IOException
     */
    @Override
    public void dump(Tuple tuple, Boolean isLast) throws IOException {

        if (isLast && counter == 0) {
            return;
        }
        // first call here
        if (!isLast && counter == 0) {
            numAttr = tuple.getSize();
            buffer.putInt(numAttr);
            // Putting a dummy value now, to be
            buffer.putInt(numAttr);
            // overridden
            counter = counter + 2; // We add 2 since 1 is for the num of
            // attributes, 1 is for the number of Rows,
            // which we know only at the end
            calculateTupleLimit();
        }

        if (!isLast && counter > 0 && (counter - 2) / numAttr < this.tuplesInOnePage) { // we
            // have
            // space,
            // so add
            for (int i = 0; i < tuple.getSize(); i++) {
                buffer.putInt(tuple.getValue(i));
                counter++;
            }
        }
        // We reached the max limit but buffer has space, so pad 0's
        else {
            while (buffer.hasRemaining()) {
                buffer.putInt(0);
            }

            // Now we need to update the number of rows in the page
            buffer.putInt(4, (counter - 2) / numAttr);
            buffer.clear();
            fc.write(buffer); // write current buffer to channel
            counter = 0;
            buffer.clear();
            buffer = ByteBuffer.allocate(this.bufferSize);
            buffer.clear();
            // ensure the last tuple, which saw a full page is written as well
            if (isLast)
                return;
            dump(tuple, isLast);
        }

    }

    /**
     * This function tells us how many tuples can be written in one page, given
     * the buffer size and length of one tuple
     */
    private void calculateTupleLimit() {
        // variableSize here is that of int, 4 , as mentioned in the Property
        // file !!
        // We multiple by 2 since we subtract space needed to write the first 2
        // integers representing the num of attributes as well as the number of
        // rows in one page.
        int tupleSize = this.variableSize * this.numAttr;
        tuplesInOnePage = (this.bufferSize - (this.variableSize * 2)) / tupleSize;
    }

    public void close() {

    }

    ;

}
