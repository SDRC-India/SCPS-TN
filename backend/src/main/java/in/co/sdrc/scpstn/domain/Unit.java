package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import in.co.sdrc.scpstn.models.UnitType;
/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Entity
@Table(name = "unit")
public class Unit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="unitid")
	private Integer unitId;
	
	@Column(name="unit_name")
	private String unitName;
	
	
	@Column(name="unit_type")
	@Enumerated(EnumType.STRING)
	private UnitType unitType;

	
	
	public Unit() {
		super();
	}

	public Unit(int unitId) {
		this.unitId = unitId;	
	}

	public Integer getUnitId() {
		return unitId;
	}

	public void setUnitId(Integer unitId) {
		this.unitId = unitId;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	
	
	
	
	
	
	
	
}
