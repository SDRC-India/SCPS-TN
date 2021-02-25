package in.co.sdrc.scpstn.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public interface ConfigurationService {

	public boolean configureIUSAndIcIUSTable();

	public boolean genPassword();

	public boolean generateIcIusMapping();

	public boolean readAndInsertDataFromExcelFileForAgency() throws InvalidFormatException, IOException;

	public boolean createFacilitiesFromExcel() throws InvalidFormatException, IOException;

	boolean createAdminCommisionerAndScpsUsers();
	
	public boolean createOtherUser();
	
	boolean readAndCreateIndicatorsFromSectors() throws InvalidFormatException, IOException;

	public boolean testAllLogin(HttpServletRequest request);
	
	public boolean createDesignation();
	
	public boolean createAuthority();
	
	public boolean createAreaLevel();
	
	public boolean createDesignationAuthorityMapping();
	

	public ResponseEntity<String> createEngineConfiguration();

	public boolean uploadIndicators();
	
	boolean updateFacilityNames();
	
	boolean createPoUsersAndDesginationAndEngineRoleFormMapping();
	
	public boolean assignReceptionUnitsToChildrenHomeForm();
}
