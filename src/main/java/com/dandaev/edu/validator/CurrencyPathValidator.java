package com.dandaev.edu.validator;

import com.dandaev.edu.exception.bad_request.MissingParameterException;

public final class CurrencyPathValidator {
	public static String extractAndValidate (String pathInfo) {
		if (pathInfo == null || pathInfo.length() <= 1) {
			throw new MissingParameterException("Currency code is missing in URL");
		}
		String code = pathInfo.substring(1).toUpperCase();

		CurrencyCodeValidator.validate(code);

		return code;
	}
}
