package org.sdrc.usermgmt.model;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author subham
 *
 */

@Data
public class Mail {

	private String fromUserName;

	private String toUserName;

	private List<String> toEmailIds;

	private List<String> ccEmailIds;

	private String subject;

	private String message;

	private Map<String, String> attachments;

}
