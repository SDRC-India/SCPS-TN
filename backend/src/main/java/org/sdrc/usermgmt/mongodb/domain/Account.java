package org.sdrc.usermgmt.mongodb.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.sdrc.usermgmt.model.AuthorityControlType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Document
@Data
public class Account implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2959445609290107998L;

	@Id
	private String id;

	private String userName;

	@JsonIgnore
	private String password;

	private boolean enabled = true;

	private boolean credentialexpired = false;

	private boolean expired = false;

	private boolean locked = false;

	private String email;

	/**
	 * for forgot password
	 */
	@JsonIgnore
	private String otp;

	@JsonIgnore
	private short invalidAttempts = 0;

	@JsonIgnore
	private Date otpGeneratedDateTime;

	@JsonIgnore
	private List<AssignedDesignations> assignedDesignations;

	@JsonIgnore
	private List<String> authorityIds;

	@JsonIgnore
	private List<Integer> mappedAreaIds;

	private Object userDetails;

	private List<AccountAudit> changeHistory;

	private AuthorityControlType authorityControlType;
	
	// constructor
	public Account() {
		super();
	}

	public Account(String id) {
		super();
		this.id = id;
	}
}