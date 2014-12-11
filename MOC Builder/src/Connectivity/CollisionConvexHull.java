package Connectivity;

import java.util.ArrayList;

import Common.Matrix4;
import Common.Vector3f;

import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

public class CollisionConvexHull extends CollisionShape {
	private ArrayList<Vector3f> vertexList;

	public CollisionConvexHull() {
		vertexList = new ArrayList<Vector3f>();
	}

	public void addVertiex(Vector3f vertex) {
		vertexList.add(vertex);
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CollisionConvexHull";
	}

	@Override
	public int parseString(String[] line) {
		return 0;
	}

	@Override
	public String toString() {
		return super.toString("");
	}

	@Override
	public float[] GetAxisLength() {
		float[] fAxisLen = new float[3];
		return fAxisLen;
	}

	@Override
	public CollisionObject getJBulletCollisionObject(Matrix4 partTransformMatrix) {
		if (this.jbulletObject == null) {
			jbulletObject = new CollisionObject();
			ObjectArrayList<javax.vecmath.Vector3f> vertices = new ObjectArrayList<javax.vecmath.Vector3f>();
			for (Vector3f vertex : vertexList) {
				vertices.add(new javax.vecmath.Vector3f(vertex.getX(), vertex
						.getY(), vertex.getZ()));
			}
			com.bulletphysics.collision.shapes.CollisionShape colShape = new ConvexHullShape(
					vertices);

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
}