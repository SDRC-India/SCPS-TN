package in.co.sdrc.sdrcdatacollector.jparepositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.co.sdrc.sdrcdatacollector.jpadomains.EnginesRoleFormMapping;
import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;

@Repository
public interface EngineRoleFormMappingRepository extends JpaRepository<EnginesRoleFormMapping, Integer> {

	List<EnginesRoleFormMapping> findByRoleRoleId(Integer roleId);

	List<EnginesRoleFormMapping> findByRoleRoleIdAndAccessType(Integer integer, AccessType accessType);

	List<EnginesRoleFormMapping> findAllByRoleRoleIdAndFormFormIdAndAccessType(Integer roleId, Integer formId,AccessType review);

	EnginesRoleFormMapping findByRoleRoleIdAndFormFormIdAndAccessType(Integer roleId, Integer formId,
			AccessType review);

	EnginesRoleFormMapping findByRoleRoleIdAndFormFormIdAndAccessTypeAndStatus(Integer roleId, Integer formId,
			AccessType dataEntry, Status active);

	List<EnginesRoleFormMapping> findByRoleRoleIdAndAccessTypeAndStatus(Integer roleId, AccessType dataEntry,
			Status active);

	List<EnginesRoleFormMapping> findByRoleRoleIdAndAccessTypeAndFormStatus(Integer roleId, AccessType review,
			Status active);

	List<EnginesRoleFormMapping> findByRoleRoleIdInAndAccessTypeAndStatus(List<Integer> roleId,AccessType downloadRawData, Status active);

	List<EnginesRoleFormMapping> findByRoleRoleIdInAndAccessTypeAndStatus(Set<Object> roleId,
			AccessType downloadRawData, Status active);

}
