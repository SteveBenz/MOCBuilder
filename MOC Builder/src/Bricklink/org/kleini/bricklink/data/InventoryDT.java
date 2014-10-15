/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.data;

import java.math.BigDecimal;
import java.util.Date;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link InventoryDT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
@JsonIgnoreProperties({"color_name"})
public final class InventoryDT {

    /**
     * The ID of the inventory
     */
    private int identifier;

    /**
     * An object representation of the item
     */
    private ItemDT item;

    /**
     * The color of the item
     */
    private ColorT color;

    /**
     * The number of items included in this inventory
     */
    private int quantity;

    /**
     * Indicates whether the item is new or used
     */
    private ConditionT condition;

    /**
     * Indicates whether the set is complete or incomplete 
     * (This value is valid only for SET type)
     */
    private CompletenessT completeness;

    /**
     * The original price of this item per sale unit
     */
    private BigDecimal price;

    /**
     * The ID of the parent lot that this lot is bound to
     */
    private int bindId;

    /**
     * A short description for this inventory
     */
    private String description;

    /**
     * User remarks on this inventory
     */
    private String remarks;

    /**
     * Buyers can buy this item only in multiples of the bulk amount
     */
    private int bulk;

    /**
     * Indicates whether the item retains in inventory after it is sold out
     */
    private boolean retain;

    /**
     * Indicates whether the item appears only in owner?™s inventory
     */
    private boolean stockRoom;

    /**
     * Indicates the stockroom that the item to be placed when the user uses multiple stockroom
     */
    private String stockRoomId;

    /**
     * The time this lot is created
     */
    private Date created;

    public InventoryDT() {
        super();
    }

    @JsonProperty("inventory_id")
    public int getIdentifier() {
        return identifier;
    }

    @JsonProperty("inventory_id")
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
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

    @JsonProperty("new_or_used")
    public ConditionT getCondition() {
        return condition;
    }

    @JsonProperty("new_or_used")
    public void setCondition(ConditionT condition) {
        this.condition = condition;
    }

    public CompletenessT getCompleteness() {
        return completeness;
    }

    public void setCompleteness(CompletenessT completeness) {
        this.completeness = completeness;
    }

    @JsonProperty("completeness")
    public void setCompleteness(char identifier) throws Exception {
        this.completeness = CompletenessT.byId(identifier);
    }

    @JsonProperty("unit_price")
    public BigDecimal getPrice() {
        return price;
    }

    @JsonProperty("unit_price")
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @JsonProperty("bind_id")
    public int getBindId() {
        return bindId;
    }

    @JsonProperty("bind_id")
    public void setBindId(int bindId) {
        this.bindId = bindId;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("remarks")
    public String getRemarks() {
        return remarks;
    }

    @JsonProperty("remarks")
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JsonProperty("bulk")
    public int getBulk() {
        return bulk;
    }

    @JsonProperty("bulk")
    public void setBulk(int bulk) {
        this.bulk = bulk;
    }

    @JsonProperty("is_retain")
    public boolean isRetain() {
        return retain;
    }

    @JsonProperty("is_retain")
    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    @JsonProperty("is_stock_room")
    public boolean isStockRoom() {
        return stockRoom;
    }

    @JsonProperty("is_stock_room")
    public void setStockRoom(boolean stockRoom) {
        this.stockRoom = stockRoom;
    }

    @JsonProperty("stock_room_id")
    public String getStockRoomId() {
        return stockRoomId;
    }

    @JsonProperty("stock_room_id")
    public void setStockRoomId(String stockRoomId) {
        this.stockRoomId = stockRoomId;
    }

    @JsonProperty("date_created")
    public Date getCreated() {
        return created;
    }

    @JsonProperty("date_created")
    public void setCreated(Date created) {
        this.created = created;
    }
}
