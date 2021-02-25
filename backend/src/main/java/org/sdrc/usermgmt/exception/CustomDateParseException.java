package org.sdrc.usermgmt.exception;

import java.text.ParseException;

public class CustomDateParseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

	public CustomDateParseException(String msg, ParseException e) {
		super(msg, e);
	}

	public CustomDateParseException(ParseException e) {
		super(e);
	}

}
