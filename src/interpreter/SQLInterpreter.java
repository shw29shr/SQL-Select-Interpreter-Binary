package interpreter;

import java.io.File;
import java.io.FileReader;

import org.apache.log4j.Logger;

import catalog.DBCatalog;
import error.SQLCustomErrorHandler;
import fileformats.BinaryTupleWriter;
import fileformats.FileTupleWriter;
import fileformats.TupleWriter;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import utils.PropertyFileReader;
import utils.SelectExecutor;

/**
 * The SqlInterpreter class in the entry point for the SQLInterpreter. The
 * main() function takes in as arguments the location of the sample inputs It
 * reads the queries one by one and generates output
 * 
 * @authors Saarthak Chandra - sc2776 Shweta Shrivastava - ss3646 Vikas P
 *          Nelamangala - vpn6
 *
 */
public class SQLInterpreter {

	SQLCustomErrorHandler handler = SQLCustomErrorHandler.getCatalogInstance();
	PropertyFileReader reader = PropertyFileReader.getInstance();
	Logger logger = Logger.getLogger(SQLInterpreter.class);

	private static String FILE_NAME;

	/**
	 * Output of the parser with the given input / output directory.
	 * 
	 * @param inPath
	 *            input directory
	 * @param outPath
	 *            output directory
	 * 
	 */
	public void parse(String inPath, String outPath) {
		DBCatalog.createDirectories(inPath, outPath);
		DBCatalog.getCatalogInstance();

		try {
			CCJSqlParser sqlparser = new CCJSqlParser(new FileReader(DBCatalog.queryPath));
			Statement querystatement;
			int querycounter = 1;
			while ((querystatement = sqlparser.Statement()) != null) {
				try {
					String fileName = DBCatalog.outputDirectory + File.separator + FILE_NAME + querycounter;

					SelectExecutor selectExecutor = new SelectExecutor(querystatement);
					long beginTime = System.currentTimeMillis();

					TupleWriter writer;
					if (reader.getProperty("isBinary").equalsIgnoreCase("true"))
						writer = new BinaryTupleWriter(fileName);
					else
						writer = new FileTupleWriter(fileName);
					selectExecutor.root.dump(writer);

					long endTime = System.currentTimeMillis();
					// Print to file here

					if (logger.isInfoEnabled())
						logger.info("Time taken by query - " + querycounter + " is - " + (endTime - beginTime)
								+ " milliseconds");

					// So we know number of query output files we created.
					querycounter++;

				} catch (Exception ex) {
					if (logger.isDebugEnabled()) {
						logger.debug(ex);
					}
					handler.printCustomError(ex, this.getClass().getSimpleName());

					// Ensure one failure does not halt execution so use
					// continue to process further
					querycounter++;
					continue;
				}
			}
		} catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug(ex);
			}
			handler.printCustomError(ex, this.getClass().getSimpleName());
		}
	}

	/**
	 * Set the output file prefix, read from the property file.
	 */
	private void setOutFilePath() {
		FILE_NAME = reader.getProperty("outputFilePrefix");
	}

	/**
	 * Create output directory folder if it does not exist
	 * 
	 * @param outputDirectory
	 *            Directory provided as input from command line
	 * 
	 */
	private void createOutputDirecoryIfNotExists(String outputDirectory) {
		new File(outputDirectory).mkdirs();
	}

	/**
	 * The main function invoked by jar.
	 * 
	 * @param args
	 *            Argument list
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			throw new IllegalArgumentException("Incorrect input format, only " + args.length + " provided");
		}

		SQLInterpreter sqlInterpret = new SQLInterpreter();
		sqlInterpret.setOutFilePath();
		sqlInterpret.createOutputDirecoryIfNotExists(args[1]);
		sqlInterpret.parse(args[0], args[1]);

	}
}
