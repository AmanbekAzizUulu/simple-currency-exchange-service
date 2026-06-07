package com.dandaev.edu.exception.not_found;

public class ExchangeRateNotFoundException extends NotFoundException{

	public ExchangeRateNotFoundException (String base, String target) {
		super("Exchange Rate Not Found: from " + base + " to " + target);
	}

	public ExchangeRateNotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

}
