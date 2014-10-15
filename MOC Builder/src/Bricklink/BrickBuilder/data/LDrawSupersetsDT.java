package Bricklink.BrickBuilder.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class LDrawSupersetsDT {
	private String partId;
	private List<LDrawModuleDT> supersets;
	
	
	@JsonProperty("partId")
	public void setPartId(String partId) {
		this.partId = partId;
	}

	@JsonProperty("partId")
	public String getPartId() {
		return this.partId;
	}
	@JsonProperty("supersets")
	public void setSupersets(List<LDrawModuleDT> subparts) {
		this.supersets = subparts;
	}

	@JsonProperty("supersets")
	public List<LDrawModuleDT> getSupersets() {
		return this.supersets;
	}	
}
