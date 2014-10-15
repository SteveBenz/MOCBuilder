/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.ItemType;

/**
 * {@link KnownColorsRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class KnownColorsRequest implements Request<KnownColorsResponse> {
	
	private ItemType type;
	private String itemNumber;

    public KnownColorsRequest(ItemType type, String itemNumber) {
        super();
        this.type = type;
        this.itemNumber = itemNumber;
    }

    @Override
    public String getPath() {
        return "items/"+type.toString()+"/"+itemNumber+"/colors";
    }

    @Override
    public Parameter[] getParameters() {
        return Parameter.EMPTY;
    }

    @Override
    public KnownColorsParser getParser() {
        return new KnownColorsParser();
    }
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
