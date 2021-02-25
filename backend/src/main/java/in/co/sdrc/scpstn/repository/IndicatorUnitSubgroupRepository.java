package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.scpstn.domain.Indicator;
import in.co.sdrc.scpstn.domain.IndicatorUnitSubgroup;
import in.co.sdrc.scpstn.domain.Subgroup;
import in.co.sdrc.scpstn.domain.Unit;
import in.co.sdrc.scpstn.models.AppType;
import in.co.sdrc.scpstn.models.IndicatorType;

public interface IndicatorUnitSubgroupRepository extends JpaRepository<IndicatorUnitSubgroup, Integer> {


	public IndicatorUnitSubgroup findByIndicatorUnitSubgroupId(Integer id);

	public IndicatorUnitSubgroup findByIndicatorAndUnitAndSubgroup(Indicator indicator, Unit unit, Subgroup subgroup);

	public List<IndicatorUnitSubgroup> findAll();

	public List<IndicatorUnitSubgroup> findAllByOrderByIndicatorUnitSubgroupIdAsc();


	public List<IndicatorUnitSubgroup> findAllByIndicatorAppTypeAndIndicatorIndicatorTypeOrderByIndicatorUnitSubgroupIdAsc(
			AppType appV2, IndicatorType actualIndicator);

	public List<IndicatorUnitSubgroup> findAllByIndicatorAppTypeAndIndicatorIndicatorTypeInOrderByIndicatorUnitSubgroupIdAsc(
			AppType appV2, IndicatorType... actualIndicator);


}
