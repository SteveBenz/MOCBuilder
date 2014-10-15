/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.Connectivity;

import java.util.List;

import Bricklink.BrickBuilder.data.ConnectivityDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link ConnectivitiesResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class ConnectivitiesResponse extends Response<List<ConnectivityDT>> {

	protected ConnectivitiesResponse(
			ResponseDT<List<ConnectivityDT>> response) {
		super(response);
	}

	public List<ConnectivityDT> getConnectivityList() {
		return getResponse().getData();
	}
}
