package org.sdrc.usermgmt.mongodb.domain;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
public class Designation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8837167379563707986L;

	@Id
	private String id;

	private String name;

	private String code;

	private Integer slugId;

}