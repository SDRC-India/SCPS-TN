package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Entity
@Table(name = "subgroup_val")
public class Subgroup {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "subgroup_val_id")
	private Integer subgroupValueId;

	@Column(name = "subgroup_val")
	private String subgroupVal;

	@Column(name = "subgroup_val_order")
	private int subgroupValOrder;

	public Subgroup() {
		super();
	}

	public Subgroup(int subgroupId) {
		this.subgroupValueId = subgroupId;
	}

	public Integer getSubgroupValueId() {
		return subgroupValueId;
	}

	public void setSubgroupValueId(Integer subgroupValueId) {
		this.subgroupValueId = subgroupValueId;
	}

	public String getSubgroupVal() {
		return subgroupVal;
	}

	public void setSubgroupVal(String subgroupVal) {
		this.subgroupVal = subgroupVal;
	}

	public int getSubgroupValOrder() {
		return subgroupValOrder;
	}

	public void setSubgroupValOrder(int subgroupValOrder) {
		this.subgroupValOrder = subgroupValOrder;
	}

}
