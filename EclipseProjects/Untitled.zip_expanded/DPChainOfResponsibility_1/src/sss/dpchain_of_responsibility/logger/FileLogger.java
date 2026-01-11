package sss.dpchain_of_responsibility.logger;

public class FileLogger extends AbstractLogger {
	
	public FileLogger(LogLevel logLevel){
		this.logLevel = logLevel;
	}

	@Override
	protected void write(String message) {		
		System.out.println("File::Logger: " + message);
	}
}
