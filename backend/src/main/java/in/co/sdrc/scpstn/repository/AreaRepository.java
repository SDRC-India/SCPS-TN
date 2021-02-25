package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.scpstn.domain.Area;

@RepositoryDefinition(domainClass = Area.class, idClass=Integer.class)
public interface AreaRepository {
	
	public List<Area> findAll();
	
	public List<Area> findAllByIsLiveTrue();
	
	public Area findByAreaId(int areaId);
	
	List<Area> findAllByParentAreaId(Integer stateId);

//	public List<Area> findCountryAndStateByStateId(int stateId);

	public Area findByAreaCode(String areaCode);

	public Area findByAreaNameAndParentAreaId(String districtName, int parentAreaId);

	public Area findByAreaName(String districtName);

	public Area findByAreaLevelAreaLevelName(String string);

	@Query(value="SELECT * FROM area a where a.area_level_id_fk=1 UNION SELECT * FROM area b where b.parent_area_id=:stateId ",nativeQuery=true)
	public List<Area> findCountryAndStateByStateId(@Param("stateId")int stateId);
	
	@Query("SELECT ar FROM Area ar WHERE ar.areaLevel.areaLevelId <= :childLevel AND ar.areaLevel.areaLevelId >=   "
			+ "(SELECT parArea.areaLevel.areaLevelId FROM Area parArea WHERE parArea.areaCode = :areaId)")
	public Area[] getAreaNid(@Param("areaId") String areaCode,
			@Param("childLevel") Integer childLevel);

	public List<Area> findAllByZone(String string);


	public List<Area> findAllByIsLiveTrueOrderByAreaNameAsc();
}
