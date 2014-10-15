/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.Connectivity;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.ConnectivityDT;
import Bricklink.BrickBuilder.data.IDMappingDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link ConnectivitiesParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class ConnectivitiesParser extends
		Parser<ConnectivitiesResponse, List<ConnectivityDT>> {

	public ConnectivitiesParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<List<ConnectivityDT>>> getResponseType() {
		return new TypeReference<ResponseDT<List<ConnectivityDT>>>() {
			// Nothing to do.
		};
	}

	@Override
	protected ConnectivitiesResponse createResponse(
			ResponseDT<List<ConnectivityDT>> response) {
		return new ConnectivitiesResponse(response);
	}
}
