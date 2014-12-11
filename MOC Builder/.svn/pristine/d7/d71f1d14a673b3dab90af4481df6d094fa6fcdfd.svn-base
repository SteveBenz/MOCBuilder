package Connectivity;

import com.bulletphysics.collision.dispatch.CollisionObject;

import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;
import LDraw.Support.MatrixMath;

public class CollisionShape extends Connectivity {
	
	protected CollisionObject jbulletObject;
	
	public CollisionShape() {
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "CollisionShape";
	}

	public Vector3f[] MakeDirection(Matrix4 partMatrix) {
		Vector3f[] vAxisDir = new Vector3f[3];
		vAxisDir[0] = new Vector3f(1, 0, 0);
		vAxisDir[1] = new Vector3f(0, 1, 0);
		vAxisDir[2] = new Vector3f(0, 0, 1);

		for (int i = 0; i < 3; i++) {
			vAxisDir[i] = MatrixMath.V3RotateByTransformMatrix(vAxisDir[i],
					getTransformMatrix());

			vAxisDir[i] = MatrixMath.V3RotateByTransformMatrix(vAxisDir[i],
					partMatrix);
		}
		return vAxisDir;
	}

	public Vector3f getCenter(LDrawPart part) {
		Vector3f center = new Vector3f(getTransformMatrix().getElement(3, 0),
				getTransformMatrix().getElement(3, 1), getTransformMatrix()
						.getElement(3, 2));
		
		center =part.transformationMatrix().transformPoint(center);
		
		return center;
	}

	public Vector3f getCenter(Matrix4 partTransformMatrix) {
		Vector3f center = new Vector3f(getTransformMatrix().getElement(3, 0),
				getTransformMatrix().getElement(3, 1), getTransformMatrix()
						.getElement(3, 2));
		center = partTransformMatrix.transformPoint(center);		
		return center;
	}

	public float[] GetAxisLength() {
		float[] fAxisLen = new float[3];
		return fAxisLen;
	}
	
	public CollisionObject getJBulletCollisionObject(Matrix4 partTransformMatrix){
		return null;
	}
}