package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Entity
@Table(name = "ic_ius_mapping")
public class IndicatorClassificationIndicatorUnitSubgroupMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ic_ius_id")
	private Integer iCIusId;

	@ManyToOne
	@JoinColumn(name = "ic_fk")
	private IndicatorClassification indicatorClassification;

	@ManyToOne
	@JoinColumn(name = "ius_fk")
	private IndicatorUnitSubgroup indicatorUnitSubgroup;

	public Integer getiCIusId() {
		return iCIusId;
	}

	public void setiCIusId(Integer iCIusId) {
		this.iCIusId = iCIusId;
	}

	public IndicatorClassification getIndicatorClassification() {
		return indicatorClassification;
	}

	public void setIndicatorClassification(IndicatorClassification indicatorClassification) {
		this.indicatorClassification = indicatorClassification;
	}

	public IndicatorUnitSubgroup getIndicatorUnitSubgroup() {
		return indicatorUnitSubgroup;
	}

	public void setIndicatorUnitSubgroup(IndicatorUnitSubgroup indicatorUnitSubgroup) {
		this.indicatorUnitSubgroup = indicatorUnitSubgroup;
	}

}