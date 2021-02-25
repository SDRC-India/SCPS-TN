package in.co.sdrc.scpstn.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class OrderDetailsOfCWC {

	@Column(name="no_of_children_with_the_orders_of_the_CWC")
	private String noOfChildrenWithOrdersOfCWC;
	
	@Column(name="no_of_children_without_the_orders_of_the_CWC")
	private String noOfChildrenWithoutOrdersOfCWC;
	
	@Column(name="rfc_residing_in_the_CCI_wto_of_CWC")
	private String reasonsforChildrenResidingInTheCCIWithoutCWCOrder;
}
