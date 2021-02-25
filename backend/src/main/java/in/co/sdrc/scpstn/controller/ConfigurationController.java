package in.co.sdrc.scpstn.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import in.co.sdrc.scpstn.service.AggregationService;
import in.co.sdrc.scpstn.service.ConfigurationService;
import in.co.sdrc.sdrcdatacollector.service.UploadFormConfigurationService;

@Controller
@RequestMapping(value = "/bypass")
public class ConfigurationController {

	@Autowired
	ConfigurationService configurationService;

	@Autowired
	UploadFormConfigurationService uploadFormConfigurationService;
	
	@Autowired
	AggregationService aggreagationService;

//	@RequestMapping(value = "/createDesignations",method=RequestMethod.GET)
//	@ResponseBody
	public boolean generateDesignation() {
		boolean designationTable = configurationService.createDesignation();
		boolean authorityTable = configurationService.createAuthority();
		boolean designationAuthorityMappingTable = configurationService.createDesignationAuthorityMapping();
		try {
			generateAccount();
			importQuestions();
			importNewIndicators();
			configureIusAndIcIusMapping();
			importFacilityFromExcel();
			importLanguages();
			configurationService.createAdminCommisionerAndScpsUsers();
			createEngineConfiguration();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return designationTable && authorityTable && designationAuthorityMappingTable;
		// importQuestions();
		// return false;
	}

	// @RequestMapping(value="/createAreaLevel")
	// @ResponseBody
	public boolean generateAreaLevel() {
		return configurationService.createAreaLevel();
	}

	// @RequestMapping(value="/createAccount")
	// @ResponseBody
	public boolean generateAccount() {
		return configurationService.createOtherUser();
	}

	// @RequestMapping(value="/createEngineConfiguration")
	// @ResponseBody
	public ResponseEntity<String> createEngineConfiguration() {
		return configurationService.createEngineConfiguration();
	}

//	@GetMapping("/importQuestions")
	public ResponseEntity<String> importQuestions() {
		return uploadFormConfigurationService.importQuestionData();
	}
	
	@GetMapping("/createMappingFileOfQuestions")
	@ResponseBody
	public ResponseEntity<String> createMappingFileOfQuestions() {
		return ResponseEntity.ok(uploadFormConfigurationService.generateConfigurationFile());
	}

//	 @GetMapping("/importFacility")
	public ResponseEntity<Boolean> importFacilityFromExcel() throws InvalidFormatException, IOException {
		return new ResponseEntity<Boolean>(configurationService.createFacilitiesFromExcel(), HttpStatus.OK);
	}

	// @GetMapping("/importNewIndicators")
	public ResponseEntity<Boolean> importNewIndicators() throws InvalidFormatException, IOException {
		return new ResponseEntity<Boolean>(configurationService.uploadIndicators(), HttpStatus.OK);
	}

//	@GetMapping("/importLanguages")
	public ResponseEntity<Boolean> importLanguages() throws InvalidFormatException, IOException {
		return new ResponseEntity<Boolean>(uploadFormConfigurationService.importLanguages(), HttpStatus.OK);
	}

	// @GetMapping("/configureIusAndIcIusMapping")
	public ResponseEntity<Boolean> configureIusAndIcIusMapping() throws InvalidFormatException, IOException {
		return new ResponseEntity<Boolean>(configurationService.configureIUSAndIcIUSTable(), HttpStatus.OK);
	}

//	@GetMapping("/updateDb")
	public ResponseEntity<Boolean> updateDb() throws InvalidFormatException, IOException {
		configurationService.updateFacilityNames();

		return new ResponseEntity<Boolean>(configurationService.createPoUsersAndDesginationAndEngineRoleFormMapping(),
				HttpStatus.OK);
	}

//	@GetMapping("/updateFNames")
	public ResponseEntity<Boolean> updateFacilityNames() throws InvalidFormatException, IOException {

		return new ResponseEntity<Boolean>(configurationService.updateFacilityNames(), HttpStatus.OK);
	}

//	@GetMapping("/assignReceptionUnitsToChildrenHomeForm")
//	@ResponseBody
	public ResponseEntity<Boolean> assignReceptionUnitsToChildrenHomeForm() throws InvalidFormatException, IOException {
		return new ResponseEntity<Boolean>(configurationService.assignReceptionUnitsToChildrenHomeForm(),
				HttpStatus.OK);
	}
	@GetMapping("/aggregate")
	@ResponseBody
	public ResponseEntity<Boolean> aggregate() {
		try {
			aggreagationService.aggregateLastMonthData();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("success agg");
		return new ResponseEntity<Boolean>(aggreagationService.startPublishingForCurrentMonth(),
				HttpStatus.OK);
	}

}
