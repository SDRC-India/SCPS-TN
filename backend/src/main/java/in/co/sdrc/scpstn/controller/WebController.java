package in.co.sdrc.scpstn.controller;

import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.models.AccountDetailsModel;
import in.co.sdrc.scpstn.models.FacilityDataModel;
import in.co.sdrc.scpstn.service.AggregationService;
import in.co.sdrc.scpstn.service.WebService;
import in.co.sdrc.sdrcdatacollector.models.AreaModel;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.sdrc.usermgmt.domain.Designation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author subham
 *
 */
@RestController
public class WebController {

	@Autowired
	private WebService webService;

	@Autowired
	private AggregationService aggregationService;

	@ResponseBody
	@RequestMapping(value = "/getAllArea")
	public Map<String, List<AreaModel>> getArea() {
		return webService.getAllAreaList();
	}
	
	@ResponseBody
	@RequestMapping(value="/getFacilityByRoleAndDistrict")
	public List<Facility> getFacilityBydesignationIdAndAreaId(@RequestParam("designationId") Integer designationId, @RequestParam("districtId") Integer districtId) {
		return webService.getFacilityBydesignationIdAndAreaId(designationId, districtId);
	}

	@ResponseBody
	@RequestMapping(value="/getFacilitiesForSuggestion")
	public List<Facility> getFacilitiesForSuggestion() {
		return webService.getFacilitiesForSuggestion();
	}
	
	@RequestMapping(value = "/getUsers")
	public List<AccountDetailsModel> getAllUsers(@RequestParam("roleId") Integer roleId , @RequestParam("areaId") Integer areaId) {
		return webService.getAllUsers(roleId,areaId);
	}
	
	@RequestMapping(value = "/getFacilityLevelDesignations")
	public List<Designation> getFacilityLevelDesignations() {
		return webService.getFacilityLevelDesignations();
	}
	
//	@RequestMapping(value = "/bypass/startAggregation")
	public ResponseEntity<Boolean> startAggregation() {
		return new ResponseEntity<Boolean>(aggregationService.aggregateLastMonthData(),HttpStatus.OK);
	}
	
	@PreAuthorize(value="hasAuthority('USER_MGMT_ALL_API')")
	@RequestMapping(value = "/saveFacility", method = { RequestMethod.POST, RequestMethod.OPTIONS })
	public ResponseEntity<String> saveFacility(@RequestBody FacilityDataModel facility)  {
		try {
			if (webService.saveFacility(facility)) {
				return new ResponseEntity<String>("success", HttpStatus.OK);
			}
			return new ResponseEntity<String>("failure", HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}
//	
	@PreAuthorize(value="hasAuthority('USER_MGMT_ALL_API')")
	@RequestMapping(value = "/changeFacilityStatus", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public ResponseEntity<String> changeFacilityStatus(@RequestParam("active") boolean active, @RequestParam("facilityId") Integer facilityId) {
		try {
			if (webService.activateOrDeactivateFacility(active, facilityId)) {
				return new ResponseEntity<String>("Success", HttpStatus.OK);
			}
			return new ResponseEntity<String>("failure", HttpStatus.OK);
		}catch(Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}
	
}
