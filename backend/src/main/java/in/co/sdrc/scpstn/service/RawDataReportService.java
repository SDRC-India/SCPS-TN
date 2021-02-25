package in.co.sdrc.scpstn.service;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.models.MessageModel;

public interface RawDataReportService {

	ResponseEntity<MessageModel> exportRawaData(Integer formId, String startDate, String endDate, Principal principal,
			OAuth2Authentication auth, Integer districtId);

	ResponseEntity<MessageModel> exportReviewDataReport(Integer formId, List<String> submissionIds, Principal principal);

	List<EnginesForm> getRawDataReportAccessForms(Principal auth);
	
}
