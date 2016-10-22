package error;

import catalog.DBCatalog;

/**
 * 
 * This class is used to call custom error handing, in the project..  
 * We can print custom messages by using this.
 * 
 * @authors 
 * Saarthak Chandra - sc2776
 * Shweta Shrivastava - ss3646
 * Vikas P Nelamangala - vpn6
 */

public class SQLCustomErrorHandler extends Exception {
    
	private static SQLCustomErrorHandler errorHandlerInstance;
	
	private SQLCustomErrorHandler () {
    
    }
    
    public static SQLCustomErrorHandler getCatalogInstance() {
		// System.out.println("get catalog called");
		if (errorHandlerInstance == null) {
			errorHandlerInstance = new SQLCustomErrorHandler();
		}
		return errorHandlerInstance;
	}
    
    /**
     * Calls the constructor of parent class Exception
	 * @param message
	 * 				Message sent for printing error
	 */
    public SQLCustomErrorHandler (String message) {
        super (message);
    }

    /**
     * Print the error class and reason to the sysOut 
     * 
     * @param cause
     * 				Here we print the cause of the error
     * @param message
     * 				Here we pass in the class name where the error is handled
     */
    
    public void printCustomError (Throwable cause,String message) {
        System.out.print("Class is - "+ message);
        System.out.println(" Error is - "+ cause.getMessage());
    }

    /**
     * Call the super constructor , if we need to do so. 
     * 
     * @param cause
     * 				Here we print the cause of the error
     * @param message
     * 				Here we pass in the class name where the error is handled
     */
    public SQLCustomErrorHandler (String message, Throwable cause) {
        super (message, cause);
    }
}