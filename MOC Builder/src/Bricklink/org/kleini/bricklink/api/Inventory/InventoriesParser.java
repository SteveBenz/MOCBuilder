/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api.Inventory;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.InventoryDT;

/**
 * {@link InventoriesParser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class InventoriesParser extends Parser<InventoriesResponse, List<InventoryDT>> {

    public InventoriesParser() {
        super();
    }

    @Override
    protected TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<List<InventoryDT>>> getResponseType() {
        return new TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<List<InventoryDT>>>() {
            // Nothing to do.
        };
    }

    @Override
    protected InventoriesResponse createResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<List<InventoryDT>> response) {
        return new InventoriesResponse(response);
    }
}
