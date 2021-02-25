package in.co.sdrc.scpstn.models;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class PerformanceChartVO {
	List<List<BarChart>> subsectorData;
	Map<String, List<DataModel>> indicatorData;
}
