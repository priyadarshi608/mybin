package sss.dll.myexceptions;

public class InvalidElementException extends RuntimeException {
	/**
	 * Constructs the exception object.
	 */
	public InvalidElementException() {
		super();
	}
	
	/**
	 * Constructs the exception object with the provided message.
	 * @param message the exception message
	 */
	public InvalidElementException(String message) {
		super(message);
	}
}
