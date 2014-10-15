/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.order;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.OrderDT;

/**
 * {@link OrderResponse}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class OrderResponse extends Response<OrderDT> {

    public OrderResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<OrderDT> response) {
        super(response);
    }

    public OrderDT getOrder() {
        return getResponse().getData();
    }
}
