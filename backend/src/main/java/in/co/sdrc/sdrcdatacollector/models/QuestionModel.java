package in.co.sdrc.sdrcdatacollector.models;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionModel {

	private Integer key;
	private String label;
	private String type;
	private String columnName;
	private Integer roleId;
	private List<OptionModel> options;
	private Integer typeId;
	private String controlType;
	private Object value;
	private String parentColumnName;
	private Integer formId;
	private Integer questionOrder;

	private List<Map<String, Object>> tableModel;

	private List<List<QuestionModel>> beginRepeat;

	private Boolean finalizeMandatory;

	private Boolean saveMandatory;
	
	private String relevance;
	
	private String constraints;
	
	private String features;
	
	private String defaultSettings;
	
	private Boolean displayScore=false;
	
	private String tableName;
	
	private String reviewHeader;
	
	private String fileExtensions;

	private String scoreExp;
	
	private String cmsg;
	
	private JsonNode languages;
}
