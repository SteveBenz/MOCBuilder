/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.Connectivity;

import Bricklink.BrickBuilder.data.ConnectivityDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link UploadConnectivityResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class UploadConnectivityResponse extends Response<ConnectivityDT> {

	protected UploadConnectivityResponse(
			ResponseDT<ConnectivityDT> response) {
		super(response);
	}
}
