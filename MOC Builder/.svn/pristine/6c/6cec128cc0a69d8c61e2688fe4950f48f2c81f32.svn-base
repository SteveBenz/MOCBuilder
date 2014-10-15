/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.data;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link SupersetDT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SupersetDT {

	 /**
     * The ID of the color of the item
     */
    private ColorT color;

    /**
     * A list of the items included in the specified item
     */
    private List<EntryDT> entries;

    public SupersetDT() {
        super();
    }

    @JsonProperty("color_id")
    public ColorT getIdentifier() {
        return color;
    }

    @JsonProperty("color_id")
    public void setColorId(int identifier) throws Exception {
        this.color = ColorT.byId(identifier);
    }

    @JsonProperty("entries")
    public List<EntryDT> getEntries() {
        return entries;
    }

    @JsonProperty("entries")
    public void setEntries(List<EntryDT> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(entries);
        if (0 != color.getIdentifier()) {
            sb.append(',');
            sb.append(color.getIdentifier());
        }
        return sb.toString();
    }
}
