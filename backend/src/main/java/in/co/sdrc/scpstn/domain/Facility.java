package in.co.sdrc.scpstn.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sdrc.usermgmt.domain.Designation;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

@Data
@Entity 
@Table(name="facility")
@ToString
public class Facility {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "facility_id")
	private Integer facilityId;

	@Column(name="name_and_address")
	private String nameAndAddress;
	
	@Column(name="name",columnDefinition="character varying(255) default ''")
	private String name;
	
	@Embedded
	private ContactDetails contactDetails;
	
	@Column(name="establishment_date")
	private String establishmentDate;
	
	@Column(name="registration_no")
	private String registrationNo;
	
	@Column(name="expiry_date")
	private String expiryDate;
	
	@Column(name="latest_inspection_date")
	private String latestInspectionDate;
	
	@Column(name="sanctioned_strength")
	private String sanctionedStrength;
	
	@Embedded
	private NumberOfChildrenPresent numberOfChildrenPresent;
	
	@Embedded
	private OrderDetailsOfCWC orderDetailsOfCWC;
	
	@Column(name="details_of_complaint_box_placed")
	private Boolean detailsOfComplaintBoxPlaced;
	
	@Column(name="other_remarks")
	private String otherRemarks;
	
	@ManyToOne
	@JoinColumn(name="designation_id_fk")
	private Designation designation;
	
	@ManyToOne
	@JoinColumn(name="area_id_fk")
	private Area area;
	
	private String facilityType;
	
	@JsonIgnore
	@OneToMany(mappedBy="facility",fetch=FetchType.EAGER)
	private List<AccountFacilityMapping> afm;
	
	@Column
	private boolean isActive;
	
	public Facility() {
		
	}
	public Facility(Integer facilityDetailsId) {
		this.facilityId = facilityDetailsId;
	}

}
