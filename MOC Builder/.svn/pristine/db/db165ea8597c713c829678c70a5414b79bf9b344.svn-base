package Connectivity;

import Common.Matrix4;
import Common.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;

public class CollisionSphere extends CollisionShape {
	float radius;

	public CollisionSphere() {
	}

	public void setSize(float radius) {
		this.radius = radius;
	}

	public void setRadius(String radius) {
		this.radius = Float.parseFloat(radius);
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	public float getRadius() {
		return radius;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CollisionSphere";
	}

	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		setRadius(line[size + 1]);
		return 0;
	}

	@Override
	public String toString() {
		return super.toString(String.format("%.6f", radius));
	}

	@Override
	public float[] GetAxisLength() {
		float[] fAxisLen = new float[3];

		fAxisLen[0] = radius;
		fAxisLen[1] = radius;
		fAxisLen[2] = radius;
		return fAxisLen;
	}

	@Override
	public CollisionObject getJBulletCollisionObject(Matrix4 partTransformMatrix) {
		if (this.jbulletObject == null) {
			jbulletObject = new CollisionObject();
			com.bulletphysics.collision.shapes.CollisionShape colShape = new SphereShape(
					radius);

			jbulletObject.setCollisionShape(colShape);
		}

		Transform transform = new Transform();
		transform.setIdentity();
		Matrix4 rotationMatrix = null;
		Vector3f center = getCurrentPos(partTransformMatrix);

		rotationMatrix = Matrix4.multiplyForRotation(getTransformMatrix(),
				partTransformMatrix);

		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				transform.basis.setElement(i, j,
						rotationMatrix.getElement(j, i));
			}
		transform.basis.normalizeCP();
		transform.origin.x = center.x;
		transform.origin.y = center.y;
		transform.origin.z = center.z;

		jbulletObject.setWorldTransform(transform);
		return jbulletObject;
	}

	public void apply(CollisionSphere newItem) {
		this.radius = newItem.getRadius();
	}
}