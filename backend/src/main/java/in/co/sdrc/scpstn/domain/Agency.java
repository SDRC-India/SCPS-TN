package in.co.sdrc.scpstn.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;

/**
 * 
 * @author Azaruddin (azaruddin@sdrc.co.in)
 *
 */
@Entity
@Table(name = "agency")
public class Agency {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "agency_id")
	private Integer agencyId;

	@Column(name = "agency_name")
	private String agencyName;

	@Column(name = "last_day_for_data_entry")
	private int lastDayForDataEntry;

	@Column(name = "last_day_for_data_edit")
	private int lastDayForDataEdit;

	@Column(name = "agg_start_day")
	private int aggStartDay;

	@Column(name = "display_last_no_of_month_in_indicator_mgmt")
	private int noOfMonth;

	@Column(name = "display_percentage")
	private boolean displayPercentage;

	@Column(name = "display_number")
	private boolean displayNumber;
	
	@Column(name="pdf_header_file_name")
	private String pdfHeaderFileName;
	
	@Column(name="pdf_footer_file_name")
	private String pdfFooterFileName;
	
	@Column(name="domain_name")
	private String domainName;

	@OneToOne
	@JoinColumn(name = "state_id_fk")
	private Area state;

	@Column(name = "last_aggregation_date")
	@Temporal(TemporalType.DATE)
	private Date lastAggregationDate;

	@Column(name = "auto_publish_on_day")
	private int autoPublishOnDay;

	@Column(name = "encrypted_agency_id")
	private String encryptedAgencyId;
	
	
	@Column(name = "map_url")
	private String mapUrl;

	public Agency() {

	}

	public Agency(int agencyId) {
		this.agencyId = agencyId;
	}

	public Integer getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(Integer agencyId) {
		this.agencyId = agencyId;
	}

	public String getAgencyName() {
		return agencyName;
	}

	public void setAgencyName(String agencyName) {
		this.agencyName = agencyName;
	}

	public int getLastDayForDataEntry() {
		return lastDayForDataEntry;
	}

	public void setLastDayForDataEntry(int lastDayForDataEntry) {
		this.lastDayForDataEntry = lastDayForDataEntry;
	}

	public int getLastDayForDataEdit() {
		return lastDayForDataEdit;
	}

	public void setLastDayForDataEdit(int lastDayForDataEdit) {
		this.lastDayForDataEdit = lastDayForDataEdit;
	}

	public int getNoOfMonth() {
		return noOfMonth;
	}

	public void setNoOfMonth(int noOfMonth) {
		this.noOfMonth = noOfMonth;
	}

	public boolean isDisplayPercentage() {
		return displayPercentage;
	}

	public void setDisplayPercentage(boolean displayPercentage) {
		this.displayPercentage = displayPercentage;
	}

	public boolean isDisplayNumber() {
		return displayNumber;
	}

	public void setDisplayNumber(boolean displayNumber) {
		this.displayNumber = displayNumber;
	}

	public Area getState() {
		return state;
	}

	public void setState(Area state) {
		this.state = state;
	}

	public Date getLastAggregationDate() {
		return lastAggregationDate;
	}

	public void setLastAggregationDate(Date lastAggregationDate) {
		this.lastAggregationDate = lastAggregationDate;
	}

	public int getAggStartDay() {
		return aggStartDay;
	}

	public void setAggStartDay(int aggStartDay) {
		this.aggStartDay = aggStartDay;
	}

	public int getAutoPublishOnDay() {
		return autoPublishOnDay;
	}

	public void setAutoPublishOnDay(int autoPublishOnDay) {
		this.autoPublishOnDay = autoPublishOnDay;
	}

	public String getEncryptedAgencyId() {
		return encryptedAgencyId;
	}

	public void setEncryptedAgencyId(String encryptedAgencyId) {
		this.encryptedAgencyId = encryptedAgencyId;
	}

	public String createEncryptedAgencyId() {
		MessageDigestPasswordEncoder encoder = new MessageDigestPasswordEncoder("MD5");
		return encoder.encodePassword(agencyName, agencyName + "_" + agencyName);
	}

	public String getPdfHeaderFileName() {
		return pdfHeaderFileName;
	}

	public void setPdfHeaderFileName(String pdfHeaderFileName) {
		this.pdfHeaderFileName = pdfHeaderFileName;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String getMapUrl() {
		return mapUrl;
	}

	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}

	public String getPdfFooterFileName() {
		return pdfFooterFileName;
	}

	public void setPdfFooterFileName(String pdfFooterFileName) {
		this.pdfFooterFileName = pdfFooterFileName;
	}

	
	
}
