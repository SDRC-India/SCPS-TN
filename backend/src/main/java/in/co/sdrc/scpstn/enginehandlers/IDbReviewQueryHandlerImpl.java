package in.co.sdrc.scpstn.enginehandlers;

import java.util.Map;

import org.springframework.stereotype.Component;

import in.co.sdrc.sdrcdatacollector.handlers.IDbReviewQueryHandler;
import in.co.sdrc.sdrcdatacollector.jpadomains.Question;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;
import in.co.sdrc.sdrcdatacollector.models.DataModel;
import in.co.sdrc.sdrcdatacollector.models.DataObject;
import in.co.sdrc.sdrcdatacollector.models.ReviewHeader;

/**
 * @author Debiprasad Parida (debiprasad@sdrc.co.in)
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Component
public class IDbReviewQueryHandlerImpl implements IDbReviewQueryHandler {

	@Override
	public DataObject setReviewHeaders(DataObject dataObject, Question question,
			Map<Integer, TypeDetail> typeDetailsMap, DataModel submissionData, String type) {

		if(question.getReviewHeader()!=null && question.getReviewHeader().trim().length() > 0){
			ReviewHeader header=new ReviewHeader();
			switch(question.getReviewHeader().split("_")[0]){
			case "L4":
			case "L1":
			case "L2":
			case "L3":
				switch(question.getControllerType()) {
				case "dropdown":
					if(question.getControllerType().equals("dropdown") && (question.getTableName()==null ||question.getTableName().equals(""))){
						header=new ReviewHeader();
						header.setName(question.getReviewHeader());
						header.setValue(typeDetailsMap.get(submissionData.getData().get(question.getColumnName())).toString());
				
						dataObject.getFormDataHead().put(header.getName(), header.getValue());

					}
						else if(question.getTableName()!=null && question.getTableName().trim().length() > 0){

						}
					break;
				case "textbox":
					header=new ReviewHeader();
					header.setName(question.getReviewHeader());
					header.setValue(submissionData.getData().get(question.getColumnName())!=null?submissionData.getData().get(question.getColumnName()).toString():null);

					dataObject.getFormDataHead().put(header.getName(), header.getValue());

					
				}
				break;
			}
		}
		
		return dataObject;
	}
}
