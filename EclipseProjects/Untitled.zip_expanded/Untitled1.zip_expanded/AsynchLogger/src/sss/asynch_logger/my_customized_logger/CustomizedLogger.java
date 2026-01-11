package sss.asynch_logger.my_customized_logger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CustomizedLogger {
	private final Logger LOGGER;
	private Handler fileHandler;
	private Formatter simpleFormatter;

	private CustomizedLogger(String className) {
		LOGGER = Logger.getLogger(className);
		doOtherCustomization();
	}

	private void doOtherCustomization() {
		try {
			fileHandler  = new FileHandler("./Error.log");
			simpleFormatter = new SimpleFormatter();
			fileHandler.setFormatter(simpleFormatter);
			LOGGER.addHandler(fileHandler);
			fileHandler.setLevel(Level.ALL);
			LOGGER.setLevel(Level.ALL);

			// To disable the console handler.
			LOGGER.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}

	public static Logger getCustomizedLogger(String className) {
		CustomizedLogger customizedLogger = new CustomizedLogger(className);
		return customizedLogger.LOGGER;
	}
}
