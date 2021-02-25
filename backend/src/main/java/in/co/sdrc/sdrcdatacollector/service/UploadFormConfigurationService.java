package in.co.sdrc.sdrcdatacollector.service;

import org.springframework.http.ResponseEntity;

public interface UploadFormConfigurationService {


	ResponseEntity<String> importQuestionData();
	
	public Boolean configureRoleFormMapping();

	Boolean importLanguages();
	
	
	public String generateConfigurationFile();
	
}
