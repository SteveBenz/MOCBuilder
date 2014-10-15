package ConnectivityEditor.Window;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Connectivity.Connectivity;

public class BallRenderer extends DefaultConnectivityRenderer {
	public BallRenderer(MainCamera camera, Connectivity conn) {
		super(camera, conn);
	}

	@Override
	public void draw(GL2 gl2) {
		if (conn.gettype() % 2 == 0) {// socket
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(0.5f, 1f, 1f);
		} else {
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(0.5f, 0f, 0f);
		}

		super.draw(gl2);
	}

}
