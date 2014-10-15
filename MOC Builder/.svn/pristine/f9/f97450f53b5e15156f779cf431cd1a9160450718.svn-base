/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawPart;

import java.util.List;

import Bricklink.BrickBuilder.data.LDrawPartDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link LDrawPartsResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class LDrawPartsResponse extends Response<List<LDrawPartDT>> {

	protected LDrawPartsResponse(
			ResponseDT<List<LDrawPartDT>> response) {
		super(response);
	}

	public List<LDrawPartDT> getLDrawPartDTList() {
		return getResponse().getData();
	}
}
