/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ItemDT;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link ItemParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class ItemParser extends
		Parser<ItemResponse, ItemDT> {

	@Override
	protected TypeReference<ResponseDT<ItemDT>> getResponseType() {
		return new TypeReference<ResponseDT<ItemDT>>() {
			// Nothing to do.
		};
	}

	@Override
	protected ItemResponse createResponse(
			ResponseDT<ItemDT> response) {
		return new ItemResponse(response);
	}
}
