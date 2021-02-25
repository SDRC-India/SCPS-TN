package org.sdrc.usermgmt.exception;

public class PasswordResetTimeExpiredException extends RuntimeException {

	private static final long serialVersionUID = 6113560192117626028L;

	public PasswordResetTimeExpiredException() {
		super();
	}

	public PasswordResetTimeExpiredException(String arg0) {
		super(arg0);
	}

	public PasswordResetTimeExpiredException(Throwable arg0) {
		super(arg0);
	}

}