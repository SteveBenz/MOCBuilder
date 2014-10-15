package Connectivity;

import java.util.ArrayList;

import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.LDrawGridTypeT;

public class MatrixItem implements Cloneable, IConnectivity {

	protected int altitude;
	protected int occupiedArea;
	protected int shape;
	protected int columnIndex;
	protected int rowIndex;
	protected Connectivity parent;

	protected MatrixItem connectedMatrix = null;

	protected Vector3f currentPos = new Vector3f();
	protected Vector3f cachedLocalPos = null;

	public MatrixItem() {
		altitude = occupiedArea = shape = 0;
		this.parent = null;
	}

	public MatrixItem(Connectivity parent) {
		this();
		this.parent = parent;
	}

	public void parseString(String string) {
		if (string == null)
			return;

		String tokens[] = string.split(":");

		if (tokens.length == 0)
			return;
		altitude = Integer.parseInt(tokens[0]);

		if (tokens.length == 3) {
			occupiedArea = Integer.parseInt(tokens[1]);
			shape = Integer.parseInt(tokens[2]);
		} else if (tokens.length == 2) {
			occupiedArea = Integer.parseInt(tokens[1]);
			shape = 0;
		} else {
			occupiedArea = shape = 0;
		}
	}

	public int getAltitude() {
		return altitude;
	}

	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}

	public int getOccupiedArea() {
		return occupiedArea;
	}

	public void setOccupiedArea(int occupiedArea) {
		this.occupiedArea = occupiedArea;
	}

	public int getShape() {
		return shape;
	}

	public void setShape(int shape) {
		this.shape = shape;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public Connectivity getParent() {
		return parent;
	}

	public Object clone() throws CloneNotSupportedException {
		MatrixItem a = (MatrixItem) super.clone();
		a.parent = parent;
		a.currentPos = (Vector3f) currentPos.clone();
		return a;
	}

	@Override
	public Direction6T getDirection() {
		if (parent != null)
			return parent.getDirection();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateConnectivityOrientationInfo() {
		if (parent == null)
			return;
		if (parent.parent == null)
			return;

		// update curentPos
		Vector3f newPos = getCurrentPos(parent.parent.transformationMatrix());
		currentPos.set(newPos);

	}

	@Override
	public Vector3f getCurrentPos() {
		return currentPos;
	}

	@Override
	public ConnectivityTestResultT isConnectable(
			ArrayList<IConnectivity> connectors) {
		if (connectors == null)
			return ConnectivityTestResultT.None;
		if (connectors.size() == 0)
			return ConnectivityTestResultT.None;
		if (connectors.size() == 1)
			return isConnectable(connectors.get(0));

		MatrixItem mergedMatrixItem = null;
		for (int i = 0; i < connectors.size(); i++) {
			MatrixItem item = (MatrixItem) connectors.get(i);
			if (item.getDirection() != getDirection())
				continue;
			if (mergedMatrixItem == null) {
				try {
					mergedMatrixItem = (MatrixItem) item.clone();
				} catch (CloneNotSupportedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mergedMatrixItem.setParent(item.getParent());
			} else {
				mergedMatrixItem.setOccupiedArea(mergedMatrixItem
						.getOccupiedArea() + item.getOccupiedArea());
				if (mergedMatrixItem.getOccupiedArea() > 4)
					mergedMatrixItem.setOccupiedArea(4);
			}
		}
		if (mergedMatrixItem == null)
			return ConnectivityTestResultT.None;
		return isConnectable(mergedMatrixItem);
	}

	@Override
	public ConnectivityTestResultT isConnectable(
			ArrayList<IConnectivity> connectors, Matrix4 partTransformMatrix) {
		return isConnectable(connectors);
	}

	@Override
	public ConnectivityTestResultT isConnectable(IConnectivity connector) {
		if (parent == null) {
			System.out.println("parent is null");
			return ConnectivityTestResultT.False;
		}

		if (MatrixItem.class.isInstance(connector) == false) {
			System.out.println("It is not a MatrixItem");
			return ConnectivityTestResultT.False;
		}

		MatrixItem matrixItem_Stud = null;
		MatrixItem matrixItem_Hole = null;

		if (Stud.class.isInstance(parent)) {
			// stud to stud check
			if (Stud.class.isInstance(((MatrixItem) connector).getParent()))
				return ConnectivityTestResultT.None;

			matrixItem_Stud = this;
			matrixItem_Hole = (MatrixItem) connector;
		} else {
			// hole to hole check
			if (Hole.class.isInstance(((MatrixItem) connector).getParent())) {
				// if (getOccupiedArea()
				// + ((MatrixItem) connector).getOccupiedArea() > 4)
				// return ConnectivityTestResultT.False;
				// else
				return ConnectivityTestResultT.None;
			}

			matrixItem_Stud = (MatrixItem) connector;
			matrixItem_Hole = this;
		}

		switch (matrixItem_Stud.getAltitude()) {
		case 1:
			switch (matrixItem_Hole.getAltitude()) {
			case 5:
			case 7:
			case 9:
			case 15:
			case 17:
				return ConnectivityTestResultT.True;
			default:
				return ConnectivityTestResultT.None;
			}
		case 0:
		case 2:
		case 3:
		case 9:
			switch (matrixItem_Hole.getAltitude()) {
			case 5:
			case 7:
			case 9:
			case 15:
			case 16:
			case 17:
				return ConnectivityTestResultT.True;
			default:
				return ConnectivityTestResultT.None;
			}
		case 18:
			switch (matrixItem_Hole.getAltitude()) {
			case 5:
			case 8:
				return ConnectivityTestResultT.True;

			default:
				return ConnectivityTestResultT.None;
			}
		case 23:
			return ConnectivityTestResultT.None;
		case 29:
			return ConnectivityTestResultT.None;
		}

		System.out.println("Unknown MatrixItem");
		System.out.println(matrixItem_Stud.getAltitude() + "<stud-hole>"
				+ matrixItem_Hole.getAltitude());

		return ConnectivityTestResultT.None;
	}

	@Override
	public ConnectivityTestResultT isConnectable(IConnectivity connector,
			Matrix4 partTransformMatrix) {
		ConnectivityTestResultT result = isConnectable(connector);
		if (result != ConnectivityTestResultT.True)
			return result;

		Matrix4 rotationMatrix = getTransformMatrixForConnecting(
				(MatrixItem) connector, partTransformMatrix);
		if (rotationMatrix == null) {
			return ConnectivityTestResultT.False;
		}
		if (getCurrentPos(partTransformMatrix).sub(
				getCurrentPos(rotationMatrix)).length() > 2f) {
			return ConnectivityTestResultT.False;
		}

		return result;
	}

	public void setParent(Connectivity conn_copy) {
		this.parent = conn_copy;
	}

	@Override
	public Direction6T getDirection(Matrix4 partTransformMatrix) {
		if (parent != null)
			return parent.getDirection(partTransformMatrix);

		return null;
	}

	@Override
	public Vector3f getCurrentPos(Matrix4 partTransformMatrix) {
		Vector3f newPos = cachedLocalPos;
		if (newPos == null) {
			newPos = new Vector3f(rowIndex

			* LDrawGridTypeT.Medium.getXZValue(), 0, -columnIndex
					* LDrawGridTypeT.Medium.getXZValue());
			cachedLocalPos = newPos;
		}

		newPos = getTransformMatrix().transformPoint(newPos);
		newPos = partTransformMatrix.transformPoint(newPos);
		return newPos;
	}

	public float distance(Vector3f testingPos) {
		Vector3f distance = testingPos.sub(getCurrentPos());
		return distance.dot(distance);
	}

	public Matrix4 getTransformMatrixForConnecting(MatrixItem existMatrixItem,
			Matrix4 initialTransformOfPart) {
		Connectivity existingConn = existMatrixItem.getParent();

		Matrix4 newTransform = getRotationMatrixForConnection(existingConn,
				initialTransformOfPart);
		if (newTransform != null) {
			// newTransform.translate(initialTransformOfPart.element[3][0],
			// initialTransformOfPart.element[3][1],
			// initialTransformOfPart.element[3][2]);
			Vector3f realMatchingPosOfExistingMatrixItem = existMatrixItem
					.getCurrentPos();

			Vector3f realPosOfTestingMatrixItem = getCurrentPos(newTransform);

			Vector3f newPos = realMatchingPosOfExistingMatrixItem
					.sub(realPosOfTestingMatrixItem);

			newTransform.translate(newPos.x, newPos.y, newPos.z);
		}

		return newTransform;
	}

	public Matrix4 getRotationMatrixForConnection(Connectivity existingConn,
			Matrix4 initialTransformMatrixOfPart) {
		Matrix4 newMatrix = new Matrix4(initialTransformMatrixOfPart);
		newMatrix.element[3][0] = newMatrix.element[3][1] = newMatrix.element[3][2] = 0;

		LDrawPart existingPart = existingConn.getParent();
		Matrix4 existingConnTransformMatrix = Matrix4.multiply(
				existingConn.getTransformMatrix(),
				existingPart.transformationMatrix());
		for (int i = 0; i < 3; i++)
			existingConnTransformMatrix.element[3][i] = 0;

		Matrix4 testingConnTransformMatrix = Matrix4.multiply(
				getTransformMatrix(), initialTransformMatrixOfPart);
		for (int i = 0; i < 3; i++)
			testingConnTransformMatrix.element[3][i] = 0;

		// if (getDirectionVector().equals(existingConn.getDirectionVector()) ==
		// false)
		// return null;

		Matrix4 candidate = Matrix4.multiply(
				Matrix4.inverse(getTransformMatrix()),
				existingConnTransformMatrix);

		Direction6T direction6tCandidate = Direction6T
				.getDirectionOfTransformMatrix(candidate);
		Direction6T direction6tInitial = Direction6T
				.getDirectionOfTransformMatrix(initialTransformMatrixOfPart);
		if (getDirection().getValue() == direction6tCandidate.getValue()
				|| getDirection().getValue() == direction6tInitial.getValue())
			if (direction6tCandidate.getValue() != direction6tInitial
					.getValue()) {
//				System.out.println(getDirection());
//				System.out.println(Direction6T.getDirectionOfTransformMatrix(
//						candidate).getValue());
//				System.out.println(Direction6T.getDirectionOfTransformMatrix(
//						initialTransformMatrixOfPart).getValue());
//				System.out.println("#####################");
				return null;
			}

		for (int i = 0; i < 3; i++)
			candidate.element[3][i] = 0;

		Matrix4 initMatrix = new Matrix4(initialTransformMatrixOfPart);
		for (int i = 0; i < 3; i++)
			initMatrix.element[3][i] = 0;

		// System.out.println(getCurrentPos(initMatrix));
		// System.out.println(getCurrentPos(candidate));
		// System.out.println("#####################");

		ArrayList<Matrix4> candidateForRotation = new ArrayList<Matrix4>();
		candidateForRotation.add(candidate);
		for (int i = 0; i < 3; i++) {
			candidate = new Matrix4(candidate);
			candidate.rotate((float) Math.toRadians(90),
					existingConn.getDirectionVector());
			candidateForRotation.add(candidate);
		}

		float posDiff = -1;
		float posDiff2 = -1;
		for (Matrix4 matrix : candidateForRotation) {
			posDiff2 = matrix.getDifferentValueForRotation(initMatrix);
			if (posDiff < 0) {
				posDiff = posDiff2;
				newMatrix = matrix;
			} else if (posDiff > posDiff2 + 0.2f) {
				posDiff = posDiff2;
				newMatrix = matrix;
			}
		}
		return newMatrix;
	}

	@Override
	public Vector3f getDirectionVector() {
		return getParent().getDirectionVector();
	}

	@Override
	public Vector3f getDirectionVector(Matrix4 partTransformMatrix) {
		return getParent().getDirectionVector(partTransformMatrix);
	}

	@Override
	public Matrix4 transformationMatrixOfPart() {
		return parent.transformationMatrixOfPart();
	}

	@Override
	public void moveTo(Vector3f moveByInWorld) {
		updateConnectivityOrientationInfo();
		Vector3f diff = getCurrentPos().sub(parent.getCurrentPos());
		Vector3f newMoveByInWorld = moveByInWorld.sub(diff);
		parent.moveTo(newMoveByInWorld);
	}

	@Override
	public void moveBy(Vector3f moveByInWorld) {
		updateConnectivityOrientationInfo();
		Vector3f diff = getCurrentPos().sub(parent.getCurrentPos());
		Vector3f newMoveByInWorld = moveByInWorld.sub(diff);
		parent.moveBy(newMoveByInWorld);
	}

	@Override
	public void rotateBy(float angle, Vector3f roationVector) {
		parent.rotateBy(angle, roationVector);
	}

	public Connectivity getConnectivity() {
		return parent;
	}

	public Matrix4 getTransformMatrix() {
		return parent.getTransformMatrix();
	}

	public MatrixItem getConnectedConnectivity() {
		return connectedMatrix;
	}

	public void setConnectedConnectivity(IConnectivity connectedMatrix) {
		this.connectedMatrix = (MatrixItem) connectedMatrix;
	}
}
