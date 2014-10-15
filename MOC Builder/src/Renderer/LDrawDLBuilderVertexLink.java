package Renderer;

public class LDrawDLBuilderVertexLink {
	/**
	 * @uml.property  name="next"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderVertexLink next;
	/**
	 * @uml.property  name="vcount"
	 */
	int vcount;
	/**
	 * @uml.property  name="data"
	 */
	float data[] = new float[0];

	public LDrawDLBuilderVertexLink(int vcount, int size){
		data = new float[size*vcount];
		this.vcount = vcount;
	}
	
	/**
	 * @return
	 * @uml.property  name="next"
	 */
	public LDrawDLBuilderVertexLink getNext() {
		return next;
	}

	/**
	 * @param next
	 * @uml.property  name="next"
	 */
	public void setNext(LDrawDLBuilderVertexLink next) {
		this.next = next;
	}

	/**
	 * @return
	 * @uml.property  name="vcount"
	 */
	public int getVcount() {
		return vcount;
	}

	/**
	 * @param vcount
	 * @uml.property  name="vcount"
	 */
	public void setVcount(int vcount) {
		this.vcount = vcount;
	}

	/**
	 * @return
	 * @uml.property  name="data"
	 */
	public float[] getData() {
		return data;
	}

	/**
	 * @param data
	 * @uml.property  name="data"
	 */
	public void setData(float[] data) {
		this.data = data;
	}

}
