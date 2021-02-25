package org.sdrc.usermgmt.core.util;

import lombok.Data;

/**
 * @author azar
 *
 * This class is added to provide oauth2 clientid and secret dynamically.
 *
 * User is resposnible to make its bean and pass clientid and secret in the application.
 *
 */
@Data
public class UgmtClientCredentials {

	private String clientId;

	private String clientPassword;

	public UgmtClientCredentials(String clientId, String clientPassword) {
		this.clientId = clientId;
		this.clientPassword = clientPassword;
	}

	public UgmtClientCredentials() {
		super();
	}

}
