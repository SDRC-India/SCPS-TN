package in.co.sdrc.sdrcdatacollector.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.sdrcdatacollector.jpadomains.Question;

/**
 * @author Ashutosh Dang(ashutosh@sdrc.co.in) Created Date : 26-Jun-2018 8:38:34 pm
 *        
 */

public interface QuestionRepository extends JpaRepository<Question, Integer>{

	// List<Question> findAllOrderByQuestionIdAsc();

		List<Question> findAllByOrderByQuestionOrderAsc();

		List<Question> findAllByFormIdOrderByQuestionOrderAsc(Integer formId);

		List<Question> findByFormId(Integer formId);

//		List<Question> findAllByFormIdOrderByQuestionOrderAscActiveTrue(Integer formId);

		List<Question> findAllByFormIdAndActiveTrueOrderByQuestionOrderAsc(Integer formId);

		List<Question> findAllByFormId(Integer formId);

		Question findAllByFormIdAndColumnName(Integer formId, String stringCellValue);

		List<Question> findAllByActiveTrueOrderByQuestionOrderAsc();
}
