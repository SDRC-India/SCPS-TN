package in.co.sdrc.scpstn.models;

import java.util.Date;

public class TimePeriodModel {

	private int timePeriod_NId;
	private Date endDate;
	private String periodicity;
	private Date startDate;
	private String timePeriod;

	public int getTimePeriod_NId() {
		return timePeriod_NId;
	}

	public void setTimePeriod_NId(int timePeriod_NId) {
		this.timePeriod_NId = timePeriod_NId;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getPeriodicity() {
		return periodicity;
	}

	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}



}
