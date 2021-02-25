package in.co.sdrc.sdrcdatacollector.models;

import lombok.Data;

/**
 * @author subham
 *
 */
@Data
public class AreaModel {
	
	private Integer areaId;

	private String areaName;

	private int parentAreaId;

	private String areaLevel;
	
	private Integer areaLevelId;

	private boolean isLive;

	private String areaCode;
	

}
