/**
 * 
 */
package in.co.sdrc.sdrcdatacollector.jparepositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.sdrcdatacollector.jpadomains.Type;
import in.co.sdrc.sdrcdatacollector.jpadomains.TypeDetail;

/**
 * @author Subham Ashish(subham@sdrc.co.in) Created Date:26-Jun-2018 2:28:53 PM
 */
public interface TypeDetailRepository extends JpaRepository<TypeDetail, Integer>{

	TypeDetail findBySlugId(Integer slugId);

	List<TypeDetail> findByTypeSlugId(Integer slugId); 

	List<TypeDetail> findBySlugIdIn(List<Integer> listOfSlugId);

	TypeDetail findByTypeAndNameAndFormId(Type type, String trim, int formId);

	List<TypeDetail> findByFormId(Integer formId);

	List<TypeDetail> findByTypeAndFormIdAndNameIn(Type type, int formId, String[] typeDetailNames);
}
