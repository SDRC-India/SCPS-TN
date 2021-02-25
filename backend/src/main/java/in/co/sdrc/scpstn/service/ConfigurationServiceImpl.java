package in.co.sdrc.scpstn.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.Authority;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.domain.DesignationAuthorityMapping;
import org.sdrc.usermgmt.model.AuthorityControlType;
import org.sdrc.usermgmt.repository.AccountDesignationMappingRepository;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.AuthorityRepository;
import org.sdrc.usermgmt.repository.DesignationAuthorityMappingRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
//import org.sdrc.scpstn2.core.IndicatorClassificationType;
//import org.sdrc.scpstn2.core.IndicatorType;
//import org.sdrc.scpstn2.core.UnitType;
//import org.sdrc.scpstn2.domain.Agency;
//import org.sdrc.scpstn2.domain.Area;
//import org.sdrc.scpstn2.domain.Data;
//import org.sdrc.scpstn2.domain.Facility;
//import org.sdrc.scpstn2.domain.FacilityUserMapping;
//import org.sdrc.scpstn2.domain.Indicator;
//import org.sdrc.scpstn2.domain.IndicatorClassification;
//import org.sdrc.scpstn2.domain.IndicatorClassificationIndicatorUnitSubgroupMapping;
//import org.sdrc.scpstn2.domain.IndicatorRoleMapping;
//import org.sdrc.scpstn2.domain.IndicatorUnitSubgroup;
//import org.sdrc.scpstn2.domain.Program_XForm_Mapping;
//import org.sdrc.scpstn2.domain.Role;
//import org.sdrc.scpstn2.domain.RoleFeaturePermissionScheme;
//import org.sdrc.scpstn2.domain.Subgroup;
//import org.sdrc.scpstn2.domain.Timeperiod;
//import org.sdrc.scpstn2.domain.Unit;
//import org.sdrc.scpstn2.domain.User;
//import org.sdrc.scpstn2.domain.UserAreaMapping;
//import org.sdrc.scpstn2.domain.UserRoleFeaturePermissionMapping;
//import org.sdrc.scpstn2.domain.User_Program_XForm_Mapping;
//import org.sdrc.scpstn2.repository.AgencyRepository;
//import org.sdrc.scpstn2.repository.AreaRepository;
//import org.sdrc.scpstn2.repository.DataEntryRepository;
//import org.sdrc.scpstn2.repository.FacilityRepository;
//import org.sdrc.scpstn2.repository.FacilityUserMappingRepository;
//import org.sdrc.scpstn2.repository.IndicatorClassificationRepository;
//import org.sdrc.scpstn2.repository.IndicatorClassification_Ius_Mapping_Repository;
//import org.sdrc.scpstn2.repository.IndicatorRepository;
//import org.sdrc.scpstn2.repository.IndicatorRoleMappingRepository;
//import org.sdrc.scpstn2.repository.IndicatorUnitSubgroupRepository;
//import org.sdrc.scpstn2.repository.Program_XForm_MappingRepository;
//import org.sdrc.scpstn2.repository.RoleFeaturePermissionRepository;
//import org.sdrc.scpstn2.repository.RoleRepository;
//import org.sdrc.scpstn2.repository.SubgroupRepository;
//import org.sdrc.scpstn2.repository.TimePeriodRepository;
//import org.sdrc.scpstn2.repository.UnitRepository;
//import org.sdrc.scpstn2.repository.UserAreaMappingRepository;
//import org.sdrc.scpstn2.repository.UserRepository;
//import org.sdrc.scpstn2.repository.UserRoleFeaturePermissionMappingRepository;
//import org.sdrc.scpstn2.repository.User_Program_XForm_MappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import in.co.sdrc.scpstn.domain.AccountAreaMapping;
import in.co.sdrc.scpstn.domain.AccountDetails;
import in.co.sdrc.scpstn.domain.AccountFacilityMapping;
import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.AreaLevel;
import in.co.sdrc.scpstn.domain.ContactDetails;
import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.domain.IndicatorClassificationIndicatorUnitSubgroupMapping;
import in.co.sdrc.scpstn.domain.IndicatorUnitSubgroup;
import in.co.sdrc.scpstn.domain.NumberOfChildrenPresent;
import in.co.sdrc.scpstn.domain.OrderDetailsOfCWC;
import in.co.sdrc.scpstn.domain.Subgroup;
import in.co.sdrc.scpstn.domain.Unit;
import in.co.sdrc.scpstn.models.AppType;
import in.co.sdrc.scpstn.models.IndicatorClassfier;
import in.co.sdrc.scpstn.models.IndicatorType;
import in.co.sdrc.scpstn.models.UnitType;
import in.co.sdrc.scpstn.repository.AccountAreaMappingRepository;
import in.co.sdrc.scpstn.repository.AccountDetailsRepository;
import in.co.sdrc.scpstn.repository.AccountFacilityMappingRepository;
import in.co.sdrc.scpstn.repository.AgencyRepository;
import in.co.sdrc.scpstn.repository.AreaLevelRepository;
import in.co.sdrc.scpstn.repository.AreaRepository;
import in.co.sdrc.scpstn.repository.FacilityRepository;
import in.co.sdrc.scpstn.repository.IndicatorClassificationRepository;
import in.co.sdrc.scpstn.repository.IndicatorClassification_Ius_Mapping_Repository;
import in.co.sdrc.scpstn.repository.IndicatorRepository;
import in.co.sdrc.scpstn.repository.IndicatorUnitSubgroupRepository;
import in.co.sdrc.scpstn.repository.SubgroupRepository;
import in.co.sdrc.scpstn.repository.UnitRepository;
import in.co.sdrc.sdrcdatacollector.jpadomains.EngineRole;
import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesRoleFormMapping;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineRoleFormMappingRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.EnginesRoleRepoitory;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Service
public class ConfigurationServiceImpl implements ConfigurationService {

	@Autowired
	AreaRepository areaRepository;

	@Autowired
	BCryptPasswordEncoder passwordEncoder;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccountDesignationMappingRepository accountDesignationMappingRepository;

	@Autowired
	AccountAreaMappingRepository accountAreaMappingRepository;

	@Autowired
	EnginesRoleRepoitory enginesRoleRepoitory;

	@Autowired
	EngineFormRepository engineFormRepository;

	@Autowired
	AreaLevelRepository areaLevelRepository;

	@Autowired
	AccountDetailsRepository accountDetailsRepository;

	@Autowired
	FacilityRepository facilityRepository;

	@Autowired
	EngineRoleFormMappingRepository engineRoleFormMappingRepository;

	@Autowired
	AccountFacilityMappingRepository accountFacilityMappingRepository;

	@Autowired
	DesignationRepository designationRepository;

	@Autowired
	AuthorityRepository authorityRepository;

	@Autowired
	DesignationAuthorityMappingRepository designationAuthorityMappingRepository;

	@Autowired
	private IndicatorUnitSubgroupRepository indicatorUnitSubgroupRepository;

	@Autowired
	private IndicatorRepository indicatorRepository;

	@Autowired
	private IndicatorClassificationRepository indicatorClassificationRepository;

	@Autowired
	private IndicatorClassification_Ius_Mapping_Repository indicatorClassification_Ius_Mapping_Repository;

	@Autowired
	private SubgroupRepository subgroupRepository;

	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private AgencyRepository agencyRepository;

	QuestionRepository questionRepository;

	@Override
	public boolean configureIUSAndIcIUSTable() {
		// Generate IUS Mapping For Percentage Indicator

		List<Agency> agencys = agencyRepository.findAll();

		for (Agency agency : agencys) {
			List<Indicator> indicators = indicatorRepository
					.findAllByAgencyAndAppTypeAndIndicatorTypeOrderByIndicatorIdAsc(agency, AppType.APP_V_2,
							IndicatorType.ACTUAL_INDICATOR);

			List<Subgroup> subgroups = subgroupRepository.findAllByOrderBySubgroupValueIdAsc();

			Unit unit = unitRepository.findByUnitType(UnitType.PERCENTAGE);

			for (Subgroup subgroup : subgroups) {

				for (Indicator indicator : indicators) {
					IndicatorUnitSubgroup indicatorUnitSubgroup = indicatorUnitSubgroupRepository
							.findByIndicatorAndUnitAndSubgroup(indicator, unit, subgroup);
					if (indicatorUnitSubgroup == null) {
						indicatorUnitSubgroup = new IndicatorUnitSubgroup();

						indicatorUnitSubgroup.setIndicator(indicator);
						indicatorUnitSubgroup.setSubgroup(subgroup);
						indicatorUnitSubgroup.setUnit(unit);
						indicatorUnitSubgroup = indicatorUnitSubgroupRepository.save(indicatorUnitSubgroup);
					}
				}

			}
		}

		// Generating IUS mapping for Index Indicator Source Wise

		for (Agency agency : agencys) {

			List<Indicator> indicators = indicatorRepository
					.findAllByAgencyAndAppTypeAndIndicatorTypeInOrderByIndicatorIdAsc(agency, AppType.APP_V_2,
							IndicatorType.INDEX_INDICATOR, IndicatorType.INDEX_OVERALL_SOURCEWISE);

			List<Subgroup> subgroups = subgroupRepository.findAllByOrderBySubgroupValueIdAsc();

			Unit unit = unitRepository.findByUnitType(UnitType.INDEX);

			for (Subgroup subgroup : subgroups) {

				for (Indicator indicator : indicators) {

					IndicatorUnitSubgroup indicatorUnitSubgroup = indicatorUnitSubgroupRepository
							.findByIndicatorAndUnitAndSubgroup(indicator, unit, subgroup);
					if (indicatorUnitSubgroup == null) {
						indicatorUnitSubgroup = new IndicatorUnitSubgroup();

						indicatorUnitSubgroup.setIndicator(indicator);
						indicatorUnitSubgroup.setSubgroup(subgroup);
						indicatorUnitSubgroup.setUnit(unit);
						// indicatorUnitSubgroup.setAgency(agency);

						indicatorUnitSubgroup = indicatorUnitSubgroupRepository.save(indicatorUnitSubgroup);
					}

				}

			}
		}

		// Generating IUS mapping for Overall Indicator

//				for (Agency agency : agencys) {
//
//					List<Indicator> indicators = indicatorRepository.findAllByAgencyAndAppTypeAndIndicatorTypeOrderByIndicatorIdAsc(agency,AppType.APP_V_2, IndicatorType.INDEX_OVERALL_SOURCEWISE);
//
//					List<Subgroup> subgroups = subgroupRepository.findAllByOrderBySubgroupValueIdAsc();
//
//					Unit unit = unitRepository.findByUnitType(UnitType.INDEX);
//
//					for (Subgroup subgroup : subgroups) {
//
//						for (Indicator indicator : indicators) {
//
//							IndicatorUnitSubgroup indicatorUnitSubgroup = new IndicatorUnitSubgroup();
//
//							indicatorUnitSubgroup.setIndicator(indicator);
//							indicatorUnitSubgroup.setSubgroup(subgroup);
//							indicatorUnitSubgroup.setUnit(unit);
//							// indicatorUnitSubgroup.setAgency(agency);
//
//							indicatorUnitSubgroup = indicatorUnitSubgroupRepository.save(indicatorUnitSubgroup);
//
//						}
//
//					}
//				}

		// Generate IC_IUS mapping for all IUS belonging to APP_V2

		List<IndicatorUnitSubgroup> ius = indicatorUnitSubgroupRepository
				.findAllByIndicatorAppTypeAndIndicatorIndicatorTypeInOrderByIndicatorUnitSubgroupIdAsc(AppType.APP_V_2,
						IndicatorType.ACTUAL_INDICATOR, IndicatorType.INDEX_INDICATOR,
						IndicatorType.INDEX_OVERALL_SOURCEWISE);

		for (IndicatorUnitSubgroup indicatorUnitSubgroup : ius) {

			Indicator in = indicatorUnitSubgroup.getIndicator();

			IndicatorClassificationIndicatorUnitSubgroupMapping mapping = indicatorClassification_Ius_Mapping_Repository
					.findByIndicatorClassificationAndIndicatorUnitSubgroup(in.getIndicatorClassification(),
							indicatorUnitSubgroup);
			if (mapping == null) {
				mapping = new IndicatorClassificationIndicatorUnitSubgroupMapping();

				mapping.setIndicatorClassification(in.getIndicatorClassification());
				mapping.setIndicatorUnitSubgroup(indicatorUnitSubgroup);

				indicatorClassification_Ius_Mapping_Repository.save(mapping);
			}
		}

		return true;
	}

	@Override
	@Transactional
	public boolean uploadIndicators() {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("templates/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;
			try {
				workbook = new XSSFWorkbook(files[f]);
				List<Question> questionList = new ArrayList<>();
				List<Indicator> newIndicators = new ArrayList<>();
				for (int sheet = 0; sheet < 10; sheet++) {

					XSSFSheet questionSheet = workbook.getSheetAt(sheet);

					System.out.println("Rows Count:::" + questionSheet.getLastRowNum());

					for (int row = 1; row <= questionSheet.getLastRowNum(); row++) {// row
																					// loop
						if (questionSheet.getRow(row) == null || questionSheet.getRow(row).getCell(0) == null)
							break;

						Question question = new Question();

						XSSFRow xssfRow = questionSheet.getRow(row);

						for (int cols = 0; cols < 29; cols++) {// column loop

							Cell cell = xssfRow.getCell(cols);
							if (cols == 0 && (cell == null))
								break;

							switch (cols) {

							case 0:
								question.setSlugId(questionList.size() + 1);
								// System.out.println("Rows::" + cell.getCellTypeEnum().toString());
								question.setFormId((int) cell.getNumericCellValue());
								break;
							case 1:
								question.setQuestionId((int) cell.getNumericCellValue());
								question.setQuestionOrder((int) cell.getNumericCellValue());
								break;
							case 2:
								question.setSection(cell.getStringCellValue());
								break;
							case 3:
								question.setSubsection(cell.getStringCellValue());
								break;
							case 5:
								question.setQuestion(cell.getStringCellValue());
								break;
							case 6:
								question.setColumnName(cell.getStringCellValue());
								break;
							case 7:
								question.setControllerType(cell.getStringCellValue().equals("dropdown-score")
										|| cell.getStringCellValue().equals("segment-score") ? "dropdown"
												: cell.getStringCellValue());
								question.setDisplayScore(
										cell.getStringCellValue().equals("dropdown-score") ? true : false);
								break;
							case 8:
								// question.setDependecy(cell.getBooleanCellValue());
								question.setFieldType(cell.getStringCellValue());
								break;
							case 23:
								if (question.getControllerType().equals("heading")) {
									ObjectMapper mapper = new ObjectMapper();
									mapper.setSerializationInclusion(Include.ALWAYS);
									ObjectReader reader = mapper.reader();

									JSONObject o = new JSONObject();
									o.put("indicatorType", cell.getStringCellValue().trim());

									if (cell.getStringCellValue().trim().equals("Performance")) {
										Cell positivityCell = xssfRow.getCell(24);
										if (positivityCell == null) {
											o.put("positivity", false);
										} else {
											if (positivityCell.getStringCellValue().equals("Positive")) {
												o.put("positivity", true);
											} else {
												o.put("positivity", false);
											}

										}

									} else {
										o.put("positivity", false);
									}
									Cell indicatorSource = xssfRow.getCell(25);
									o.put("source", indicatorSource.getStringCellValue());

									JsonNode node = reader.readTree(o.toString());
									question.setExtraKeys(node);
								}
								break;
							}

						}
						questionList.add(question);
					}

				}

				for (int index = 0; index < questionList.size(); index++) {

					Question question = questionList.get(index);

					if (question.getControllerType().toLowerCase().trim().equals("heading")) {
						Question numeratorQuestion = questionList.get(index + 1);
						Question denominatorQuestion = questionList.get(index + 2);

						Indicator indicator = new Indicator();
						indicator.setAgency(new Agency(1));
						indicator.setDisplayToDeoFromMonth(4);
						indicator.setDisplayToDeoFromYear(2017);
						indicator.setHighIsGood(question.getExtraKeys().get("positivity").asBoolean());

						switch (question.getSubsection().split("_")[1]) {
						case "Management, Coordination and Operation":
						case "Institutional Care":
						case "Finance and Budget Management":
						case "Non Institutional Care":
						case "Structures at district, block and Village":
						case "Intervention":
						case "Follow Up":
						case "Pendency of cases":
						}
						System.out.println("Sector::" + question.getSection().split("_")[1]);
						System.out.println("SubSector::" + question.getSubsection().split("_")[1]);
						IndicatorClassification sector = indicatorClassificationRepository
								.findByNameAndParentIsNull(question.getSection().split("_")[1]);

						IndicatorClassification subsector = indicatorClassificationRepository
								.findByNameAndParent(question.getSubsection().split("_")[1], sector);
						if (subsector == null) {
							System.out.println("No sector:" + question.getSubsection().split("_")[1]
									+ ", found for question:" + question.getSection() + ":" + question.getSubsection()
									+ ", form ID:" + question.getFormId());
							throw new IllegalArgumentException(
									"No sector found for sector :" + question.getSubsection().split("_")[1]);
						}

						indicator.setIndicatorClassification(subsector);
						indicator.setIndicatorMetadata(null);
						indicator.setIndicatorName(question.getQuestion());
						indicator.setIndicatorType(IndicatorType.ACTUAL_INDICATOR);
						indicator.setPublishMonth(02);
						indicator.setPublishYear(2018);
						indicator.setIndicatorXpath(null);
						indicator.setNumeratorXpath(null);
						indicator.setDenominatorXpath(null);
						indicator.setPercentageXPath(null);
						indicator.setJsonXpath(question.getColumnName());
						indicator.setClassifier(
								question.getExtraKeys().get("indicatorType").asText().equals("Performance")
										? IndicatorClassfier.PERFORMANCE
										: IndicatorClassfier.MANAGEMENT);
						indicator.setIndicatorSource(question.getExtraKeys().get("source").asText());

						indicator.setAppType(AppType.APP_V_2);

						Indicator numerator = new Indicator();
						numerator.setAgency(new Agency(1));
						numerator.setDisplayToDeoFromMonth(0);
						numerator.setDisplayToDeoFromYear(0);
						numerator.setHighIsGood(false);
						numerator.setIndicatorName(numeratorQuestion.getQuestion());
						numerator.setIndicatorType(IndicatorType.NUMERATOR);
						numerator.setPublishMonth(0);
						numerator.setPublishYear(0);
						numerator.setJsonXpath(numeratorQuestion.getColumnName());

						Indicator denominator = new Indicator();
						denominator.setAgency(new Agency(1));
						denominator.setDisplayToDeoFromMonth(0);
						denominator.setDisplayToDeoFromYear(0);
						denominator.setHighIsGood(false);
						denominator.setIndicatorName(denominatorQuestion.getQuestion());
						denominator.setIndicatorType(IndicatorType.DENOMINATOR);
						denominator.setPublishMonth(0);
						denominator.setPublishYear(0);
						denominator.setJsonXpath(denominatorQuestion.getColumnName());

						List<Indicator> numeratorDenominators = new ArrayList<>();
						numeratorDenominators.add(numerator);
						numeratorDenominators.add(denominator);

						indicator.setNumeratorDenominator(numeratorDenominators);

						newIndicators.add(indicator);

						index = index + 2;
					}
				}
				indicatorRepository.save(newIndicators);
				configureIUSAndIcIUSTable();
			} catch (Exception e) {
				e.printStackTrace();

			} finally {
				try {
					workbook.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}

		}

		// return new ResponseEntity<>(true, HttpStatus.OK);

		return false;
	}

	@Override
	public boolean genPassword() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean generateIcIusMapping() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean readAndInsertDataFromExcelFileForAgency() throws InvalidFormatException, IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@Transactional
	public boolean createFacilitiesFromExcel() throws InvalidFormatException, IOException {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("facility/update22112019/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

//		int ch = 0, saa =0, sh =0, slNo = 0, ach=0;

		int ch = 1198,
//				saa = 18, sh = 14, ach = 5, 

				slNo = 0;

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;
			try {

				workbook = new XSSFWorkbook(files[f]);
				XSSFSheet questionSheet = workbook.getSheetAt(0);
				List<AccountDetails> accounts = new ArrayList<>();
				for (int rowNum = 1233; rowNum < 1315; rowNum++) {
					XSSFRow row = questionSheet.getRow(rowNum);

					if (row == null || row.getCell(0).getCellTypeEnum() == CellType.BLANK)
						break;

					Facility facility = new Facility();
					ContactDetails contactDetails = new ContactDetails();
					NumberOfChildrenPresent nocp = new NumberOfChildrenPresent();
					OrderDetailsOfCWC odoc = new OrderDetailsOfCWC();
					++slNo;
					for (int colNum = 0; colNum < 26; colNum++) {
						Cell cell = row.getCell(colNum);
						switch (colNum) {
						case 2:
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								facility.setName(cell.getStringCellValue());
							}
							break;
						case 3:
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								facility.setNameAndAddress(cell.getStringCellValue());
							}
							break;
						case 4:
							if (cell.getCellTypeEnum() == CellType.STRING) {
								System.out.println(cell.getStringCellValue());
								switch (cell.getStringCellValue().trim()) {
								case "CHildren Home":
								case "Children Home":
								case "Childrenhome":
								case "Childen Home":
								case "Childrens Home":
								case "Children's Home":
								case "1.CHILDREN HOME":
								case "Children Home.":
								case "1. Children Home":
								case "1. Children Home (BOYS)                 2. Children Home (GIRLS)":
								case "CH":
								case "Children":
								case "chidren home":
								case "children home":
								case "Children home":
								case "children Home":
								case "CHILDREN HOME":
								case "1.Children Home":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("CH"));
									break;
								case "Adoption Agency":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("AA"));
									break;
								case "Specialised Adoption Agency":
								case "SAA":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("SAA"));
									break;
								case "Children Home/SAA":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("SAA"));

									break;
								case "Special Home":
								case "special":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("SH"));
									break;

								case "Observation Home":
								case "Govt. Observation Home":
								case " Observation Home":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("OH"));
									break;

								case "Reception Unit":
								case "Reception Home":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("RU"));
									break;
								case "After Care Home":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("ACH"));
									if (facility.getDesignation() == null) {
										throw new NullPointerException("Designation not found" + "ACH");
									}
									break;
								case "Open Shelter":
									facility.setFacilityType(cell.getStringCellValue());
									facility.setDesignation(designationRepository.findByCode("OS"));
									if (facility.getDesignation() == null) {
										throw new NullPointerException("Designation not found" + "OS");
									}
									break;
								default:
									throw new IllegalArgumentException("Facility type not matching suggesseted list::"
											+ cell.getStringCellValue());
								}

							}

							break;
						case 5:
							String areaName = cell.getStringCellValue();
							System.out.println("Row::" + (row.getCell(0).getCellTypeEnum() == CellType.STRING
									? (row.getCell(0).getStringCellValue())
									: row.getCell(0).getCellTypeEnum() == CellType.NUMERIC
											? (int) (row.getCell(0).getNumericCellValue())
											: 0));

							if (areaName.equals("Pudukottai")) {
								areaName = "Pudukkottai";
							} else if (areaName.equals("Thoothukudi")) {
								areaName = "Thoothukkudi";
							} else if (areaName.equals("Trichy")) {
								areaName = "Tiruchirappalli";
							} else if (areaName.equals("Thirunelveli")) {
								areaName = "Tirunelveli";
							} else if (areaName.equals("Tirupur")) {
								areaName = "Tiruppur";
							} else if (areaName.equals("Thiruvannamalai")) {
								areaName = "Tiruvannamalai";
							} else if (areaName.equals("Villupuram")) {
								areaName = "Viluppuram";
							} else if (areaName.equals("Sivagangai")) {
								areaName = "Sivaganga";
							} else if (areaName.equals("Thiruvanamalai")) {
								areaName = "Tiruvannamalai";
							}

							Area area = areaRepository.findByAreaName(areaName);
							if (area == null) {
								throw new IllegalArgumentException("Invalid area name :" + cell.getStringCellValue());
							}
							facility.setArea(area);
							break;

						case 6:
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								contactDetails.setPhNo(cell.getStringCellValue() + "");

							}
							break;

						case 7:
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								contactDetails.setEmailId(cell.getStringCellValue());
							}

							break;

						case 8: {
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								facility.setEstablishmentDate(cell.getStringCellValue());

							} else {
								facility.setEstablishmentDate(cell == null ? null : cell.getNumericCellValue() + "");
							}

						}
							break;

						case 9: {
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								contactDetails.setNameOfHead(cell.getStringCellValue());

							}

						}

							break;

						case 10:

							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								facility.setRegistrationNo(cell.getStringCellValue());
							} else if (cell != null) {
								facility.setRegistrationNo(cell.getNumericCellValue() + "");
							}
							break;

						case 11: {
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								facility.setExpiryDate(cell.getStringCellValue());
							} else if (cell != null) {
								facility.setExpiryDate(cell.getNumericCellValue() + "");
							}

						}
							break;

						case 12: {
							if (cell != null && cell.getCellTypeEnum() == CellType.STRING) {
								facility.setSanctionedStrength((cell.getStringCellValue()));
							} else if (cell != null) {
								facility.setSanctionedStrength((cell.getNumericCellValue() + ""));
							}

						}
							break;

						default:
							break;
						}
					}

					System.out.println(
							"ROw::" + rowNum + "slNO::" + slNo + "  ::::::Name::" + facility.getNameAndAddress());

					facility.setContactDetails(contactDetails);
					facility.setNumberOfChildrenPresent(nocp);
					facility.setOrderDetailsOfCWC(odoc);
					facilityRepository.save(facility);

					Account user = new Account();
					user.setInvalidAttempts((short) 0);
					user.setAuthorityControlType(AuthorityControlType.DESIGNATION);

					if (facility.getDesignation().getCode().toString().equalsIgnoreCase("CH")) {
						user.setUserName(facility.getDesignation().getCode().toLowerCase().concat("_")
								.concat(facility.getArea().getAreaName().toLowerCase()).concat("_")
								.concat(Integer.toString((++ch))));

					} else if (facility.getDesignation().getCode().toString().equalsIgnoreCase("SAA")) {
						user.setUserName(facility.getDesignation().getCode().toLowerCase().concat("_")
								.concat(facility.getArea().getAreaName().toLowerCase()).concat("_")
								.concat(Integer.toString((++ch))));

					} else if (facility.getDesignation().getCode().toString().equalsIgnoreCase("SH")) {
						user.setUserName(facility.getDesignation().getCode().toLowerCase().concat("_")
								.concat(facility.getArea().getAreaName().toLowerCase()).concat("_")
								.concat(Integer.toString((++ch))));

					} else if (facility.getDesignation().getCode().toString().equalsIgnoreCase("OH")) {
						user.setUserName(facility.getDesignation().getCode().toLowerCase().concat("_")
								.concat(facility.getArea().getAreaName().toLowerCase()).concat("_")
								.concat(Integer.toString((++ch))));

					} else if (facility.getDesignation().getCode().toString().equalsIgnoreCase("RU")) {
						user.setUserName(facility.getDesignation().getCode().toLowerCase().concat("_")
								.concat(facility.getArea().getAreaName().toLowerCase()).concat("_")
								.concat(Integer.toString((++ch))));

					} else if (facility.getDesignation().getCode().toString().equalsIgnoreCase("ACH")) {
						user.setUserName(facility.getDesignation().getCode().toLowerCase().concat("_")
								.concat(facility.getArea().getAreaName().toLowerCase()).concat("_")
								.concat(Integer.toString((++ch))));
					} else if (facility.getDesignation().getCode().toString().equalsIgnoreCase("OS")) {
						user.setUserName(facility.getDesignation().getCode().toLowerCase().concat("_")
								.concat(facility.getArea().getAreaName().toLowerCase()).concat("_")
								.concat(Integer.toString((++ch))));
					}
					user.setPassword(passwordEncoder.encode(user.getUserName().concat("_")
							.concat(Integer.toString(facility.getDesignation().getId()))));

					user.setOtp(null);
					user.setOtpGeneratedDateTime(null);
					user.setEmail("scpstn4@gmail.com");
					user = accountRepository.save(user);

					AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
					accountDesignationMapping.setDesignation(facility.getDesignation());
					accountDesignationMapping.setAccount(user);

					List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
					accountDesignationMappingList.add(accountDesignationMapping);

					accountDesignationMappingRepository.save(accountDesignationMappingList);

					AccountFacilityMapping facilityUserMapping = new AccountFacilityMapping();
					facilityUserMapping.setFacility(facility);
					facilityUserMapping.setAccount(user);
					accountFacilityMappingRepository.save(facilityUserMapping);

					AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
					accountAreaMapping.setArea(facility.getArea());
					accountAreaMapping.setAccount(user);

					accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);

					// save in account details

					AccountDetails accountDetails = new AccountDetails();
					accountDetails.setAccount(user);
					accountDetails.setFullName(user.getUserName());
					accounts.add(accountDetails);
				}
				accountDetailsRepository.save(accounts);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return true;
	}

	@Override
	public boolean createAdminCommisionerAndScpsUsers() {

		Area STATE = areaRepository.findByAreaId(2);

		{
			Designation ADMIN = designationRepository.findByCode("ADMIN");
			Account ADMIN_USER = new Account();
			ADMIN_USER.setInvalidAttempts((short) 0);
			ADMIN_USER.setUserName("admin");
			ADMIN_USER.setPassword(passwordEncoder.encode(ADMIN_USER.getUserName().concat("_").concat("1092")));
			ADMIN_USER.setAuthorityControlType(AuthorityControlType.DESIGNATION);

			ADMIN_USER.setOtp(null);
			ADMIN_USER.setOtpGeneratedDateTime(null);
			ADMIN_USER.setEmail("scpstn4@gmail.com");

			ADMIN_USER = accountRepository.save(ADMIN_USER);

			AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
			accountDesignationMapping.setDesignation(ADMIN);
			accountDesignationMapping.setAccount(ADMIN_USER);

			List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
			accountDesignationMappingList.add(accountDesignationMapping);

			accountDesignationMappingRepository.save(accountDesignationMappingList);

			AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
			accountAreaMapping.setArea(STATE);
			accountAreaMapping.setAccount(ADMIN_USER);

			accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);

			// save in account details

			AccountDetails accountDetails = new AccountDetails();
			accountDetails.setAccount(ADMIN_USER);
			accountDetails.setFullName("ADMIN");

			accountDetailsRepository.save(accountDetails);
		}

		{
			Designation COMMISSIONER = designationRepository.findByCode("COMMISIONER");
			Account COMMISSIONER_USER = new Account();
			COMMISSIONER_USER.setInvalidAttempts((short) 0);
			COMMISSIONER_USER.setUserName("comm_tamilnadu");
			COMMISSIONER_USER
					.setPassword(passwordEncoder.encode(COMMISSIONER_USER.getUserName().concat("_").concat("1092")));
			COMMISSIONER_USER.setAuthorityControlType(AuthorityControlType.DESIGNATION);

			COMMISSIONER_USER.setOtp(null);
			COMMISSIONER_USER.setOtpGeneratedDateTime(null);
			COMMISSIONER_USER.setEmail("scpstn4@gmail.com");
			COMMISSIONER_USER = accountRepository.save(COMMISSIONER_USER);

			AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
			accountDesignationMapping.setDesignation(COMMISSIONER);
			accountDesignationMapping.setAccount(COMMISSIONER_USER);

			List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
			accountDesignationMappingList.add(accountDesignationMapping);

			accountDesignationMappingRepository.save(accountDesignationMappingList);

			AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
			accountAreaMapping.setArea(STATE);
			accountAreaMapping.setAccount(COMMISSIONER_USER);

			accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);

			// save in account details

			AccountDetails accountDetails = new AccountDetails();
			accountDetails.setAccount(COMMISSIONER_USER);
			accountDetails.setFullName("COMMISSIONER_USER");

			accountDetailsRepository.save(accountDetails);
		}

		{
			Designation SCPS = designationRepository.findByCode("SCPS");
			Account SCPS_USER = new Account();
			SCPS_USER.setInvalidAttempts((short) 0);
			SCPS_USER.setUserName("scps");
			SCPS_USER.setPassword(passwordEncoder.encode(SCPS_USER.getUserName().concat("_").concat("1092")));
			SCPS_USER.setAuthorityControlType(AuthorityControlType.DESIGNATION);
			SCPS_USER.setOtp(null);
			SCPS_USER.setOtpGeneratedDateTime(null);
			SCPS_USER.setEmail("scpstn4@gmail.com");
			SCPS_USER = accountRepository.save(SCPS_USER);

			AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
			accountDesignationMapping.setDesignation(SCPS);
			accountDesignationMapping.setAccount(SCPS_USER);

			List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
			accountDesignationMappingList.add(accountDesignationMapping);

			accountDesignationMappingRepository.save(accountDesignationMappingList);

			AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
			accountAreaMapping.setArea(STATE);
			accountAreaMapping.setAccount(SCPS_USER);

			accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);

			// save in account details

			AccountDetails accountDetails = new AccountDetails();
			accountDetails.setAccount(SCPS_USER);
			accountDetails.setFullName("SCPS_USER");
			accountDetailsRepository.save(accountDetails);
		}

		return false;
	}

	@Override
	@Transactional
	public boolean createOtherUser() {
		// TODO Auto-generated method stub
//		"CCI"
//		"SAA"
//		"COMMISIONER"
//		"CH"
//		"OS"
//		"RU"
//		"POS"
//		"OH"
//		"AA"
//		"ACH"
//		"SH"
//		"ADMIN"
		// Excluding the list of CCI's,COMMISIONER and ADMIN during creation of district
		// level users
		List<Designation> designations = designationRepository.findAll().stream()
				.filter(d -> !d.getCode().equals("COMMISIONER") && !d.getCode().equals("CCI")
						&& !d.getCode().equals("CH") && !d.getCode().equals("OS") && !d.getCode().equals("SAA")
						&& !d.getCode().equals("RU") && !d.getCode().equals("OH") && !d.getCode().equals("SH")
						&& !d.getCode().equals("POS") && !d.getCode().equals("ACH") && !d.getCode().equals("AA")
						&& !d.getCode().equals("OH") && !d.getCode().equals("ADMIN") && !d.getCode().equals("SCPS"))
				.collect(Collectors.toList());

		List<Area> districts = areaRepository.findAllByParentAreaId(2);

		for (Designation designation : designations) {
			if (designation.getCode().equals("ADMIN")) {
				throw new IllegalArgumentException("ADMIN IS NOT FILTERED");
			}
			for (Area district : districts) {

				Account user = new Account();
				user.setInvalidAttempts((short) 0);

				user.setUserName(
						designation.getCode().toLowerCase().concat("_").concat(district.getAreaName().toLowerCase()));

				user.setPassword(
						passwordEncoder.encode((user.getUserName().concat("_").concat(designation.getId() + ""))));

				user.setOtp(null);
				user.setOtpGeneratedDateTime(null);
				user.setEmail("scpstn4@gmail.com");
				user.setAuthorityControlType(AuthorityControlType.DESIGNATION);
				user = accountRepository.save(user);

				AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
				accountDesignationMapping.setDesignation(designation);
				accountDesignationMapping.setAccount(user);

				List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
				accountDesignationMappingList.add(accountDesignationMapping);

				accountDesignationMappingRepository.save(accountDesignationMappingList);

				Facility facility = new Facility();
				facility.setArea(district);
				facility.setNameAndAddress(
						designation.getName().toLowerCase().concat("_").concat(district.getAreaName().toLowerCase()));
				facility.setDesignation(designation);

				Facility facilityDomain = facilityRepository.save(facility);

				AccountFacilityMapping facilityUserMapping = new AccountFacilityMapping();
				facilityUserMapping.setFacility(facilityDomain);
				facilityUserMapping.setAccount(user);
				accountFacilityMappingRepository.save(facilityUserMapping);

				AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
				accountAreaMapping.setArea(district);
				accountAreaMapping.setAccount(user);

				accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);

				// save in account details

				AccountDetails accountDetails = new AccountDetails();
				accountDetails.setAccount(user);
				accountDetails.setFullName(user.getUserName());

				accountDetailsRepository.save(accountDetails);

			}

		}

		return true;

	}

	@Override
	public boolean readAndCreateIndicatorsFromSectors() throws InvalidFormatException, IOException {

		return false;
	}

	@Override
	public boolean testAllLogin(HttpServletRequest request) {

		return false;
	}

	@Override
	public boolean createDesignation() {

		Designation designation = new Designation();
		designation.setName("Child Care Institution");
		designation.setCode("CCI");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Juvenile Justice Board");
		designation.setCode("JJB");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Child Welfare Committee");
		designation.setCode("CWC");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("District Child Protection Unit");
		designation.setCode("DCPU");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Specialised Adoption Agency");
		designation.setCode("SAA");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Child Welfare Protection Officer");
		designation.setCode("CWPO");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Special Juvenile Police Units");
		designation.setCode("SJPU");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Commisioner");
		designation.setCode("COMMISIONER");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Children Home");
		designation.setCode("CH");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Open Shelter");
		designation.setCode("OS");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Reception Unit");
		designation.setCode("RU");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Place of Safety");
		designation.setCode("POS");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Observation Home");
		designation.setCode("OH");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Adoption Agency");
		designation.setCode("AA");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("After Care Home");
		designation.setCode("ACH");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Special Home");
		designation.setCode("SH");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("Probation Officer");
		designation.setCode("PO");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("ADMIN");
		designation.setCode("ADMIN");
		designationRepository.save(designation);

		designation = new Designation();
		designation.setName("SCPS");
		designation.setCode("SCPS");
		designationRepository.save(designation);

		return true;

	}

	@Override
	public boolean createAuthority() {

		Authority authority = new Authority();
		authority.setAuthority("CREATE_USER");
		authority.setDescription("Allow user to CREATE USER module");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("USER_MGMT_ALL_API");
		authority.setDescription("Allow user to manage usermanagement module");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("CHANGE_PASSWORD");
		authority.setDescription("Allow user to access changepassword API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("UPDATE_USER");
		authority.setDescription("Allow user to access updateuser API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("ENABLE_DISABLE_USER");
		authority.setDescription("Allow user to access enable/disable user API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("RESET_PASSWORD");
		authority.setDescription("Allow user to access reset password API");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("DASHBOARD_VIEW");
		authority.setDescription("Allow user to access dashboard");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("DATA_ENTRY");
		authority.setDescription("Allow user to data entry module");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("ALL_FORM_RAWDATA_DOWNLOAD");
		authority.setDescription("Allow user to download raw data reports");
		authorityRepository.save(authority);

		authority = new Authority();
		authority.setAuthority("PUBLISH_DATA");
		authority.setDescription("Allow user to publish data to dashboard");
		authorityRepository.save(authority);

		return true;
	}

	@Override
	public boolean createDesignationAuthorityMapping() {

		Authority dataEntry = authorityRepository.findByAuthority("DATA_ENTRY");
		Authority dashboardView = authorityRepository.findByAuthority("DASHBOARD_VIEW");
		Authority ALL_FORM_RAWDATA_DOWNLOAD = authorityRepository.findByAuthority("ALL_FORM_RAWDATA_DOWNLOAD");

		Designation cci = designationRepository.findByCode("CCI");
		Designation dcpu = designationRepository.findByCode("DCPU"); // district
		Designation sjpu = designationRepository.findByCode("SJPU"); // district
		Designation jjb = designationRepository.findByCode("JJB"); // district
		Designation cwc = designationRepository.findByCode("CWC"); // district
		Designation saa = designationRepository.findByCode("SAA"); // district
		Designation cwpo = designationRepository.findByCode("CWPO"); // district
		Designation commissioner = designationRepository.findByCode("COMMISIONER");
		Designation PO = designationRepository.findByCode("PO");

		Designation oh = designationRepository.findByCode("OH");
		Designation aa = designationRepository.findByCode("AA");
		Designation ach = designationRepository.findByCode("ACH");
		Designation sh = designationRepository.findByCode("SH");

		Designation ch = designationRepository.findByCode("CH");
		Designation os = designationRepository.findByCode("OS"); // Open Shelter
		Designation ru = designationRepository.findByCode("RU");
		Designation pos = designationRepository.findByCode("POS");
		Designation SCPS = designationRepository.findByCode("SCPS");

		// dataentry permissions
		DesignationAuthorityMapping designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(cci);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(dcpu);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(jjb);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(cwc);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(saa);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(cwpo);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(aa);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(ach);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(ch);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(os);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(ru);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(pos);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(oh);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(sh);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(sjpu);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(PO);
		designationAuthorityMapping.setAuthority(dataEntry);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(commissioner);
		designationAuthorityMapping.setAuthority(dashboardView);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setDesignation(SCPS);
		designationAuthorityMapping.setAuthority(dashboardView);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setAuthority(authorityRepository.findByAuthority("USER_MGMT_ALL_API"));
		designationAuthorityMapping.setDesignation(designationRepository.findByCode("ADMIN"));
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setAuthority(authorityRepository.findByAuthority("PUBLISH_DATA"));
		designationAuthorityMapping.setDesignation(commissioner);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setAuthority(ALL_FORM_RAWDATA_DOWNLOAD);
		designationAuthorityMapping.setDesignation(SCPS);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);

		designationAuthorityMapping = new DesignationAuthorityMapping();
		designationAuthorityMapping.setAuthority(ALL_FORM_RAWDATA_DOWNLOAD);
		designationAuthorityMapping.setDesignation(commissioner);
		designationAuthorityMappingRepository.save(designationAuthorityMapping);
		return true;
	}

	@Override
	public boolean createAreaLevel() {
		// TODO Auto-generated method stub
		AreaLevel areaLevel = new AreaLevel();
		areaLevel.setAreaLevelName("Country");
		areaLevelRepository.save(areaLevel);

		areaLevel = new AreaLevel();
		areaLevel.setAreaLevelName("State");
		areaLevelRepository.save(areaLevel);

		areaLevel = new AreaLevel();
		areaLevel.setAreaLevelName("District");
		areaLevelRepository.save(areaLevel);

		return true;
	}

	@Override
	@org.springframework.transaction.annotation.Transactional
	public ResponseEntity<String> createEngineConfiguration() {
		List<Designation> designations = designationRepository.findAll().stream()
				.filter(d -> !d.getCode().equals("CCI") && !d.getCode().equals("ADMIN")

				).collect(Collectors.toList());

		for (Designation designation : designations) {
			EngineRole role = new EngineRole();
			role.setRoleCode(designation.getCode());
			role.setRoleId(designation.getId());
			role.setRoleName(designation.getName());
			enginesRoleRepoitory.save(role);
		}

		EnginesForm chform = new EnginesForm();
		chform.setFormId(1);
		chform.setName("CHILDREN HOME FORM");
		chform.setStatus(Status.ACTIVE);
		engineFormRepository.save(chform);

		EnginesForm OHform = new EnginesForm();
		OHform.setFormId(2);
		OHform.setName("OBSERVATION HOME FORM");
		OHform.setStatus(Status.ACTIVE);
		engineFormRepository.save(OHform);

		EnginesForm SAAform = new EnginesForm();
		SAAform.setFormId(3);
		SAAform.setName("SAA FORM");
		SAAform.setStatus(Status.ACTIVE);
		engineFormRepository.save(SAAform);

		EnginesForm ACHform = new EnginesForm();
		ACHform.setFormId(4);
		ACHform.setName("AFTER CARE HOME FORM");
		ACHform.setStatus(Status.ACTIVE);
		engineFormRepository.save(ACHform);

		EnginesForm SHform = new EnginesForm();
		SHform.setFormId(5);
		SHform.setName("SPECIAL HOME FORM");
		SHform.setStatus(Status.ACTIVE);
		engineFormRepository.save(SHform);

		EnginesForm DCPUform = new EnginesForm();
		DCPUform.setFormId(6);
		DCPUform.setName("DCPU FORM");
		DCPUform.setStatus(Status.ACTIVE);
		engineFormRepository.save(DCPUform);

		EnginesForm CWCform = new EnginesForm();
		CWCform.setFormId(7);
		CWCform.setName("CWC FORM");
		CWCform.setStatus(Status.ACTIVE);
		engineFormRepository.save(CWCform);

		EnginesForm JJBform = new EnginesForm();
		JJBform.setFormId(8);
		JJBform.setName("JJB FORM");
		JJBform.setStatus(Status.ACTIVE);
		engineFormRepository.save(JJBform);

		EnginesForm SJPUform = new EnginesForm();
		SJPUform.setFormId(9);
		SJPUform.setName("SJPU FORM");
		SJPUform.setStatus(Status.ACTIVE);
		engineFormRepository.save(SJPUform);

		EnginesForm POFORM = new EnginesForm();
		POFORM.setFormId(10);
		POFORM.setName("PO FORM");
		POFORM.setStatus(Status.ACTIVE);
		engineFormRepository.save(POFORM);

		EnginesRoleFormMapping mapping = new EnginesRoleFormMapping();
		mapping.setForm(chform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("CH"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(OHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("OH"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SAAform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SAA"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(ACHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("ACH"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SH"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(DCPUform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("DCPU"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(CWCform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("CWC"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(JJBform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("JJB"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SJPUform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SJPU"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(POFORM);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("PO"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DATA_ENTRY);
		engineRoleFormMappingRepository.save(mapping);

		// assiging RAW DATA REPORT AUTHORITIES

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(chform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(OHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SAAform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(ACHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(DCPUform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(CWCform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(JJBform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SJPUform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(POFORM);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("SCPS"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		// -----------Assigning Commisioner Raw Data Report Download

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(chform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(OHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SAAform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(ACHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SHform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(DCPUform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(CWCform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(JJBform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(SJPUform);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(POFORM);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("COMMISIONER"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);
		return null;
	}

	public boolean configureSubsectorIndexAndIndexIndicatorForSubsector() {

//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (41, 1, true, 'SC', 'Index of Subsectors', NULL, NULL, 'SECTOR_INDEX', NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (42, 2, false, 'SC', 'Index -  Institutional Care ( Results for Children )', '2', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (43, 3, false, 'SC', 'Index -  Non Institutional Care ( Results for Children )', '3', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (44, 4, false, 'SC', 'Index -  Pendency of Cases ( Results for Children )', '11', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (45, 5, false, 'SC', 'Index -  Missing Children ( Results for Children )', '12', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (46, 6, false, 'SC', 'Index -  Intervention ( Results for Children )', '30', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (47, 7, false, 'SC', 'Index -  Follow Up ( Results for Children )', '31', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (48, 8, false, 'SC', 'Index -  Human Resources ( Human Resources )', '5', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (49, 9, false, 'SC', 'Index -  Capacity Building ( Human Resources )', '6', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (50, 10, false, 'SC', 'Index -  Structures at district, block and Village ( ICPS Structures and Functionalities )', '8', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (51, 11, false, 'SC', 'Index -  Management, Coordination and Operation ( ICPS Structures and Functionalities )', '9', NULL, NULL, 14)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator_classification (indicator_classification_id, ic_order, index, indicatorclassificationtype, classification_name, sector_ids, classification_short_name, type, parent_id) VALUES (52, 12, false, 'SC', 'Index -  Finance and Budget Management ( ICPS Structures and Functionalities )', '10', NULL, NULL, 14)  ON CONFLICT DO NOTHING;

//
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (173, NULL, 3, 2018, true, NULL, 'Index -  Institutional Care ( Results for Children )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 42, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (174, NULL, 3, 2018, true, NULL, 'Index -  Non Institutional Care ( Results for Children )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 43, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (175, NULL, 3, 2018, true, NULL, 'Index -  Pendency of Cases ( Results for Children )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 44, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (176, NULL, 3, 2018, true, NULL, 'Index -  Missing Children ( Results for Children )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 45, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (177, NULL, 3, 2018, true, NULL, 'Index -  Intervention ( Results for Children )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 46, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (178, NULL, 3, 2018, true, NULL, 'Index -  Follow Up ( Results for Children )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 47, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (179, NULL, 3, 2018, true, NULL, 'Index -  Human Resources ( Human Resources )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 48, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (180, NULL, 3, 2018, true, NULL, 'Index -  Capacity Building ( Human Resources )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 49, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (181, NULL, 3, 2018, true, NULL, 'Index -  Structures at district, block and Village ( ICPS Structures and Functionalities )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 50, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (182, NULL, 3, 2018, true, NULL, 'Index -  Management, Coordination and Operation ( ICPS Structures and Functionalities )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 51, NULL)  ON CONFLICT DO NOTHING;
//INSERT INTO indicator (indicator_id, denominator_xpath, display_to_deo_from_month, display_to_deo_from_year, high_is_good, indicator_metadata, indicator_name, indicator_type, indicator_xpath, numerator_xpath, percentage_xpath, publish_month, publish_year, agency_id_fk, indicator_classification_id, belongs_to_indicator) VALUES (183, NULL, 3, 2018, true, NULL, 'Index -  Finance and Budget Management ( ICPS Structures and Functionalities )', 'INDEX_INDICATOR', NULL, NULL, NULL, 2, 2018, 1, 52, NULL)  ON CONFLICT DO NOTHING;

		return true;
	}

	@org.springframework.transaction.annotation.Transactional
	public boolean updateFacilityNames() {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL url = loader.getResource("facility/");
		String path = url.getPath().replaceAll("%20", " ");
		File files[] = new File(path).listFiles();

		if (files == null) {
			throw new RuntimeException("No file found in path " + path);
		}

		for (int f = 0; f < files.length; f++) {

			XSSFWorkbook workbook = null;
			try {
				System.out.println("Updating facilities");
				workbook = new XSSFWorkbook(files[f]);
				XSSFSheet questionSheet = workbook.getSheetAt(0);
				for (int rowNum = 2; rowNum < 1234; rowNum++) {
					XSSFRow row = questionSheet.getRow(rowNum);

					if (row == null || row.getCell(0).getCellTypeEnum() == CellType.BLANK)
						break;
					for (int colNum = 0; colNum < 1; colNum++) {
						switch (colNum) {
						case 0:
							int slNo = 0;
							switch (row.getCell(0).getCellTypeEnum()) {
							case NUMERIC:
								slNo = (int) row.getCell(0).getNumericCellValue();
								break;
							case STRING:
								slNo = Integer.valueOf(row.getCell(0).getStringCellValue());
								break;
							}
							String facilityName = row.getCell(2).getStringCellValue();
							String facilityAddress = row.getCell(3).getStringCellValue();
							String facilityType = row.getCell(4).getStringCellValue();
							System.out.println("ROw::" + rowNum + "slNO::" + slNo + "  ::::::Name::" + facilityName);

							Facility facility = facilityRepository.findByFacilityId(slNo);
							facility.setName(facilityName);
							facility.setNameAndAddress(facilityAddress);
							facility.setFacilityType(facilityType);

							facilityRepository.save(facility);
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean createPoUsersAndDesginationAndEngineRoleFormMapping() {

		Authority authority = new Authority();
		authority.setAuthority("QUICK_STATS");
		authority.setDescription("Allow user to view QuickStats Page");
		authority = authorityRepository.save(authority);

		// --------------------------
		Designation designation = new Designation();
		designation.setName("PO North Zone");
		designation.setCode("PO_NZONE");
		designationRepository.save(designation);

		EngineRole role = new EngineRole();
		role.setRoleCode(designation.getCode());
		role.setRoleId(designation.getId());
		role.setRoleName(designation.getName());
		enginesRoleRepoitory.save(role);

		DesignationAuthorityMapping dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("CHANGE_PASSWORD"));
		dam.setDesignation(designation);
		designationAuthorityMappingRepository.save(dam);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("ALL_FORM_RAWDATA_DOWNLOAD"));
		dam.setDesignation(designation);
		designationAuthorityMappingRepository.save(dam);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("QUICK_STATS"));
		dam.setDesignation(designationRepository.findByCode("SCPS"));
		designationAuthorityMappingRepository.save(dam);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("QUICK_STATS"));
		dam.setDesignation(designationRepository.findByCode("COMMISIONER"));
		designationAuthorityMappingRepository.save(dam);

		// --------------------------

		designation = new Designation();
		designation.setName("PO South Zone");
		designation.setCode("PO_SZONE");
		designationRepository.save(designation);

		role = new EngineRole();
		role.setRoleCode(designation.getCode());
		role.setRoleId(designation.getId());
		role.setRoleName(designation.getName());
		enginesRoleRepoitory.save(role);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("CHANGE_PASSWORD"));
		dam.setDesignation(designation);
		designationAuthorityMappingRepository.save(dam);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("ALL_FORM_RAWDATA_DOWNLOAD"));
		dam.setDesignation(designation);
		designationAuthorityMappingRepository.save(dam);

		// --------------------------

		designation = new Designation();
		designation.setName("PO STATE LEVEL");
		designation.setCode("PO_STATE_LEVEL");
		designationRepository.save(designation);

		role = new EngineRole();
		role.setRoleCode(designation.getCode());
		role.setRoleId(designation.getId());
		role.setRoleName(designation.getName());
		enginesRoleRepoitory.save(role);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("CHANGE_PASSWORD"));
		dam.setDesignation(designation);
		designationAuthorityMappingRepository.save(dam);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("ALL_FORM_RAWDATA_DOWNLOAD"));
		dam.setDesignation(designation);
		designationAuthorityMappingRepository.save(dam);

		dam = new DesignationAuthorityMapping();
		dam.setAuthority(authorityRepository.findByAuthority("DASHBOARD_VIEW"));
		dam.setDesignation(designation);
		designationAuthorityMappingRepository.save(dam);

		// --------------------------

		List<Area> northZoneAreas = areaRepository.findAllByZone("north");
		List<Area> southZoneAreas = areaRepository.findAllByZone("south");

		{

			Designation PO_NZONE = designationRepository.findByCode("PO_NZONE");
			Account PO_NZONE_USER = new Account();
			PO_NZONE_USER.setInvalidAttempts((short) 0);
			PO_NZONE_USER.setUserName("nzone");
			PO_NZONE_USER.setPassword(passwordEncoder.encode(PO_NZONE_USER.getUserName().concat("_").concat("3000")));
			PO_NZONE_USER.setAuthorityControlType(AuthorityControlType.DESIGNATION);

			PO_NZONE_USER.setOtp(null);
			PO_NZONE_USER.setOtpGeneratedDateTime(null);
			PO_NZONE_USER.setEmail("scpstn4@gmail.com");

			PO_NZONE_USER = accountRepository.save(PO_NZONE_USER);

			AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
			accountDesignationMapping.setDesignation(PO_NZONE);
			accountDesignationMapping.setAccount(PO_NZONE_USER);

			List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
			accountDesignationMappingList.add(accountDesignationMapping);

			accountDesignationMappingRepository.save(accountDesignationMappingList);

			Iterator<Area> itr = northZoneAreas.iterator();
			while (itr.hasNext()) {
				AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
				accountAreaMapping.setArea((Area) itr.next());
				accountAreaMapping.setAccount(PO_NZONE_USER);
				accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);
			}
			// save in account details
			AccountDetails accountDetails = new AccountDetails();
			accountDetails.setAccount(PO_NZONE_USER);
			accountDetails.setFullName("PO NORTH ZONE");
			accountDetailsRepository.save(accountDetails);

		}

		{

			Designation SO_NZONE = designationRepository.findByCode("PO_SZONE");
			Account PO_SZONE_USER = new Account();
			PO_SZONE_USER.setInvalidAttempts((short) 0);
			PO_SZONE_USER.setUserName("szone");
			PO_SZONE_USER.setPassword(passwordEncoder.encode(PO_SZONE_USER.getUserName().concat("_").concat("3000")));
			PO_SZONE_USER.setAuthorityControlType(AuthorityControlType.DESIGNATION);
			PO_SZONE_USER.setOtp(null);
			PO_SZONE_USER.setOtpGeneratedDateTime(null);
			PO_SZONE_USER.setEmail("scpstn4@gmail.com");

			PO_SZONE_USER = accountRepository.save(PO_SZONE_USER);

			AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
			accountDesignationMapping.setDesignation(SO_NZONE);
			accountDesignationMapping.setAccount(PO_SZONE_USER);

			List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
			accountDesignationMappingList.add(accountDesignationMapping);

			accountDesignationMappingRepository.save(accountDesignationMappingList);

			Iterator<Area> itr = southZoneAreas.iterator();
			while (itr.hasNext()) {
				AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
				accountAreaMapping.setArea(itr.next());
				accountAreaMapping.setAccount(PO_SZONE_USER);
				accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);
			}
			// save in account details
			AccountDetails accountDetails = new AccountDetails();
			accountDetails.setAccount(PO_SZONE_USER);
			accountDetails.setFullName("PO SOUTH ZONE");
			accountDetailsRepository.save(accountDetails);
		}

		{
			Area STATE = areaRepository.findByAreaId(2);
			Designation PO_STATE_L = designationRepository.findByCode("PO_STATE_LEVEL");
			Account PO_STATE_USER = new Account();
			PO_STATE_USER.setInvalidAttempts((short) 0);
			PO_STATE_USER.setUserName("po_state");
			PO_STATE_USER.setPassword(passwordEncoder.encode(PO_STATE_USER.getUserName().concat("_").concat("4000")));
			PO_STATE_USER.setAuthorityControlType(AuthorityControlType.DESIGNATION);
			PO_STATE_USER.setOtp(null);
			PO_STATE_USER.setOtpGeneratedDateTime(null);
			PO_STATE_USER.setEmail("scpstn4@gmail.com");
			PO_STATE_USER = accountRepository.save(PO_STATE_USER);

			AccountDesignationMapping accountDesignationMapping = new AccountDesignationMapping();
			accountDesignationMapping.setDesignation(PO_STATE_L);
			accountDesignationMapping.setAccount(PO_STATE_USER);

			List<AccountDesignationMapping> accountDesignationMappingList = new ArrayList<>();
			accountDesignationMappingList.add(accountDesignationMapping);
			accountDesignationMappingRepository.save(accountDesignationMappingList);

			AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
			accountAreaMapping.setArea(STATE);
			accountAreaMapping.setAccount(PO_STATE_USER);
			accountAreaMapping = accountAreaMappingRepository.save(accountAreaMapping);

			// save in account details
			AccountDetails accountDetails = new AccountDetails();
			accountDetails.setAccount(PO_STATE_USER);
			accountDetails.setFullName("PO STATE LEVEL USER");
			accountDetailsRepository.save(accountDetails);
		}

		EnginesForm POFORM = engineFormRepository.findByName("PO FORM");

		EnginesRoleFormMapping mapping = new EnginesRoleFormMapping();
		mapping.setForm(POFORM);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("PO_NZONE"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(POFORM);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("PO_SZONE"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		mapping = new EnginesRoleFormMapping();
		mapping.setForm(POFORM);
		mapping.setRole(enginesRoleRepoitory.findByRoleCode("PO_STATE_LEVEL"));
		mapping.setCreatedDate(new Date());
		mapping.setStatus(Status.ACTIVE);
		mapping.setAccessType(AccessType.DOWNLOAD_RAW_DATA);
		engineRoleFormMappingRepository.save(mapping);

		return true;
	}

	public boolean assignReceptionUnitsToChildrenHomeForm() {
		EnginesForm CHILDREN_HOME_FORM = engineFormRepository.findByName("CHILDREN HOME FORM");
		EngineRole receptionUnitRole = enginesRoleRepoitory.findByRoleCode("RU");
		List<EnginesRoleFormMapping> mappings = engineRoleFormMappingRepository
				.findAllByRoleRoleIdAndFormFormIdAndAccessType(receptionUnitRole.getRoleId(),
						CHILDREN_HOME_FORM.getFormId(), AccessType.DATA_ENTRY);
		if (mappings.isEmpty()) {
			EnginesRoleFormMapping mapping = new EnginesRoleFormMapping();
			mapping.setForm(CHILDREN_HOME_FORM);
			mapping.setRole(receptionUnitRole);
			mapping.setCreatedDate(new Date());
			mapping.setStatus(Status.ACTIVE);
			mapping.setAccessType(AccessType.DATA_ENTRY);
			engineRoleFormMappingRepository.save(mapping);
		}

		EngineRole OS = enginesRoleRepoitory.findByRoleCode("OS");
		List<EnginesRoleFormMapping> OSmappings = engineRoleFormMappingRepository
				.findAllByRoleRoleIdAndFormFormIdAndAccessType(OS.getRoleId(),
						CHILDREN_HOME_FORM.getFormId(), AccessType.DATA_ENTRY);
		if (OSmappings.isEmpty()) {
			EnginesRoleFormMapping mapping = new EnginesRoleFormMapping();
			mapping.setForm(CHILDREN_HOME_FORM);
			mapping.setRole(OS);
			mapping.setCreatedDate(new Date());
			mapping.setStatus(Status.ACTIVE);
			mapping.setAccessType(AccessType.DATA_ENTRY);
			engineRoleFormMappingRepository.save(mapping);
		}
		
		// RESET PASSWORD
//		update ACCOUNT set USER_NAME='ch_dindigul_154' where USER_NAME='ch_dharmapuri_154';
//		update ACCOUNT set USER_NAME='ch_dindigul_155' where USER_NAME='ch_dharmapuri_155';
//		update ACCOUNT set USER_NAME='ch_dindigul_156' where USER_NAME='ch_dharmapuri_156';
//		update ACCOUNT set USER_NAME='ch_dindigul_157' where USER_NAME='ch_dharmapuri_157';
//		update ACCOUNT set USER_NAME='ch_dindigul_158' where USER_NAME='ch_dharmapuri_158';
//		update ACCOUNT set USER_NAME='ch_dindigul_159' where USER_NAME='ch_dharmapuri_159';
//		update ACCOUNT set USER_NAME='ch_dindigul_160' where USER_NAME='ch_dharmapuri_160';
//		update ACCOUNT set USER_NAME='ch_dindigul_161' where USER_NAME='ch_dharmapuri_161';
//		update ACCOUNT set USER_NAME='ch_dindigul_162' where USER_NAME='ch_dharmapuri_162';
//		update ACCOUNT set USER_NAME='ch_dindigul_163' where USER_NAME='ch_dharmapuri_163';
//		update ACCOUNT set USER_NAME='ch_dindigul_164' where USER_NAME='ch_dharmapuri_164';
//		update ACCOUNT set USER_NAME='ch_dindigul_165' where USER_NAME='ch_dharmapuri_165';
//		update ACCOUNT set USER_NAME='ch_dindigul_166' where USER_NAME='ch_dharmapuri_166';
//		update ACCOUNT set USER_NAME='ch_dindigul_168' where USER_NAME='ch_dharmapuri_168';
//		update ACCOUNT set USER_NAME='ch_dindigul_169' where USER_NAME='ch_dharmapuri_169';
//		update ACCOUNT set USER_NAME='ch_dindigul_170' where USER_NAME='ch_dharmapuri_170';

//		List<String>  userNames= Arrays.asList("saa_dindigul_1281","ch_dindigul_1282" , "ch_erode_1283" ,  "ch_erode_1284" ,  "ch_erode_1285" , "saa_dindigul_1286" ,
//				"ch_dindigul_154",
//				"ch_dindigul_155", "ch_dindigul_156","ch_dindigul_157","ch_dindigul_158","ch_dindigul_159","ch_dindigul_160","ch_dindigul_161","ch_dindigul_162","ch_dindigul_163",
//				"ch_dindigul_164", "ch_dindigul_165","ch_dindigul_166","ch_dindigul_168","ch_dindigul_169","ch_dindigul_170");
//
//
//		for(String userName : userNames) {
//
//			Account account = accountRepository.findByUserName(userName);
//			Designation d = designationRepository.findByCode(userName.split("_")[0].toUpperCase());
//			 
//			account.setPassword(passwordEncoder.encode(account.getUserName().concat("_")
//					.concat(Integer.toString(d.getId())))); 
//			 
//			accountRepository.save(account);
//		}
		
		
		return true;
	}
}