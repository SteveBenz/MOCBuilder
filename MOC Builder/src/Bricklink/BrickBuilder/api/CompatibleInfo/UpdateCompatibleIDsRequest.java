 /*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.CompatibleInfo;

import java.util.ArrayList;
import java.util.List;

import Bricklink.org.kleini.bricklink.api.HttpRequestT;
import Bricklink.org.kleini.bricklink.api.Parameter;
import Bricklink.org.kleini.bricklink.api.Request;
import Bricklink.org.kleini.bricklink.data.CurrencyT;
import Bricklink.org.kleini.bricklink.data.ItemType;
import Exports.PartDomainT;

/**
 * {@link UpdateCompatibleIDsRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class UpdateCompatibleIDsRequest implements
		Request<UpdateCompatibleIDsResponse> {
	private PartDomainT fromDomain;
	private String fromId;
	private PartDomainT toDomain;
	private String toId;

	public UpdateCompatibleIDsRequest(String fromId, PartDomainT fromDomain, String toId, PartDomainT toDomain){
		super();
		this.fromId = fromId;
		this.fromDomain = fromDomain;
		this.toId = toId;
		this.toDomain = toDomain;
	}


	@Override
	public String getPath() {
		return "/compatible/partid";
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
	public UpdateCompatibleIDsParser getParser() {
		return new UpdateCompatibleIDsParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.PUT;
	}
}
