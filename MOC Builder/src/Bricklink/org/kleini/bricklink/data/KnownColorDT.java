/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link KnownColorDT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class KnownColorDT {

    private int identifier, quantity;

    public KnownColorDT() {
        super();
    }

    @JsonProperty("color_id")
    public int getIdentifier() {
        return identifier;
    }

    @JsonProperty("color_id")
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
    
    @JsonProperty("quantity")
    public int getQuantity() {
        return quantity;
    }

    @JsonProperty("quantity")
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
