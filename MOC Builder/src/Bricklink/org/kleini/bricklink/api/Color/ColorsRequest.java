/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Color;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;

/**
 * {@link ColorsRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class ColorsRequest implements Request<ColorsResponse> {

    public ColorsRequest() {
        super();
    }

    @Override
    public String getPath() {
        return "colors";
    }

    @Override
    public Parameter[] getParameters() {
        return Parameter.EMPTY;
    }

    @Override
    public ColorsParser getParser() {
        return new ColorsParser();
    }
    
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
