package in.co.sdrc.sdrcdatacollector.jpadomains;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author azaruddin
 */
@Entity
@Data
public class EngineRole implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4136672948813915329L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	
	private Integer roleId;

	private String roleCode;

	private String roleName;
	
	private String description;
	
}

