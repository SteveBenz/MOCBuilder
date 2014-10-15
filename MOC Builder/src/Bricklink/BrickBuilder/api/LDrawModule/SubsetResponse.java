/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import java.util.List;

import Bricklink.BrickBuilder.data.LDrawSubsetDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link SubsetResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class SubsetResponse extends Response<LDrawSubsetDT> {

	protected SubsetResponse(
			ResponseDT<LDrawSubsetDT> response) {
		super(response);
	}

	public LDrawSubsetDT getMappingList() {
		return getResponse().getData();
	}
}
