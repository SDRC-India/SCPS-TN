package org.sdrc.usermgmt.mongodb.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Authority implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6855101564302901915L;

	@Id
	private String id;

	private String authority;

	private String description;


}