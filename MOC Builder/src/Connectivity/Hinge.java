package Connectivity;

import java.util.ArrayList;

import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;

public class Hinge extends Connectivity {
	float FlipLimMax;
	float FlipLimMin;
	float LimMax;
	float LimMin;
	int oriented;
	String tag;

	public float getFlipLimMax() {
		return FlipLimMax;
	}

	public void setFlipLimMax(String flipLimMax) {
		FlipLimMax = Float.parseFloat(flipLimMax);
	}

	public float getFlipLimMin() {
		return FlipLimMin;
	}

	public void setFlipLimMin(String flipLimMin) {
		FlipLimMin = Float.parseFloat(flipLimMin);
	}

	public float getLimMax() {
		return LimMax;
	}

	public void setLimMax(String limMax) {
		LimMax = Float.parseFloat(limMax);
	}

	public float getLimMin() {
		return LimMin;
	}

	public void setLimMin(String limMin) {
		LimMin = Float.parseFloat(limMin);
	}

	public int getoriented() {
		return oriented;
	}

	public void setoriented(String oriented) {
		this.oriented = Integer.parseInt(oriented);
	}

	public String gettag() {
		return tag;
	}

	public void settag(String tag) {
		this.tag = tag;
	}

	@Override
	public String toString() {
		if (tag != null) {
			return super.toString(String.format("%f %f %f %f %d %s",
					FlipLimMax, FlipLimMin, LimMax, LimMin, oriented, tag));
		} else {

			return super.toString(String.format("%f %f %f %f %d", FlipLimMax,
					FlipLimMin, LimMax, LimMin, oriented));
		}
	}

	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		setFlipLimMax(line[size + 1]);
		setFlipLimMin(line[size + 2]);
		setLimMax(line[size + 3]);
		setLimMin(line[size + 4]);
		setoriented(line[size + 5]);
		settag(line[size + 6]);
		return 0;
	}

	@Override
	public String getName() {
		return "Hinge";
	}

	@Override
	public Matrix4 getRotationMatrixForConnection(Connectivity existingConn,
			Matrix4 initialTransformMatrixOfPart) {
		Matrix4 newMatrix = new Matrix4(initialTransformMatrixOfPart);
		newMatrix.element[3][0] = newMatrix.element[3][1] = newMatrix.element[3][2] = 0;
		LDrawPart existingPart = existingConn.getParent();
		Matrix4 existingConnTransformMatrix = Matrix4.multiply(
				existingConn.getTransformMatrix(),
				existingPart.transformationMatrix());

		Matrix4 candidate = Matrix4.multiply(Matrix4.inverse(transformMatrix),
				existingConnTransformMatrix);

		Vector3f rotationVector = getRotationVector(candidate);

		Matrix4 inverseOfCandidate = new Matrix4(candidate);
		inverseOfCandidate.rotate((float) Math.toRadians(180), rotationVector);

		Vector3f directionVector_init = getDirectionVector(initialTransformMatrixOfPart);
		Vector3f directionVector_candidate = getDirectionVector(candidate);
		Vector3f directionVector_icandidate = getDirectionVector(inverseOfCandidate);

		float dirDiff = (float) Math.acos(directionVector_init
				.dot(directionVector_candidate)
				/ directionVector_candidate.length()
				/ directionVector_init.length());
		float dirDiff2 = (float) Math.acos(directionVector_init
				.dot(directionVector_icandidate)
				/ directionVector_icandidate.length()
				/ directionVector_init.length());

		if (directionVector_init.equals(directionVector_candidate))
			return newMatrix;
		else if (directionVector_init.equals(directionVector_icandidate))
			return newMatrix;
		else if (dirDiff > dirDiff2)
			candidate = inverseOfCandidate;

		ArrayList<Matrix4> candidateForRotation = new ArrayList<Matrix4>();
		candidateForRotation.add(candidate);
		for (int i = 0; i < 3; i++) {
			candidate = new Matrix4(candidate);
			candidate.rotate((float) Math.toRadians(90),
					existingConn.getDirectionVector());
			candidateForRotation.add(candidate);
		}

		dirDiff = -1;
		// System.out.println("#########################");
		for (Matrix4 matrix : candidateForRotation) {
			dirDiff2 = matrix
					.getDifferentValueForRotation(initialTransformMatrixOfPart);
			if (dirDiff < 0) {
				dirDiff = dirDiff2;
				newMatrix = matrix;
			} else if (dirDiff > dirDiff2 + 0.1f) {
				dirDiff = dirDiff2;
				newMatrix = matrix;
			}
		}
		newMatrix.element[3][0] = newMatrix.element[3][1] = newMatrix.element[3][2] = 0;

		return newMatrix;
	}

	private Vector3f getRotationVector(Matrix4 candidate) {
		Vector3f directionVector = getDirectionVector(candidate);
		// System.out.println("DirectionVector: "+directionVector);
		Vector3f rotationVector = null;
		switch (Direction6T.getDirectionOfTransformMatrix(Matrix4.multiply(
				transformMatrix, candidate))) {
		case X_Minus:
		case X_Plus:
			rotationVector = directionVector.cross(new Vector3f(0, 1, 0));
			if (rotationVector.equals(new Vector3f()))
				rotationVector = new Vector3f(0, 1, 0);
			break;
		case Y_Minus:
		case Y_Plus:
			rotationVector = directionVector.cross(new Vector3f(0, 0, 1));
			if (rotationVector.equals(new Vector3f()))
				rotationVector = new Vector3f(0, 0, 1);
			break;
		case Z_Minus:
		case Z_Plus:
			rotationVector = directionVector.cross(new Vector3f(1, 0, 0));
			if (rotationVector.equals(new Vector3f()))
				rotationVector = new Vector3f(1, 0, 0);
			break;
		}
		// System.out.println("Rotation Vector: "+rotationVector);
		return rotationVector;
	}
}
