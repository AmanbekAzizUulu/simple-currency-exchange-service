package com.dandaev.edu.exception.bad_request;

public class InvalidCurrencySignException extends BadRequestException {

	public InvalidCurrencySignException (String message) {
		super(message);
	}

	public InvalidCurrencySignException (String message, Throwable cause) {
		super(message, cause);
	}

}
