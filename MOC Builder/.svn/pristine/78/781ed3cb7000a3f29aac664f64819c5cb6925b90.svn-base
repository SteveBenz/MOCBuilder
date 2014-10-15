/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.ItemType;

/**
 * {@link ItemRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class ItemRequest implements Request<ItemResponse> {
	
	private ItemType type;
	private String itemNumber;

    public ItemRequest(ItemType type, String itemNumber) {
        super();
        this.type = type;
        this.itemNumber = itemNumber;
    }

    @Override
    public String getPath() {
        return "items/"+type.toString()+"/"+itemNumber;
    }

    @Override
    public Parameter[] getParameters() {
        return Parameter.EMPTY;
    }

    @Override
    public ItemParser getParser() {
        return new ItemParser();
    }
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
