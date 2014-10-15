/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link EntryDT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class EntryDT {

    /**
     * An object representation of the item that is included in the specified item
     */
    private ItemDT item;

    /**
     * The ID of the color of the item
     */
    private ColorT color;

    /**
     * The number of items that are included in
     */
    private int quantity;

    /**
     * The number of items that are appear as "extra" item
     */
    private int extraQuantity;

    /**
     * Indicates that the item is appear as "alternate" item in this specified item
     */
    private boolean alternate;
    
    private AppearsT appears_as;

    public EntryDT() {
        super();
    }

    @JsonProperty("item")
    public ItemDT getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(ItemDT item) {
        this.item = item;
    }

    public ColorT getColor() {
        return color;
    }

    public void setColor(ColorT color) {
        this.color = color;
    }

    @JsonProperty("color_id")
    public void setColorID(int colorId) throws Exception {
        this.color = ColorT.byId(colorId);
    }

    @JsonProperty("quantity")
    public int getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @JsonProperty("extra_quantity")
    public int getExtraQuantity() {
        return extraQuantity;
    }

    @JsonProperty("extra_quantity")
    public void setExtraQuantity(int extraQuantity) {
        this.extraQuantity = extraQuantity;
    }

    @JsonProperty("is_alternate")
    public boolean isAlternate() {
        return alternate;
    }

    @JsonProperty("is_alternate")
    public void setAlternate(boolean alternate) {
        this.alternate = alternate;
    }
    
    @JsonProperty("appears_as")
    public void setAppearsAs(String code){
    	try {
			this.appears_as = AppearsT.byCode(code);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			appears_as = AppearsT.Unknown;
		}
    }
    
    @JsonProperty("appears_as")
    public String getAppearsAs(){
    	return appears_as.getCode();
    }

    @Override
    public String toString() {
        return "Entry [" + quantity + '+' + extraQuantity + ' ' + color + ' ' + item + ',' + alternate + ']';
    }
}
