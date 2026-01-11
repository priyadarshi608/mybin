package sss.dll.myexceptions;

public class InvalidIndexException extends RuntimeException{
	/**
	 * Constructs the exception object.
	 */
	public InvalidIndexException() {
		super();
	}
	
	/**
	 * Constructs the exception object with the given message.
	 * @param message the message related to the exception.
	 */
	public InvalidIndexException(String message) {
		super(message);
	}
}
