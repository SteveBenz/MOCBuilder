package Connectivity;

public class Hole extends Connectivity implements ICustom2DField, Cloneable {
	int height;
	int width;
	MatrixItem[][] matrixItem;

	public int getheight() {
		return height;
	}

	public void setheight(String height) {
		this.height = Integer.parseInt(height);
	}

	public int getwidth() {
		return width;
	}

	public void setwidth(String width) {
		this.width = Integer.parseInt(width);
	}

	public void setMatrixItem(String holeMatrixString) {
		matrixItem = new MatrixItem[height + 1][width + 1];
		String[] values = holeMatrixString.split(",");
		int count = 0;
		if (values.length == (width + 1) * (height + 1)) {
			for (int column = 0; column < height + 1; column++) {
				for (int row = 0; row < width + 1; row++) {
					MatrixItem item = new MatrixItem();
					item.parseString(values[count++]);
					matrixItem[column][row] = item;
				}
			}
		}
	}

	public MatrixItem[][] getMatrixItem() {
		return matrixItem;
	}

	@Override
	public String toString() {
		String str = height + " " + width + " ";
		for (int column = 0; column < height + 1; column++) {
			for (int row = 0; row < width + 1; row++) {
				str += matrixItem[column][row].getAltitude() + ":"
						+ matrixItem[column][row].getOccupiedArea() + ":"
						+ matrixItem[column][row].getShape() + ",";
			}
		}
		return super.toString(str);
	}

	@Override
	public int parseString(String[] line) {
		int size = super.parseString(line);
		setheight(line[size + 1]);
		setwidth(line[size + 2]);
		setMatrixItem(line[size + 3]);
		return 0;
	}

	@Override
	public String getName() {
		return "Hole";
	}

	public Object clone() throws CloneNotSupportedException {
		Hole a = (Hole) super.clone();
		a.matrixItem = new MatrixItem[matrixItem.length][matrixItem[0].length];
		for (int i = 0; i < matrixItem.length; i++) {
			for (int j = 0; j < matrixItem[i].length; j++)
				a.matrixItem[i][j] = (MatrixItem) this.matrixItem[i][j].clone();
		}
		a.parent = null;

		return a;
	}
}
