package in.co.sdrc.scpstn.models;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class DataModel {

	 private String id;
	 private Integer position;
	 private String areaCode;
	 private String areaName;
	 private Integer areaNid;
	 private String value;
	 private String unit;
	 private String rank;
	 private String cssClass;
	 private String percentageChange;
	 private Boolean isPositiveTrend;
	 private List<ValueObject> dataSeries;
	 private List<LineSeries> lineSeries;
	 private List<Map<Object, String>> columnSeries;
	 private boolean isColumnVisible;
	 private String indicatorName;
	 private String subsectorName;
	 private String timeperiod;
	 private Integer numerator;
	 private Integer denominator;
	 
}
