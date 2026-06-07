package com.dandaev.edu.exception.bad_request;

import com.dandaev.edu.exception.ApplicationException;

// 400 Bad Request
public class BadRequestException extends ApplicationException {
	public BadRequestException (String message) {
		super(message);
	}

	public BadRequestException (String message, Throwable cause) {
		super(message, cause);
	}

	@ Override
	public int getHttpStatus () {
		return 400;
	}
}
