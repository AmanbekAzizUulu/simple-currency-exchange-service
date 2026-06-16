package com.dandaev.edu.validator;

import java.util.Map;

import com.dandaev.edu.exception.bad_request.MissingParameterException;

public final class RequestParamValidator {

	public static void requiredParams (Map<String, String> params, String... requiredKeys) {
		for (String key : requiredKeys) {
			String value = params.get(key);
			if (value == null || value.isBlank()) {
				throw new MissingParameterException("Missing required field: " + key);
			}
		}
	}

	public static void requireNotEmpty (String fieldName, String value) {
		if (value == null || value.isBlank()) {
			throw new MissingParameterException("Missing required field: " + fieldName);
		}
	}

}
