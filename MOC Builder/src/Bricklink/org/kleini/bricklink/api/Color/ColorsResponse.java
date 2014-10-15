/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Color;

import java.util.List;

import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ColorDT;

/**
 * {@link ColorsResponse}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class ColorsResponse extends Response<List<ColorDT>> {

    protected ColorsResponse(Bricklink.org.kleini.bricklink.data.ResponseDT<List<ColorDT>> response) {
        super(response);
    }

    public List<ColorDT> getColors() {
        return getResponse().getData();
    }
}
