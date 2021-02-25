package in.co.sdrc.scpstn.models;

import lombok.Data;

@Data
public class BarChart {
	private String axis;
	private String value;
	private String timePeriod;
	private String areaCode;
	private String areaName;
	private Integer areaNid;
	private String subsectorName;
	private String cssColor;
}
