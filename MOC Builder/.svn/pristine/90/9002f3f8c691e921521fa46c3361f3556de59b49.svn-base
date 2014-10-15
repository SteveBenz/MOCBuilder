package Bricklink.BrickBuilder.data;

import org.codehaus.jackson.annotate.JsonProperty;

public class IDMappingDT {
	private PartIdDT fromId;
	private PartIdDT toId;
	private boolean certified;

	@JsonProperty("partIdFrom")
	public void setFromId(PartIdDT id) {
		this.fromId = id;
	}

	@JsonProperty("partIdFrom")
	public PartIdDT getFromId() {
		return this.fromId;
	}
	@JsonProperty("partIdTo")
	public void setToId(PartIdDT id) {
		this.toId = id;
	}

	@JsonProperty("partIdTo")
	public PartIdDT getToId() {
		return this.toId;
	}
	
	@JsonProperty("certified")
	public void setCertified(boolean certified) {
		this.certified = certified;
	}

	@JsonProperty("certified")
	public boolean getCertified() {
		return this.certified;
	}
}
