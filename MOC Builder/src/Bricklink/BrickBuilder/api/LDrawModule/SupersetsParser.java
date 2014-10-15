/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.LDrawSupersetsDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link SupersetsParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SupersetsParser extends
		Parser<SupersetsResponse, LDrawSupersetsDT> {

	public SupersetsParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<LDrawSupersetsDT>> getResponseType() {
		return new TypeReference<ResponseDT<LDrawSupersetsDT>>() {
			// Nothing to do.
		};
	}

	@Override
	protected SupersetsResponse createResponse(
			ResponseDT<LDrawSupersetsDT> response) {
		return new SupersetsResponse(response);
	}
}
