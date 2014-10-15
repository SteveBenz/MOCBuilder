package ConnectivityEditor.Connectivity;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import Builder.MainCamera;
import Command.LDrawPart;
import Common.Box3;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.CollisionBox;
import Connectivity.Connectivity;
import Connectivity.Hole;
import Connectivity.IConnectivity;
import Connectivity.ICustom2DField;
import Connectivity.MatrixItem;
import ConnectivityEditor.Window.ConnectivityEditor;
import ConnectivityEditor.Window.DefaultConnectivityRenderer;
import ConnectivityEditor.Window.IConnectivityRenderer;
import LDraw.Support.MatrixMath;

public class ConnectivityRendererForConnectivityEditor {
	public MainCamera camera;
	private GLU glu;

	public ConnectivityRendererForConnectivityEditor(MainCamera cam) {
		glu = new GLU();
		camera = cam;
	}

	public void draw(GL2 gl2) {
		gl2.glDisable(GL2.GL_LIGHTING);
		gl2.glUseProgram(0);

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPushMatrix();
		gl2.glLoadMatrixf(camera.getProjection(), 0);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPushMatrix();
		gl2.glLoadMatrixf(camera.getModelView(), 0);

		drawConnectivity(gl2);

		drawCollisionBoxes(gl2);

		drawBoundingBoxes(gl2);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glPopMatrix();

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glPopMatrix();

		gl2.glEnable(GL2.GL_LIGHTING);
	}

	public void drawCollisionBox(GL2 gl2, Vector3f[] pos) {

		gl2.glBegin(GL2.GL_QUADS); // draw using triangles

		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);

		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);
		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);

		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);
		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);

		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);

		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);

		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);
		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);

		gl2.glEnd();
	}

	private void drawBoundingBox(GL2 gl2, Vector3f[] pos) {

		gl2.glBegin(GL2.GL_LINES); // draw using triangles
		gl2.glColor4d(1, 0, 0, 1.0f);

		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);

		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);
		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);
		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);

		gl2.glVertex3f(pos[0].x, pos[0].y, pos[0].z);
		gl2.glVertex3f(pos[4].x, pos[4].y, pos[4].z);
		gl2.glVertex3f(pos[1].x, pos[1].y, pos[1].z);
		gl2.glVertex3f(pos[5].x, pos[5].y, pos[5].z);
		gl2.glVertex3f(pos[2].x, pos[2].y, pos[2].z);
		gl2.glVertex3f(pos[6].x, pos[6].y, pos[6].z);
		gl2.glVertex3f(pos[3].x, pos[3].y, pos[3].z);
		gl2.glVertex3f(pos[7].x, pos[7].y, pos[7].z);

		gl2.glEnd();
	}

	private void drawCollisionBoxes(GL2 gl2) {

		gl2.glLoadMatrixf(camera.getModelView(), 0);

		LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();
		if (part == null)
			return;
		ArrayList<CollisionBox> boxes = part.getCollisionBoxList();
		if (boxes == null || boxes.size() == 0)
			return;

		for (int j = 0; j < boxes.size(); j++) {
			CollisionBox collisionBox = boxes.get(j);

			if (collisionBox.isSelected()) {
				gl2.glColor4d(0, 0, 0, 0.3f);
			} else {
				gl2.glColor4d(0, 1, 0, 0.3f);
			}
			Vector3f[] boxPos = new Vector3f[8];
			boxPos[0] = new Vector3f(-collisionBox.getsX(),
					-collisionBox.getsY(), -collisionBox.getsZ());
			boxPos[1] = new Vector3f(collisionBox.getsX(),
					-collisionBox.getsY(), -collisionBox.getsZ());
			boxPos[2] = new Vector3f(collisionBox.getsX(),
					-collisionBox.getsY(), collisionBox.getsZ());
			boxPos[3] = new Vector3f(-collisionBox.getsX(),
					-collisionBox.getsY(), collisionBox.getsZ());
			boxPos[4] = new Vector3f(-collisionBox.getsX(),
					collisionBox.getsY(), -collisionBox.getsZ());
			boxPos[5] = new Vector3f(collisionBox.getsX(),
					collisionBox.getsY(), -collisionBox.getsZ());
			boxPos[6] = new Vector3f(collisionBox.getsX(),
					collisionBox.getsY(), collisionBox.getsZ());
			boxPos[7] = new Vector3f(-collisionBox.getsX(),
					collisionBox.getsY(), collisionBox.getsZ());

			for (int k = 0; k < 8; k++) {
				boxPos[k] = collisionBox.getTransformMatrix().transformPoint(
						boxPos[k]);
				boxPos[k] = part.transformationMatrix().transformPoint(
						boxPos[k]);
			}
			drawCollisionBox(gl2, boxPos);
		}

	}

	private void drawBoundingBoxes(GL2 gl2) {

		gl2.glLoadMatrixf(camera.getModelView(), 0);

		LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();
		if (part == null)
			return;
		Box3 boundingBox = part.boundingBox3();

		if (boundingBox != null) {
			Vector3f max = boundingBox.getMax();
			Vector3f min = boundingBox.getMin();

			Vector3f[] boxPos = new Vector3f[8];
			boxPos[0] = new Vector3f(min.x, min.y, min.z);
			boxPos[1] = new Vector3f(max.x, min.y, min.z);
			boxPos[2] = new Vector3f(max.x, min.y, max.z);
			boxPos[3] = new Vector3f(min.x, min.y, max.z);
			boxPos[4] = new Vector3f(min.x, max.y, min.z);
			boxPos[5] = new Vector3f(max.x, max.y, min.z);
			boxPos[6] = new Vector3f(max.x, max.y, max.z);
			boxPos[7] = new Vector3f(min.x, max.y, max.z);

			drawBoundingBox(gl2, boxPos);
		}
	}

	private void drawConnectivity(GL2 gl2) {

		LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();
		if (part == null)
			return;
		if (part.getConnectivityList() == null)
			return;
		ArrayList<Connectivity> copy = new ArrayList<Connectivity>(
				part.getConnectivityList());
		for (Connectivity conn : copy) {
			conn.updateConnectivityOrientationInfo();
			// if (conn instanceof ICustom2DField) {
			// drawCustom2DField(gl2, conn);
			// } else
			drawConnectivity(gl2, conn);
		}
	}

	private void drawCustom2DField(GL2 gl2, Connectivity conn) {
		MatrixItem[][] matrixItmes = ((ICustom2DField) conn).getMatrixItem();
		if (conn instanceof Hole) {
			for (int column = 0; column < matrixItmes.length; column++)
				for (int row = 0; row < matrixItmes[column].length; row++) {
					matrixItmes[column][row]
							.updateConnectivityOrientationInfo();
					drawHoleMatrix(gl2, matrixItmes[column][row]);
				}
		} else {
			for (int column = 0; column < matrixItmes.length; column++)
				for (int row = 0; row < matrixItmes[column].length; row++) {
					matrixItmes[column][row]
							.updateConnectivityOrientationInfo();
					drawStudMatrix(gl2, matrixItmes[column][row]);
				}
		}

	}

	private HashMap<Connectivity, IConnectivityRenderer> rendererMap;

	public void drawConnectivity(GL2 gl2, Connectivity conn) {
		if (rendererMap == null)
			rendererMap = new HashMap<Connectivity, IConnectivityRenderer>();

		if (rendererMap.containsKey(conn) == false)
			rendererMap
					.put(conn, DefaultConnectivityRenderer.createRendererFor(
							camera, conn));

		rendererMap.get(conn).draw(gl2);
	}

	public void drawStudMatrix(GL2 gl2, MatrixItem matrixItem) {
		gl2.glColor3d(1, matrixItem.getAltitude() / 29.0,
				matrixItem.getAltitude() / 29.0);
		if (matrixItem.getParent().isSelected())
			gl2.glColor3d(0, 0, 0);
		Vector3f pos = matrixItem.getCurrentPos();
		// pos = LDrawGridTypeT.getSnappedPos(pos, LDrawGridTypeT.Medium);
		GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
		final float radius = 2;
		final int slices = 4;
		final int stacks = 4;
		gl2.glLoadMatrixf(camera.getModelView(), 0);
		gl2.glTranslatef(pos.getX(), pos.getY(), pos.getZ());
		glu.gluSphere(earth, radius, slices, stacks);
		glu.gluDeleteQuadric(earth);
	}

	public void drawHoleMatrix(GL2 gl2, MatrixItem matrixItem) {

		gl2.glColor3d(matrixItem.getAltitude() / 29.0, 1,
				matrixItem.getAltitude() / 29.0);

		if (matrixItem.getParent().isSelected())
			gl2.glColor3d(0, 0, 0);

		Vector3f pos = matrixItem.getCurrentPos();
		// pos = LDrawGridTypeT.getSnappedPos(pos, LDrawGridTypeT.Medium);

		// Draw sphere (possible styles: FILL, LINE, POINT).
		GLUquadric earth = glu.gluNewQuadric();
		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
		final float radius = 2;
		final int slices = 4;
		final int stacks = 4;
		gl2.glLoadMatrixf(camera.getModelView(), 0);
		gl2.glTranslatef(pos.getX(), pos.getY(), pos.getZ());
		glu.gluSphere(earth, radius, slices, stacks);
		glu.gluDeleteQuadric(earth);
	}

	public IConnectivity getHittedConnectivity(MainCamera camera,
			float screenX, float screenY) {

		FloatBuffer distance = FloatBuffer.allocate(1);
		distance.put(0, Float.MAX_VALUE);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);
		IConnectivity hittedConn = null;

		LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();
		if (part == null)
			return null;

		if (rendererMap == null)
			return null;

		for (Connectivity conn : part.getConnectivityList()) {
			if (rendererMap.containsKey(conn)) {
				distanceTemp.put(0, Float.MAX_VALUE);
				IConnectivityRenderer renderer = rendererMap.get(conn);
				if (renderer.isHitted(camera, screenX, screenY, distanceTemp)){
					if (distanceTemp.get(0) < distance.get(0)) {
						hittedConn = renderer.getConnectivity();
						distance.put(0, distanceTemp.get(0));
					}
				}
			}
		}
		return hittedConn;
	}

	public Vector3f getHittedPos(MainCamera camera, float screenX, float screenY) {

		FloatBuffer distance = FloatBuffer.allocate(1);
		distance.put(0, Float.MAX_VALUE);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);
		distanceTemp.put(0, Float.MAX_VALUE);
		Vector3f hitPos = null;

		LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();
		if (part == null)
			return null;

		if (rendererMap == null)
			return null;
		Ray3 ray = camera.getRay(screenX, screenY);
		for (Connectivity conn : part.getConnectivityList())
			if (rendererMap.containsKey(conn)) {
				IConnectivityRenderer renderer = rendererMap.get(conn);
				if (renderer.isHitted(camera, screenX, screenY, distanceTemp)
						&& distanceTemp.get(0) < distance.get(0)) {
					distance.put(0, distanceTemp.get(0));
					hitPos = ray.getOrigin().add(
							ray.getDirection().scale(distance.get(0)));
				}
			}
		return hitPos;
	}
}
