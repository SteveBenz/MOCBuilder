package Renderer;

public enum AttributeT {
	attr_position(0), // This defines the attribute indices for our particular
						// shader.
	attr_normal(1), // This must be kept in sync with the string list in the .m
					// file.
	attr_color(2), attr_transform_x(3), attr_transform_y(4), attr_transform_z(5), attr_transform_w(
			6), attr_color_current(7), attr_color_compliment(8), attr_texture_mix(
			9), attr_count(10);
	/**
	 * @uml.property  name="value"
	 */
	private int value;

	private AttributeT(int value) {
		this.value = value;
	}

	/**
	 * @return
	 * @uml.property  name="value"
	 */
	public int getValue() {
		return value;
	}

}