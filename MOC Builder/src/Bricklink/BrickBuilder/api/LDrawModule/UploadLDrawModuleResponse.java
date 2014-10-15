/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawModule;

import Bricklink.BrickBuilder.data.LDrawModuleDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link UploadLDrawModuleResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class UploadLDrawModuleResponse extends Response<LDrawModuleDT> {

	protected UploadLDrawModuleResponse(
			ResponseDT<LDrawModuleDT> response) {
		super(response);
	}
}
