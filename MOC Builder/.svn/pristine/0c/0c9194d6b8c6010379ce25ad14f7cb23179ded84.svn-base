package ConnectivityEditor.ConnectivityControlGuide;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import BrickControlGuide.AxisGuideTypeT;
import BrickControlGuide.IGuideRenderer;
import BrickControlGuide.MovementGuide;
import BrickControlGuide.RotationGuide;
import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.IConnectivity;

import com.jogamp.opengl.util.gl2.GLUT;

public class ConnectivityMovementGuideRenderer {
	private static ConnectivityMovementGuideRenderer _instance = null;

	private MainCamera camera;
	private GLU glu;
	private GLUT glut;
	private IConnectivity conn;
	private IGuideRenderer selectedGuide = null;
	private ArrayList<IGuideRenderer> defaultGuideList;

	public static ConnectivityMovementGuideRenderer getInstance() {
		return _instance;
	}

	public synchronized static ConnectivityMovementGuideRenderer getInstance(
			MainCamera cam) {
		_instance = new ConnectivityMovementGuideRenderer(cam);
		return _instance;
	}

	private ConnectivityMovementGuideRenderer(MainCamera cam) {
		camera = cam;
		conn = null;
		glu = new GLU(); // get GL Utilities
		glut = new GLUT();

		defaultGuideList = new ArrayList<IGuideRenderer>();
		MovementGuide movementGuide = new MovementGuide(glu);
		movementGuide.setColor3f(1, 0, 0);
		movementGuide.setAxisGuideType(AxisGuideTypeT.X_Movement);
		defaultGuideList.add(movementGuide);

		movementGuide = new MovementGuide(glu);
		movementGuide.setColor3f(0, 1, 0);
		movementGuide.setAxisGuideType(AxisGuideTypeT.Y_Movement);
		defaultGuideList.add(movementGuide);

		movementGuide = new MovementGuide(glu);
		movementGuide.setColor3f(0, 0, 1);
		movementGuide.setAxisGuideType(AxisGuideTypeT.Z_Movement);
		defaultGuideList.add(movementGuide);

		RotationGuide rotationGuide = new RotationGuide(glu, glut);
		rotationGuide.setColor3f(1, 0, 0);
		rotationGuide.setAxisGuideType(AxisGuideTypeT.X_Rotate);
		defaultGuideList.add(rotationGuide);

		rotationGuide = new RotationGuide(glu, glut);
		rotationGuide.setColor3f(0, 1, 0);
		rotationGuide.setAxisGuideType(AxisGuideTypeT.Y_Rotate);
		defaultGuideList.add(rotationGuide);

		rotationGuide = new RotationGuide(glu, glut);
		rotationGuide.setColor3f(0, 0, 1);
		rotationGuide.setAxisGuideType(AxisGuideTypeT.Z_Rotate);
		defaultGuideList.add(rotationGuide);
	}

	public void draw(GL2 gl2) {
		if (camera == null)
			return;
		if (conn == null)
			return;

		Vector3f pos = conn.getCurrentPos();
		gl2.glDisable(GL2.GL_LIGHTING);

		gl2.glPushMatrix();

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadMatrixf(camera.getModelView(), 0);

		if (selectedGuide == null && conn != null) {
			for (IGuideRenderer guideRenderer : defaultGuideList)
				guideRenderer.draw(gl2, camera, pos);

		} else if (conn != null)
			selectedGuide.draw(gl2, camera, pos);

		gl2.glPopMatrix();

		gl2.glEnable(GL2.GL_LIGHTING);
	}

	public IConnectivity getConn() {
		return this.conn;
	}

	public IGuideRenderer getHittedAxisArrow(float screenX, float screenY) {
		if (conn == null)
			return null;
		FloatBuffer distance = FloatBuffer.allocate(1);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);
		distance.put(0, Float.MAX_VALUE);
		IGuideRenderer resultGuideRenderer = null;

		for (IGuideRenderer guideRenderer : defaultGuideList) {
			distanceTemp.put(0, Float.MAX_VALUE);

			if (guideRenderer.isHitted(camera, conn.getCurrentPos(), screenX,
					screenY, distanceTemp))
				if (distanceTemp.get(0) < distance.get(0)) {
					distance.put(0, distanceTemp.get(0));
					resultGuideRenderer = guideRenderer;
				}
		}
		return resultGuideRenderer;
	}

	public void axisSelectedType(IGuideRenderer guide) {
		this.selectedGuide = guide;
	}

	public IGuideRenderer getSelectedGuide() {
		return this.selectedGuide;
	}

	public void setConn(IConnectivity conn) {
		if (conn != null)
			this.conn = conn.getConnectivity();
		else
			this.conn = null;
	}
}
