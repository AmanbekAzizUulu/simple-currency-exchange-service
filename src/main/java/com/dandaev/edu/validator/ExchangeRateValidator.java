package com.dandaev.edu.validator;

import java.math.BigDecimal;

import com.dandaev.edu.exception.bad_request.InvalidParameterFormatException;

public class ExchangeRateValidator {

	public static void validateRate (BigDecimal rate) {
		if (rate.compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidParameterFormatException("Rate must be greater than zero");
		}
	}
}
