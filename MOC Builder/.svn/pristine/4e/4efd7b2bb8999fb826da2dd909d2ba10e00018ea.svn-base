/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.LinkedList;
import java.util.List;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.ColorDT;
import Bricklink.org.kleini.bricklink.data.ItemType;

/**
 * {@link SubsetsRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SubsetsRequest implements Request<SubsetsResponse> {

    private final ItemType type;
    private final String itemID;
    private final ColorDT color;

    public SubsetsRequest(ItemType type, String itemID, ColorDT color) {
        super();
        this.type = type;
        this.itemID = itemID;
        this.color = color;
    }

    public SubsetsRequest(ItemType type, String itemID) {
        this(type, itemID, null);
    }

    @Override
    public String getPath() {
        return "items/" + type.getLongId() + '/' + itemID + "/subsets";
    }

    @Override
    public Parameter[] getParameters() {
        List<Parameter> retval = new LinkedList<Parameter>();
        if (null != color) {
            retval.add(new Parameter("color_id", color.getIdentifier()));
        }
        retval.add(new Parameter("box", true));
        retval.add(new Parameter("instruction", true));
        retval.add(new Parameter("break_minifigs", true));
        retval.add(new Parameter("break_subsets", true));
        return retval.toArray(new Parameter[retval.size()]);
    }

    @Override
    public SubsetsParser getParser() {
        return new SubsetsParser();
    }
    
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
