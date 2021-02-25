package org.sdrc.usermgmt.mongodb.domain;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class DesignationAuthorityMapping implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6622269530474950318L;

	@Id
	private String id;

	@DBRef
	private Designation designation;

	@DBRef
	private Authority authority;

}