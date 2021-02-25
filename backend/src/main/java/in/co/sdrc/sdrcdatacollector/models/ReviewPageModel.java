package in.co.sdrc.sdrcdatacollector.models;

import java.util.List;
import java.util.Map;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import lombok.Data;

@Data
public class ReviewPageModel {
	
	private List<EnginesForm> forms;
	private Map<Integer, List<DataObject>> reviewDataMap;

}
