package com.dandaev.edu.exception.conflict;

public class CurrencyAlreadyExistsException extends ConflictException {

	public CurrencyAlreadyExistsException (String code) {
		super("Currency with code '" + code + "' already exists");
	}

	public CurrencyAlreadyExistsException (String message, Throwable cause) {
		super(message, cause);
	}

}
