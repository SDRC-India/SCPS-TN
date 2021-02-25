package org.sdrc.usermgmt.mongodb.domain;

import lombok.Data;

@Data
public class AssignedDesignations {

	private String designationIds;

	private Boolean enable = true;
}
