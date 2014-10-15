/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import Bricklink.BricklinkAPI;
import Bricklink.BrickBuilder.api.BrickBuilderClient;
import Bricklink.BrickBuilder.data.SubpartDT;
import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.CurrencyT;
import Bricklink.org.kleini.bricklink.data.ItemType;
import Exports.PartDomainT;

/**
 * {@link SupersetsRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SupersetsRequest implements Request<SupersetsResponse> {

	public static void main(String args[]) throws Exception {
		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();
		List<String> tags = new ArrayList<String>();
		tags.add("truck");
		tags.add("body");
		Request request = new SupersetsRequest("3005");
		SupersetsResponse response = client.execute(request);
	}

	private String partId;
	private Integer colorId = null;

	public SupersetsRequest(String partId) {
		super();
		this.partId = partId;
	}

	public SupersetsRequest(String partId, Integer colorId) {
		super();
		this.partId = partId;
		this.colorId = colorId;
	}

	@Override
	public String getPath() {
		try {
			return "/modules/" + URLEncoder.encode(partId, "UTF-8")
					+ "/supersets";
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@Override
	public Parameter[] getParameters() {
		List<Parameter> retval = new ArrayList<Parameter>();
		retval.add(new Parameter("partId", partId));
		if (colorId != null)
			retval.add(new Parameter("colorId", colorId));
		return retval.toArray(new Parameter[retval.size()]);
	}

	@Override
	public SupersetsParser getParser() {
		return new SupersetsParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
