package com.dandaev.edu.exception.bad_request;

public class InvalidCurrencyFullNameException extends BadRequestException {

	public InvalidCurrencyFullNameException (String message) {
		super(message);
	}

	public InvalidCurrencyFullNameException (String message, Throwable cause) {
		super(message, cause);
	}

}
