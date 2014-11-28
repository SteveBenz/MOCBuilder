/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.Connectivity;

import java.util.ArrayList;
import java.util.List;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;

/**
 * {@link ConnectivitiesRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class ConnectivitiesRequest implements
		Request<ConnectivitiesResponse> {
	private boolean composeUncertified = false;

	public ConnectivitiesRequest() {
		super();
	}

	public ConnectivitiesRequest(boolean compoaseUncertified) {
		super();
		this.composeUncertified = compoaseUncertified;
	}

	@Override
	public String getPath() {
		return "/connectivity";
	}

	@Override
	public Parameter[] getParameters() {
		List<Parameter> retval = new ArrayList<Parameter>();
		retval.add(new Parameter("composeuncertified",
				this.composeUncertified ? "1" : "0"));
		return retval.toArray(new Parameter[retval.size()]);
	}

	@Override
	public ConnectivitiesParser getParser() {
		return new ConnectivitiesParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
