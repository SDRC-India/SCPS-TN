package in.co.sdrc.scpstn.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="publish_history")
public class PublishHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "publish_history_id")
	private Integer publishHistoryId;
	
	@Column(name="data_being_published_for_month")
	private Integer dataBeingPublishedForMonth;
	
	
	@Column(name="data_being_published_for_year")
	private Integer dataBeingPublishedForYear;
	
	@Column(name = "data_published_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataPublishedDate;

	public Integer getPublishHistoryId() {
		return publishHistoryId;
	}

	public void setPublishHistoryId(Integer publishHistoryId) {
		this.publishHistoryId = publishHistoryId;
	}

	public Integer getDataBeingPublishedForMonth() {
		return dataBeingPublishedForMonth;
	}

	public void setDataBeingPublishedForMonth(Integer dataBeingPublishedForMonth) {
		this.dataBeingPublishedForMonth = dataBeingPublishedForMonth;
	}

	public Integer getDataBeingPublishedForYear() {
		return dataBeingPublishedForYear;
	}

	public void setDataBeingPublishedForYear(Integer dataBeingPublishedForYear) {
		this.dataBeingPublishedForYear = dataBeingPublishedForYear;
	}

	public Date getDataPublishedDate() {
		return dataPublishedDate;
	}

	public void setDataPublishedDate(Date dataPublishedDate) {
		this.dataPublishedDate = dataPublishedDate;
	}
	
	
	
	
}
