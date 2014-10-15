/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;
import Bricklink.org.kleini.bricklink.data.SupersetDT;

/**
 * {@link SupersetsParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SupersetsParser extends
		Parser<SupersetsResponse, List<SupersetDT>> {

	@Override
	protected TypeReference<ResponseDT<List<SupersetDT>>> getResponseType() {
		return new TypeReference<ResponseDT<List<SupersetDT>>>() {
			// Nothing to do.
		};
	}

	@Override
	protected SupersetsResponse createResponse(
			ResponseDT<List<SupersetDT>> response) {
		return new SupersetsResponse(response);
	}
}
