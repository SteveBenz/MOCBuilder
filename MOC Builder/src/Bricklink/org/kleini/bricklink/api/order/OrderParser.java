/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.order;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.OrderDT;

/**
 * {@link OrderParser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class OrderParser extends Parser<OrderResponse, OrderDT> {

    public OrderParser() {
        super();
    }

    @Override
    protected TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<OrderDT>> getResponseType() {
        return new TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<OrderDT>>() {
            // Nothing to do.
        };
    }

    @Override
    protected OrderResponse createResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<OrderDT> response) {
        return new OrderResponse(response);
    }
}
