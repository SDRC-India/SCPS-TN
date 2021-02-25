package  in.co.sdrc.sdrcdatacollector.jpadomains;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author azaruddin
 */

@NoArgsConstructor
@ToString
@Entity
@Data
public class Type implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private String typeName;
 
	private String description;

	private Integer slugId;

	private Integer formId;

}
