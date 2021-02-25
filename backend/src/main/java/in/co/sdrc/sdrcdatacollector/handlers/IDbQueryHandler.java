package in.co.sdrc.sdrcdatacollector.handlers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.OptionModel;
import in.co.sdrc.sdrcdatacollector.models.QuestionModel;

public interface IDbQueryHandler {

	public List<OptionModel> getOptions(QuestionModel questionModel, Map<Integer, TypeDetail> typeDetailsMap,
			Question question, String checkedValue, Object user);
	
	
	public String getDropDownValueForRawData(String tableName,Integer dropdownId);


	public QuestionModel setValueForTextBoxFromExternal(QuestionModel qModel, Question question,
			Map<String, Object> paramKeyValMap, HttpSession session, Object user, Object submission);


	
}
