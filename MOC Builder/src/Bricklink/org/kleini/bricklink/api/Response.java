/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api;

import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link Response}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public abstract class Response<T> {

    private ResponseDT<T> response;

    protected Response(ResponseDT<T> response) {
        super();
        this.response = response;
    }

    public ResponseDT<T> getResponse() {
        return response;
    }
}
