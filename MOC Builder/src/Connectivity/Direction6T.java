package Connectivity;

import Common.Matrix4;
import Common.Vector3f;
import LDraw.Support.MatrixMath;

public enum Direction6T {
	X_Plus('X'), X_Minus('X'), Y_Plus('Y'), Y_Minus('Y'), Z_Plus('Z'), Z_Minus('Z');
	
	private char value;
	
	private Direction6T(char value){
		this.value = value;
	}

	public static Direction6T getDirectionOfTransformMatrix(
			Matrix4 transformMatrix) {
		Vector3f normalVector = new Vector3f(0, -1.0f, 0);
		normalVector = MatrixMath.V3RotateByTransformMatrix(normalVector,
				transformMatrix);

		float absMax = Math.max(Math.abs(normalVector.x),
				Math.abs(normalVector.y));
		absMax = Math.max(absMax, Math.abs(normalVector.z));

		if (MatrixMath.compareFloat(absMax, normalVector.y) == 0)
			return Y_Plus;
		else if (MatrixMath.compareFloat(-absMax, normalVector.y) == 0)
			return Y_Minus;
		else if (MatrixMath.compareFloat(absMax, normalVector.x) == 0)
			return X_Plus;
		else if (MatrixMath.compareFloat(-absMax, normalVector.x) == 0)
			return X_Minus;
		else if (MatrixMath.compareFloat(absMax, normalVector.z) == 0)
			return Z_Plus;
		else if (MatrixMath.compareFloat(-absMax, normalVector.z) == 0)
			return Z_Minus;

		return null;
	}
	
	public static Matrix4 getSnappedTransformMatrix(Matrix4 partTransformMatrix) {
		Matrix4 transformMatrix = Matrix4.getIdentityMatrix4();

		Direction6T direction = Direction6T
				.getDirectionOfTransformMatrix(partTransformMatrix);

		float degree = getDegreeOfTransformMatrixInDirection(direction,
				partTransformMatrix);

		switch (direction) {
		case X_Minus:
			transformMatrix.rotate((float) Math.PI / 2, new Vector3f(0, 0, 1));
			transformMatrix.rotate(degree, new Vector3f(-1, 0, 0));
			break;
		case X_Plus:
			transformMatrix.rotate((float) Math.PI / 2, new Vector3f(0, 0, -1));
			transformMatrix.rotate(degree, new Vector3f(-1, 0, 0));
			break;
		case Y_Minus:
			transformMatrix.rotate(degree, new Vector3f(0, 1, 0));
			break;
		case Y_Plus:
			transformMatrix.rotate((float) Math.PI, new Vector3f(1, 0, 0));
			transformMatrix.rotate(degree, new Vector3f(0, -1, 0));
			break;
		case Z_Minus:
			transformMatrix.rotate((float) Math.PI / 2, new Vector3f(-1, 0, 0));
			transformMatrix.rotate(degree, new Vector3f(0, 0, 1));
			break;
		case Z_Plus:
			transformMatrix.rotate((float) Math.PI / 2, new Vector3f(1, 0, 0));
			transformMatrix.rotate(degree, new Vector3f(0, 0, 1));
			break;
		}

		return transformMatrix;
	}
	
	public static float getDegreeOfTransformMatrixInDirection(Direction6T direction,
			Matrix4 transformMatrix) {
		float degree = 0;

		Vector3f unitVector = new Vector3f(1, 1, 1);

		unitVector = MatrixMath.V3RotateByTransformMatrix(unitVector,
				transformMatrix);
		switch (direction) {
		case X_Minus:
			if (unitVector.y >= 0 && unitVector.z >= 0)
				degree = (float) (Math.PI / 2 * 3);
			else if (unitVector.y < 0 && unitVector.z >= 0)
				degree = 0;
			else if (unitVector.y < 0 && unitVector.z < 0)
				degree = (float) (Math.PI / 2);
			else if (unitVector.y >= 0 && unitVector.z < 0)
				degree = (float) (Math.PI);
			break;
		case X_Plus:
			if (unitVector.y >= 0 && unitVector.z >= 0)
				degree = 0;
			else if (unitVector.y < 0 && unitVector.z >= 0)
				degree = (float) (Math.PI / 2);
			else if (unitVector.y < 0 && unitVector.z < 0)
				degree = (float) (Math.PI);
			else if (unitVector.y >= 0 && unitVector.z < 0)
				degree = (float) (Math.PI / 2 * 3);
			break;
		case Y_Minus:
			if (unitVector.x >= 0 && unitVector.z >= 0)
				degree = 0;
			else if (unitVector.x < 0 && unitVector.z >= 0)
				degree = (float) (Math.PI / 2);
			else if (unitVector.x < 0 && unitVector.z < 0)
				degree = (float) (Math.PI);
			else if (unitVector.x >= 0 && unitVector.z < 0)
				degree = (float) (Math.PI / 2 * 3);
			break;
		case Y_Plus:
			if (unitVector.x >= 0 && unitVector.z >= 0)
				degree = (float) (Math.PI / 2 * 3);
			else if (unitVector.x < 0 && unitVector.z >= 0)
				degree = (float) (Math.PI);
			else if (unitVector.x < 0 && unitVector.z < 0)
				degree = (float) (Math.PI / 2);
			else if (unitVector.x >= 0 && unitVector.z < 0)
				degree = 0;
			break;
		case Z_Minus:
			if (unitVector.y >= 0 && unitVector.x >= 0)
				degree = (float) (Math.PI / 2 * 3);
			else if (unitVector.y < 0 && unitVector.x >= 0)
				degree = 0;
			else if (unitVector.y < 0 && unitVector.x < 0)
				degree = (float) (Math.PI / 2);
			else if (unitVector.y >= 0 && unitVector.x < 0)
				degree = (float) (Math.PI);
			break;
		case Z_Plus:
			if (unitVector.y >= 0 && unitVector.x >= 0)
				degree = 0;
			else if (unitVector.y < 0 && unitVector.x >= 0)
				degree = (float) (Math.PI / 2);
			else if (unitVector.y < 0 && unitVector.x < 0)
				degree = (float) (Math.PI);
			else if (unitVector.y >= 0 && unitVector.x < 0)
				degree = (float) (Math.PI / 2 * 3);
			break;
		}

		return degree;
	}
	
	public char getValue(){
		return value;
	}
}
