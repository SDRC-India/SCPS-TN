package in.co.sdrc.scpstn.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Entity
@Table(name = "data",

indexes = { @Index(name = "i_indicator_unit_subgroup_id_fk_2", columnList = "indicator_unit_subgroup_id_fk"), @Index(name = "i_indicator_id_fk_2", columnList = "indicator_id_fk"),
		@Index(name = "i_unit_id_fk_2", columnList = "unit_id_fk"), @Index(name = "i_subgroup_id_fk_2", columnList = "subgroup_id_fk"),
		@Index(name = "i_ius_2", columnList = "indicator_unit_subgroup_id_fk,indicator_id_fk,unit_id_fk,subgroup_id_fk")

},

		uniqueConstraints={@UniqueConstraint(name="unq_ius_i_u_s_source_time_area",
		columnNames={"indicator_unit_subgroup_id_fk","indicator_id_fk","unit_id_fk",
				"subgroup_id_fk","time_period_id_fk","district_id_fk","source_id_or_ic_id_fk"})}
		)
public class Data {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "data_id")
	private Integer dataId;

	@ManyToOne
	@JoinColumn(name = "indicator_unit_subgroup_id_fk")
	private IndicatorUnitSubgroup indicatorUnitSubgroup;

	@ManyToOne
	@JoinColumn(name = "indicator_id_fk")
	private Indicator indicator;

	@ManyToOne
	@JoinColumn(name = "unit_id_fk")
	private Unit unit;

	@ManyToOne
	@JoinColumn(name = "subgroup_id_fk")
	private Subgroup subgroup;

	@ManyToOne
	@JoinColumn(name = "time_period_id_fk")
	private Timeperiod timePeriod;

	@ManyToOne
	@JoinColumn(name = "source_id_or_ic_id_fk")
	private IndicatorClassification source;

	@ManyToOne
	@JoinColumn(name = "district_id_fk")
	private Area area;

	@Column(columnDefinition = "numeric(5,2)")
	private Double percentage;

	@Column(name = "numerator")
	private int numerator;

	@Column(name = "denominator")
	private int denominator;

	@Column(name = "published",columnDefinition="boolean default 'false'")
	private boolean published;
	
	@Column(columnDefinition="character varying(255) default null")
	private String indicatorName;
	
	@Column(columnDefinition="character varying(255) default null")
	private String unitName;
	
	@Column(columnDefinition="character varying(255) default null")
	private String subgroupName;
	
	@Column(columnDefinition="character varying(255) default null")
	private String sectorName;
	
	@Column(columnDefinition="character varying(255) default null")
	private String subsectorName;
	
	@Column(columnDefinition="character varying(255) default null")
	private String areaName;
	
	@Column(columnDefinition="character varying(255) default null")
	private String sourceName;
	
	@Column(columnDefinition="character varying(255) default null")
	private String indicatorClassifier;

	@Transient
	private BigDecimal normalizedValue;

	public Integer getDataId() {
		return dataId;
	}

	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}

	public IndicatorUnitSubgroup getIndicatorUnitSubgroup() {
		return indicatorUnitSubgroup;
	}

	public void setIndicatorUnitSubgroup(IndicatorUnitSubgroup indicatorUnitSubgroup) {
		this.indicatorUnitSubgroup = indicatorUnitSubgroup;
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

	public Timeperiod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(Timeperiod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public IndicatorClassification getSource() {
		return source;
	}

	public void setSource(IndicatorClassification source) {
		this.source = source;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}



	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public int getNumerator() {
		return numerator;
	}

	public void setNumerator(int numerator) {
		this.numerator = numerator;
	}

	public int getDenominator() {
		return denominator;
	}

	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}

	public BigDecimal getNormalizedValue() {
		return normalizedValue;
	}

	public void setNormalizedValue(BigDecimal normalizedValue) {
		this.normalizedValue = normalizedValue;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	// business method
	public void normalizeValue(BigDecimal maximum, BigDecimal minimum, boolean isHighIsGood) {

		if (maximum.subtract(minimum).compareTo(new BigDecimal(0.00)) == 0) {
			setNormalizedValue(new BigDecimal(0.00));
			return;
		}
		if (maximum.compareTo(new BigDecimal(0.00)) == 0 && minimum.compareTo(new BigDecimal(0.00)) == 0) {
			setNormalizedValue(new BigDecimal(0.00));
		}

		if (isHighIsGood) {
			setNormalizedValue((new BigDecimal(percentage).subtract(minimum)).divide((maximum.subtract(minimum)), 2, RoundingMode.HALF_UP).setScale(2));
		} else {
			setNormalizedValue((maximum.subtract(new BigDecimal(percentage))).divide((maximum.subtract(minimum)), 2, RoundingMode.HALF_UP).setScale(2));
		}

	}

	@Override
	public String toString() {
		return "Data [dataId=" + dataId + ", indicator=" + indicator + ", timePeriod=" + timePeriod + ", source=" + source + ", area=" + area + ", percentage=" + percentage + ", numerator=" + numerator + ", denominator=" + denominator + ", published=" + published + ", normalizedValue=" + normalizedValue + "]";
	}

	public String getIndicatorName() {
		return indicatorName;
	}

	public void setIndicatorName(String indicatorName) {
		this.indicatorName = indicatorName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getSubgroupName() {
		return subgroupName;
	}

	public void setSubgroupName(String subgroupName) {
		this.subgroupName = subgroupName;
	}

	public String getSectorName() {
		return sectorName;
	}

	public void setSectorName(String sectorName) {
		this.sectorName = sectorName;
	}

	public String getSubsectorName() {
		return subsectorName;
	}

	public void setSubsectorName(String subsectorName) {
		this.subsectorName = subsectorName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getIndicatorClassifier() {
		return indicatorClassifier;
	}

	public void setIndicatorClassifier(String indicatorClassifier) {
		this.indicatorClassifier = indicatorClassifier;
	}
	
	

}
