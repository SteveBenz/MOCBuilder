package Connectivity;

import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;
import LDraw.Support.MatrixMath;

public class CollisionBox extends Connectivity {
	float sX, sY, sZ;

	public CollisionBox() {
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
		return "CollisionBox";
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

		fAxisLen[0] = sX;
		fAxisLen[1] = sY;
		fAxisLen[2] = sZ;
		return fAxisLen;
	}

	static boolean CheckOBBCollision(CollisionBox box1, Vector3f box1CenterPos,
			Vector3f[] box1vAxisDir, float[] box1fAxisLen, CollisionBox box2,
			Vector3f box2CenterPos, Vector3f[] box2vAxisDir,
			float[] box2fAxisLen) {
		float[][] c = new float[3][3];
		float[][] absC = new float[3][3];
		float[] d = new float[3];

		float r0, r1, r;
		int i;

		float cutoff = 0.99999999999999999999f;
		boolean existsParallelPair = false;

		Vector3f diff = box1CenterPos.sub(box2CenterPos);

		for (i = 0; i < 3; ++i) {
			c[0][i] = box1vAxisDir[0].dot(box2vAxisDir[i]);
			absC[0][i] = Math.abs(c[0][i]);
			if (absC[0][i] > cutoff)
				existsParallelPair = true;
		}
		d[0] = diff.dot(box1vAxisDir[0]);
		r = Math.abs(d[0]);
		r0 = box1fAxisLen[0];
		r1 = box2fAxisLen[0] * absC[0][0] + box2fAxisLen[1] * absC[0][1]
				+ box2fAxisLen[2] * absC[0][2];

		if (r > r0 + r1)
			return false;
		for (i = 0; i < 3; ++i) {
			c[1][i] = box1vAxisDir[1].dot(box2vAxisDir[i]);
			absC[1][i] = Math.abs(c[1][i]);
			if (absC[1][i] > cutoff)
				existsParallelPair = true;
		}
		d[1] = diff.dot(box1vAxisDir[1]);
		r = Math.abs(d[1]);
		r0 = box1fAxisLen[1];
		r1 = box2fAxisLen[0] * absC[1][0] + box2fAxisLen[1] * absC[1][1]
				+ box2fAxisLen[2] * absC[1][2];

		if (r > r0 + r1)
			return false;

		for (i = 0; i < 3; ++i) {
			c[2][i] = box1vAxisDir[2].dot(box2vAxisDir[i]);
			absC[2][i] = Math.abs(c[2][i]);
			if (absC[2][i] > cutoff)
				existsParallelPair = true;
		}
		d[2] = diff.dot(box1vAxisDir[2]);
		r = Math.abs(d[2]);
		r0 = box1fAxisLen[2];
		r1 = box2fAxisLen[0] * absC[2][0] + box2fAxisLen[1] * absC[2][1]
				+ box2fAxisLen[2] * absC[2][2];

		if (r > r0 + r1)
			return false;

		r = Math.abs(diff.dot(box2vAxisDir[0]));
		r0 = box1fAxisLen[0] * absC[0][0] + box1fAxisLen[1] * absC[1][0]
				+ box1fAxisLen[2] * absC[2][0];
		r1 = box2fAxisLen[0];

		if (r > r0 + r1)
			return false;

		r = Math.abs(diff.dot(box2vAxisDir[1]));
		r0 = box1fAxisLen[0] * absC[0][1] + box1fAxisLen[1] * absC[1][1]
				+ box1fAxisLen[2] * absC[2][1];
		r1 = box2fAxisLen[1];

		if (r > r0 + r1)
			return false;

		r = Math.abs(diff.dot(box2vAxisDir[2]));
		r0 = box1fAxisLen[0] * absC[0][2] + box1fAxisLen[1] * absC[1][2]
				+ box1fAxisLen[2] * absC[2][2];
		r1 = box2fAxisLen[2];

		if (r > r0 + r1)
			return false;

		if (existsParallelPair == true)
			return true;

		r = Math.abs(d[2] * c[1][0] - d[1] * c[2][0]);
		r0 = box1fAxisLen[1] * absC[2][0] + box1fAxisLen[2] * absC[1][0];
		r1 = box2fAxisLen[1] * absC[0][2] + box2fAxisLen[2] * absC[0][1];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[2] * c[1][1] - d[1] * c[2][1]);
		r0 = box1fAxisLen[1] * absC[2][1] + box1fAxisLen[2] * absC[1][1];
		r1 = box2fAxisLen[0] * absC[0][2] + box2fAxisLen[2] * absC[0][0];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[2] * c[1][2] - d[1] * c[2][2]);
		r0 = box1fAxisLen[1] * absC[2][2] + box1fAxisLen[2] * absC[1][2];
		r1 = box2fAxisLen[0] * absC[0][1] + box2fAxisLen[1] * absC[0][0];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[0] * c[2][0] - d[2] * c[0][0]);
		r0 = box1fAxisLen[0] * absC[2][0] + box1fAxisLen[2] * absC[0][0];
		r1 = box2fAxisLen[1] * absC[1][2] + box2fAxisLen[2] * absC[1][1];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[0] * c[2][1] - d[2] * c[0][1]);
		r0 = box1fAxisLen[0] * absC[2][1] + box1fAxisLen[2] * absC[0][1];
		r1 = box2fAxisLen[0] * absC[1][2] + box2fAxisLen[2] * absC[1][0];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[0] * c[2][2] - d[2] * c[0][2]);
		r0 = box1fAxisLen[0] * absC[2][2] + box1fAxisLen[2] * absC[0][2];
		r1 = box2fAxisLen[0] * absC[1][1] + box2fAxisLen[1] * absC[1][0];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[1] * c[0][0] - d[0] * c[1][0]);
		r0 = box1fAxisLen[0] * absC[1][0] + box1fAxisLen[1] * absC[0][0];
		r1 = box2fAxisLen[1] * absC[2][2] + box2fAxisLen[2] * absC[2][1];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[1] * c[0][1] - d[0] * c[1][1]);
		r0 = box1fAxisLen[0] * absC[1][1] + box1fAxisLen[1] * absC[0][1];
		r1 = box2fAxisLen[0] * absC[2][2] + box2fAxisLen[2] * absC[2][0];
		if (r > r0 + r1)
			return false;

		r = Math.abs(d[1] * c[0][2] - d[0] * c[1][2]);
		r0 = box1fAxisLen[0] * absC[1][2] + box1fAxisLen[1] * absC[0][2];
		r1 = box2fAxisLen[0] * absC[2][1] + box2fAxisLen[1] * absC[2][0];
		if (r > r0 + r1)
			return false;

		return true;
	}

}