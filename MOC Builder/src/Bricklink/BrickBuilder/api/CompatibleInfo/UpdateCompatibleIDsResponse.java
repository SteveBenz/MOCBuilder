 /*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.CompatibleInfo;

import java.util.List;

import Bricklink.BrickBuilder.data.IDMappingDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link UpdateCompatibleIDsResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class UpdateCompatibleIDsResponse extends Response<String> {

	protected UpdateCompatibleIDsResponse(
			ResponseDT<String> response) {
		super(response);
	}
}
