package org.sdrc.usermgmt.mongodb.domain;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class LoginAudit {

	@Id
	private String id;

	private String username;

	@DBRef
	private Account account;

	private Date loggedInDate;

	private Date logoutDate;

	private boolean active;

	private String userAgent;

	private String actualUserAgent;

	private String ipAddress;

}
