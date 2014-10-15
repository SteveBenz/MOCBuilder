/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawPart;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.ConnectivityDT;
import Bricklink.BrickBuilder.data.IDMappingDT;
import Bricklink.BrickBuilder.data.LDrawPartDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link LDrawPartsParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class LDrawPartsParser extends
		Parser<LDrawPartsResponse, List<LDrawPartDT>> {

	public LDrawPartsParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<List<LDrawPartDT>>> getResponseType() {
		return new TypeReference<ResponseDT<List<LDrawPartDT>>>() {
			// Nothing to do.
		};
	}

	@Override
	protected LDrawPartsResponse createResponse(
			ResponseDT<List<LDrawPartDT>> response) {
		return new LDrawPartsResponse(response);
	}
}
