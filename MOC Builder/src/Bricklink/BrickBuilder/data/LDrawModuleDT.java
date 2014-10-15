package Bricklink.BrickBuilder.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class LDrawModuleDT {
	private String id;
	private String moduleName;
	private String author;
	private List<String> tags;
	private String state;
	private String fileURL;
	private String registDate;
	private String updateDate;

	@JsonProperty("moduleId")
	public void setId(String id) {
		this.id = id;
	}

	@JsonProperty("moduleId")
	public String getId() {
		return this.id;
	}
	
	@JsonProperty("moduleName")
	public void setName(String name) {
		this.moduleName = name;
	}

	@JsonProperty("moduleName")
	public String getName() {
		return this.moduleName;
	}
	
	@JsonProperty("author")
	public void setAuthor(String author) {
		this.author = author;
	}

	@JsonProperty("author")
	public String getAuthor() {
		return this.author;
	}
	
	@JsonProperty("tags")
	public void setTags(List<String> tags) {
		this.tags  = tags;
	}

	@JsonProperty("tags")
	public List<String> getTags() {
		return this.tags;
	}

	@JsonProperty("state")
	public void setState(String state) {
		this.state = state;
	}

	@JsonProperty("state")
	public String getDomain() {
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
