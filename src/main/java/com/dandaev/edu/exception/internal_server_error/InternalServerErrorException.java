package com.dandaev.edu.exception.internal_server_error;

import com.dandaev.edu.exception.ApplicationException;

// 500 Internal Server Error
public class InternalServerErrorException extends ApplicationException {

	public InternalServerErrorException (String message) {
		super(message);
	}

	public InternalServerErrorException (String message, Throwable cause) {
		super(message, cause);
	}

	@ Override
	public int getHttpStatus () {
		return 500;
	}

}
