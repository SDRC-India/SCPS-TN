package in.co.sdrc.sdrcdatacollector.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.co.sdrc.sdrcdatacollector.handlers.IDbFetchDataHandler;
import in.co.sdrc.sdrcdatacollector.handlers.IDbReviewQueryHandler;
import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesRoleFormMapping;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineFormRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.EngineRoleFormMappingRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.QuestionRepository;
import in.co.sdrc.sdrcdatacollector.jparepositories.TypeDetailRepository;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;
import in.co.sdrc.sdrcdatacollector.util.EngineUtils;
import in.co.sdrc.sdrcdatacollector.util.Status;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author azaruddin (azaruddin@sdrc.co.in)
 */
@Service
@Slf4j
@Transactional
public class FormsServiceImpl implements FormsService {

	private final String BEGIN_REPEAT = "beginrepeat";

	@Autowired(required = false)
	private QuestionRepository questionRepository;

	@Autowired(required = false)
	private TypeDetailRepository typeDetailRepository;

	@Autowired(required = false)
	private EngineRoleFormMappingRepository engineRoleFormMappingRepository;

	@Autowired(required = false)
	private EngineFormRepository formRepository;

	@Autowired
	private EngineUtils engineUtils;

	@Autowired
	IDbFetchDataHandler iDbFetchDataHandler;

	private DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private SimpleDateFormat sdfDateTimeWithSeconds = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Autowired
	IDbReviewQueryHandler iDbReviewQueryHandler;

	@Override
	public Map<String, Map<String, List<Map<String, List<QuestionModel>>>>> getQuestions(
			Map<String, Object> paramKeyValMap, HttpSession session, Object user, Integer roleId) {

		if (roleId == null) {
			throw new NullPointerException("No role has been specified for the specified for user.");
		}

		Map<String, Map<String, List<Map<String, List<QuestionModel>>>>> mappedQuestionsMap = new HashMap<>();

		List<EnginesRoleFormMapping> mappings = engineRoleFormMappingRepository
				.findByRoleRoleIdAndAccessTypeAndStatus(roleId, AccessType.DATA_ENTRY, Status.ACTIVE);

		if (mappings.isEmpty()) {
			throw new IllegalArgumentException("No form has been assigned to the specified role id : " + roleId);
		}
		// get all forms
		List<EnginesForm> forms = formRepository.findAll();
		/*
		 * key is formid and value is formname
		 */

		Map<Integer, String> formMap = forms.stream()
				.collect(Collectors.toMap(EnginesForm::getFormId, EnginesForm::getName));
		for (EnginesRoleFormMapping mapping : mappings) {

			log.info("Fetching questions for formId {} for user with roleId {}", mapping.getForm().getFormId(), roleId);

			List<TypeDetail> typeDetails = typeDetailRepository.findByFormId(mapping.getForm().getFormId());

			List<Question> questionList = questionRepository
					.findAllByFormIdAndActiveTrueOrderByQuestionOrderAsc(mapping.getForm().getFormId());

			Map<Integer, TypeDetail> typeDetailsMap = typeDetails.stream()
					.collect(Collectors.toMap(TypeDetail::getSlugId, typeDe -> typeDe));

			Map<String, Question> questionMap = questionList.stream()
					.collect(Collectors.toMap(Question::getColumnName, question -> question));

			Map<String, List<Map<String, List<QuestionModel>>>> mapOfSectionSubsectionListOfQuestionModel = new LinkedHashMap<>();

			List<QuestionModel> listOfQuestionModel = new LinkedList<>();

			Map<String, Map<String, List<QuestionModel>>> sectionMap = new LinkedHashMap<String, Map<String, List<QuestionModel>>>();
			Map<String, List<QuestionModel>> subsectionMap = null;

			QuestionModel questionModel = null;

			for (Question question : questionList) {
				questionModel = null;
				switch (question.getControllerType()) {

				case "textbox":
				case "heading":
					if (question.getParentColumnName().isEmpty()) {
						questionModel = engineUtils.prepareQuestionModel(question);
						questionModel = engineUtils.setValueForTextBoxFromExternal(question, questionModel,
								paramKeyValMap, session, user,null);
					}
					break;
				case "score-holder":
				
				case "Date Widget":
				case "textarea":
				case "uuid":
				case "score-keeper":
				case "sub-score-keeper":
				case "Time Widget":
				case "file":
				case "mfile":
				case "camera":
				case "geolocation":
					if (question.getParentColumnName().isEmpty()) {
						questionModel = engineUtils.prepareQuestionModel(question);
					}
					break;
				case "dropdown":
				case "segment": {
					if (question.getParentColumnName().isEmpty()) {
						questionModel = engineUtils.prepareQuestionModel(question);
						questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap, question,
								null, user, paramKeyValMap, session);
					}
				}
					break;
				case "table":
				case "tableWithRowWiseArithmetic": {
					questionModel = engineUtils.prepareQuestionModel(question);
					/**
					 * from table question id and cell parent id getting all matched cells here
					 */
					List<Question> tableCells = questionList.stream()
							.filter(q -> q.getParentColumnName().equals(question.getColumnName()))
							.collect(Collectors.toList());

					Map<String, List<Question>> groupWiseQuestionsMap = new LinkedHashMap<>();
					tableCells.forEach(cell -> {
						if (groupWiseQuestionsMap.get(cell.getQuestion().split("@@split@@")[0].trim()) == null) {
							List<Question> questionPerGroup = new ArrayList<>();
							questionPerGroup.add(cell);
							groupWiseQuestionsMap.put(cell.getQuestion().split("@@split@@")[0].trim(),
									questionPerGroup);
						} else {
							List<Question> questionPerGroup = groupWiseQuestionsMap
									.get(cell.getQuestion().split("@@split@@")[0].trim());
							questionPerGroup.add(cell);
							groupWiseQuestionsMap.put(cell.getQuestion().split("@@split@@")[0].trim(),
									questionPerGroup);
						}

					});

					List<Map<String, Object>> array = new LinkedList<>();
					groupWiseQuestionsMap.forEach((k, v) -> {
						List<Question> qs = v;
						Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
						jsonMap.put(question.getQuestion(), k);

						for (Question q : qs) {
							QuestionModel qModel = engineUtils.prepareQuestionModel(q);
							jsonMap.put(q.getQuestion().split("@@split@@")[1].trim(), qModel);
						}
						array.add(jsonMap);
					});
					questionModel.setTableModel(array);
				}

					break;

				case BEGIN_REPEAT:

				{
					questionModel = engineUtils.prepareQuestionModel(question);
					List<Question> beginRepeatQuestions = questionList.stream()
							.filter(qq -> qq.getParentColumnName().equals(question.getColumnName()))
							.collect(Collectors.toList());

					List<QuestionModel> model = new ArrayList<>();
					List<List<QuestionModel>> beginRepeatModel = new ArrayList<>();
					int arrayIndex = 0;

					for (int index = 0; index < beginRepeatQuestions.size(); index++) {

						Question q = beginRepeatQuestions.get(index);

						// if submission data is null we have to design an array
						// of array of question.

						QuestionModel qModel = engineUtils.prepareQuestionModel(q);
						switch (q.getControllerType().trim()) {
						case "dropdown":
						case "segment":
							qModel = engineUtils.setTypeDetailsAsOptions(qModel, typeDetailsMap, q, null, user,
									paramKeyValMap, session);
							break;
						}
						qModel = engineUtils.setParentColumnNameOfOptionTypeForBeginRepeat(qModel, question,
								questionMap, arrayIndex, index, model, beginRepeatQuestions);
						model.add(qModel);
					}
					beginRepeatModel.add(model);
					questionModel.setBeginRepeat(beginRepeatModel);
				}
					break;
				}

				/**
				 * making section name as key in section map, subsection name key in subsection
				 * map
				 * 
				 * subsection map is of two type here due to accordion
				 * 
				 * 1)if not accordian Map<String,List<QuestionModel>> ie one subsection have
				 * multiple questionmodel 2) if it is accordian type-
				 * Map<String,List<List<QuestionModel>>>
				 * 
				 * And finally adding in to one section against list<Subsection>
				 * 
				 */
				if (sectionMap.containsKey(question.getSection())) {

					if (subsectionMap.containsKey(question.getSubsection())) {

						@SuppressWarnings("unchecked")
						List<QuestionModel> list = (List<QuestionModel>) subsectionMap.get(question.getSubsection());
						if (questionModel != null)
							list.add(questionModel);
					} else {
						listOfQuestionModel = new LinkedList<>();
						if (questionModel != null)
							listOfQuestionModel.add(questionModel);
						subsectionMap.put(question.getSubsection(), listOfQuestionModel);
					}
				} else {
					subsectionMap = new LinkedHashMap<>();
					listOfQuestionModel = new ArrayList<>();
					if (questionModel != null)
						listOfQuestionModel.add(questionModel);
					subsectionMap.put(question.getSubsection(), listOfQuestionModel);
					sectionMap.put(question.getSection(), subsectionMap);
				}
			}
			/**
			 * adding list of subsection against a section.
			 */
			for (Map.Entry<String, Map<String, List<QuestionModel>>> entry : sectionMap.entrySet()) {
				if (mapOfSectionSubsectionListOfQuestionModel.containsKey(entry.getKey())) {
					mapOfSectionSubsectionListOfQuestionModel.get(entry.getKey()).add(entry.getValue());
				} else {
					mapOfSectionSubsectionListOfQuestionModel.put(entry.getKey(), Arrays.asList(entry.getValue()));
				}
			}
			mappedQuestionsMap.put(mapping.getForm().getFormId() + "_" + formMap.get(mapping.getForm().getFormId()),
					mapOfSectionSubsectionListOfQuestionModel);
		}
		return mappedQuestionsMap;
	}

	@Override
	public ReviewPageModel getAllForms(Map<String, Object> paramKeyValMap, HttpSession session, Object user,
			Integer roleId) {
		List<EnginesRoleFormMapping> mappings = engineRoleFormMappingRepository
				.findByRoleRoleIdAndAccessTypeAndFormStatus(roleId, AccessType.REVIEW, Status.ACTIVE);

		List<EnginesForm> forms = new ArrayList<>();
		Set<Integer> setOfForms = new HashSet<Integer>();
		mappings.forEach(v -> setOfForms.add(v.getForm().getFormId()));
		forms = formRepository.findByFormIdIn(setOfForms);

		ReviewPageModel pageModel = new ReviewPageModel();
		pageModel.setForms(forms);

		return pageModel;
	}

	@SuppressWarnings("unchecked")
	private Object getReviewDataMap(EnginesRoleFormMapping mapping, Object reviewDataMapObj, String type,
			Map<Integer, String> mapOfForms, Date startDate, Date endDate, Map<String, Object> paramKeyValMap,
			HttpSession session, Object user, Integer roleId) {

		List<DataObject> reviewData = new ArrayList<>();
		List<TypeDetail> typeDetails = typeDetailRepository.findByFormId(mapping.getForm().getFormId());

		List<Question> questionList = questionRepository
				.findAllByFormIdOrderByQuestionOrderAsc(mapping.getForm().getFormId());

		Map<Integer, TypeDetail> typeDetailsMap = typeDetails.stream()
				.collect(Collectors.toMap(TypeDetail::getSlugId, typeDe -> typeDe));

		Map<String, Question> questionMap = questionList.stream()
				.collect(Collectors.toMap(Question::getColumnName, question -> question));

		List<DataModel> submissionDatas = iDbFetchDataHandler.fetchDataFromDb(mapping, type, mapOfForms, startDate,
				endDate, paramKeyValMap, session, user);

		for (DataModel submissionData : submissionDatas) {

			DataObject dataObject = new DataObject();
			dataObject.setFormId(submissionData.getFormId());
			dataObject.setTime(new Timestamp(submissionData.getCreatedDate().getTime()));
			dataObject.setUsername(submissionData.getUserName());
			dataObject.setExtraKeys(submissionData.getExtraKeys());
			dataObject.setCreatedDate(dateFormat.format(submissionData.getCreatedDate()));
			dataObject.setUpdatedDate(dateFormat.format(submissionData.getUpdatedDate()));
			dataObject.setUniqueId(submissionData.getUniqueId());
			dataObject.setRejected(submissionData.isRejected());
			dataObject.setUniqueName(submissionData.getUniqueName());
			Map<String, List<Map<String, List<QuestionModel>>>> mapOfSectionSubsectionListOfQuestionModel = new LinkedHashMap<>();

			List<QuestionModel> listOfQuestionModel = new LinkedList<>();

			Map<String, Map<String, List<QuestionModel>>> sectionMap = new LinkedHashMap<String, Map<String, List<QuestionModel>>>();
			Map<String, List<QuestionModel>> subsectionMap = null;

			/**
			 * for accordion
			 */

			QuestionModel questionModel = null;

			for (Question question : questionList) {

				iDbReviewQueryHandler.setReviewHeaders(dataObject, question, typeDetailsMap, submissionData, type);

				questionModel = null;
				switch (question.getControllerType()) {
				case "heading":
					if (question.getParentColumnName().isEmpty()) {
						questionModel = engineUtils.prepareQuestionModel(question);
						questionModel = engineUtils.setValueForTextBoxFromExternal(question, questionModel,
								paramKeyValMap, session, user,submissionData);
					}
					break;

				case "Date Widget":
					if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {
						questionModel = engineUtils.prepareQuestionModel(question);
						if (String.class.cast(submissionData.getData().get(question.getColumnName())) != null) {
							String dt = getDateFromString(
									String.class.cast(submissionData.getData().get(question.getColumnName())));
							questionModel.setValue(dt);
						} else
							questionModel.setValue(null);
					}
					break;
				case "Time Widget":
					if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {
						questionModel = engineUtils.prepareQuestionModel(question);
						questionModel
								.setValue(String.class.cast(submissionData.getData().get(question.getColumnName())));
					}
					break;

				case "checkbox": {
					if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {

						questionModel = engineUtils.prepareQuestionModel(question);

						// setting model
						if (submissionData != null) {
							questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap, question,
									String.class.cast(submissionData.getData().get(question.getColumnName())), user,
									paramKeyValMap, session);
						} else {
							questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap, question,
									null, user, paramKeyValMap, session);
						}
					}
				}
					break;
				case "textbox":
				case "textarea": {
					if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {

						questionModel = engineUtils.prepareQuestionModel(question);
						questionModel = engineUtils.setValueForTextBoxFromExternal(question, questionModel,
								paramKeyValMap, session, user,submissionData);
						switch (question.getFieldType()) {

						case "singledecimal":
						case "doubledecimal":
						case "threedecimal":
							questionModel.setValue(submissionData.getData().get(question.getColumnName()) != null
									? String.valueOf(submissionData.getData().get(question.getColumnName()).toString())
									: null);
							break;

						case "tel":
							questionModel.setValue(submissionData.getData().get(question.getColumnName()) != null
									? String.valueOf(submissionData.getData().get(question.getColumnName()).toString()).trim()
									: null);

							break;
						default:
							questionModel.setValue(submissionData.getData().get(question.getColumnName()) != null
									? String.valueOf(submissionData.getData().get(question.getColumnName()).toString()).trim()
									: null);
							break;
						}

					}
				}
					break;

				case "dropdown":
				case "segment": {
					switch (question.getFieldType()) {
					case "checkbox":
						if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {

							questionModel = engineUtils.prepareQuestionModel(question);

							// setting model
							if (submissionData != null) {
								questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap,
										question,
										String.class.cast(submissionData.getData().get(question.getColumnName())), user,
										paramKeyValMap, session);
							} else {
								questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap,
										question, null, user, paramKeyValMap, session);
							}
						}
						break;
					default:
						if (question.getParentColumnName() == null || question.getParentColumnName().isEmpty()) {
							questionModel = engineUtils.prepareQuestionModel(question);
							questionModel = engineUtils.setTypeDetailsAsOptions(questionModel, typeDetailsMap, question,
									null, user, paramKeyValMap, session);

							questionModel
									.setValue(submissionData.getData().get(question.getColumnName()) != null
											? Integer.parseInt(
													submissionData.getData().get(question.getColumnName()).toString())
											: null);
						}
					}

				}
					break;
				case "table":
				case "tableWithRowWiseArithmetic": {
					questionModel = engineUtils.prepareQuestionModel(question);
					/**
					 * from table question id and cell parent id getting all matched cells here
					 */
					List<Question> tableCells = questionList.stream()
							.filter(q -> q.getParentColumnName().equals(question.getColumnName()))
							.collect(Collectors.toList());

					Map<String, List<Question>> groupWiseQuestionsMap = new LinkedHashMap<>();

					tableCells.forEach(cell -> {

						if (groupWiseQuestionsMap.get(cell.getQuestion().split("@@split@@")[0].trim()) == null) {
							List<Question> questionPerGroup = new ArrayList<>();
							questionPerGroup.add(cell);
							groupWiseQuestionsMap.put(cell.getQuestion().split("@@split@@")[0].trim(),
									questionPerGroup);
						} else {
							List<Question> questionPerGroup = groupWiseQuestionsMap
									.get(cell.getQuestion().split("@@split@@")[0].trim());
							questionPerGroup.add(cell);
							groupWiseQuestionsMap.put(cell.getQuestion().split("@@split@@")[0].trim(),
									questionPerGroup);
						}

					});

					List<Map<String, Object>> array = new LinkedList<>();
					Integer index = 0;
					for (Map.Entry<String, List<Question>> map : groupWiseQuestionsMap.entrySet()) {
						List<Question> qs = map.getValue();
						;
						Map<String, Object> jsonMap = new LinkedHashMap<String, Object>();
						jsonMap.put(question.getQuestion(), map.getKey());

						for (Question qdomain : qs) {
							QuestionModel qModel = engineUtils.prepareQuestionModel(qdomain);

							qModel.setValue(submissionData == null ? null
									: (List<Map<String, String>>) submissionData.getData()
											.get(question.getColumnName()) != null
													? String.class.cast(((List<Map<String, String>>) submissionData
															.getData().get(question.getColumnName())).get(index)
																	.get(qdomain.getColumnName()))
													: null);
							jsonMap.put(qdomain.getQuestion().split("@@split@@")[1].trim(), qModel);
						}
						index++;
						array.add(jsonMap);
					}

					questionModel.setTableModel(array);
				}
					break;

				case BEGIN_REPEAT: {
					questionModel = prepareBeginRepeatModelWithData(question, questionList, submissionData, questionMap,
							typeDetailsMap, paramKeyValMap, session, user);

				}

					break;
				}

				if (sectionMap.containsKey(question.getSection())) {

					if (subsectionMap.containsKey(question.getSubsection())) {

						/**
						 * checking the type of accordian here ie. RepeatSubSection()==no means not an
						 * accordian, and yes means accordian
						 */
						List<QuestionModel> list = (List<QuestionModel>) subsectionMap.get(question.getSubsection());
						if (questionModel != null)
							list.add(questionModel);

					} else {
						listOfQuestionModel = new LinkedList<>();
						if (questionModel != null)
							listOfQuestionModel.add(questionModel);
						subsectionMap.put(question.getSubsection(), listOfQuestionModel);
					}

				} else {
					subsectionMap = new LinkedHashMap<>();
					listOfQuestionModel = new ArrayList<>();
					if (questionModel != null)
						listOfQuestionModel.add(questionModel);
					subsectionMap.put(question.getSubsection(), listOfQuestionModel);

					sectionMap.put(question.getSection(), subsectionMap);
				}
			}

			/**
			 * adding list of subsection against a section.
			 */

			for (Map.Entry<String, Map<String, List<QuestionModel>>> entry : sectionMap.entrySet()) {

				if (mapOfSectionSubsectionListOfQuestionModel.containsKey(entry.getKey())) {
					mapOfSectionSubsectionListOfQuestionModel.get(entry.getKey()).add(entry.getValue());
				} else {
					mapOfSectionSubsectionListOfQuestionModel.put(entry.getKey(), Arrays.asList(entry.getValue()));
				}
			}
			dataObject.setFormData(mapOfSectionSubsectionListOfQuestionModel);
			reviewData.add(dataObject);
		}

		if ("dataReview".equals(type)) {
			Map<Integer, List<DataObject>> reviewDataMap = (Map<Integer, List<DataObject>>) reviewDataMapObj;
			reviewDataMap.put(mapping.getForm().getFormId(), reviewData);
			reviewDataMapObj = reviewDataMap;
		} else if ("rejectedData".equals(type) && !reviewData.isEmpty()) {
			Map<String, List<DataObject>> reviewDataMap = (Map<String, List<DataObject>>) reviewDataMapObj;
			reviewDataMap.put(mapping.getForm().getFormId() + "_" + mapOfForms.get(mapping.getForm().getFormId()),
					reviewData);
			reviewDataMapObj = reviewDataMap;
		}

		return reviewDataMapObj;
	}

	private String getDateFromString(String stringDate) {
		String dt = null;
		try {
			Date date = new SimpleDateFormat("dd-MM-yyyy").parse(stringDate);
			dt = new SimpleDateFormat("yyyy-MM-dd").format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<DataObject>> getRejectedData(Map<String, Object> paramKeyValMap, HttpSession session,
			Object user, Integer roleId) {

		Map<String, List<DataObject>> reviewDataMap = new HashMap<>();
		List<EnginesRoleFormMapping> mappings = engineRoleFormMappingRepository.findByRoleRoleIdAndAccessType(roleId,
				AccessType.DATA_ENTRY);

		List<EnginesForm> forms = new ArrayList<>();
		Set<Integer> setOfForms = new HashSet<Integer>();
		mappings.forEach(v -> setOfForms.add(v.getForm().getFormId()));
		forms = formRepository.findByFormIdIn(setOfForms);
		Map<Integer, String> mapOfForms = forms.stream()
				.collect(Collectors.toMap(EnginesForm::getFormId, EnginesForm::getName));

		for (EnginesRoleFormMapping mapping : mappings) {
			reviewDataMap = (Map<String, List<DataObject>>) getReviewDataMap(mapping, reviewDataMap, "rejectedData",
					mapOfForms, null, null, paramKeyValMap, session, user, roleId);
		}
		return reviewDataMap;
	}

	@SuppressWarnings("unchecked")
	@Override
	public ReviewPageModel getDataForReview(Integer formId, String sDate, String eDate,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user, Integer roleId) {

		try {
			sDate = sDate.concat(" 00:00:00");
			eDate = eDate.concat(" 23:59:59");

			Date startDate = sdfDateTimeWithSeconds.parse(sDate);

			Date endDate = sdfDateTimeWithSeconds.parse(eDate);

			Map<Integer, List<DataObject>> reviewDataMap = new HashMap<>();

			List<EnginesRoleFormMapping> mappings = engineRoleFormMappingRepository
					.findAllByRoleRoleIdAndFormFormIdAndAccessType(roleId, formId, AccessType.DATA_ENTRY);

			ReviewPageModel pageModel = new ReviewPageModel();
			for (EnginesRoleFormMapping mapping : mappings) {
				reviewDataMap = (Map<Integer, List<DataObject>>) getReviewDataMap(mapping, reviewDataMap, "dataReview",
						null, startDate, endDate, paramKeyValMap, session, user, roleId);
			}

			pageModel.setReviewDataMap(reviewDataMap);
			return pageModel;

		} catch (Exception e) {
			log.error("Error while getting Data for review with payload {},{},{}", formId, sDate, eDate, e);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 1.Start by checking if data for the begin repeat key exists in submission.If
	 * not available throw {@code}NullPointerException{@code}.
	 * <p>
	 * 2.For each begin-repeat according, treat it as a row in table and column as
	 * questions of table.
	 * <p>
	 * 3.Loop through the list of questions, retrieved from database, check if the
	 * data for the question exists or not.If not exists move to next question.
	 * <p>
	 * 4.For each question, loop each row of table that contains that
	 * question.Prepare the question model, set the value and options, etc and add
	 * the question to the rowIndex.
	 * 
	 * @param question
	 * @param questionList
	 * @param submissionData
	 * @param questionMap
	 * @param typeDetailsMap
	 * @param paramKeyValMap
	 * @param session
	 * @param user
	 * @return
	 */
	public QuestionModel prepareBeginRepeatModelWithData(Question question, List<Question> questionList,
			DataModel submissionData, Map<String, Question> questionMap, Map<Integer, TypeDetail> typeDetailsMap,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user) {

		QuestionModel questionModel = engineUtils.prepareQuestionModel(question);

		List<Question> beginRepeatQuestions = questionList.stream()
				.filter(qq -> qq.getParentColumnName().equals(question.getColumnName())).collect(Collectors.toList());

		List<QuestionModel> questionsInRow = new ArrayList<>();

		List<List<QuestionModel>> beginRepeatModel = new ArrayList<>();
		Map<Integer, List<QuestionModel>> rowInTable = new HashMap<>();

		Map<String, Object> data = submissionData.getData();

		if (data.get(question.getColumnName()) == null) {
			throw new NullPointerException("No data available for the begin repeat key :" + question.getColumnName());
		}
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> beginRepeatData = (List<Map<String, Object>>) data.get(question.getColumnName());

		for (int qIndex = 0; qIndex < beginRepeatQuestions.size(); qIndex++) {
			Question questionInRow = beginRepeatQuestions.get(qIndex);

			switch (questionInRow.getControllerType()) {
			default:

				if (beginRepeatData.get(0).get(questionInRow.getColumnName()) == null) {
					// in case the question is not found in data, due to fact that the question was
					// not there in blank form and was added later point of time into
					// checklist/form, skip that question and continue to next question.
					continue;
				}
				int rowIndexOfTable = 0;
				String checkedValue = null;

				for (Map<String, Object> rowData : beginRepeatData) {
					checkedValue = null;
					QuestionModel qModel = engineUtils.prepareQuestionModel(questionInRow);
					qModel = engineUtils.setParentColumnNameOfOptionTypeForBeginRepeatWithData(qModel, question,
							questionMap, rowIndexOfTable, qIndex, rowInTable, beginRepeatQuestions);

					switch (questionInRow.getControllerType().trim()) {
					case "Date Widget": {
						if (String.class.cast(rowData.get(questionInRow.getColumnName())) != null) {
							String date = getDateFromString(
									String.class.cast(rowData.get(questionInRow.getColumnName())));
							qModel.setValue(rowData.get(questionInRow.getColumnName()) != null ? date : null);
						} else {
							qModel.setValue(rowData.get(questionInRow.getColumnName()) != null
									? String.class.cast(rowData.get(questionInRow.getColumnName()))
									: null);
						}

					}
						break;
					case "Time Widget":
						qModel.setValue(rowData.get(questionInRow.getColumnName()) != null
								? String.class.cast(rowData.get(questionInRow.getColumnName()))
								: null);
						break;
					case "dropdown":
					case "segment":
						// we know, only single option is selected,
						// so we fetch the type details from
						// typeDetail Map and set value
						switch (questionInRow.getFieldType().trim()) {
						case "checkbox":
							if ((rowData.get(question.getColumnName()) == null ? null
									: rowData.get(questionInRow.getColumnName()).equals("") ? null
											: rowData.get(questionInRow.getColumnName()).toString()) != null) {

								String typeIdsCommaSeparated[] = String.class
										.cast(rowData.get(questionInRow.getColumnName()) != null
												? rowData.get(questionInRow.getColumnName())
												: "")
										.split(",");
								List<String> typeDetailsNames = new ArrayList<>();
								for (String typeId : typeIdsCommaSeparated) {
									TypeDetail t = typeDetailsMap.get(Integer.parseInt(typeId));
									typeDetailsNames.add(t.getName());
								}
								String fieldValue = String.join(",", typeDetailsNames);
								checkedValue = fieldValue;

								qModel.setValue(checkedValue);
								qModel = engineUtils.setTypeDetailsAsOptions(qModel, typeDetailsMap, questionInRow,
										checkedValue, user, paramKeyValMap, session);
							}

							break;
						default:
							qModel = engineUtils.setTypeDetailsAsOptions(qModel, typeDetailsMap, questionInRow, null,
									user, paramKeyValMap, session);
							qModel.setValue(rowData.get(questionInRow.getColumnName()) != null
									? Integer.class.cast(rowData.get(questionInRow.getColumnName()))
									: null);
						}

						break;
					case "textbox":
					case "textarea":
						// We return the captured value as string
						switch (question.getFieldType()) {
						case "tel":
							qModel.setValue(rowData.get(questionInRow.getColumnName()) != null
									? Integer.parseInt(rowData.get(questionInRow.getColumnName()).toString())
									: null);
							break;
						default:
							qModel.setValue(rowData.get(questionInRow.getColumnName()) != null
									? String.class.cast(rowData.get(questionInRow.getColumnName()))
									: null);
							break;
						}
					}

					if (rowInTable.get(rowIndexOfTable) == null) {
						questionsInRow = new ArrayList<>();
						questionsInRow.add(qModel);
						rowInTable.put(rowIndexOfTable, questionsInRow);
					} else {
						questionsInRow = rowInTable.get(rowIndexOfTable);
						questionsInRow.add(qModel);
						rowInTable.put(rowIndexOfTable, questionsInRow);
					}
					rowIndexOfTable++;
				}
				break;
			}
		}
		if (rowInTable == null || rowInTable.isEmpty()) {
			beginRepeatModel.add(questionsInRow);
		} else {
			rowInTable.forEach((k, v) -> {
				beginRepeatModel.add(v);
			});
		}
		questionModel.setBeginRepeat(beginRepeatModel);
		return questionModel;
	}
}