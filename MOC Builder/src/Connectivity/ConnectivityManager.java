package Connectivity;

import java.util.ArrayList;
import java.util.HashMap;

import Command.LDrawPart;
import Common.Vector3f;
import LDraw.Support.type.LDrawGridTypeT;

public class ConnectivityManager {

	private ArrayList<MatrixItem> connectivityMatrixItemList = new ArrayList<MatrixItem>();
	private ArrayList<Connectivity> connectivityList = new ArrayList<Connectivity>();
	private HashMap<Vector3f, ArrayList<MatrixItem>> studCoordinateMap = new HashMap<Vector3f, ArrayList<MatrixItem>>();
	private HashMap<Vector3f, ArrayList<MatrixItem>> holeCoordinateMap = new HashMap<Vector3f, ArrayList<MatrixItem>>();

	public void addPart(LDrawPart part) {
		ArrayList<Connectivity> connList = part
				.getConnectivityList(false, true);
		if (connList == null)
			return;
		for (Connectivity conn : connList) {
			connectivityList.add(conn);
			if (ICustom2DField.class.isInstance(conn)) {
				ICustom2DField custom2DField = (ICustom2DField) conn;
				MatrixItem[][] matrix = custom2DField.getMatrixItem();
				for (int column = 0; column < matrix.length; column++)
					for (int row = 0; row < matrix[column].length; row++) {
						matrix[column][row].updateConnectivityOrientationInfo();
						if (optimizeGroupConnectivity(matrix[column][row]))
							connectivityMatrixItemList.add(matrix[column][row]);
					}
			}
		}
		// System.out.println(connectivityMatrixItemList.size());
	}

	public void addConn(Connectivity conn) {
		connectivityList.add(conn);
	}

	public void addMatrixItem(MatrixItem matrixItem) {
		if (optimizeGroupConnectivity(matrixItem)){
			connectivityMatrixItemList.add(matrixItem);
		}
	}

	private boolean optimizeGroupConnectivity(MatrixItem item) {
		Vector3f finePos = LDrawGridTypeT.getSnappedPos(item.getCurrentPos(),
				LDrawGridTypeT.Fine);
		boolean retValue = true;
		
		if(item.getAltitude()==29)return false;

		if (item.getParent() instanceof Hole) {
			if (holeCoordinateMap.containsKey(finePos) == false)
				holeCoordinateMap.put(finePos, new ArrayList<MatrixItem>());
			else {
				ArrayList<MatrixItem> victim = new ArrayList<MatrixItem>();
				for (MatrixItem eItem : holeCoordinateMap.get(finePos)) {
					if (eItem.getDirectionVector().hashCode() == item
							.getDirectionVector().hashCode()) {
						connectivityMatrixItemList.remove(eItem);
						victim.add(eItem);
						retValue = false;
					}
				}
				for (MatrixItem eItem : victim)
					holeCoordinateMap.remove(eItem);
			}
			if (retValue == false)
				return retValue;
			if (studCoordinateMap.containsKey(finePos)) {
				ArrayList<MatrixItem> victim = new ArrayList<MatrixItem>();
				for (MatrixItem eItem : studCoordinateMap.get(finePos)) {
					if (eItem.getDirectionVector().hashCode() == item
							.getDirectionVector().hashCode()) {
						connectivityMatrixItemList.remove(eItem);
						victim.add(eItem);
						retValue = false;
					}
				}
				for (MatrixItem eItem : victim)
					studCoordinateMap.remove(eItem);
			}

			if (retValue)
				holeCoordinateMap.get(finePos).add(item);
		} else {
			if (studCoordinateMap.containsKey(finePos) == false)
				studCoordinateMap.put(finePos, new ArrayList<MatrixItem>());
			else {
				ArrayList<MatrixItem> victim = new ArrayList<MatrixItem>();
				for (MatrixItem eItem : studCoordinateMap.get(finePos)) {
					if (eItem.getDirectionVector().hashCode() == item
							.getDirectionVector().hashCode()) {
						connectivityMatrixItemList.remove(eItem);
						victim.add(eItem);
						retValue = false;
					}
				}
				for (MatrixItem eItem : victim)
					studCoordinateMap.remove(eItem);
			}
			if (retValue == false)
				return retValue;

			if (holeCoordinateMap.containsKey(finePos)) {
				ArrayList<MatrixItem> victim = new ArrayList<MatrixItem>();
				for (MatrixItem eItem : holeCoordinateMap.get(finePos)) {
					if (eItem.getDirectionVector().hashCode() == item
							.getDirectionVector().hashCode()) {
						connectivityMatrixItemList.remove(eItem);
						victim.add(eItem);
						retValue = false;
					}
				}
				for (MatrixItem eItem : victim)
					holeCoordinateMap.remove(eItem);

			}
			if (retValue)
				studCoordinateMap.get(finePos).add(item);
		}
		return retValue;
	}

	public void clear() {
		holeCoordinateMap.clear();
		studCoordinateMap.clear();
		connectivityList.clear();
		connectivityMatrixItemList.clear();
	}

	public ArrayList<MatrixItem> getConnectivityMatrixItemList() {
		return new ArrayList<MatrixItem>(connectivityMatrixItemList);
	}

	public ArrayList<Connectivity> getConnectivityList() {
		return new ArrayList<Connectivity>(connectivityList);
	}

	public void removePart(LDrawPart part) {
		ArrayList<Connectivity> connList = part
				.getConnectivityList(false, true);
		if (connList == null)
			return;
		for (Connectivity conn : connList) {
			connectivityList.remove(conn);
			if (ICustom2DField.class.isInstance(conn)) {
				ICustom2DField custom2DField = (ICustom2DField) conn;
				MatrixItem[][] matrix = custom2DField.getMatrixItem();
				for (int column = 0; column < matrix.length; column++)
					for (int row = 0; row < matrix[column].length; row++)
						connectivityMatrixItemList.remove(matrix[column][row]);
			}
		}
	}
}
