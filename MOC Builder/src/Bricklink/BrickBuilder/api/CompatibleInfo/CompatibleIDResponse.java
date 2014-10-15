 /*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.CompatibleInfo;

import java.util.List;

import Bricklink.BrickBuilder.data.IDMappingDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link CompatibleIDResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class CompatibleIDResponse extends Response<List<IDMappingDT>> {

	protected CompatibleIDResponse(
			ResponseDT<List<IDMappingDT>> response) {
		super(response);
	}

	public List<IDMappingDT> getMappingList() {
		return getResponse().getData();
	}
}
