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
 * {@link SubsetRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class SubsetRequest implements Request<SubsetResponse> {
	
	public static void main(String args[]) throws Exception {
		BrickBuilderClient client = BricklinkAPI.getInstance()
				.getBrickBuilderClient();
		List<String> tags = new ArrayList<String>();
		tags.add("truck");
		tags.add("body");
		Request request = new SubsetRequest(19);
		Response response = client.execute(request);
	}

	private Integer moduleId;

	public SubsetRequest(Integer moduleId) {
		super();
		this.moduleId = moduleId;
	}

	@Override
	public String getPath() {
		return "/modules/"+moduleId+"/subset";
	}

	@Override
	public Parameter[] getParameters() {		
		return Parameter.EMPTY;
	}

	@Override
	public SubsetParser getParser() {
		return new SubsetParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
