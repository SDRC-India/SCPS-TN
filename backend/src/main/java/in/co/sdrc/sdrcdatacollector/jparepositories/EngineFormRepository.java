package in.co.sdrc.sdrcdatacollector.jparepositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesForm;
import in.co.sdrc.sdrcdatacollector.util.Status;

public interface EngineFormRepository extends JpaRepository<EnginesForm, Integer>{

	EnginesForm findByName(String string);

	List<EnginesForm> findByFormIdIn(Set<Integer> setOfForms);

	EnginesForm findByNameAndFormId(String trimWhitespace, Integer valueOf);

	EnginesForm findByNameAndFormIdAndStatus(String trimWhitespace, Integer valueOf, Status active);

	

}
