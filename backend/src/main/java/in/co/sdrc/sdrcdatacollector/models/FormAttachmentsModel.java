package in.co.sdrc.sdrcdatacollector.models;

import java.util.Map;

import lombok.Data;

@Data
public class FormAttachmentsModel {

	private String columnName;

	private String filePath;

	private String originalName;

	private Long fileSize;

	private String submissionId;

	private Integer formId;

	private String fileExtension;

	private Map<String, Object> extraKeys;

	private String localDevicePath;
}
