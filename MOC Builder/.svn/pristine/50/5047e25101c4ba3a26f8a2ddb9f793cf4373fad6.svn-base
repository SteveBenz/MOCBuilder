/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Catalog;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.SubsetDT;

/**
 * {@link SubsetsParser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class SubsetsParser extends Parser<SubsetsResponse, List<SubsetDT>> {

    public SubsetsParser() {
        super();
    }

    @Override
    protected TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<List<SubsetDT>>> getResponseType() {
        return new TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<List<SubsetDT>>>() {
            // Nothing to do.
        };
    }

    @Override
    protected SubsetsResponse createResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<List<SubsetDT>> response) {
        return new SubsetsResponse(response);
    }
}
