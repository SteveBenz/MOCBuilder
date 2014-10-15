/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

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
 * {@link LDrawModulesRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class LDrawModulesRequest implements Request<LDrawModulesResponse> {
	
	public static void main(String args[]) throws Exception {
		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();
		List<String> tags = new ArrayList<String>();
		tags.add("truck");
		tags.add("body");
		Request request = new LDrawModulesRequest(true, tags);
		Response response = client.execute(request);
	}

	private boolean composeUncertified = false;
	private List<String> tags;

	public LDrawModulesRequest() {
		super();
		tags = new ArrayList<String>();
	}

	public LDrawModulesRequest(boolean compoaseUncertified, List<String> tags) {
		super();
		this.composeUncertified = compoaseUncertified;
		this.tags = tags;
	}

	@Override
	public String getPath() {
		return "/modules";
	}

	@Override
	public Parameter[] getParameters() {
		List<Parameter> retval = new ArrayList<Parameter>();
		retval.add(new Parameter("composeuncertified",
				this.composeUncertified ? "1" : "0"));
		retval.add(new Parameter("tags", tags));
		return retval.toArray(new Parameter[retval.size()]);
	}

	@Override
	public LDrawModulesParser getParser() {
		return new LDrawModulesParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
