/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.Connectivity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;

/**
 * {@link UploadConnectivityRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class UploadConnectivityRequest implements
		Request<UploadConnectivityResponse> {
	private String partId;
	private String filePath;

	public UploadConnectivityRequest(String partId, String filePath){
		super();
		this.partId = partId;
		this.filePath = filePath;
	}


	@Override
	public String getPath() {
		try {
			return "/connectivity/"+URLEncoder.encode(partId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Parameter[] getParameters() {
		List<Parameter> retval = new ArrayList<Parameter>();
		retval.add(new Parameter("file", filePath));		
		return retval.toArray(new Parameter[retval.size()]);
	}

	@Override
	public UploadConnectivityParser getParser() {
		return new UploadConnectivityParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.POST;
	}
}
