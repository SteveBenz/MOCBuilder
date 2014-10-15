/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.category;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.CategoryDT;

/**
 * {@link CategoriesParser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class CategoriesParser extends Parser<CategoriesResponse, List<CategoryDT>> {

    public CategoriesParser() {
        super();
    }

    @Override
    protected TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<List<CategoryDT>>> getResponseType() {
        return new TypeReference<Bricklink.org.kleini.bricklink.data.ResponseDT<List<CategoryDT>>>() {
            // Nothing to do.
        };
    }

    @Override
    protected CategoriesResponse createResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<List<CategoryDT>> response) {
        return new CategoriesResponse(response);
    }
}
