package in.co.sdrc.sdrcdatacollector.jpadomains;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CreationTimestamp;

import in.co.sdrc.sdrcdatacollector.models.AccessType;
import in.co.sdrc.sdrcdatacollector.util.Status;
import lombok.Data;

/**
 * @author azaruddin <br>
 *         We are using referencedColumnName in both role and both, because we
 *         dont want to map foreign keys to primary keys of domain, instead to
 *         roleId in EngineRole domain and formId in EngineForm domain
 */
@Entity
@Data
public class EnginesRoleFormMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@JoinColumn(name = "engine_role_id_fk", referencedColumnName = "roleId")
	@ManyToOne
	private EngineRole role;

	@JoinColumn(name = "engine_form_id_fk", referencedColumnName = "formId")
	@ManyToOne
	private EnginesForm form;

	@CreationTimestamp
	private Date createdDate;

	@Enumerated(EnumType.STRING)
	private AccessType accessType;

	@Enumerated(EnumType.STRING)
	private Status status = Status.ACTIVE;

}
