package in.co.sdrc.sdrcdatacollector.jpadomains;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import in.co.sdrc.sdrcdatacollector.util.Status;
import lombok.Data;

/**
 * @author azaruddin
 */
@Entity
@Data
public class EnginesForm implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4420256298121424182L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private Integer formId; 

	private String name;

	private String dbName;
	
	
	private String fileNameToBeOutputted;
	
	@Enumerated(EnumType.STRING)
	private Status status=Status.ACTIVE;
	
	private Integer version;
}
