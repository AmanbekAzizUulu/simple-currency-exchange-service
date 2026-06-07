package com.dandaev.edu.exception.not_found;

import com.dandaev.edu.exception.ApplicationException;

// 404 Not Found
public class NotFoundException extends ApplicationException {

	public NotFoundException (String message) {
		super(message);
	}

	public NotFoundException (String message, Throwable cause) {
		super(message, cause);
	}

	@ Override
	public int getHttpStatus () {
		return 404;
	}

}
