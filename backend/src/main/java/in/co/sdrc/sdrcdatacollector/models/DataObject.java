package in.co.sdrc.sdrcdatacollector.models;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DataObject {

	private Integer formId;
	
	private Timestamp time;
	
	private Map<String,Object> extraKeys;
	
	private Map<String, List<Map<String, List<QuestionModel>>>> formData;


	private Map<String,String> formDataHead = new HashMap<>();


	private String username;

	private Boolean rejected;

	private String createdDate;

	private String updatedDate;

	private String uniqueId;

	private String uniqueName;
}
