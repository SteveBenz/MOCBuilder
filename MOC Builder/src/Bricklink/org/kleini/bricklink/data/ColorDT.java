/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link ColorDT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class ColorDT {

    private int identifier;

    private String name, code, type;

    public ColorDT() {
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

    @JsonProperty("color_name")
    public String getName() {
        return name;
    }

    @JsonProperty("color_name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("color_code")
    public String getCode() {
        return code;
    }

    @JsonProperty("color_code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("color_type")
    public String getType() {
        return type;
    }

    @JsonProperty("color_type")
    public void setType(String type) {
        this.type = type;
    }
}
