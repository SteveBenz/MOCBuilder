/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.category;

import java.util.List;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.CategoryDT;

/**
 * {@link CategoriesResponse}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class CategoriesResponse extends Response<List<CategoryDT>> {

    protected CategoriesResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<List<CategoryDT>> response) {
        super(response);
    }

    public List<CategoryDT> getCategories() {
        return getResponse().getData();
    }
}
