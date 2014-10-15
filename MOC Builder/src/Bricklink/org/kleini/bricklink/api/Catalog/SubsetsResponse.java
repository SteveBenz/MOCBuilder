/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.List;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.SubsetDT;

/**
 * {@link SubsetsResponse}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SubsetsResponse extends Response<List<SubsetDT>> {

    protected SubsetsResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<List<SubsetDT>> response) {
        super(response);
    }

    public List<SubsetDT> getSubsets() {
        return getResponse().getData();
    }
}
