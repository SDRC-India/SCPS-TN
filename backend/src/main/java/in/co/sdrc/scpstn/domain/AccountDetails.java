package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Data;

import org.sdrc.usermgmt.domain.Account;

@Data
@Entity 
@Table(name="account_details")
public class AccountDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_details_id")
	private Integer accountDetailsId;
	
	@OneToOne
	@JoinColumn(name="acc_id_fk")
	private Account account;
	
	@Column(name="full_name")
	private String fullName;
	
	@Column(name="mobile_no")
	private String mblNo;

	
	
}
