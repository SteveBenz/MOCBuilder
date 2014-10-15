package LDraw.Support.type;

public enum CacheFlagsT {
	// The bounding box of the directive has changed and is no longer valid.
			CacheFlagBounds(1), 
			DisplayList(2),
		    ContainerInvalid(4);  // Subdirectives have changed in a way that may invalidate the cache

		    /**
			 * @uml.property  name="value"
			 */
		    private int value;
			private CacheFlagsT(int value){
				this.value = value;
			}
}
