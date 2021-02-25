package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.co.sdrc.scpstn.domain.Agency;
import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.models.AppType;
import in.co.sdrc.scpstn.models.IndicatorClassfier;
import in.co.sdrc.scpstn.models.IndicatorType;

public interface IndicatorRepository extends JpaRepository<Indicator,Integer> {


	public Indicator findByIndicatorId(Integer indicatorId);

	public List<Indicator> findAll();

	public List<Indicator> findAllByAgencyAgencyId(Integer agencyId);

	public List<Indicator> findAllByAgencyAndIndicatorType(Agency agency, IndicatorType indicatorType);

	public List<Indicator> findAllByAgencyAndIndicatorTypeOrderByIndicatorIdAsc(Agency agency,
			IndicatorType indicatorType);

	public Indicator findByAgencyAndIndicatorClassificationAndIndicatorType(Agency agency, IndicatorClassification ic,
			IndicatorType indicatorType);

	public Indicator findByIndicatorNameAndAgencyAgencyId(String indicatorName, Integer agencyId);

	@Query(value = "select i from Indicator i where "
			+ " (i.indicatorType='ACTUAL_INDICATOR' OR i.indicatorType='INDEX_INDICATOR' OR i.indicatorType='OVERALL') "
			+ "and i.indicatorClassification.indicatorClassificationId = :subsector_id_fk ")
	public List<Indicator> findIndicatorByICforAllAgency(@Param(value = "subsector_id_fk") Integer subsector);

	@Query(value = "select * from indicator i where (i.indicator_type='ACTUAL_INDICATOR' OR i.indicator_type='INDEX_INDICATOR' OR i.indicator_type='OVERALL') and "
			+ "i.indicator_classification_id = :subsector_id_fk and i.agency_id_fk = :agency", nativeQuery = true)
	public List<Indicator> findAllIndicatorByICforAgency(@Param(value = "subsector_id_fk") Integer subsector,
			@Param(value = "agency") Integer agency);

	@Query(value = "select * from indicator i where i.indicator_type='ACTUAL_INDICATOR' and i.agency_id_fk = :agency and i.indicator_classification_id IN (:subsector_id_fk)", nativeQuery = true)
	public List<Indicator> findIndicatorsOfAgencyBySector(@Param(value = "agency") Agency agency,
			@Param(value = "subsector_id_fk") List<Integer> subsector);

	@Query("SELECT uticius, utUnit, " + "  utIn, " + "  subEn   "
			+ " FROM IndicatorClassificationIndicatorUnitSubgroupMapping uticius JOIN uticius.indicatorUnitSubgroup utius "
			+ " JOIN utius.indicator utIn JOIN  utius.unit utUnit JOIN utius.subgroup subEn "
			+ " WHERE uticius.indicatorClassification = :sectorNid and utIn.agency = :agency Order by uticius.indicatorUnitSubgroup ")
	public List<Object[]> findByIC_Type_And_Agency(@Param("sectorNid") IndicatorClassification sectorNid,
			@Param("agency") Agency agency) throws DataAccessException;

	@Query("SELECT  utIndicatorEn FROM Data utData JOIN utData.indicator utIndicatorEn"
			+ " WHERE utData.indicator.indicatorId = utIndicatorEn.indicatorId AND "
			+ " utData.indicatorUnitSubgroup.indicatorUnitSubgroupId = :iusNID ")
	public Indicator findByIndicator_NId(@Param("iusNID") Integer indicatorId);


	public List<Indicator> findAllByAgencyAndAppTypeAndIndicatorTypeOrderByIndicatorIdAsc(Agency agency, AppType appV2,
			IndicatorType actualIndicator);

	public List<Indicator> findAllByAppType(AppType appV2);


	public List<Indicator> findByIndicatorClassificationInAndClassifier(List<Integer> subsectorIds,
			IndicatorClassfier performance);

	public List<Indicator> findByClassifierAndIndicatorClassificationIn(IndicatorClassfier performance,
			List<IndicatorClassification> subsectorIds);

	public List<Indicator> findByClassifierAndIndicatorSourceAndIndicatorClassificationIn(
			IndicatorClassfier performance, String name, List<IndicatorClassification> subsectorIds);

	public List<Indicator> findByClassifierAndIndicatorSourceAndIndicatorClassification(IndicatorClassfier performance,
			String name, IndicatorClassification subsector);

	public List<Indicator> findByClassifierAndIndicatorClassification(IndicatorClassfier performance,
			IndicatorClassification subsector);

	public List<Indicator> findByClassifierAndIndicatorClassificationInAndIndicatorSource(
			IndicatorClassfier performance, List<IndicatorClassification> children, String isource);

	public Indicator findAllByAgencyAndIndicatorTypeAndIndicatorSource(Agency agency, IndicatorType overallBySource,
			String iSource);

	public List<Indicator> findByClassifierAndIndicatorClassificationAndIndicatorSource(IndicatorClassfier performance,
			IndicatorClassification sector, String iSource);

	public Indicator findByIndicatorClassificationAndIndicatorType(IndicatorClassification overallSectorForSource,
			IndicatorType indexOverallSourcewise);

	public List<Indicator> findAllByAgencyAndAppTypeAndIndicatorTypeInOrderByIndicatorIdAsc(Agency agency,
			AppType appV2, IndicatorType ... indexIndicator);

	public List<Indicator> findAllByAgencyAndAppTypeAndIndicatorTypeNotInOrderByIndicatorIdAsc(Agency agency,
			AppType appV2, IndicatorType... actualIndicator);

	public List<Indicator> findByClassifierAndIndicatorClassificationInAndIndicatorSource(
			IndicatorClassfier performance, IndicatorClassification subsector, String iSource);

	public List<Indicator> findAllByAgencyAndIndicatorTypeAndIndicatorSourceIsNull(Agency agency,
			IndicatorType indexIndicator);

	public Indicator findByIndicatorType(IndicatorType indexIndicator);

	public List<Indicator> findByIndicatorClassificationInAndIndicatorType(List<IndicatorClassification> children,
			IndicatorType indexIndicator);

	public List<Indicator> findByIndicatorClassificationInAndIndicatorTypeAndAppTypeAndClassifier(
			List<IndicatorClassification> subsectors, IndicatorType actualIndicator, AppType appV2,
			IndicatorClassfier performance);

	public List<Indicator> findAllByIndicatorClassificationAndAppType(IndicatorClassification child, AppType appV2);
	
	public List<Indicator> findAllByIndicatorClassificationAndIndicatorTypeNotIn(IndicatorClassification child,IndicatorType ... indicatorType);


	public List<Indicator> findAllByAgencyAndIndicatorTypeAndIndicatorSourceIsNullOrIndicatorType(Agency agency,
			IndicatorType indexIndicator, IndicatorType overall);

	public List<Indicator> findByIndicatorClassificationInAndIndicatorTypeIn(List<IndicatorClassification> children,
			IndicatorType ... indexIndicator);

//	@Query("SELECT  utIndicatorEn FROM  Indicator utIndicatorEn join utIndicatorEn.indicatorRoleMapping irm  "
//			+ " WHERE utIndicatorEn.indicatorType='ACTUAL_INDICATOR' AND  utIndicatorEn.indicatorXpath IN (:indicatorXpathNames) AND irm.role.roleId = :roleId")
//	public List<Indicator> findAllByIndicatorNameIn(
//			@Param("indicatorXpathNames") List<String> commaSeparatedIndicatorNames, @Param("roleId") Integer roleId);

}
