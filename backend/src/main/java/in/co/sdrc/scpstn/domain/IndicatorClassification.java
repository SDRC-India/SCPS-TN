package in.co.sdrc.scpstn.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import in.co.sdrc.scpstn.models.IndicatorClassificationType;
import in.co.sdrc.scpstn.models.Type;
/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Entity
@Table(name = "indicator_classification")
public class IndicatorClassification {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "indicator_classification_id")
	private Integer indicatorClassificationId;

//	@OneToMany(mappedBy = "indicatorClassification")
//	private List<Indicator> mappedIndicators;

	@ManyToOne
	@JoinColumn(name = "parent_id")
	private IndicatorClassification parent;

	@OneToMany(mappedBy = "parent")
	private List<IndicatorClassification> children;

	@Column(name = "classification_name")
	private String name;

	@Column(name="classification_short_name")
	private String shortName;
	
	@Column(name = "sector_ids")
	private String sectorIds;

	@Column(name = "index")
	private boolean index;

	@Column
	@Enumerated(EnumType.STRING)
	private Type type;

	@Column(name="indicatorclassificationtype")
	@Enumerated(EnumType.STRING)
	private IndicatorClassificationType indicatorClassificationType;

	@Column(name = "ic_order")
	private int icOrder;
	
	@Column(columnDefinition="character varying(255) default null")
	private String indexForSource;
	
	
	@Column(columnDefinition="character varying(255) default null")
	private String lineChartColor;
	
	public IndicatorClassification(){
	
	}

	/**
	 * @return the indexForSource
	 */
	public String getIndexForSource() {
		return indexForSource;
	}

	/**
	 * @return the lineChartColor
	 */
	public String getLineChartColor() {
		return lineChartColor;
	}

	/**
	 * @param indexForSource the indexForSource to set
	 */
	public void setIndexForSource(String indexForSource) {
		this.indexForSource = indexForSource;
	}

	/**
	 * @param lineChartColor the lineChartColor to set
	 */
	public void setLineChartColor(String lineChartColor) {
		this.lineChartColor = lineChartColor;
	}

	public IndicatorClassification(int id){
		this.indicatorClassificationId = id;
	}
	
	
	public Integer getIndicatorClassificationId() {
		return indicatorClassificationId;
	}

	public void setIndicatorClassificationId(Integer indicatorClassificationId) {
		this.indicatorClassificationId = indicatorClassificationId;
	}

	public IndicatorClassification getParent() {
		return parent;
	}

	public void setParent(IndicatorClassification parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public IndicatorClassificationType getIndicatorClassificationType() {
		return indicatorClassificationType;
	}

	public void setIndicatorClassificationType(IndicatorClassificationType indicatorClassificationType) {
		this.indicatorClassificationType = indicatorClassificationType;
	}

	public int getIcOrder() {
		return icOrder;
	}

	public void setIcOrder(int icOrder) {
		this.icOrder = icOrder;
	}

	public boolean isIndex() {
		return index;
	}

	public void setIndex(boolean index) {
		this.index = index;
	}

	public List<IndicatorClassification> getChildren() {
		return children;
	}

	public void setChildren(List<IndicatorClassification> children) {
		this.children = children;
	}

	public String getSectorIds() {
		return sectorIds;
	}

	public void setSectorIds(String sectorIds) {
		this.sectorIds = sectorIds;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

//	public List<Indicator> getMappedIndicators() {
//		return mappedIndicators;
//	}
//
//	public void setMappedIndicators(List<Indicator> mappedIndicators) {
//		this.mappedIndicators = mappedIndicators;
//	}

}
