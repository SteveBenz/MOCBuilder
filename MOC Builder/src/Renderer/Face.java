package Renderer;

//A single face in our mesh.
public class Face {
	public static final Integer UNKNOWN_FACE = null;
	/**
	 * @uml.property name="degree"
	 */
	int degree; // Number of vertices - this defines whether we are a line, tri
				// or quad.
	// Set to 0 after export to null out the face.
	/**
	 * @uml.property name="vertex"
	 * @uml.associationEnd multiplicity="(0 -1)" inverse="face:Renderer.Vertex"
	 */
	Vertex[] vertex; // Vertices - 0,1,2 is CCW traversal
	/**
	 * @uml.property name="neighbor"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	Face[] neighbor; // Neighbors - numbered by SOURCE vertex, or NULL if no
						// smooth neighbor or -1L if not yet determined.
	/**
	 * @uml.property name="index"
	 */
	int index[]; // Index of our neighbor edge's source in neighbor, if we have
					// one.
	/**
	 * @uml.property name="flip"
	 */
	int flip[]; // Indicates that our neighbor is winding-flipped from us.

	/**
	 * @uml.property name="t_list"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	VertexInsert t_list[]; // For T junctions, a list of vertices that form Ts
							// with the edge starting with vertex N.

	/**
	 * @uml.property name="normal"
	 */
	float normal[]; // Whole-face properties: calculated normal
	/**
	 * @uml.property name="color"
	 */
	float color[]; // RGBA color passed in,
	/**
	 * @uml.property name="tid"
	 */
	int tid;

	public Face() {
		vertex = new Vertex[4];
		neighbor = new Face[4];
		index = new int[4];
		flip = new int[4];

		t_list = new VertexInsert[4];

		normal = new float[3];
		color = new float[4];

		for (int i = 0; i < 4; i++)
			vertex[i] = new Vertex();
	}

	/**
	 * @return
	 * @uml.property name="degree"
	 */
	public int getDegree() {
		return this.degree;
	}

	/**
	 * @return
	 * @uml.property name="vertex"
	 */
	public Vertex[] getVertex() {
		return vertex;
	}

	/**
	 * @param vertex
	 * @uml.property name="vertex"
	 */
	public void setVertex(Vertex[] vertex) {
		this.vertex = vertex;
	}

	/**
	 * @return
	 * @uml.property name="neighbor"
	 */
	public Face[] getNeighbor() {
		return neighbor;
	}

	/**
	 * @param neighbor
	 * @uml.property name="neighbor"
	 */
	public void setNeighbor(Face[] neighbor) {
		this.neighbor = neighbor;
	}

	/**
	 * @return
	 * @uml.property name="index"
	 */
	public int[] getIndex() {
		return index;
	}

	/**
	 * @param index
	 * @uml.property name="index"
	 */
	public void setIndex(int[] index) {
		this.index = index;
	}

	/**
	 * @return
	 * @uml.property name="flip"
	 */
	public int[] getFlip() {
		return flip;
	}

	/**
	 * @param flip
	 * @uml.property name="flip"
	 */
	public void setFlip(int[] flip) {
		this.flip = flip;
	}

	/**
	 * @return
	 * @uml.property name="t_list"
	 */
	public VertexInsert[] getT_list() {
		return t_list;
	}

	/**
	 * @param t_list
	 * @uml.property name="t_list"
	 */
	public void setT_list(VertexInsert[] t_list) {
		this.t_list = t_list;
	}

	/**
	 * @return
	 * @uml.property name="normal"
	 */
	public float[] getNormal() {
		return normal;
	}

	/**
	 * @param normal
	 * @uml.property name="normal"
	 */
	public void setNormal(float[] normal) {
		this.normal = normal;
	}

	/**
	 * @return
	 * @uml.property name="color"
	 */
	public float[] getColor() {
		return color;
	}

	/**
	 * @param color
	 * @uml.property name="color"
	 */
	public void setColor(float[] color) {
		this.color = color;
	}

	/**
	 * @return
	 * @uml.property name="tid"
	 */
	public int getTid() {
		return tid;
	}

	/**
	 * @param tid
	 * @uml.property name="tid"
	 */
	public void setTid(int tid) {
		this.tid = tid;
	}

	public static Integer getUnknownFace() {
		return UNKNOWN_FACE;
	}

	/**
	 * @param degree
	 * @uml.property name="degree"
	 */
	public void setDegree(int degree) {
		this.degree = degree;
	}

	public void setVertexAtIndex(int i, Vertex vertex) {
		this.vertex[i] = vertex;
	}

}
