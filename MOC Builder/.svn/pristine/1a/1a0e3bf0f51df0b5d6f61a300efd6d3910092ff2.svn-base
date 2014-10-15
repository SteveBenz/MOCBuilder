/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api.order;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;

/**
 * {@link OrdersRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class OrdersRequest implements Request<OrdersResponse> {

    public OrdersRequest() {
        super();
    }

    @Override
    public String getPath() {
        return "orders";
    }

    @Override
    public Parameter[] getParameters() {
        return new Parameter[] { new Parameter("direction", "in") };
    }

    @Override
    public OrdersParser getParser() {
        return new OrdersParser();
    }
    
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
