package in.co.sdrc.scpstn.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sdrc.usermgmt.domain.Account;

import lombok.Data;

@Data
@Entity
@Table(name="account_facility_mapping")
public class AccountFacilityMapping {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_facility_mapping_id")
	private Integer accountFacilityMappingId;
	
	
	@ManyToOne
	@JoinColumn(name="facility_id_fk")
	private Facility facility;
		
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="acc_id_fk")
	private Account account;
	
	@Column(name="is_live")
	private boolean isLive;
}
