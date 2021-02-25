package in.co.sdrc.scpstn.service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.domain.IndicatorClassificationIndicatorUnitSubgroupMapping;
import in.co.sdrc.scpstn.domain.IndicatorUnitSubgroup;
import in.co.sdrc.scpstn.domain.Subgroup;
import in.co.sdrc.scpstn.domain.Unit;
import in.co.sdrc.scpstn.models.AppType;
import in.co.sdrc.scpstn.models.IndicatorClassificationType;
import in.co.sdrc.scpstn.models.IndicatorType;
import in.co.sdrc.scpstn.models.NewIndicatorModel;
import in.co.sdrc.scpstn.models.Type;
import in.co.sdrc.scpstn.models.UnitType;
import in.co.sdrc.scpstn.repository.AgencyRepository;
import in.co.sdrc.scpstn.repository.IndicatorClassificationRepository;
import in.co.sdrc.scpstn.repository.IndicatorClassification_Ius_Mapping_Repository;
import in.co.sdrc.scpstn.repository.IndicatorRepository;
import in.co.sdrc.scpstn.repository.IndicatorUnitSubgroupRepository;
import in.co.sdrc.scpstn.repository.SubgroupRepository;
import in.co.sdrc.scpstn.repository.UnitRepository;

@Service
public class IndicatorManagementServiceImpl implements IndicatorManagementService {

	@Autowired
	private IndicatorRepository indicatorRepository;


	@Autowired
	private UnitRepository unitRepository;

	@Autowired
	private IndicatorClassificationRepository indicatorClassificationRepository;

	@Autowired
	private IndicatorUnitSubgroupRepository indicatorUnitSubgroupRepository;

	@Autowired
	private IndicatorClassification_Ius_Mapping_Repository indicatorClassification_Ius_Mapping_Repository;


	@Autowired
	private SubgroupRepository subgroupRepository;

	@Autowired
	AccountRepository accountRepository;
	
	@Autowired
	AgencyRepository agencyRepository;
	
	@Autowired
	DesignationRepository designationRepository;

	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, JSONArray> initializeJson() {

		Map<String,Integer> designationMap = designationRepository.findAll().stream().collect(Collectors.toMap(Designation::getCode, Designation::getId));
		
		Agency agency = agencyRepository.findAll().get(0);
		
		Map<String, JSONArray> json = new LinkedHashMap<String, JSONArray>();

		JSONArray sectorArray = new JSONArray();

		List<IndicatorClassification> icsSector = indicatorClassificationRepository.findByIndicatorClassificationTypeAndParentIsNull(IndicatorClassificationType.SC);

		for (IndicatorClassification indicatorClassificationOrSectorSubSectorSource : icsSector) {

			JSONObject sector = new JSONObject();

			sector.put("sectorId", indicatorClassificationOrSectorSubSectorSource.getIndicatorClassificationId());
			sector.put("sectorName", indicatorClassificationOrSectorSubSectorSource.getName());
			if (indicatorClassificationOrSectorSubSectorSource.getType() != null && (indicatorClassificationOrSectorSubSectorSource.getType().equals(Type.INDEX))) {
				sector.put("isIndex", true);

			} else {
				sector.put("isIndex", false);
			}

			List<IndicatorClassification> children = indicatorClassificationRepository.findByParent(indicatorClassificationOrSectorSubSectorSource);

			JSONArray subSectorArray = new JSONArray();

			for (IndicatorClassification child : children) {

				JSONObject subsector = new JSONObject();
				subsector.put("sectorId", child.getIndicatorClassificationId());
				subsector.put("sectorName", child.getName());

				if (child.getType() != null && (child.getType().equals(Type.OVERALL))) {
					subsector.put("isOverallIndex", true);
				} else {
					subsector.put("isOverallIndex", false);
				}

				JSONArray subSectorIndicatorArray = new JSONArray();

				List<Indicator> allIndicatorsOfAllAgencies = indicatorRepository.findAllByIndicatorClassificationAndAppType(child,AppType.APP_V_2);
			
				List<Indicator> allIndicatorsOfMyAgency = indicatorRepository.findAllByIndicatorClassificationAndAppType(child,AppType.APP_V_2);

				List<Indicator> nonexistingIndicators = new ArrayList<>();
				
				for (Indicator indicator : allIndicatorsOfAllAgencies) {
					int count = 0;
					for (Indicator myAgencyIndicator : allIndicatorsOfMyAgency) {
						if (indicator.getIndicatorName().equals(myAgencyIndicator.getIndicatorName())) {
							count++;
						}
					}
					if (count == 0) {
						int count2=0;
						for (Indicator existingIndicator : nonexistingIndicators) {
							if (indicator.getIndicatorName().equals(existingIndicator.getIndicatorName())) {
								count2++;
							}
						}
						if(count2==0){
							nonexistingIndicators.add(indicator);
						}
					
					}
				}
				for (Indicator indicator : allIndicatorsOfMyAgency) {

					JSONObject in = new JSONObject();
				
					in.put("isAdded", true);
					in.put("name", indicator.getIndicatorName());
					in.put("metadata", indicator.getIndicatorMetadata());
					
					
					in.put("roleId", designationMap.get(indicator.getIndicatorSource()));

					List<Indicator> numden = indicator.getNumeratorDenominator();

					for (Indicator indicator2 : numden) {
						if (indicator2.getIndicatorType().equals(IndicatorType.NUMERATOR)) {
							in.put("numerator", indicator2.getIndicatorName());
						} else {
							in.put("denominator", indicator2.getIndicatorName());
						}
					}
					subSectorIndicatorArray.add(in);
				}

				for (Indicator indicator : nonexistingIndicators) {

					JSONObject in = new JSONObject();
				
					in.put("isAdded", false);
					in.put("name", indicator.getIndicatorName());
					in.put("metadata", indicator.getIndicatorMetadata());
					in.put("roleId", designationMap.get(indicator.getIndicatorSource()));

					List<Indicator> numden = indicator.getNumeratorDenominator();

					for (Indicator indicator2 : numden) {
						if (indicator2.getIndicatorType().equals(IndicatorType.NUMERATOR)) {
							in.put("numerator", indicator2.getIndicatorName());
						} else {
							in.put("denominator", indicator2.getIndicatorName());
						}
					}
					subSectorIndicatorArray.add(in);
				}

				subsector.put("indicators", subSectorIndicatorArray);
				subSectorArray.add(subsector);
			}
			sector.put("subSectors", subSectorArray);
			sectorArray.add(sector);
		}

		json.put("sectors", sectorArray);

		// adding units

		JSONArray unitsArray = new JSONArray();

		List<Unit> units = unitRepository.findAll();

		for (Unit unit : units) {

			JSONObject unitObj = new JSONObject();
			unitObj.put("unitId", unit.getUnitId());
			unitObj.put("unitName", unit.getUnitName());
			if (unit.getUnitType().equals(UnitType.INDEX)) {
				unitObj.put("indexType", true);
			} else {
				unitObj.put("indexType", false);
			}
			unitsArray.add(unitObj);
		}

		json.put("units", unitsArray);

		// Subgroups

		JSONArray subgroupArray = new JSONArray();

		List<Subgroup> subgroups = subgroupRepository.findAllByOrderBySubgroupValueIdAsc();

		for (Subgroup subgroup : subgroups) {

			JSONObject subgroupObj = new JSONObject();
			subgroupObj.put("subgroupId", subgroup.getSubgroupValueId());
			subgroupObj.put("subgroupName", subgroup.getSubgroupVal());
			subgroupArray.add(subgroupObj);
		}

		json.put("subgroups", subgroupArray);

		// roles

		JSONArray roleArray = new JSONArray();

		List<Designation> roles = designationRepository.findAll();

		for (Designation role : roles) {

			JSONObject roleObj = new JSONObject();
			roleObj.put("roleId", role.getId());
			roleObj.put("roleName", role.getName());
			roleArray.add(roleObj);

		}

		json.put("roles", roleArray);

		// Indicator Activation Timeperiod

		JSONArray indicatorPublishTimePeriodArray = new JSONArray();

		LocalDateTime dateTime = LocalDateTime.now();

		for (int i = 1; i <= agency.getNoOfMonth(); i++) {

			dateTime = dateTime.plusMonths(1);

			JSONObject timeJson = new JSONObject();
			timeJson.put("monthName", dateTime.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
			timeJson.put("year", dateTime.getYear());

			indicatorPublishTimePeriodArray.add(timeJson);

		}

		json.put("indicatorPublishMonthYear", indicatorPublishTimePeriodArray);


		JSONArray highIsGoodArray = new JSONArray();

		JSONObject high = new JSONObject();
		high.put("label", "Yes");
		high.put("value", true);

		highIsGoodArray.add(high);

		JSONObject low = new JSONObject();
		low.put("label", "No");
		low.put("value", false);

		highIsGoodArray.add(low);

		json.put("highIsGood", highIsGoodArray);

		return json;
	}

	@Transactional
	public boolean saveNewIndicator(NewIndicatorModel newIndicatorModel) {

		// doing server side data validation

		if (newIndicatorModel.getIndicatorName() == null || newIndicatorModel.getIndicatorName().isEmpty()) {
			throw new IllegalArgumentException("Indicator name cannot be empty !");
		} else if (!newIndicatorModel.isIndex() && (newIndicatorModel.getDenominatorName() == null || newIndicatorModel.getDenominatorName().isEmpty())) {
			throw new IllegalArgumentException("Denominator name cannot be empty !");
		} else if (!newIndicatorModel.isIndex() && (newIndicatorModel.getNumeratorName() == null || newIndicatorModel.getNumeratorName().isEmpty())) {
			throw new IllegalArgumentException("Numerator name cannot be empty !");
		} else if (newIndicatorModel.getRoleId() == null && !newIndicatorModel.isIndex()) {
			throw new IllegalArgumentException("Role is to be assigned to Index Indicator !");
		} else if (newIndicatorModel.getRoleId() != null && !newIndicatorModel.isIndex() && newIndicatorModel.getRoleId() <= 0) {
			throw new IllegalArgumentException("Invalid role id passed to link the new indicator to specified role !");
		}

		Agency agency = agencyRepository.findAll().get(0);
		Map<Integer,String> designationMap = designationRepository.findAll().stream().collect(Collectors.toMap(Designation::getId, Designation::getCode));

		// add into ic_ius mapping
		IndicatorClassification ic = new IndicatorClassification();
		ic.setIndicatorClassificationId(newIndicatorModel.getSubsectorId());

		Indicator numerator = null, denominator = null;

		Indicator newIndicator = new Indicator();

		newIndicator.setAgency(agency);
		newIndicator.setHighIsGood(newIndicatorModel.isHighIsGood());
		newIndicator.setIndicatorMetadata(newIndicatorModel.getIndicatorMetadata());
		newIndicator.setIndicatorName(newIndicatorModel.getIndicatorName());
		newIndicator.setIndicatorSource(designationMap.get(newIndicatorModel.getRoleId()));
		newIndicator.setAppType(AppType.APP_V_2);
//		newIndicator.set

		if (!newIndicatorModel.isIndex() && !newIndicatorModel.isOverallIndex()) {
			newIndicator.setIndicatorType(IndicatorType.ACTUAL_INDICATOR);
			denominator = new Indicator();
			denominator.setAgency(agency);
			denominator.setIndicatorName(newIndicatorModel.getDenominatorName());
			denominator.setIndicatorType(IndicatorType.DENOMINATOR);

			numerator = new Indicator();
			numerator.setAgency(agency);
			numerator.setIndicatorName(newIndicatorModel.getNumeratorName());
			numerator.setIndicatorType(IndicatorType.NUMERATOR);

			List<Indicator> numeratorDenominators = new ArrayList<Indicator>();
			numeratorDenominators.add(numerator);
			numeratorDenominators.add(denominator);
			newIndicator.setNumeratorDenominator(numeratorDenominators);
		} else if (newIndicatorModel.isIndex() && !newIndicatorModel.isOverallIndex())
			newIndicator.setIndicatorType(IndicatorType.INDEX_INDICATOR);
		else if (newIndicatorModel.isIndex() && newIndicatorModel.isOverallIndex()) {
			newIndicator.setIndicatorType(IndicatorType.OVERALL);
		}

		newIndicator.setIndicatorClassification(ic);

		String publishMonthName = newIndicatorModel.getPublishMonth();

		int publishMonth = Month.valueOf(publishMonthName.toUpperCase()).getValue();
		int publishYear = newIndicatorModel.getPublishYear();

		newIndicator.setPublishMonth(publishMonth);
		newIndicator.setPublishYear(publishYear);

		int displayToDeoFromMonth = publishMonth;
		int displayToDeoFromYear = publishYear;

		if (publishMonth == 12) {
			displayToDeoFromMonth = 1;
			displayToDeoFromYear = publishYear + 1;
		} else {
			displayToDeoFromMonth = publishMonth + 1;
		}

		newIndicator.setDisplayToDeoFromMonth(displayToDeoFromMonth);
		newIndicator.setDisplayToDeoFromYear(displayToDeoFromYear);

		newIndicator = indicatorRepository.save(newIndicator);

		// add into IUS table
		Subgroup subgroup = new Subgroup();
		subgroup.setSubgroupValueId(newIndicatorModel.getSubgroupId());

		Unit unit = new Unit();
		unit.setUnitId(newIndicatorModel.getUnitId());

		IndicatorUnitSubgroup ius = new IndicatorUnitSubgroup();

		ius.setIndicator(newIndicator);
		ius.setSubgroup(subgroup);
		ius.setUnit(unit);

		ius = indicatorUnitSubgroupRepository.save(ius);

		IndicatorClassificationIndicatorUnitSubgroupMapping ic_ius_mapping = new IndicatorClassificationIndicatorUnitSubgroupMapping();
		ic_ius_mapping.setIndicatorUnitSubgroup(ius);
		ic_ius_mapping.setIndicatorClassification(ic);

		indicatorClassification_Ius_Mapping_Repository.save(ic_ius_mapping);

		return true;
	}


}
