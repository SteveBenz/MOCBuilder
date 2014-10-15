package Connectivity;

import Command.LDrawPart;
import Common.Matrix4;
import LDraw.Support.type.LDrawGridTypeT;

public class Ball extends Connectivity {
	float[] flexAttributes;

	public float[] getflexAttributes() {
		return flexAttributes;
	}

	public void setflexAttributes(String flexAttributes) {
		String[] split = flexAttributes.split(",");
		this.flexAttributes = new float[split.length];
		for (int i = 0; i < split.length; i++) {
			this.flexAttributes[i] = Float.parseFloat(split[i]);
		}
	}

	@Override
	public String toString() {
		String str = null;
		if (flexAttributes != null) {
			str = String.valueOf(flexAttributes[0]);
			for (int i = 1; i < flexAttributes.length; i++) {
				str += "," + flexAttributes[i];
			}
		}
		return super.toString(str);
	}

	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		if (line.length > size + 2) {
			setflexAttributes(line[size + 1]);
		}
		return 0;
	}

	@Override
	public String getName() {
		return "Ball";
	}

	// @Override
	// public Matrix4 getTransformMatrixForSnapConnecting(
	// Connectivity existingConn, Matrix4 initialTransformOfPart) {
	// Matrix4 newTransform = getTransformMatrixForSnapDirection(existingConn);
	// if (newTransform != null) {
	// Vector3f realMatchingPosOfExistingConn = existingConn
	// .getCurrentPos();
	// Vector3f realPosOfTestingConn = getCurrentPos(newTransform);
	//
	// Vector3f posAdjust = realMatchingPosOfExistingConn
	// .sub(realPosOfTestingConn);
	// newTransform.translate(posAdjust.x, posAdjust.y, posAdjust.z);
	// }
	// return newTransform;
	// }
	@Override
	public Matrix4 getRotationMatrixForConnection(Connectivity existingConn,
			Matrix4 initialMatrixOfPart) {
		Matrix4 newTransform = new Matrix4(initialMatrixOfPart);
		newTransform.element[3][0] = newTransform.element[3][1] = newTransform.element[3][2] = 0;

		return newTransform;
	}
}
