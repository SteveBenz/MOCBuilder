package Renderer;

public class LDrawDragHandleInstance {
	/**
	 * @uml.property  name="next"
	 * @uml.associationEnd  
	 */
	LDrawDragHandleInstance next;
	/**
	 * @uml.property  name="xyz"
	 */
	float xyz[];
	/**
	 * @uml.property  name="size"
	 */
	float size;

	public LDrawDragHandleInstance() {
		xyz = new float[3];
	}

	/**
	 * @return
	 * @uml.property  name="next"
	 */
	public LDrawDragHandleInstance getNext() {
		return next;
	}

	/**
	 * @param next
	 * @uml.property  name="next"
	 */
	public void setNext(LDrawDragHandleInstance next) {
		this.next = next;
	}

	/**
	 * @return
	 * @uml.property  name="xyz"
	 */
	public float[] getXyz() {
		return xyz;
	}

	/**
	 * @param xyz
	 * @uml.property  name="xyz"
	 */
	public void setXyz(float[] xyz) {
		this.xyz = xyz;
	}

	/**
	 * @return
	 * @uml.property  name="size"
	 */
	public float getSize() {
		return size;
	}

	/**
	 * @param size
	 * @uml.property  name="size"
	 */
	public void setSize(float size) {
		this.size = size;
	}

	
}
