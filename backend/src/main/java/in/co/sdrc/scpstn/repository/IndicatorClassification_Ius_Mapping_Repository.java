package in.co.sdrc.scpstn.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.scpstn.domain.IndicatorClassification;
import in.co.sdrc.scpstn.domain.IndicatorClassificationIndicatorUnitSubgroupMapping;
import in.co.sdrc.scpstn.domain.IndicatorUnitSubgroup;

public interface IndicatorClassification_Ius_Mapping_Repository extends JpaRepository<IndicatorClassificationIndicatorUnitSubgroupMapping,Integer>{
	

	public IndicatorClassificationIndicatorUnitSubgroupMapping findByIndicatorClassificationAndIndicatorUnitSubgroup(IndicatorClassification icSubSector, IndicatorUnitSubgroup ius);

}