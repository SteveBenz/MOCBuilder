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
 * {@link CompatibleColorsRequest}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public final class CompatibleColorsRequest implements
		Request<CompatibleColorsResponse> {
	private boolean composeUncertified = false;
	private PartDomainT domain;

	public CompatibleColorsRequest(PartDomainT domain) {
		super();
		this.domain = domain;
	}

	public CompatibleColorsRequest(PartDomainT domain,
			boolean compoaseUncertified) {
		super();
		this.domain = domain;
		this.composeUncertified = compoaseUncertified;
	}

	@Override
	public String getPath() {
		return "/compatible/partcolor/"+domain.toString();
	}

	@Override
	public Parameter[] getParameters() {
		List<Parameter> retval = new ArrayList<Parameter>();
		retval.add(new Parameter("domain", this.domain.toString()));
		retval.add(new Parameter("composeuncertified", this.composeUncertified? "1" : "0"));
		return retval.toArray(new Parameter[retval.size()]);
	}

	@Override
	public CompatibleColorsParser getParser() {
		return new CompatibleColorsParser();
	}

	@Override
	public HttpRequestT getRequestType() {
		// TODO Auto-generated method stub
		return HttpRequestT.GET;
	}
}
