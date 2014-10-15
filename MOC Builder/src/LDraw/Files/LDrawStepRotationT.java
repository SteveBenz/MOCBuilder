package LDraw.Files;

public enum LDrawStepRotationT {
	LDrawStepRotationNone(0),	// inherit previous step rotation (or default view)
			LDrawStepRotationRelative(1),	// rotate relative to default 3D viewing angle
			LDrawStepRotationAbsolute(2),	// rotate relative to (0, 0, 0)
			LDrawStepRotationAdditive(3),	// rotate relative to the previous step's rotation
			LDrawStepRotationEnd	(4);		// cancel the effect of the previous rotation
	/**
	 * @uml.property  name="value"
	 */
	private int value;

	private LDrawStepRotationT(int value) {
		this.value = value;
	}
}
