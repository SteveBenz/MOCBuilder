/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.PriceDetailDT;
import Bricklink.org.kleini.bricklink.data.PriceGuideDT;

/**
 * {@link PriceGuideParser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class PriceGuideParser extends Parser<PriceGuideResponse, PriceGuideDT> {

    public PriceGuideParser() {
        super();
    }

    @Override
    protected TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<PriceGuideDT>> getResponseType() {
        return new TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<PriceGuideDT>>() {
            // Nothing to do.
        };
    }

    @Override
    protected PriceGuideResponse createResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<PriceGuideDT> response) {
        PriceGuideDT priceGuide = response.getData();
        List<PriceDetailDT> details = priceGuide.getDetail();
        Collections.sort(details, new Comparator<PriceDetailDT>() {
            @Override
            public int compare(PriceDetailDT o1, PriceDetailDT o2) {
                int compared = o1.getPrice().compareTo(o2.getPrice());
                if (0 == compared) {
                    // Order by decreasing quantity
                    compared = Integer.compare(o2.getQuantity(), o1.getQuantity());
                }
                return compared;
            }
        });
        return new PriceGuideResponse(response);
    }
}
