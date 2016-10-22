package fileformats;

import models.Tuple;

/**
 * Interface for TupleReader
 * This object is created to read the tuples from the underlying relations
 * Has 2 concrete implementations -
 * - FileTupleReader - To read tuples from csv files
 * - BinaryTupleReader - To read tuples from binary files
 * Mandates implementation of 2 methods -
 * - getNextTuple() - to read the nect tuple from the underlying relation
 * - reset() - to reset the read head when the first tuple needs to be read again
 * 
 * @author 
 * Saarthak Chandra - sc2776 
 * Shweta Shrivastava - ss3646 
 * Vikas P Nelamangala - vpn6
 *
 */
public interface TupleReader {

	public Tuple getNextTuple();
	public void reset();

}
