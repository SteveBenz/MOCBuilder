/*
 * GPLv3
 */

package Bricklink.BrickBuilder.api.LDrawPart;

import Bricklink.BrickBuilder.data.LDrawPartDT;
import Bricklink.org.kleini.bricklink.api.Response;
import Bricklink.org.kleini.bricklink.data.ResponseDT;



/**
 * {@link UploadLDrawPartResponse}
 * 
 * @author <a href="mailto:himself@kleini.org">Marcus Klein</a>
 */
public class UploadLDrawPartResponse extends Response<LDrawPartDT> {

	protected UploadLDrawPartResponse(
			ResponseDT<LDrawPartDT> response) {
		super(response);
	}
}
