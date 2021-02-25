package in.co.sdrc.scpstn.models;

import java.util.List;

public class IndicatorClassificationModel {

	private int ic_NId;
	private String diYear;
	private String ic_GId;
	private byte ic_Global;
	private String ic_Info;
	private String ic_Name;
	private int ic_Order;
	private int ic_Parent_NId;
	private String ic_Short_Name;
	private String ic_Type;
	private String isbn;
	private String nature;
	private String publisher;
	private String sourceLink1;
	private String sourceLink2;
	private String title;
	private String children;
	
	private List<IndicatorObject> indicatorObject;
	

	public List<IndicatorObject> getIndicatorObject() {
		return indicatorObject;
	}

	public void setIndicatorObject(List<IndicatorObject> indicatorObject) {
		this.indicatorObject = indicatorObject;
	}

	public int getIc_NId() {
		return ic_NId;
	}

	public void setIc_NId(int ic_NId) {
		this.ic_NId = ic_NId;
	}

	public String getDiYear() {
		return diYear;
	}

	public void setDiYear(String diYear) {
		this.diYear = diYear;
	}

	public String getIc_GId() {
		return ic_GId;
	}

	public void setIc_GId(String ic_GId) {
		this.ic_GId = ic_GId;
	}

	public byte getIc_Global() {
		return ic_Global;
	}

	public void setIc_Global(byte ic_Global) {
		this.ic_Global = ic_Global;
	}

	public String getIc_Info() {
		return ic_Info;
	}

	public void setIc_Info(String ic_Info) {
		this.ic_Info = ic_Info;
	}

	public String getIc_Name() {
		return ic_Name;
	}

	public void setIc_Name(String ic_Name) {
		this.ic_Name = ic_Name;
	}

	public int getIc_Order() {
		return ic_Order;
	}

	public void setIc_Order(int ic_Order) {
		this.ic_Order = ic_Order;
	}

	public int getIc_Parent_NId() {
		return ic_Parent_NId;
	}

	public void setIc_Parent_NId(int ic_Parent_NId) {
		this.ic_Parent_NId = ic_Parent_NId;
	}

	public String getIc_Short_Name() {
		return ic_Short_Name;
	}

	public void setIc_Short_Name(String ic_Short_Name) {
		this.ic_Short_Name = ic_Short_Name;
	}

	public String getIc_Type() {
		return ic_Type;
	}

	public void setIc_Type(String ic_Type) {
		this.ic_Type = ic_Type;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getSourceLink1() {
		return sourceLink1;
	}

	public void setSourceLink1(String sourceLink1) {
		this.sourceLink1 = sourceLink1;
	}

	public String getSourceLink2() {
		return sourceLink2;
	}

	public void setSourceLink2(String sourceLink2) {
		this.sourceLink2 = sourceLink2;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getChildren() {
		return children;
	}

	public void setChildren(String children) {
		this.children = children;
	}



}
