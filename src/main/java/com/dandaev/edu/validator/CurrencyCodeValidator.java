package com.dandaev.edu.validator;

import com.dandaev.edu.exception.bad_request.InvalidCurrencyCodeException;

public final class CurrencyCodeValidator {

	public static void validate (String code) {
		if (!code.matches("[A-Z]{3}")) {
			throw new InvalidCurrencyCodeException(code == null ? "null" : code);
		}
	}
}
