package LDraw.Support.type;

public enum RotationDrawModeT {

	LDrawGLDrawNormal(0), LDrawGLDrawExtremelyFast(1);
	/**
	 * @uml.property  name="value"
	 */
	private int value;

	private RotationDrawModeT(int value) {
		this.value = value;
	}
}
