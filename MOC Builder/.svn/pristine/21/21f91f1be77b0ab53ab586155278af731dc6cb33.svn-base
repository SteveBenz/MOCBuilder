/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api.Inventory;

import java.util.List;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.InventoryDT;

/**
 * {@link InventoriesResponse}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class InventoriesResponse extends Response<List<InventoryDT>> {

    public InventoriesResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<List<InventoryDT>> response) {
        super(response);
    }

    public List<InventoryDT> getInventories() {
        return getResponse().getData();
    }
}
