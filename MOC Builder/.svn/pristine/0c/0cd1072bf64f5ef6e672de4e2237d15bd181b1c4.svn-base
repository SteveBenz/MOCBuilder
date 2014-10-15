/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.category;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;

/**
 * {@link CategoriesRequest}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class CategoriesRequest implements Request<CategoriesResponse> {

    public CategoriesRequest() {
        super();
    }

    @Override
    public String getPath() {
        return "categories";
    }

    @Override
    public Parameter[] getParameters() {
        return Parameter.EMPTY;
    }

    @Override
    public CategoriesParser getParser() {
        return new CategoriesParser();
    }
    
    @Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
