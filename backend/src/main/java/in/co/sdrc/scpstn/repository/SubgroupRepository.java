package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.scpstn.domain.Subgroup;

public interface SubgroupRepository extends JpaRepository<Subgroup, Integer>{

	
	public List<Subgroup> findAllByOrderBySubgroupValueIdAsc();
	public Subgroup findBySubgroupValueId(Integer subgroupValueId);
	public Subgroup findBySubgroupVal(String stringCellValue);
	
}
