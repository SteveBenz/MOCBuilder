package ConnectivityEditor.Window;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.Connectivity;
import Connectivity.Hinge;
import ConnectivityEditor.Connectivity.AxleT;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.LDrawGridTypeT;

public class HingeRenderer extends DefaultConnectivityRenderer {
	private Hinge hinge;

	public HingeRenderer(MainCamera camera, Connectivity conn) {
		super(camera, conn);

		hinge = (Hinge) conn;
	}

	@Override
	public void draw(GL2 gl2) {

		if (conn.gettype() % 2 == 0) {
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(0.5f, 0.5f, 1f);
		} else {
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(0.5f, 0.5f, 0f);
		}
		gl2.glLoadMatrixf(camera.getModelView(), 0);

		gl2.glBegin(GL2.GL_QUADS); // draw using triangles
		for (Vector3f vertex : getAxleSocketVertices())
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();

		gl2.glBegin(GL2.GL_QUADS); // draw using triangles
		for (Vector3f vertex : getGuideVertices())
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();

	}

	@Override
	public boolean isHitted(MainCamera camera, float screenX, float screenY,
			FloatBuffer distance) {

		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);

		boolean isHitted = false;

		Vector3f[] vertices = getAxleSocketVertices();
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

		vertices = getGuideVertices();
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

	private Vector3f[] getGuideVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float cylinderRadius = LDrawGridTypeT.Medium.getXZValue() / 3f;
		final float length = 2;

		Vector3f vertex;
		int slice = 24;
		vertex = new Vector3f();
		for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
			vertices.add(vertex.add(
					(float) (cylinderRadius / 16f * Math.cos((sliceIndex)
							* Math.PI * 2 / (slice - 1))),
					0,
					(float) (cylinderRadius / 16f * Math.sin((sliceIndex)
							* Math.PI * 2 / (slice - 1)))));
			vertices.add(vertex.add(

					(float) (cylinderRadius / 16f * Math.cos((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1))),
					0,
					(float) (cylinderRadius / 16f * Math.sin((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1)))));

			vertices.add(vertex.add(
					(float) (cylinderRadius * Math.cos((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1))),
					0,
					(float) (cylinderRadius * Math.sin((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1)))));
			vertices.add(vertex.add(

					(float) (cylinderRadius * Math.cos((sliceIndex) * Math.PI
							* 2 / (slice - 1))),
					0,
					(float) (cylinderRadius * Math.sin((sliceIndex) * Math.PI
							* 2 / (slice - 1)))));
		}
		vertex = new Vector3f(0, length, 0);
		for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
			vertices.add(vertex.add(
					(float) (cylinderRadius / 1.6f * Math.cos((sliceIndex)
							* Math.PI * 2 / (slice - 1))),
					0,
					(float) (cylinderRadius / 1.6f * Math.sin((sliceIndex)
							* Math.PI * 2 / (slice - 1)))));
			vertices.add(vertex.add(

					(float) (cylinderRadius / 1.6f * Math.cos((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1))),
					0,
					(float) (cylinderRadius / 1.6f * Math.sin((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1)))));

			vertices.add(vertex.add(
					(float) (cylinderRadius * Math.cos((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1))),
					0,
					(float) (cylinderRadius * Math.sin((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1)))));
			vertices.add(vertex.add(

					(float) (cylinderRadius * Math.cos((sliceIndex) * Math.PI
							* 2 / (slice - 1))),
					0,
					(float) (cylinderRadius * Math.sin((sliceIndex) * Math.PI
							* 2 / (slice - 1)))));

		}

		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = hinge.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	private Vector3f[] getAxleSocketVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float cylinderRadius = LDrawGridTypeT.Medium.getXZValue() / 3f;
		final float cylinderHeight = 2;

		Vector3f vertex;
		int slice = 24;
		vertex = new Vector3f();
		for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
			vertices.add(vertex.add(

					(float) (cylinderRadius * Math.cos(sliceIndex * Math.PI * 2
							/ (slice - 1))),
					0,
					(float) (cylinderRadius * Math.sin(sliceIndex * Math.PI * 2
							/ (slice - 1)))));
			vertices.add(vertex.add(

					(float) (cylinderRadius * Math.cos((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1))),
					0,
					(float) (cylinderRadius * Math.sin((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1)))));
			vertices.add(vertex.add(

					(float) (cylinderRadius * Math.cos((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1))),
					cylinderHeight,
					(float) (cylinderRadius * Math.sin((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1)))));
			vertices.add(vertex.add(

					(float) (cylinderRadius * Math.cos(sliceIndex * Math.PI * 2
							/ (slice - 1))),
					cylinderHeight,
					(float) (cylinderRadius * Math.sin(sliceIndex * Math.PI * 2
							/ (slice - 1)))));

		}

		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = hinge.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}
}
