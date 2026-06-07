package com.dandaev.edu.exception.database;

import com.dandaev.edu.exception.internal_server_error.InternalServerErrorException;

// оборачивает SQLException из DAO-шек
public class DataAccessException extends InternalServerErrorException {

	public DataAccessException (String message) {
		super(message);
	}

	public DataAccessException (String message, Throwable cause) {
		super(message, cause);
	}

}
