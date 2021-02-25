package org.sdrc.usermgmt.mongodb.domain;

import java.util.Date;

import lombok.Data;

/**
 * @author subham
 *
 *This model is used to maintain audit trail while user update its information
 */
@Data
public class AccountAudit {

	private String account;

	private String auditBy;

	private Date auditDate;

}
