package com.dandaev.edu.validator;

import java.math.BigDecimal;

import com.dandaev.edu.exception.bad_request.InvalidParameterFormatException;

public final class BigDecimalValidator {

	public static BigDecimal parseAndValidate (String value, String fieldName) {
		try {
			return new BigDecimal(value);
		} catch (NumberFormatException nfe) {
			throw new InvalidParameterFormatException(fieldName + " must be a valid decimal number");
		}
	}
}
