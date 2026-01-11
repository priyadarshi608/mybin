

public class InvalidStartEndException extends RuntimeException {
	/**
	 * Constructs the exception object.
	 */
	public InvalidStartEndException() {
		super();
	}
	
	/**
	 * Constructs the exception object with the provided message.
	 * @param message the exception message
	 */
	public InvalidStartEndException(String message) {
		super(message);
	}
}
