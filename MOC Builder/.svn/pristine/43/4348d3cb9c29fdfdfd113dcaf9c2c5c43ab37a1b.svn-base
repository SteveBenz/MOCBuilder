package BrickControlGuide;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import Builder.MainCamera;
import Common.Box3;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector3f;
import LDraw.Support.MatrixMath;

public class MovementGuide extends DefaultGuideRenderer {
	public MovementGuide(GLU glu) {
		super.glu = glu;
	}

	private Vector3f[] getCylinderVertices(MainCamera camera,
			Vector3f orientation) {
		Vector3f[] vertices = new Vector3f[21];
		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 40f;
		final float cylinderHeight = camera.getDistanceBetweenObjectToCamera() / 20;

		vertices[vertices.length - 1] = new Vector3f(0, 0, cylinderHeight);
		for (int i = 0; i < vertices.length - 1; i++) {
			vertices[i] = new Vector3f((float) (cylinderRadius * Math.cos(i
					* Math.PI * 2 / (vertices.length - 1))),
					(float) (cylinderRadius * Math.sin(i * Math.PI * 2
							/ (vertices.length - 1))), 0);
		}

		float line_length = camera.getDistanceBetweenObjectToCamera() / 4;
		Vector3f unitZVector = new Vector3f(0, 0, 1);
		Vector3f rotationVector = MatrixMath.V3Cross(unitZVector,
				getAxisDirectionVector());

		if (rotationVector.equals(new Vector3f()))
			rotationVector = new Vector3f(1, 0, 0);
		Matrix4 rotationMatrix = Matrix4.getIdentityMatrix4();
		if (MatrixMath.compareFloat(rotationVector.length(), 0) != 0)
			rotationVector = rotationVector.scale(1 / rotationVector.length());
		float angle = (float) Math.acos(unitZVector
				.dot(getAxisDirectionVector())
				/ (getAxisDirectionVector().length() * unitZVector.length()));
		int angleInDegree = Math.round(angle / (float) (Math.PI * 2) * 360);
		if (MatrixMath.compareFloat(rotationVector.length(), 0) != 0
				&& Float.isNaN(angle) == false
				&& MatrixMath.compareFloat(angle, 0) != 0)
			rotationMatrix.rotate(-angle, rotationVector);
		else if (MatrixMath.compareFloat(rotationVector.length(), 0) != 0
				&& angleInDegree == 180)
			rotationMatrix.rotate(angle, new Vector3f(1, 0, 0));

		for (int i = 0; i < vertices.length; i++)
			vertices[i] = MatrixMath.V3RotateByTransformMatrix(vertices[i],
					rotationMatrix);
		Vector3f endPos = getAxisDirectionVector().scale(line_length).add(
				orientation);
		for (int i = 0; i < vertices.length; i++)
			vertices[i] = vertices[i].add(endPos);
		return vertices;
	}

	public void drawCylinder(GL2 gl2, MainCamera camera, Vector3f orientation) {
		Vector3f[] vertices = getCylinderVertices(camera, orientation);
		gl2.glLoadMatrixf(camera.getModelView(), 0);
		gl2.glBegin(GL2.GL_LINES); // draw using triangles
		gl2.glColor3d(r, g, b);
		for (int i = 0; i < vertices.length - 2; i++) {
			gl2.glVertex3f(vertices[i].x, vertices[i].y, vertices[i].z);
			gl2.glVertex3f(vertices[i + 1].x, vertices[i + 1].y,
					vertices[i + 1].z);
		}
		for (int i = 0; i < vertices.length - 1; i++) {
			gl2.glVertex3f(vertices[i].x, vertices[i].y, vertices[i].z);
			gl2.glVertex3f(vertices[vertices.length - 1].x,
					vertices[vertices.length - 1].y,
					vertices[vertices.length - 1].z);
		}
		gl2.glEnd();
	}

	@Override
	public void draw(GL2 gl2, MainCamera camera, Vector3f orientation) {
		if (centerConnector != null)
			orientation = getCenterPos();

		float line_length = camera.getDistanceBetweenObjectToCamera() / 4;

		gl2.glLoadMatrixf(camera.getModelView(), 0);
		// draw guide line for x, y, z axis
		gl2.glBegin(GL2.GL_LINES); // draw using triangles
		gl2.glColor3d(r, g, b);
		gl2.glVertex3f(orientation.getX(), orientation.getY(),
				orientation.getZ());
		Vector3f endPos = getAxisDirectionVector().scale(line_length).add(
				orientation);
		gl2.glVertex3f(endPos.x, endPos.y, endPos.z);
		gl2.glEnd();

		// if (orientation != null)
		// return;

		// draw guide arrow for x, y, z axis movement
		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 40f;
		final float cylinderHeight = camera.getDistanceBetweenObjectToCamera() / 20;
		GLUquadric body = glu.gluNewQuadric();
		int slices = 20;
		int stacks = 10;
		glu.gluQuadricTexture(body, false);
		glu.gluQuadricDrawStyle(body, GLU.GLU_FILL);
		glu.gluQuadricNormals(body, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(body, GLU.GLU_OUTSIDE);

		gl2.glLoadMatrixf(camera.getModelView(), 0);
		gl2.glTranslatef(endPos.x, endPos.y, endPos.z);

		Vector3f unitZVector = new Vector3f(0, 0, 1);
		Vector3f rotationVector = MatrixMath.V3Cross(unitZVector,
				getAxisDirectionVector());
		if (MatrixMath.compareFloat(rotationVector.length(), 0) != 0)
			rotationVector = rotationVector.scale(1 / rotationVector.length());

		if (rotationVector.equals(new Vector3f()))
			rotationVector = new Vector3f(1, 0, 0);
		float angle = (float) Math.acos(unitZVector
				.dot(getAxisDirectionVector())
				/ (getAxisDirectionVector().length() * unitZVector.length()));
		int angleInDegree = Math.round(angle / (float) (Math.PI * 2) * 360);
		// System.out.println("first transform: " + rotationVector + ", "
		// + angleInDegree + "(" + angle + ")");
		if (MatrixMath.compareFloat(rotationVector.length(), 0) != 0
				&& Float.isNaN(angle) == false
				&& MatrixMath.compareFloat(angle, 0) != 0)
			gl2.glRotatef(angleInDegree, rotationVector.x, rotationVector.y,
					rotationVector.z);
		else if (MatrixMath.compareFloat(rotationVector.length(), 0) != 0
				&& angleInDegree == 180)
			gl2.glRotatef(angleInDegree, 1, 0, 0);

		glu.gluDisk(body, 0, cylinderRadius, slices, 2);
		glu.gluCylinder(body, cylinderRadius, 0, cylinderHeight, slices, stacks);
	}

	@Override
	public boolean isHitted(MainCamera camera, Vector3f orientation,
			float screenX, float screenY, FloatBuffer distance) {

		if (centerConnector != null)
			orientation = getCenterPos();

		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);

		Vector3f[] vertices = getCylinderVertices(camera, orientation);
		boolean isHitted = false;
		for (int i = 0; i < vertices.length - 1; i++) {
			if (MatrixMath.V3RayIntersectsTriangle(ray, vertices[i],
					vertices[(i + 1 % (vertices.length - 1))],
					vertices[vertices.length - 1], distanceTemp, null)) {
				if (distance != null)
					if (distanceTemp.get(0) < distance.get(0))
						distance.put(0, distanceTemp.get(0));
				isHitted = true;
			}
		}
		lastHittedDistance = distance.get(0);
		return isHitted;
	}

	@Override
	public float getLastHittedDistance() {
		return lastHittedDistance;
	}
}
