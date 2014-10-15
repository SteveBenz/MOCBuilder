/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.ArrayList;
import java.util.List;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.ConditionT;
import Bricklink.org.kleini.bricklink.data.CountryT;
import Bricklink.org.kleini.bricklink.data.CurrencyT;
import Bricklink.org.kleini.bricklink.data.GuideTypeDT;
import Bricklink.org.kleini.bricklink.data.ItemType;

/**
 * {@link PriceGuideRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class PriceGuideRequest implements Request<PriceGuideResponse> {

    private final ItemType type;
    private final String itemID;
    private final int colorID;
    private final GuideTypeDT guideType;
    private final ConditionT newOrUsed;
    private final CountryT country;

    public PriceGuideRequest(ItemType type, String itemID, int colorID, GuideTypeDT guideType, ConditionT newOrUsed, CountryT country) {
        super();
        this.type = type;
        this.itemID = itemID;
        this.colorID = colorID;
        this.guideType = guideType;
        this.newOrUsed = newOrUsed;
        this.country = country;
    }

    public PriceGuideRequest(ItemType type, String itemID, int colorID, GuideTypeDT guideType, ConditionT newOrUsed) {
        this(type, itemID, colorID, guideType, newOrUsed, null);
    }

    @Override
    public String getPath() {
        return "items/" + type.name().toLowerCase() + '/' + itemID + "/price";
    }

    @Override
    public Parameter[] getParameters() {
        List<Parameter> retval = new ArrayList<Parameter>();
        retval.add(new Parameter("color_id", colorID));
        retval.add(new Parameter("guide_type", guideType.getParamValue()));
        retval.add(new Parameter("new_or_used", newOrUsed.name()));
        retval.add(new Parameter("currency_code", CurrencyT.EUR.name()));
        if (null != country) {
            retval.add(new Parameter("country_code", country.name()));
        }
        retval.add(new Parameter("vat", "Y")); // Vat option must be Y, N, or O
        return retval.toArray(new Parameter[retval.size()]);
    }

    @Override
    public PriceGuideParser getParser() {
        return new PriceGuideParser();
    }
    
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
