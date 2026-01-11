import sss.dpchain_of_responsibility.logger.AbstractLogger;
import sss.dpchain_of_responsibility.logger.LogLevel;
import sss.dpchain_of_responsibility.logger_chain.LoggerChain;

public class ChainPatternDemo {
	
   public static void main(String[] args) {
      AbstractLogger loggerChain = LoggerChain.getErrorDebugInfoChain();

      loggerChain.logMessage(LogLevel.INFO, 
         "This is an information.");
      System.out.println();

      loggerChain.logMessage(LogLevel.DEBUG, 
         "This is an debug level information.");
      System.out.println();

      loggerChain.logMessage(LogLevel.ERROR, 
         "This is an error information.");
      System.out.println();
   }
}
