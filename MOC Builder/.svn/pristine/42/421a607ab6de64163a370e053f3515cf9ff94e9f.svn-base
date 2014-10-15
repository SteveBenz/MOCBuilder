/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.CompatibleInfo;

import java.util.List;

import Bricklink.BrickBuilder.data.IDMappingDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link CompatibleColorsResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class CompatibleColorsResponse extends Response<List<IDMappingDT>> {

	protected CompatibleColorsResponse(
			ResponseDT<List<IDMappingDT>> response) {
		super(response);
	}

	public List<IDMappingDT> getMappingList() {
		return getResponse().getData();
	}
}
