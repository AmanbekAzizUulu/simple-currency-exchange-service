package com.dandaev.edu.exception.conflict;

import com.dandaev.edu.exception.ApplicationException;

// 409 Conflict
public class ConflictException extends ApplicationException {

	public ConflictException (String message) {
		super(message);
	}

	public ConflictException (String message, Throwable cause) {
		super(message, cause);
	}

	@ Override
	public int getHttpStatus () {
		return 409;
	}

}
