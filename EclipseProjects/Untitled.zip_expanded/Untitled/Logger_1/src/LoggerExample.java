import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

class LoggerExample {

	private static final Logger LOGGER = Logger.getLogger(LoggerExample.class.getName());

	public static void main(String[] args) throws SecurityException, IOException {
		// A logger can be simply used to send the error message to console
		LOGGER.info("Logger Name: "+LOGGER.getName());
		LOGGER.warning("Can cause ArrayIndexOutOfBoundsException");
		int []a = {1,2,3};
		int index = 4;
		LOGGER.config("index is set to "+index);
		try{
			System.out.println(a[index]);
		}catch(ArrayIndexOutOfBoundsException ex){
			LOGGER.log(Level.SEVERE, "Exception occur", ex);
		}
		
		
		// Using logger with handlers
		Handler consoleHandler = null;
		Handler fileHandler  = null;
		try{
			//Creating consoleHandler and fileHandler
			consoleHandler = new ConsoleHandler();
			fileHandler  = new FileHandler("./javacodegeeks.log");
			
			//Assigning handlers to LOGGER object
			LOGGER.addHandler(consoleHandler);
			LOGGER.addHandler(fileHandler);
			
			//Setting levels to handlers and LOGGER
			consoleHandler.setLevel(Level.ALL);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);
			
			LOGGER.config("Configuration done.");
			
			//Console handler removed
			LOGGER.removeHandler(consoleHandler);
			
			LOGGER.log(Level.FINE, "Finer logged");
		}catch(IOException exception){
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}
		LOGGER.finer("Finest example on LOGGER handler completed.");

		
		// Using logger with formatter
		fileHandler = null;
		Formatter simpleFormatter = null;
		try{
			
			// Creating FileHandler
			fileHandler = new FileHandler("./javacodegeeks.formatter.log");
			
			// Creating SimpleFormatter
			simpleFormatter = new SimpleFormatter();
			
			// Assigning handler to logger
			LOGGER.addHandler(fileHandler);
			
			// Logging message of Level info (this should be publish in the default format i.e. XMLFormat)
			LOGGER.info("Finnest message: Logger with DEFAULT FORMATTER");
			
			// Setting formatter to the handler
			fileHandler.setFormatter(simpleFormatter);
			
			// Setting Level to ALL
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);
			
			// Logging message of Level finest (this should be publish in the simple format)
			LOGGER.finest("Finnest message: Logger with SIMPLE FORMATTER");
		}catch(IOException exception){
			LOGGER.log(Level.SEVERE, "Error occur in FileHandler.", exception);
		}
		
	}

}
