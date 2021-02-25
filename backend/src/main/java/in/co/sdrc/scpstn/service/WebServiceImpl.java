package in.co.sdrc.scpstn.service;

import in.co.sdrc.scpstn.domain.AccountAreaMapping;
import in.co.sdrc.scpstn.domain.AccountDetails;
import in.co.sdrc.scpstn.domain.AccountFacilityMapping;
import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.ContactDetails;
import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.domain.NumberOfChildrenPresent;
import in.co.sdrc.scpstn.domain.OrderDetailsOfCWC;
import in.co.sdrc.scpstn.models.AccountDetailsModel;
import in.co.sdrc.scpstn.models.FacilityDataModel;
import in.co.sdrc.scpstn.repository.AccountAreaMappingRepository;
import in.co.sdrc.scpstn.repository.AccountDetailsRepository;
import in.co.sdrc.scpstn.repository.AccountFacilityMappingRepository;
import in.co.sdrc.scpstn.repository.AreaRepository;
import in.co.sdrc.scpstn.repository.CustomDesignationRepository;
import in.co.sdrc.scpstn.repository.FacilityRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.TypeRepository;
import in.co.sdrc.sdrcdatacollector.models.AreaModel;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Column;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 *
 */
@Service
@Transactional
public class WebServiceImpl implements WebService {

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	private DesignationRepository designationRepository;

	@Autowired
	private CustomDesignationRepository customDesignationRepository;

	@Autowired
	private AccountAreaMappingRepository accountAreaMappingRepository;

	@Autowired
	private AccountDetailsRepository accountDetailsRepository;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private TypeRepository typeRepository;

	@Autowired
	private FacilityRepository facilityRepository;

	@Autowired
	AccountFacilityMappingRepository accountFacilityMappingRepository;

	@Override
	public Map<String, List<AreaModel>> getAllAreaList() {

		List<Area> areas = areaRepository.findAllByIsLiveTrueOrderByAreaNameAsc();
		
		List<AreaModel> areaModelList = new ArrayList<>();
		Map<String, List<AreaModel>> areaMap = new LinkedHashMap();

		// setting areas is area-model list
		for (Area area : areas) {

			AreaModel areaModel = new AreaModel();

			areaModel.setAreaCode(area.getAreaCode());
			areaModel.setAreaId(area.getAreaId());
			areaModel.setAreaLevel(area.getAreaLevel().getAreaLevelName());
			areaModel.setAreaLevelId(area.getAreaLevel().getAreaLevelId());
			areaModel.setAreaName(area.getAreaName());
			areaModel.setParentAreaId(area.getParentAreaId());
			areaModelList.add(areaModel);

		}

		// making levelName as a key
		for (AreaModel areaModel : areaModelList) {

			if (areaMap.containsKey(areaModel.getAreaLevel())) {
				areaMap.get(areaModel.getAreaLevel()).add(areaModel);
			} else {
				areaModelList = new ArrayList<>();
				areaModelList.add(areaModel);
				areaMap.put(areaModel.getAreaLevel(), areaModelList);
			}
		}

		return areaMap;
	}

	@Override

	public List<AccountDetailsModel> getAllUsers(Integer designationId, Integer areaId) {

		List<AccountAreaMapping> accountArea = accountAreaMappingRepository
				.findByAccountAccountDesignationMappingDesignationIdAndAreaAreaId(designationId, areaId);

		List<AccountDetailsModel> modelList = new ArrayList<>();
		List<Integer> accountList = new ArrayList<>();
		for (AccountAreaMapping accountAreaMapping : accountArea) {
			accountList.add(accountAreaMapping.getAccount().getId());
		}
		List<AccountDetails> accountDetails = accountDetailsRepository.findByAccountIdIn(accountList);
		Map<Integer, AccountDetails> accountDetailsMap = accountDetails.stream()
				.collect(Collectors.toMap(k -> k.getAccount().getId(), v -> v));
//
		accountArea.forEach(v -> {
//		AccountDetails accountDetails = accountDetailsRepository.findByAccount(v.getAccount());

			AccountDetailsModel model = new AccountDetailsModel();
			model.setAreaId(v.getArea().getAreaId());
			model.setAreaName(v.getArea().getAreaName());
			model.setUserName(v.getAccount().getUserName());
			model.setUserId(v.getAccount().getId());
			model.setEnable(v.getAccount().isEnabled());
			model.setName(accountDetailsMap.get(v.getAccount().getId()).getFullName());
			model.setMobileNumber(accountDetailsMap.get(v.getAccount().getId()).getMblNo());
//		model.setTypeDetailsId(userDetailsMap.get(v.getUser().getId()).getTypeDetails().getTypeDetailId());
			model.setEmailId(v.getAccount().getEmail());
			model.setDesignationId(designationId);

			AccountFacilityMapping afmapping = accountFacilityMappingRepository.findByAccount(v.getAccount()).get(0);
			model.setFacilityId(afmapping.getFacility().getFacilityId());
			model.setFacilityName(afmapping.getFacility().getName()  +"\n\n" + afmapping.getFacility().getNameAndAddress());


			modelList.add(model);
		});

		return modelList;
	}
//	
//	@Override
//	public List<ValueObjectModel> getTypes() {
//		
//		List<Type> list = typeRepository.findByTypeIdIn(Arrays.asList(2,3));
//		List<ValueObjectModel> typeModels = new ArrayList<>();
//
//		for (Type type : list) {
//			ValueObjectModel typeModel = new ValueObjectModel();
//			typeModel.setTypeDetailId(type.getTypeId());
//			typeModel.setTypeDetailName(type.getTypeName());
//			typeModels.add(typeModel);
//		}
//		return typeModels;
//	}

	@Override
	@Transactional
	public boolean saveFacility(FacilityDataModel facilityData) {

		if (facilityData.getName() == null || facilityData.getName().isEmpty()) {
			throw new IllegalArgumentException("Facility name cannot be empty !");
		} else if (facilityData.getFacilityType() == null || facilityData.getFacilityType().isEmpty()) {
			throw new IllegalArgumentException("Facility type cannot be empty !");
		} else if (facilityData.getNameAndAddress() == null || facilityData.getNameAndAddress().isEmpty()) {
			throw new IllegalArgumentException("Address cannot be empty !");
		} else if (facilityData.getAreaId() == null) {
			throw new IllegalArgumentException("District not provided !");
		}

		Facility facility = null;
		if (facilityData.getFacilityId() != null) {
			facility = facilityRepository.findByFacilityId(facilityData.getFacilityId());
		}else {
			facility = new Facility();
		}

		facility.setName(facilityData.getName());
		facility.setFacilityType(facilityData.getFacilityType());
		facility.setNameAndAddress(facilityData.getNameAndAddress());
		facility.setDetailsOfComplaintBoxPlaced(facilityData.getDetailsOfComplaintBoxPlaced());
		facility.setEstablishmentDate(facilityData.getEstablishmentDate());
		facility.setExpiryDate(facilityData.getExpiryDate());
		facility.setLatestInspectionDate(facilityData.getLatestInspectionDate());
		facility.setRegistrationNo(facilityData.getRegistrationNo());
		facility.setOtherRemarks(facilityData.getOtherRemarks());
		facility.setSanctionedStrength(facilityData.getSanctionedStrength());

		ContactDetails contactDetails = new ContactDetails();
		contactDetails.setEmailId(facilityData.getEmailId());
		contactDetails.setNameOfHead(facilityData.getNameOfHead());
		contactDetails.setPhNo(facilityData.getPhNo());
		contactDetails.setWebsiteLink(facilityData.getWebsiteLink());
		facility.setContactDetails(contactDetails);
		facility.setActive(true);

		NumberOfChildrenPresent childrenPresent = new NumberOfChildrenPresent();
		childrenPresent.setBoys0To6(facilityData.getBoys0To6());
		childrenPresent.setBoys7To11(facilityData.getBoys7To11());
		childrenPresent.setBoys12To14(facilityData.getBoys12To14());
		childrenPresent.setBoys15To18(facilityData.getBoys15To18());
		childrenPresent.setBoysTotal(facilityData.getBoysTotal());
		childrenPresent.setGirls0To6(facilityData.getGirls0To6());
		childrenPresent.setGirls7To11(facilityData.getGirls7To11());
		childrenPresent.setGirls12To14(facilityData.getGirls12To14());
		childrenPresent.setGirls15To18(facilityData.getGirls15To18());
		childrenPresent.setGirlsTotal(facilityData.getGirlsTotal());
		facility.setNumberOfChildrenPresent(childrenPresent);

		OrderDetailsOfCWC orderDetailsOfCWC = new OrderDetailsOfCWC();
		orderDetailsOfCWC.setNoOfChildrenWithOrdersOfCWC(facilityData.getNoOfChildrenWithOrdersOfCWC());
		orderDetailsOfCWC.setNoOfChildrenWithoutOrdersOfCWC(facilityData.getNoOfChildrenWithoutOrdersOfCWC());
		orderDetailsOfCWC.setReasonsforChildrenResidingInTheCCIWithoutCWCOrder(
				facilityData.getReasonsforChildrenResidingInTheCCIWithoutCWCOrder());
		facility.setOrderDetailsOfCWC(orderDetailsOfCWC);

		List<Designation> designation = designationRepository
				.findByIdIn(Arrays.asList(facilityData.getDesignationId()));
		facility.setDesignation(designation.get(0));

		Area area = areaRepository.findByAreaId(facilityData.getAreaId());
		facility.setArea(area);

		facilityRepository.save(facility);
		return true;
	}

	@Override
	@Transactional
	public boolean activateOrDeactivateFacility(boolean active, Integer facilityId) {

		Facility facility = facilityRepository.findByFacilityId(facilityId);
		facility.setActive(!active);

		List<AccountFacilityMapping> accountFacilityMappings = accountFacilityMappingRepository
				.findByFacilityFacilityIdIn(facilityId);

		for (AccountFacilityMapping accountFacilityMapping : accountFacilityMappings) {
			Account account = accountRepository.findById(accountFacilityMapping.getAccount().getId());
			if (active)
				account.setLocked(true);
			else
				account.setLocked(false);

		}
		return true;
	}

	@Override
	public List<Designation> getFacilityLevelDesignations() {

		List<String> desingationCodes = Arrays.asList("CH", "OS", "RU", "POS", "OH", "AA", "ACH", "SH", "SAA");
		List<Designation> facilityTypes = customDesignationRepository.findByCodeIn(desingationCodes);
		return facilityTypes;
	}

	@Override
	public List<Facility> getFacilityBydesignationIdAndAreaId(Integer designationId, Integer areaId) {
		if (designationId == null) {
			throw new IllegalArgumentException("desingnationId parameter not provided !");
		} else if (areaId == null) {
			throw new IllegalArgumentException("districtId parameter not provided !");
		}
		List<Facility> facilities = facilityRepository.findByDesignationAndArea(
				designationRepository.findByIdIn(Arrays.asList(designationId)).get(0),
				areaRepository.findByAreaId(areaId));

		for (Facility facility : facilities) {
			if (facility.getContactDetails() == null) {
				facility.setContactDetails(new ContactDetails());
			}
			if (facility.getNumberOfChildrenPresent() == null) {
				facility.setNumberOfChildrenPresent(new NumberOfChildrenPresent());
			}
			if (facility.getOrderDetailsOfCWC() == null) {
				facility.setOrderDetailsOfCWC(new OrderDetailsOfCWC());
			}
			if (facility.getNumberOfChildrenPresent() == null) {
				facility.setNumberOfChildrenPresent(new NumberOfChildrenPresent());
			}
		}
		return facilities;
	}

	@Override
	public List<Facility> getFacilitiesForSuggestion() {
		return facilityRepository
				.findByDesignationCodeIn(Arrays.asList("CH", "OS", "RU", "POS", "OH", "AA", "ACH", "SH", "SAA")).stream()
				.filter(p -> p.isActive() == true).collect(Collectors.toList());
	}

}
