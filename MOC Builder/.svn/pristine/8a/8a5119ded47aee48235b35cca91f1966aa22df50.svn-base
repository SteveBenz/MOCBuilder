package Renderer;

public enum CullingT {	
		cull_skip(0),			// Don't draw - object is off screen or too-small-to-care.
		cull_box(1),			// Draw, but consider replacing with a box for speed - the object is rather small.
		cull_draw(2);			// Draw, the object is on screen and big.
		
		/**
		 * @uml.property  name="value"
		 */
		private int value;
		private CullingT(int value){
			this.value = value;
		}
		
		/**
		 * @return
		 * @uml.property  name="value"
		 */
		public int getValue(){
			return this.value;
		}
}
