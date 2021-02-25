package in.co.sdrc.scpstn.models;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.domain.Submission;
import in.co.sdrc.scpstn.domain.Timeperiod;
import in.co.sdrc.scpstn.repository.AreaRepository;
import in.co.sdrc.scpstn.repository.FacilityRepository;
import in.co.sdrc.scpstn.repository.SubmissionRepository;
import in.co.sdrc.scpstn.repository.TimePeriodRepository;
import in.co.sdrc.scpstn.service.QuickStatsService;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;

@Service
@Transactional(readOnly = true)
public class QuickStatsServiceImpl implements QuickStatsService {

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	SubmissionRepository submissionRepository;

	@Autowired
	FacilityRepository facilityRepository;

	@Autowired(required = false)
	private QuestionRepository questionRepository;

	@Autowired
	private TimePeriodRepository timePeriodRepository;

	@Override
	public QuickStatPageVO getQuickStats() {

		List<Question> questionList = questionRepository.findAllByActiveTrueOrderByQuestionOrderAsc();

		Map<String, Question> questionMap = questionList.stream()
				.collect(Collectors.toMap(Question::getColumnName, question -> question));

		QuickStatPageVO qvo = new QuickStatPageVO();

		LocalDate today = new LocalDate();

//		Timeperiod tp = timePeriodRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(today.toDate(),today.toDate());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());

		int month = (calendar.get(Calendar.MONTH) + 1);

		String monthString = month + "";
		if (month < 10) {
			monthString = "0" + month;
		}

		Date startDate = null, endDate = null;

		try {
			startDate = sdf.parse(calendar.get(Calendar.YEAR) + "-" + monthString + "-"
					+ calendar.getActualMinimum(Calendar.DAY_OF_MONTH) + " 00:00:00");

			endDate = sdf.parse(calendar.get(Calendar.YEAR) + "-" + monthString + "-"
					+ calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + " 23:59:59");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Submission> submissions = submissionRepository
				.findByFormStatusAndFacilityAfmAccountLockedIsFalseAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(FormStatus.FINALIZED,startDate,
						endDate);

		Map<Integer, Submission> submissionMap = submissions.stream()
				.collect(Collectors.toMap(txt -> txt.getFacility().getFacilityId(), tt -> tt));
		
		
		Integer NUMBER_OF_CICL_SUBMITTED_DATA = 0;
		Integer NUMBER_OF_CNCP_SUBMITTED_DATA = 0;
		List<String> tableHeading = new ArrayList<>();
		List<Map<String, Object>> tableData = new ArrayList<>();
		List<Area> districts = areaRepository.findAllByParentAreaId(2);
		List<QuickStartVO> quickStats = new ArrayList<>();
		Integer CICL_HOMES = 0, CNCP_HOMES = 0, CICL_SANCTIONED_STRENGTH = 0, CNCP_SANCTIONED_STRENGTH = 0,
				CICL_CURRENT_STRENGTH = 0, CNCP_CURRENT_STRENGTH = 0;

		tableHeading.add("Name of CCI");
		tableHeading.add("Registration No");
		tableHeading.add("CCI Type");
		tableHeading.add("Address");
		tableHeading.add("Contact No");
		tableHeading.add("* Sanctioned Strength");
		tableHeading.add("* Current Strength");
		tableHeading.add("* Number of staff");
//		tableHeading.add("Status");

		// Get List of CCI's
		List<Facility> facilities = facilityRepository.findByIsActiveIsTrueAndFacilityTypeIsNotNull().stream()
				.filter(facility -> !(facility.getAfm().size() > 0 && facility.getAfm().get(0).getAccount().isLocked()))
				.collect(Collectors.toList());

		for (Facility facility : facilities) {
			Map<String, Object> facilityVO = new LinkedHashMap<>();

			facilityVO.put("Name of CCI", facility.getName().replaceAll("\n", ""));
			facilityVO.put("Registration No",
					facility.getRegistrationNo() != null ? facility.getRegistrationNo().replaceAll("\n", "") : "");
			facilityVO.put("CCI Type", facility.getFacilityType());
			facilityVO.put("Address",
					facility.getNameAndAddress() != null ? facility.getNameAndAddress().replaceAll("\n", "") : "");
			facilityVO.put("districtId", facility.getArea().getAreaId());
//			facilityVO.put("Status",
//					facility.getAfm().size() > 0
//							? facility.getAfm().get(0).getAccount().isLocked() ? "Inactive" : "Active"
//							: "Inactive");

			if (facility.getContactDetails() != null) {
				facilityVO.put("Contact No",
						((facility.getContactDetails().getNameOfHead() == null ? ""
								: facility.getContactDetails().getNameOfHead())
								+ ", "
								+ (facility.getContactDetails().getEmailId() == null ? ""
										: facility.getContactDetails().getEmailId())
								+ ", " + (facility.getContactDetails().getPhNo() == null ? ""
										: facility.getContactDetails().getPhNo())).replaceAll("\n", ""));
			} else {
				facilityVO.put("Contact No", "");
			}

			switch (facility.getFacilityType().trim()) {
			case "Observation Home":
				CICL_HOMES++;

//				facilityVO.put("* Sanctioned Strength", submissionMap.get(facility.getFacilityId())!=null ? submissionMap.get(facility.getFacilityId()).getData().get("F2Q16").isNull()  || submissionMap.get(facility.getFacilityId()).getData().get("F2Q16").toString().isEmpty()? 0 : Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData().get("F2Q16").asText()) : 0);
//				facilityVO.put("* Current Strength", submissionMap.get(facility.getFacilityId())!=null ?  submissionMap.get(facility.getFacilityId()).getData().get("F2Q15").isNull()  || submissionMap.get(facility.getFacilityId()).getData().get("F2Q15").toString().isEmpty()? 0 : Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData().get("F2Q15").asText()): 0);
//				facilityVO.put("* Number of staff", submissionMap.get(facility.getFacilityId())!=null ?  submissionMap.get(facility.getFacilityId()).getData().get("F2Q51").isNull()  || submissionMap.get(facility.getFacilityId()).getData().get("F2Q51").toString().isEmpty()? 0 : Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData().get("F2Q51").asText()): 0);
				
				
				if(submissionMap.get(facility.getFacilityId()) != null) {
					++NUMBER_OF_CICL_SUBMITTED_DATA;
				}
				facilityVO.put("* Sanctioned Strength", submissionMap.get(facility.getFacilityId()) != null
						? submissionMap.get(facility.getFacilityId()).getData().get("F2Q16").isNull() || submissionMap
								.get(facility.getFacilityId()).getData().get("F2Q16").toString().isEmpty()
										? 0
										: Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData()
												.get("F2Q16").asText().trim())
						: 0);
				facilityVO.put("* Current Strength", submissionMap.get(facility.getFacilityId()) != null
						? submissionMap.get(facility.getFacilityId()).getData().get("F2Q15").isNull() || submissionMap
								.get(facility.getFacilityId()).getData().get("F2Q15").toString().isEmpty()
										? 0
										: Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData()
												.get("F2Q15").asText().trim())
						: 0);
				facilityVO.put("* Number of staff", submissionMap.get(facility.getFacilityId()) != null
						? submissionMap.get(facility.getFacilityId()).getData().get("F2Q51").isNull() || submissionMap
								.get(facility.getFacilityId()).getData().get("F2Q51").toString().isEmpty()
										? 0
										: Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData()
												.get("F2Q51").asText().trim())
						: 0);

				CICL_SANCTIONED_STRENGTH = CICL_SANCTIONED_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Sanctioned Strength").toString());
				CICL_CURRENT_STRENGTH = CICL_CURRENT_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Current Strength").toString());
				break;
			case "Special Home":
				CICL_HOMES++;
//				facilityVO.put("* Sanctioned Strength", submissionMap.get(facility.getFacilityId())!=null ? submissionMap.get(facility.getFacilityId()).getData().get("F5Q16").isNull() || submissionMap.get(facility.getFacilityId()).getData().get("F5Q16").toString().isEmpty()? 0 : Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData().get("F5Q16").asText()): 0);
//				facilityVO.put("* Current Strength", submissionMap.get(facility.getFacilityId())!=null ? submissionMap.get(facility.getFacilityId()).getData().get("F5Q15").isNull()  || submissionMap.get(facility.getFacilityId()).getData().get("F5Q15").toString().isEmpty()? 0 : Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData().get("F5Q15").asText()): 0);
//				facilityVO.put("* Number of staff", submissionMap.get(facility.getFacilityId())!=null ?  submissionMap.get(facility.getFacilityId()).getData().get("F5Q27").isNull() || submissionMap.get(facility.getFacilityId()).getData().get("F5Q27").toString().isEmpty()? 0 : Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData().get("F5Q27").asText()): 0);
				if(submissionMap.get(facility.getFacilityId()) != null) {
					++NUMBER_OF_CICL_SUBMITTED_DATA;
				}
				facilityVO.put("* Sanctioned Strength", submissionMap.get(facility.getFacilityId()) != null
						? submissionMap.get(facility.getFacilityId()).getData().get("F5Q16").isNull() || submissionMap
								.get(facility.getFacilityId()).getData().get("F5Q16").toString().isEmpty()
										? 0
										: Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData()
												.get("F5Q16").asText().trim())
						: 0);
				facilityVO.put("* Current Strength", submissionMap.get(facility.getFacilityId()) != null
						? submissionMap.get(facility.getFacilityId()).getData().get("F5Q15").isNull() || submissionMap
								.get(facility.getFacilityId()).getData().get("F5Q15").toString().isEmpty()
										? 0
										: Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData()
												.get("F5Q15").asText().trim())
						: 0);
				facilityVO.put("* Number of staff", submissionMap.get(facility.getFacilityId()) != null
						? submissionMap.get(facility.getFacilityId()).getData().get("F5Q27").isNull() || submissionMap
								.get(facility.getFacilityId()).getData().get("F5Q27").toString().isEmpty()
										? 0
										: Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData()
												.get("F5Q27").asText().trim())
						: 0);

				CICL_SANCTIONED_STRENGTH = CICL_SANCTIONED_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Sanctioned Strength").toString());
				CICL_CURRENT_STRENGTH = CICL_CURRENT_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Current Strength").toString());

				break;

			case "children Home":
			case "children home":
			case "CHildren Home":
			case "CHILDREN HOME":
			case "Children home":
			case "Children Home":
			case "Reception Unit":
			case "Open Shelter":
				CNCP_HOMES++;
				if(submissionMap.get(facility.getFacilityId()) != null) {
					++NUMBER_OF_CNCP_SUBMITTED_DATA;
				}
				facilityVO.put("* Sanctioned Strength", getValue(submissionMap, facility, "F1Q16", questionMap));
				facilityVO.put("* Current Strength", getValue(submissionMap, facility, "F1Q15", questionMap));
				facilityVO.put("* Number of staff", getValue(submissionMap, facility, "F1Q51", questionMap));

				CNCP_SANCTIONED_STRENGTH = CNCP_SANCTIONED_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Sanctioned Strength").toString());
				CNCP_CURRENT_STRENGTH = CNCP_CURRENT_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Current Strength").toString());

				break;

			case "SAA":
				CNCP_HOMES++;
				if(submissionMap.get(facility.getFacilityId()) != null) {
					++NUMBER_OF_CNCP_SUBMITTED_DATA;
				}
				facilityVO.put("* Sanctioned Strength", getValue(submissionMap, facility, "F3Q12", questionMap));
				facilityVO.put("* Current Strength", getValue(submissionMap, facility, "F3Q11", questionMap));
				CNCP_SANCTIONED_STRENGTH = CNCP_SANCTIONED_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Sanctioned Strength").toString());
				CNCP_CURRENT_STRENGTH = CNCP_CURRENT_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Current Strength").toString());
				facilityVO.put("* Number of staff", getValue(submissionMap, facility, "F3Q55", questionMap));

				break;
			case "After Care Home":
				CNCP_HOMES++;
				if(submissionMap.get(facility.getFacilityId()) != null) {
					++NUMBER_OF_CNCP_SUBMITTED_DATA;
				}
				facilityVO.put("* Sanctioned Strength", getValue(submissionMap, facility, "F4Q3", questionMap));
				facilityVO.put("* Current Strength", getValue(submissionMap, facility, "F4Q2", questionMap));
				CNCP_SANCTIONED_STRENGTH = CNCP_SANCTIONED_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Sanctioned Strength").toString());
				CNCP_CURRENT_STRENGTH = CNCP_CURRENT_STRENGTH
						+ Integer.parseInt(facilityVO.get("* Current Strength").toString());
				facilityVO.put("* Number of staff", getValue(submissionMap, facility, "F4Q21", questionMap));
				break;
			default:
				break;
			}

			tableData.add(facilityVO);
		}
		// setting up quick stats
		QuickStartVO numOfCICL_HOMES = new QuickStartVO();
		numOfCICL_HOMES.setLabel("Total no. of CCI's under CICL");
		numOfCICL_HOMES.setImg(Constants.Web.NO_OF_INSTITUTION);
		numOfCICL_HOMES.setValue(CICL_HOMES + "");
		
		QuickStartVO numOfCCI_SUBMISSIONS = new QuickStartVO();
		numOfCCI_SUBMISSIONS.setLabel("Total no. of CICL submitted data");
		numOfCCI_SUBMISSIONS.setImg(Constants.Web.NO_OF_INSTITUTION);
		numOfCCI_SUBMISSIONS.setValue(NUMBER_OF_CICL_SUBMITTED_DATA + "");

		QuickStartVO numOfCICL_SANCTIONED_STRENGTH = new QuickStartVO();
		numOfCICL_SANCTIONED_STRENGTH.setLabel("Total Sanctioned Strength of CICL children");
		numOfCICL_SANCTIONED_STRENGTH.setImg(Constants.Web.INSTITUTION_CAPACITY);
		numOfCICL_SANCTIONED_STRENGTH.setValue(CICL_SANCTIONED_STRENGTH + "");

		QuickStartVO numOfCICL_CURRENT_STRENGTH = new QuickStartVO();
		numOfCICL_CURRENT_STRENGTH.setLabel("Total Current Strength of CICL children");
		numOfCICL_CURRENT_STRENGTH.setImg(Constants.Web.NO_OF_CHILDREN);
		numOfCICL_CURRENT_STRENGTH.setValue(CICL_CURRENT_STRENGTH + "");

		QuickStartVO numOfCNCP_HOMES = new QuickStartVO();
		numOfCNCP_HOMES.setLabel("Total no. of CCI's under CNCP");
		numOfCNCP_HOMES.setImg(Constants.Web.NO_OF_INSTITUTION);
		numOfCNCP_HOMES.setValue(CNCP_HOMES + "");
		
		QuickStartVO numOfCNCP_SUBMISSIONS = new QuickStartVO();
		numOfCNCP_SUBMISSIONS.setLabel("Total no. of CNCP submitted data");
		numOfCNCP_SUBMISSIONS.setImg(Constants.Web.NO_OF_INSTITUTION);
		numOfCNCP_SUBMISSIONS.setValue(NUMBER_OF_CNCP_SUBMITTED_DATA + "");

		QuickStartVO numOfCNCP_SANCTIONED_STRENGTH = new QuickStartVO();
		numOfCNCP_SANCTIONED_STRENGTH.setLabel("Total Sanctioned Strength of CNCP children");
		numOfCNCP_SANCTIONED_STRENGTH.setImg(Constants.Web.INSTITUTION_CAPACITY);
		numOfCNCP_SANCTIONED_STRENGTH.setValue(CNCP_SANCTIONED_STRENGTH + "");

		QuickStartVO numOfCNCP_CURRENT_STRENGTH = new QuickStartVO();
		numOfCNCP_CURRENT_STRENGTH.setLabel("Total Current Strength of CNCP children");
		numOfCNCP_CURRENT_STRENGTH.setImg(Constants.Web.NO_OF_CHILDREN);
		numOfCNCP_CURRENT_STRENGTH.setValue(CNCP_CURRENT_STRENGTH + "");

		// adding quick stats
		
		quickStats.add(numOfCICL_HOMES);
		quickStats.add(numOfCCI_SUBMISSIONS);
		quickStats.add(numOfCICL_SANCTIONED_STRENGTH);
		quickStats.add(numOfCICL_CURRENT_STRENGTH);

		quickStats.add(numOfCNCP_HOMES);
		quickStats.add(numOfCNCP_SUBMISSIONS);
		quickStats.add(numOfCNCP_SANCTIONED_STRENGTH);
		quickStats.add(numOfCNCP_CURRENT_STRENGTH);

		qvo.setTableColumn(tableHeading);
		qvo.setTableData(tableData);
		qvo.setDistricts(districts);
		qvo.setQuickStats(quickStats);
		return qvo;
	}

	public int getValue(Map<Integer, Submission> submissionMap, Facility facility, String colName,
			Map<String, Question> questionMap) {
		if (submissionMap.get(facility.getFacilityId()) == null
				|| submissionMap.get(facility.getFacilityId()).getData().get(colName) == null
				|| submissionMap.get(facility.getFacilityId()).getData().get(colName).asText().equals("null")
				|| submissionMap.get(facility.getFacilityId()).getData().get(colName).asText().isEmpty()) {
			return 0;
		} else {
			if (questionMap.get(colName).getFieldType().contentEquals("tel") && submissionMap
					.get(facility.getFacilityId()).getData().get(colName).asText().trim().contains("\\.")) {
				return (int) (Double.parseDouble(
						(submissionMap.get(facility.getFacilityId()).getData().get(colName).asText().trim())));
			}
			return Integer.parseInt(submissionMap.get(facility.getFacilityId()).getData().get(colName).asText().trim());
		}
	}
}
