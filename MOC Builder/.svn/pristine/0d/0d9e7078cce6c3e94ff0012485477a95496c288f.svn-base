/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ItemDT;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link ItemImageParser}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class ItemImageParser extends
		Parser<ItemImageResponse, ItemDT> {

	public ItemImageParser() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	protected TypeReference<ResponseDT<ItemDT>> getResponseType() {
		return new TypeReference<ResponseDT<ItemDT>>() {
			// Nothing to do.
		};
	}

	@Override
	protected ItemImageResponse createResponse(
			ResponseDT<ItemDT> response) {
		return new ItemImageResponse(response);
	}
}
