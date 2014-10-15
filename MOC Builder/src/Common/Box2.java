package Common;

public class Box2 {
	/**
	 * @uml.property name="size"
	 * @uml.associationEnd
	 */
	public Size2 size;
	/**
	 * @uml.property name="origin"
	 * @uml.associationEnd
	 */
	public Vector2f origin;

	public Box2(float[][] value) {
		origin = new Vector2f(value[0][0], value[0][1]);
		size = new Size2(value[1][0], value[1][1]);
	}
	
	public Box2() {
		size = new Size2();
		origin = new Vector2f();
	}

	public static Box2 getZeroBox2() {
		return new Box2(new float[][] { { 0.0f, 0.0f }, { 0.0f, 0.0f } });
	}

	/**
	 * @return
	 * @uml.property name="origin"
	 */
	public Vector2f getOrigin() {
		return this.origin;
	}

	public void setOrigin(Vector2f origin) {
		this.origin = origin;
	}

	/**
	 * @return
	 * @uml.property name="size"
	 */
	public Size2 getSize() {
		return this.size;
	}

	public void setSize(Size2 size) {
		this.size = size;
	}
	
	public String toString(){
		return origin.toString() + ": "+size.toString();
	}
}
