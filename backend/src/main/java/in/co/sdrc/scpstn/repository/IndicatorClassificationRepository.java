package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.models.IndicatorClassificationType;
import in.co.sdrc.scpstn.models.Type;

public interface IndicatorClassificationRepository extends JpaRepository<IndicatorClassification,Integer> {


	public IndicatorClassification findByIndicatorClassificationId(Integer icId);

	public IndicatorClassification findByIndexIsTrue();

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentIsNull(
			IndicatorClassificationType indicatorClassificationType);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentIsNullAndIndexIsFalse(
			IndicatorClassificationType indicatorClassificationType);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentIsNotNull(
			IndicatorClassificationType indicatorClassificationType);

	public List<IndicatorClassification> findByParent(
			IndicatorClassification indicatorClassificationOrSectorSubSectorSource);

	public List<IndicatorClassification> findAll();

	public List<IndicatorClassification> findByIndicatorClassificationType(
			IndicatorClassificationType indicatorClassificationType);

	public IndicatorClassification findByType(Type indicatorClassificationType);

	public IndicatorClassification findBySectorIds(String stringIds);

	// public List<Object[]> findAllIndicatorsOfaSector(Integer parentSectorId);

	public IndicatorClassification findByNameAndParentIsNotNull(String stringCellValue);

	public IndicatorClassification findByNameAndParentIsNull(String stringCellValue);

	public IndicatorClassification findByNameAndParent(String stringCellValue, IndicatorClassification icSector);

	@Query("SELECT ic FROM IndicatorClassification ic WHERE ic.indicatorClassificationId "
			+ " in (SELECT distinct data.source FROM Data data JOIN data.indicatorUnitSubgroup ius JOIN ius.indicator ind JOIN ind.agency ag WHERE data.indicatorUnitSubgroup.indicatorUnitSubgroupId = :iusNid and ag.agencyId = :agencyId )")
	public List<IndicatorClassification> findByIUS_Nid(@Param("iusNid") Integer iusNid,
			@Param("agencyId") Integer agencyId) throws DataAccessException;

	@Query(value = "SELECT ic.indicator_classification_id as icNId, ic.parent_id as icParentNId, ic.classification_name as icName, "
			+ "ius.indicator_id_fk as indicatorNId, indi.indicator_name as indicatorName FROM indicator_classification as ic, "
			+ "ic_ius_mapping as icius, indicator_unit_subgroup as ius, indicator as indi WHERE ic.indicator_classification_id=icius.ic_fk and "
			+ "icius.ius_fk=ius.indicator_unit_subgroup_id and ius.indicator_id_fk=indi.indicator_id and ic.parent_id != -1 and "
			+ "ic.indicatorClassificationType='SC' ORDER BY ic.parent_id, ic.classification_name Asc,indi.indicator_name", nativeQuery = true)
	List<Object[]> findAllIndicators();

	@Query(value = "SELECT ic.indicator_classification_id as icNId, ic.parent_id as icParentNId, ic.classification_name as icName, "
			+ "ius.indicator_id_fk as indicatorNId, indi.indicator_name as indicatorName FROM indicator_classification as ic, "
			+ "ic_ius_mapping as icius, indicator_unit_subgroup as ius, indicator as indi WHERE ic.indicator_classification_id=icius.ic_fk and "
			+ "icius.ius_fk=ius.indicator_unit_subgroup_id and ius.indicator_id_fk=indi.indicator_id and ic.parent_id != -1 and "
			+ "ic.indicatorClassificationType='SC' and indi.agency_id_fk= :agencyId ORDER BY ic.parent_id, ic.classification_name Asc,indi.indicator_name", nativeQuery = true)
	List<Object[]> findAllIndicatorsByAgency(@Param("agencyId") int agencyId);

	@Query(value = "SELECT ic.indicator_classification_id as icNId, ic.parent_id as icParentNId, ic.classification_name as icName, "
			+ "ius.indicator_id_fk as indicatorNId, indi.indicator_name as indicatorName "
			+ "FROM indicator_classification as ic inner join "
			+ "ic_ius_mapping as icius on ic.indicator_classification_id=icius.ic_fk INNER JOIN "
			+ "indicator_unit_subgroup as ius ON  icius.ius_fk=ius.indicator_unit_subgroup_id INNER JOIN  "
			+ "indicator as indi ON  ius.indicator_id_fk=indi.indicator_id " + "WHERE   "
			+ " ic.parent_id = :parentSectorId and ic.indicatorclassificationtype='SC' and indi.agency_id_fk = :agencyId ORDER BY ic.parent_id", nativeQuery = true)
	public List<Object[]> findAllIndicatorsOfaSector(@Param("parentSectorId") Integer parentSectorId,
			@Param("agencyId") int agencyId);

	public IndicatorClassification findByNameAndParentIsNotNullAndTypeIsNull(String string);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentIsNotNullAndIndexIsFalse(
			IndicatorClassificationType sc);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentIsNotNullAndSectorIdsIsNull(
			IndicatorClassificationType sc);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndType(IndicatorClassificationType sc,
			Type sectorIndexSourcewise);

	public IndicatorClassification findBySectorIdsAndParentType(String valueOf, Type sectorIndexSourcewise);

	public IndicatorClassification findBySectorIdsAndParentTypeAndIndexForSource(String valueOf,
			Type sectorIndexSourcewise, String iSource);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentType(IndicatorClassificationType sc,
			Type sectorIndexSourcewise);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentTypeAndIndexForSource(
			IndicatorClassificationType sc, Type sectorIndexSourcewise, String iSource);

	public IndicatorClassification findByIndicatorClassificationTypeAndParentIsNullAndIndexIsFalseAndSectorIds(
			IndicatorClassificationType sc, String sectorIds);

	public List<IndicatorClassification> findByIndicatorClassificationTypeAndParentTypeAndIndexForSourceAndTypeIsNull(
			IndicatorClassificationType sc, Type sectorIndexSourcewise, String iSource);

	public IndicatorClassification findByNameAndIndicatorClassificationType(String iSource,
			IndicatorClassificationType sr);

	public IndicatorClassification findByTypeAndIndexForSource(Type overallBySource, String iSource);

	public List<IndicatorClassification> findByNameInAndIndicatorClassificationType(List<String> sources,
			IndicatorClassificationType sr);

	public List<IndicatorClassification> findByParentTypeAndSectorIdsIn(Type subsectorIndex, List<String> sectorIds);

	@Query(value="SELECT DISTINCT(SI.*) FROM INDICATOR I JOIN indicator_classification SI ON SI.indicator_classification_id = I.indicator_classification_id "
			+ "JOIN indicator_classification PS ON PS.indicator_classification_id = I.indicator_classification_id " + 
			"	WHERE I.indicator_classification_id IN ( SELECT indicator_classification_id FROM indicator_classification WHERE PARENT_ID = :indicatorClassificationSectorId) "
			+ "AND I.classifier='PERFORMANCE' and I.app_type='APP_V_2' AND I.indicator_source IN ('DCPU','CWC','PO','SJPU','JJB')",nativeQuery=true)
	public List<IndicatorClassification> findByPerformanceIndicators(@Param(value="indicatorClassificationSectorId") Integer indicatorClassificationSectorId);
	
	
	
	@Query(value = "SELECT ic.indicator_classification_id as icNId, ic.parent_id as icParentNId, ic.classification_name as icName, "
			+ "ius.indicator_id_fk as indicatorNId, indi.indicator_name as indicatorName "
			+ "FROM indicator_classification as ic inner join "
			+ "ic_ius_mapping as icius on ic.indicator_classification_id=icius.ic_fk INNER JOIN "
			+ "indicator_unit_subgroup as ius ON  icius.ius_fk=ius.indicator_unit_subgroup_id INNER JOIN  "
			+ "indicator as indi ON  ius.indicator_id_fk=indi.indicator_id " + "WHERE   "
			+ " ic.parent_id = :parentSectorId and ic.indicatorclassificationtype='SC' and indi.agency_id_fk = :agencyId AND indi.app_type=:appType ORDER BY ic.parent_id", nativeQuery = true)
	public List<Object[]> findAllIndicatorsOfaSectorAndAppType(@Param("parentSectorId") Integer parentSectorId,
			@Param("agencyId") int agencyId,@Param("appType")String appType);
	
	
	@Query(value="SELECT DISTINCT(SI.*) FROM INDICATOR I JOIN indicator_classification SI ON SI.indicator_classification_id = I.indicator_classification_id "
			+ "JOIN indicator_classification PS ON PS.indicator_classification_id = I.indicator_classification_id " + 
			"	WHERE I.indicator_classification_id IN ( SELECT indicator_classification_id FROM indicator_classification WHERE PARENT_ID = :indicatorClassificationSectorId) "
			+ "AND I.classifier='PERFORMANCE' and I.app_type='APP_V_2' AND I.indicator_source = :indicatorSource",nativeQuery=true)
	public List<IndicatorClassification> findByPerformanceIndicatorsAndReportingSource(@Param(value="indicatorClassificationSectorId") Integer indicatorClassificationSectorId,
			@Param(value="indicatorSource") String indicatorSource);

}
