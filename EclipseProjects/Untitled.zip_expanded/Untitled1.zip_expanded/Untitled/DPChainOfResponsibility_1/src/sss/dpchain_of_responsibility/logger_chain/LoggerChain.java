package sss.dpchain_of_responsibility.logger_chain;

import sss.dpchain_of_responsibility.logger.AbstractLogger;
import sss.dpchain_of_responsibility.logger.ConsoleLogger;
import sss.dpchain_of_responsibility.logger.ErrorLogger;
import sss.dpchain_of_responsibility.logger.FileLogger;
import sss.dpchain_of_responsibility.logger.LogLevel;

public class LoggerChain {

	public static AbstractLogger getErrorDebugInfoChain() {

		AbstractLogger errorLogger = new ErrorLogger(LogLevel.ERROR);
		AbstractLogger fileLogger = new FileLogger(LogLevel.DEBUG);
		AbstractLogger consoleLogger = new ConsoleLogger(LogLevel.INFO);

		errorLogger.setNextLogger(fileLogger);
		fileLogger.setNextLogger(consoleLogger);

		return errorLogger;	
	}
}
