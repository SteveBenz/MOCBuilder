package Renderer;

public class VertexInsert {
	/**
	 * @uml.property  name="next"
	 * @uml.associationEnd  readOnly="true"
	 */
	VertexInsert next;
	/**
	 * @uml.property  name="dist"
	 */
	float					dist;		// Distance along the edge of this insert.
	/**
	 * @uml.property  name="vert"
	 * @uml.associationEnd  readOnly="true"
	 */
	Vertex				vert;		// Pointer to vertex from another triangle that is a T with our edge.
	
	public VertexInsert(){		
	}
}
