package ConnectivityEditor.Window;

import java.util.ArrayList;

import Common.Matrix4;
import Common.Vector3f;
import Connectivity.CollisionBox;
import Connectivity.CollisionConvexHull;
import Connectivity.CollisionShape;
import LDraw.Files.LDrawModel;
import LDraw.Support.MatrixMath;
import LDraw.Support.PartLibrary;
import Renderer.ILDrawCollector;
import Renderer.LDrawTextureSpec;

public class CollisionShapeGenerator implements ILDrawCollector {

	private static CollisionShapeGenerator _instance = null;

	private CollisionShapeGenerator() {
		quad = new ArrayList<Vector3f>();
		tri = new ArrayList<Vector3f>();
	}

	public static synchronized CollisionShapeGenerator getInstance() {
		if (_instance == null)
			_instance = new CollisionShapeGenerator();
		return _instance;
	}

	private ArrayList<Vector3f> quad = null;
	private ArrayList<Vector3f> tri = null;

	public ArrayList<CollisionShape> generateCollisionShape(String partName) {
		quad.clear();
		tri.clear();

		ArrayList<CollisionShape> retList = new ArrayList<CollisionShape>();
//		LDrawModel model = PartLibrary.sharedPartLibrary().modelForName(
//				partName);
//		if (model != null)
//			model.collectSelf(this);
//
//		for (int i = 0; i < quad.size(); i += 4) {
//			CollisionConvexHull collisionObj = new CollisionConvexHull();
//			Vector3f vertices[] = new Vector3f[4];
//			for (int j = 0; j < 4; j++) {
//				collisionObj.addVertiex(quad.get(i + j));
//				vertices[j] = quad.get(i + j);
//			}
//				retList.add(collisionObj);
//		}

		// for (int i = 0; i < tri.size(); i += 3) {
		// CollisionConvexHull collisionObj = new CollisionConvexHull();
		// for (int j = 0; j < 3; j++)
		// collisionObj.addVertiex(tri.get(i + j));
		// retList.add(collisionObj);
		// }
		return retList;
	}

	public boolean isTooSmall(Vector3f[] vertices) {
		Vector3f edge[] = new Vector3f[4];
		float[] edgeLength = new float[4];
		for (int k = 0; k < 4; k++) {
			edge[k] = vertices[(k + 1) % 4].sub(vertices[k]);
			edgeLength[k] = edge[k].length();
		}

		boolean isTooSmall = true;
		for (int k = 0; k < 2; k++)
			if (edgeLength[k] >= 5)
				isTooSmall = false;
		return isTooSmall;
	}

	public CollisionBox getCollisionBox(Vector3f[] vertices) {
		Vector3f edge[] = new Vector3f[4];
		float[] edgeLength = new float[4];
		for (int k = 0; k < 4; k++) {
			edge[k] = vertices[(k + 1) % 4].sub(vertices[k]);
			edgeLength[k] = edge[k].length();
		}

		boolean isTooSmall = true;
		for (int k = 0; k < 2; k++)
			if (edgeLength[k] >= 5)
				isTooSmall = false;

		if (isTooSmall)
			return null;

		if (MatrixMath.compareFloat(edge[0].dot(edge[1]), 0) != 0
				|| MatrixMath.compareFloat(edge[1].dot(edge[2]), 0) != 0) {
			// ���� �� ���� ã��.
			int longestEdgeIndex = 0;
			float longestEdgeLength = -1;
			for (int i = 0; i < 4; i++)
				if (edgeLength[i] > longestEdgeLength) {
					longestEdgeLength = edgeLength[i];
					longestEdgeIndex = i;
				}

			float offset0 = -edge[(longestEdgeIndex + 3) % 4]
					.dot(edge[longestEdgeIndex]) / edgeLength[longestEdgeIndex];
			float offset1 = edge[(longestEdgeIndex + 1) % 4]
					.dot(edge[longestEdgeIndex]) / edgeLength[longestEdgeIndex];

			if (offset0 > 0)
				vertices[longestEdgeIndex] = vertices[longestEdgeIndex]
						.add(MatrixMath.V3Normalize(edge[longestEdgeIndex])
								.scale(offset0));
			else
				vertices[(longestEdgeIndex + 3) % 4] = vertices[(longestEdgeIndex + 3) % 4]
						.add(MatrixMath.V3Normalize(edge[longestEdgeIndex])
								.scale(-offset0));

			if (offset1 < 0)
				vertices[(longestEdgeIndex + 1) % 4] = vertices[(longestEdgeIndex + 1) % 4]
						.add(MatrixMath.V3Normalize(edge[longestEdgeIndex])
								.scale(offset1));
			else
				vertices[(longestEdgeIndex + 2) % 4] = vertices[(longestEdgeIndex + 2) % 4]
						.add(MatrixMath.V3Normalize(edge[longestEdgeIndex])
								.scale(-offset1));

			for (int k = 0; k < 4; k++) {
				edge[k] = vertices[(k + 1) % 4].sub(vertices[k]);
				edgeLength[k] = edge[k].length();
			}

			if (MatrixMath.compareFloat(edgeLength[(longestEdgeIndex + 3) % 4],
					edgeLength[(longestEdgeIndex + 1) % 4]) != 0) {

				if (edgeLength[(longestEdgeIndex + 3) % 4] < edgeLength[(longestEdgeIndex + 1) % 4]) {
					vertices[(longestEdgeIndex + 2) % 4] = vertices[(longestEdgeIndex + 3) % 4]
							.add(edge[longestEdgeIndex]);
				} else
					vertices[(longestEdgeIndex + 3) % 4] = vertices[(longestEdgeIndex + 2) % 4]
							.sub(edge[longestEdgeIndex]);

			}
		}

		Vector3f initialNormalVector = new Vector3f(0, 0, 1);
		Vector3f finalNormalVector = MatrixMath.V3Cross(
				MatrixMath.V3Sub(vertices[1], vertices[0]),
				MatrixMath.V3Sub(vertices[3], vertices[0]));
		finalNormalVector.normalize();

		Vector3f rotationVector = initialNormalVector.cross(finalNormalVector);

		float rotationDegree = (float) Math
				.acos(initialNormalVector.dot(finalNormalVector)
						/ (initialNormalVector.length() * initialNormalVector
								.length()));

		if (Float.isNaN(rotationDegree))
			rotationDegree = 0;
		if (rotationVector.equals(Vector3f.getZeroVector3f()))
			rotationDegree = 0;

		Matrix4 testMatrix = Matrix4.getIdentityMatrix4();
		if (rotationDegree != 0)
			testMatrix.rotate(-rotationDegree, rotationVector);
		else if (finalNormalVector.equals(testMatrix
				.transformPoint(initialNormalVector)) == false) {
			// System.out.println(finalNormalVector+" : "+testMatrix
			// .transformPoint(initialNormalVector));
			testMatrix.rotate((float) Math.PI, new Vector3f(1, 0, 0));
		}

		if (finalNormalVector.equals(testMatrix
				.transformPoint(initialNormalVector)) == false) {
			System.out.println("Fist Rotation: "
					+ testMatrix.transformPoint(initialNormalVector) + " : "
					+ finalNormalVector);
			return null;
		}

		Vector3f unitDirectionVector2 = new Vector3f(0, 1, 0);

		Vector3f finalDirectionVector2 = vertices[2].sub(vertices[1]);
		finalDirectionVector2.normalize();

		Vector3f transformedUnit = testMatrix
				.transformPoint(unitDirectionVector2);

		if (transformedUnit.equals(finalDirectionVector2.scale(-1))) {
			testMatrix.rotate((float) Math.PI, finalNormalVector);
		}

		Vector3f rotationVector2 = transformedUnit.cross(finalDirectionVector2);
		rotationVector2.normalize();

		// if(rotationVector2.equals(finalNormalVector)==false){
		// System.out.println("asdf");
		// }

		float rotationDegree2 = (float) Math.acos(testMatrix.transformPoint(
				unitDirectionVector2).dot(finalDirectionVector2)
				/ (finalDirectionVector2.length() * finalDirectionVector2
						.length()));

		if (Float.isNaN(rotationDegree2))
			rotationDegree2 = 0;
		if (rotationVector2.equals(Vector3f.getZeroVector3f()))
			rotationDegree2 = 0;

		// System.out.println("##################################");
		// System.out.println(testMatrix.transformPoint(initialNormalVector) +
		// " : "
		// + finalNormalVector + ":: " +
		// rotationVector2+"("+rotationDegree2+")");

		if (rotationDegree2 != 0)
			testMatrix.rotate(-rotationDegree2, rotationVector2);
		else if (finalDirectionVector2.equals(testMatrix
				.transformPoint(unitDirectionVector2)) == false) {
			System.out.println(finalDirectionVector2 + " : "
					+ testMatrix.transformPoint(unitDirectionVector2));
			// testMatrix.rotate((float) Math.PI, new Vector3f(1, 0, 0));
		}
		//

		if (finalNormalVector.equals(testMatrix
				.transformPoint(initialNormalVector)) == false) {
			System.out.println("Second Rotation_1: "
					+ testMatrix.transformPoint(initialNormalVector) + " : "
					+ finalNormalVector + ":: " + rotationVector2 + "("
					+ rotationDegree2 + ")");

			// for(int i=0; i < 4; i++)
			// System.out.println(vertices[i]);
			return null;
		}
		//
		if (finalDirectionVector2.equals(testMatrix
				.transformPoint(unitDirectionVector2)) == false) {
			System.out.println("Second Rotation_2: "
					+ testMatrix.transformPoint(unitDirectionVector2) + " : "
					+ finalDirectionVector2 + ":: " + rotationDegree2);
			return null;
		}

		Vector3f normalVector = MatrixMath.V3Cross(
				MatrixMath.V3Sub(vertices[1], vertices[0]),
				MatrixMath.V3Sub(vertices[3], vertices[0]));
		normalVector.normalize();

		for (int k = 0; k < 4; k++) {
			vertices[k] = vertices[k].sub(normalVector.scale(0.1f));
		}
		//
		for (int k = 0; k < 4; k++) {
			edge[k] = vertices[(k + 1) % 4].sub(vertices[k]);
			edgeLength[k] = edge[k].length();
		}

		// System.out.println(rotationVector+" : "+rotationVector2);
		Vector3f size = new Vector3f(edgeLength[0], edgeLength[1], 0)
				.scale(0.49f);
		Vector3f center = vertices[0].add(vertices[2]).scale(0.5f);
		CollisionBox cb = new CollisionBox();
		cb.setSize(size.x, size.y, size.z);
		cb.setTransformMatrix(testMatrix);
		cb.moveTo(center);
		cb.setFileName("Collision Box");
		cb.updateConnectivityOrientationInfo();

		return cb;
	}

	@Override
	public void pushTexture(LDrawTextureSpec tex_spec) {
		// TODO Auto-generated method stub

	}

	@Override
	public void popTexture() {
		// TODO Auto-generated method stub

	}

	@Override
	public void drawQuad(float[] vertices, float[] normal, float[] color) {
		for (int i = 0; i < vertices.length; i += 3) {
			quad.add(new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));
		}

	}

	@Override
	public void drawTri(float[] vertices, float[] normal, float[] color) {
		for (int i = 0; i < vertices.length; i += 3) {
			tri.add(new Vector3f(vertices[i], vertices[i + 1], vertices[i + 2]));
		}
	}

	@Override
	public void drawLine(float[] vertices, float[] normal, float[] color) {
		// TODO Auto-generated method stub
	}
}
