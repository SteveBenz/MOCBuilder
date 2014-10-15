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
 * {@link CompatibleColorsParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class CompatibleColorsParser extends
		Parser<CompatibleColorsResponse, List<IDMappingDT>> {

	public CompatibleColorsParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<List<IDMappingDT>>> getResponseType() {
		return new TypeReference<ResponseDT<List<IDMappingDT>>>() {
			// Nothing to do.
		};
	}

	@Override
	protected CompatibleColorsResponse createResponse(
			ResponseDT<List<IDMappingDT>> response) {
		return new CompatibleColorsResponse(response);
	}
}
