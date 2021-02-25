package in.co.sdrc.sdrcdatacollector.models;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Data;

@Data
public class ReceiveEventModel {

	private Integer formId;

	private String createdDate;

	private String updatedDate;

	private String uniqueId;
	
	private JsonNode submissionData;
	
	private Integer attachmentCount;
	
	private String formStatus;
	
}
