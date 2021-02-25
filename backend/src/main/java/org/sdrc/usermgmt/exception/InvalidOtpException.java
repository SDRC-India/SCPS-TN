package org.sdrc.usermgmt.exception;

public class InvalidOtpException extends RuntimeException {

	private static final long serialVersionUID = 6113560192117626028L;

	public InvalidOtpException() {
		super();
	}

	public InvalidOtpException(String arg0) {
		super(arg0);
	}

	public InvalidOtpException(Throwable arg0) {
		super(arg0);
	}

}