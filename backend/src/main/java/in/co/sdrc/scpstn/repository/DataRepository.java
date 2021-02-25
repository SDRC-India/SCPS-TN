package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Data;
import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.domain.IndicatorUnitSubgroup;
import in.co.sdrc.scpstn.domain.Subgroup;
import in.co.sdrc.scpstn.domain.Timeperiod;
import in.co.sdrc.scpstn.domain.Unit;

@RepositoryDefinition(domainClass = Data.class, idClass = Integer.class)
public interface DataRepository extends JpaRepository<Data, Integer> {

	Data findByIndicatorUnitSubgroupAndSourceAndTimePeriodAndArea(IndicatorUnitSubgroup ius,
			IndicatorClassification source, Timeperiod timeperiod, Area district);

	List<Data> findByIndicatorAndUnitAndSubgroupAndSourceAndTimePeriodOrderByPercentageAsc(Indicator indicator,
			Unit percentageUnit, Subgroup subgroup, IndicatorClassification parent, Timeperiod timeperiod);

	List<Data> findByIndicatorAndSourceAndTimePeriod(Indicator indicator, IndicatorClassification source,
			Timeperiod timeperiod);

	Data findByIndicatorAndSourceAndTimePeriodAndArea(Indicator indicator, IndicatorClassification source,
			Timeperiod timeperiod, Area state);

	List<Data> findByIndicatorAndSourceAndTimePeriodOrderByPercentageDesc(Indicator indicator,
			IndicatorClassification source, Timeperiod latestTp);

	List<Data> findByIndicatorInAndSourceAndTimePeriodOrderByPercentageDesc(List<Indicator> indicators,
			IndicatorClassification source, Timeperiod latestTp);

	List<Data> findByIndicatorInAndSourceAndTimePeriodInOrderByPercentageDesc(List<Indicator> indicators,
			IndicatorClassification source, List<Timeperiod> latestTps);

	List<Data> findByIndicatorInAndSourceAndTimePeriodInOrderByTimePeriodEndDateDesc(List<Indicator> indicators,
			IndicatorClassification source, List<Timeperiod> latestTps);

	List<Data> findByIndicatorInAndSourceAndTimePeriodInAndAreaAreaCodeOrderByTimePeriodEndDateAsc(
			List<Indicator> indicators, IndicatorClassification source, List<Timeperiod> latestTps, String areaCode);

	List<Data> findByIndicatorInAndTimePeriodOrderByPercentageDesc(List<Indicator> performanceIndicators,
			Timeperiod latestTp);

	List<Data> findByIndicatorInAndSourceAndTimePeriodAndAreaAreaCodeOrderByIndicatorIndicatorIdAsc(
			List<Indicator> indicators, IndicatorClassification source, Timeperiod latestTp, String areaCode);

	@Query("SELECT utData , utData.area , utData.timePeriod  FROM Data utData JOIN utData.area utArea JOIN utData.timePeriod utTimePeriod "
			+ " WHERE " + " utData.timePeriod.timeperiodId = :timeperiodId AND "
			+ " utData.source.indicatorClassificationId = :sourceNid AND "
			+ " utData.indicatorUnitSubgroup.indicatorUnitSubgroupId = :indicatorUnitSubgroupId AND utArea.areaId "
			+ " IN " + "(:areaNid)  " + " ORDER BY utData.percentage")
	public List<Object[]> findDataByTimePeriod(@Param("indicatorUnitSubgroupId") Integer indicatorId,
			@Param("timeperiodId") Integer timeperiodId, @Param("sourceNid") Integer sourceNid,
			@Param("areaNid") Integer[] areaNid);

	@Query(value="SELECT utData.percentage,area.area_code,area.area_name , utData.district_id_fk , tp.timeperiod,rank() over(order by utData.percentage desc)  FROM Data utData "
			+ " JOIN Area area on area.area_id=utData.district_id_fk "
			+ " JOIN time_period tp on tp.timeperiod_id = utData.time_period_id_fk "
			+ "	WHERE utData.time_period_id_fk = :timeperiodId AND " + "utData.source_id_or_ic_id_fk = :sourceNid AND "
			+ "	utData.indicator_unit_subgroup_id_fk = :indicatorUnitSubgroupId AND utData.district_id_fk"
			+ "	IN (:areaNid)   ORDER BY utData.percentage",nativeQuery=true)
	public List<Object[]> findDataByTimePeriodWithRankWithHighGood(@Param("indicatorUnitSubgroupId") Integer indicatorId,
			@Param("timeperiodId") Integer timeperiodId, @Param("sourceNid") Integer sourceNid,
			@Param("areaNid") Integer[] areaNid);
	
	@Query(value="SELECT utData.percentage,area.area_code,area.area_name , utData.district_id_fk , tp.timeperiod,rank() over(order by utData.percentage asc)  FROM Data utData JOIN Area area on area.area_id=utData.district_id_fk "
			+ " JOIN time_period tp on tp.timeperiod_id = utData.time_period_id_fk "
			+ "			WHERE utData.time_period_id_fk = :timeperiodId AND " + "utData.source_id_or_ic_id_fk = :sourceNid AND "
			+ "			 utData.indicator_unit_subgroup_id_fk = :indicatorUnitSubgroupId AND utData.district_id_fk"
			+ "			 IN (:areaNid)   ORDER BY utData.percentage",nativeQuery=true)
	public List<Object[]> findDataByTimePeriodWithRankWithHighBad(@Param("indicatorUnitSubgroupId") Integer indicatorId,
			@Param("timeperiodId") Integer timeperiodId, @Param("sourceNid") Integer sourceNid,
			@Param("areaNid") Integer[] areaNid);
	

//	@Query("SELECT utData , utArea , utTimePeriod , sourceObj from Data utData JOIN utData.area as utArea JOIN utData.timePeriod as utTimePeriod JOIN utData.source as sourceObj "
//			+ "WHERE " + "utArea.areaId = :areaNid AND "
//			+ "utData.indicatorUnitSubgroup.indicatorUnitSubgroupId = :indicatorId "
//			+ "ORDER BY utArea.areaCode,utTimePeriod.timePeriod")
//	public List<Object[]> findDataforCommissioner(@Param("indicatorId") Integer indicatorId,
//			@Param("areaNid") Integer areaNid);

	@Query("SELECT utData , utArea , utTimePeriod , sourceObj from Data utData JOIN utData.area as utArea JOIN utData.timePeriod as utTimePeriod JOIN utData.source as sourceObj "
			+ "WHERE " + "utArea.areaId = :areaNid AND "
			+ "utData.indicatorUnitSubgroup.indicatorUnitSubgroupId = :indicatorId and utData.published=true "
			+ "ORDER BY utArea.areaCode,utTimePeriod.timePeriod")
	public List<Object[]> findData(@Param("indicatorId") Integer indicatorId, @Param("areaNid") Integer areaNid);
	
	
	
//	@Query("SELECT utData , utArea , utTimePeriod , sourceObj from Data utData JOIN utData.area as utArea JOIN utData.timePeriod as utTimePeriod JOIN utData.source as sourceObj "
//			+ "WHERE " + "utArea.areaId = :areaNid AND "
//			+ "utData.indicatorUnitSubgroup.indicatorUnitSubgroupId = :indicatorId AND utData.source.indicatorClassificationId = :sourceNid "
//			+ "ORDER BY utArea.areaCode,utTimePeriod.timePeriod")
//	public List<Object[]> findDataforCommissionerWithSource(@Param("indicatorId") Integer indicatorId,
//			@Param("areaNid") Integer areaNid, @Param("sourceNid") Integer sourceNid );

	@Query("SELECT utData , utArea , utTimePeriod , sourceObj from Data utData JOIN utData.area as utArea JOIN utData.timePeriod as utTimePeriod JOIN utData.source as sourceObj "
			+ "WHERE " + "utArea.areaId = :areaNid AND "
			+ "utData.indicatorUnitSubgroup.indicatorUnitSubgroupId = :indicatorId and utData.published=true AND utData.source.indicatorClassificationId = :sourceNid "
			+ "ORDER BY utArea.areaCode,utTimePeriod.timePeriod desc")
	public List<Object[]> findDataWithSource(@Param("indicatorId") Integer indicatorId, @Param("areaNid") Integer areaNid, @Param("sourceNid") Integer sourceNid);
	

	List<Data> findByTimePeriodAndAreaIn(Timeperiod timePeriod, List<Area> areas);

	List<Data> findByIndicatorInAndTimePeriodAndAreaAreaCodeOrderByPercentageDesc(List<Indicator> performanceIndicators,
			Timeperiod latestTp, String areaCode);

	List<Data> findByIndicatorInAndTimePeriodAndAreaAreaCodeAndSourceOrderByPercentageDesc(
			List<Indicator> performanceIndicators, Timeperiod latestTp, String areaCode,
			IndicatorClassification source);

	@Query(value="SELECT utData.percentage,area.area_code,area.area_name , utData.district_id_fk , tp.timeperiod,rank() over(order by utData.percentage desc)  FROM Data utData "
			+ " JOIN Area area on area.area_id=utData.district_id_fk "
			+ " JOIN time_period tp on tp.timeperiod_id = utData.time_period_id_fk "
			+ "	WHERE utData.time_period_id_fk = :timeperiodId AND " + "utData.source_id_or_ic_id_fk = :sourceNid AND "
			+ "	utData.indicator_id_fk = :indicatorId AND area.area_level_id_fk=3 ORDER BY utData.percentage",nativeQuery=true)
	List<Object[]> findDataByTimePeriodForIndicatorWithRankWithHighGood(@Param("indicatorId") Integer indicatorId,
			@Param("timeperiodId") Integer timeperiodId, @Param("sourceNid") Integer sourceNid);

	@Query(value="SELECT utData.percentage,area.area_code,area.area_name , utData.district_id_fk , tp.timeperiod,rank() over(order by utData.percentage asc)  FROM Data utData JOIN Area area on area.area_id=utData.district_id_fk "
			+ " JOIN time_period tp on tp.timeperiod_id = utData.time_period_id_fk "
			+ "			WHERE utData.time_period_id_fk = :timeperiodId AND " + "utData.source_id_or_ic_id_fk = :sourceNid AND "
			+ "			 utData.indicator_id_fk = :indicatorId AND area.area_level_id_fk=3 ORDER BY utData.percentage",nativeQuery=true)
	List<Object[]> findDataByTimePeriodForIndicatorWithRankWithHighBad(@Param("indicatorId") Integer indicatorId,
			@Param("timeperiodId") Integer timeperiodId, @Param("sourceNid") Integer sourceNid);

}
