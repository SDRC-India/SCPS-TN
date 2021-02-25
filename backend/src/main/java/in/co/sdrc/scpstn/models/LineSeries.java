package in.co.sdrc.scpstn.models;

public class LineSeries {

	private String source;
	private String date;
	private Object value;
	private String rank;
	private String percentageChange;
	private Boolean isPositive;
	private String cssClass;
	private Boolean isUpward;
	public String key;

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	public Boolean getIsUpward() {
		return isUpward;
	}

	public void setIsUpward(Boolean isUpward) {
		this.isUpward = isUpward;
	}

	public String getCssClass() {
		return cssClass;
	}

	public void setCssClass(String cssClass) {
		this.cssClass = cssClass;
	}

	public LineSeries() {
		super();
	}

	public LineSeries(String source, String date, Object value) {
		super();
		this.source = source;
		this.date = date;
		this.value = value;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getPercentageChange() {
		return percentageChange;
	}

	public void setPercentageChange(String percentageChange) {
		this.percentageChange = percentageChange;
	}

	@Override
	public String toString() {
		return "LineSeries [source=" + source + ", date=" + date + ", value=" + value + ", rank=" + rank
				+ ", percentageChange=" + percentageChange + "]";
	}

	public Boolean getIsPositive() {
		return isPositive;
	}

	public void setIsPositive(Boolean isPositive) {
		this.isPositive = isPositive;
	}

}
