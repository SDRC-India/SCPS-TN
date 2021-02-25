package in.co.sdrc.scpstn.service;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import in.co.sdrc.scpstn.domain.AccountFacilityMapping;
import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.domain.Submission;
import in.co.sdrc.scpstn.exceptions.DataEntryDateExceededException;
import in.co.sdrc.scpstn.models.FormStatus;
import in.co.sdrc.scpstn.repository.AccountFacilityMappingRepository;
import in.co.sdrc.scpstn.repository.AgencyRepository;
import in.co.sdrc.scpstn.repository.SubmissionRepository;
import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesRoleFormMapping;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineRoleFormMappingRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel;

@Service
@Transactional
public class SubmissionServiceImpl implements SubmissionService {

	@Autowired
	SubmissionRepository submissionRepository;

	@Autowired
	private AccountFacilityMappingRepository afmr;

	@Autowired
	AgencyRepository agencyRepository;

	@Autowired
	EngineRoleFormMappingRepository engineRoleFormMappingRepository;

	@Autowired(required = false)
	private QuestionRepository questionRepository;

	@Override
	public boolean saveSubmission(ReceiveEventModel receiveEventModel, Integer facilityId, Principal principal) {

		UserModel model = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		List<Question> questionList = questionRepository
				.findAllByFormIdOrderByQuestionOrderAsc(receiveEventModel.getFormId());

		Map<String, Question> questionMap = questionList.stream()
				.collect(Collectors.toMap(Question::getColumnName, question -> question));

		Agency agency = agencyRepository.findAll().get(0);
		LocalDate currentDate = LocalDate.now();
		int day = currentDate.getDayOfMonth();
		if (day <= agency.getLastDayForDataEdit()) {
			if (receiveEventModel.getFormStatus() == null || !(receiveEventModel.getFormStatus().equals("save")
					|| receiveEventModel.getFormStatus().equals("finalized"))) {
				throw new IllegalArgumentException("Invalid parameter sent in payload for formStatus.");
			}
			Account acc = new Account();
			acc.setId((int) model.getUserId());
			List<AccountFacilityMapping> mappings = afmr.findByAccount(acc);
			if (mappings.isEmpty()) {
				throw new RuntimeException("");
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());

			int month = (calendar.get(Calendar.MONTH) + 1);

			String monthString = month + "";
			if (month < 10) {
				monthString = "0" + month;
			}

			Date startDate = null, endDate = null;

			try {
				startDate = sdf.parse(calendar.get(Calendar.YEAR) + "-" + monthString + "-"
						+ calendar.getActualMinimum(Calendar.DAY_OF_MONTH) + " 00:00:00");

				endDate = sdf.parse(calendar.get(Calendar.YEAR) + "-" + monthString + "-"
						+ calendar.getActualMaximum(Calendar.DAY_OF_MONTH) + " 23:59:59");
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			List<EnginesRoleFormMapping> forms = engineRoleFormMappingRepository
					.findByRoleRoleId((Integer) model.getDesignationIds().toArray()[0]);

			boolean anyMatch = forms.stream().anyMatch(f -> f.getForm().getId() == receiveEventModel.getFormId());

			if (!anyMatch) {
				throw new RuntimeException("Form with id " + receiveEventModel.getFormId()
						+ " is not assigned to the designation " + model.getDesignations().toArray()[0] + "!!");
			}

			// check data

			Submission submission = submissionRepository
					.findByFacilityAndCreatedDateBetween(mappings.get(0).getFacility(), startDate, endDate);

			ObjectNode objectNode = (ObjectNode) receiveEventModel.getSubmissionData();

			Iterator<Map.Entry<String, JsonNode>> iter = objectNode.fields();

			while (iter.hasNext()) {
				Map.Entry<String, JsonNode> jsonNode = iter.next();

				String fieldName = jsonNode.getKey();
				if (questionMap.get(fieldName).getFieldType().contentEquals("tel") && jsonNode.getValue() != null
						&& !jsonNode.getValue().asText().trim().contentEquals("")) {
					try {
						Integer.parseInt(jsonNode.getValue().asText());
					} catch (Exception e) {
						throw new RuntimeException(questionMap.get(fieldName).getQuestion()
								+ " has invalid values! Please correct values and submit the form.");
					}
				}
				if (questionMap.get(fieldName).getFieldType().contentEquals("tel") && jsonNode.getValue() != null
						&& jsonNode.getValue().asText().trim().contains(".")) {
					objectNode.put(fieldName, ((int) (Double.parseDouble((jsonNode.getValue().asText().trim())))) + "");
				}
			}

			receiveEventModel.setSubmissionData(objectNode);
			if (submission == null) {

				submission = new Submission();
				submission.setData(receiveEventModel.getSubmissionData());
				submission.setAttachmentCount(receiveEventModel.getAttachmentCount());
				submission.setFormId(receiveEventModel.getFormId());
				submission.setUniqueId(receiveEventModel.getUniqueId());
				submission.setCreatedDate(new Date());
				submission.setUpdatedDate(new Date());
				submission.setSubmissionDate(
						Date.from(LocalDate.now().atStartOfDay(ZoneId.of("Asia/Calcutta")).toInstant()));
				submission.setFormId(receiveEventModel.getFormId());

				submission.setFacility(mappings.get(0).getFacility());
				submission.setCreatedBy(acc);
				submission.setFormStatus(
						receiveEventModel.getFormStatus().equals("save") ? FormStatus.SAVED : FormStatus.FINALIZED);
			} else {

				if (!receiveEventModel.getUniqueId().contentEquals(submission.getUniqueId())) {
					throw new RuntimeException("Unique id mismatch.");
				}

				submission.setData(receiveEventModel.getSubmissionData());
				submission.setSubmissionDate(
						Date.from(LocalDate.now().atStartOfDay(ZoneId.of("Asia/Calcutta")).toInstant()));
				submission.setFormStatus(
						receiveEventModel.getFormStatus().equals("save") ? FormStatus.SAVED : FormStatus.FINALIZED);
				submission.setUpdatedDate(new Date());
			}
			submissionRepository.save(submission);
			return true;
		} else {
			throw new DataEntryDateExceededException("Data entry cannot be done anymore date of month");
		}
	}

	@Override
	public boolean isDataEntryDoneForCurrentMonth() {
		UserModel userModel = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Integer accountId = (Integer) userModel.getUserId();

		Account account = new Account();
		account.setId(accountId);

		List<AccountFacilityMapping> facilityUserMapping = afmr.findByAccount(account);

		Facility facility = facilityUserMapping.get(0).getFacility();

		Submission submission = submissionRepository.findTop1ByFacilityAndCreatedByOrderBySubmissionDateDesc(facility,
				account);

		Agency agency = agencyRepository.findAll().get(0);
		LocalDate currentDate = LocalDate.now();

		LocalDate submissionDate = null;
		if (submission != null) {
			submissionDate = LocalDateTime.ofInstant(submission.getSubmissionDate().toInstant(), ZoneId.systemDefault())
					.toLocalDate();
		}

		if (submission == null && agency.getLastDayForDataEntry() >= currentDate.getDayOfMonth()) {
			// route to data entry
			return false;
		} else if (submission != null && submissionDate.getMonthValue() != currentDate.getMonthValue()
				&& agency.getLastDayForDataEntry() >= currentDate.getDayOfMonth()) {
			return false;
		}
		// if submission is done route to drafts
		return true;
	}

}
