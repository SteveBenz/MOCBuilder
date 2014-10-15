package Renderer;

public enum LDrawDLT {
	dl_has_alpha(1), // At least one prim in this DL has translucency.
	dl_has_meta(2), // At least one prim in this DL uses a meta-color and thus
					// MIGHT pick up translucency from parent state during draw.
	dl_has_tex(4), // At lesat one real texture is used.
	dl_needs_destroy(8); // Destroy after drawing - ptr is only around because
							// it is queued!

	/**
	 * @uml.property  name="value"
	 */
	private int value;

	private LDrawDLT(int value) {
		this.value = value;
	}
}
