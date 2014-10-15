/*
 * GPL v3
 */

package Bricklink.org.kleini.bricklink.api;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import Bricklink.org.kleini.bricklink.data.MetaDT;
import Bricklink.org.kleini.bricklink.data.ResponseDT;

/**
 * {@link Parser}
 *
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public abstract class Parser<T extends Response<?>, U> {

    private final ObjectMapper mapper = new ObjectMapper();

    protected Parser() {
        super();
    }

    public final static String checkResponse(CloseableHttpResponse response) throws Exception {
        if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
            throw new Exception("Request failed. (" + response.getStatusLine().getReasonPhrase() + ")");
        }
        return EntityUtils.toString(response.getEntity());
    }

    public final T parse(String body) throws Exception {
        final ResponseDT<U> response;
        try {        	
            response = mapper.readValue(body, getResponseType());            
        } catch (JsonMappingException e) {
//            System.err.println("Body: " + body);
            throw e;
        }
        MetaDT meta = response.getMeta();
        if (200 != meta.getCode()) {
            throw new Exception(meta.getMessage() + ";" + meta.getDescription());
        }
        return createResponse(response);
    }

    protected abstract TypeReference<ResponseDT<U>> getResponseType();

    protected abstract T createResponse(ResponseDT<U> response);
}
