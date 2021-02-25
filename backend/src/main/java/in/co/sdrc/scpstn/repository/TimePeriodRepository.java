package in.co.sdrc.scpstn.repository;

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.scpstn.domain.Timeperiod;
import in.co.sdrc.scpstn.models.AppType;

public interface TimePeriodRepository  extends JpaRepository<Timeperiod, Integer>{

	public Timeperiod findByTimePeriod(String timePeriod);

	public Timeperiod findByTimeperiodId(Integer timePeriod);


	List<Timeperiod> findAll();

	
	@Query("SELECT time FROM Timeperiod time WHERE time.timeperiodId " +
			" IN (SELECT distinct data.timePeriod FROM Data data  WHERE data.indicatorUnitSubgroup.indicatorUnitSubgroupId = :iusNid "
			+ "AND data.source.indicatorClassificationId = :sourceNid) order by time.startDate asc")
	List<Timeperiod> findBySource_Nid(@Param("iusNid") Integer iusNid,@Param("sourceNid") Integer sourceNid) throws DataAccessException;

	@Query("SELECT time FROM Timeperiod time WHERE time.timeperiodId " +
			" IN (SELECT distinct data.timePeriod FROM Data data  WHERE data.indicatorUnitSubgroup.indicatorUnitSubgroupId = :iusNid "
			+ "AND data.source.indicatorClassificationId = :sourceNid and data.published=true) order by time.startDate asc")
	public List<Timeperiod> findBySource_NidForPublicView(@Param("iusNid")Integer iusNid, @Param("sourceNid") Integer sourceNid);

	@Query(value="select t.* from time_period t "
			+ "left outer join data d on t.timeperiod_id=d.time_period_id_fk "
			+ " inner join indicator i on d.indicator_id_fk=i.indicator_id "
			+ "where i.agency_id_fk= :agencyId and d.indicator_id_fk is not null group by t.timeperiod_id order by t.start_date asc",nativeQuery=true)
	
	public List<Timeperiod> findTimePeriodsPresentForDataOfMyAgencyAllPublishedAndUnPublished(@Param("agencyId") int agencyId);
	
	
	@Query(value="select t.* from time_period t "
			+ "left outer join data d on t.timeperiod_id=d.time_period_id_fk "
			+ " inner join indicator i on d.indicator_id_fk=i.indicator_id "
			+ "where i.agency_id_fk= :agencyId and d.published=true and d.indicator_id_fk is not null group by t.timeperiod_id order by t.start_date asc",nativeQuery=true)
	public List<Timeperiod> findTimePeriodsPresentForDataOfMyAgencyOnlyPublished(@Param("agencyId") int agencyId);

	public Timeperiod findTop1ByOrderByEndDateDesc();

	public List<Timeperiod> findTop12ByOrderByEndDateDesc();

	public List<Timeperiod> findTop3ByOrderByEndDateDesc();

	public List<Timeperiod> findTop12ByStartDateGreaterThanOrderByEndDateDesc(Date tpAfterMarch2019);

	public Timeperiod findTop1ByAppTypeOrderByEndDateDesc(AppType appV2);

	public Timeperiod findByStartDateLessThanEqualAndEndDateGreaterThanEqual(Date today, Date today2);
	

}
