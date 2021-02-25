package in.co.sdrc.scpstn.repository;

import java.util.Date;
import java.util.List;

import javax.persistence.LockModeType;

import org.sdrc.usermgmt.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.scpstn.domain.Area;
import in.co.sdrc.scpstn.domain.Facility;
import in.co.sdrc.scpstn.domain.Submission;
import in.co.sdrc.scpstn.models.FormStatus;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

	List<Submission> findTop6ByFacilityOrderBySubmissionDateAsc(Facility facility);

	Submission findByUniqueIdAndCreatedBy(String uniqueId, Account acc);

	Submission findTop1ByFacilityAndCreatedByOrderBySubmissionDateDesc(Facility facility, Account account);

	Submission findTop1ByFormIdAndFacilityAndCreatedByOrderBySubmissionDateDesc(Integer formId, Facility facility,
			Account account);

	List<Submission> findAllByFormStatusAndFormIdAndSubmissionDateBetween(FormStatus finalized, Integer formId,
			Date startDate, Date endDate);

	List<Submission> findByIdInAndFormId(List<String> submissionIds, Integer formId);

	List<Submission> findAllByFormStatusAndSubmissionDateBetween(FormStatus finalized, Date startDate, Date endDate);

	Submission findTop1ByOrderBySubmissionDateAsc();

	Submission findTop1BySubmissionDateBetweenOrderBySubmissionDateAsc(Date startDate, Date endDate);

	@Query(value = "SELECT AVG( utdata.percentage) as avg , utdata.district_id_fk as areaID, area.area_name as areaName, "
			+ "icius.ic_fk as icNid , ic.classification_name as icName " + "FROM data as utdata, "
			+ "ic_ius_mapping as icius , " + "indicator_classification as ic, " + "area as area "
			+ " where icius.ius_fk = utdata.indicator_unit_subgroup_id_fk  and icius.ic_fk = ic.indicator_classification_id "
			+ " and area.area_id = utdata.district_id_fk and "
			+ " utdata.time_period_id_fk = :timePeriod and utdata.district_id_fk IN (:areaList) "
			+ " and ic.parent_id = :index and ic.indicatorclassificationtype = 'SC' and utdata.source_id_or_ic_id_fk = 27 "
			+ " group by utdata.district_id_fk, icius.ic_fk,ic.classification_name,area.area_name "
			+ " ORDER BY utdata.district_id_fk", nativeQuery = true)
	List<Object[]> findByArea(@Param("areaList") List<Integer> areaList, @Param("timePeriod") Integer timePeriod,
			@Param("index") int index);

	@Query(value = "SELECT data.percentage as dataValue, ic.indicator_classification_id as icNId, ic.classification_name as icName, indi.indicator_id as indNId, "
			+ "indi.indicator_name as indName, data.district_id_fk as areaNId, area.area_name as areaName "
			+ "FROM data as data, " + "ic_ius_mapping as icius, " + "indicator_classification as ic, "
			+ "indicator as indi, " + "area as area "
			+ "where icius.ic_fk=ic.indicator_classification_id and data.indicator_unit_subgroup_id_fk=icius.ius_fk and indi.indicator_id=data.indicator_id_fk"
			+ " and data.district_id_fk=area.area_id and "
			+ "data.time_period_id_fk =:timePeriod and ic.parent_id=:parentSectorId AND "
			+ "data.district_id_fk IN (:areaList) ORDER BY data.district_id_fk,ic.classification_name", nativeQuery = true)
	List<Object[]> findInByArea(@Param("areaList") List<Integer> areaList, @Param("timePeriod") Integer timePeriod,
			@Param("parentSectorId") Integer parentSectorId);

	@Query(value = "select * from submission where id IN ( select s from (select f.facility_id,(select s.id from  submission s where s.facility_id_fk=f.facility_id order by s.submission_date desc limit 1) as s from facility f where f.facility_type IS NOT NULL) as x)", nativeQuery = true)
	List<Submission> findAllLastestSubmissionOfEachFacility();

//	findByStartDateLessThanEqualAndEndDateGreaterThan
	List<Submission> findByFacilityIsActiveIsTrueAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(
			Date startDate, Date endDate);

	List<Submission> findTop6ByFacilityOrderBySubmissionDateDesc(Facility facility);

	Submission findByUniqueIdAndFacility(String uniqueId, Facility facility);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Submission findByFacilityAndCreatedDateBetween(Facility facility, Date startDate, Date endDate);

	Submission findById(Long long1);

	List<Submission> findAllByFacilityAreaAreaIdAndFormStatusAndFormIdAndSubmissionDateBetweenOrderByFacilityAreaAreaNameAscSubmissionDateDesc(
			Integer districtId, FormStatus finalized, Integer formId, Date startDate, Date endDate);

	List<Submission> findAllByFacilityAreaAreaIdAndFormStatusAndFormIdAndFacilityAreaInAndSubmissionDateBetweenOrderByFacilityAreaAreaNameAscSubmissionDateDesc(
			Integer districtId, FormStatus finalized, Integer formId, List<Area> districts, Date startDate,
			Date endDate);

	List<Submission> findByFacilityAfmIsLiveIsTrueAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(
			Date startDate, Date endDate);

	List<Submission> findByFacilityAfmAccountLockedIsFalseAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(
			Date startDate, Date endDate);

	List<Submission> findAllByFormStatusAndFormIdAndFacilityAreaInAndSubmissionDateBetweenOrderByFacilityAreaAreaNameAscSubmissionDateDesc(
			FormStatus finalized, Integer formId, List<Area> districts, Date startDate, Date endDate);

	List<Submission> findAllByFormStatusAndFormIdAndSubmissionDateBetweenOrderByFacilityAreaAreaNameAscSubmissionDateDesc(
			FormStatus finalized, Integer formId, Date startDate, Date endDate);

	List<Submission> findByFormStatusAndFacilityAfmAccountLockedIsFalseAndCreatedDateGreaterThanEqualAndCreatedDateLessThanEqual(
			FormStatus finalized, Date startDate, Date endDate);

}
