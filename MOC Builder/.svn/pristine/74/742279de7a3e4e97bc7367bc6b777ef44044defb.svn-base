/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.order;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;

/**
 * {@link OrderRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class OrderRequest implements Request<OrderResponse> {

    private int orderId;

    public OrderRequest(int orderId) {
        super();
        this.orderId = orderId;
    }

    @Override
    public String getPath() {
        return "orders/" + Integer.toString(orderId);
    }

    @Override
    public Parameter[] getParameters() {
        return Parameter.EMPTY;
    }

    @Override
    public OrderParser getParser() {
        return new OrderParser();
    }
    
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
