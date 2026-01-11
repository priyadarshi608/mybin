package sss.dpchain_of_responsibility.logger;

public abstract class AbstractLogger {
	
	LogLevel logLevel;
	
	//next element in chain or responsibility
	protected AbstractLogger nextLogger;

	public void setNextLogger(AbstractLogger nextLogger){
		this.nextLogger = nextLogger;
	}

	public void logMessage(LogLevel logLevel, String message){
		if(this.logLevel.ordinal() <= logLevel.ordinal()) {
			write(message);
		}
		if(nextLogger !=null) {
			nextLogger.logMessage(logLevel, message);
		}
	}

	abstract protected void write(String message);

}
