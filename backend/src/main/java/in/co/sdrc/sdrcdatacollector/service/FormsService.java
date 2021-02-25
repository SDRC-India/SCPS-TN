package in.co.sdrc.sdrcdatacollector.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;
import in.co.sdrc.sdrcdatacollector.models.ReviewPageModel;

/**
 * @author Azharuddin Created Date:27-Sept-2018 11:46:35 AM
 */
public interface FormsService {

	Map<String, Map<String, List<Map<String, List<QuestionModel>>>>> getQuestions(Map<String, Object> paramKeyValMap,
			HttpSession session, Object userModel, Integer roleId);

	Map<String, List<DataObject>> getRejectedData(Map<String, Object> paramKeyValMap, HttpSession session, Object user,
			Integer roleId);

	ReviewPageModel getAllForms(Map<String, Object> paramKeyValMap, HttpSession session, Object user, Integer roleId);

	ReviewPageModel getDataForReview(Integer formId, String sDate, String eDate, Map<String, Object> paramKeyValMap,
			HttpSession session, Object user, Integer roleId);


}
