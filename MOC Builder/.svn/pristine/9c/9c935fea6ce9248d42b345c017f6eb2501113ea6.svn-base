package Bricklink.BrickBuilder.data;

import org.codehaus.jackson.annotate.JsonProperty;

public class SubpartDT {
	private String partId;
	private Integer colorId;
	private Integer quantity;
	
	public SubpartDT(){		
	}
	
	public SubpartDT(String id, Integer colorId, Integer quantity){
		this.partId = id;
		this.colorId = colorId;
		this.quantity = quantity;
	}

	@JsonProperty("partId")
	public void setPartId(String id) {
		this.partId = id;
	}

	@JsonProperty("partId")
	public String getPartId() {
		return this.partId;
	}
	@JsonProperty("colorId")
	public void setColorId(Integer color) {
		this.colorId = color;
	}

	@JsonProperty("colorId")
	public Integer getColorId() {
		return this.colorId;
	}
	
	@JsonProperty("quantity")
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@JsonProperty("quantity")
	public Integer getQuantity() {
		return this.quantity;
	}
}
