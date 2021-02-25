package in.co.sdrc.scpstn.service;

import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.models.AccountDetailsModel;
import in.co.sdrc.scpstn.models.FacilityDataModel;
import in.co.sdrc.sdrcdatacollector.models.AreaModel;
import in.co.sdrc.sdrcdatacollector.models.ValueObjectModel;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.sdrc.usermgmt.domain.Designation;

public interface WebService {

	Map<String, List<AreaModel>> getAllAreaList();

//	List<ValueObjectModel> getTypeDetails();



	/**
	 * @param areaId
	 * @param areaLevel
	 * @return
	 */
//	Map<String, List<AreaModel>> getAllAreaList(Integer areaId);

	List<AccountDetailsModel> getAllUsers(Integer roleId, Integer areaId);
//

	
	List<Facility> getFacilityBydesignationIdAndAreaId(Integer districtId, Integer areaId);
	
	boolean saveFacility(FacilityDataModel facility);
	
	boolean activateOrDeactivateFacility(boolean active, Integer facilityId);
	
	List<Designation> getFacilityLevelDesignations();
	
	List<Facility> getFacilitiesForSuggestion();

}
