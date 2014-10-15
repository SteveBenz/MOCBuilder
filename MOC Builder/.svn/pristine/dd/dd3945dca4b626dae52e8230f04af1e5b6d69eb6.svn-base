/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import java.util.List;

import Bricklink.BrickBuilder.data.LDrawModuleDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link LDrawModulesResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class LDrawModulesResponse extends Response<List<LDrawModuleDT>> {

	protected LDrawModulesResponse(
			ResponseDT<List<LDrawModuleDT>> response) {
		super(response);
	}

	public List<LDrawModuleDT> getMappingList() {
		return getResponse().getData();
	}
}
