package Common;

import LDraw.Support.LDrawGlobalFlag;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.MatrixMath;

public class Matrix4 {

	/**
	 * @uml.property name="element"
	 */
	public float[][] element;

	public Matrix4() {
		initialize();
	}

	public Matrix4(float[] m) {
		initialize();
		set(m);
	}

	public Matrix4(Matrix4 matrix) {
		initialize();
		set(matrix);
	}

	public Matrix4(float m00, float m01, float m02, float m03, float m10,
			float m11, float m12, float m13, float m20, float m21, float m22,
			float m23, float m30, float m31, float m32, float m33) {
		initialize();
		set(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30,
				m31, m32, m33);
	}

	private void initialize() {
		element = new float[4][4];
		setIdentity();
	}

	/**
	 * @return
	 * @uml.property name="element"
	 */
	public float[][] getElement() {
		return this.element;
	}

	public static Matrix4 getIdentityMatrix4() {
		Matrix4 identityMarix = new Matrix4();
		float[][] element = new float[4][4];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				if (i == j)
					element[i][j] = 1;
				else
					element[i][j] = 0;
		identityMarix.setElements(element);
		return identityMarix;
	}

	public void setElements(float[][] elements_out) {
		assert elements_out == null;
		assert elements_out.length != 16;

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				this.element[i][j] = elements_out[i][j];
	}

	public void set(Matrix4 matrix) {
		for (int i = 0; i < 16; i++) {
			element[i / 4][i % 4] = matrix.element[i / 4][i % 4];
		}
	}

	public void set(float[] m) {
		for (int i = 0; i < 16; i++) {
			element[i / 4][i % 4] = m[i];
		}
	}

	public void set(float m00, float m01, float m02, float m03, float m10,
			float m11, float m12, float m13, float m20, float m21, float m22,
			float m23, float m30, float m31, float m32, float m33) {
		element[0][0] = m00;
		element[0][1] = m01;
		element[0][2] = m02;
		element[0][3] = m03;

		element[1][0] = m10;
		element[1][1] = m11;
		element[1][2] = m12;
		element[1][3] = m13;

		element[2][0] = m20;
		element[2][1] = m21;
		element[2][2] = m22;
		element[2][3] = m23;

		element[3][0] = m30;
		element[3][1] = m31;
		element[3][2] = m32;
		element[3][3] = m33;

	}

	public void setIdentity() {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				element[i][j] = i == j ? 1.0f : 0.0f;
	}

	public float[] get() {
		float[] m = new float[16];

		for (int i = 0; i < 16; i++) {
			m[i] = element[i / 4][i % 4];
		}
		return m;
	}

	public boolean equals(Object object) {
		Matrix4 matrix = (Matrix4) object;

		for (int i = 0; i < 16; i++)
			if (MatrixMath.compareFloat(element[i / 4][i % 4],
					matrix.element[i / 4][i % 4]) != 0)
				return false;
		return true;
	}

	public float getElement(int i, int j) {
		return element[i][j];
	}

	public void setElement(int i, int j, float value) {
		element[i][j] = value;
	}

	public void add(Matrix4 matrix) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				element[i][j] += matrix.element[i][j];
	}

	public static Matrix4 add(Matrix4 m1, Matrix4 m2) {
		Matrix4 m = new Matrix4(m1);
		m.add(m2);
		return m;
	}

	public void multiply(Matrix4 matrix) {
		Matrix4 product = new Matrix4();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				product.element[i][j] = 0.0f;
				for (int k = 0; k < 4; k++)
					product.element[i][j] += element[i][k]
							* matrix.element[k][j];
			}
		}
		set(product);
	}

	public static Matrix4 multiply(Matrix4 m1, Matrix4 m2) {
		Matrix4 m = new Matrix4(m1);
		m.multiply(m2);
		return m;
	}

	public Vector4f multiply(Vector4f vector4) {
		Vector4f product = new Vector4f();

		product.x = element[0][0] * vector4.x + element[0][1] * vector4.y
				+ element[0][2] * vector4.z + element[0][3] * vector4.w;
		product.y = element[1][0] * vector4.x + element[1][1] * vector4.y
				+ element[1][2] * vector4.z + element[1][3] * vector4.w;
		product.z = element[2][0] * vector4.x + element[2][1] * vector4.y
				+ element[2][2] * vector4.z + element[2][3] * vector4.w;
		product.w = element[3][0] * vector4.x + element[3][1] * vector4.y
				+ element[3][2] * vector4.z + element[3][3] * vector4.w;

		return product;
	}

	public Vector3f multiply(Vector3f vector3) {
		Vector3f product = new Vector3f();
		product.x = getElement(0, 0) * vector3.x + getElement(0, 1) * vector3.y
				+ getElement(0, 2) * vector3.z + getElement(0, 3);
		product.y = getElement(1, 0) * vector3.x + getElement(1, 1) * vector3.y
				+ getElement(1, 2) * vector3.z + getElement(1, 3);
		product.z = getElement(2, 0) * vector3.x + getElement(2, 1) * vector3.y
				+ getElement(2, 2) * vector3.z + getElement(2, 3);
		return product;
	}

	public float[] transformPoint(float[] point) {
		float[] result = new float[3];
		result[0] = point[0] * element[0][0] + point[1] * element[1][0]
				+ point[2] * element[2][0] + element[3][0];

		result[1] = point[0] * element[0][1] + point[1] * element[1][1]
				+ point[2] * element[2][1] + element[3][1];

		result[2] = point[0] * element[0][2] + point[1] * element[1][2]
				+ point[2] * element[2][2] + element[3][2];

		return result;
	}

	public Vector3f transformPoint(Vector3f point) {
		Vector3f result = new Vector3f();

		result.x = point.x * element[0][0] + point.y * element[1][0] + point.z
				* element[2][0] + element[3][0];

		result.y = point.x * element[0][1] + point.y * element[1][1] + point.z
				* element[2][1] + element[3][1];

		result.z = point.x * element[0][2] + point.y * element[1][2] + point.z
				* element[2][2] + element[3][2];

		return result;
	}

	public void transformPoints(float[] points) {
		for (int i = 0; i < points.length; i += 3) {
			float x = points[i + 0] * element[0][0] + points[i + 1]
					* element[1][0] + points[i + 2] * element[2][0]
					+ element[3][0];
			float y = points[i + 0] * element[0][1] + points[i + 1]
					* element[1][1] + points[i + 2] * element[2][1]
					+ element[3][1];
			float z = points[i + 0] * element[0][2] + points[i + 1]
					* element[1][2] + points[i + 2] * element[2][2]
					+ element[3][2];
			points[i + 0] = x;
			points[i + 1] = y;
			points[i + 2] = z;
		}
	}

	public void transformXyPoints(float[] points) {
		for (int i = 0; i < points.length; i += 2) {
			float x = points[i + 0] * element[0][0] + points[i + 1]
					* element[1][0] + element[3][0];

			float y = points[i + 0] * element[0][1] + points[i + 1]
					* element[1][1] + element[3][1];

			points[i + 0] = x;
			points[i + 1] = y;
		}
	}

	public void transformPoints(int[] points) {
		for (int i = 0; i < points.length; i += 3) {
			float x = points[i + 0] * element[0][0] + points[i + 1]
					* element[0][1] + points[i + 2] * element[0][2]
					+ element[0][3];

			float y = points[i + 0] * element[0][1] + points[i + 1]
					* element[1][1] + points[i + 2] * element[2][1]
					+ element[3][1];

			float z = points[i + 0] * element[0][2] + points[i + 1]
					* element[1][2] + points[i + 2] * element[2][2]
					+ element[3][2];

			points[i + 0] = (int) Math.round(x);
			points[i + 1] = (int) Math.round(y);
			points[i + 2] = (int) Math.round(z);
		}
	}

	public void transformXyPoints(int[] points) {
		for (int i = 0; i < points.length; i += 2) {
			float x = points[i + 0] * element[0][0] + points[i + 1]
					* element[1][0] + element[3][0];

			float y = points[i + 0] * element[0][1] + points[i + 1]
					* element[1][1] + element[3][1];

			points[i + 0] = (int) Math.round(x);
			points[i + 1] = (int) Math.round(y);
		}
	}

	public void translate(float dx, float dy, float dz) {
		Matrix4 translationMatrix = new Matrix4();

		translationMatrix.setElement(3, 0, dx);
		translationMatrix.setElement(3, 1, dy);
		translationMatrix.setElement(3, 2, dz);

		multiply(translationMatrix);
	}

	public void translate(float dx, float dy) {
		translate(dx, dy, 0.0f);
	}

	public void rotateX(float angle) {
		Matrix4 rotationMatrix = new Matrix4();

		float cosAngle = (float) Math.cos(angle);
		float sinAngle = (float) Math.sin(angle);

		if(Float.isNaN(cosAngle) || Float.isInfinite(cosAngle))cosAngle=0;
		if(Float.isNaN(sinAngle) || Float.isInfinite(sinAngle))sinAngle=1;
		
		rotationMatrix.setElement(1, 1, cosAngle);
		rotationMatrix.setElement(1, 2, sinAngle);
		rotationMatrix.setElement(2, 1, -sinAngle);
		rotationMatrix.setElement(2, 2, cosAngle);

		multiply(rotationMatrix);
	}

	public void rotateY(float angle) {
		Matrix4 rotationMatrix = new Matrix4();

		float cosAngle = (float) Math.cos(angle);
		float sinAngle = (float) Math.sin(angle);

		if(Float.isNaN(cosAngle) || Float.isInfinite(cosAngle))cosAngle=0;
		if(Float.isNaN(sinAngle) || Float.isInfinite(sinAngle))sinAngle=1;
		
		rotationMatrix.setElement(0, 0, cosAngle);
		rotationMatrix.setElement(0, 2, -sinAngle);
		rotationMatrix.setElement(2, 0, sinAngle);
		rotationMatrix.setElement(2, 2, cosAngle);

		multiply(rotationMatrix);
	}

	public void rotateZ(float angle) {
		Matrix4 rotationMatrix = new Matrix4();

		float cosAngle = (float) Math.cos(angle);
		float sinAngle = (float) Math.sin(angle);

		rotationMatrix.setElement(0, 0, cosAngle);
		rotationMatrix.setElement(0, 1, sinAngle);
		rotationMatrix.setElement(1, 0, -sinAngle);
		rotationMatrix.setElement(1, 1, cosAngle);

		multiply(rotationMatrix);
	}

	public void rotate(float angle, float[] p0, float[] p1) {
		// Represent axis of rotation by a unit vector [a,b,c]
		float a = p1[0] - p0[0];
		float b = p1[1] - p0[1];
		float c = p1[2] - p0[2];

		float length = (float) Math.sqrt(a * a + b * b + c * c);

		a /= length;
		b /= length;
		c /= length;

		float d = (float) Math.sqrt(b * b + c * c);

		// Coefficients used for step 2 matrix
		float e = d == 0.0f ? 1.0f : c / d;
		float f = d == 0.0f ? 0.0f : b / d;

		// Coefficients used for the step 3 matrix
		float k = d;
		float l = a;

		// Coefficients for the step 5 matrix (inverse of step 3)
		float m = d / (a * a + d * d);
		float n = a / (a * a + d * d);

		// Coefficients for the step 4 matrix
		float cosAngle = (float) Math.cos(angle);
		float sinAngle = (float) Math.sin(angle);

		//
		// Step 1
		//
		Matrix4 step1 = new Matrix4();
		step1.setElement(3, 0, -p0[0]);
		step1.setElement(3, 1, -p0[1]);
		step1.setElement(3, 2, -p0[2]);

		//
		// Step 2
		//
		Matrix4 step2 = new Matrix4();
		step2.setElement(1, 1, e);
		step2.setElement(1, 2, f);
		step2.setElement(2, 1, -f);
		step2.setElement(2, 2, e);

		//
		// Step 3
		//
		Matrix4 step3 = new Matrix4();
		step3.setElement(0, 0, k);
		step3.setElement(0, 2, l);
		step3.setElement(2, 0, -l);
		step3.setElement(2, 2, k);

		//
		// Step 4
		//
		Matrix4 step4 = new Matrix4();
		step4.setElement(0, 0, cosAngle);
		step4.setElement(0, 1, sinAngle);
		step4.setElement(1, 0, -sinAngle);
		step4.setElement(1, 1, cosAngle);

		//
		// Step 5 (inverse of step 3)
		//
		Matrix4 step5 = new Matrix4();
		step5.setElement(0, 0, m);
		step5.setElement(0, 2, -n);
		step5.setElement(2, 0, n);
		step5.setElement(2, 2, m);

		//
		// Step 6 (inverse of step 2)
		//
		Matrix4 step6 = new Matrix4();
		step6.setElement(1, 1, e);
		step6.setElement(1, 2, -f);
		step6.setElement(2, 1, f);
		step6.setElement(2, 2, e);

		//
		// Step 7 (inverse of step 1)
		//
		Matrix4 step7 = new Matrix4();
		step7.setElement(3, 0, p0[0]);
		step7.setElement(3, 1, p0[1]);
		step7.setElement(3, 2, p0[2]);

		multiply(step1);
		multiply(step2);
		multiply(step3);
		multiply(step4);
		multiply(step5);
		multiply(step6);
		multiply(step7);
	}

	public void rotate(float angle, Vector3f p) {
		float[] p1 = new float[] { p.x, p.y, p.z };
		float[] p2 = new float[] { 0, 0, 0 };

		rotate(angle, p1, p2);
	}

	public void scale(float xScale, float yScale, float zScale) {
		Matrix4 scalingMatrix = new Matrix4();

		scalingMatrix.setElement(0, 0, xScale);
		scalingMatrix.setElement(1, 1, yScale);
		scalingMatrix.setElement(2, 2, zScale);

		multiply(scalingMatrix);
	}

	public void scale(float xScale, float yScale, float zScale,
			float[] fixedPoint) {
		Matrix4 step1 = new Matrix4();
		step1.translate(-fixedPoint[0], -fixedPoint[1], -fixedPoint[2]);

		Matrix4 step2 = new Matrix4();
		step2.scale(xScale, yScale, zScale);

		Matrix4 step3 = new Matrix4();
		step3.translate(fixedPoint[0], fixedPoint[1], fixedPoint[2]);

		multiply(step1);
		multiply(step2);
		multiply(step3);
	}

	public void invert() {
		float[] tmp = new float[12];
		float[] src = new float[16];
		float[] dst = new float[16];

		// Transpose matrix
		for (int i = 0; i < 4; i++) {
			src[i + 0] = element[i][0];
			src[i + 4] = element[i][1];
			src[i + 8] = element[i][2];
			src[i + 12] = element[i][3];
		}

		// Calculate pairs for first 8 elements (cofactors)
		tmp[0] = src[10] * src[15];
		tmp[1] = src[11] * src[14];
		tmp[2] = src[9] * src[15];
		tmp[3] = src[11] * src[13];
		tmp[4] = src[9] * src[14];
		tmp[5] = src[10] * src[13];
		tmp[6] = src[8] * src[15];
		tmp[7] = src[11] * src[12];
		tmp[8] = src[8] * src[14];
		tmp[9] = src[10] * src[12];
		tmp[10] = src[8] * src[13];
		tmp[11] = src[9] * src[12];

		// Calculate first 8 elements (cofactors)
		dst[0] = tmp[0] * src[5] + tmp[3] * src[6] + tmp[4] * src[7];
		dst[0] -= tmp[1] * src[5] + tmp[2] * src[6] + tmp[5] * src[7];
		dst[1] = tmp[1] * src[4] + tmp[6] * src[6] + tmp[9] * src[7];
		dst[1] -= tmp[0] * src[4] + tmp[7] * src[6] + tmp[8] * src[7];
		dst[2] = tmp[2] * src[4] + tmp[7] * src[5] + tmp[10] * src[7];
		dst[2] -= tmp[3] * src[4] + tmp[6] * src[5] + tmp[11] * src[7];
		dst[3] = tmp[5] * src[4] + tmp[8] * src[5] + tmp[11] * src[6];
		dst[3] -= tmp[4] * src[4] + tmp[9] * src[5] + tmp[10] * src[6];
		dst[4] = tmp[1] * src[1] + tmp[2] * src[2] + tmp[5] * src[3];
		dst[4] -= tmp[0] * src[1] + tmp[3] * src[2] + tmp[4] * src[3];
		dst[5] = tmp[0] * src[0] + tmp[7] * src[2] + tmp[8] * src[3];
		dst[5] -= tmp[1] * src[0] + tmp[6] * src[2] + tmp[9] * src[3];
		dst[6] = tmp[3] * src[0] + tmp[6] * src[1] + tmp[11] * src[3];
		dst[6] -= tmp[2] * src[0] + tmp[7] * src[1] + tmp[10] * src[3];
		dst[7] = tmp[4] * src[0] + tmp[9] * src[1] + tmp[10] * src[2];
		dst[7] -= tmp[5] * src[0] + tmp[8] * src[1] + tmp[11] * src[2];

		// Calculate pairs for second 8 elements (cofactors)
		tmp[0] = src[2] * src[7];
		tmp[1] = src[3] * src[6];
		tmp[2] = src[1] * src[7];
		tmp[3] = src[3] * src[5];
		tmp[4] = src[1] * src[6];
		tmp[5] = src[2] * src[5];
		tmp[6] = src[0] * src[7];
		tmp[7] = src[3] * src[4];
		tmp[8] = src[0] * src[6];
		tmp[9] = src[2] * src[4];
		tmp[10] = src[0] * src[5];
		tmp[11] = src[1] * src[4];

		// Calculate second 8 elements (cofactors)
		dst[8] = tmp[0] * src[13] + tmp[3] * src[14] + tmp[4] * src[15];
		dst[8] -= tmp[1] * src[13] + tmp[2] * src[14] + tmp[5] * src[15];
		dst[9] = tmp[1] * src[12] + tmp[6] * src[14] + tmp[9] * src[15];
		dst[9] -= tmp[0] * src[12] + tmp[7] * src[14] + tmp[8] * src[15];
		dst[10] = tmp[2] * src[12] + tmp[7] * src[13] + tmp[10] * src[15];
		dst[10] -= tmp[3] * src[12] + tmp[6] * src[13] + tmp[11] * src[15];
		dst[11] = tmp[5] * src[12] + tmp[8] * src[13] + tmp[11] * src[14];
		dst[11] -= tmp[4] * src[12] + tmp[9] * src[13] + tmp[10] * src[14];
		dst[12] = tmp[2] * src[10] + tmp[5] * src[11] + tmp[1] * src[9];
		dst[12] -= tmp[4] * src[11] + tmp[0] * src[9] + tmp[3] * src[10];
		dst[13] = tmp[8] * src[11] + tmp[0] * src[8] + tmp[7] * src[10];
		dst[13] -= tmp[6] * src[10] + tmp[9] * src[11] + tmp[1] * src[8];
		dst[14] = tmp[6] * src[9] + tmp[11] * src[11] + tmp[3] * src[8];
		dst[14] -= tmp[10] * src[11] + tmp[2] * src[8] + tmp[7] * src[9];
		dst[15] = tmp[10] * src[10] + tmp[4] * src[8] + tmp[9] * src[9];
		dst[15] -= tmp[8] * src[9] + tmp[11] * src[10] + tmp[5] * src[8];

		// Calculate determinant
		float det = src[0] * dst[0] + src[1] * dst[1] + src[2] * dst[2]
				+ src[3] * dst[3];

		// Calculate matrix inverse
		det = 1.0f / det;
		for (int i = 0; i < 16; i++)
			element[i / 4][i % 4] = dst[i] * det;
	}

	public static Matrix4 inverse(Matrix4 matrix) {
		Matrix4 m = new Matrix4(matrix);

		m.invert();
		return m;
	}

	public Vector4f solve(Vector4f vector) {
		Matrix4 inverse = new Matrix4(this);
		inverse.invert();
		Vector4f result = inverse.multiply(vector);
		return result;
	}

	public void setWorld2DeviceTransform(float[] w0, float[] w1, float[] w2,
			int x0, int y0, int width, int height) {
		setIdentity();

		float[] x = new float[4];
		float[] y = new float[4];
		float[] z = new float[4];

		// Make direction vectors for new system
		x[0] = w2[0];
		y[0] = w2[1];
		z[0] = w2[2];
		x[1] = w1[0] - w0[0];
		y[1] = w1[1] - w0[1];
		z[1] = w1[2] - w0[2];
		x[2] = w0[0] - w2[0];
		y[2] = w0[1] - w2[1];
		z[2] = w0[2] - w2[2];

		x[3] = y[1] * z[2] - z[1] * y[2];
		y[3] = z[1] * x[2] - x[1] * z[2];
		z[3] = x[1] * y[2] - y[1] * x[2];

		// Normalize new z-vector, in case someone needs
		// new z-value in addition to device coordinates */
		float length = (float) Math.sqrt(x[3] * x[3] + y[3] * y[3] + z[3]
				* z[3]);
		x[3] /= length;
		y[3] /= length;
		z[3] /= length;

		// Translate back to new origin
		translate(-x[0], -y[0], -z[0]);

		// Multiply with inverse of definition of new coordinate system
		float a = y[2] * z[3] - z[2] * y[3];
		float b = z[1] * y[3] - y[1] * z[3];
		float c = y[1] * z[2] - z[1] * y[2];

		float det = x[1] * a + x[2] * b + x[3] * c;

		float[] m = new float[16];

		m[0] = a / det;
		m[1] = b / det;
		m[2] = c / det;
		m[3] = 0.0f;

		m[4] = (x[3] * z[2] - x[2] * z[3]) / det;
		m[5] = (x[1] * z[3] - x[3] * z[1]) / det;
		m[6] = (z[1] * x[2] - x[1] * z[2]) / det;
		m[7] = 0.0f;

		m[8] = (x[2] * y[3] - x[3] * y[2]) / det;
		m[9] = (y[1] * x[3] - x[1] * y[3]) / det;
		m[10] = (x[1] * y[2] - y[1] * x[2]) / det;
		m[11] = 0.0f;

		m[12] = 0.0f;
		m[13] = 0.0f;
		m[14] = 0.0f;
		m[15] = 1.0f;

		Matrix4 matrix = new Matrix4(m);
		multiply(matrix);

		// Scale according to height and width of viewport
		matrix.setIdentity();
		matrix.setElement(0, 0, width);
		matrix.setElement(1, 1, height);
		multiply(matrix);

		// Translate according to origin of viewport
		matrix.setIdentity();
		matrix.setElement(3, 0, x0);
		matrix.setElement(3, 1, y0);
		multiply(matrix);
	}

	public String toString() {
		String string = new String();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++)
				string += LDrawUtilities.outputStringForFloat(getElement(i, j))
						+ " ";
			string += '\n';
		}

		return string;
	}

	public float getDet() {
		float[] tmp = new float[12];
		float[] src = new float[16];
		float[] dst = new float[16];

		// Transpose matrix
		for (int i = 0; i < 4; i++) {
			src[i + 0] = element[i][0];
			src[i + 4] = element[i][1];
			src[i + 8] = element[i][2];
			src[i + 12] = element[i][3];
		}

		// Calculate pairs for first 8 elements (cofactors)
		tmp[0] = src[10] * src[15];
		tmp[1] = src[11] * src[14];
		tmp[2] = src[9] * src[15];
		tmp[3] = src[11] * src[13];
		tmp[4] = src[9] * src[14];
		tmp[5] = src[10] * src[13];
		tmp[6] = src[8] * src[15];
		tmp[7] = src[11] * src[12];
		tmp[8] = src[8] * src[14];
		tmp[9] = src[10] * src[12];
		tmp[10] = src[8] * src[13];
		tmp[11] = src[9] * src[12];

		// Calculate first 8 elements (cofactors)
		dst[0] = tmp[0] * src[5] + tmp[3] * src[6] + tmp[4] * src[7];
		dst[0] -= tmp[1] * src[5] + tmp[2] * src[6] + tmp[5] * src[7];
		dst[1] = tmp[1] * src[4] + tmp[6] * src[6] + tmp[9] * src[7];
		dst[1] -= tmp[0] * src[4] + tmp[7] * src[6] + tmp[8] * src[7];
		dst[2] = tmp[2] * src[4] + tmp[7] * src[5] + tmp[10] * src[7];
		dst[2] -= tmp[3] * src[4] + tmp[6] * src[5] + tmp[11] * src[7];
		dst[3] = tmp[5] * src[4] + tmp[8] * src[5] + tmp[11] * src[6];
		dst[3] -= tmp[4] * src[4] + tmp[9] * src[5] + tmp[10] * src[6];

		// Calculate determinant
		float det = src[0] * dst[0] + src[1] * dst[1] + src[2] * dst[2]
				+ src[3] * dst[3];

		return det;
	}

	public void makeLookAtMatrix(Vector3f eye, Vector3f center, Vector3f up) {
		Vector3f zaxis = new Vector3f();

		zaxis.x = eye.x - center.x;
		zaxis.y = eye.y - center.y;
		zaxis.z = eye.z - center.z;

		zaxis.normalize();
		Vector3f xaxis = up.cross(zaxis);
		xaxis.normalize();
		Vector3f yaxis = zaxis.cross(xaxis);

		setIdentity();

		element[0][0] = xaxis.x;
		element[1][0] = xaxis.y;
		element[2][0] = xaxis.z;

		element[0][1] = yaxis.x;
		element[1][1] = yaxis.y;
		element[2][1] = yaxis.z;

		element[0][2] = zaxis.x;
		element[1][2] = zaxis.y;
		element[2][2] = zaxis.z;

		element[3][0] = -xaxis.dot(eye);
		element[3][1] = -yaxis.dot(eye);
		element[3][2] = -zaxis.dot(eye);
	}

	public void makePerspectiveMatrix(float fovy, float aspect, float zNear,
			float zFar) {
		float f = 1.0f / (float) Math.tan(fovy * (Math.PI / 360.0));
		float rangeReciprocal = 1.0f / (zNear - zFar);

		element[0][0] = f / aspect;
		element[0][1] = 0.0f;
		element[0][2] = 0.0f;
		element[0][3] = 0.0f;

		element[1][0] = 0.0f;
		element[1][1] = f;
		element[1][2] = 0.0f;
		element[1][3] = 0.0f;

		element[2][0] = 0.0f;
		element[2][1] = 0.0f;
		element[2][2] = (zFar + zNear) * rangeReciprocal;
		element[2][3] = -1.0f;

		element[3][0] = 0.0f;
		element[3][1] = 0.0f;
		element[3][2] = 2.0f * zFar * zNear * rangeReciprocal;
		element[3][3] = 0.0f;

	}

	public void buildRotationMatrix(float angle, float x, float y, float z) {
		// | x^2*(1-c)+c x*y*(1-c)-z*s x*z*(1-c)+y*s 0 |
		// R = | y*x*(1-c)+z*s y^2*(1-c)+c y*z*(1-c)-x*s 0 |
		// | x*z*(1-c)-y*s y*z*(1-c)+x*s z^2*(1-c)+c 0 |
		// | 0 0 0 1 |

		float c = (float) Math.cos(angle * Math.PI / 180.0f);
		float s = (float) Math.sin(angle * Math.PI / 180.0f);

		element[0][0] = x * x * (1 - c) + c;
		element[1][0] = x * y * (1 - c) - z * s;
		element[2][0] = x * z * (1 - c) + y * s;
		element[3][0] = 0;
		element[0][1] = y * x * (1 - c) + z * s;
		element[1][1] = y * y * (1 - c) + c;
		element[2][1] = y * z * (1 - c) - x * s;
		element[3][1] = 0;
		element[0][2] = x * z * (1 - c) - y * s;
		element[1][2] = y * z * (1 - c) + x * s;
		element[2][2] = z * z * (1 - c) + c;
		element[3][2] = 0;
		element[0][3] = 0;
		element[1][3] = 0;
		element[2][3] = 0;
		element[3][3] = 1;

	}

	public void buildTranslationMatrix(float x, float y, float z) {
		element[0][0] = 1;
		element[1][0] = 0;
		element[2][0] = 0;
		element[3][0] = x;
		element[0][1] = 0;
		element[1][1] = 1;
		element[2][1] = 0;
		element[3][1] = y;
		element[0][2] = 0;
		element[1][2] = 0;
		element[2][2] = 1;
		element[3][2] = z;
		element[0][3] = 0;
		element[1][3] = 0;
		element[2][3] = 0;
		element[3][3] = 1;
	}

	public void multMatrices(final Matrix4 aMat, final Matrix4 bMat) {

		float[][] a = aMat.getElement();
		float[][] b = bMat.getElement();

		element[0][0] = b[0][0] * a[0][0] + b[0][1] * a[1][0] + b[0][2]
				* a[2][0] + b[0][3] * a[3][0];
		element[0][1] = b[0][0] * a[0][1] + b[0][1] * a[1][1] + b[0][2]
				* a[2][1] + b[0][3] * a[3][1];
		element[0][2] = b[0][0] * a[0][2] + b[0][1] * a[1][2] + b[0][2]
				* a[2][2] + b[0][3] * a[3][2];
		element[0][3] = b[0][0] * a[0][3] + b[0][1] * a[1][3] + b[0][2]
				* a[2][3] + b[0][3] * a[3][3];
		element[1][0] = b[1][0] * a[0][0] + b[1][1] * a[1][0] + b[1][2]
				* a[2][0] + b[1][3] * a[3][0];
		element[1][1] = b[1][0] * a[0][1] + b[1][1] * a[1][1] + b[1][2]
				* a[2][1] + b[1][3] * a[3][1];
		element[1][2] = b[1][0] * a[0][2] + b[1][1] * a[1][2] + b[1][2]
				* a[2][2] + b[1][3] * a[3][2];
		element[1][3] = b[1][0] * a[0][3] + b[1][1] * a[1][3] + b[1][2]
				* a[2][3] + b[1][3] * a[3][3];
		element[2][0] = b[2][0] * a[0][0] + b[2][1] * a[1][0] + b[2][2]
				* a[2][0] + b[2][3] * a[3][0];
		element[2][1] = b[2][0] * a[0][1] + b[2][1] * a[1][1] + b[2][2]
				* a[2][1] + b[2][3] * a[3][1];
		element[2][2] = b[2][0] * a[0][2] + b[2][1] * a[1][2] + b[2][2]
				* a[2][2] + b[2][3] * a[3][2];
		element[2][3] = b[2][0] * a[0][3] + b[2][1] * a[1][3] + b[2][2]
				* a[2][3] + b[2][3] * a[3][3];
		element[3][0] = b[3][0] * a[0][0] + b[3][1] * a[1][0] + b[3][2]
				* a[2][0] + b[3][3] * a[3][0];
		element[3][1] = b[3][0] * a[0][1] + b[3][1] * a[1][1] + b[3][2]
				* a[2][1] + b[3][3] * a[3][1];
		element[3][2] = b[3][0] * a[0][2] + b[3][1] * a[1][2] + b[3][2]
				* a[2][2] + b[3][3] * a[3][2];
		element[3][3] = b[3][0] * a[0][3] + b[3][1] * a[1][3] + b[3][2]
				* a[2][3] + b[3][3] * a[3][3];
	}// end multMatrices

	public void applyRotationMatrix(float angle, float x, float y, float z) {
		Matrix4 temp = new Matrix4(this);
		Matrix4 r = new Matrix4();
		r.buildRotationMatrix(angle, x, y, z);

		multMatrices(temp, r);
	}

	public boolean equalRotation(Matrix4 transformationMatrix) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (MatrixMath.compareFloat(this.element[i][j],
						transformationMatrix.element[i][j]) != 0)
					return false;

		return true;
	}

	private static final Vector3f zeroPos = new Vector3f();

	public final Vector3f getDefaultTransformPos() {
		return transformPoint(zeroPos);
	}

	public void round() {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				element[i][j] = Math.round(element[i][j]
						/ LDrawGlobalFlag.DecimalPoint)
						* LDrawGlobalFlag.DecimalPoint;
	}

	public float[] toAxisAngle() {
		float[] retValue = new float[4];
		float angle, x, y, z; // variables for result
		float epsilon = 0.01f; // margin to allow for rounding errors
		float epsilon2 = 0.1f; // margin to distinguish between 0 and 180
								// degrees
		// optional check that input is pure rotation, 'isRotationMatrix' is
		// defined at:
		// http://www.euclideanspace.com/maths/algebra/matrix/orthogonal/rotation/

		if ((Math.abs(element[0][1] - element[1][0]) < epsilon)
				&& (Math.abs(element[0][2] - element[2][0]) < epsilon)
				&& (Math.abs(element[1][2] - element[2][1]) < epsilon)) {
			// singularity found
			// first check for identity matrix which must have +1 for all terms
			// in leading diagonaland zero in other terms
			if ((Math.abs(element[0][1] + element[1][0]) < epsilon2)
					&& (Math.abs(element[0][2] + element[2][0]) < epsilon2)
					&& (Math.abs(element[1][2] + element[2][1]) < epsilon2)
					&& (Math.abs(element[0][0] + element[1][1] + element[2][2]
							- 3) < epsilon2)) {
				// this singularity is identity matrix so angle = 0
				retValue[0] = 0;
				retValue[1] = 1;
				retValue[2] = 0;
				retValue[3] = 0;
				// return new axisAngle(0,1,0,0); // zero angle, arbitrary axis
				return retValue;
			}
			// otherwise this singularity is angle = 180
			angle = (float) Math.PI;
			float xx = (element[0][0] + 1) / 2;
			float yy = (element[1][1] + 1) / 2;
			float zz = (element[2][2] + 1) / 2;
			float xy = (element[0][1] + element[1][0]) / 4;
			float xz = (element[0][2] + element[2][0]) / 4;
			float yz = (element[1][2] + element[2][1]) / 4;
			if ((xx > yy) && (xx > zz)) { // element[0][0] is the largest
											// diagonal term
				if (xx < epsilon) {
					x = 0;
					y = 0.7071f;
					z = 0.7071f;
				} else {
					x = (float) Math.sqrt(xx);
					y = xy / x;
					z = xz / x;
				}
			} else if (yy > zz) { // element[1][1] is the largest diagonal term
				if (yy < epsilon) {
					x = 0.7071f;
					y = 0;
					z = 0.7071f;
				} else {
					y = (float) Math.sqrt(yy);
					x = xy / y;
					z = yz / y;
				}
			} else { // element[2][2] is the largest diagonal term so base
						// result on this
				if (zz < epsilon) {
					x = 0.7071f;
					y = 0.7071f;
					z = 0;
				} else {
					z = (float) Math.sqrt(zz);
					x = xz / z;
					y = yz / z;
				}
			}
			retValue[0] = angle;
			retValue[1] = x;
			retValue[2] = y;
			retValue[3] = z;
			// return new axisAngle(angle,x,y,z); // return 180 deg rotation
			return retValue;
		}
		// as we have reached here there are no singularities so we can handle
		// normally
		float s = (float) Math.sqrt((element[2][1] - element[1][2])
				* (element[2][1] - element[1][2])
				+ (element[0][2] - element[2][0])
				* (element[0][2] - element[2][0])
				+ (element[1][0] - element[0][1])
				* (element[1][0] - element[0][1])); // used to normalise
		if (Math.abs(s) < 0.001)
			s = 1;
		// prevent divide by zero, should not happen if matrix is orthogonal and
		// should be
		// caught by singularity test above, but I've left it in just in case
		angle = (float) Math.acos((element[0][0] + element[1][1]
				+ element[2][2] - 1) / 2);
		x = (element[2][1] - element[1][2]) / s;
		y = (element[0][2] - element[2][0]) / s;
		z = (element[1][0] - element[0][1]) / s;

		retValue[0] = angle;
		retValue[1] = x;
		retValue[2] = y;
		retValue[3] = z;
		// return new axisAngle(angle,x,y,z); // return 180 deg rotation
		return retValue;
	}

	public float getDifferentValueForRotation(Matrix4 initialMatrix) {		
		float value = 0;
		Vector3f unitVectors[] = new Vector3f[] { new Vector3f(1, 0, 0),
				new Vector3f(0, 1, 0), new Vector3f(0, 0, 1) };
		
		for (int i = 0; i < 3; i++)
			value += MatrixMath.V3RotateByTransformMatrix(unitVectors[i], initialMatrix).sub(MatrixMath.V3RotateByTransformMatrix(unitVectors[i], this))
					.length();
		return value;
	}
}
