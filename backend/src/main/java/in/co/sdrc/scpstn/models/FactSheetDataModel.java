package in.co.sdrc.scpstn.models;

import java.util.Map;

public class FactSheetDataModel {

	private ValueObject districtInfo;
	private Map<String, String> sectorData;

	public ValueObject getDistrictInfo() {
		return districtInfo;
	}

	public void setDistrictInfo(ValueObject districtInfo) {
		this.districtInfo = districtInfo;
	}

	public Map<String, String> getSectorData() {
		return sectorData;
	}

	public void setSectorData(Map<String, String> sectorData) {
		this.sectorData = sectorData;
	}

}
