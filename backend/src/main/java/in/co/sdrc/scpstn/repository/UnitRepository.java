package in.co.sdrc.scpstn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.co.sdrc.scpstn.domain.Unit;
import in.co.sdrc.scpstn.models.UnitType;

public interface UnitRepository extends JpaRepository<Unit, Integer> {

	public List<Unit> findAll();

	public Unit findByUnitType(UnitType unitType);
	
	

	public Unit findByUnitId(Integer unitId);

	public Unit findByUnitName(String stringCellValue);

}
