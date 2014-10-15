package Common;

public class Matrix3 {
	/**
	 * @uml.property  name="element" multiplicity="(0 -1)" dimension="2"
	 */
	float element[][]; // [row][column]

	public Matrix3() {
		element = new float[3][3];
	}

	public static Matrix3 getIdentityMatrix3() {
		Matrix3 newMatrix = new Matrix3();
		float[][] elements = new float[3][3];
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				if (i == j)
					elements[i][j] = 1;
				else
					elements[i][j] = 0;
		newMatrix.setElements(elements);
		return newMatrix;
	}

	public void setElements(float[][] elemenets) {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				this.element[i][j] = elemenets[i][j];
	}
	
	public float[][] getElements(){
		return this.element;
	}
}
