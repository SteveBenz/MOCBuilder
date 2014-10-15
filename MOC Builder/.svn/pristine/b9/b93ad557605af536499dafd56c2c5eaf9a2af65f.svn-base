/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.BrickBuilder.data.LDrawModuleDT;
import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link LDrawModulesParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class LDrawModulesParser extends
		Parser<LDrawModulesResponse, List<LDrawModuleDT>> {

	public LDrawModulesParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<List<LDrawModuleDT>>> getResponseType() {
		return new TypeReference<ResponseDT<List<LDrawModuleDT>>>() {
			// Nothing to do.
		};
	}

	@Override
	protected LDrawModulesResponse createResponse(
			ResponseDT<List<LDrawModuleDT>> response) {
		return new LDrawModulesResponse(response);
	}
}
