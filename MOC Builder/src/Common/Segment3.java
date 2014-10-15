package Common;

public class Segment3 {
	/**
	 * @uml.property  name="point0"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f point0;
	/**
	 * @uml.property  name="point1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f point1;

	public Segment3(Vector3f point0, Vector3f point1) {
		this.point0 = point0;
		this.point1 = point1;
		
	}
	public Segment3() {
		this.point0 = new Vector3f();
		this.point1 = new Vector3f();
	}

	/**
	 * @return
	 * @uml.property  name="point0"
	 */
	public Vector3f getPoint0() {
		return point0;
	}

	/**
	 * @param point0
	 * @uml.property  name="point0"
	 */
	public void setPoint0(Vector3f point0) {
		this.point0 = point0;
	}

	/**
	 * @return
	 * @uml.property  name="point1"
	 */
	public Vector3f getPoint1() {
		return point1;
	}

	/**
	 * @param point1
	 * @uml.property  name="point1"
	 */
	public void setPoint1(Vector3f point1) {
		this.point1 = point1;
	}
	
	
}
