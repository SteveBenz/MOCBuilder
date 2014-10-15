/*
 * GPLv3 
 */

package Bricklink.org.kleini.bricklink.data;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * {@link ResponseDT}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class ResponseDT<T> {

    private MetaDT meta;

    private T data;

    public ResponseDT() {
        super();
    }

    @JsonProperty("meta")
    public MetaDT getMeta() {
        return meta;
    }

    @JsonProperty("meta")
    public void setMeta(MetaDT meta) {
        this.meta = meta;
    }

    @JsonProperty("data")
    public T getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(T data) {
        this.data = data;
    }

}
