package Common;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

import LDraw.Support.LDrawGlobalFlag;
import LDraw.Support.MatrixMath;

public class Vector3f implements Cloneable {
	/**
	 * @uml.property name="x"
	 */
	public float x;
	/**
	 * @uml.property name="y"
	 */
	public float y;
	/**
	 * @uml.property name="z"
	 */
	public float z;

	private int hashCode;
	private boolean isHashCodeValid = false;

	public Vector3f(float[] value) {
		this.x = value[0];
		this.y = value[1];
		this.z = value[2];
	}

	public Vector3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3f() {
		// TODO Auto-generated constructor stub
		x = 0;
		y = 0;
		z = 0;
	}

	public Vector3f(String _x, String _y, String _z) {
		if (_x == null || _x.length() == 0) {
			this.x = 0.0f;
		} else {
			this.x = Float.parseFloat(_x);
		}
		if (_y == null || _y.length() == 0) {
			this.y = 0.0f;
		} else {
			this.y = Float.parseFloat(_y);
		}
		if (_z == null || _z.length() == 0) {
			this.z = 0.0f;
		} else {
			this.z = Float.parseFloat(_z);
		}
	}

	public Vector3f(Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public void set(float _x, float _y, float _z) {
		x = _x;
		y = _y;
		z = _z;
		isHashCodeValid = false;
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
		isHashCodeValid = false;
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
		isHashCodeValid = false;
	}

	/**
	 * @return
	 * @uml.property name="z"
	 */
	public float getZ() {
		return z;
	}

	/**
	 * @param z
	 * @uml.property name="z"
	 */
	public void setZ(float z) {
		this.z = z;
		isHashCodeValid = false;
	}

	public static Vector3f getZeroVector3f() {
		// TODO Auto-generated method stub
		return new Vector3f(0, 0, 0);
	}

	public void set(Vector3f modelX) {
		this.x = modelX.getX();
		this.y = modelX.getY();
		this.z = modelX.getZ();
		isHashCodeValid = false;
	}

	public float[] getArray() {
		float values[] = new float[] { x, y, z };
		return values;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public void Read(DataInputStream in) throws IOException {
		this.x = Endian.changeByteOrder(in.readFloat());
		this.y = Endian.changeByteOrder(in.readFloat());
		this.z = Endian.changeByteOrder(in.readFloat());
		isHashCodeValid = false;
	}

	public String toString() {
		String string = new String();

		string += String.format("%.6f, ", this.x);
		string += String.format("%.6f, ", this.y);
		string += String.format("%.6f", this.z);

		return string;
	}

	public Vector3f getNormal(Vector3f v0, Vector3f v2) {
		Vector3f normal;
		Vector3f v0v1 = new Vector3f(v0.x - x, v0.y - y, v0.z - z);
		Vector3f v2v1 = new Vector3f(v2.x - x, v2.y - y, v2.z - z);
		normal = new Vector3f(v0v1.y * v2v1.z - v0v1.z * v2v1.y, v0v1.z
				* v2v1.x - v0v1.x * v2v1.z, v0v1.x * v2v1.y - v0v1.y * v2v1.x);
		normal.normalize();
		return normal;
	}

	public void normalize() {
		double length = Math.sqrt(x * x + y * y + z * z);
		if (length != 0.0) {
			this.x = (float) (this.x / length);
			this.y = (float) (this.y / length);
			this.z = (float) (this.z / length);
		} else {
			this.x = 0;
			this.y = 0;
			this.z = 0;
		}
		isHashCodeValid = false;
	}

	public final Vector3f normalizeTo(float len) {
		float mag = (float) Math.sqrt(x * x + y * y + z * z);
		if (mag > 0) {
			mag = len / mag;
			this.x *= mag;
			this.y *= mag;
			this.z *= mag;
		}
		isHashCodeValid = false;
		return this;
	}

	public final Vector3f getNormalizedTo(float len) {
		return new Vector3f(this).normalizeTo(len);
	}

	public final Vector3f getNormalized() {
		Vector3f ret = new Vector3f(this);
		ret.normalize();
		return ret;
	}

	public final Vector3f cross(Vector3f v) {
		return new Vector3f(y * v.z - v.y * z, z * v.x - v.z * x, x * v.y - v.x
				* y);
	}

	public final float magnitude() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public final float dot(Vector3f v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public final float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3f sub(Vector3f other) {
		return new Vector3f(x - other.x, y - other.y, z - other.z);
	}

	public Vector3f sub(float _x, float _y, float _z) {
		return new Vector3f(x - _x, y - _y, z - _z);
	}

	public Vector3f add(Vector3f other) {
		return new Vector3f(x + other.x, y + other.y, z + other.z);
	}

	public Vector3f add(float _x, float _y, float _z) {
		return new Vector3f(x + _x, y + _y, z + _z);
	}

	public Vector3f scale(float f) {
		return new Vector3f(x * f, y * f, z * f);
	}

	public Vector3f div(float f) {
		return new Vector3f(x / f, y / f, z / f);
	}

	public static Vector3f intersectRayPlane(Vector3f start, Vector3f dir,
			Vector3f S1, Vector3f S2, Vector3f S3) {

		Vector3f dS21 = S2.sub(S1);
		Vector3f dS31 = S3.sub(S1);
		Vector3f n = dS21.cross(dS31);

		float ndotdR = n.dot(dir);

		if (Math.abs(ndotdR) < 1e-6f) // Choose your tolerance
		{
			return null;
		}
		Vector3f R1 = start.add(dir);
		float t = -n.dot(R1.sub(S1)) / ndotdR;
		Vector3f M = R1.add(dir.scale(t));

		return M;
	}

	public static Vector3f intersectRayTriangle(Vector3f start, Vector3f dir,
			Vector3f S1, Vector3f S2, Vector3f S3) {

		Vector3f dS21 = S2.sub(S1);
		Vector3f dS31 = S3.sub(S1);
		Vector3f n = dS21.cross(dS31);

		float ndotdR = n.dot(dir);

		if (Math.abs(ndotdR) < 1e-6f) // Choose your tolerance
		{
			return null;
		}
		Vector3f R1 = start.add(dir);
		float t = -n.dot(R1.sub(S1)) / ndotdR;
		Vector3f M = R1.add(dir.scale(t));

		Vector3f dMS1 = M.sub(S1);
		float u = dMS1.dot(dS21);
		float v = dMS1.dot(dS31);
		if (u >= 0.0f && u <= dS21.dot(dS21) && v >= 0.0f
				&& v <= dS31.dot(dS31))
			return null;
		return M;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (Vector3f.class.isInstance(o) == false)
			return false;
		return MatrixMath.V3EqualPoints(this, (Vector3f) o);
	}

	@Override
	public int hashCode() {
		if (isHashCodeValid == false) {
			final String keyString = ""+Math.round(x) + "," + Math.round(y) + "," + Math
					.round(z);
			hashCode = keyString.hashCode();
			isHashCodeValid = true;
		}
		return hashCode;
	}

	public void round() {
		this.x = Math.round(this.x / LDrawGlobalFlag.DecimalPoint)
				* LDrawGlobalFlag.DecimalPoint;
		this.y = Math.round(this.y / LDrawGlobalFlag.DecimalPoint)
				* LDrawGlobalFlag.DecimalPoint;
		this.z = Math.round(this.z / LDrawGlobalFlag.DecimalPoint)
				* LDrawGlobalFlag.DecimalPoint;
		isHashCodeValid = false;
	}
}
