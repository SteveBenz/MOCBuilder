package ConnectivityEditor.Window;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.media.opengl.GL2;

import Builder.MainCamera;
import Common.Ray3;
import Common.Vector3f;
import Connectivity.CollisionCylinder;
import Connectivity.Connectivity;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.LDrawGridTypeT;

public class CollisionCylinderRenderer extends DefaultConnectivityRenderer {

	public CollisionCylinderRenderer(MainCamera camera, Connectivity conn) {
		super(camera, conn);
	}

	@Override
	public void draw(GL2 gl2) {
		gl2.glColor3d(0, 1, 0);
		if (conn.isSelected())
			gl2.glColor3d(0, 0, 0);

		float radius = ((CollisionCylinder) conn).getsX();
		float height = ((CollisionCylinder) conn).getsY();

		// draw base
		gl2.glLoadMatrixf(camera.getModelView(), 0);
		// Draw Stud Disk (possible styles: FILL, LINE, POINT).
		gl2.glBegin(GL2.GL_QUADS); // draw using triangles
		for (Vector3f vertex : getCylinderVertices(radius, height))
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();

		gl2.glBegin(GL2.GL_TRIANGLES);
		for (Vector3f vertex : getDiskVertices(radius, height))
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();

		gl2.glBegin(GL2.GL_TRIANGLES);
		for (Vector3f vertex : getDiskVertices(radius, -height))
			gl2.glVertex3f(vertex.x, vertex.y, vertex.z);
		gl2.glEnd();
	}

	private Vector3f[] getDiskVertices(float diskRadius, float diskHeight) {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		Vector3f vertex;
		int slice = 24;
		vertex = new Vector3f(LDrawGridTypeT.Coarse.getXZValue() / 2,
				diskHeight, -LDrawGridTypeT.Coarse.getXZValue() / 2);

		for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
			vertices.add(vertex);
			vertices.add(vertex.add(
					(float) (diskRadius * Math.cos((sliceIndex) * Math.PI * 2
							/ (slice - 1))),
					0,
					(float) (diskRadius * Math.sin((sliceIndex) * Math.PI * 2
							/ (slice - 1)))));
			vertices.add(vertex.add(
					(float) (diskRadius * Math.cos((sliceIndex + 1) * Math.PI
							* 2 / (slice - 1))),
					0,
					(float) (diskRadius * Math.sin((sliceIndex + 1) * Math.PI
							* 2 / (slice - 1)))));
		}

		Vector3f[] retArray = new Vector3f[vertices.size()];
		for (int i = 0; i < vertices.size(); i++) {
			retArray[i] = conn.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	private Vector3f[] getCylinderVertices(float cylinderRadius,
			float cylinderHeight) {
		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		Vector3f vertex;
		int slice = 24;
		vertex = new Vector3f(LDrawGridTypeT.Coarse.getXZValue() / 2, 0,
				-LDrawGridTypeT.Coarse.getXZValue() / 2);

		for (int sliceIndex = 0; sliceIndex < slice - 1; sliceIndex++) {
			vertices.add(vertex.add(
					(float) (cylinderRadius * Math.cos(sliceIndex * Math.PI * 2
							/ (slice - 1))),
					-cylinderHeight,
					(float) (cylinderRadius * Math.sin(sliceIndex * Math.PI * 2
							/ (slice - 1)))));
			vertices.add(vertex.add(
					(float) (cylinderRadius * Math.cos((sliceIndex + 1)
							* Math.PI * 2 / (slice - 1))),
					-cylinderHeight,
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
			retArray[i] = conn.getTransformMatrix().transformPoint(
					vertices.get(i));
		}
		return retArray;
	}

	@Override
	public boolean isHitted(MainCamera camera, float screenX, float screenY,
			FloatBuffer distance) {

		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);
		distanceTemp.put(Float.MAX_VALUE);

		float radius = ((CollisionCylinder) conn).getsX();
		float height = ((CollisionCylinder) conn).getsY();

		Vector3f[] vertices = getCylinderVertices(radius, height);
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

		vertices = getDiskVertices(radius, height);
		for (int i = 0; i < vertices.length; i += 3) {
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
					vertices[(i + 1)], vertices[(i + 2)], distanceTemp, null)) {
				if (distance != null)
					if (distanceTemp.get(0) < distance.get(0))
						distance.put(0, distanceTemp.get(0));
				isHitted = true;
			}
		}

		vertices = getDiskVertices(radius, -height);
		for (int i = 0; i < vertices.length; i += 3) {
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
					vertices[(i + 1)], vertices[(i + 2)], distanceTemp, null)) {
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
