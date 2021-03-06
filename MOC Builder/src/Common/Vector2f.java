package Common;


public class Vector2f {
	/**
	 * @uml.property name="x"
	 */
	private float x;
	/**
	 * @uml.property name="y"
	 */
	private float y;

	public Vector2f() {
		this(0, 0);
	}

	public Vector2f(Vector2f vec) {
		this(vec.x, vec.y);
	}

	public Vector2f(float x2, float y2) {
		// TODO Auto-generated constructor stub
		this.x = x2;
		this.y = y2;
	}

	/**
	 * @return
	 * @uml.property name="x"
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 * @uml.property name="x"
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return
	 * @uml.property name="y"
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y
	 * @uml.property name="y"
	 */
	public void setY(float y) {
		this.y = y;
	}

	public static Vector2f getZeroVector2f() {
		return new Vector2f(0, 0);
	}

	public void set(Vector2f v2Make) {
		this.x = v2Make.getX();
		this.y = v2Make.getY();

	}

	public String toString() {
		return x + ", " + y;
	}

	public Vector2f sub(Vector2f vec) {
		return new Vector2f(x - vec.x, y - vec.y);
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public void scale(float f) {
		x *= f;
		y *= f;
	}
}
