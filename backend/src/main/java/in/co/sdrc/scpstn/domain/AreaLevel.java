package in.co.sdrc.scpstn.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "area_level")
public class AreaLevel implements Serializable {

	private static final long serialVersionUID = 1519381375815795764L;

	@Id
	@Column(name = "area_level_id_pk")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer areaLevelId;

	@Column(name = "area_level_name", nullable = false)
	private String areaLevelName;

}
