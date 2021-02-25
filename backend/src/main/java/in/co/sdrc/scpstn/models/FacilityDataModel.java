package in.co.sdrc.scpstn.models;

import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import in.co.sdrc.scpstn.domain.ContactDetails;
import lombok.Data;

@Data
public class FacilityDataModel {
	private Integer facilityId;
	private String nameAndAddress;
	private String name;
	private String nameOfHead;
	private String phNo;
	private String emailId;
	private String websiteLink;
	private String establishmentDate;
	private String registrationNo;
	private String expiryDate;
	private String latestInspectionDate;
	private String sanctionedStrength;
	private String boys0To6;
	private String boys7To11;
	private String boys12To14;
	private String boys15To18;
	private String boysTotal;
	private String girls0To6;
	private String girls7To11;
	private String girls12To14;
	private String girls15To18;
	private String girlsTotal;
	private String noOfChildrenWithOrdersOfCWC;
	private String noOfChildrenWithoutOrdersOfCWC;
	private String reasonsforChildrenResidingInTheCCIWithoutCWCOrder;
	private Boolean detailsOfComplaintBoxPlaced;
	private String otherRemarks;
	private Integer designationId;
	private Integer areaId;
	private String facilityType;
}
