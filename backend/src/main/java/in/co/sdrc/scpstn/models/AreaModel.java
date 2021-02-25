package in.co.sdrc.scpstn.models;

public class AreaModel {
	private Integer areaId;
	private String areaName;
	private Integer areaLevelId;
	private Integer parentAreaId;
	private String areaCode;

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public Integer getAreaId() {
		return areaId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Integer getAreaLevelId() {
		return areaLevelId;
	}

	public void setAreaLevelId(Integer areaLevelId) {
		this.areaLevelId = areaLevelId;
	}

	public Integer getParentAreaId() {
		return parentAreaId;
	}

	public void setParentAreaId(Integer parentAreaId) {
		this.parentAreaId = parentAreaId;
	}

}
