package in.co.sdrc.scpstn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.co.sdrc.scpstn.models.DataCollectionModel;
import in.co.sdrc.scpstn.models.IndicatorSource;
import in.co.sdrc.scpstn.models.MLineSeries;
import in.co.sdrc.scpstn.models.PerformanceChartVO;
import in.co.sdrc.scpstn.service.IPerformanceDashboardService;

@RestController
@RequestMapping("bypass/performance/api")
public class PerformanceDashboardController {

	@Autowired
	IPerformanceDashboardService iPerformanceDashboardService;
	
	@RequestMapping("/getIndicatorSources")
	public List<IndicatorSource> getSources(){
		return iPerformanceDashboardService.getSources();
	}
	
	@RequestMapping("/getIndexIndicatorValuesOnLoad")
	public List<MLineSeries> getIndexIndicatorValuesOnLoad(){
		return iPerformanceDashboardService.getMLineSeriesIndexIndicators("IND033",27);
	}
	
	@RequestMapping("/getIndexIndicatorValues")
	public List<MLineSeries> getIndexIndicatorValues(@RequestParam String areaCode,@RequestParam Integer sourceId){
		return iPerformanceDashboardService.getMLineSeriesIndexIndicators(areaCode,sourceId);
	}
	
	@RequestMapping("/getMapDataOnInitialLoad")
	public DataCollectionModel getMapDataOnInitialLoad(){
		return iPerformanceDashboardService.getThematicData(27);
	}
	
	@RequestMapping("/getMapDataBySource")
	public DataCollectionModel getThematicData(@RequestParam(defaultValue="27") Integer sourceId){
		return iPerformanceDashboardService.getThematicData(sourceId);
	}
	
	@RequestMapping("/getMLineSeriesOnInitialLoad")
	public List<MLineSeries> getMLineSeries(){
		return iPerformanceDashboardService.getMLineSeries("IND033",27);
	}
	
	@RequestMapping("/getMLineSeries")
	public List<MLineSeries> getMLineSeries(@RequestParam String areaCode,@RequestParam Integer sourceId){
		return iPerformanceDashboardService.getMLineSeries(areaCode,sourceId);
	}
	@RequestMapping("/getPerformanceChartByAreaAndSectorAndSourceOnInitialLoad")
	public PerformanceChartVO getPerformanceChartsByArea(){
		return iPerformanceDashboardService.getPerformanceChartByAreaAndSectorAndSource("IND033",1,27);
	}
	@RequestMapping("/getPerformanceChartByAreaAndSectorAndSource")
	public PerformanceChartVO getPerformanceChartsByArea(@RequestParam String areaCode,@RequestParam Integer sectorId,@RequestParam Integer sourceId){
		return iPerformanceDashboardService.getPerformanceChartByAreaAndSectorAndSource(areaCode,sectorId,sourceId);
	}
	
}
