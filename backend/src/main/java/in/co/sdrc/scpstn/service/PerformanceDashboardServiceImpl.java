package in.co.sdrc.scpstn.service;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.sdrc.scpstn.domain.Data;
import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.domain.Timeperiod;
import in.co.sdrc.scpstn.models.AppType;
import in.co.sdrc.scpstn.models.BarChart;
import in.co.sdrc.scpstn.models.Constants;
import in.co.sdrc.scpstn.models.DataCollectionModel;
import in.co.sdrc.scpstn.models.DataModel;
import in.co.sdrc.scpstn.models.IndicatorClassfier;
import in.co.sdrc.scpstn.models.IndicatorClassificationType;
import in.co.sdrc.scpstn.models.IndicatorSource;
import in.co.sdrc.scpstn.models.IndicatorType;
import in.co.sdrc.scpstn.models.MLineSeries;
import in.co.sdrc.scpstn.models.PerformanceChartVO;
import in.co.sdrc.scpstn.models.Type;
import in.co.sdrc.scpstn.models.ValueObject;
import in.co.sdrc.scpstn.repository.DataRepository;
import in.co.sdrc.scpstn.repository.IndicatorClassificationRepository;
import in.co.sdrc.scpstn.repository.IndicatorRepository;
import in.co.sdrc.scpstn.repository.TimePeriodRepository;

@Service
@Transactional
public class PerformanceDashboardServiceImpl implements IPerformanceDashboardService {

	@Autowired
	private TimePeriodRepository timePeriodRepository;
	@Autowired
	private IndicatorRepository indicatorRepository;
	@Autowired
	private DataRepository dataRepository;
	@Autowired
	private IndicatorClassificationRepository indicatorClassificationRepository;

	private static DecimalFormat df = new DecimalFormat(".#");

	private static DecimalFormat df2 = new DecimalFormat("0.00");

	@Override
	public List<IndicatorSource> getSources() {
		List<String> sources = new ArrayList<>();

		UserModel model = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (((Integer) model.getDesignationIds().toArray()[0]) == 22) {
			sources.add("PO");
		} else {
			sources.add("COMPUTED_INDEX");
			sources.add("DCPU");
			sources.add("JJB");
			sources.add("CWC");
			sources.add("SJPU");
			sources.add("PO");
		}

		List<IndicatorClassification> sourcesList = indicatorClassificationRepository
				.findByNameInAndIndicatorClassificationType(sources, IndicatorClassificationType.SR);
		List<IndicatorSource> sModels = new ArrayList<>();

		IndicatorSource COMPUTED_INDEX = null;
		for (int i = 0; i < sourcesList.size(); i++) {
			IndicatorClassification source = sourcesList.get(i);
			IndicatorSource is = new IndicatorSource();
			is.setDbName(source.getName());
			is.setSourceId(source.getIndicatorClassificationId());
			is.setSourceName(source.getName());
			if (source.getName().equals("COMPUTED_INDEX")) {
				COMPUTED_INDEX = new IndicatorSource();
				COMPUTED_INDEX.setDbName(source.getName());
				COMPUTED_INDEX.setSourceName("OVERALL PERFORMANCE");
				COMPUTED_INDEX.setSourceId(source.getIndicatorClassificationId());

			} else {
				sModels.add(is);
			}
		}
		if (COMPUTED_INDEX != null)
			sModels.add(COMPUTED_INDEX);

		return sModels;
	}

	/**
	 * @param sourceId
	 *            The indicator classification id of the source. Return
	 *            {@code}List<DataCollectionModel{@code} i.e Fetch the latest data
	 *            from the data table of the source and return in list.
	 * 
	 */

	@Override
	public DataCollectionModel getThematicData(Integer sourceId) {
		Timeperiod latestTp = timePeriodRepository.findTop1ByAppTypeOrderByEndDateDesc(AppType.APP_V_2);
		IndicatorClassification source = new IndicatorClassification();
		source.setIndicatorClassificationId(sourceId);
		Indicator indicator = indicatorRepository.findByIndicatorType(IndicatorType.OVERALL);
		DataCollectionModel model = new DataCollectionModel();

		List<Object[]> data = new ArrayList<>();
		if (latestTp != null) {
			LocalDateTime ldt = LocalDateTime.ofInstant(latestTp.getStartDate().toInstant(),
					ZoneId.of("Asia/Calcutta"));
			model.setTimePeriod(ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ldt.getYear());

			if (indicator.isHighIsGood()) {
				data = dataRepository.findDataByTimePeriodForIndicatorWithRankWithHighGood(indicator.getIndicatorId(),
						latestTp.getTimeperiodId(), source.getIndicatorClassificationId());
			} else {
				data = dataRepository.findDataByTimePeriodForIndicatorWithRankWithHighBad(indicator.getIndicatorId(),
						latestTp.getTimeperiodId(), source.getIndicatorClassificationId());
			}
		}

		// Calculate TOP 3 and Buttom 3 performers and Rank
		// List<String> topPerformers = new ArrayList<String>();
		// List<String> bottomPerformers = new ArrayList<String>();
		List<DataModel> dataModels = new ArrayList<>();
		for (int index = 0; index < data.size(); index++) {
			DataModel utDataModel = new DataModel();
			Object[] performer = data.get(index);
			double percentage = Double.parseDouble(performer[0].toString());
			// calculating Rank

			utDataModel.setAreaCode(performer[1].toString());
			utDataModel.setAreaName(performer[2].toString());
			utDataModel.setAreaNid(Integer.parseInt(performer[3].toString()));
			utDataModel.setUnit("percent");
			utDataModel.setRank(performer[5].toString());

			utDataModel.setValue(df2.format(percentage));

			if (indicator.isHighIsGood())
				utDataModel.setCssClass(percentage <= 0.60d ? "firstslices"
						: percentage >= 0.61d && percentage <= 0.80d ? "secondslices"
								: percentage >= 0.81d ? "fifthslices" : "sixthslices");

			else
				utDataModel.setCssClass(percentage <= 0.60d ? "fifthslices"
						: percentage >= 0.61d && percentage <= 0.80d ? "secondslices"
								: percentage >= 0.81d ? "firstslices" : "sixthslices");

			dataModels.add(utDataModel);
		}
		// start creating legends
		List<ValueObject> list = new ArrayList<ValueObject>();
		// list.add(new ValueObject("0.00 - 0.20", Slices.FIRST_SLICE));
		// list.add(new ValueObject("0.21 - 0.40", Slices.SECOND_SLICE));
		// list.add(new ValueObject("0.41 - 0.60 ", Slices.THIRD_SLICE));
		// list.add(new ValueObject("0.61 - 0.80 ", Slices.FOUTRH_SLICE));
		// list.add(new ValueObject("0.81 - 1.00 ", Slices.FIFTHSLICES));
		// list.add(new ValueObject("Not Available", Slices.SIXTH_SLICES));

		list.add(new ValueObject("0.00 - 0.60", Constants.Slices.FIRST_SLICE));
		list.add(new ValueObject("0.61 - 0.80 ", Constants.Slices.SECOND_SLICE));
		list.add(new ValueObject("0.81 - 1.00 ", Constants.Slices.FIFTHSLICES));
		list.add(new ValueObject("Not Available", Constants.Slices.SIXTH_SLICES));

		model.setDataCollection(dataModels);
		model.setLegends(list);
		return model;

	}

	@Override
	public List<MLineSeries> getMLineSeries(String areaCode, Integer sourceId) {

		IndicatorClassification indexSector = indicatorClassificationRepository.findByType(Type.INDEX);
		List<IndicatorClassification> children = indexSector.getChildren();

		List<Indicator> indicators = indicatorRepository.findByIndicatorClassificationInAndIndicatorTypeIn(children,
				IndicatorType.INDEX_INDICATOR, IndicatorType.OVERALL);
		// GETTING DATA FOR LAST 12 TIMEPERIODS

		Date tpAfterMarch2019 = null;
		try {
			tpAfterMarch2019 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-31 23:59:59");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Timeperiod> latestTps = timePeriodRepository
				.findTop12ByStartDateGreaterThanOrderByEndDateDesc(tpAfterMarch2019);
		IndicatorClassification source = new IndicatorClassification();
		source.setIndicatorClassificationId(sourceId);

		List<Data> data = dataRepository
				.findByIndicatorInAndSourceAndTimePeriodInAndAreaAreaCodeOrderByTimePeriodEndDateAsc(indicators, source,
						latestTps, areaCode);
		List<MLineSeries> mLines = new ArrayList<>();
		for (Data performer : data) {
			MLineSeries line = new MLineSeries();
			line.setAreaName(performer.getArea().getAreaName());
			line.setAreaNid(performer.getArea().getAreaId());
			line.setKey(performer.getIndicator().getIndicatorName());
			line.setCssClass(performer.getIndicatorUnitSubgroup().getIndicator().getIndicatorClassification()
					.getLineChartColor());
			LocalDateTime ldt = LocalDateTime.ofInstant(performer.getTimePeriod().getStartDate().toInstant(),
					ZoneId.of("Asia/Calcutta"));
			line.setTimeperiod(ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ldt.getYear());
			line.setValue(df2.format(performer.getPercentage()));
			line.setSubsectorKey(performer.getIndicatorUnitSubgroup().getIndicator().getIndicatorClassification()
					.getIndicatorClassificationId() + "");
			mLines.add(line);
		}
		return mLines;
	}

	@Override
	public PerformanceChartVO getPerformanceChartByAreaAndSectorAndSource(String areaCode, Integer sectorId,
			Integer sourceId) {

		IndicatorClassification sourceReported = indicatorClassificationRepository.findByIndicatorClassificationId(sourceId);

		IndicatorClassification topSector = indicatorClassificationRepository.findByIndicatorClassificationId(sectorId);
		
		List<IndicatorClassification> topSubsectors = topSector.getChildren();
		
		List<IndicatorClassification>  dataInSubsectors = indicatorClassificationRepository.findByPerformanceIndicatorsAndReportingSource(sectorId, sourceReported.getName());
		

		// FETCH THE SUBSECTORS, THAT HAS PERFORMANCE INDICATORS AND REPORTED BY
		// DISTRICT LEVEL SOURCES. ('DCPU','CWC','PO','SJPU','JJB')
		// NOTE : SAA HAS PERFORMANCE INDICATORS BUT HAS NOT BEEN ADDED FOR INDEX
		// CALCULATION OF PERFORMANCE.
		// IF SAA IS CONSIDERED FOR INDEX CALCULATION. ADD SAA IN QUERY FOR RETRIEVING
		// SUBSECTOR THAT HAS PERFORMANCE RELATED INDICATORS REPORT BY SAA.
		List<IndicatorClassification> performanceSubsectors = indicatorClassificationRepository
				.findByPerformanceIndicators(topSector.getIndicatorClassificationId());

		List<String> sectorIds = performanceSubsectors.stream().map(i -> (i.getIndicatorClassificationId() + ""))
				.collect(Collectors.toList());
		// FETCHED ALL SUBSECTOR THAT HAVE PERFORMANCE INDICATOR DATA FOR THE SECTOR FOR
		// LAST THREE TIMEPERIODS

		List<IndicatorClassification> subsectors = indicatorClassificationRepository
				.findByParentTypeAndSectorIdsIn(Type.SUBSECTOR_INDEX, sectorIds);

		List<Indicator> indicators = indicatorRepository.findByIndicatorClassificationInAndIndicatorTypeIn(subsectors,
				IndicatorType.INDEX_INDICATOR, IndicatorType.OVERALL);
		List<Timeperiod> latestTps = timePeriodRepository.findTop3ByOrderByEndDateDesc();
		IndicatorClassification source = new IndicatorClassification();
		source.setIndicatorClassificationId(sourceId);

		List<Data> data = dataRepository
				.findByIndicatorInAndSourceAndTimePeriodInAndAreaAreaCodeOrderByTimePeriodEndDateAsc(indicators, source,
						latestTps, areaCode);

		List<List<BarChart>> dataModels = new ArrayList<>();
		Map<String, List<BarChart>> map = new LinkedHashMap<>();
		for (Data performer : data) {
			BarChart utDataModel = new BarChart();
			utDataModel.setAreaCode(performer.getArea().getAreaCode());
			utDataModel.setAreaName(performer.getAreaName());
			utDataModel.setAreaNid(performer.getArea().getAreaId());
			utDataModel.setValue(df2.format(performer.getPercentage()));
			utDataModel.setSubsectorName(performer.getSubsectorName());
			LocalDateTime ldt = LocalDateTime.ofInstant(performer.getTimePeriod().getStartDate().toInstant(),
					ZoneId.of("Asia/Calcutta"));
			utDataModel.setTimePeriod(
					ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ldt.getYear());
//		System.out.println(performer.getIndicatorUnitSubgroup().getIndicator().getIndicatorName() == null ? "**************" + performer : "");
			utDataModel.setAxis(performer.getIndicatorUnitSubgroup().getIndicator().getIndicatorName());
			utDataModel.setCssColor("");
			utDataModel.setCssColor(performer.getPercentage() <= 0.60d ? Slices.FIRST_SLICE_COLOR
					: performer.getPercentage() >= 0.61d && performer.getPercentage() <= 0.80d
							? Slices.SECOND_SLICE_COLOR
							: performer.getPercentage() >= 0.81d ? Slices.FIFTHSLICES_COLOR
									: Slices.SIXTH_SLICES_COLOR);
			if (map.containsKey(utDataModel.getTimePeriod())) {
				map.get(utDataModel.getTimePeriod()).add(utDataModel);
			} else {
				List<BarChart> barModels = new ArrayList<>();
				barModels.add(utDataModel);
				map.put(utDataModel.getTimePeriod(), barModels);
			}
		}

		map.forEach((k, barModels) -> {
			// if one of subsector is missing we add all the subsector.
				performanceSubsectors.forEach(sec->{
					
					List<BarChart> charts = barModels.stream().filter(barChart -> barChart.getAxis().equals(sec.getName())).collect(Collectors.toList());
					if(charts.isEmpty()) {
						//add the Sector
						BarChart utDataModel = new BarChart();
						utDataModel.setAreaCode("");
						utDataModel.setAreaName("");
						utDataModel.setAreaNid(null);
						utDataModel.setValue("N/A");
						utDataModel.setSubsectorName(sec.getName());
						utDataModel.setAxis(sec.getName());
						utDataModel.setTimePeriod(k);
						barModels.add(utDataModel);
					}
				});
			
			dataModels.add(barModels);
		});
		Map<String, List<DataModel>> iData = new LinkedHashMap<>();
		if (!areaCode.equals("IND033")) {
			// START FETCHING ALL INDICATORS OF THE SECTOR FOR LASTEST TP
			Timeperiod latestTp = timePeriodRepository.findTop1ByOrderByEndDateDesc();
			List<Indicator> performanceIndicators = indicatorRepository
					.findByIndicatorClassificationInAndIndicatorTypeAndAppTypeAndClassifier(topSubsectors,
							IndicatorType.ACTUAL_INDICATOR, AppType.APP_V_2, IndicatorClassfier.PERFORMANCE);
			List<Data> indicatorData = dataRepository
					.findByIndicatorInAndTimePeriodAndAreaAreaCodeAndSourceOrderByPercentageDesc(performanceIndicators,
							latestTp, areaCode, source);

			for (Data performer : indicatorData) {
				DataModel utDataModel = new DataModel();
				utDataModel.setAreaCode(performer.getArea().getAreaCode());
				utDataModel.setAreaName(performer.getAreaName());
				utDataModel.setAreaNid(performer.getArea().getAreaId());
				utDataModel.setValue(df.format(performer.getPercentage()));
				utDataModel.setSubsectorName(performer.getSubsectorName());
				LocalDateTime ldt = LocalDateTime.ofInstant(performer.getTimePeriod().getStartDate().toInstant(),
						ZoneId.of("Asia/Calcutta"));
				utDataModel.setTimeperiod(
						ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ldt.getYear());
				utDataModel.setIndicatorName(performer.getIndicator().getIndicatorName());
				utDataModel.setNumerator(performer.getNumerator());
				utDataModel.setDenominator(performer.getDenominator());
				if (iData.containsKey(performer.getSubsectorName())) {
					iData.get(performer.getSubsectorName()).add(utDataModel);
				} else {
					List<DataModel> pDataModels = new ArrayList<>();
					pDataModels.add(utDataModel);
					iData.put(performer.getSubsectorName(), pDataModels);
				}
			}
		}

		PerformanceChartVO vo = new PerformanceChartVO();
		vo.setSubsectorData(dataModels);
		vo.setIndicatorData(iData);

		return vo;
	}

	static class Slices {
		public static String FIRST_SLICE = "firstslices";
		public static String SECOND_SLICE = "secondslices";
		public static String THIRD_SLICE = "thirdslices";
		public static String FOUTRH_SLICE = "fourthslices";
		public static final String FIFTHSLICES = "fifthslices";
		public static final String SIXTH_SLICES = "sixthslices";

		public static String FIRST_SLICE_COLOR = "#d73027";
		public static String SECOND_SLICE_COLOR = "#fc8d59";
		// public static String THIRD_SLICE = "thirdslices";
		// public static String FOUTRH_SLICE = "fourthslices";
		public static final String FIFTHSLICES_COLOR = "#1a9850";
		public static final String SIXTH_SLICES_COLOR = "#d73027";

	}

	@Override
	public List<MLineSeries> getMLineSeriesIndexIndicators(String areaCode, Integer sourceId) {

		IndicatorClassification indexSector = indicatorClassificationRepository.findByType(Type.INDEX);
		List<IndicatorClassification> children = indexSector.getChildren();

		List<Indicator> indicators = indicatorRepository.findByIndicatorClassificationInAndIndicatorTypeIn(children,
				IndicatorType.INDEX_INDICATOR, IndicatorType.OVERALL);
		// GETTING DATA FOR LAST 12 TIMEPERIODS
		List<Timeperiod> latestTps = timePeriodRepository.findTop12ByOrderByEndDateDesc();
		IndicatorClassification source = new IndicatorClassification();
		source.setIndicatorClassificationId(sourceId);

		List<Data> data = new ArrayList<>();
		Timeperiod latestTp = null;
		if (!latestTps.isEmpty()) {
			latestTp = latestTps.get(0);
			data = dataRepository.findByIndicatorInAndSourceAndTimePeriodAndAreaAreaCodeOrderByIndicatorIndicatorIdAsc(
					indicators, source, latestTp, areaCode);
		}
		List<MLineSeries> mLines = new ArrayList<>();
		for (Data performer : data) {
			MLineSeries line = new MLineSeries();
			line.setAreaName(performer.getAreaName());
			line.setAreaNid(performer.getArea().getAreaId());
			line.setKey(performer.getIndicatorName());
			LocalDateTime ldt = LocalDateTime.ofInstant(performer.getTimePeriod().getStartDate().toInstant(),
					ZoneId.of("Asia/Calcutta"));
			line.setTimeperiod(ldt.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + ldt.getYear());
			line.setValue(df2.format(performer.getPercentage()));
			line.setSubsectorKey(
					performer.getIndicatorUnitSubgroup().getIndicator().getIndicatorClassification().getSectorIds()
							+ "");
			line.setCssClass(performer.getPercentage() <= 0.60d ? Slices.FIRST_SLICE
					: performer.getPercentage() >= 0.61d && performer.getPercentage() <= 0.80d ? Slices.SECOND_SLICE
							: performer.getPercentage() >= 0.81d ? Slices.FIFTHSLICES : Slices.SIXTH_SLICES);
			mLines.add(line);
		}
		return mLines;
	}

}
