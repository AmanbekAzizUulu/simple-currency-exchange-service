package com.dandaev.edu.exception.conflict;

public class ExchangeRateAlreadyExistsException extends ConflictException {

	public ExchangeRateAlreadyExistsException (String code) {
		super("Exchange Rate Already Exists: " + code);
	}

	public ExchangeRateAlreadyExistsException (String message, Throwable cause) {
		super(message, cause);
	}

}
