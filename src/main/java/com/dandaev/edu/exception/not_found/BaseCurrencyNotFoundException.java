package com.dandaev.edu.exception.not_found;

public class BaseCurrencyNotFoundException extends CurrencyNotFoundException {

	public BaseCurrencyNotFoundException (String code) {
		super("Base currency not found with code: " + code);
	}

	public BaseCurrencyNotFoundException (Long id) {
		super("Base currency not found with id: " + id);
	}
}
