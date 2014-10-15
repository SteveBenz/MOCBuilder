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
 * {@link CompatibleIDParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class CompatibleIDParser extends
		Parser<CompatibleIDResponse, List<IDMappingDT>> {

	public CompatibleIDParser() {
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
	protected CompatibleIDResponse createResponse(
			ResponseDT<List<IDMappingDT>> response) {
		return new CompatibleIDResponse(response);
	}
}
