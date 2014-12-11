package ConnectivityEditor.Window;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.Axle;
import Connectivity.Ball;
import Connectivity.CollisionBox;
import Connectivity.CollisionCylinder;
import Connectivity.CollisionSphere;
import Connectivity.Connectivity;
import Connectivity.Fixed;
import Connectivity.Hinge;
import Connectivity.Hole;
import Connectivity.IConnectivity;
import Connectivity.Slider;
import Connectivity.Stud;
import LDraw.Support.MatrixMath;

public class DefaultConnectivityRenderer implements IConnectivityRenderer {
	protected MainCamera camera;
	protected GLU glu;
	protected Connectivity conn;
	protected float lastHittedDistance;

	public DefaultConnectivityRenderer(MainCamera camera, Connectivity conn) {
		this.glu = new GLU();
		this.conn = conn;
		this.camera = camera;
	}

	private final int M = 36;
	private final int N = 18;

	private Vector3f[][] getVertices() {
		Vector3f[][] vertices = new Vector3f[M + 1][N + 1];

		final float radius = 3;

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
	public void draw(GL2 gl2) {
		// pos = LDrawGridTypeT.getSnappedPos(pos, LDrawGridTypeT.Medium);

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

	@Override
	public float getLastHittedDistance() {
		return lastHittedDistance;
	}

	public static IConnectivityRenderer createRendererFor(MainCamera camera,
			Connectivity conn2) {
		if (conn2 instanceof Stud) {
			return new StudRenderer(camera, conn2);
		} else if (conn2 instanceof Hole) {
			return new HoleRenderer(camera, conn2);
		} else if (conn2 instanceof Axle) {
			return new AxleRenderer(camera, conn2);
		} else if (conn2 instanceof Hinge) {
			return new HingeRenderer(camera, conn2);
		} else if (conn2 instanceof Slider) {
			return new SliderRenderer(camera, conn2);
		} else if (conn2 instanceof Ball) {
			return new BallRenderer(camera, conn2);
		} else if (conn2 instanceof Fixed) {
			return new FixedRenderer(camera, conn2);
		} else if (conn2 instanceof CollisionBox) {
			return new CollisionBoxRenderer(camera, conn2);
		} else if (conn2 instanceof CollisionSphere) {
			return new CollisionSphereRenderer(camera, conn2);
		} else if (conn2 instanceof CollisionCylinder) {
			return new CollisionCylinderRenderer(camera, conn2);
		}

		return new DefaultConnectivityRenderer(camera, conn2);
	}

	@Override
	public IConnectivity getConnectivity() {
		// TODO Auto-generated method stub
		return conn;
	}

}
