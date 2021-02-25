package in.co.sdrc.sdrcdatacollector.models;

import java.util.List;

import lombok.Data;

@Data
public class RawDataModel {
	
	List<DataModel> dataModels;
	
	private String sheetName;
	
	private String fileName;
	
	private String formName;


}
