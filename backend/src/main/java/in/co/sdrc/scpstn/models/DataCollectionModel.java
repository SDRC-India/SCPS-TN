package in.co.sdrc.scpstn.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DataCollectionModel {

	public List<DataModel> dataCollection;
	private List<ValueObject> legends;
	private List<String> topPerformers ;	
	private List<String> bottomPerformers ;
	private String timePeriod;
	private List<Map<String,Object>> tableModel = new ArrayList<>();
	
}
