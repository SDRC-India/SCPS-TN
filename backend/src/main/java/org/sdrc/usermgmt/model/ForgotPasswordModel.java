package org.sdrc.usermgmt.model;

import lombok.Data;
import lombok.ToString;

/**
 * @author subham
 */
@Data
@ToString
public class ForgotPasswordModel {

	private String emailId;
	
	private String otp;
	
	private String newPassword;
	
	private String confirmPassword;
	
	private String valueType;
}
