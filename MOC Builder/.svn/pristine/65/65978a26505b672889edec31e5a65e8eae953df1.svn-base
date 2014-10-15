/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawPart;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.LDrawPartDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link UploadLDrawPartParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class UploadLDrawPartParser extends
		Parser<UploadLDrawPartResponse, LDrawPartDT> {

	public UploadLDrawPartParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<LDrawPartDT>> getResponseType() {
		return new TypeReference<ResponseDT<LDrawPartDT>>() {
			// Nothing to do.
		};
	}

	@Override
	protected UploadLDrawPartResponse createResponse(
			ResponseDT<LDrawPartDT> response) {
		return new UploadLDrawPartResponse(response);
	}
}
