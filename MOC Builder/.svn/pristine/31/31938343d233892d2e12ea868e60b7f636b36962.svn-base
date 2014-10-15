package Renderer;

public class LDrawDLSortedInstanceLink implements Cloneable {

	/**
	 * @uml.property  name="next"
	 * @uml.associationEnd  
	 */
	LDrawDLSortedInstanceLink next; // DURING draw, we keep a linked list of
									// these guys off of the session as we go.
	/**
	 * @uml.property  name="eval"
	 */
	float eval; // At the end of draw, when we need to sort, we copy to a fixed
				// size array and sort.

	/**
	 * @uml.property  name="dl"
	 * @uml.associationEnd  
	 */
	LDrawDL dl;
	/**
	 * @uml.property  name="spec"
	 * @uml.associationEnd  
	 */
	LDrawTextureSpec spec;
	/**
	 * @uml.property  name="color"
	 */
	float color[];
	/**
	 * @uml.property  name="comp"
	 */
	float comp[];
	/**
	 * @uml.property  name="transform"
	 */
	float transform[];

	/**
	 * @return
	 * @uml.property  name="next"
	 */
	public LDrawDLSortedInstanceLink getNext() {
		return next;
	}

	/**
	 * @param next
	 * @uml.property  name="next"
	 */
	public void setNext(LDrawDLSortedInstanceLink next) {
		this.next = next;
	}

	/**
	 * @return
	 * @uml.property  name="eval"
	 */
	public float getEval() {
		return eval;
	}

	/**
	 * @param eval
	 * @uml.property  name="eval"
	 */
	public void setEval(float eval) {
		this.eval = eval;
	}

	/**
	 * @return
	 * @uml.property  name="dl"
	 */
	public LDrawDL getDl() {
		return dl;
	}

	/**
	 * @param dl
	 * @uml.property  name="dl"
	 */
	public void setDl(LDrawDL dl) {
		this.dl = dl;
	}

	/**
	 * @return
	 * @uml.property  name="spec"
	 */
	public LDrawTextureSpec getSpec() {
		return spec;
	}

	/**
	 * @param spec
	 * @uml.property  name="spec"
	 */
	public void setSpec(LDrawTextureSpec spec) {
		this.spec = spec;
	}

	/**
	 * @return
	 * @uml.property  name="color"
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * @param color
	 * @uml.property  name="color"
	 */
	public void setColor(float[] color) {
		this.color = color;
	}

	/**
	 * @return
	 * @uml.property  name="comp"
	 */
	public float[] getComp() {
		return comp;
	}

	/**
	 * @param comp
	 * @uml.property  name="comp"
	 */
	public void setComp(float[] comp) {
		this.comp = comp;
	}

	/**
	 * @return
	 * @uml.property  name="transform"
	 */
	public float[] getTransform() {
		return transform;
	}

	/**
	 * @param transform
	 * @uml.property  name="transform"
	 */
	public void setTransform(float[] transform) {
		this.transform = transform;
	}
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
