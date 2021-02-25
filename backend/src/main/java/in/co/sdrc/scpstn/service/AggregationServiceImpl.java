package in.co.sdrc.scpstn.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;

import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Data;
import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.domain.IndicatorUnitSubgroup;
import in.co.sdrc.scpstn.domain.Subgroup;
import in.co.sdrc.scpstn.domain.Submission;
import in.co.sdrc.scpstn.domain.Timeperiod;
import in.co.sdrc.scpstn.domain.Unit;
import in.co.sdrc.scpstn.models.AggregationModel;
import in.co.sdrc.scpstn.models.AppType;
import in.co.sdrc.scpstn.models.FormStatus;
import in.co.sdrc.scpstn.models.IndicatorClassfier;
import in.co.sdrc.scpstn.models.IndicatorClassificationType;
import in.co.sdrc.scpstn.models.IndicatorType;
import in.co.sdrc.scpstn.models.Type;
import in.co.sdrc.scpstn.models.UnitType;
import in.co.sdrc.scpstn.repository.AgencyRepository;
import in.co.sdrc.scpstn.repository.AreaRepository;
import in.co.sdrc.scpstn.repository.DataRepository;
import in.co.sdrc.scpstn.repository.IndicatorClassificationRepository;
import in.co.sdrc.scpstn.repository.IndicatorRepository;
import in.co.sdrc.scpstn.repository.IndicatorUnitSubgroupRepository;
import in.co.sdrc.scpstn.repository.SubgroupRepository;
import in.co.sdrc.scpstn.repository.SubmissionRepository;
import in.co.sdrc.scpstn.repository.TimePeriodRepository;
import in.co.sdrc.scpstn.repository.UnitRepository;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Transactional
public class AggregationServiceImpl implements AggregationService {

	@Autowired
	private AgencyRepository agencyRepository;
	@Autowired
	private IndicatorRepository indicatorRepository;
	@Autowired
	private TimePeriodRepository timePeriodRepository;
	@Autowired
	private SubmissionRepository submissionRepository;
	@Autowired
	private IndicatorClassificationRepository indicatorClassificationRepository;
	@Autowired
	private IndicatorUnitSubgroupRepository indicatorUnitSubgroupRepository;
	@Autowired
	private UnitRepository unitRepository;
	@Autowired
	private DataRepository dataRepository;
	@Autowired
	SubgroupRepository subgroupRepository;
	@Autowired
	AreaRepository areaRepository;

	@Autowired
	DashboardService dashboardService;

	@Autowired(required = false)
	private QuestionRepository questionRepository;

	@Override
	@Transactional
	public boolean aggregateLastMonthData() {

		int day = 0;

		Agency agency = agencyRepository.findAll().get(0);
		// pickup last aggregate date, and iterate aggregation and publish data until
		// current month reached.
		// Since our cronTrigger will be fired once incase of misfire for months lets
		// say, so we have to
		// aggregate for two months.This will applied for any number of months.
		Date lastAggregatedInitally = agency.getLastAggregationDate();

		if (lastAggregatedInitally == null) {
			// pick up the lowest data entry created date, and add one month and take that
			// month and year as
			// aggregation start date.
			Submission submission = submissionRepository.findTop1ByOrderBySubmissionDateAsc();
			if (submission == null) {
				return true;
			}

			lastAggregatedInitally = submission.getSubmissionDate();
			LocalDate laste = new LocalDate(lastAggregatedInitally.getTime());
			laste = laste.minusMonths(1);
			lastAggregatedInitally = laste.toDate();

		}

		LocalDate lastAggregatedDate = new LocalDate(lastAggregatedInitally.getTime());
		int lastAggMonth = lastAggregatedDate.getMonthOfYear();
		int lastAggYear = lastAggregatedDate.getYear();

		LocalDateTime now = LocalDateTime.now();
		day = now.getDayOfMonth();

		int currentYear = now.getYear();
		int currentMonth = now.getMonthValue();

		Date day1OfLastAggDate = null;
		Date day1OfCurrDate = null;
		try {
			day1OfLastAggDate = new SimpleDateFormat("yyyy-MM-dd").parse(
					lastAggYear + "-" + (lastAggMonth < 10 ? "0" + lastAggMonth : lastAggMonth) + "-01 00:00:00");
			day1OfCurrDate = new SimpleDateFormat("yyyy-MM-dd").parse(
					currentYear + "-" + (currentMonth < 10 ? "0" + currentMonth : currentMonth) + "-01 00:00:00");
		} catch (ParseException e) {

		}

		DateTime sDate = new DateTime(day1OfLastAggDate.getTime());
		DateTime eDate = new DateTime(day1OfCurrDate.getTime());
		int difInMonths = Months.monthsBetween(sDate, eDate).getMonths();

		log.info("Last agg Date :{}", day1OfLastAggDate);
		log.info("Curr Date :{}", day1OfCurrDate);
		log.info("Diff In Months :{}", Months.monthsBetween(sDate, eDate).getMonths());

		for (int monthToIterate = 0; monthToIterate < difInMonths; monthToIterate++) {

			agency = agencyRepository.findByAgencyId(1);

			// in second loop, value will be picked from Session Cache of JPA as we are
			// updating the
			// LastAggregationDate below and have not committed in database
			Date lastAggregated = agency.getLastAggregationDate();

			if (lastAggregated == null) {
				lastAggregated = lastAggregatedInitally;
			}

			lastAggregatedDate = new LocalDate(lastAggregated.getTime());

			lastAggMonth = lastAggregatedDate.getMonthOfYear();
			lastAggYear = lastAggregatedDate.getYear();

			log.info("Last agg Month :{}", lastAggMonth);
			log.info("Last agg Year :{}", lastAggYear);

			if (agency.getAggStartDay() == day) {
				LocalDate oneMonthIncreasedLastAggregatedDate = lastAggregatedDate.plusMonths(1);

				LocalDateTime dataTime = LocalDateTime.ofInstant(
						oneMonthIncreasedLastAggregatedDate.toDate().toInstant(), ZoneId.of("Asia/Calcutta"));

				Date startDate = null, endDate = null;
				try {
					YearMonth ym = YearMonth.of(dataTime.getYear(), dataTime.getMonth());
					System.out.println(ym.lengthOfMonth());
					startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
							.parse(dataTime.getYear() + "-" + dataTime.getMonthValue() + "-01" + " 00:00:00");
					endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataTime.getYear() + "-"
							+ dataTime.getMonthValue() + "-" + ym.lengthOfMonth() + " 23:59:59");
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
				Submission submission = submissionRepository
						.findTop1BySubmissionDateBetweenOrderBySubmissionDateAsc(startDate, endDate);
				if (submission == null) {
					agency.setLastAggregationDate(oneMonthIncreasedLastAggregatedDate.toDate());
					continue;
				}

				aggregateDataByAgency(agency, lastAggYear, lastAggMonth, dataTime);
//				System.out.println("Completed aggregae data");
				createIndex(agency, lastAggYear, lastAggMonth);
//				System.out.println("Completed index");

				createIndexForSubsectors(agency, lastAggYear, lastAggMonth);
//				System.out.println("Completed createIndexForSubsectors");

				generateIndexOfSectorBySource(agency, lastAggYear, lastAggMonth);
//				System.out.println("Completed generateIndexOfSectorBySource");

				generateIndexOfSubsectorsBySource(agency, lastAggYear, lastAggMonth);
//				System.out.println("Completed generateIndexOfSubsectorsBySource");

				createStateLevelIndexOfIndicators(agency, lastAggYear, lastAggMonth);
//				System.out.println("Completed createStateLevelIndexOfIndicators");

				agency.setLastAggregationDate(oneMonthIncreasedLastAggregatedDate.toDate());

			}
//			System.out.println("Rounds completed::::::"+ monthToIterate);
		}

		return true;
	}

	public boolean aggregateDataByAgency(Agency agency, int yearOfPreviousMonth, int previousMonth,
			LocalDateTime lastMonthPlusOne) {

		String monthString = "";
		if (previousMonth >= 10) {
			monthString = previousMonth + "";
		} else {
			monthString = "0" + previousMonth;
		}
		Year y = Year.of(yearOfPreviousMonth);
		Month m = Month.of(previousMonth);

		String dayString = "";
		if (m.length(y.isLeap()) < 10) {
			dayString = "0" + m.length(y.isLeap());
		} else {
			dayString = m.length(y.isLeap()) + "";
		}
		Date startDate = null, endDate = null;
		try {
			YearMonth ym = YearMonth.of(lastMonthPlusOne.getYear(), lastMonthPlusOne.getMonth());
			startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(lastMonthPlusOne.getYear() + "-" + lastMonthPlusOne.getMonthValue() + "-01" + " 00:00:00");
			endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastMonthPlusOne.getYear() + "-"
					+ lastMonthPlusOne.getMonthValue() + "-" + ym.lengthOfMonth() + " 23:59:59");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		Timeperiod timeperiod = timePeriodRepository.findByTimePeriod(yearOfPreviousMonth + "." + monthString);
		if (timeperiod == null) {
			Timeperiod newTimeperiod = new Timeperiod();
			newTimeperiod.setTimePeriod(yearOfPreviousMonth + "." + monthString);
			try {
				newTimeperiod.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-01 00:00:00"));
				newTimeperiod.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-" + dayString + " 23:59:59"));
				newTimeperiod.setAppType(AppType.APP_V_2);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			newTimeperiod.setPeriodicity("1");
			timeperiod = timePeriodRepository.save(newTimeperiod);
		}
		Unit unit = unitRepository.findByUnitType(UnitType.PERCENTAGE);
		Subgroup subgroup = subgroupRepository.findOne(1);

		List<Indicator> indicators = indicatorRepository
				.findAllByAgencyAndAppTypeAndIndicatorTypeNotInOrderByIndicatorIdAsc(new Agency(1), AppType.APP_V_2,
						IndicatorType.INDEX_INDICATOR, IndicatorType.INDEX_OVERALL_SOURCEWISE);
		// get all submissions except CCI'S
		List<Submission> submissions = submissionRepository
				.findAllByFormStatusAndSubmissionDateBetween(FormStatus.FINALIZED, startDate, endDate);

		List<AggregationModel> models = new ArrayList<>();

		Map<String, AggregationModel> modelMap = new HashMap<>();

		List<Question> questionList = questionRepository.findAllByActiveTrueOrderByQuestionOrderAsc();

		Map<String, Question> questionMap = questionList.stream()
				.collect(Collectors.toMap(Question::getColumnName, question -> question));

		for (Submission submission : submissions) {

//			System.out.println("submission:::" + submission.getId() + "   ");
			JsonNode data = submission.getData();

			for (Indicator indicator : indicators) {
				Indicator numerator = indicator.getNumeratorDenominator().stream()
						.filter(e -> e.getIndicatorType() == IndicatorType.NUMERATOR).collect(Collectors.toList())
						.get(0);
				Indicator denominator = indicator.getNumeratorDenominator().stream()
						.filter(e -> e.getIndicatorType() == IndicatorType.DENOMINATOR).collect(Collectors.toList())
						.get(0);
				
				// if the numerator's key is available in the JSON data, then indicator exists
				// for the submission.
				if (data.has(numerator.getJsonXpath()) && data.has(denominator.getJsonXpath()) 
						&& !( data.get(denominator.getJsonXpath()).isNull()
								|| data.get(denominator.getJsonXpath()).asText().trim().equals("")
								|| new BigDecimal(data.get(denominator.getJsonXpath()).asText().trim()).compareTo(BigDecimal.ZERO) == 0)) {
					
//					System.out.println("Numerator:::" + numerator.getJsonXpath() + "  , denominator ::  "
//							+ denominator.getJsonXpath()
//
//							+ " numerator:::" + data.get(numerator.getJsonXpath()).asText("0").trim() + ","
//							+ "  denominator:::" + data.get(denominator.getJsonXpath()).asText("0").trim());
					switch (indicator.getIndicatorSource()) {
					case "CH":
					case "OH":
					case "SAA":
					case "ACH":
					case "SH":
					case "RU":
					case "OS":
					// AS THESE ARE CCI TYPES, WE HAVE TO SUM THE NUMERATOR AND DENOMINATOR

					{

						AggregationModel model = null;
						if (modelMap.containsKey(indicator.getIndicatorSource() + ":"
								+ submission.getFacility().getArea().getAreaId())) {
							model = modelMap.get(indicator.getIndicatorId() + ":" + indicator.getIndicatorSource() + ":"
									+ submission.getFacility().getArea().getAreaId());
							Integer numeratorValue;
							Integer denominatorValue;

							if ((questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("tel") 
									|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("doubledecimal")
									|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("decimal")

									)
									&& data.get(numerator.getJsonXpath()).asText("0").trim().contains(".")) {
								numeratorValue = ((int) (Double
										.parseDouble((data.get(numerator.getJsonXpath()).asText().trim()))));
							} else {
								numeratorValue = Integer
										.parseInt(data.get(numerator.getJsonXpath()).asText("0").trim());
							}
							if ((questionMap.get(denominator.getJsonXpath()).getFieldType().contentEquals("tel")
									|| questionMap.get(denominator.getJsonXpath()).getFieldType().contentEquals("doubledecimal")
									|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("decimal")
)
									&& data.get(denominator.getJsonXpath()).asText().trim().contains(".")) {
								denominatorValue = ((int) (Double
										.parseDouble((data.get(denominator.getJsonXpath()).asText().trim()))));
							} else {
								denominatorValue = Integer
										.parseInt(data.get(denominator.getJsonXpath()).asText().trim());
							}

							numeratorValue = numeratorValue + model.getNumerator();
							denominatorValue = denominatorValue + model.getDenominator();
							model.setNumerator(numeratorValue);
							model.setDenominator(denominatorValue);
							modelMap.put(indicator.getIndicatorId() + ":" + indicator.getIndicatorSource() + ":"
									+ submission.getFacility().getArea().getAreaId(), model);

						} else {
							Integer numeratorValue;
							Integer denominatorValue;
//							System.out.println(questionMap.get(numerator.getJsonXpath()).getFieldType());
//							System.out.println(numerator.getJsonXpath());

							if ((questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("tel")
									|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("doubledecimal")
									
									|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("decimal")
									)
									&& data.get(numerator.getJsonXpath()).asText("0").trim().contains(".")) {
								numeratorValue = ((int) (Double
										.parseDouble((data.get(numerator.getJsonXpath()).asText().trim()))));
							} else {
								numeratorValue = Integer.parseInt(data.get(numerator.getJsonXpath()).asText("0").trim());
							}
							if ((questionMap.get(denominator.getJsonXpath()).getFieldType().contentEquals("tel")
									|| questionMap.get(denominator.getJsonXpath()).getFieldType().contentEquals("doubledecimal")
									|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("decimal")
									)
									&& data.get(denominator.getJsonXpath()).asText().trim().contains(".")) {
								denominatorValue = ((int) (Double
										.parseDouble((data.get(denominator.getJsonXpath()).asText().trim()))));
							} else {
								denominatorValue = Integer
										.parseInt(data.get(denominator.getJsonXpath()).asText().trim());
							}
							model = new AggregationModel();
							model.setIndicatorId(indicator.getIndicatorId());
							model.setAreaId(submission.getFacility().getArea().getAreaId());
							model.setNumerator(numeratorValue);
							model.setDenominator(denominatorValue);
							model.setSubgroupId(subgroup.getSubgroupValueId());
							model.setUnitId(unit.getUnitId());
							model.setSubsector(indicator.getIndicatorClassification());
							model.setSector(indicator.getIndicatorClassification().getParent());
							modelMap.put(indicator.getIndicatorId() + ":" + indicator.getIndicatorSource() + ":"
									+ submission.getFacility().getArea().getAreaId(), model);
						}

					}
						break;
					default: {
						Integer numeratorValue;
						Integer denominatorValue;

						if ((questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("tel")
								|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("doubledecimal")
								|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("decimal")
)
								&& data.get(numerator.getJsonXpath()).asText("0").trim().contains(".")) {
							numeratorValue = ((int) (Double
									.parseDouble((data.get(numerator.getJsonXpath()).asText().trim()))));
						} else {
							numeratorValue = Integer.parseInt(data.get(numerator.getJsonXpath()).asText("0").trim());
						}
						if ((questionMap.get(denominator.getJsonXpath()).getFieldType().contentEquals("tel")
								|| questionMap.get(denominator.getJsonXpath()).getFieldType().contentEquals("doubledecimal")|| questionMap.get(numerator.getJsonXpath()).getFieldType().contentEquals("decimal"))
								&& data.get(denominator.getJsonXpath()).asText().trim().contains(".")) {
							denominatorValue = ((int) (Double
									.parseDouble((data.get(denominator.getJsonXpath()).asText().trim()))));
						} else {
							denominatorValue = Integer
									.parseInt(data.get(denominator.getJsonXpath()).asText().trim());
						}
						
						AggregationModel model = new AggregationModel();
						model.setIndicatorId(indicator.getIndicatorId());
						model.setAreaId(submission.getFacility().getArea().getAreaId());
						model.setNumerator(numeratorValue);
						model.setDenominator(denominatorValue);
						model.setSubgroupId(subgroup.getSubgroupValueId());
						model.setUnitId(unit.getUnitId());
						model.setSubsector(indicator.getIndicatorClassification());
						model.setSector(indicator.getIndicatorClassification().getParent());
						modelMap.put(indicator.getIndicatorId() + ":" + indicator.getIndicatorSource() + ":"
								+ submission.getFacility().getArea().getAreaId(), model);
					}
					}

				}
			}
		}
		modelMap.forEach((k, v) -> {
			models.add(v);
		});
		// Note taking data was entered in Unit "as Percent" and Subgroup as
		// "total" for all districts of an agency
		List<Data> datas = new ArrayList<Data>();
		for (AggregationModel model : models) {
			int areaId = model.getAreaId();
			int indicatorId = model.getIndicatorId();
			int numerator = model.getNumerator();
			int denominator = model.getDenominator();
			Area district = areaRepository.findByAreaId(areaId);

			Indicator indicator = indicatorRepository.findByIndicatorId(indicatorId);
			IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorAndUnitAndSubgroup(indicator,
					unit, subgroup);

			IndicatorClassification source = indicatorClassificationRepository
					.findByNameAndParentIsNotNull(indicator.getIndicatorSource());

			Data data = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(ius, source, timeperiod,
					district);

			if (data == null) {
				data = new Data();
				data.setArea(district);
				data.setIndicator(indicator);
				data.setIndicatorUnitSubgroup(ius);
				data.setSubgroup(subgroup);
				data.setTimePeriod(timeperiod);
				data.setUnit(unit);
				data.setSource(source);
				data.setIndicatorName(indicator.getIndicatorName());
				data.setUnitName(unit.getUnitName());
				data.setSubgroupName(subgroup.getSubgroupVal());
				data.setSectorName(model.getSector().getName());
				data.setSubsectorName(model.getSubsector().getName());

				data.setAreaName(district.getAreaName());
				data.setSourceName(source.getName());
				data.setIndicatorClassifier(indicator.getClassifier().toString());

			}

			data.setDenominator(denominator);
			data.setNumerator(numerator);
			if (denominator == 0) {
				data.setPercentage(0.00d);
			} else if (numerator > denominator) {
				data.setPercentage(100.0d);
			} else {
				int num = numerator * 100;
				data.setPercentage((new BigDecimal(num).divide(new BigDecimal(denominator), 1, RoundingMode.HALF_UP))
						.doubleValue());
			}
			
			datas.add(data);
		}
		datas = dataRepository.save(datas);
		return true;
	}

	/**
	 * for each indicator find values in all districts for a particular timeperiod
	 * for which index needs to be calculated. We take the following steps to
	 * calculate the normalized value of positive indicator : a) If all data points
	 * of an indicator for a particular timeperiod are less than 100 and are same,
	 * then the normalized value of all data points is 0. b) If all data points of
	 * an indicator for a particular timeperiod are 100 and same, then the
	 * normalized value of all data points is 1. c) If all data points of an
	 * indicator for a particular timeperiod are less than 100 and are not same,
	 * then the normalized value of each datapoint is calculated. 1. If the data
	 * points ( < 100) for all the area is same, then the normalized value must be
	 * Ã¢â‚¬Å“0Ã¢â‚¬Â�. 2. If the data points ( = 100) for all the area is same,
	 * then the normalized value must be Ã¢â‚¬Å“1Ã¢â‚¬Â�.
	 * 
	 * 
	 * We take the following steps to calculate the normalized value of negative
	 * indicator : a) If all data points of an indicator for a particular timeperiod
	 * are greater than 0 ( > 0) and are same, then the normalized value of all data
	 * points is 0. b) If all data points of an indicator for a particular
	 * timeperiod are 0 and same, then the normalized value of all data points is 1.
	 * c) If all data points of an indicator for a particular timeperiod are less
	 * than 100 and are not same, then the normalized value of each datapoint is
	 * calculated. 1. If the data points ( > 0) for all the area is same, then the
	 * normalized value must be Ã¢â‚¬Å“0Ã¢â‚¬Â�. 2. If the data points ( = 0) for
	 * all the area is same, then the normalized value must be Ã¢â‚¬Å“1Ã¢â‚¬Â�.
	 * 
	 */

	@Transactional
	public boolean createIndex(Agency agency, int yearOfPreviousMonth, int previousMonth) {
		DecimalFormat df = new DecimalFormat("#.##");
		String monthString = "";
		if (previousMonth >= 10) {
			monthString = previousMonth + "";
		} else {
			monthString = "0" + previousMonth;
		}

		Timeperiod timeperiod = timePeriodRepository.findByTimePeriod(yearOfPreviousMonth + "." + monthString);
		if (timeperiod == null) {
			String dayString = "";
			Month m = Month.of(previousMonth);
			Year y = Year.of(yearOfPreviousMonth);
			if (m.length(y.isLeap()) < 10) {
				dayString = "0" + m.length(y.isLeap());
			} else {
				dayString = m.length(y.isLeap()) + "";
			}
			Timeperiod newTimeperiod = new Timeperiod();
			newTimeperiod.setTimePeriod(yearOfPreviousMonth + "." + monthString);
			try {
				newTimeperiod.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-01 00:00:00"));
				newTimeperiod.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-" + dayString + " 23:59:59"));
				newTimeperiod.setAppType(AppType.APP_V_2);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			newTimeperiod.setPeriodicity("1");
			timeperiod = timePeriodRepository.save(newTimeperiod);
		}

		/*
		 * 1. Fetch the source name INDEX_COMPUTED 2. CREATE A MAP , PUT ALL NORMALIZED
		 * DATA OF ALL INDICATORS OF ALL AREA IN A MAP, WITH SECTOR_ID AS ITS KEY
		 */
		Unit indexUnit = unitRepository.findByUnitType(UnitType.INDEX);

		IndicatorClassification sourceForComputedIndex = indicatorClassificationRepository
				.findByType(Type.INDEX_COMPUTED);

		Map<Integer, List<Data>> indexAgainstSector = new LinkedHashMap<>();

		Unit percentageUnit = unitRepository.findByUnitType(UnitType.PERCENTAGE);

		Subgroup subgroup = subgroupRepository.findOne(1);

		/*
		 * 1. Fetch the ALL THE SECTORS, WHOSE PARENT ID IS NULL AND INDEX COLUMN IN DB
		 * IS FALSE
		 */

		List<IndicatorClassification> ics = indicatorClassificationRepository
				.findByIndicatorClassificationTypeAndParentIsNullAndIndexIsFalse(IndicatorClassificationType.SC);

		ListIterator<IndicatorClassification> it = ics.listIterator();
		while (it.hasNext()) {

			IndicatorClassification sector = (IndicatorClassification) it.next();

			List<Data> indexDataForAllDistricts = new ArrayList<>();
			/*
			 * 1. Fetch the ALL INDICATORS BELONGING THE SUBSECTOR OF THE SECTOR
			 */
			List<Indicator> indicators = indicatorRepository
					.findByClassifierAndIndicatorClassificationIn(IndicatorClassfier.PERFORMANCE, sector.getChildren());

			/*
			 * 1. CREATE A MAP WITH DISTRICT ID AS ITS KEY, AND LIST OF SUBMISSIONS FOR THE
			 * ALL THE INDICATORS FOR THE PARTICULAR TIMEPERIOD WITH ITS NORMALIZED VALUES
			 */
			Map<Integer, List<Data>> districtWiseAllIndicatorsNormalizedDataInList = new LinkedHashMap<>();

			ListIterator<Indicator> indicatorIterator = indicators.listIterator();
			while (indicatorIterator.hasNext()) {

				Indicator indicator = indicatorIterator.next();

				IndicatorClassification source = indicatorClassificationRepository
						.findByNameAndParentIsNotNull(indicator.getIndicatorSource());

				List<Data> dataValues = dataRepository
						.findByIndicatorAndUnitAndSubgroupAndSourceAndTimePeriodOrderByPercentageAsc(indicator,
								percentageUnit, subgroup, source, timeperiod);

				log.debug("indicator Id : {}", indicator.getIndicatorId());
				log.debug("unit Id : {}", percentageUnit.getUnitId());
				log.debug("subgroup Id : {}", subgroup.getSubgroupValueId());
				log.debug("source Id : {}", source.getIndicatorClassificationId());
				log.debug("timeperiod Id : {}", timeperiod.getTimeperiodId());

				
				/* The process of normalization starts. We check for all the three assumptions based on values.
				 * i.e. doAllAreasHaveSameValue, doAllAreasHaveHundredForIndicator, allAreasHavingZero
				 */
				if (dataValues != null && dataValues.size() != 0) {
					boolean doAllAreasHaveSameValue = dataValues.stream()
							.allMatch(e -> e.getPercentage().compareTo(dataValues.get(0).getPercentage()) == 0);
					boolean doAllAreasHaveHundredForIndicator = dataValues.stream()
							.allMatch(e -> e.getPercentage().compareTo(new Double("100.00")) == 0);
					boolean allAreasHavingZero = dataValues.stream()
							.allMatch(i -> i.getPercentage().compareTo(new Double("0.00")) == 0);

					if (indicator.isHighIsGood()) {
						if (doAllAreasHaveHundredForIndicator) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("1.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else if (doAllAreasHaveSameValue) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("0.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else {
							/*
							 * Since, the first two assumptions didn't match for the list of values of different areas for an indicator, 
							 * normalization of values of indicator for each area will be calculated using the formula. For it finding of
							 * maximum and minimum in the values takes place.
							 * If only one data value is present then, maximum and minimum is taken same.
							 */
							BigDecimal minimum;
							BigDecimal maximum;
							if (dataValues.size() == 1) {
								minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
							} else {
								log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
								log.debug("DF formatted maximum :{}",
										df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

								minimum = dataValues.size() > 0
										? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
										: new BigDecimal("0.00");
								maximum = dataValues.size() > 0
										? new BigDecimal(
												df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
														.setScale(2)
										: new BigDecimal("0.00");
							}
							
							/*
							 * Once the maximum minimum was already found out , all the areas of a indicator are called in loop
							 * to normalize the values. The normalization stored the normalized value in the field normalizedValue
							 * of Data domain.
							 * The all those domain is put on map named 'districtWiseAllIndicatorsNormalizedDataInList'
							 * which contains areaId as Key and the domain object which has normalized value in the value.
							 * 
							 */
							for (Data data2 : dataValues) {
								data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						}

					} else {
						if (allAreasHavingZero) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("1.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else if (doAllAreasHaveSameValue) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("0.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else {
							BigDecimal minimum;
							BigDecimal maximum;
							if (dataValues.size() == 1) {
								minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
							} else {
								log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
								log.debug("DF formatted maximum :{}",
										df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

								minimum = dataValues.size() > 0
										? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
										: new BigDecimal("0.00");
								maximum = dataValues.size() > 0
										? new BigDecimal(
												df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
														.setScale(2)
										: new BigDecimal("0.00");
							}
							for (Data data2 : dataValues) {
								data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						}
					}
				}
			}

			/*
			 * 1. FOR EACH DISTRICT AS KEY, WE HAVE NORMALIZED VALUE OF ALL INDICATORS FOR
			 * THE SECTORS AS VALUE 2. FOR EACH DISTRICT ITERATE AND GET AN AVERAGE OF ALL
			 * THE NORMALIZED INDICATORS OF THE SECTOR 3. THE DENOMINATOR IS TAKEN AS COUNT
			 * OF THE REPORTED INDICATORS 4. FIND THE INDEX SECTOR OF THE EQUIVALENT SECTOR
			 * BE USING ITS ID, TO QUERY THE TABLE SECTORIDS COLUMN IN TABLE 5. AFTER
			 * FINDING THE INDEX SECTOR, QUERY INDICATOR TABLE'S
			 * 'INDICATORCLASSIFICATION_ID' COLUMN TO FIND THE EQUIVALENT INDEX INDICATOR
			 * FOR THAT SECTOR. 5. CREATE A NEW DATA POINT OBJECT, AND SET THE AREA AS
			 * DISTRICT ID,INDICATOR AS THE FOUND INDEX-INDICATOR FOR THE SECTOR, AND SET
			 * SOURCE AS COMPUTED INDEX. 6. PUSH THE NEW DATA POINT INTO A LIST NAMED
			 * 'indexDataForAllDistricts' THAT WILL HOLD ALL THE INDEX-INDICATOR-FOR-SECTOR
			 * VALUES OF ALL DISTRICT 7. FINALLY, PUSH THE LIST INTO A MAP THAT TAKES
			 * SECTORID AS KEY AND VALUE AS LIST OF VALUES OF INDEX-INDICATOR-FOR-SECTOR FOR
			 * ALL DISTRICT.
			 */
			Timeperiod tp = timeperiod;
			districtWiseAllIndicatorsNormalizedDataInList.forEach((district, normalizedDataOfAllIndicators) -> {

				BigDecimal avg = new BigDecimal(0.00);
				BigDecimal sum = new BigDecimal(0.00);

				for (Data data : normalizedDataOfAllIndicators) {
					sum = sum.add(data.getNormalizedValue());
					log.debug("indicator Id :{}", data.getIndicator().getIndicatorId());
					log.debug("Data Id :{}", data.getDataId());
					log.debug("Normalized Value :{}", data.getNormalizedValue());
				}

				avg = sum.divide(new BigDecimal(normalizedDataOfAllIndicators.size()), 2, RoundingMode.HALF_UP);

				log.debug("--Sum--:{}", sum);
				log.debug("--Size--:{}", normalizedDataOfAllIndicators.size());
				log.debug("--avg--:{}", avg);

				Area area = areaRepository.findByAreaId(district);

				IndicatorClassification subsectorOfIndex = indicatorClassificationRepository
						.findBySectorIdsAndParentType(String.valueOf(sector.getIndicatorClassificationId()),
								Type.INDEX);

				Indicator i = (indicatorRepository.findByAgencyAndIndicatorClassificationAndIndicatorType(agency,
						subsectorOfIndex, IndicatorType.INDEX_INDICATOR));

				IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorAndUnitAndSubgroup(i,
						indexUnit, subgroup);

				Data avgIndexOfDistrict = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(ius,
						sourceForComputedIndex, tp, area);

				if (avgIndexOfDistrict == null) {
					avgIndexOfDistrict = new Data();
					avgIndexOfDistrict.setArea(area);
					avgIndexOfDistrict.setDenominator(0);
					avgIndexOfDistrict.setNumerator(0);
					avgIndexOfDistrict.setSource(sourceForComputedIndex);
					avgIndexOfDistrict.setSubgroup(subgroup);
					avgIndexOfDistrict.setTimePeriod(tp);
					avgIndexOfDistrict.setUnit(indexUnit);
					avgIndexOfDistrict.setIndicator(i);
					avgIndexOfDistrict.setIndicatorUnitSubgroup(ius);

					avgIndexOfDistrict.setIndicatorName(i.getIndicatorName());
					avgIndexOfDistrict.setUnitName(indexUnit.getUnitName());
					avgIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
					avgIndexOfDistrict.setSectorName(subsectorOfIndex.getParent().getName());
					avgIndexOfDistrict.setSubsectorName(subsectorOfIndex.getName());

					avgIndexOfDistrict.setSourceName(sourceForComputedIndex.getName());
					avgIndexOfDistrict.setAreaName(area.getAreaName());

				}

				avgIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));

				indexDataForAllDistricts.add(avgIndexOfDistrict);

			});

			indexAgainstSector.put(sector.getIndicatorClassificationId(), indexDataForAllDistricts);

		}

		// Generating value for Overall Index

		Timeperiod tp = timeperiod;

		Map<Integer, List<Data>> districtOverallMap = new LinkedHashMap<>();

		indexAgainstSector.forEach((indicatorClassification, indexValues) -> {

			for (Data data2 : indexValues) {

				log.debug("indicator id:::::::{}", data2.getIndicator().getIndicatorId());
				log.debug("ius id :::::::::{}", data2.getIndicatorUnitSubgroup().getIndicatorUnitSubgroupId());
				log.debug("source id:::::::{}", data2.getSource().getIndicatorClassificationId());
				log.debug("timeperiod id:::::::{}", data2.getTimePeriod().getTimePeriod());
				log.debug("area id:::::::{}", data2.getArea().getAreaId());
				// checking data if exists for
				Data d = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(
						data2.getIndicatorUnitSubgroup(), data2.getSource(), data2.getTimePeriod(), data2.getArea());

				if (districtOverallMap.get(data2.getArea().getAreaId()) == null) {
					List<Data> indicatorData = new ArrayList<>();
					if (d == null)
						indicatorData.add(data2);
					else
						indicatorData.add(d);
					districtOverallMap.put(data2.getArea().getAreaId(), indicatorData);
				} else {
					List<Data> indicatorData = districtOverallMap.get(data2.getArea().getAreaId());
					if (d == null)
						indicatorData.add(data2);
					else
						indicatorData.add(d);
					districtOverallMap.put(data2.getArea().getAreaId(), indicatorData);
				}
			}
			// for each sector persist index values.
//			indexValues.forEach(d->{
//				System.out.println(d);
//				dataRepository.save(d);
//			});
			dataRepository.save(indexValues);
		});

		districtOverallMap.forEach((district, calculatedIndexForIndicators) -> {

			log.debug("District :::{}", district);

			BigDecimal avg = new BigDecimal(0.00);
			BigDecimal sum = new BigDecimal(0.00);
			for (Data d : calculatedIndexForIndicators) {
				sum = sum.add(new BigDecimal(d.getPercentage()));
			}

			avg = sum.divide(new BigDecimal(calculatedIndexForIndicators.size()), 2, RoundingMode.HALF_UP);

			Indicator overallIndicator = indicatorRepository
					.findAllByAgencyAndIndicatorType(agency, IndicatorType.OVERALL).get(0);

			IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository
					.findByIndicatorAndUnitAndSubgroup(overallIndicator, indexUnit, subgroup);

			Area area = areaRepository.findByAreaId(district);

			Data overallIndexOfDistrict = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(ius,
					sourceForComputedIndex, tp, area);

			if (overallIndexOfDistrict == null) {
				overallIndexOfDistrict = new Data();
				overallIndexOfDistrict.setArea(area);
				overallIndexOfDistrict.setDenominator(0);
				overallIndexOfDistrict.setNumerator(0);
				overallIndexOfDistrict.setSource(sourceForComputedIndex);
				overallIndexOfDistrict.setTimePeriod(tp);
				overallIndexOfDistrict.setUnit(indexUnit);
				overallIndexOfDistrict.setSubgroup(subgroup);
				overallIndexOfDistrict.setIndicator(overallIndicator);
				overallIndexOfDistrict.setIndicatorUnitSubgroup(ius);

				overallIndexOfDistrict.setIndicatorName(overallIndicator.getIndicatorName());
				overallIndexOfDistrict.setUnitName(indexUnit.getUnitName());
				overallIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
				overallIndexOfDistrict
						.setSectorName(overallIndicator.getIndicatorClassification().getParent().getName());
				overallIndexOfDistrict.setSubsectorName(overallIndicator.getIndicatorClassification().getName());

				overallIndexOfDistrict.setSourceName(sourceForComputedIndex.getName());
				overallIndexOfDistrict.setAreaName(area.getAreaName());

			}
			overallIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));
//			System.out.println(overallIndexOfDistrict);
			dataRepository.save(overallIndexOfDistrict);

		});

		return true;

	}

	@Transactional
	public boolean createIndexForSubsectors(Agency agency, int yearOfPreviousMonth, int previousMonth) {
		DecimalFormat df = new DecimalFormat("#.##");
		String monthString = "";
		if (previousMonth >= 10) {
			monthString = previousMonth + "";
		} else {
			monthString = "0" + previousMonth;
		}

		Timeperiod timeperiod = timePeriodRepository.findByTimePeriod(yearOfPreviousMonth + "." + monthString);
		if (timeperiod == null) {
			String dayString = "";
			Month m = Month.of(previousMonth);
			Year y = Year.of(yearOfPreviousMonth);
			if (m.length(y.isLeap()) < 10) {
				dayString = "0" + m.length(y.isLeap());
			} else {
				dayString = m.length(y.isLeap()) + "";
			}
			Timeperiod newTimeperiod = new Timeperiod();
			newTimeperiod.setTimePeriod(yearOfPreviousMonth + "." + monthString);
			try {
				newTimeperiod.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-01 00:00:00"));
				newTimeperiod.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-" + dayString + " 23:59:59"));
				newTimeperiod.setAppType(AppType.APP_V_2);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			newTimeperiod.setPeriodicity("1");
			timeperiod = timePeriodRepository.save(newTimeperiod);
		}

		/*
		 * 1. Fetch the source name INDEX_COMPUTED 2. CREATE A MAP , PUT ALL NORMALIZED
		 * DATA OF ALL INDICATORS OF ALL AREA IN A MAP, WITH SECTOR_ID AS ITS KEY
		 */
		Unit indexUnit = unitRepository.findByUnitType(UnitType.INDEX);

		Map<Integer, List<Data>> indexAgainstSubsector = new LinkedHashMap<>();

		Unit percentageUnit = unitRepository.findByUnitType(UnitType.PERCENTAGE);

		Subgroup subgroup = subgroupRepository.findOne(1);
		/*
		 * 1. Fetch the ALL THE SECTORS, WHOSE PARENT ID IS NULL AND INDEX COLUMN IN DB
		 * IS FALSE
		 */

		// GET LIST OF SUBSECTORS
//		List<IndicatorClassification> subsectors = indicatorClassificationRepository
//				.findByIndicatorClassificationTypeAndParentIsNotNullAndSectorIdsIsNull(IndicatorClassificationType.SC);

		List<IndicatorClassification> subsectors = indicatorClassificationRepository
				.findByIndicatorClassificationTypeAndParentType(IndicatorClassificationType.SC, Type.SUBSECTOR_INDEX);

//		IndicatorClassification sectorOfSource = (IndicatorClassification) it.next();
//
//		List<Data> indexDataForAllDistricts = new ArrayList<>();
//		/*
//		 * 1. Fetch the ALL INDICATORS BELONGING THE SUBSECTOR OF THE SECTOR
//		 */
//		IndicatorClassification sector = indicatorClassificationRepository.findOne(Integer.parseInt(sectorOfSource.getSectorIds()));
//		List<Indicator> indicators = indicatorRepository.findByClassifierAndIndicatorClassificationInAndIndicatorSource(IndicatorClassfier.PERFORMANCE,sector.getChildren(),iSource);

		ListIterator<IndicatorClassification> it = subsectors.listIterator();
		while (it.hasNext()) {

			IndicatorClassification subsectorOfSource = (IndicatorClassification) it.next();

			IndicatorClassification subsector = indicatorClassificationRepository
					.findOne(Integer.parseInt(subsectorOfSource.getSectorIds()));

			List<Data> indexDataForAllDistricts = new ArrayList<>();
			/*
			 * 1. Fetch the ALL INDICATORS BELONGING THE SUBSECTOR OF THE SECTOR
			 */

			List<Indicator> indicators = indicatorRepository
					.findByClassifierAndIndicatorClassification(IndicatorClassfier.PERFORMANCE, subsector);

			/*
			 * 1. CREATE A MAP WITH DISTRICT ID AS ITS KEY, AND LIST OF SUBMISSIONS FOR THE
			 * ALL THE INDICATORS FOR THE PARTICULAR TIMEPERIOD WITH ITS NORMALIZED VALUES
			 */
			Map<Integer, List<Data>> districtWiseAllIndicatorsNormalizedDataInList = new LinkedHashMap<>();

			ListIterator<Indicator> indicatorIterator = indicators.listIterator();
			while (indicatorIterator.hasNext()) {

				Indicator indicator = indicatorIterator.next();
				IndicatorClassification source = indicatorClassificationRepository
						.findByNameAndParentIsNotNull(indicator.getIndicatorSource());

				List<Data> dataValues = dataRepository
						.findByIndicatorAndUnitAndSubgroupAndSourceAndTimePeriodOrderByPercentageAsc(indicator,
								percentageUnit, subgroup, source, timeperiod);

				log.debug("indicator Id : {}", indicator.getIndicatorId());
				log.debug("unit Id : {}", percentageUnit.getUnitId());
				log.debug("subgroup Id : {}", subgroup.getSubgroupValueId());
				log.debug("source Id : {}", source.getIndicatorClassificationId());
				log.debug("timeperiod Id : {}", timeperiod.getTimeperiodId());

				if (dataValues != null && dataValues.size() != 0) {
					boolean doAllAreasHaveSameValue = dataValues.stream()
							.allMatch(e -> e.getPercentage().compareTo(dataValues.get(0).getPercentage()) == 0);
					boolean doAllAreasHaveHundredForIndicator = dataValues.stream()
							.allMatch(e -> e.getPercentage().compareTo(new Double("100.00")) == 0);
					boolean allAreasHavingZero = dataValues.stream()
							.allMatch(i -> i.getPercentage().compareTo(new Double("0.00")) == 0);

					if (indicator.isHighIsGood()) {
						if (doAllAreasHaveHundredForIndicator) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("1.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else if (doAllAreasHaveSameValue) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("0.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else {
							BigDecimal minimum;
							BigDecimal maximum;
							if (dataValues.size() == 1) {
								minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
							} else {
								log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
								log.debug("DF formatted maximum :{}",
										df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

								minimum = dataValues.size() > 0
										? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
										: new BigDecimal("0.00");
								maximum = dataValues.size() > 0
										? new BigDecimal(
												df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
														.setScale(2)
										: new BigDecimal("0.00");
							}
							for (Data data2 : dataValues) {
								data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						}

					} else {
						if (allAreasHavingZero) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("1.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else if (doAllAreasHaveSameValue) {
							for (Data data2 : dataValues) {
								data2.setNormalizedValue(new BigDecimal("0.00"));
								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						} else {
							BigDecimal minimum;
							BigDecimal maximum;
							if (dataValues.size() == 1) {
								minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
							} else {
								log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
								log.debug("DF formatted maximum :{}",
										df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

								minimum = dataValues.size() > 0
										? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
										: new BigDecimal("0.00");
								maximum = dataValues.size() > 0
										? new BigDecimal(
												df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
														.setScale(2)
										: new BigDecimal("0.00");
							}
							for (Data data2 : dataValues) {
								data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

								if (districtWiseAllIndicatorsNormalizedDataInList
										.get(data2.getArea().getAreaId()) == null) {
									List<Data> indicatorData = new ArrayList<>();
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								} else {
									List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId());
									indicatorData.add(data2);
									districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
											indicatorData);
								}
							}
						}
					}
				}
			}

			/*
			 * 1. FOR EACH DISTRICT AS KEY, WE HAVE NORMALIZED VALUE OF ALL INDICATORS FOR
			 * THE SUBSECTOR AS VALUE 2. FOR EACH DISTRICT ITERATE AND GET AN AVERAGE OF ALL
			 * THE NORMALIZED INDICATORS OF THE SUBSECTOR 3. THE DENOMINATOR IS TAKEN AS
			 * COUNT OF THE REPORTED INDICATORS 4. FIND THE INDEX SUBSECTOR OF THE
			 * EQUIVALENT SUBSECTOR BE USING ITS ID, TO QUERY THE TABLE SECTORIDS COLUMN IN
			 * TABLE 5. AFTER FINDING THE INDEX SUBSECTOR, QUERY INDICATOR TABLE'S
			 * 'INDICATORCLASSIFICATION_ID' COLUMN TO FIND THE EQUIVALENT INDEX INDICATOR
			 * FOR THAT SUBSECTOR. 5. CREATE A NEW DATA POINT OBJECT, AND SET THE AREA AS
			 * DISTRICT ID,INDICATOR AS THE FOUND INDEX-INDICATOR FOR THE SUBSECTOR, AND SET
			 * SOURCE AS COMPUTED INDEX. 6. PUSH THE NEW DATA POINT INTO A LIST NAMED
			 * 'indexDataForAllDistricts' THAT WILL HOLD ALL THE
			 * INDEX-INDICATOR-FOR-SUBSECTOR VALUES OF ALL DISTRICT 7. FINALLY, PUSH THE
			 * LIST INTO A MAP THAT TAKES SUBSECTORID AS KEY AND VALUE AS LIST OF VALUES OF
			 * INDEX-INDICATOR-FOR-SUBSECTOR FOR ALL DISTRICT.
			 */
			Timeperiod tp = timeperiod;
			districtWiseAllIndicatorsNormalizedDataInList.forEach((district, normalizedDataOfAllIndicators) -> {

				BigDecimal avg = new BigDecimal(0.00);
				BigDecimal sum = new BigDecimal(0.00);

				for (Data data : normalizedDataOfAllIndicators) {
					sum = sum.add(data.getNormalizedValue());
					log.debug("indicator Id :{}", data.getIndicator().getIndicatorId());
					log.debug("Data Id :{}", data.getDataId());
					log.debug("Normalized Value :{}", data.getNormalizedValue());
				}

				avg = sum.divide(new BigDecimal(normalizedDataOfAllIndicators.size()), 2, RoundingMode.HALF_UP);

				log.debug("--Sum--:{}", sum);
				log.debug("--Size--:{}", normalizedDataOfAllIndicators.size());
				log.debug("--avg--:{}", avg);

				Area area = areaRepository.findByAreaId(district);

				IndicatorClassification subsectorOfIndex = indicatorClassificationRepository
						.findBySectorIdsAndParentType(String.valueOf(subsector.getIndicatorClassificationId()),
								Type.SUBSECTOR_INDEX);

				Indicator i = (indicatorRepository.findByAgencyAndIndicatorClassificationAndIndicatorType(agency,
						subsectorOfIndex, IndicatorType.INDEX_INDICATOR));

				IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorAndUnitAndSubgroup(i,
						indexUnit, subgroup);

				IndicatorClassification sourceIndexComputed = indicatorClassificationRepository
						.findByType(Type.INDEX_COMPUTED);

				Data avgIndexOfDistrict = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(ius,
						sourceIndexComputed, tp, area);

				if (avgIndexOfDistrict == null) {
					avgIndexOfDistrict = new Data();
					avgIndexOfDistrict.setArea(area);
					avgIndexOfDistrict.setDenominator(0);
					avgIndexOfDistrict.setNumerator(0);
					avgIndexOfDistrict.setSource(sourceIndexComputed);
					avgIndexOfDistrict.setSubgroup(subgroup);
					avgIndexOfDistrict.setTimePeriod(tp);
					avgIndexOfDistrict.setUnit(indexUnit);
					avgIndexOfDistrict.setIndicator(i);
					avgIndexOfDistrict.setIndicatorUnitSubgroup(ius);

					avgIndexOfDistrict.setIndicatorName(i.getIndicatorName());
					avgIndexOfDistrict.setUnitName(indexUnit.getUnitName());
					avgIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
					avgIndexOfDistrict.setSectorName(subsectorOfIndex.getParent().getName());
					avgIndexOfDistrict.setSubsectorName(subsectorOfIndex.getName());
					avgIndexOfDistrict.setSourceName(sourceIndexComputed.getName());
					avgIndexOfDistrict.setAreaName(area.getAreaName());
				}
				avgIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));
				indexDataForAllDistricts.add(avgIndexOfDistrict);
			});
			indexAgainstSubsector.put(subsector.getIndicatorClassificationId(), indexDataForAllDistricts);
		}

		indexAgainstSubsector.forEach((indicatorClassification, indexValues) -> {

//			indexValues.forEach(d->{
//				System.out.println(d);	
//				dataRepository.save(d);
//			});
			dataRepository.save(indexValues);

		});
		return true;

	}

	@Transactional
	public boolean createIndexSourceWise(Agency agency, int yearOfPreviousMonth, int previousMonth) {
		DecimalFormat df = new DecimalFormat("#.##");
		String monthString = "";
		if (previousMonth >= 10) {
			monthString = previousMonth + "";
		} else {
			monthString = "0" + previousMonth;
		}

		Timeperiod timeperiod = timePeriodRepository.findByTimePeriod(yearOfPreviousMonth + "." + monthString);
		if (timeperiod == null) {
			String dayString = "";
			Month m = Month.of(previousMonth);
			Year y = Year.of(yearOfPreviousMonth);
			if (m.length(y.isLeap()) < 10) {
				dayString = "0" + m.length(y.isLeap());
			} else {
				dayString = m.length(y.isLeap()) + "";
			}
			Timeperiod newTimeperiod = new Timeperiod();
			newTimeperiod.setTimePeriod(yearOfPreviousMonth + "." + monthString);
			try {
				newTimeperiod.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-01 00:00:00"));
				newTimeperiod.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-" + dayString + " 23:59:59"));
				newTimeperiod.setAppType(AppType.APP_V_2);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			newTimeperiod.setPeriodicity("1");
			timeperiod = timePeriodRepository.save(newTimeperiod);
		}

		/*
		 * 1. Fetch the source name INDEX_COMPUTED 2. CREATE A MAP , PUT ALL NORMALIZED
		 * DATA OF ALL INDICATORS OF ALL AREA IN A MAP, WITH SECTOR_ID AS ITS KEY
		 */
		Unit indexUnit = unitRepository.findByUnitType(UnitType.INDEX);

		Unit percentageUnit = unitRepository.findByUnitType(UnitType.PERCENTAGE);

		Subgroup subgroup = subgroupRepository.findOne(1);

		// Get all DISTRICT WISE FACILITIES REPRESENTING AS SOURCE.LIKE
		// DCPU,SJPU,JJB,ETC. WE HAVE TO CREATE A COMPUTED_INDEX FOR EACH
		// SECTOR BY SOURCE.
		//

		List<String> ISources = new ArrayList<>();
		ISources.add("DCPU");
		ISources.add("SJPU");
		ISources.add("JJB");
		ISources.add("CWC");
		ISources.add("PO");

		for (String iSource : ISources) {
			/*
			 * 1. Fetch the ALL THE SECTORS, TYPE IS SECTOR AND PARENT'S TYPE IS
			 * SECTOR_INDEX_SOURCEWISE, AND INDEX FOR SOURCE COLUMN IS NAME OF SOURCE. I.E
			 * DCPU,SJPU
			 */

			Map<Integer, List<Data>> indexAgainstSector = new LinkedHashMap<>();

			List<IndicatorClassification> ics = indicatorClassificationRepository
					.findByIndicatorClassificationTypeAndParentTypeAndIndexForSourceAndTypeIsNull(
							IndicatorClassificationType.SC, Type.SECTOR_INDEX_SOURCEWISE, iSource);

			ListIterator<IndicatorClassification> it = ics.listIterator();
			while (it.hasNext()) {

				IndicatorClassification sector = (IndicatorClassification) it.next();

				List<Data> indexDataForAllDistricts = new ArrayList<>();
				/*
				 * 1. Fetch the ALL INDICATORS BELONGING THE SUBSECTOR OF THE SECTOR
				 */
				IndicatorClassification actualsector = indicatorClassificationRepository
						.findOne(Integer.parseInt(sector.getSectorIds()));

				List<Indicator> indicators = indicatorRepository
						.findByClassifierAndIndicatorClassificationInAndIndicatorSource(IndicatorClassfier.PERFORMANCE,
								actualsector.getChildren(), iSource);

				/*
				 * 1. CREATE A MAP WITH DISTRICT ID AS ITS KEY, AND LIST OF SUBMISSIONS FOR THE
				 * ALL THE INDICATORS FOR THE PARTICULAR TIMEPERIOD WITH ITS NORMALIZED VALUES
				 */
				Map<Integer, List<Data>> districtWiseAllIndicatorsNormalizedDataInList = new LinkedHashMap<>();

				ListIterator<Indicator> indicatorIterator = indicators.listIterator();
				while (indicatorIterator.hasNext()) {

					Indicator indicator = indicatorIterator.next();

					IndicatorClassification source = indicatorClassificationRepository
							.findByNameAndParentIsNotNull(indicator.getIndicatorSource());

					List<Data> dataValues = dataRepository
							.findByIndicatorAndUnitAndSubgroupAndSourceAndTimePeriodOrderByPercentageAsc(indicator,
									percentageUnit, subgroup, source, timeperiod);

					log.debug("indicator Id : {}", indicator.getIndicatorId());
					log.debug("unit Id : {}", percentageUnit.getUnitId());
					log.debug("subgroup Id : {}", subgroup.getSubgroupValueId());
					log.debug("source Id : {}", source.getIndicatorClassificationId());
					log.debug("timeperiod Id : {}", timeperiod.getTimeperiodId());

					if (dataValues != null && dataValues.size() != 0) {
						boolean doAllAreasHaveSameValue = dataValues.stream()
								.allMatch(e -> e.getPercentage().compareTo(dataValues.get(0).getPercentage()) == 0);
						boolean doAllAreasHaveHundredForIndicator = dataValues.stream()
								.allMatch(e -> e.getPercentage().compareTo(new Double("100.00")) == 0);
						boolean allAreasHavingZero = dataValues.stream()
								.allMatch(i -> i.getPercentage().compareTo(new Double("0.00")) == 0);

						if (indicator.isHighIsGood()) {
							if (doAllAreasHaveHundredForIndicator) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("1.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else if (doAllAreasHaveSameValue) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("0.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else {
								BigDecimal minimum;
								BigDecimal maximum;
								if (dataValues.size() == 1) {
									minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
									maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								} else {
									log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
									log.debug("DF formatted maximum :{}",
											df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

									minimum = dataValues.size() > 0
											? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
											: new BigDecimal("0.00");
									maximum = dataValues.size() > 0
											? new BigDecimal(
													df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
															.setScale(2)
											: new BigDecimal("0.00");
								}
								for (Data data2 : dataValues) {
									data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							}

						} else {
							if (allAreasHavingZero) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("1.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else if (doAllAreasHaveSameValue) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("0.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else {
								BigDecimal minimum;
								BigDecimal maximum;
								if (dataValues.size() == 1) {
									minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
									maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								} else {
									log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
									log.debug("DF formatted maximum :{}",
											df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

									minimum = dataValues.size() > 0
											? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
											: new BigDecimal("0.00");
									maximum = dataValues.size() > 0
											? new BigDecimal(
													df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
															.setScale(2)
											: new BigDecimal("0.00");
								}
								for (Data data2 : dataValues) {
									data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							}
						}
					}
				}

				/*
				 * 1. FOR EACH DISTRICT AS KEY, WE HAVE NORMALIZED VALUE OF ALL INDICATORS FOR
				 * THE SECTORS AS VALUE 2. FOR EACH DISTRICT ITERATE AND GET AN AVERAGE OF ALL
				 * THE NORMALIZED INDICATORS OF THE SECTOR 3. THE DENOMINATOR IS TAKEN AS COUNT
				 * OF THE REPORTED INDICATORS 4. FIND THE INDEX SECTOR OF THE EQUIVALENT SECTOR
				 * BE USING ITS ID, TO QUERY THE TABLE SECTORIDS COLUMN IN TABLE 5. AFTER
				 * FINDING THE INDEX SECTOR, QUERY INDICATOR TABLE'S
				 * 'INDICATORCLASSIFICATION_ID' COLUMN TO FIND THE EQUIVALENT INDEX INDICATOR
				 * FOR THAT SECTOR. 5. CREATE A NEW DATA POINT OBJECT, AND SET THE AREA AS
				 * DISTRICT ID,INDICATOR AS THE FOUND INDEX-INDICATOR FOR THE SECTOR, AND SET
				 * SOURCE AS COMPUTED INDEX. 6. PUSH THE NEW DATA POINT INTO A LIST NAMED
				 * 'indexDataForAllDistricts' THAT WILL HOLD ALL THE INDEX-INDICATOR-FOR-SECTOR
				 * VALUES OF ALL DISTRICT 7. FINALLY, PUSH THE LIST INTO A MAP THAT TAKES
				 * SECTORID AS KEY AND VALUE AS LIST OF VALUES OF INDEX-INDICATOR-FOR-SECTOR FOR
				 * ALL DISTRICT.
				 */
				Timeperiod tp = timeperiod;
				districtWiseAllIndicatorsNormalizedDataInList.forEach((district, normalizedDataOfAllIndicators) -> {

					BigDecimal avg = new BigDecimal(0.00);
					BigDecimal sum = new BigDecimal(0.00);

					for (Data data : normalizedDataOfAllIndicators) {
						sum = sum.add(data.getNormalizedValue());
						log.debug("indicator Id :{}", data.getIndicator().getIndicatorId());
						log.debug("Data Id :{}", data.getDataId());
						log.debug("Normalized Value :{}", data.getNormalizedValue());
					}

					avg = sum.divide(new BigDecimal(normalizedDataOfAllIndicators.size()), 2, RoundingMode.HALF_UP);

					log.debug("--Sum--:{}", sum);
					log.debug("--Size--:{}", normalizedDataOfAllIndicators.size());
					log.debug("--avg--:{}", avg);

					Area area = areaRepository.findByAreaId(district);

					IndicatorClassification sectorOfSourceIndicator = sector;
					IndicatorClassification sourceIndexComputed = indicatorClassificationRepository
							.findByNameAndParentIsNotNull(iSource);

					Indicator i = (indicatorRepository.findByAgencyAndIndicatorClassificationAndIndicatorType(agency,
							sectorOfSourceIndicator, IndicatorType.INDEX_INDICATOR));

					IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorAndUnitAndSubgroup(i,
							indexUnit, subgroup);

					Data avgIndexOfDistrict = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(
							ius, sourceIndexComputed, tp, area);

					if (avgIndexOfDistrict == null) {
						avgIndexOfDistrict = new Data();
						avgIndexOfDistrict.setArea(area);
						avgIndexOfDistrict.setDenominator(0);
						avgIndexOfDistrict.setNumerator(0);
						avgIndexOfDistrict.setSource(sourceIndexComputed);
						avgIndexOfDistrict.setSubgroup(subgroup);
						avgIndexOfDistrict.setTimePeriod(tp);
						avgIndexOfDistrict.setUnit(indexUnit);
						avgIndexOfDistrict.setIndicator(i);
						avgIndexOfDistrict.setIndicatorUnitSubgroup(ius);

						avgIndexOfDistrict.setIndicatorName(i.getIndicatorName());
						avgIndexOfDistrict.setUnitName(indexUnit.getUnitName());
						avgIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
						avgIndexOfDistrict.setSectorName(i.getIndicatorClassification().getParent().getName());
						avgIndexOfDistrict.setSubsectorName(i.getIndicatorClassification().getName());
						avgIndexOfDistrict.setSourceName(sourceIndexComputed.getName());
						avgIndexOfDistrict.setAreaName(area.getAreaName());

					}

					avgIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));

					indexDataForAllDistricts.add(avgIndexOfDistrict);

				});

				indexAgainstSector.put(sector.getIndicatorClassificationId(), indexDataForAllDistricts);

			}

			// Generating value for Overall Index

			Timeperiod tp = timeperiod;

			Map<Integer, List<Data>> districtOverallMap = new LinkedHashMap<>();

			indexAgainstSector.forEach((indicatorClassification, indexValues) -> {

				for (Data data2 : indexValues) {

					log.debug("indicator id:::::::{}", data2.getIndicator().getIndicatorId());
//				log.debug("ius id :::::::::{}", data2.getIndicatorUnitSubgroup().getIndicatorUnitSubgroupId());
					log.debug("source id:::::::{}", data2.getSource().getIndicatorClassificationId());
					log.debug("timeperiod id:::::::{}", data2.getTimePeriod().getTimePeriod());
					log.debug("area id:::::::{}", data2.getArea().getAreaId());
					// checking data if exists for
					Data d = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(
							data2.getIndicatorUnitSubgroup(), data2.getSource(), data2.getTimePeriod(),
							data2.getArea());

					if (districtOverallMap.get(data2.getArea().getAreaId()) == null) {
						List<Data> indicatorData = new ArrayList<>();
						if (d == null)
							indicatorData.add(data2);
						else
							indicatorData.add(d);
						districtOverallMap.put(data2.getArea().getAreaId(), indicatorData);
					} else {
						List<Data> indicatorData = districtOverallMap.get(data2.getArea().getAreaId());
						if (d == null)
							indicatorData.add(data2);
						else
							indicatorData.add(d);
						districtOverallMap.put(data2.getArea().getAreaId(), indicatorData);
					}
				}

				// for each sector persist index values of overall
				dataRepository.save(indexValues);
			});

			districtOverallMap.forEach((district, calculatedIndexForIndicators) -> {

				log.debug("District :::{}", district);

				BigDecimal avg = new BigDecimal(0.00);
				BigDecimal sum = new BigDecimal(0.00);
				for (Data d : calculatedIndexForIndicators) {
					sum = sum.add(new BigDecimal(d.getPercentage()));
				}

				avg = sum.divide(new BigDecimal(calculatedIndexForIndicators.size()), 2, RoundingMode.HALF_UP);

				IndicatorClassification overallSectorForSource = indicatorClassificationRepository
						.findByTypeAndIndexForSource(Type.OVERALL_BY_SOURCE, iSource);

				Indicator overallIndicator = indicatorRepository.findByIndicatorClassificationAndIndicatorType(
						overallSectorForSource, IndicatorType.INDEX_OVERALL_SOURCEWISE);

				IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository
						.findByIndicatorAndUnitAndSubgroup(overallIndicator, indexUnit, subgroup);

				Area area = areaRepository.findByAreaId(district);

				IndicatorClassification sourceForComputedIndexOfOverall = indicatorClassificationRepository
						.findByNameAndParentIsNotNull(iSource);

				Data overallIndexOfDistrict = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(
						ius, sourceForComputedIndexOfOverall, tp, area);

				if (overallIndexOfDistrict == null) {
					overallIndexOfDistrict = new Data();
					overallIndexOfDistrict.setArea(area);
					overallIndexOfDistrict.setDenominator(0);
					overallIndexOfDistrict.setNumerator(0);
					overallIndexOfDistrict.setSource(sourceForComputedIndexOfOverall);
					overallIndexOfDistrict.setTimePeriod(tp);
					overallIndexOfDistrict.setUnit(indexUnit);
					overallIndexOfDistrict.setSubgroup(subgroup);
					overallIndexOfDistrict.setIndicator(overallIndicator);
					overallIndexOfDistrict.setIndicatorUnitSubgroup(ius);

					overallIndexOfDistrict.setIndicatorName(overallIndicator.getIndicatorName());
					overallIndexOfDistrict.setUnitName(indexUnit.getUnitName());
					overallIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
					overallIndexOfDistrict
							.setSectorName(overallIndicator.getIndicatorClassification().getParent().getName());
					overallIndexOfDistrict.setSubsectorName(overallIndicator.getIndicatorClassification().getName());
					overallIndexOfDistrict.setSourceName(sourceForComputedIndexOfOverall.getName());
					overallIndexOfDistrict.setAreaName(area.getAreaName());

				}
				overallIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));
				dataRepository.save(overallIndexOfDistrict);

			});
		}
		return true;

	}

	public boolean generateIndexOfSectorBySource(Agency agency, int yearOfPreviousMonth, int previousMonth) {

		DecimalFormat df = new DecimalFormat("#.##");
		String monthString = "";
		if (previousMonth >= 10) {
			monthString = previousMonth + "";
		} else {
			monthString = "0" + previousMonth;
		}

		Timeperiod timeperiod = timePeriodRepository.findByTimePeriod(yearOfPreviousMonth + "." + monthString);
		if (timeperiod == null) {
			String dayString = "";
			Month m = Month.of(previousMonth);
			Year y = Year.of(yearOfPreviousMonth);
			if (m.length(y.isLeap()) < 10) {
				dayString = "0" + m.length(y.isLeap());
			} else {
				dayString = m.length(y.isLeap()) + "";
			}
			Timeperiod newTimeperiod = new Timeperiod();
			newTimeperiod.setTimePeriod(yearOfPreviousMonth + "." + monthString);
			try {
				newTimeperiod.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-01 00:00:00"));
				newTimeperiod.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-" + dayString + " 23:59:59"));
				newTimeperiod.setAppType(AppType.APP_V_2);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			newTimeperiod.setPeriodicity("1");
			timeperiod = timePeriodRepository.save(newTimeperiod);
		}

		/*
		 * 1. Fetch the source name INDEX_COMPUTED 2. CREATE A MAP , PUT ALL NORMALIZED
		 * DATA OF ALL INDICATORS OF ALL AREA IN A MAP, WITH SECTOR_ID AS ITS KEY
		 */
		Unit indexUnit = unitRepository.findByUnitType(UnitType.INDEX);

		Unit percentageUnit = unitRepository.findByUnitType(UnitType.PERCENTAGE);

		Subgroup subgroup = subgroupRepository.findOne(1);

		List<String> ISources = new ArrayList<>();
		ISources.add("DCPU");
		ISources.add("SJPU");
		ISources.add("JJB");
		ISources.add("CWC");
		ISources.add("PO");

		for (String iSource : ISources) {
			/*
			 * 1. Fetch the ALL THE SECTORS, WHOSE PARENT ID IS NULL AND INDEX COLUMN IN DB
			 * IS FALSE
			 */
			Map<Integer, List<Data>> indexAgainstSector = new LinkedHashMap<>();

			IndicatorClassification source = indicatorClassificationRepository.findByNameAndParentIsNotNull(iSource);

			List<IndicatorClassification> ics = indicatorClassificationRepository
					.findByIndicatorClassificationTypeAndParentIsNullAndIndexIsFalse(IndicatorClassificationType.SC);

			ListIterator<IndicatorClassification> it = ics.listIterator();
			while (it.hasNext()) {

				IndicatorClassification sector = (IndicatorClassification) it.next();

				List<Data> indexDataForAllDistricts = new ArrayList<>();
				/*
				 * 1. Fetch the ALL INDICATORS BELONGING THE SUBSECTOR OF THE SECTOR
				 */
				List<Indicator> indicators = indicatorRepository
						.findByClassifierAndIndicatorClassificationInAndIndicatorSource(IndicatorClassfier.PERFORMANCE,
								sector.getChildren(), iSource);

//				
				/*
				 * 1. CREATE A MAP WITH DISTRICT ID AS ITS KEY, AND LIST OF SUBMISSIONS FOR THE
				 * ALL THE INDICATORS FOR THE PARTICULAR TIMEPERIOD WITH ITS NORMALIZED VALUES
				 */
				Map<Integer, List<Data>> districtWiseAllIndicatorsNormalizedDataInList = new LinkedHashMap<>();

				ListIterator<Indicator> indicatorIterator = indicators.listIterator();
				while (indicatorIterator.hasNext()) {

					Indicator indicator = indicatorIterator.next();

//					IndicatorClassification source = indicatorClassificationRepository.findByNameAndParentIsNotNull(indicator.getIndicatorSource());

					List<Data> dataValues = dataRepository
							.findByIndicatorAndUnitAndSubgroupAndSourceAndTimePeriodOrderByPercentageAsc(indicator,
									percentageUnit, subgroup, source, timeperiod);

					log.debug("indicator Id : {}", indicator.getIndicatorId());
					log.debug("unit Id : {}", percentageUnit.getUnitId());
					log.debug("subgroup Id : {}", subgroup.getSubgroupValueId());
					log.debug("source Id : {}", source.getIndicatorClassificationId());
					log.debug("timeperiod Id : {}", timeperiod.getTimeperiodId());

					if (dataValues != null && dataValues.size() != 0) {
						boolean doAllAreasHaveSameValue = dataValues.stream()
								.allMatch(e -> e.getPercentage().compareTo(dataValues.get(0).getPercentage()) == 0);
						boolean doAllAreasHaveHundredForIndicator = dataValues.stream()
								.allMatch(e -> e.getPercentage().compareTo(new Double("100.00")) == 0);
						boolean allAreasHavingZero = dataValues.stream()
								.allMatch(i -> i.getPercentage().compareTo(new Double("0.00")) == 0);

						if (indicator.isHighIsGood()) {
							if (doAllAreasHaveHundredForIndicator) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("1.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else if (doAllAreasHaveSameValue) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("0.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else {
								BigDecimal minimum;
								BigDecimal maximum;
								if (dataValues.size() == 1) {
									minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
									maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								} else {
									log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
									log.debug("DF formatted maximum :{}",
											df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

									minimum = dataValues.size() > 0
											? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
											: new BigDecimal("0.00");
									maximum = dataValues.size() > 0
											? new BigDecimal(
													df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
															.setScale(2)
											: new BigDecimal("0.00");
								}
								for (Data data2 : dataValues) {
									data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							}

						} else {
							if (allAreasHavingZero) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("1.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else if (doAllAreasHaveSameValue) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("0.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else {
								BigDecimal minimum;
								BigDecimal maximum;
								if (dataValues.size() == 1) {
									minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
									maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								} else {
									log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
									log.debug("DF formatted maximum :{}",
											df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

									minimum = dataValues.size() > 0
											? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
											: new BigDecimal("0.00");
									maximum = dataValues.size() > 0
											? new BigDecimal(
													df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
															.setScale(2)
											: new BigDecimal("0.00");
								}
								for (Data data2 : dataValues) {
									data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							}
						}
					}
				}

				/*
				 * 1. FOR EACH DISTRICT AS KEY, WE HAVE NORMALIZED VALUE OF ALL INDICATORS FOR
				 * THE SECTORS AS VALUE 2. FOR EACH DISTRICT ITERATE AND GET AN AVERAGE OF ALL
				 * THE NORMALIZED INDICATORS OF THE SECTOR 3. THE DENOMINATOR IS TAKEN AS COUNT
				 * OF THE REPORTED INDICATORS 4. FIND THE INDEX SECTOR OF THE EQUIVALENT SECTOR
				 * BE USING ITS ID, TO QUERY THE TABLE SECTORIDS COLUMN IN TABLE 5. AFTER
				 * FINDING THE INDEX SECTOR, QUERY INDICATOR TABLE'S
				 * 'INDICATORCLASSIFICATION_ID' COLUMN TO FIND THE EQUIVALENT INDEX INDICATOR
				 * FOR THAT SECTOR. 5. CREATE A NEW DATA POINT OBJECT, AND SET THE AREA AS
				 * DISTRICT ID,INDICATOR AS THE FOUND INDEX-INDICATOR FOR THE SECTOR, AND SET
				 * SOURCE AS COMPUTED INDEX. 6. PUSH THE NEW DATA POINT INTO A LIST NAMED
				 * 'indexDataForAllDistricts' THAT WILL HOLD ALL THE INDEX-INDICATOR-FOR-SECTOR
				 * VALUES OF ALL DISTRICT 7. FINALLY, PUSH THE LIST INTO A MAP THAT TAKES
				 * SECTORID AS KEY AND VALUE AS LIST OF VALUES OF INDEX-INDICATOR-FOR-SECTOR FOR
				 * ALL DISTRICT.
				 */
				Timeperiod tp = timeperiod;
				districtWiseAllIndicatorsNormalizedDataInList.forEach((district, normalizedDataOfAllIndicators) -> {

					BigDecimal avg = new BigDecimal(0.00);
					BigDecimal sum = new BigDecimal(0.00);

					for (Data data : normalizedDataOfAllIndicators) {
						sum = sum.add(data.getNormalizedValue());
						log.debug("indicator Id :{}", data.getIndicator().getIndicatorId());
						log.debug("Data Id :{}", data.getDataId());
						log.debug("Normalized Value :{}", data.getNormalizedValue());
					}

					avg = sum.divide(new BigDecimal(normalizedDataOfAllIndicators.size()), 2, RoundingMode.HALF_UP);

					log.debug("--Sum--:{}", sum);
					log.debug("--Size--:{}", normalizedDataOfAllIndicators.size());
					log.debug("--avg--:{}", avg);

					Area area = areaRepository.findByAreaId(district);

					IndicatorClassification subsectorOfIndex = indicatorClassificationRepository
							.findBySectorIdsAndParentType(String.valueOf(sector.getIndicatorClassificationId()),
									Type.INDEX);

					Indicator i = (indicatorRepository.findByAgencyAndIndicatorClassificationAndIndicatorType(agency,
							subsectorOfIndex, IndicatorType.INDEX_INDICATOR));

					IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorAndUnitAndSubgroup(i,
							indexUnit, subgroup);

					Data avgIndexOfDistrict = dataRepository
							.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(ius, source, tp, area);

					if (avgIndexOfDistrict == null) {
						avgIndexOfDistrict = new Data();
						avgIndexOfDistrict.setArea(area);
						avgIndexOfDistrict.setDenominator(0);
						avgIndexOfDistrict.setNumerator(0);
						avgIndexOfDistrict.setSource(source);
						avgIndexOfDistrict.setSubgroup(subgroup);
						avgIndexOfDistrict.setTimePeriod(tp);
						avgIndexOfDistrict.setUnit(indexUnit);
						avgIndexOfDistrict.setIndicator(i);
						avgIndexOfDistrict.setIndicatorUnitSubgroup(ius);

						avgIndexOfDistrict.setIndicatorName(i.getIndicatorName());
						avgIndexOfDistrict.setUnitName(indexUnit.getUnitName());
						avgIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
						avgIndexOfDistrict.setSectorName(subsectorOfIndex.getParent().getName());
						avgIndexOfDistrict.setSubsectorName(subsectorOfIndex.getName());

						avgIndexOfDistrict.setSourceName(source.getName());
						avgIndexOfDistrict.setAreaName(area.getAreaName());

					}

					avgIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));

					indexDataForAllDistricts.add(avgIndexOfDistrict);

				});

				indexAgainstSector.put(sector.getIndicatorClassificationId(), indexDataForAllDistricts);

			}

			// Generating value for Overall Index

			Timeperiod tp = timeperiod;

			Map<Integer, List<Data>> districtOverallMap = new LinkedHashMap<>();

			indexAgainstSector.forEach((indicatorClassification, indexValues) -> {

				for (Data data2 : indexValues) {

					log.debug("indicator id:::::::{}", data2.getIndicator().getIndicatorId());
					log.debug("ius id :::::::::{}", data2.getIndicatorUnitSubgroup().getIndicatorUnitSubgroupId());
					log.debug("source id:::::::{}", data2.getSource().getIndicatorClassificationId());
					log.debug("timeperiod id:::::::{}", data2.getTimePeriod().getTimePeriod());
					log.debug("area id:::::::{}", data2.getArea().getAreaId());
					// checking data if exists for
					Data d = dataRepository.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(
							data2.getIndicatorUnitSubgroup(), data2.getSource(), data2.getTimePeriod(),
							data2.getArea());

					if (districtOverallMap.get(data2.getArea().getAreaId()) == null) {
						List<Data> indicatorData = new ArrayList<>();
						if (d == null)
							indicatorData.add(data2);
						else
							indicatorData.add(d);
						districtOverallMap.put(data2.getArea().getAreaId(), indicatorData);
					} else {
						List<Data> indicatorData = districtOverallMap.get(data2.getArea().getAreaId());
						if (d == null)
							indicatorData.add(data2);
						else
							indicatorData.add(d);
						districtOverallMap.put(data2.getArea().getAreaId(), indicatorData);
					}
				}

				// for each sector persist index values of overall

				dataRepository.save(indexValues);
			});

			districtOverallMap.forEach((district, calculatedIndexForIndicators) -> {

				log.debug("District :::{}", district);

				BigDecimal avg = new BigDecimal(0.00);
				BigDecimal sum = new BigDecimal(0.00);
				for (Data d : calculatedIndexForIndicators) {
					sum = sum.add(new BigDecimal(d.getPercentage()));
				}

				avg = sum.divide(new BigDecimal(calculatedIndexForIndicators.size()), 2, RoundingMode.HALF_UP);

				Indicator overallIndicator = indicatorRepository
						.findAllByAgencyAndIndicatorType(agency, IndicatorType.OVERALL).get(0);

				IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository
						.findByIndicatorAndUnitAndSubgroup(overallIndicator, indexUnit, subgroup);

				Area area = areaRepository.findByAreaId(district);

				Data overallIndexOfDistrict = dataRepository
						.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(ius, source, tp, area);

				if (overallIndexOfDistrict == null) {
					overallIndexOfDistrict = new Data();
					overallIndexOfDistrict.setArea(area);
					overallIndexOfDistrict.setDenominator(0);
					overallIndexOfDistrict.setNumerator(0);
					overallIndexOfDistrict.setSource(source);
					overallIndexOfDistrict.setTimePeriod(tp);
					overallIndexOfDistrict.setUnit(indexUnit);
					overallIndexOfDistrict.setSubgroup(subgroup);
					overallIndexOfDistrict.setIndicator(overallIndicator);
					overallIndexOfDistrict.setIndicatorUnitSubgroup(ius);

					overallIndexOfDistrict.setIndicatorName(overallIndicator.getIndicatorName());
					overallIndexOfDistrict.setUnitName(indexUnit.getUnitName());
					overallIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
					overallIndexOfDistrict
							.setSectorName(overallIndicator.getIndicatorClassification().getParent().getName());
					overallIndexOfDistrict.setSubsectorName(overallIndicator.getIndicatorClassification().getName());

					overallIndexOfDistrict.setSourceName(source.getName());
					overallIndexOfDistrict.setAreaName(area.getAreaName());

				}
				overallIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));
				dataRepository.save(overallIndexOfDistrict);

			});
		}

		// calculate state level index
		return true;

	}

	@Transactional
	public boolean generateIndexOfSubsectorsBySource(Agency agency, int yearOfPreviousMonth, int previousMonth) {
		DecimalFormat df = new DecimalFormat("#.##");
		String monthString = "";
		if (previousMonth >= 10) {
			monthString = previousMonth + "";
		} else {
			monthString = "0" + previousMonth;
		}

		Timeperiod timeperiod = timePeriodRepository.findByTimePeriod(yearOfPreviousMonth + "." + monthString);
		if (timeperiod == null) {
			String dayString = "";
			Month m = Month.of(previousMonth);
			Year y = Year.of(yearOfPreviousMonth);
			if (m.length(y.isLeap()) < 10) {
				dayString = "0" + m.length(y.isLeap());
			} else {
				dayString = m.length(y.isLeap()) + "";
			}
			Timeperiod newTimeperiod = new Timeperiod();
			newTimeperiod.setTimePeriod(yearOfPreviousMonth + "." + monthString);
			try {
				newTimeperiod.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-01 00:00:00"));
				newTimeperiod.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-" + dayString + " 23:59:59"));
				newTimeperiod.setAppType(AppType.APP_V_2);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			newTimeperiod.setPeriodicity("1");
			timeperiod = timePeriodRepository.save(newTimeperiod);
		}

		/*
		 * 1. Fetch the source name INDEX_COMPUTED 2. CREATE A MAP , PUT ALL NORMALIZED
		 * DATA OF ALL INDICATORS OF ALL AREA IN A MAP, WITH SECTOR_ID AS ITS KEY
		 */
		Unit indexUnit = unitRepository.findByUnitType(UnitType.INDEX);

		Unit percentageUnit = unitRepository.findByUnitType(UnitType.PERCENTAGE);

		Subgroup subgroup = subgroupRepository.findOne(1);
		/*
		 * 1. Fetch the ALL THE SECTORS, WHOSE PARENT ID IS NULL AND INDEX COLUMN IN DB
		 * IS FALSE
		 */

		// GET LIST OF SUBSECTORS
		List<String> ISources = new ArrayList<>();
		ISources.add("DCPU");
		ISources.add("SJPU");
		ISources.add("JJB");
		ISources.add("CWC");
		ISources.add("PO");

		for (String iSource : ISources) {
			/*
			 * 1. Fetch the ALL THE SECTORS, WHOSE PARENT ID IS NULL AND INDEX COLUMN IN DB
			 * IS FALSE
			 */
			Map<Integer, List<Data>> indexAgainstSubsector = new LinkedHashMap<>();

			IndicatorClassification source = indicatorClassificationRepository.findByNameAndParentIsNotNull(iSource);

			List<IndicatorClassification> subsectors = indicatorClassificationRepository
					.findByIndicatorClassificationTypeAndParentType(IndicatorClassificationType.SC,
							Type.SUBSECTOR_INDEX);

			ListIterator<IndicatorClassification> it = subsectors.listIterator();
			while (it.hasNext()) {

				IndicatorClassification subsectorOfSource = (IndicatorClassification) it.next();

				IndicatorClassification subsector = indicatorClassificationRepository
						.findOne(Integer.parseInt(subsectorOfSource.getSectorIds()));

				List<Data> indexDataForAllDistricts = new ArrayList<>();
				/*
				 * 1. Fetch the ALL INDICATORS BELONGING THE SUBSECTOR OF THE SECTOR
				 */

				List<Indicator> indicators = indicatorRepository
						.findByClassifierAndIndicatorClassificationInAndIndicatorSource(IndicatorClassfier.PERFORMANCE,
								subsector, iSource);
				/*
				 * 2. CREATE A MAP WITH DISTRICT ID AS ITS KEY, AND LIST OF SUBMISSIONS FOR THE
				 * ALL THE INDICATORS FOR THE PARTICULAR TIMEPERIOD WITH ITS NORMALIZED VALUES
				 */
				Map<Integer, List<Data>> districtWiseAllIndicatorsNormalizedDataInList = new LinkedHashMap<>();

				ListIterator<Indicator> indicatorIterator = indicators.listIterator();
				while (indicatorIterator.hasNext()) {

					Indicator indicator = indicatorIterator.next();

					List<Data> dataValues = dataRepository
							.findByIndicatorAndUnitAndSubgroupAndSourceAndTimePeriodOrderByPercentageAsc(indicator,
									percentageUnit, subgroup, source, timeperiod);

					log.debug("indicator Id : {}", indicator.getIndicatorId());
					log.debug("unit Id : {}", percentageUnit.getUnitId());
					log.debug("subgroup Id : {}", subgroup.getSubgroupValueId());
					log.debug("source Id : {}", source.getIndicatorClassificationId());
					log.debug("timeperiod Id : {}", timeperiod.getTimeperiodId());

					if (dataValues != null && dataValues.size() != 0) {
						boolean doAllAreasHaveSameValue = dataValues.stream()
								.allMatch(e -> e.getPercentage().compareTo(dataValues.get(0).getPercentage()) == 0);
						boolean doAllAreasHaveHundredForIndicator = dataValues.stream()
								.allMatch(e -> e.getPercentage().compareTo(new Double("100.00")) == 0);
						boolean allAreasHavingZero = dataValues.stream()
								.allMatch(i -> i.getPercentage().compareTo(new Double("0.00")) == 0);

						if (indicator.isHighIsGood()) {
							if (doAllAreasHaveHundredForIndicator) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("1.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else if (doAllAreasHaveSameValue) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("0.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else {
								BigDecimal minimum;
								BigDecimal maximum;
								if (dataValues.size() == 1) {
									minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
									maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								} else {
									log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
									log.debug("DF formatted maximum :{}",
											df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

									minimum = dataValues.size() > 0
											? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
											: new BigDecimal("0.00");
									maximum = dataValues.size() > 0
											? new BigDecimal(
													df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
															.setScale(2)
											: new BigDecimal("0.00");
								}
								for (Data data2 : dataValues) {
									data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							}

						} else {
							if (allAreasHavingZero) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("1.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else if (doAllAreasHaveSameValue) {
								for (Data data2 : dataValues) {
									data2.setNormalizedValue(new BigDecimal("0.00"));
									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							} else {
								BigDecimal minimum;
								BigDecimal maximum;
								if (dataValues.size() == 1) {
									minimum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
									maximum = new BigDecimal(df.format(dataValues.get(0).getPercentage()));
								} else {
									log.debug("DF formatted minimum :{}", df.format(dataValues.get(0).getPercentage()));
									log.debug("DF formatted maximum :{}",
											df.format(dataValues.get(dataValues.size() - 1).getPercentage()));

									minimum = dataValues.size() > 0
											? new BigDecimal(df.format(dataValues.get(0).getPercentage())).setScale(2)
											: new BigDecimal("0.00");
									maximum = dataValues.size() > 0
											? new BigDecimal(
													df.format(dataValues.get(dataValues.size() - 1).getPercentage()))
															.setScale(2)
											: new BigDecimal("0.00");
								}
								for (Data data2 : dataValues) {
									data2.normalizeValue(maximum, minimum, indicator.isHighIsGood());

									if (districtWiseAllIndicatorsNormalizedDataInList
											.get(data2.getArea().getAreaId()) == null) {
										List<Data> indicatorData = new ArrayList<>();
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									} else {
										List<Data> indicatorData = districtWiseAllIndicatorsNormalizedDataInList
												.get(data2.getArea().getAreaId());
										indicatorData.add(data2);
										districtWiseAllIndicatorsNormalizedDataInList.put(data2.getArea().getAreaId(),
												indicatorData);
									}
								}
							}
						}
					}
				}

				/*
				 * 1. FOR EACH DISTRICT AS KEY, WE HAVE NORMALIZED VALUE OF ALL INDICATORS FOR
				 * THE SUBSECTOR AS VALUE 2. FOR EACH DISTRICT ITERATE AND GET AN AVERAGE OF ALL
				 * THE NORMALIZED INDICATORS OF THE SUBSECTOR 3. THE DENOMINATOR IS TAKEN AS
				 * COUNT OF THE REPORTED INDICATORS 4. FIND THE INDEX SUBSECTOR OF THE
				 * EQUIVALENT SUBSECTOR BE USING ITS ID, TO QUERY THE TABLE SECTORIDS COLUMN IN
				 * TABLE 5. AFTER FINDING THE INDEX SUBSECTOR, QUERY INDICATOR TABLE'S
				 * 'INDICATORCLASSIFICATION_ID' COLUMN TO FIND THE EQUIVALENT INDEX INDICATOR
				 * FOR THAT SUBSECTOR. 5. CREATE A NEW DATA POINT OBJECT, AND SET THE AREA AS
				 * DISTRICT ID,INDICATOR AS THE FOUND INDEX-INDICATOR FOR THE SUBSECTOR, AND SET
				 * SOURCE AS COMPUTED INDEX. 6. PUSH THE NEW DATA POINT INTO A LIST NAMED
				 * 'indexDataForAllDistricts' THAT WILL HOLD ALL THE
				 * INDEX-INDICATOR-FOR-SUBSECTOR VALUES OF ALL DISTRICT 7. FINALLY, PUSH THE
				 * LIST INTO A MAP THAT TAKES SUBSECTORID AS KEY AND VALUE AS LIST OF VALUES OF
				 * INDEX-INDICATOR-FOR-SUBSECTOR FOR ALL DISTRICT.
				 */
				Timeperiod tp = timeperiod;
				districtWiseAllIndicatorsNormalizedDataInList.forEach((district, normalizedDataOfAllIndicators) -> {

					BigDecimal avg = new BigDecimal(0.00);
					BigDecimal sum = new BigDecimal(0.00);

					for (Data data : normalizedDataOfAllIndicators) {
						sum = sum.add(data.getNormalizedValue());
						log.debug("indicator Id :{}", data.getIndicator().getIndicatorId());
						log.debug("Data Id :{}", data.getDataId());
						log.debug("Normalized Value :{}", data.getNormalizedValue());
					}

					avg = sum.divide(new BigDecimal(normalizedDataOfAllIndicators.size()), 2, RoundingMode.HALF_UP);

					log.debug("--Sum--:{}", sum);
					log.debug("--Size--:{}", normalizedDataOfAllIndicators.size());
					log.debug("--avg--:{}", avg);

					Area area = areaRepository.findByAreaId(district);

					IndicatorClassification subsectorOfIndex = indicatorClassificationRepository
							.findBySectorIdsAndParentType(String.valueOf(subsector.getIndicatorClassificationId()),
									Type.SUBSECTOR_INDEX);

					Indicator i = (indicatorRepository.findByAgencyAndIndicatorClassificationAndIndicatorType(agency,
							subsectorOfIndex, IndicatorType.INDEX_INDICATOR));

					IndicatorUnitSubgroup ius = indicatorUnitSubgroupRepository.findByIndicatorAndUnitAndSubgroup(i,
							indexUnit, subgroup);

					Data avgIndexOfDistrict = dataRepository
							.findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(ius, source, tp, area);

					if (avgIndexOfDistrict == null) {
						avgIndexOfDistrict = new Data();
						avgIndexOfDistrict.setArea(area);
						avgIndexOfDistrict.setDenominator(0);
						avgIndexOfDistrict.setNumerator(0);
						avgIndexOfDistrict.setSource(source);
						avgIndexOfDistrict.setSubgroup(subgroup);
						avgIndexOfDistrict.setTimePeriod(tp);
						avgIndexOfDistrict.setUnit(indexUnit);
						avgIndexOfDistrict.setIndicator(i);
						avgIndexOfDistrict.setIndicatorUnitSubgroup(ius);

						avgIndexOfDistrict.setIndicatorName(i.getIndicatorName());
						avgIndexOfDistrict.setUnitName(indexUnit.getUnitName());
						avgIndexOfDistrict.setSubgroupName(subgroup.getSubgroupVal());
						avgIndexOfDistrict.setSectorName(subsectorOfIndex.getParent().getName());
						avgIndexOfDistrict.setSubsectorName(subsectorOfIndex.getName());
						avgIndexOfDistrict.setSourceName(source.getName());
						avgIndexOfDistrict.setAreaName(area.getAreaName());
					}
					avgIndexOfDistrict.setPercentage(Double.valueOf(df.format(avg)));
					indexDataForAllDistricts.add(avgIndexOfDistrict);
				});
				indexAgainstSubsector.put(subsector.getIndicatorClassificationId(), indexDataForAllDistricts);
			}
			indexAgainstSubsector.forEach((indicatorClassification, indexValues) -> {
				indexValues.forEach(d -> {
					dataRepository.save(d);
				});

			});
		}
		return true;

	}

	@Transactional
	public boolean createStateLevelIndexOfIndicators(Agency agency, int yearOfPreviousMonth, int previousMonth) {
		DecimalFormat df = new DecimalFormat("#.##");
		String monthString = "";
		if (previousMonth >= 10) {
			monthString = previousMonth + "";
		} else {
			monthString = "0" + previousMonth;
		}

		Timeperiod timeperiod = timePeriodRepository.findByTimePeriod(yearOfPreviousMonth + "." + monthString);
		if (timeperiod == null) {
			String dayString = "";
			Month m = Month.of(previousMonth);
			Year y = Year.of(yearOfPreviousMonth);
			if (m.length(y.isLeap()) < 10) {
				dayString = "0" + m.length(y.isLeap());
			} else {
				dayString = m.length(y.isLeap()) + "";
			}
			Timeperiod newTimeperiod = new Timeperiod();
			newTimeperiod.setTimePeriod(yearOfPreviousMonth + "." + monthString);
			try {
				newTimeperiod.setStartDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-01 00:00:00"));
				newTimeperiod.setEndDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(yearOfPreviousMonth + "-" + monthString + "-" + dayString + " 23:59:59"));
				newTimeperiod.setAppType(AppType.APP_V_2);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			newTimeperiod.setPeriodicity("1");
			timeperiod = timePeriodRepository.save(newTimeperiod);
		}
		// calculate state level index
		List<String> ISources = new ArrayList<>();
		ISources.add("DCPU");
		ISources.add("SJPU");
		ISources.add("JJB");
		ISources.add("CWC");
		ISources.add("PO");
		ISources.add("COMPUTED_INDEX");
		// 1. GET ALL THE INDICATORS FOR INDEX_INDICATOR
		// 2. DO A AVG AND PERSIST IT AS STATE VALUE.

		List<Indicator> indicators = indicatorRepository
				.findAllByAgencyAndIndicatorTypeAndIndicatorSourceIsNullOrIndicatorType(agency,
						IndicatorType.INDEX_INDICATOR, IndicatorType.OVERALL);

		Area state = areaRepository.findByAreaLevelAreaLevelName("STATE");
		// 2. GET ALL DATA, FROM ALL SOURCE WHERE SOURCE IS COMPUTED INDEX, AND ALL
		// OTHER SOURCES LIKE DCPU,JJB,CWC,SJPU ,ETC

		List<Data> stateLevelData = new ArrayList<>();
		Subgroup subgroup = subgroupRepository.findBySubgroupValueId(1);
		for (Indicator indicator : indicators) {
			for (String isource : ISources) {
				IndicatorClassification source = indicatorClassificationRepository
						.findByNameAndParentIsNotNull(isource);
				List<Data> districtsData = dataRepository.findByIndicatorAndSourceAndTimePeriod(indicator, source,
						timeperiod);
				if (districtsData.size() > 0) {
					OptionalDouble sum = districtsData.stream().mapToDouble(i -> i.getPercentage()).average();
					Data statesData = dataRepository.findByIndicatorAndSourceAndTimePeriodAndArea(indicator, source,
							timeperiod, state);
					if (statesData == null) {
						statesData = new Data();
						statesData.setArea(state);
						statesData.setDenominator(0);
						statesData.setNumerator(0);
						statesData.setSource(source);
						statesData.setSubgroup(subgroup);
						statesData.setTimePeriod(districtsData.get(0).getTimePeriod());
						statesData.setUnit(districtsData.get(0).getUnit());
						statesData.setIndicator(districtsData.get(0).getIndicator());
						statesData.setIndicatorUnitSubgroup(districtsData.get(0).getIndicatorUnitSubgroup());
						statesData.setIndicatorName(districtsData.get(0).getIndicatorName());
						statesData.setUnitName(districtsData.get(0).getUnitName());
						statesData.setSubgroupName(subgroup.getSubgroupVal());
						statesData.setSectorName(districtsData.get(0).getSectorName());
						statesData.setSubsectorName(districtsData.get(0).getSubsectorName());
						statesData.setSourceName(source.getName());
						statesData.setAreaName(state.getAreaName());
					}
					statesData.setPercentage(Double.valueOf(df.format(sum.getAsDouble())));
					stateLevelData.add(statesData);
				}
			}
		}
		stateLevelData.forEach(d -> {
			dataRepository.save(d);
		});
		return true;
	}

	@Override
	@Transactional
	public boolean startPublishingForCurrentMonth() {

		List<Agency> agencies = agencyRepository.findAll();

		for (Agency agency : agencies) {

			int day = 0;
			int year = 0;
			int month = 0;

			LocalDateTime now = LocalDateTime.now();
			day = now.getDayOfMonth();
			year = now.getYear();
			month = now.getMonthValue();

			if (month == 1) {
				month = 12;
				year = year - 1;
			} else {
				month = month - 1;
			}
			String monthString = "";
			if (month >= 10) {
				monthString = month + "";
			} else {
				monthString = "0" + month;
			}

			// auto publish
			if (agency.getAutoPublishOnDay() == day) {
				dashboardService.publishData(agency.getAgencyId(), year, month, monthString);
			}
		}

		return true;
	}
}
