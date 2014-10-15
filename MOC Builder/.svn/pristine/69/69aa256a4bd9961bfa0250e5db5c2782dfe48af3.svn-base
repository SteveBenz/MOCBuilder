 /*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.CompatibleInfo;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.IDMappingDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link UpdateCompatibleIDsParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class UpdateCompatibleIDsParser extends
		Parser<UpdateCompatibleIDsResponse, String> {

	public UpdateCompatibleIDsParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<String>> getResponseType() {
		return new TypeReference<ResponseDT<String>>() {
			// Nothing to do.
		};
	}

	@Override
	protected UpdateCompatibleIDsResponse createResponse(
			ResponseDT<String> response) {
		return new UpdateCompatibleIDsResponse(response);
	}
}
