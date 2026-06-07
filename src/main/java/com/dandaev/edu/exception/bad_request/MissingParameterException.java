package com.dandaev.edu.exception.bad_request;

public class MissingParameterException extends BadRequestException {

	public MissingParameterException (String message) {
		super(message);
	}

	public MissingParameterException (String message, Throwable cause) {
		super(message, cause);
	}
}
