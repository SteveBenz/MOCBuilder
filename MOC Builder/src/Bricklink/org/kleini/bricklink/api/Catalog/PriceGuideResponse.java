/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.PriceGuideDT;

/**
 * {@link PriceGuideResponse}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class PriceGuideResponse extends Response<PriceGuideDT> {

    public PriceGuideResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<PriceGuideDT> response) {
        super(response);
    }

    public PriceGuideDT getPriceGuide() {
        return getResponse().getData();
    }
}
