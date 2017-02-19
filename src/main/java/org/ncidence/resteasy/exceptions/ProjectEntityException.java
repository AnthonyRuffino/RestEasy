package org.ncidence.resteasy.exceptions;

import org.springframework.http.HttpStatus;

public class ProjectEntityException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 201702182300L;
	
	private HttpStatus statusCode;

	public ProjectEntityException(String message, HttpStatus statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public HttpStatus getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(HttpStatus statusCode) {
		this.statusCode = statusCode;
	}
	
	
}
