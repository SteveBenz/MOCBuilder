/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ItemDT;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link ItemImageResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class ItemImageResponse extends Response<ItemDT> {

	protected ItemImageResponse(
			ResponseDT<ItemDT> response) {
		super(response);
	}

	public ItemDT getCatalogItem() {
		return getResponse().getData();
	}
}
