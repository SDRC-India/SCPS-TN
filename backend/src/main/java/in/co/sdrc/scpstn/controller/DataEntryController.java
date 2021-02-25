package in.co.sdrc.scpstn.controller;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import in.co.sdrc.scpstn.service.SubmissionService;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReceiveEventModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;
import in.co.sdrc.sdrcdatacollector.service.FormsService;

@Controller
@ResponseBody
@RequestMapping(value = "/api")
public class DataEntryController {

	@Autowired
	private FormsService dataEntryService;

	@Autowired
	private SubmissionService submissionService;
	

	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@GetMapping("/getQuestion")
	public Map<String, Map<String, List<Map<String, List<QuestionModel>>>>> getQuestions(HttpSession session) {
		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> map = new HashMap<>();
		return dataEntryService.getQuestions(map, session, user, (Integer) user.getDesignationIds().toArray()[0]);
	}

	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@RequestMapping(value = "/saveData", method = { RequestMethod.POST, RequestMethod.OPTIONS })
	public ResponseEntity<String> sendNewSubmissionCommand(@RequestBody ReceiveEventModel receiveEventModel,
			Principal principal)  {
		try {
			if (submissionService.saveSubmission(receiveEventModel, 0, principal)) {
				return new ResponseEntity<String>("success", HttpStatus.OK);
			}
			return new ResponseEntity<String>("failure", HttpStatus.OK);
		}catch(Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.CONFLICT);
		}
	}
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@GetMapping("/getDataForReview")
	public ReviewPageModel getDataForReview(@RequestParam("formId") Integer formId,
			@RequestParam(value = "startDate", required = false) String startDate,
			@RequestParam(value = "endDate", required = false) String endDate, HttpSession session) {

		UserModel user = (UserModel) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Map<String, Object> paramKeyValMap = new HashMap<>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		Date date = new Date();
		startDate = sdf.format(date);
		endDate = sdf.format(date);

		ReviewPageModel model = dataEntryService.getDataForReview(formId, startDate, endDate, paramKeyValMap, session,
				user, (Integer) user.getDesignationIds().toArray()[0]);

		return model;
	}
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@RequestMapping(value = "/checkDataEntryStatus", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public ResponseEntity<Boolean> sendNewSubmissionCommand() throws Exception {
		if (submissionService.isDataEntryDoneForCurrentMonth()) {
			return new ResponseEntity<>(true, HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false, HttpStatus.OK);
	}
	
	
	@PreAuthorize(value="hasAuthority('DATA_ENTRY')")
	@RequestMapping(value = "/getCurrentFormEntryMomthYear", method = { RequestMethod.GET, RequestMethod.OPTIONS })
	public ResponseEntity<String> getCurrentFormEntryMomthYear() {
		LocalDate current  = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		current = current.minusMonths(1);
		String monthYear = current.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " - "+ current.getYear();
		return new ResponseEntity<String>(monthYear, HttpStatus.OK);
	}
}
