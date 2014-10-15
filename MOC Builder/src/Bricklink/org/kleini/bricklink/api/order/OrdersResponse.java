/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api.order;

import java.util.ArrayList;
import java.util.List;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.OrderDT;

/**
 * {@link OrdersResponse}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class OrdersResponse extends Response<ArrayList<OrderDT>> {

    public OrdersResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<ArrayList<OrderDT>> response) {
        super(response);
    }

    public List<OrderDT> getOrders() {
        return getResponse().getData();
    }
}
