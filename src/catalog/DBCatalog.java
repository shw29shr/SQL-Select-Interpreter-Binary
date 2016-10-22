package catalog;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.SQLCustomErrorHandler;
import models.Table;
import utils.PropertyFileReader;

/**
 * The DBCatalog class implements a singleton pattern and provides an instance
 * of the Catalog consisting of schema, directories etc..
 *
 * @authors Saarthak Chandra - sc2776 Shweta Shrivastava - ss3646 Vikas P
 *          Nelamangala - vpn6
 *
 */
public class DBCatalog {
	private static DBCatalog catalogInstance;

	public static String inputDirectory = "";
	public static String outputDirectory = "";
	public static String dbDirectory;
	public static String dataDirectory;
	public static String schemaPath;
	public static String queryPath;

	static PropertyFileReader reader = PropertyFileReader.getInstance();
	public static Map<String, ArrayList<String>> schemas = new HashMap<String, ArrayList<String>>();
	public static Map<String, String> aliases = new HashMap<String, String>();

	SQLCustomErrorHandler handler = SQLCustomErrorHandler.getCatalogInstance();


	/**
	 * Instantiate the constructor.
	 */
	private DBCatalog() {
		createDirectories(inputDirectory, outputDirectory);
	}

	/**
	 * Create the input and output directories.
	 *
	 * @param newInputDirectory
	 *            takes input directory
	 * @param newOutputDirectory
	 *            takes output directory
	 */
	public static void createDirectories(String newInputDirectory, String newOutputDirectory) {
		inputDirectory = newInputDirectory;
		outputDirectory = newOutputDirectory;
		dbDirectory = newInputDirectory + File.separator + reader.getProperty("dbSubDirectory") + File.separator;
		dataDirectory = dbDirectory + reader.getProperty("dataSubDirectory") + File.separator;
		schemaPath = dbDirectory + reader.getProperty("schemaFileName");
		queryPath = inputDirectory + File.separator + reader.getProperty("queriesFileName");
		DBCatalog.createSchema();
	}

	/**
	 * Returns DBCatalog instance. The Singleton implementation ensures that
	 * there exists only 1 version of catalog at any point of time.
	 *
	 * @return The instance of the class
	 */
	public static DBCatalog getCatalogInstance() {
		// System.out.println("get catalog called");
		if (catalogInstance == null) {
			catalogInstance = new DBCatalog();
		}
		return catalogInstance;
	}

	/**
	 * Read form the given schema file and generate schema for each table
	 * present The generated set of schemas is stored as a Map<String,
	 * ArrayList<String>> where the key of the map is the table name and the
	 * value is an array of column names
	 */
	public static void createSchema() {
		// System.out.println("Create Schema Called....");
		BufferedReader br;
		schemas.clear();
		try {
			br = new BufferedReader(new FileReader(schemaPath));
			String row = null;
			while ((row = br.readLine()) != null) {
				String[] tokens = row.split(" ");
				String key = tokens[0];
				ArrayList<String> columnNames = new ArrayList<String>();
				for (int i = 1; i < tokens.length; i++) {
					columnNames.add(tokens[i]);
				}
				schemas.put(key, columnNames);
				// System.out.println("key=" + key + " ....and.... value=" +
				// columnNames);
			}

			br.close();
		} catch (Exception ex) {
			SQLCustomErrorHandler handler = SQLCustomErrorHandler.getCatalogInstance();
			handler.printCustomError(ex, "DBCatalog");
			// TODO Auto-generated catch block
			// ex.printStackTrace();
		}
	}

	/**
	 * Get the schema for a given tablename/alias
	 *
	 * @param tabName
	 *            Name of the table/alias for the table
	 * @return Corresponding schema object (represented as List<String>)
	 */
	public static List<String> getSchema(String tabName) {
		String fileName;
		if (aliases.containsKey(tabName))
			fileName = aliases.get(tabName);
		else
			fileName = tabName.trim();
		return schemas.get(fileName);
	}

	/**
	 * Creates a table object with given table name and schema details.
	 *
	 * @param tableName
	 *            Name of the table
	 * @return Table object comprising of table name, table schema and a buffer
	 *         to read from the corresponding data file
	 * @throws IOException
	 */
	public static Table getTableObject(String tableName) throws IOException {
		return new Table(tableName, getSchema(tableName));
	}

}
