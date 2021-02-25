package in.co.sdrc.sdrcdatacollector.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.sdrcdatacollector.jpadomains.Type;

/**
 * @author Subham Ashish(subham@sdrc.co.in)
 * Created Date:26-Jun-2018 2:22:46 PM
 */
public interface TypeRepository extends JpaRepository<Type, Integer>{

	Type findByTypeName(String typeName);

	Type findByTypeNameAndFormId(String trim,Integer formId);

	List<Type> findAllByFormId(int formId);
	

}
