/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import Bricklink.BrickBuilder.data.LDrawSupersetsDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link SupersetsResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class SupersetsResponse extends Response<LDrawSupersetsDT> {

	protected SupersetsResponse(
			ResponseDT<LDrawSupersetsDT> response) {
		super(response);
	}

	public LDrawSupersetsDT getSupersetsDT() {
		return getResponse().getData();
	}
}
