package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;
import lombok.ToString;

@Data
@Embeddable
@ToString
public class ContactDetails {

	@Column(name = "name_of_head")
	private String nameOfHead;
	
	@Column(name="ph_no")
	private String phNo;
	
	@Column(name="email_id")
	private String emailId;
	
	@Column(name="website_link")
	private String websiteLink;
	
	
}
