/*
 * GPLv3
 */

package Bricklink.org.kleini.bricklink.api.Color;

import java.util.List;

import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.api.Parser;
import Bricklink.org.kleini.bricklink.data.ColorDT;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link ColorsParser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class ColorsParser extends Parser<ColorsResponse, List<ColorDT>> {

    @Override
    protected TypeReference<ResponseDT<List<ColorDT>>> getResponseType() {
        return new TypeReference<ResponseDT<List<ColorDT>>>() {
            // Nothing to do.
        };
    }

    @Override
    protected ColorsResponse createResponse(ResponseDT<List<ColorDT>> response) {
        return new ColorsResponse(response);
    }
}
