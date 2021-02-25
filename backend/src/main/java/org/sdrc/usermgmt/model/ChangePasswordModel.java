package org.sdrc.usermgmt.model;

import lombok.Data;

/**
 * @author subham
 */
@Data
public class ChangePasswordModel {

	private String userName;

	private String oldPassword;

	private String newPassword;

	private String confirmPassword;

}