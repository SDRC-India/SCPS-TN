package in.co.sdrc.sdrcdatacollector.models;

import lombok.Data;

@Data
public class FileModel {

	private String base64;
	private String fileName;
	private Integer typeId;
	private Boolean isDeleted;
	private Long submissionId;
	private Boolean isAttached;
	private String columnName;
	private String monthRange;
	private String yearRange;
	private Long fileSize;
	private String typeName;
	private Long attachmentId;

}
