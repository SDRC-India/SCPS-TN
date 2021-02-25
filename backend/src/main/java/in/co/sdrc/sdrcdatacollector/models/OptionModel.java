package in.co.sdrc.sdrcdatacollector.models;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionModel {

	private Integer key;

	private String value;

	private Integer order;

	private Boolean isSelected;

	private Integer parentId;
	
	private List<Integer> parentIds;

	private Integer level;

	private boolean visible = true;

	private String score;
	
	private Map<String, String> autoPopulateValue;

}
