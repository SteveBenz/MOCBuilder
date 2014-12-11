package Connectivity;

import Common.Matrix4;
import Common.Vector3f;
import LDraw.Support.MatrixMath;

public class Fixed extends Connectivity {
	int axes;
	String tag;

	public void apply(Fixed newConn) {
		this.type = newConn.gettype();
	}

	public int getaxes() {
		return axes;
	}

	public void setaxes(String axes) {
		this.axes = Integer.parseInt(axes);
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
			return super.toString(axes + " " + tag);
		} else {
			return super.toString(String.valueOf(axes));
		}
	}

	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		setaxes(line[size + 1]);
		settag(line[size + 2]);
		return 0;
	}

	@Override
	public String getName() {
		return "Fixed";
	}

	@Override
	public Matrix4 getRotationMatrixForConnection(Connectivity existingConn,
			Matrix4 initialTransformMatrixOfPart) {
		Matrix4 newMatrix = new Matrix4(initialTransformMatrixOfPart);
		newMatrix.element[3][0] = newMatrix.element[3][1] = newMatrix.element[3][2] = 0;

		Vector3f directionVectorE = existingConn.getDirectionVector();

		Vector3f directionVectorT = getDirectionVector(initialTransformMatrixOfPart);

		Vector3f rotationVector = MatrixMath.V3Cross(directionVectorE,
				directionVectorT);
		if (MatrixMath.compareFloat(rotationVector.length(), 0) != 0) {
			rotationVector = rotationVector.scale(1 / rotationVector.length());

			float angle = (float) Math.acos(directionVectorE
					.dot(directionVectorT)
					/ (directionVectorE.length() * directionVectorT.length()));

			// System.out.println("first E: "+directionVectorE+", T: "+directionVectorT);
			// System.out.println(rotationVector + ", " + angle / (Math.PI * 2)
			// *
			// 360+"("+angle+")");

			if (rotationVector.length() > 0.5f
					&& MatrixMath.compareFloat(angle, (float) Math.PI / 2) > 0)
				return null;

			if (rotationVector.length() > 0.1f && Float.isNaN(angle) == false
					&& MatrixMath.compareFloat(angle, 0) != 0) {
				newMatrix.rotate(angle, rotationVector);
			}
		}
		return newMatrix;
	}

}
