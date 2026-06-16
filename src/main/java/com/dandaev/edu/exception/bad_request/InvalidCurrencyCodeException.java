package com.dandaev.edu.exception.bad_request;

public class InvalidCurrencyCodeException extends BadRequestException {

	public InvalidCurrencyCodeException (String code) {
		super("Invalid currency code: " + code);
	}
}
