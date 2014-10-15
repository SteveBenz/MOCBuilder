package LDraw.Support.type;

public enum ViewOrientationT {

	ViewOrientation3D(0), ViewOrientationFront(1), ViewOrientationBack(2), ViewOrientationLeft(
			3), ViewOrientationRight(4), ViewOrientationTop(5), ViewOrientationBottom(
			6), ViewOrientationWalkThrough(7);

	/**
	 * @uml.property  name="value"
	 */
	private int value;

	private ViewOrientationT(int value) {
		this.value = value;
	}
}
