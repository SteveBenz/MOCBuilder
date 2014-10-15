package BrickControlGuide;

import Common.Vector3f;

public enum AxisGuideTypeT {
	None(0xff, 0, 0, 0), X_Movement(0x01, 1, 0, 0), Y_Movement(0x02, 0, -1, 0), Z_Movement(
			0x04, 0, 0, 1), X_Rotate(0x10, 1, 0, 0), Y_Rotate(0x20, 0, -1, 0), Z_Rotate(
			0x40, 0, 0, 1), Custom(0x80, 0, 0, 0);
		
	private AxisGuideTypeT(int value, float x, float y, float z) {
		this.value = value;
		this.directionVector = new Vector3f(x, y, z);
	}

	private int value = 0;
	private Vector3f directionVector;

	public Vector3f getDirectionVector() {
		return directionVector;
	}

	public int value() {
		return value;
	}
}
