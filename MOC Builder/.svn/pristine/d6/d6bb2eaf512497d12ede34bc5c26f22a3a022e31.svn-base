package Common;

public class Vector4f {
	/**
	 * @uml.property  name="x"
	 */
	float x;
	/**
	 * @uml.property  name="y"
	 */
	float y;
	/**
	 * @uml.property  name="z"
	 */
	float z;
	/**
	 * @uml.property  name="w"
	 */
	float w;

	public Vector4f(float[] value) {
		assert value.length != 4;

		x = value[0];
		y = value[1];
		z = value[2];
		w = value[3];

	}

	public Vector4f(int x, int y, int z, int w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f() {
		x = y = z = w = 0;
	}

	public Vector4f(float x, float y, float z, float w){
		this.x =x;
		this.y  = y;
		this.z = z;
		this.w = w;
	}

	/**
	 * @return
	 * @uml.property  name="x"
	 */
	public float getX() {
		return x;
	}

	/**
	 * @return
	 * @uml.property  name="y"
	 */
	public float getY() {
		return y;
	}

	/**
	 * @return
	 * @uml.property  name="z"
	 */
	public float getZ() {
		return z;
	}

	/**
	 * @return
	 * @uml.property  name="w"
	 */
	public float getW() {
		return w;
	}

	/**
	 * @param w
	 * @uml.property  name="w"
	 */
	public void setW(float w) {
		this.w = w;
	}

	/**
	 * @param y
	 * @uml.property  name="y"
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @param x
	 * @uml.property  name="x"
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @param z
	 * @uml.property  name="z"
	 */
	public void setZ(float z) {
		this.z = z;
	}

	public static Vector4f getZeroVector4f() {
		return new Vector4f(0, 0, 0, 0);
	}
}
