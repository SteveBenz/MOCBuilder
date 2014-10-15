/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.Connectivity;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.ConnectivityDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link UploadConnectivityParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class UploadConnectivityParser extends
		Parser<UploadConnectivityResponse, ConnectivityDT> {

	public UploadConnectivityParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<ConnectivityDT>> getResponseType() {
		return new TypeReference<ResponseDT<ConnectivityDT>>() {
			// Nothing to do.
		};
	}

	@Override
	protected UploadConnectivityResponse createResponse(
			ResponseDT<ConnectivityDT> response) {
		return new UploadConnectivityResponse(response);
	}
}
