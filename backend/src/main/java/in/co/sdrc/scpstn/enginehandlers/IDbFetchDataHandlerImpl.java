package in.co.sdrc.scpstn.enginehandlers;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.sdrc.usermgmt.repository.DesignationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.co.sdrc.scpstn.domain.AccountFacilityMapping;
import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.domain.PublishHistory;
import in.co.sdrc.scpstn.domain.Submission;
import in.co.sdrc.scpstn.repository.AccountFacilityMappingRepository;
import in.co.sdrc.scpstn.repository.AgencyRepository;
import in.co.sdrc.scpstn.repository.PublishHistoryRepository;
import in.co.sdrc.scpstn.repository.SubmissionRepository;
import in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler;
import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesRoleFormMapping;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.RawDataModel;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 */
@Component
public class IDbFetchDataHandlerImpl implements IDbFetchDataHandler {

	@Autowired
	DesignationRepository designationRepository;
	
	
	@Autowired
	SubmissionRepository submissionRepository;
	
	@Autowired
	AccountFacilityMappingRepository accountFacilityMappingRepository;
	
	@Autowired
	AgencyRepository agencyRepository;
	
	@Autowired
	PublishHistoryRepository publishHistoryRepository;
	
	private final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
	
	@SuppressWarnings("unchecked")
	@Override
	public List<DataModel> fetchDataFromDb(EnginesRoleFormMapping mapping, String type, Map<Integer, String> mapOfForms,
			Date startDate, Date endDate, Map<String, Object> paramKeyValMap, HttpSession session, Object user) {

		List<DataModel> submissionDatas = new ArrayList<>();
		UserModel userModel = (UserModel) user;
		Agency agency = agencyRepository.findAll().get(0);
		Account account = new Account();
		account.setId((Integer)userModel.getUserId());
		List<AccountFacilityMapping> accMapping = accountFacilityMappingRepository.findByAccount(account);
		List<Submission> entriesList = submissionRepository.findTop6ByFacilityOrderBySubmissionDateDesc(accMapping.get(0).getFacility());
		
		Collections.reverse(entriesList);
		
		ObjectMapper mapper = new ObjectMapper();
		
		LocalDate currentDateCheck = LocalDate.now();
		
		int day = currentDateCheck.getDayOfMonth();
		
		PublishHistory history = publishHistoryRepository.findByDataBeingPublishedForMonthAndDataBeingPublishedForYear(currentDateCheck.getYear(), currentDateCheck.getMonthValue());

		for(Submission data : entriesList) {
			Map<String,Object> extraKeys=new HashMap<>();
			
			extraKeys.put("submissionId", data.getId());
			if(data.getSubmissionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue() == currentDateCheck.getMonthValue() 
					&& data.getSubmissionDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear() == currentDateCheck.getYear() && day <=agency.getLastDayForDataEdit() && history==null) {
				extraKeys.put("view", "edit");
			}else
				extraKeys.put("view", "preview");
			
			

			org.joda.time.LocalDate submissionDate = new org.joda.time.LocalDate(data.getSubmissionDate().getTime());
			submissionDate = submissionDate.minusMonths(1);
			
			extraKeys.put("monthYearOfSubmission", submissionDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " - "+ submissionDate.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
			extraKeys.put("createdOn", sdf.format(data.getCreatedDate()));
			extraKeys.put("updatedOn", sdf.format(data.getUpdatedDate()));
			DataModel model = new DataModel();
			model.setCreatedDate(data.getCreatedDate());
			model.setData(mapper.convertValue(data.getData(),Map.class));
			model.setFormId(data.getFormId());
			model.setId(data.getId()+"");
			model.setRejected(false);
			model.setUniqueId(data.getUniqueId());
			model.setUpdatedDate(data.getUpdatedDate());
			model.setUserId(data.getCreatedBy().getId()+"");
			model.setUserName(data.getCreatedBy().getUserName());
			model.setExtraKeys(extraKeys);
			submissionDatas.add(model);
		}

		return submissionDatas;
	}

	@Override
	public RawDataModel findAllByRejectedFalseAndSyncDateBetween(Integer formId, Date startDate, Date endDate) {

		return null;
	}

}
