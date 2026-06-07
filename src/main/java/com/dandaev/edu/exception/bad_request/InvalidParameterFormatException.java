package com.dandaev.edu.exception.bad_request;

public class InvalidParameterFormatException extends BadRequestException{

	public InvalidParameterFormatException (String message) {
		super(message);
	}

	public InvalidParameterFormatException (String message, Throwable cause) {
		super(message, cause);
	}

}
