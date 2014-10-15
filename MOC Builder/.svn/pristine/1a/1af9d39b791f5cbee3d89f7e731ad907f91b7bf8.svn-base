/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api.order;

import java.util.ArrayList;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.OrderDT;

/**
 * {@link OrdersParser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
final class OrdersParser extends Parser<OrdersResponse, ArrayList<OrderDT>> {

    OrdersParser() {
        super();
    }

    @Override
    protected TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<ArrayList<OrderDT>>> getResponseType() {
        return new TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<ArrayList<OrderDT>>>() {
            // Nothing to do.
        };
    }

    @Override
    protected OrdersResponse createResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<ArrayList<OrderDT>> response) {
        return new OrdersResponse(response);
    }
}
