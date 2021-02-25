package org.sdrc.usermgmt.exception;

public class DuplicateEmailIdException extends RuntimeException {

	private static final long serialVersionUID = 6113560192117626028L;

	public DuplicateEmailIdException() {
		super();
	}

	public DuplicateEmailIdException(String arg0) {
		super(arg0);
	}

	public DuplicateEmailIdException(Throwable arg0) {
		super(arg0);
	}

}

