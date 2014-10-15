package LDraw.Support.type;

public enum LocationModeT {
	LocationModeModel(0), LocationModeWalkthrough(1);
	/**
	 * @uml.property  name="value"
	 */
	private int value;

	private LocationModeT(int value) {
		this.value = value;
	}

}
