package ConnectivityEditor.Window;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.Axle;
import Connectivity.Connectivity;
import ConnectivityEditor.Connectivity.AxleT;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.LDrawGridTypeT;

public class AxleRenderer extends DefaultConnectivityRenderer {
	private Axle axle;

	public AxleRenderer(MainCamera camera, Connectivity conn) {
		super(camera, conn);

		axle = (Axle) conn;
	}

	@Override
	public void draw(GL2 gl2) {
		if (axle.gettype() % 2 == 0) {// socket
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(1f, 1f, 0);
		} else {
			if (conn.isSelected())
				gl2.glColor3f(0.2f, 0.2f, 0.2f);
			else
				gl2.glColor3f(0f, 0f, 1f);
		}

		gl2.glLoadMatrixf(camera.getModelView(), 0);
		if (axle.gettype() % 2 == 0) {// socket
			gl2.glBegin(GL2.GL_QUADS); // draw using triangles
			for (Vector3f vertex : getAxleSocketVertices())
				gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
			gl2.glEnd();

			gl2.glBegin(GL2.GL_QUADS); // draw using triangles
			for (Vector3f vertex : getGuideVertices())
				gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
			gl2.glEnd();

			gl2.glBegin(GL2.GL_TRIANGLES);
			for (Vector3f vertex : getCapVertices())
				gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
			gl2.glEnd();
		} else {// axle
				// Draw Stud Disk (possible styles: FILL, LINE, POINT).
			if (AxleT.byValue(axle.gettype()).toString().contains("Cross")) {
				gl2.glBegin(GL2.GL_QUADS); // draw using triangles
				for (Vector3f vertex : getAxleBarVertices_Cross())
					gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
				gl2.glEnd();

			} else if (AxleT.byValue(axle.gettype()).toString().contains("Pin")) {
				gl2.glBegin(GL2.GL_QUADS); // draw using triangles
				for (Vector3f vertex : getAxleBarVertices_Pin())
					gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
				gl2.glEnd();

				gl2.glBegin(GL2.GL_QUADS);
				for (Vector3f vertex : getGuideVertices())
					gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
				gl2.glEnd();
			} else { // O axle
				gl2.glBegin(GL2.GL_QUADS); // draw using triangles
				for (Vector3f vertex : getAxleBarVertices_O())
					gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
				gl2.glEnd();

				gl2.glBegin(GL2.GL_QUADS);
				for (Vector3f vertex : getGuideVertices())
					gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
				gl2.glEnd();
			}
			gl2.glBegin(GL2.GL_TRIANGLES);
			for (Vector3f vertex : getCapVertices())
				gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
			gl2.glEnd();
		}
	}

	private Vector3f[] getCapVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float diskRadius = LDrawGridTypeT.Medium.getXZValue();

		Vector3f vertex = null;
		int slice = 24;

		if (axle.getstartCapped() == 1) {
			vertex = new Vector3f();
			for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
				vertices.add(vertex);
				vertices.add(vertex.add(
						(float) (diskRadius * Math.cos((sliceIndex) * Math.PI
								* 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex) * Math.PI
								* 2 / (slice - 1)))));
				vertices.add(vertex.add(

						(float) (diskRadius * Math.cos((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1)))));
			}
		}
		if (axle.getendCapped() == 1) {
			vertex = new Vector3f(0, -axle.getlength(), 0);
			for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
				vertices.add(vertex);
				vertices.add(vertex.add(
						(float) (diskRadius * Math.cos((sliceIndex) * Math.PI
								* 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex) * Math.PI
								* 2 / (slice - 1)))));
				vertices.add(vertex.add(
						(float) (diskRadius * Math.cos((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1))),
						0,
						(float) (diskRadius * Math.sin((sliceIndex + 1)
								* Math.PI * 2 / (slice - 1)))));
			}
		}

		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = axle.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	private Vector3f[] getAxleBarVertices_O() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float cylinderRadius = LDrawGridTypeT.Medium.getXZValue() / 1.6f;
		final float cylinderHeight = -axle.getlength();

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
			retArray[i] = axle.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	private Vector3f[] getAxleBarVertices_Pin() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float cylinderRadius = LDrawGridTypeT.Medium.getXZValue() / 2f;
		final float cylinderHeight = -axle.getlength();

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
			retArray[i] = axle.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	private Vector3f[] getAxleBarVertices_Cross() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float radius = LDrawGridTypeT.Medium.getXZValue() / 2f;
		final float length = axle.getlength();
		final float thickness = 2;
		vertices.add(new Vector3f(-radius, 0, thickness / 2));
		vertices.add(new Vector3f(-radius, 0, -thickness / 2));
		vertices.add(new Vector3f(-radius, -length, -thickness / 2));
		vertices.add(new Vector3f(-radius, -length, thickness / 2));

		vertices.add(new Vector3f(radius, 0, thickness / 2));
		vertices.add(new Vector3f(radius, 0, -thickness / 2));
		vertices.add(new Vector3f(radius, -length, -thickness / 2));
		vertices.add(new Vector3f(radius, -length, thickness / 2));

		vertices.add(new Vector3f(-radius, 0, -thickness / 2));
		vertices.add(new Vector3f(radius, 0, -thickness / 2));
		vertices.add(new Vector3f(radius, -length, -thickness / 2));
		vertices.add(new Vector3f(-radius, -length, -thickness / 2));

		vertices.add(new Vector3f(-radius, 0, thickness / 2));
		vertices.add(new Vector3f(radius, 0, thickness / 2));
		vertices.add(new Vector3f(radius, -length, thickness / 2));
		vertices.add(new Vector3f(-radius, -length, thickness / 2));

		vertices.add(new Vector3f(-thickness / 2, 0, radius));
		vertices.add(new Vector3f(-thickness / 2, 0, -radius));
		vertices.add(new Vector3f(-thickness / 2, -length, -radius));
		vertices.add(new Vector3f(-thickness / 2, -length, radius));

		vertices.add(new Vector3f(thickness / 2, 0, radius));
		vertices.add(new Vector3f(thickness / 2, 0, -radius));
		vertices.add(new Vector3f(thickness / 2, -length, -radius));
		vertices.add(new Vector3f(thickness / 2, -length, radius));

		vertices.add(new Vector3f(-thickness / 2, 0, -radius));
		vertices.add(new Vector3f(thickness / 2, 0, -radius));
		vertices.add(new Vector3f(thickness / 2, -length, -radius));
		vertices.add(new Vector3f(-thickness / 2, -length, -radius));

		vertices.add(new Vector3f(-thickness / 2, 0, radius));
		vertices.add(new Vector3f(thickness / 2, 0, radius));
		vertices.add(new Vector3f(thickness / 2, -length, radius));
		vertices.add(new Vector3f(-thickness / 2, -length, radius));

		vertices.add(new Vector3f(-thickness / 2, 0, -thickness / 2));
		vertices.add(new Vector3f(thickness / 2, 0, -thickness / 2));
		vertices.add(new Vector3f(thickness / 2, 0, thickness / 2));
		vertices.add(new Vector3f(-thickness / 2, 0, thickness / 2));

		vertices.add(new Vector3f(-thickness / 2, -length, -thickness / 2));
		vertices.add(new Vector3f(thickness / 2, -length, -thickness / 2));
		vertices.add(new Vector3f(thickness / 2, -length, thickness / 2));
		vertices.add(new Vector3f(-thickness / 2, -length, thickness / 2));

		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = axle.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	@Override
	public boolean isHitted(MainCamera camera, float screenX, float screenY,
			FloatBuffer distance) {

		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);

		boolean isHitted = false;
		if (axle.gettype() % 2 == 0) {// socket
			Vector3f[] vertices = getAxleSocketVertices();
			for (int i = 0; i < vertices.length; i += 4) {
				if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
						vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
						null)) {

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
						vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
						null)) {
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

			vertices = getCapVertices();
			for (int i = 0; i < vertices.length; i += 3) {
				if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
						vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
						null)) {
					if (distance != null)
						if (distanceTemp.get(0) < distance.get(0))
							distance.put(0, distanceTemp.get(0));
					isHitted = true;
				}
			}
		} else {// axle
				// Draw Stud Disk (possible styles: FILL, LINE, POINT).
			if (AxleT.byValue(axle.gettype()).toString().contains("Cross")) {
				Vector3f[] vertices = getAxleBarVertices_Cross();
				for (int i = 0; i < vertices.length; i += 4) {
					if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
							vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
							null)) {

						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
					if (MatrixMath.V3RayIntersectsTriangle(ray,
							vertices[i + 2], vertices[(i + 3)], vertices[i],
							distanceTemp, null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
				}

			} else if (AxleT.byValue(axle.gettype()).toString().contains("Pin")) {
				Vector3f[] vertices = getAxleBarVertices_Pin();
				for (int i = 0; i < vertices.length; i += 4) {
					if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
							vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
							null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
					if (MatrixMath.V3RayIntersectsTriangle(ray,
							vertices[i + 2], vertices[(i + 3)], vertices[i],
							distanceTemp, null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
				}

				vertices = getGuideVertices();
				for (int i = 0; i < vertices.length; i += 4) {
					if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
							vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
							null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
					if (MatrixMath.V3RayIntersectsTriangle(ray,
							vertices[i + 2], vertices[(i + 3)], vertices[i],
							distanceTemp, null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
				}

			} else { // O axle
				Vector3f[] vertices = getAxleBarVertices_O();
				for (int i = 0; i < vertices.length; i += 4) {
					if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
							vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
							null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
					if (MatrixMath.V3RayIntersectsTriangle(ray,
							vertices[i + 2], vertices[(i + 3)], vertices[i],
							distanceTemp, null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
				}

				vertices = getGuideVertices();
				for (int i = 0; i < vertices.length; i += 4) {
					if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
							vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
							null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
					if (MatrixMath.V3RayIntersectsTriangle(ray,
							vertices[i + 2], vertices[(i + 3)], vertices[i],
							distanceTemp, null)) {
						if (distance != null)
							if (distanceTemp.get(0) < distance.get(0))
								distance.put(0, distanceTemp.get(0));
						isHitted = true;
					}
				}

			}
			Vector3f[] vertices = getCapVertices();
			for (int i = 0; i < vertices.length; i += 3) {
				if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
						vertices[(i + 1)], vertices[(i + 2)], distanceTemp,
						null)) {
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

	private Vector3f[] getGuideVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		final float cylinderRadius = LDrawGridTypeT.Medium.getXZValue() / 1.6f;

		Vector3f vertex;
		int slice = 24;
		vertex = new Vector3f();
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
		vertex = new Vector3f(0, -axle.getlength(), 0);
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
			retArray[i] = axle.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	private Vector3f[] getAxleSocketVertices() {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();

		final float cylinderRadius = LDrawGridTypeT.Medium.getXZValue() / 1.6f;
		final float cylinderHeight = -axle.getlength();

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
			retArray[i] = axle.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}
}
