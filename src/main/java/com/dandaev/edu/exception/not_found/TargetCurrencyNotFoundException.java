package com.dandaev.edu.exception.not_found;

public class TargetCurrencyNotFoundException extends CurrencyNotFoundException {

	public TargetCurrencyNotFoundException (String code) {
		super("Target currency not found with code: " + code);
	}

	public TargetCurrencyNotFoundException (Long id) {
		super("Target currency not found with id: " + id);
	}
}
