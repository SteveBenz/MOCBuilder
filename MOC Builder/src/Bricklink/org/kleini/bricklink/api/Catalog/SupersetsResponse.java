/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.List;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;
import Bricklink.org.kleini.bricklink.data.SupersetDT;



/**
 * {@link SupersetsResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class SupersetsResponse extends Response<List<SupersetDT>> {

	protected SupersetsResponse(
			ResponseDT<List<SupersetDT>> response) {
		super(response);
	}

	public List<SupersetDT> getSuperSetList() {
		return getResponse().getData();
	}
}
