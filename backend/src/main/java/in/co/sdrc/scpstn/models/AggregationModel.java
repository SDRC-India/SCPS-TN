package in.co.sdrc.scpstn.models;

import in.co.sdrc.scpstn.domain.IndicatorClassification;
import lombok.Data;

@Data
public class AggregationModel {

	private Integer areaId;
	private Integer indicatorId;
	private Integer unitId;
	private Integer subgroupId;
	private Integer numerator;
	private Integer denominator;
	private IndicatorClassification sector;
	private IndicatorClassification subsector;

	
}
