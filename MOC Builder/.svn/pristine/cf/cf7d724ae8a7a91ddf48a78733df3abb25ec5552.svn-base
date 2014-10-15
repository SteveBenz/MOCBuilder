/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.ItemType;

/**
 * {@link SupersetsRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SupersetsRequest implements
		Request<SupersetsResponse> {

	private ItemType type;
	private String itemNumber;
	private int colorId;

	public SupersetsRequest(ItemType type, String itemNumber,
			int colorId) {
		super();
		this.type = type;
		this.itemNumber = itemNumber;
		this.colorId = colorId;
	}

	@Override
	public String getPath() {
		return "items/" + type.toString() + "/" + itemNumber + "/supersets";
	}

	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { new Parameter("type", type.toString()),
				new Parameter("no", itemNumber),
				new Parameter("color_id", colorId) };
	}

	@Override
	public SupersetsParser getParser() {
		return new SupersetsParser();
	}
	
	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
