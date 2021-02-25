package in.co.sdrc.sdrcdatacollector.jpadomains;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * @author azaruddin
 */
@Entity
@Data
public class Attachment {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer attachmentId;

	private String columnName;

	private String filePath;

	private Integer typeDetailId; // typedetails

	private Boolean isDeleted;

	private String originalName;

	private Long fileSize;

}
