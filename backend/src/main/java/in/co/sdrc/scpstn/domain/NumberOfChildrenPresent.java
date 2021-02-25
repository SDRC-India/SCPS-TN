package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class NumberOfChildrenPresent {

	@Column(name="boys_0_6")
	private String boys0To6;
	
	@Column(name="boys_7_11")
	private String boys7To11;
	
	@Column(name="boys_12_14")
	private String boys12To14;
	
	@Column(name="boys_15_18")
	private String boys15To18;
	
	@Column(name="boys_total")
	private String boysTotal;
	
	@Column(name="girls_0_6")
	private String girls0To6;
	
	@Column(name="girls_7_11")
	private String girls7To11;
	
	@Column(name="girls_12_14")
	private String girls12To14;
	
	@Column(name="girls_15_18")
	private String girls15To18;
	
	
	@Column(name="girls_total")
	private String girlsTotal;
}
