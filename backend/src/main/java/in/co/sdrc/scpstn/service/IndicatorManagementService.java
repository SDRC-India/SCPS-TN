package in.co.sdrc.scpstn.service;

import java.util.Map;

import org.json.simple.JSONArray;

import in.co.sdrc.scpstn.models.NewIndicatorModel;

public interface IndicatorManagementService {
	
	
	public Map<String, JSONArray> initializeJson();
	
	
	public boolean saveNewIndicator(NewIndicatorModel newIndicatorModel);

}
