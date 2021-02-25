package org.sdrc.usermgmt.exception;

/**
 * @author subham
 *
 */
public class InvalidPropertyException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 866017796207625245L;

	
	public InvalidPropertyException() {
		super();
	}

	public InvalidPropertyException(String arg0) {
		super(arg0);
	}

	public InvalidPropertyException(Throwable arg0) {
		super(arg0);
	}
}
