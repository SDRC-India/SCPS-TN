package org.sdrc.usermgmt.exception;

public class OtpRetryLimitExceededException extends RuntimeException {

	private static final long serialVersionUID = 6113560192117626028L;

	public OtpRetryLimitExceededException() {
		super();
	}

	public OtpRetryLimitExceededException(String arg0) {
		super(arg0);
	}

	public OtpRetryLimitExceededException(Throwable arg0) {
		super(arg0);
	}

}