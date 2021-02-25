package org.sdrc.usermgmt.exception;

/**
 * @author Subham Ashish(subham@sdrc.co.in) Created Date:10-Jul-2018 6:59:06 PM
 */
public class PasswordMismatchException extends RuntimeException {

	private static final long serialVersionUID = 6113560192117626028L;

	public PasswordMismatchException() {
		super();
	}

	public PasswordMismatchException(String arg0) {
		super(arg0);
	}

	public PasswordMismatchException(Throwable arg0) {
		super(arg0);
	}

}