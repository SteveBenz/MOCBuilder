package ConnectivityEditor.Window;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.CollisionBox;
import Connectivity.Connectivity;
import LDraw.Support.MatrixMath;

public class CollisionBoxRenderer extends DefaultConnectivityRenderer {
	public CollisionBoxRenderer(MainCamera camera, Connectivity conn) {
		super(camera, conn);
	}

	@Override
	public void draw(GL2 gl2) {
		gl2.glColor3d(0, 1, 0);
		if (conn.isSelected())
			gl2.glColor3d(0, 0, 0);

		Vector3f[] pos = getVertices((CollisionBox) conn);

		gl2.glBegin(GL2.GL_QUADS); // draw using triangles
		for (int i = 0; i < pos.length; i++)
			gl2.glVertex3f(pos[i].x, pos[i].y, pos[i].z);
		gl2.glEnd();
	}

	private Vector3f[] getVertices(CollisionBox collisionBox) {

		Vector3f[] boxPos = new Vector3f[8];
		boxPos[0] = new Vector3f(-collisionBox.getsX(), -collisionBox.getsY(),
				-collisionBox.getsZ());
		boxPos[1] = new Vector3f(collisionBox.getsX(), -collisionBox.getsY(),
				-collisionBox.getsZ());
		boxPos[2] = new Vector3f(collisionBox.getsX(), -collisionBox.getsY(),
				collisionBox.getsZ());
		boxPos[3] = new Vector3f(-collisionBox.getsX(), -collisionBox.getsY(),
				collisionBox.getsZ());
		boxPos[4] = new Vector3f(-collisionBox.getsX(), collisionBox.getsY(),
				-collisionBox.getsZ());
		boxPos[5] = new Vector3f(collisionBox.getsX(), collisionBox.getsY(),
				-collisionBox.getsZ());
		boxPos[6] = new Vector3f(collisionBox.getsX(), collisionBox.getsY(),
				collisionBox.getsZ());
		boxPos[7] = new Vector3f(-collisionBox.getsX(), collisionBox.getsY(),
				collisionBox.getsZ());

		for (int k = 0; k < 8; k++) {
			boxPos[k] = collisionBox.getTransformMatrix().transformPoint(
					boxPos[k]);
			boxPos[k] = collisionBox.getParent().transformationMatrix()
					.transformPoint(boxPos[k]);
		}

		Vector3f vertices[] = new Vector3f[24];
		int cnt = 0;
		for (int i = 0; i < 8; i++) {
			vertices[cnt++] = boxPos[i];
		}
		vertices[cnt++] = boxPos[0];
		vertices[cnt++] = boxPos[4];
		vertices[cnt++] = boxPos[7];
		vertices[cnt++] = boxPos[3];

		vertices[cnt++] = boxPos[1];
		vertices[cnt++] = boxPos[2];
		vertices[cnt++] = boxPos[6];
		vertices[cnt++] = boxPos[5];

		vertices[cnt++] = boxPos[0];
		vertices[cnt++] = boxPos[1];
		vertices[cnt++] = boxPos[5];
		vertices[cnt++] = boxPos[4];

		vertices[cnt++] = boxPos[2];
		vertices[cnt++] = boxPos[3];
		vertices[cnt++] = boxPos[7];
		vertices[cnt++] = boxPos[6];

		return vertices;
	}

	@Override
	public boolean isHitted(MainCamera camera, float screenX, float screenY,
			FloatBuffer distance) {

		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);

		Vector3f[] vertices = getVertices((CollisionBox) conn);
		boolean isHitted = false;
		for (int i = 0; i < vertices.length; i += 4) {
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
					vertices[(i + 1)], vertices[(i + 2)], distanceTemp, null)) {

				if (distance != null)
					if (distanceTemp.get(0) < distance.get(0))
						distance.put(0, distanceTemp.get(0));
				isHitted = true;
			}
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i + 2],
					vertices[(i + 3)], vertices[i], distanceTemp, null)) {
				if (distance != null)
					if (distanceTemp.get(0) < distance.get(0))
						distance.put(0, distanceTemp.get(0));
				isHitted = true;
			}
		}
		lastHittedDistance = distance.get(0);
		return isHitted;
	}
}
