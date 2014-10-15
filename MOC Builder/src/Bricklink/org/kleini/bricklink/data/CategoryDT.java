/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link CategoryDT}
 *
 * @author <a href="mailto:hismelf@kleini.org">Marcus Klein</a>
 */
public final class CategoryDT {

    private int identifier, parentId;

    private String name;

    public CategoryDT() {
        super();
    }

    @JsonProperty("category_id")
    public int getIdentifier() {
        return identifier;
    }

    @JsonProperty("category_id")
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    @JsonProperty("parent_id")
    public int getParentId() {
        return parentId;
    }

    @JsonProperty("parent_id")
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    @JsonProperty("category_name")
    public String getName() {
        return name;
    }

    @JsonProperty("category_name")
    public void setName(String name) {
        this.name = name;
    }
}
