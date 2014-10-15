package Renderer;

public class Vertex {
	// These properties are intentionally ordered so that we get near-vertices
	// to sort near each other even before normal smoothing.
	/**
	 * @uml.property name="location"
	 */
	float location[]; // Actual vertex location
	/**
	 * @uml.property name="normal"
	 */
	float normal[]; // Smooth normal at this vertex - starts as face normal but
					// can be changed by smoothing.
	/**
	 * @uml.property name="color"
	 */
	float color[]; // Color for my face.

	/**
	 * @uml.property name="index"
	 */
	int index; // Index of us within our owning face.
	/**
	 * @uml.property name="face"
	 * @uml.associationEnd inverse="vertex:Renderer.Face"
	 */
	Face face; // Our owning face.

	/**
	 * @uml.property name="next"
	 * @uml.associationEnd inverse="prev:Renderer.Vertex"
	 */
	Vertex next; // For snapping: when we are snapping vertices, we build them
					// into a doubly-linked list. These point to the other
					// vertices
	/**
	 * @uml.property name="prev"
	 * @uml.associationEnd inverse="next:Renderer.Vertex"
	 */
	Vertex prev; // in our snap list (or is null if we are not snapped with
					// anyone.)

	public Vertex() {
		location = new float[3];
		normal = new float[3];
		color = new float[4];
	}

	public static Vertex getNthVertex(Vertex begin, int n) {
		int count = 0;
		if (n == 0)
			return begin;

		while (begin != null && count < n) {
			count++;
			begin = begin.getNext();
			if (count == n)
				return begin;
		}

		return null;
	}

	/**
	 * @return
	 * @uml.property name="location"
	 */
	public float[] getLocation() {
		// TODO Auto-generated method stub
		return location;
	}

	/**
	 * @return
	 * @uml.property name="normal"
	 */
	public float[] getNormal() {
		return normal;
	}

	/**
	 * @return
	 * @uml.property name="color"
	 */
	public float[] getColor() {
		return color;
	}
	/**
	 * @param vertex
	 * @uml.property name="next"
	 */
	public void setNext(Vertex vertex) {
		this.next = vertex;
	}

	/**
	 * @param prev
	 * @uml.property name="prev"
	 */
	public void setPrev(Vertex prev) {
		this.prev = prev;
	}

	/**
	 * @return
	 * @uml.property name="next"
	 */
	public Vertex getNext() {
		return next;
	}

	/**
	 * @return
	 * @uml.property name="prev"
	 */
	public Vertex getPrev() {
		return prev;
	}

	/**
	 * @return
	 * @uml.property name="index"
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index
	 * @uml.property name="index"
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return
	 * @uml.property name="face"
	 */
	public Face getFace() {
		return face;
	}

	/**
	 * @param face
	 * @uml.property name="face"
	 */
	public void setFace(Face face) {
		this.face = face;
	}

	/**
	 * @param location
	 * @uml.property name="location"
	 */
	public void setLocation(float[] location) {
		this.location = location;
	}

	/**
	 * @param normal
	 * @uml.property name="normal"
	 */
	public void setNormal(float[] normal) {
		this.normal = normal;
	}

	/**
	 * @param color
	 * @uml.property name="color"
	 */
	public void setColor(float[] color) {
		this.color = color;
	}

}
