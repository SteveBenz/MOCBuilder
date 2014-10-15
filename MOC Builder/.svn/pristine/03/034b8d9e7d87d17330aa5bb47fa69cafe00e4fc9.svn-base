/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.LDrawSubsetDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link SubsetParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SubsetParser extends
		Parser<SubsetResponse, LDrawSubsetDT> {

	public SubsetParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<LDrawSubsetDT>> getResponseType() {
		return new TypeReference<ResponseDT<LDrawSubsetDT>>() {
			// Nothing to do.
		};
	}

	@Override
	protected SubsetResponse createResponse(
			ResponseDT<LDrawSubsetDT> response) {
		return new SubsetResponse(response);
	}
}
