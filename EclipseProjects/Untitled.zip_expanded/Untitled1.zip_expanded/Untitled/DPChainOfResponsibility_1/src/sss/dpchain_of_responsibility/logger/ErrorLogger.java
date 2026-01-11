package sss.dpchain_of_responsibility.logger;

public class ErrorLogger extends AbstractLogger {
	
	public ErrorLogger(LogLevel logLevel){
		this.logLevel = logLevel;
	}

	@Override
	protected void write(String message) {		
		System.out.println("Error Console::Logger: " + message);
	}
}
