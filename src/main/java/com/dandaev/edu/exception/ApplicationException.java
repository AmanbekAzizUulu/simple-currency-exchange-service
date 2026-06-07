package com.dandaev.edu.exception;

public  abstract class ApplicationException extends RuntimeException{

	public ApplicationException (String message) {
		super(message);
	}

	public ApplicationException (String message, Throwable cause) {
		super(message, cause);
	}

	public abstract int getHttpStatus ();
}
