package ConnectivityEditor.Window;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.CollisionSphere;
import Connectivity.Connectivity;
import LDraw.Support.MatrixMath;

public class CollisionSphereRenderer extends DefaultConnectivityRenderer {
	public CollisionSphereRenderer(MainCamera camera, Connectivity conn) {
		super(camera, conn);
	}

	private final int M = 36;
	private final int N = 18;

	@Override
	public void draw(GL2 gl2) {
		gl2.glColor3d(0, 1, 0);
		if (conn.isSelected())
			gl2.glColor3d(0, 0, 0);

		Vector3f vertices[][] = getVertices();
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				gl2.glBegin(GL2.GL_POLYGON);
				gl2.glVertex3f(vertices[i][j].x, vertices[i][j].y,
						vertices[i][j].z);
				gl2.glVertex3f(vertices[i + 1][j].x, vertices[i + 1][j].y,
						vertices[i + 1][j].z);
				gl2.glVertex3f(vertices[i + 1][j + 1].x,
						vertices[i + 1][j + 1].y, vertices[i + 1][j + 1].z);
				gl2.glVertex3f(vertices[i][j + 1].x, vertices[i][j + 1].y,
						vertices[i][j + 1].z);
				gl2.glEnd();
			}
		}
	}

	private Vector3f[][] getVertices() {
		Vector3f[][] vertices = new Vector3f[M + 1][N + 1];

		final float radius = ((CollisionSphere) conn).getRadius();

		float theta;
		float phi;
		float delta_theta = (float) (2 * Math.PI / M);
		float delta_phi = (float) (Math.PI / N);
		Vector3f vertex;
		for (int i = 0; i <= M; i++) {
			for (int j = 0; j <= N; j++) {
				theta = i * delta_theta;
				phi = (float) (j * delta_phi - (Math.PI / 2));
				vertex = new Vector3f();
				vertex.x = (float) (radius * Math.cos(theta) * Math.cos(phi));
				vertex.y = (float) (radius * Math.sin(theta) * Math.cos(phi));
				vertex.z = (float) (radius * Math.sin(phi));
				vertices[i][j] = conn.getTransformMatrix().transformPoint(
						vertex);
			}
		}

		return vertices;
	}

	@Override
	public boolean isHitted(MainCamera camera, float screenX, float screenY,
			FloatBuffer distance) {

		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);

		Vector3f[][] vertices = getVertices();
		boolean isHitted = false;
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i][j],
						vertices[i + 1][j], vertices[i + 1][j + 1],
						distanceTemp, null)) {

					if (distance != null)
						if (distanceTemp.get(0) < distance.get(0))
							distance.put(0, distanceTemp.get(0));
					isHitted = true;
				}
				if (MatrixMath.V3RayIntersectsTriangle(ray,
						vertices[i + 1][j + 1], vertices[i][j + 1],
						vertices[i][j], distanceTemp, null)) {
					if (distance != null)
						if (distanceTemp.get(0) < distance.get(0))
							distance.put(0, distanceTemp.get(0));
					isHitted = true;
				}
			}
		}
		lastHittedDistance = distance.get(0);
		return isHitted;
	}

}
