package fileformats;

import java.io.IOException;
import models.Tuple;


/**
 * Interface for TupleWriter
 * This object is created to write the resultant tuples 
 * Has 2 concrete implementations -
 * - FileTupleWriter - To write tuples as text/csv files
 * - BinaryTupleWriter - To write tuples as binary files
 * Mandates implementation of one method -
 * - dump() - to write out the tuples
 * 
 * @author 
 * Saarthak Chandra - sc2776 
 * Shweta Shrivastava - ss3646 
 * Vikas P Nelamangala - vpn6
 *
 */

public interface TupleWriter {

	public void dump(Tuple tuple,Boolean isLast) throws IOException;
	public void close();

}
