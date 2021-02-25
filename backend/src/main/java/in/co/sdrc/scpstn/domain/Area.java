package in.co.sdrc.scpstn.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;


@Entity
@Table(name = "area")
@Data
public class Area implements Serializable {


	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "area_id")
	private Integer areaId;

	@Column(name = "area_name")
	private String areaName;

	@Column(name = "parent_area_id")
	private int parentAreaId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "area_level_id_fk", nullable = false)
	private AreaLevel areaLevel;

	@Column(name = "created_date")
	private Timestamp createdDate;

	@Column(name = "updated_date")
	private Timestamp updatedDate;

	@Column(name = "is_live")
	private boolean isLive;

	@Column(name = "area_code")
	private String areaCode;

	@Column(name = "short_name")
	private String shortName;
	
	private String zone;

	public Area() {
		super();
	}

	public Area(Integer id) {
		this.areaId = id;
	}
	
//	@ManyToOne
//	@JoinColumn(name="AreaLevelId", nullable = false)
//	private AreaLevel areaLevel;

	// ******** bi-directional one-to-many association to UserAreaMapping
	// *******
	
	

}
