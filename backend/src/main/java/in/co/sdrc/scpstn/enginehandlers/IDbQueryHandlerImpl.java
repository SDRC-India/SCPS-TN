package in.co.sdrc.scpstn.enginehandlers;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.joda.time.LocalDate;
import org.sdrc.usermgmt.domain.Account;
import org.sdrc.usermgmt.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.domain.Submission;
import in.co.sdrc.scpstn.models.Language;
import in.co.sdrc.scpstn.repository.AccountFacilityMappingRepository;
import in.co.sdrc.scpstn.repository.AreaRepository;
import in.co.sdrc.scpstn.repository.SubmissionRepository;
import in.co.sdrc.sdrcdatacollector.handlers.IDbQueryHandler;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.OptionModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 *
 */
@Component
public class IDbQueryHandlerImpl implements IDbQueryHandler {

	@Autowired
	private AreaRepository areaRepository;

	@Autowired
	AccountFacilityMappingRepository accountFacilityMappingRepository;

	@Autowired
	SubmissionRepository submissionRepository;

	@Override
	public List<OptionModel> getOptions(QuestionModel questionModel, Map<Integer, TypeDetail> typeDetailsMap,
			Question question, String checkedValue, Object user1) {

		List<OptionModel> listOfOptions = new ArrayList<>();
		String tableName = questionModel.getTableName().split("\\$\\$")[0].trim();
		String areaLevel = questionModel.getTableName().split("\\$\\$")[1].trim().split("=")[1];
		List<Area> areas = null;
		UserModel user = (UserModel) user1;

		return listOfOptions;

	}

	@Override
	public String getDropDownValueForRawData(String tableName, Integer dropdownId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public QuestionModel setValueForTextBoxFromExternal(QuestionModel questionModel, Question question,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user,Object submissionSent) {

		String featureName = questionModel.getFeatures();
		UserModel userModel = (UserModel) user;
		if (featureName != null && featureName.contains("fetch_from_external")) {
			Integer formId = question.getFormId();
			Account account = new Account((int) userModel.getUserId());
			Facility facility = accountFacilityMappingRepository.findByAccount(account).get(0).getFacility();
			Submission submission = submissionRepository
					.findTop1ByFormIdAndFacilityAndCreatedByOrderBySubmissionDateDesc(formId, facility, account);

			for (String feature : featureName.split("@AND")) {
				switch (feature.split(":")[0].trim()) {
				case "fetch_from_external": {

					switch (feature.split(":")[1].trim()) {
					case "previous_month":
					case "previous_month,last_updated_date": {
						if (submission == null) {
							return questionModel;
						}
						JsonNode node = submission.getData();
						String fieldValue = node.get(questionModel.getColumnName()).asText();
						questionModel.setValue(fieldValue);
					}
						break;
					// fetch previous month value, then disable the field
					case "previous_month_disable_once_entered": {
						if (submission == null) {
							return questionModel;
						}
						JsonNode node = submission.getData();
						String fieldValue = node.get(questionModel.getColumnName()).asText();
						questionModel.setValue(fieldValue);
						String defaultSetting = questionModel.getDefaultSettings();
						defaultSetting = defaultSetting.concat(",disabled");
						questionModel.setDefaultSettings(defaultSetting);
					}
						break;
					case "append_mquarter_to_label": {
//						System.out.println("\n\n"+questionModel.getLanguages().toString() +" \n "+ feature.split(":")[0].trim());

						Calendar c = Calendar.getInstance();
						DataModel dataModel = submissionSent!=null ? (DataModel) submissionSent : null;

						if (dataModel != null) {
							c.setTime(dataModel.getCreatedDate());
						}
						int month = c.get(Calendar.MONTH);
						String UK_QUARTER = (month >= Calendar.JANUARY && month <= Calendar.MARCH)
								? " (October - December)"
								: (month >= Calendar.APRIL && month <= Calendar.JUNE) ? " (Jan - March)"
										: (month >= Calendar.JULY && month <= Calendar.SEPTEMBER) ? " (Apr - June)"
												: " (July - September)";

//						String TAMIL_QUARTER = (month >= Calendar.JANUARY && month <= Calendar.MARCH) ? " (October - December)"
//								: (month >= Calendar.APRIL && month <= Calendar.JUNE) ? " (Jan - March)"
//										: (month >= Calendar.JULY && month <= Calendar.SEPTEMBER) ? " (Apr - June)"
//												: " (July - September)";

						String ORIGINAL_LABEL = question.getQuestion();
						ORIGINAL_LABEL = ORIGINAL_LABEL.concat(UK_QUARTER);
						questionModel.setLabel(ORIGINAL_LABEL);

						JsonNode node = question.getLanguages();
						if (node != null) {
							String UK_LABEL = node.has(Language.EN_UK.toString())
									? node.get(Language.EN_UK.toString()).asText()
									: "";
							String TAMIL_LABEL = node.has(Language.TAMIL.toString())
									? node.get(Language.TAMIL.toString()).asText()
									: "";
							ObjectNode on = JsonNodeFactory.instance.objectNode();

							if (!UK_LABEL.equals("")) {
								UK_LABEL = UK_LABEL.concat(UK_QUARTER);
								on.put(Language.EN_UK.toString(), UK_LABEL);
							}
							if (!TAMIL_LABEL.equals("")) {
								TAMIL_LABEL = TAMIL_LABEL.concat(UK_QUARTER);
								on.put(Language.TAMIL.toString(), TAMIL_LABEL);
							}
							questionModel.setLanguages(on);
						}
//						System.out.println("\n\n"+questionModel.getLanguages().toString() +" \n "+ feature.split(":")[0].trim());

//						System.out.println(""+question.getLanguages().toString());

					}
						break;

					case "append_6month_range_to_label": {
//						System.out.println("\n\n"+questionModel.getLanguages().toString() +" \n "+ feature.split(":")[0].trim());
						
						
						DataModel dataModel = submissionSent!=null ? (DataModel) submissionSent : null;
						LocalDate cDate = new LocalDate(new Date());
						cDate = cDate.minusMonths(1);
						LocalDate fiveMinusDate = new LocalDate(cDate.minusMonths(5));
						if (dataModel != null) {
							fiveMinusDate = new LocalDate(dataModel.getCreatedDate());
							fiveMinusDate = fiveMinusDate.minusMonths(6);
							cDate = new LocalDate(dataModel.getCreatedDate());
							cDate = cDate.minusMonths(1);
						}
						
					

						Month lfivemonth = Month.of(fiveMinusDate.getMonthOfYear());
						Month currentMonth = Month.of(cDate.getMonthOfYear());

						String UK_QUARTER = " ("+lfivemonth.getDisplayName(TextStyle.FULL, Locale.UK) + " - " +

								currentMonth.getDisplayName(TextStyle.FULL, Locale.UK) + ")";

//						String TAMIL_QUARTER = (month >= Calendar.JANUARY && month <= Calendar.MARCH) ? " (October - December)"
//								: (month >= Calendar.APRIL && month <= Calendar.JUNE) ? " (Jan - March)"
//										: (month >= Calendar.JULY && month <= Calendar.SEPTEMBER) ? " (Apr - June)"
//												: " (July - September)";

						String ORIGINAL_LABEL = question.getQuestion();
						ORIGINAL_LABEL = ORIGINAL_LABEL.concat(UK_QUARTER);
						questionModel.setLabel(ORIGINAL_LABEL);

						JsonNode node = question.getLanguages();
						if (node != null) {
							String UK_LABEL = node.has(Language.EN_UK.toString())
									? node.get(Language.EN_UK.toString()).asText()
									: "";
							String TAMIL_LABEL = node.has(Language.TAMIL.toString())
									? node.get(Language.TAMIL.toString()).asText()
									: "";
							ObjectNode on = JsonNodeFactory.instance.objectNode();

							if (!UK_LABEL.equals("")) {
								UK_LABEL = UK_LABEL.concat(UK_QUARTER);
								on.put(Language.EN_UK.toString(), UK_LABEL);
							}
							if (!TAMIL_LABEL.equals("")) {
								TAMIL_LABEL = TAMIL_LABEL.concat(UK_QUARTER);
								on.put(Language.TAMIL.toString(), TAMIL_LABEL);
							}
							questionModel.setLanguages(on);
						}
//						System.out.println(""+questionModel.getLanguages().toString() +" \n "+ feature.split(":")[0].trim());
//						System.out.println(""+question.getLanguages().toString());

					}
						break;
					}
				}
					break;
				}
			}
		}
		return questionModel;
	}

}
