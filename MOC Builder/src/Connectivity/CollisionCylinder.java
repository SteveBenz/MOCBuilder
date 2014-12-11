package Connectivity;

import Common.Matrix4;
import Common.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.CylinderShape;
import com.bulletphysics.linearmath.Transform;

public class CollisionCylinder extends CollisionShape {
	float sX, sY, sZ;

	public CollisionCylinder() {
	}

	public float getsX() {
		return sX;
	}

	public void setsX(String sX) {
		this.sX = Float.parseFloat(sX);
	}

	public void setSize(float sx, float sy, float sz) {
		this.sX = sx;
		this.sY = sy;
		this.sZ = sz;
	}

	public float getsY() {
		return sY;
	}

	public void setsY(String sY) {
		this.sY = Float.parseFloat(sY);
	}

	public float getsZ() {
		return sZ;
	}

	public void setsZ(String sZ) {
		this.sZ = Float.parseFloat(sZ);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CollisionCylinder";
	}

	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		setsX(line[size + 1]);
		setsY(line[size + 2]);
		setsZ(line[size + 3]);
		return 0;
	}

	@Override
	public String toString() {
		return super.toString(String.format("%.6f %.6f %.6f", sX, sY, sZ));
	}

	@Override
	public float[] GetAxisLength() {
		float[] fAxisLen = new float[3];

		fAxisLen[0] = sX;
		fAxisLen[1] = sY;
		fAxisLen[2] = sZ;
		return fAxisLen;
	}

	@Override
	public CollisionObject getJBulletCollisionObject(Matrix4 partTransformMatrix) {
		if (this.jbulletObject == null) {
			jbulletObject = new CollisionObject();
			com.bulletphysics.collision.shapes.CollisionShape colShape = new CylinderShape(
					new javax.vecmath.Vector3f(sX, sY, sZ));

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

	public void apply(CollisionCylinder newItem) {
		this.sX = newItem.getsX();
		this.sY = newItem.getsY();
		this.sZ = newItem.getsZ();
	}
}