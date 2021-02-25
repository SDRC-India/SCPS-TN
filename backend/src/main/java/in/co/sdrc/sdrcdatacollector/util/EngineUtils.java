package in.co.sdrc.sdrcdatacollector.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import in.co.sdrc.sdrcdatacollector.handlers.IDbQueryHandler;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.OptionModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

@Component
public class EngineUtils {

	@Autowired(required = false)
	MongoTemplate mongoTemplate;

	@Autowired(required = false)
	JdbcTemplate jdbcTemplate;

	// @Autowired
	// IDataSourceBuilder iDataSourceBuilder;

	@Autowired
	private IDbQueryHandler iDbQueryHandler;

	/**
	 * This method copies all properties from question domain to question model
	 * 
	 * @param question
	 *            {@code} Question
	 * @return {@code} QuestionModel
	 */
	public QuestionModel prepareQuestionModel(Question question) {

		QuestionModel questionModel = new QuestionModel();
		questionModel.setColumnName(question.getColumnName());
		questionModel.setType(question.getFieldType());
		questionModel.setLabel(question.getQuestion());
		questionModel.setKey(question.getQuestionId());
		questionModel.setType(question.getFieldType());
		questionModel.setControlType(question.getControllerType());
		questionModel.setQuestionOrder(question.getQuestionOrder());
		questionModel.setFormId(question.getFormId());
		questionModel.setParentColumnName(question.getParentColumnName());
		questionModel.setSaveMandatory(question.getSaveMandatory());
		questionModel.setFinalizeMandatory(question.getFinalizeMandatory());
		questionModel.setRelevance(question.getRelevance());
		questionModel.setConstraints(question.getConstraints());
		questionModel.setFeatures(question.getFeatures());
		questionModel.setSaveMandatory(question.getSaveMandatory());
		questionModel.setDefaultSettings(question.getDefaultSettings());
		questionModel.setDisplayScore(question.getDisplayScore());
		questionModel.setTableName(question.getTableName());
		questionModel.setFileExtensions(question.getFileExtensions());
		questionModel.setScoreExp(question.getScoreExp());
		questionModel.setReviewHeader(question.getReviewHeader());
		questionModel.setLanguages(question.getLanguages());
		questionModel.setCmsg(question.getCmsg());
		return questionModel;
	}

	/**
	 * For each of the question that belongs to begin repeat (means parent column
	 * value as that of begin repeat questions column name), we have to rename
	 * column names of question.
	 * <p>
	 * If the begin repeat column name is <b>f1bgrepeat</b> and its child question
	 * has column name <b>f1q25</b>, then we rename the child questions column name
	 * in following way :
	 * <p>
	 * 1. We can relate this begin repeat accordion to a row in table, and questions
	 * as columns of a table.
	 * <p>
	 * 2. So each question will have a row index and column index.
	 * <p>
	 * 3. Note (Since, its only fetching blank checklist , only one accordion will
	 * be presented in the view layer, so we assign a row index of 0 to begin repeat
	 * accordion.
	 * <p>
	 * 4. Now each question belonging to begin repeat will be assigned a column
	 * index.We take the column index default to index of list, that was retrieved
	 * from database.
	 * <p>
	 * 5. If the row index is 0 and column index of question is 1, the question name
	 * is renamed in following way:
	 * <p>
	 * <b>{begin_repeat_column_name}-{row-index}-{column-index}-{column name}.</b>
	 * <p>
	 * Renaming the question will be <b>f1bgrepeat-0-1-f1q25</b>
	 * 
	 *
	 * @param questionModel
	 *            question model of the begin repeat question (question in column)
	 * @param beginRepeatQuestion
	 *            question domain of the begin repeat question (question in column)
	 * @param questionMap
	 *            This is map containing question's column name as key and question
	 *            domain as value
	 * @param arrayIndex
	 *            The row index of accordion
	 * @param internalIndex
	 *            The column index of the question
	 * @param bgQuestionsModelList
	 *            List of question models of begin repeat questions.
	 * @param beginRepeatQuestions
	 *            List of question domains of begin repeat questions.
	 * @return QuestionModel Renames the model's column names, column names in
	 *         relevance, constraints and score expressions.
	 */
	public QuestionModel setParentColumnNameOfOptionTypeForBeginRepeat(QuestionModel questionModel,
			Question beginRepeatQuestion, Map<String, Question> questionMap, Integer arrayIndex, Integer internalIndex,
			List<QuestionModel> bgQuestionsModelList, List<Question> beginRepeatQuestions) {

		questionModel.setColumnName(beginRepeatQuestion.getColumnName().concat("-").concat(String.valueOf(arrayIndex))
				.concat("-").concat(String.valueOf(internalIndex)).concat("-").concat(questionModel.getColumnName()));

		
		
		for (QuestionModel bgOuter : bgQuestionsModelList) {

			if (questionModel.getRelevance() != null
					&& questionMap.get(bgOuter.getColumnName().split("-")[3]).getParentColumnName() != null)
				questionModel.setRelevance(questionModel.getRelevance().replace(bgOuter.getColumnName().split("-")[3] + ":",
						bgOuter.getColumnName() + ":"));

			for (QuestionModel bgInner : bgQuestionsModelList) {
				if (bgInner.getFeatures() != null) {
					for (String feature : bgInner.getFeatures().split("@AND")) {
						switch (feature.split(":")[0]) {
						case "exp": {

						}
							break;
						case "date_sync": {
							String newString = "date_sync:";
							for (String colName : feature.split(":")[1].split("&")) {
								if (bgInner.getParentColumnName() != null && bgOuter.getColumnName().split("-")[3].equals(colName))
									newString = newString + bgOuter.getColumnName() + "&";
								else
									newString = newString + colName + "&";
							}
							newString = newString.substring(0, newString.length() - 1);
							bgInner.setFeatures(bgInner.getFeatures().replace(feature, newString));

						}
							break;
						case "area_group":
						case "filter_single":
						case "filter_multiple": {
							String colName = feature.split(":")[1];
							if (bgInner.getParentColumnName() != null && bgOuter.getColumnName().split("-")[3].equals(colName))
								bgInner.setFeatures(questionModel.getFeatures().replace(feature,
										feature + ":" + bgOuter.getColumnName()));
						}
							break;
						}
					}
				}
			}
			if (questionModel.getConstraints() != null) {
				String renamedContraints = renameColumnNameInExpressionForBeginRepeatQuestions(
						questionModel.getConstraints(), arrayIndex, questionModel.getParentColumnName(), questionMap,
						beginRepeatQuestions);
				questionModel.setConstraints(renamedContraints);

			}
			if (questionModel.getScoreExp() != null) {
				String scoreExp = renameColumnNameInExpressionForBeginRepeatQuestions(questionModel.getScoreExp(),
						arrayIndex, questionModel.getParentColumnName(), questionMap, beginRepeatQuestions);
				questionModel.setScoreExp(scoreExp);

			}
		}

		return questionModel;
	}

	public QuestionModel setParentColumnNameOfOptionTypeForBeginRepeatWithData(QuestionModel questionModel,
			Question beginRepeatQuestion, Map<String, Question> questionMap, Integer arrayIndex, Integer internalIndex,
			Map<Integer, List<QuestionModel>> bgMapModel,List<Question> beginRepeatQuestions) {

		questionModel.setColumnName(beginRepeatQuestion.getColumnName().concat("-").concat(String.valueOf(arrayIndex))
				.concat("-").concat(String.valueOf(internalIndex)).concat("-").concat(questionModel.getColumnName()));

		List<QuestionModel> qs = bgMapModel.getOrDefault(arrayIndex, null);
		if (qs != null)
			for (QuestionModel bgOuter : qs) {

				if (questionModel.getRelevance() != null
						&& questionMap.get(bgOuter.getColumnName().split("-")[3]).getParentColumnName() != null) {
					questionModel.setRelevance(questionModel.getRelevance().replaceAll(" ", ""));
					questionModel.setRelevance(questionModel.getRelevance()
							.replaceAll(bgOuter.getColumnName().split("-")[3] + ":", bgOuter.getColumnName() + ":"));
				}
					

				for (QuestionModel bgInner : qs) {
					if (bgInner.getFeatures() != null) {
						for (String feature : bgInner.getFeatures().split("@AND")) {
							switch (feature.split(":")[0]) {
							case "exp": {

							}
								break;
							case "date_sync": {
								String newString = "date_sync:";
								for (String colName : feature.split(":")[1].split("&")) {
									if (bgInner.getParentColumnName() != null
											&& bgOuter.getColumnName().split("-")[3].equals(colName))
										newString = newString + bgOuter.getColumnName() + "&";
									else
										newString = newString + colName + "&";
								}
								newString = newString.substring(0, newString.length() - 1);
								bgInner.setFeatures(bgInner.getFeatures().replace(feature, newString));

							}
								break;
							case "area_group":
							case "filter_single":
							case "filter_multiple": {
								String colName = feature.split(":")[1];
								if (bgInner.getParentColumnName() != null && bgOuter.getColumnName().split("-")[3].equals(colName))
									bgInner.setFeatures(questionModel.getFeatures().replace(feature,
											feature + ":" + bgOuter.getColumnName()));
							}
								break;
							}
						}
					}
				}
				if (questionModel.getConstraints() != null) {
					String renamedContraints = renameColumnNameInExpressionForBeginRepeatQuestions(
							questionModel.getConstraints(), arrayIndex, questionModel.getParentColumnName(), questionMap,
							beginRepeatQuestions);
					questionModel.setConstraints(renamedContraints);

				}
				if (questionModel.getScoreExp() != null) {
					String scoreExp = renameColumnNameInExpressionForBeginRepeatQuestions(questionModel.getScoreExp(),
							arrayIndex, questionModel.getParentColumnName(), questionMap, beginRepeatQuestions);
					questionModel.setScoreExp(scoreExp);

				}
			}

		return questionModel;
	}

	public QuestionModel setTypeDetailsAsOptions(QuestionModel questionModel, Map<Integer, TypeDetail> typeDetailsMap,
			Question question, String checkedValue, Object user, Map<String, Object> paramKeyValMap,
			HttpSession session) {

		if (questionModel.getFeatures() == null || (!questionModel.getFeatures().contains("fetch_from_query")
				&& !questionModel.getFeatures().contains("fetch_tables"))) {
			if (question.getTypeId() == null) {
				throw new NullPointerException("Cannot set options as type id is null, question="
						+ question.getQuestion() + ", column name =" + question.getColumnName());
			}
			questionModel.setTypeId(question.getTypeId().getSlugId());

			List<TypeDetail> typedetails = typeDetailsMap.values().stream()
					.filter(t -> (question.getTypeId() != null
							&& (t.getType().getSlugId() == question.getTypeId().getSlugId())))
					.collect(Collectors.toList());

			List<OptionModel> listOfOptions = new ArrayList<>();

			for (TypeDetail typeDetail : typedetails) {

				OptionModel optionModel = new OptionModel();
				optionModel.setKey(typeDetail.getSlugId());
				optionModel.setValue(typeDetail.getName());
				optionModel.setOrder(typeDetail.getOrderLevel());
				optionModel.setScore(typeDetail.getScore());
				optionModel.setParentIds(typeDetail.getParentIds().stream().map(t -> t.getSlugId()).collect(Collectors.toList()));
				if (checkedValue != null) {
					for (String check : checkedValue.split(",")) {
						if (check.equals(String.valueOf(typeDetail.getSlugId()))) {
							optionModel.setIsSelected(true);
						}
					}
				}
				listOfOptions.add(optionModel);
			}
			questionModel.setOptions(listOfOptions);
		} else {

			if (question.getFeatures() != null && !StringUtils.isEmpty(question.getFeatures())) {
				String features[] = question.getFeatures().split("@AND");
				for (String feature : features) {
					switch (StringUtils.trimWhitespace(feature).split(":")[0]) {
					case "fetch_from_query": {
						setOptionsForDropdown(question.getQuery(), question, questionModel, paramKeyValMap, session,
								user);
					}
						break;
					case "fetch_tables": {
						List<OptionModel> options = iDbQueryHandler.getOptions(questionModel, typeDetailsMap, question,
								checkedValue, user);
						questionModel.setOptions(options);
					}
						break;
					}

				}

			}

		}

		return questionModel;

	}

	public QuestionModel setOptionsForDropdown(String query, Question question, QuestionModel qModel,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user) {

		String datasourceType = StringUtils.trimWhitespace(query.split("@AND@")[0].split(":")[1]);
		List<OptionModel> options = new ArrayList<>();
		switch (datasourceType) {
		case "SQL": {
			String dbquery = query.split("@AND@")[1].split(":")[1];
			String dbparams[] = query.split("@AND@")[2].split(":")[1].split(",");
			String dbparamsFrom = query.split("@AND@")[3].split(":")[1];
			Connection connection = null;
			PreparedStatement psmt = null;
			ResultSet rs = null;
			try {
				connection = jdbcTemplate.getDataSource().getConnection();
				psmt = connection.prepareStatement(dbquery);
				if (dbparams != null && dbparams.length > 0) {
					int pindex = 0;
					for (String param : dbparams) {
						String keyName = StringUtils.trimWhitespace(param.split("\\$")[0]);
						switch (StringUtils.trimWhitespace(param.split("\\$")[1])) {
						case "Int":
							switch (dbparamsFrom) {
							case "MAP":
								psmt.setInt(++pindex, (Integer) paramKeyValMap.get(keyName));
								break;
							case "SESSION":
								psmt.setInt(++pindex, (Integer) session.getAttribute(keyName));
								break;
							}
							break;
						case "String":
							switch (dbparamsFrom) {
							case "MAP":
								psmt.setString(++pindex, paramKeyValMap.get(keyName).toString());
								break;
							case "SESSION":
								psmt.setString(++pindex, session.getAttribute(keyName).toString());
								break;
							}
							break;
						}
					}
				}
				rs = psmt.executeQuery();
				int size = 0;
				while (rs != null && rs.next()) {
					OptionModel option = new OptionModel();
					option.setKey(rs.getInt("key"));
					option.setValue(rs.getString("value"));
					option.setParentId(rs.getInt("parent_id"));
					option.setLevel(rs.getInt("level"));
					option.setVisible(rs.getBoolean("visible"));
					option.setOrder(++size);
					options.add(option);
				}
			} catch (SQLException e) {
				throw new RuntimeException(
						"SQL query for question id :" + question.getSlugId() + " for query :" + query, e);
			} finally {
				if (psmt != null) {
					try {
						psmt.close();
					} catch (SQLException e) {
						throw new RuntimeException("Unable to close PreparedStatement SQL query for question id :"
								+ question.getSlugId() + " for query :" + query, e);
					}
				}
				if (connection != null) {
					try {
						connection.close();
					} catch (SQLException e) {
						throw new RuntimeException("Unable to close SQL Connection for question id :"
								+ question.getSlugId() + " for query :" + query, e);
					}
				}
			}
		}
			break;
		case "MONGO": {
			// query = "{$db}:MONGO @AND@ {$colName}:area @AND@
			// {$query}=parentAreaId:$eq:findAll @AND@ {$PARAMS}:BLOCK_ID$Int @AND@
			// {$PARAM_FROM}:MAP @AND@
			// {$keys}:key=areaId,value=areaName,parentId=parentAreaId";

			String collectionName = StringUtils.trimWhitespace(query.split("@AND@")[1].split(":")[1]);
			String[] dbquerys = query.split("@AND@")[2].split("=")[1].split(",");
			String dbparams[] = query.split("@AND@")[3].split(":")[1].split(",");
			String dbparamsFrom = query.split("@AND@")[4].split(":")[1].trim();
			String mongoKeyMapper = query.split("@AND@")[5].split(":")[1];
			Map<String, String> mongoKeyMapperMap = Arrays.asList(mongoKeyMapper.split(",")).stream()
					.collect(Collectors.toMap(t -> t.toString().split("=")[0], t -> t.toString().split("=")[1]));

			int paramIndex = 0;
			DBCursor cursor = null;
			DBCollection collection = mongoTemplate.getCollection(collectionName);

			BasicDBObject basicDbObject = new BasicDBObject();
			if (dbquerys.length == 1 && dbquerys[0].equals("findAll")) {
				collection.find().toArray();
				cursor = collection.find();
			} else {
				for (String dbquery : dbquerys) {
					String key = dbquery.split(":")[0];
					String operator = dbquery.split(":")[1];
					String keyName = StringUtils.trimWhitespace(dbparams[paramIndex].split("\\$")[0]);
					String type = StringUtils.trimWhitespace(dbparams[paramIndex].split("\\$")[1]);
					switch (type) {
					case "Int":
						switch (dbparamsFrom) {
						case "MAP":
							basicDbObject.append(key, new BasicDBObject(operator,
									Integer.valueOf(paramKeyValMap.get(keyName).toString())));
							break;
						case "SESSION":
							basicDbObject.append(key, new BasicDBObject(operator,
									Integer.valueOf(session.getAttribute(keyName).toString())));
							break;
						}
						break;
					}

				}
				cursor = collection.find(basicDbObject);

			}
			int size = 0;
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				OptionModel option = new OptionModel();
				option.setKey((Integer) obj.get(mongoKeyMapperMap.get("key")));
				option.setValue(obj.get(mongoKeyMapperMap.get("value")).toString());
				option.setParentId(mongoKeyMapperMap.get("parentId") == null ? null
						: Integer.valueOf(obj.get(mongoKeyMapperMap.get("parentId")).toString()));
				option.setLevel(mongoKeyMapperMap.get("level") == null ? null
						: Integer.valueOf(obj.get(mongoKeyMapperMap.get("level")).toString()));
				option.setVisible(mongoKeyMapperMap.get("visible") == null ? false
						: Boolean.valueOf(obj.get(mongoKeyMapperMap.get("level")).toString()));
				option.setOrder(++size);
				options.add(option);
			}
		}

			break;
		}

		qModel.setOptions(options);
		return qModel;
	}

	/**
	 * 
	 * @param question
	 * @param qModel
	 * @param paramKeyValMap
	 * @param session
	 * @param user
	 * @return
	 */
	public QuestionModel setValueForTextBoxFromExternal(Question question, QuestionModel qModel,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user, Object submission) {

		if (question.getFeatures() != null && !StringUtils.isEmpty(question.getFeatures())) {
			String features[] = question.getFeatures().split("@AND");
			for (String feature : features) {
				switch (StringUtils.trimWhitespace(feature).split(":")[0]) {
				case "fetch_from_external": {
					return iDbQueryHandler.setValueForTextBoxFromExternal(qModel, question, paramKeyValMap, session,
							user,submission);
				}
				}
			}
		}
		return qModel;

	}

	public String renameColumnNameInExpressionForBeginRepeatQuestions(String expression, Integer arrayIndex,
			String beginRepeatParentColumnName, Map<String, Question> questionMap,
			List<Question> beginRepeatQuestions) {

		if (expression == null || expression.isEmpty()) {
			return expression;
		}
		String reformedExpression = "";
		for (int i = 0; i < expression.split("").length; i++) {
			if (i < expression.length() - 1 && expression.charAt(i) == '$' && expression.charAt(i + 1) == '{') {
				String columnName = "";
				for (int j = i + 2; j < expression.split("").length; j++) {
					if (expression.charAt(j) == '}') {
						i = j;
						break;
					}
					columnName = columnName + expression.charAt(j);
				}
				final String cName = columnName;
				Optional<Question> bgQuestion = beginRepeatQuestions.stream()
						.filter(qq -> (qq.getColumnName().equals(cName))).findFirst();

				if (!bgQuestion.isPresent() && questionMap.get(cName) != null
						&& questionMap.get(cName).getParentColumnName() != null) {
					throw new IllegalArgumentException(
							"Column " + cName + " cannot be found on expression as question. Expression " + expression);
				}
				String columnNameInBeginRepeatIndexStyle = "";
				if (bgQuestion.isPresent()) {
					Integer indexOfQuestion = beginRepeatQuestions.indexOf(bgQuestion.get());
					columnNameInBeginRepeatIndexStyle = beginRepeatParentColumnName.concat("-")
							.concat(String.valueOf(arrayIndex)).concat("-").concat(String.valueOf(indexOfQuestion))
							.concat("-").concat(columnName);
				} else {
					columnNameInBeginRepeatIndexStyle = cName;
				}
				reformedExpression = reformedExpression + "${" + columnNameInBeginRepeatIndexStyle + "}";
			} else {
				reformedExpression = reformedExpression + expression.charAt(i);
			}
		}
		return reformedExpression;
	}
}
