package BrickControlGuide;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import Builder.MainCamera;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector3f;
import LDraw.Support.MatrixMath;

import com.jogamp.opengl.util.gl2.GLUT;

public class RotationGuide extends DefaultGuideRenderer {
	private GLUT glut;

	public RotationGuide(GLU glu, GLUT glut) {
		super.glu = glu;
		this.glut = glut;
	}

	public void drawSphere(GL2 gl2, MainCamera camera, Vector3f orientation) {

		Vector3f[] vertices = getSphereForTorusVertices(camera, orientation);
		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 20 / 4f;
		GLUquadric body = glu.gluNewQuadric();
		int slices = 20;
		int stacks = 10;
		glu.gluQuadricTexture(body, false);
		glu.gluQuadricDrawStyle(body, GLU.GLU_FILL);
		glu.gluQuadricNormals(body, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(body, GLU.GLU_OUTSIDE);
		for (int i = 0; i < vertices.length; i++) {
			gl2.glLoadMatrixf(camera.getModelView(), 0);
			gl2.glTranslatef(vertices[i].x, vertices[i].y, vertices[i].z);
			glu.gluSphere(body, cylinderRadius, slices, stacks);
		}
	}

	@Override
	public void draw(GL2 gl2, MainCamera camera, Vector3f orientation) {
		if (centerConnector != null)
			orientation = getCenterPos();
		// drawSphere(gl2, camera, orientation);

		// if (orientation != null)
		// return;

		float line_length = camera.getDistanceBetweenObjectToCamera() / 4;

		gl2.glLoadMatrixf(camera.getModelView(), 0);
		// draw guide line for the axis
		gl2.glBegin(GL2.GL_LINES); // draw using triangles
		gl2.glColor3d(r, g, b);
		gl2.glVertex3f(orientation.getX(), orientation.getY(),
				orientation.getZ());
		Vector3f endPos = getAxisDirectionVector().scale(line_length).add(
				orientation);
		gl2.glVertex3f(endPos.x, endPos.y, endPos.z);
		gl2.glEnd();

		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 40f;
		int slices = 20;
		int stacks = 10;
		gl2.glLoadMatrixf(camera.getModelView(), 0);
		endPos = getAxisDirectionVector().scale(
				line_length - cylinderRadius * 2).add(orientation);

		gl2.glTranslatef(endPos.x, endPos.y, endPos.z);

		Vector3f unitZVector = new Vector3f(0, 0, 1);
		Vector3f rotationVector = MatrixMath.V3Cross(unitZVector,
				getAxisDirectionVector());
		float angle = (float) Math.acos(getAxisDirectionVector().dot(
				unitZVector)
				/ (getAxisDirectionVector().length() * unitZVector.length()));
		int angleInDegree = Math.round(angle / (float) (Math.PI * 2) * 360);

		if (rotationVector.length() > 0.1f && Float.isNaN(angle) == false
				&& MatrixMath.compareFloat(angle, 0) != 0)
			gl2.glRotatef(angleInDegree, rotationVector.x, rotationVector.y,
					rotationVector.z);
		else if (rotationVector.length() < 0.1f && angleInDegree == 180)
			gl2.glRotatef(angleInDegree, 1, 0, 0);

		glut.glutSolidTorus(cylinderRadius / 2, cylinderRadius, slices, stacks);
	}

	private Vector3f[] getSphereForTorusVertices(MainCamera camera,
			Vector3f orientation) {
		Vector3f[] vertices = new Vector3f[20];

		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 20f;
		float centerOfTorus = camera.getDistanceBetweenObjectToCamera() / 4f
				- cylinderRadius;
		Vector3f endPos = getAxisDirectionVector().scale(centerOfTorus).add(
				orientation);

		for (int i = 0; i < vertices.length; i++)
			vertices[i] = new Vector3f((float) (cylinderRadius / 2 * Math.cos(i
					* Math.PI * 2 / (vertices.length))),
					(float) (cylinderRadius / 2 * Math.sin(i * Math.PI * 2
							/ (vertices.length))), 0);

		Vector3f unitZVector = new Vector3f(0, 0, 1);
		Vector3f rotationVector = MatrixMath.V3Cross(unitZVector,
				getAxisDirectionVector());
		float angle = (float) Math.acos(getAxisDirectionVector().dot(
				unitZVector)
				/ (getAxisDirectionVector().length() * unitZVector.length()));
		int angleInDegree = Math.round(angle / (float) (Math.PI * 2) * 360);

		Matrix4 rotationMatrix = Matrix4.getIdentityMatrix4();
		if (rotationVector.length() > 0.1f && Float.isNaN(angle) == false
				&& MatrixMath.compareFloat(angle, 0) != 0)
			rotationMatrix.rotate(-angle, rotationVector);
		else if (rotationVector.length() < 0.1f && angleInDegree == 180)
			rotationMatrix.rotate(angle, new Vector3f(1, 0, 0));

		for (int i = 0; i < vertices.length; i++)
			vertices[i] = MatrixMath.V3RotateByTransformMatrix(vertices[i],
					rotationMatrix).add(endPos);
		return vertices;
	}

	@Override
	public boolean isHitted(MainCamera camera, Vector3f orientation,
			float screenX, float screenY, FloatBuffer distance) {
		if (centerConnector != null)
			orientation = getCenterPos();

		final float cylinderRadius = camera.getDistanceBetweenObjectToCamera() / 20f;
		Ray3 ray = camera.getRay(screenX, screenY);
		FloatBuffer distanceTemp = FloatBuffer.allocate(1);

		boolean isHitted=false;
		for (Vector3f vertex : getSphereForTorusVertices(camera, orientation))
			if (MatrixMath.V3RayIntersectsSphere(ray, vertex,
					cylinderRadius / 4, distanceTemp)) {
				if (distanceTemp.get(0) < distance.get(0))
					distance.put(0, distanceTemp.get(0));
				isHitted=true;
			}
		lastHittedDistance = distance.get(0);
		return isHitted;
	}

	@Override
	public float getLastHittedDistance() {

		return lastHittedDistance;
	}
}
