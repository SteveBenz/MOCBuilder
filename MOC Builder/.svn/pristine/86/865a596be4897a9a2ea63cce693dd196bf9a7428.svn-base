package Common;

public class Ray3 {
	/**
	 * @uml.property name="origin"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	Vector3f origin;
	/**
	 * @uml.property name="direction"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	Vector3f direction;

	public Ray3() {
		this.origin = new Vector3f();
		this.direction = new Vector3f();
	}

	/**
	 * @return
	 * @uml.property name="origin"
	 */
	public Vector3f getOrigin() {
		return origin;
	}

	/**
	 * @param origin
	 * @uml.property name="origin"
	 */
	public void setOrigin(Vector3f origin) {
		this.origin = origin;
	}

	/**
	 * @return
	 * @uml.property name="direction"
	 */
	public Vector3f getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 * @uml.property name="direction"
	 */
	public void setDirection(Vector3f direction) {
		this.direction = direction;
	}

	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj instanceof Ray3 == false)
			return false;

		Ray3 other = (Ray3) obj;
		//Don't use MatrixMath.compareFloat. It does not provide enough precision. 
		if (other.origin.sub(origin).length() <0.001f && other.direction.sub(direction).length() < 0.001f)
			return true;
		return false;
	}

}
