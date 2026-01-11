import sss.dpchain_of_responsibility.logger.AbstractLogger;
import sss.dpchain_of_responsibility.logger.LogLevel;
import sss.dpchain_of_responsibility.logger_chain.LoggerChain;

public class ChainPatternDemo {
	
   public static void main(String[] args) {
      AbstractLogger loggerChain = LoggerChain.getErrorDebugInfoChain();

      loggerChain.logMessage(LogLevel.INFO, 
         "This is an information.");

      loggerChain.logMessage(LogLevel.DEBUG, 
         "This is an debug level information.");

      loggerChain.logMessage(LogLevel.ERROR, 
         "This is an error information.");
   }
}
