package com.dandaev.edu.exception.not_found;

public class CurrencyNotFoundException extends NotFoundException {

	public CurrencyNotFoundException (String message) {
		super(message);
	}

	public CurrencyNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}


}
