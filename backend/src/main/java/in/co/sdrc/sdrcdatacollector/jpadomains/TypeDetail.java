package in.co.sdrc.sdrcdatacollector.jpadomains;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author azaruddin
 */
@Data
@AllArgsConstructor
@ToString
@Entity
@NoArgsConstructor
public class TypeDetail implements Serializable {

	private static final long serialVersionUID = 7158994858633568625L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;

	private Integer slugId;

	private String name;

	@JoinColumn(name="type_id_fk")
	@ManyToOne
	private Type type;

	private Integer orderLevel;

	private Integer formId;

	private String score;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "type_typedetails_mapping", joinColumns = {
			@JoinColumn(name = "type_details_id_fk", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "type_id_fk", referencedColumnName = "id") })
	private List<Type> parentIds;

	@org.hibernate.annotations.Type(type = "jsonb-node")
	@Column(columnDefinition = "jsonb default null")
	private JsonNode languages;
	// @Override
	// public int compareTo(TypeDetail o) {
	// // TODO Auto-generated method stub
	// return this.slugId.compareTo(o.getSlugId());
	// }

}
