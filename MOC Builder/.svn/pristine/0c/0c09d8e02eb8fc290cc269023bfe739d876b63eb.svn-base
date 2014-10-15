/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.KnownColorDT;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link KnownColorsParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class KnownColorsParser extends
		Parser<KnownColorsResponse, List<KnownColorDT>> {

	@Override
	protected TypeReference<ResponseDT<List<KnownColorDT>>> getResponseType() {
		return new TypeReference<ResponseDT<List<KnownColorDT>>>() {
			// Nothing to do.
		};
	}

	@Override
	protected KnownColorsResponse createResponse(
			ResponseDT<List<KnownColorDT>> response) {
		return new KnownColorsResponse(response);
	}
}
