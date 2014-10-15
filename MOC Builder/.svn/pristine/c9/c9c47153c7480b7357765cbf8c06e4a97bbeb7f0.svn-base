/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.ItemType;

/**
 * {@link ItemImageRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class ItemImageRequest implements Request<ItemImageResponse> {
	
	private ItemType type;
	private String itemNumber;
	private int colorId;

    public ItemImageRequest(ItemType type, String itemNumber, int colorId) {
        super();
        this.type = type;
        this.itemNumber = itemNumber;
        this.colorId = colorId;
    }

    @Override
    public String getPath() {
        return "items/"+type.toString()+"/"+itemNumber+"/images/"+colorId;
    }

    @Override
    public Parameter[] getParameters() {
        return Parameter.EMPTY;
    }

    @Override
    public ItemImageParser getParser() {
        return new ItemImageParser();
    }
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
