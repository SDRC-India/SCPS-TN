package in.co.sdrc.scpstn.models;


import java.util.List;

public class ValueModel {

	private Integer key;
	private String value;
	private String metaData;
	private List<ValueModel> valueModelList;// added by harsh

	public List<ValueModel> getValueModelList() {
		return valueModelList;
	}

	public void setValueModelList(List<ValueModel> valueModelList) {
		this.valueModelList = valueModelList;
	}

	public Integer getKey() {
		return key;
	}

	public void setKey(Integer key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public ValueModel(Integer key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public ValueModel() {
		super();
	}

}
