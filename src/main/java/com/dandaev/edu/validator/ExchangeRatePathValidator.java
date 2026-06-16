package com.dandaev.edu.validator;

import com.dandaev.edu.exception.bad_request.InvalidParameterFormatException;

public final class ExchangeRatePathValidator {

	public static String[] extractAndValidate (String pathInfo) {
		if (pathInfo == null || pathInfo.length() != 7) {
			throw new InvalidParameterFormatException("Invalid currency pair in URL");
		}

		String baseCode = pathInfo.substring(1, 4).toUpperCase();
		String targetCode = pathInfo.substring(4, 7).toUpperCase();

		CurrencyCodeValidator.validate(baseCode);
		CurrencyCodeValidator.validate(targetCode);

		return new String[]{ baseCode, targetCode};
	}
}
