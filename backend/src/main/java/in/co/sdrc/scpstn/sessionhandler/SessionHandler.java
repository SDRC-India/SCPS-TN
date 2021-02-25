package in.co.sdrc.scpstn.sessionhandler;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.sdrc.usermgmt.core.util.IUserManagementHandler;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.domain.AccountDesignationMapping;
import org.sdrc.usermgmt.domain.Designation;
import org.sdrc.usermgmt.repository.AccountRepository;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import in.co.sdrc.scpstn.domain.AccountAreaMapping;
import in.co.sdrc.scpstn.domain.AccountDetails;
import in.co.sdrc.scpstn.domain.AccountFacilityMapping;
import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.repository.AccountAreaMappingRepository;
import in.co.sdrc.scpstn.repository.AccountDetailsRepository;
import in.co.sdrc.scpstn.repository.AccountFacilityMappingRepository;
import in.co.sdrc.scpstn.repository.AreaRepository;
import in.co.sdrc.scpstn.repository.CustomAccountDesignationMappingRepository;
import in.co.sdrc.scpstn.repository.FacilityRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineRoleFormMappingRepository;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Component
@Transactional
public class SessionHandler implements IUserManagementHandler {

	@Autowired
	AccountAreaMappingRepository accountAreaMappingRepository;

	@Autowired
	AreaRepository areaRepository;

	@Autowired
	AccountFacilityMappingRepository accountFacilityMappingRepository;

	@Autowired
	FacilityRepository facilityRepository;

	@Autowired
	AccountDetailsRepository accountDetailsRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	DesignationRepository designationRepository;

	@Autowired
	CustomAccountDesignationMappingRepository customAccountDesignationMappingRepository;

	@Autowired
	EngineRoleFormMappingRepository engineRoleFormMappingRepository;

	@Override
	public Map<String, Object> sessionMap(Object account) {
		Account acc = (Account) account;
		Map<String, Object> sessionMap = new HashMap<>();
		Designation d = acc.getAccountDesignationMapping().get(0).getDesignation();
		
		Integer designationId = (Integer) d.getId();
		
		sessionMap.put("assignedFormId", engineRoleFormMappingRepository
				.findByRoleRoleIdAndAccessTypeAndFormStatus(designationId, AccessType.DATA_ENTRY, Status.ACTIVE));
		
		List<AccountFacilityMapping> afm = accountFacilityMappingRepository.findByAccount(acc);
		if(!afm.isEmpty()) {
			sessionMap.put("assignedFacility", 
					afm.get(0).getFacility().getName()!=null && afm.get(0).getFacility().getName().isEmpty() ? afm.get(0).getFacility().getNameAndAddress().toUpperCase().replaceAll("_", " ") : afm.get(0).getFacility().getName());
		}
		return sessionMap;
	}

	@Override
	@Transactional
	public boolean saveAccountDetails(Map<String, Object> map, Object account) {
		try {
			Area area = areaRepository.findByAreaId((Integer) map.get("areaId"));
			List<Designation> designationList = designationRepository
					.findByIdIn((List<Integer>) map.get("designationIds"));
			List<Facility> facilities = facilityRepository.findByDesignationAndArea(designationList.get(0), area);

			// save Account Details
			AccountDetails accountDetails = new AccountDetails();
			accountDetails.setAccount((Account) account);
			accountDetails.setFullName((String) map.get("name"));
			accountDetails.setMblNo((String) map.get("mblNo"));
			
			accountDetailsRepository.save(accountDetails);

			// SAVE user-Area Mapping
			AccountAreaMapping accountAreaMapping = new AccountAreaMapping();
			accountAreaMapping.setAccount((Account) account);
			accountAreaMapping.setArea(area);
			accountAreaMappingRepository.save(accountAreaMapping);

			// checks not being cci user
//			if (designationList.get(0).getId() != 1) {
//				for (Facility facility : facilities) {
//					AccountFacilityMapping accountFacilityMapping = new AccountFacilityMapping();
//					accountFacilityMapping.setAccount((Account) account);
//					accountFacilityMapping.setFacility((Facility) facility);
//					accountFacilityMappingRepository.save(accountFacilityMapping);
//				}
//			} else {
				if ((Integer) map.get("facilityId") != null && !map.get("facilityId").toString().isEmpty()) {
					AccountFacilityMapping accountFacilityMapping = new AccountFacilityMapping();
					accountFacilityMapping.setAccount((Account) account);
					accountFacilityMapping
							.setFacility(facilityRepository.findByFacilityId((Integer) map.get("facilityId")));
					accountFacilityMappingRepository.save(accountFacilityMapping);
				} else {
					throw new RuntimeException("key : facilityId not found in map");
				}
//			}
			return true;
		} catch (Exception e) {
			// Log.error("Action: while creating user with payload {} " + map, e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean updateAccountDetails(Map<String, Object> map, Object account, Principal p) {
		List<Integer> designationIds;
		if (map.get("name") == null || map.get("name").toString().isEmpty())
			throw new RuntimeException("key : name not found in map");

		if (map.get("mblNo") == null || map.get("mblNo").toString().isEmpty())
			throw new RuntimeException("key : mblNo not found in map");

		if (map.get("designationIds") == null || map.get("designationIds").toString().isEmpty())
			throw new RuntimeException("key : designationIds not found in map");
		else
			designationIds = (List<Integer>) map.get("designationIds");
		if (map.get("areaId") == null || map.get("areaId").toString().isEmpty())
			throw new RuntimeException("key : areaId not found in map");
		boolean flag = false;
		try {
			Account accountEntity = (Account) account;
			accountEntity.setEmail(map.get("email")!=null ? map.get("email").toString() : null);
			accountRepository.save(accountEntity);
			
			AccountDetails acc = accountDetailsRepository.findByAccount((Account) account);

			if (!(map.get("mblNo").toString()).equals(acc.getMblNo())) {
				acc.setMblNo(map.get("mblNo").toString());
				flag = true;
			}
			if (!(map.get("name").toString()).equals(acc.getFullName())) {
				acc.setFullName(map.get("name").toString());
				flag = true;
			}

			// SAVE user-Area Mapping
			AccountAreaMapping uaMapping = accountAreaMappingRepository.findByAccount((Account) account);
			if (((Integer) map.get("areaId")).intValue() != uaMapping.getArea().getAreaId()) {
				uaMapping.setArea(new Area((Integer) map.get("areaId")));
				flag = true;
			}

			AccountDesignationMapping adm = customAccountDesignationMappingRepository.findByAccount((Account) account);

			if (designationIds.get(0) != adm.getDesignation().getId()) {
				adm.setDesignation(new Designation((Integer) designationIds.get(0)));
				flag = true;
			}

			if (designationIds.get(0) != 1) {
				Area area = areaRepository.findByAreaId((Integer) map.get("areaId"));
				List<Designation> designationList = designationRepository.findByIdIn((designationIds));
				List<Facility> facilities = facilityRepository.findByDesignationAndArea(designationList.get(0), area);

				// remove all previous mappings
				List<AccountFacilityMapping> afMappings = accountFacilityMappingRepository
						.findByAccount((Account) account);
				for (AccountFacilityMapping afmapping : afMappings) {
					afmapping.setLive(false);
				}

				for (Facility facility : facilities) {
					AccountFacilityMapping accountFacilityMapping = new AccountFacilityMapping();
					accountFacilityMapping.setAccount((Account) account);
					accountFacilityMapping.setFacility((Facility) facility);
					accountFacilityMappingRepository.save(accountFacilityMapping);
				}

			} else {
				if ((Integer) map.get("facilityId") != null && !map.get("facilityId").toString().isEmpty()) {
					// remove all previous mappings
					List<AccountFacilityMapping> afMappings = accountFacilityMappingRepository
							.findByAccount((Account) account);
					for (AccountFacilityMapping afmapping : afMappings) {
						afmapping.setLive(false);
					}

					AccountFacilityMapping accountFacilityMapping = new AccountFacilityMapping();
					accountFacilityMapping.setAccount((Account) account);
					accountFacilityMapping
							.setFacility(facilityRepository.findByFacilityId((Integer) map.get("facilityId")));
					accountFacilityMappingRepository.save(accountFacilityMapping);
				} else {
					throw new RuntimeException("key : facilityId not found in map");
				}
			}

			return false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<?> getAllAuthorities() {
		return null;
	}

}
