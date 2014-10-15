/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.List;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.KnownColorDT;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link KnownColorsResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class KnownColorsResponse extends Response<List<KnownColorDT>> {

	protected KnownColorsResponse(
			ResponseDT<List<KnownColorDT>> response) {
		super(response);
	}

	public List<KnownColorDT> getKnownColors() {
		return getResponse().getData();
	}
}
