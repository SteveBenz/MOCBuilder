/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.CompatibleInfo;

import java.util.ArrayList;
import java.util.List;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Exports.PartDomainT;

/**
 * {@link UpdateCompatibleColorRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class UpdateCompatibleColorRequest implements
		Request<UpdateCompatibleColorResponse> {
	private PartDomainT fromDomain;
	private String fromId;
	private PartDomainT toDomain;
	private String toId;

	public UpdateCompatibleColorRequest(String fromId, PartDomainT fromDomain, String toId, PartDomainT toDomain){
		super();
		this.fromId = fromId;
		this.fromDomain = fromDomain;
		this.toId = toId;
		this.toDomain = toDomain;
	}


	@Override
	public String getPath() {
		return "/compatible/partcolor";
	}

	@Override
	public Parameter[] getParameters() {
		List<Parameter> retval = new ArrayList<Parameter>();
		retval.add(new Parameter("fromId", fromId));
		retval.add(new Parameter("fromDomain", fromDomain.toString()));
		retval.add(new Parameter("toId", toId));
		retval.add(new Parameter("toDomain", toDomain.toString()));
		return retval.toArray(new Parameter[retval.size()]);
	}

	@Override
	public UpdateCompatibleColorParser getParser() {
		return new UpdateCompatibleColorParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.PUT;
	}
}
