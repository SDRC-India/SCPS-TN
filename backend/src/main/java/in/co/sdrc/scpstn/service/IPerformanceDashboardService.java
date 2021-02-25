package in.co.sdrc.scpstn.service;

import java.util.List;

import in.co.sdrc.scpstn.models.DataCollectionModel;
import in.co.sdrc.scpstn.models.IndicatorSource;
import in.co.sdrc.scpstn.models.MLineSeries;
import in.co.sdrc.scpstn.models.PerformanceChartVO;

public interface IPerformanceDashboardService {

	List<IndicatorSource> getSources();

	DataCollectionModel getThematicData(Integer sourceId);
	
	List<MLineSeries> getMLineSeriesIndexIndicators(String areaCode, Integer sourceId);

	List<MLineSeries> getMLineSeries(String areaCode, Integer sourceId);

	PerformanceChartVO getPerformanceChartByAreaAndSectorAndSource(String areaCode, Integer sectorId, Integer sourceId);

}
