package sss.dpchain_of_responsibility.logger;

public class ConsoleLogger extends AbstractLogger {

	public ConsoleLogger(LogLevel logLevel){
		this.logLevel = logLevel;
	}

	@Override
	protected void write(String message) {		
		System.out.println("Standard Console::Logger: " + message);
	}
}
