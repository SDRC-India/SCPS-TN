package in.co.sdrc.scpstn.models;

public class NewIndicatorModel {

	private int sectorId;

	private int subsectorId;

	private boolean existingIndicator;

	private int indicatorId;

	private String indicatorName;

	private String indicatorMetadata;

	private boolean highIsGood;

	private String numeratorName;

	private String denominatorName;

	private int unitId;

	private int subgroupId;

	private Integer roleId;

	private String publishMonth;

	private int publishYear;
	
	
	private boolean index=false;
	
	private boolean overallIndex=false;

	public int getSectorId() {
		return sectorId;
	}

	public void setSectorId(int sectorId) {
		this.sectorId = sectorId;
	}

	public int getSubsectorId() {
		return subsectorId;
	}

	public void setSubsectorId(int subsectorId) {
		this.subsectorId = subsectorId;
	}

	public boolean isExistingIndicator() {
		return existingIndicator;
	}

	public void setExistingIndicator(boolean existingIndicator) {
		this.existingIndicator = existingIndicator;
	}

	public int getIndicatorId() {
		return indicatorId;
	}

	public void setIndicatorId(int indicatorId) {
		this.indicatorId = indicatorId;
	}

	public String getIndicatorName() {
		return indicatorName;
	}

	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}

	public String getIndicatorMetadata() {
		return indicatorMetadata;
	}

	public void setIndicatorMetadata(String indicatorMetadata) {
		this.indicatorMetadata = indicatorMetadata;
	}

	public boolean isHighIsGood() {
		return highIsGood;
	}

	public void setHighIsGood(boolean highIsGood) {
		this.highIsGood = highIsGood;
	}

	public String getNumeratorName() {
		return numeratorName;
	}

	public void setNumeratorName(String numeratorName) {
		this.numeratorName = numeratorName;
	}

	public String getDenominatorName() {
		return denominatorName;
	}

	public void setDenominatorName(String denominatorName) {
		this.denominatorName = denominatorName;
	}

	public int getUnitId() {
		return unitId;
	}

	public void setUnitId(int unitId) {
		this.unitId = unitId;
	}

	public int getSubgroupId() {
		return subgroupId;
	}

	public void setSubgroupId(int subgroupId) {
		this.subgroupId = subgroupId;
	}

	

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public String getPublishMonth() {
		return publishMonth;
	}

	public void setPublishMonth(String publishMonth) {
		this.publishMonth = publishMonth;
	}

	public int getPublishYear() {
		return publishYear;
	}

	public void setPublishYear(int publishYear) {
		this.publishYear = publishYear;
	}

	public boolean isIndex() {
		return index;
	}

	public void setIndex(boolean index) {
		this.index = index;
	}

	public boolean isOverallIndex() {
		return overallIndex;
	}

	public void setOverallIndex(boolean overallIndex) {
		this.overallIndex = overallIndex;
	}

}
