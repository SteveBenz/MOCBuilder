package Bricklink.BrickBuilder.data;

import org.codehaus.jackson.annotate.JsonProperty;

public class ConnectivityDT {
	private String id;
	private String state;
	private String fileURL;
	private String registDate;
	private String updateDate;

	@JsonProperty("partId")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("partId")
	public String getId() {
		return this.id;
	}

	@JsonProperty("state")
	public void setState(String state) {
		this.state = state;
	}

	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@JsonProperty("fileURL")
	public void setFileURL(String url) {
		this.fileURL = url;
	}

	@JsonProperty("fileURL")
	public String getFileURL() {
		return this.fileURL;
	}

	@JsonProperty("registDate")
	public void setRegistDate(String date) {
		this.registDate = date;
	}

	@JsonProperty("registDate")
	public String getRegistDate() {
		return this.registDate;
	}

	@JsonProperty("updateDate")
	public void setUpdateDate(String date) {
		this.updateDate = date;
	}

	@JsonProperty("updateDate")
	public String getUpdateDate() {
		return this.updateDate;
	}
}
