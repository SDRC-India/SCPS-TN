package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Entity
@Table(name = "indicator_unit_subgroup", 
		
		indexes = { 
	
		@Index(name = "i_indicator_id_fk", columnList = "indicator_id_fk"), 
		@Index(name = "i_unit_id_fk", columnList = "unit_id_fk"), 
		@Index(name = "i_subgroup_id_fk", columnList = "subgroup_id_fk"), 
		@Index(name = "i_ius", columnList = "indicator_id_fk,unit_id_fk,subgroup_id_fk") },
		
		 uniqueConstraints=
         @UniqueConstraint(columnNames={"indicator_id_fk", "unit_id_fk","subgroup_id_fk"}))
	

public class IndicatorUnitSubgroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="indicator_unit_subgroup_id")
	private Integer indicatorUnitSubgroupId;

	@ManyToOne
	@JoinColumn(name = "indicator_id_fk")
	private Indicator indicator;

	@ManyToOne
	@JoinColumn(name = "unit_id_fk")
	private Unit unit;

	@ManyToOne
	@JoinColumn(name = "subgroup_id_fk")
	private Subgroup subgroup;

	public Integer getIndicatorUnitSubgroupId() {
		return indicatorUnitSubgroupId;
	}

	public void setIndicatorUnitSubgroupId(Integer indicatorUnitSubgroupId) {
		this.indicatorUnitSubgroupId = indicatorUnitSubgroupId;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public Subgroup getSubgroup() {
		return subgroup;
	}

	public void setSubgroup(Subgroup subgroup) {
		this.subgroup = subgroup;
	}

	
	

}
