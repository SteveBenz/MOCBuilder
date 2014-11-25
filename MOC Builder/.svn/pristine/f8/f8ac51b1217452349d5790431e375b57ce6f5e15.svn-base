package Builder;

import java.util.ArrayList;
import java.util.HashMap;

import BrickControlGuide.BrickMovementGuideRenderer;
import Command.LDrawLSynth;
import Command.LDrawPart;
import Common.Box2;
import Common.Box3;
import Common.Matrix4;
import Common.Size2;
import Common.Vector2f;
import Common.Vector3f;
import Connectivity.Connectivity;
import Connectivity.ConnectivityManager;
import Connectivity.ConnectivityTestResultT;
import Connectivity.Direction6T;
import Connectivity.GlobalConnectivityManager;
import Connectivity.Hole;
import Connectivity.ICustom2DField;
import Connectivity.MatrixItem;
import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawStep;
import LDraw.Support.LDrawDirective;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.LDrawGridTypeT;
import Notification.LDrawDirectiveSelected;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Window.MOCBuilder;

public class DirectiveSelectionManager {
	private static DirectiveSelectionManager _instance = null;

	public synchronized static DirectiveSelectionManager getInstance() {
		if (_instance == null)
			_instance = new DirectiveSelectionManager();
		return _instance;
	}

	private DirectiveGroupForTransform selectedDirectives = null;
	private HashMap<LDrawDirective, Matrix4> startTransformMatrixMap = null;

	private HashMap<LDrawDirective, Matrix4> initialTransformMatrixMap = null;
	private ArrayList<LDrawDirective> directiveList;

	private HashMap<LDrawDirective, ArrayList<Vector2f>> projectedLDrawDirectiveVerticesMap;

	private ConnectivityManager connectivityManager = new ConnectivityManager();

	private LDrawLSynth lastSelectedLSynth = null;

	private DirectiveSelectionManager() {
		selectedDirectives = new DirectiveGroupForTransform();
		startTransformMatrixMap = new HashMap<LDrawDirective, Matrix4>();
		initialTransformMatrixMap = new HashMap<LDrawDirective, Matrix4>();
		directiveList = new ArrayList<LDrawDirective>();
		projectedLDrawDirectiveVerticesMap = new HashMap<LDrawDirective, ArrayList<Vector2f>>();
	}

	public void addDirective(LDrawDirective directive,
			boolean updateProjectionMap) {

		synchronized (directiveList) {
			if (directiveList.contains(directive))
				return;

			directiveList.add(directive);
		}

		if (directive instanceof LDrawLSynth) {
			for (LDrawDirective constraint : ((LDrawLSynth) directive)
					.subdirectives())
				addDirective(constraint, updateProjectionMap);
			lastSelectedLSynth = (LDrawLSynth) directive;
		} else if (directive instanceof LDrawStep) {
			for (LDrawDirective subDirective : ((LDrawStep) directive)
					.subdirectives())
				addDirective(subDirective, updateProjectionMap);
		} else if (directive instanceof LDrawPart) {
			if (updateProjectionMap)
				updateScreenProjectionVerticesMap(directive);
		}
	}

	public void addDirectiveToSelection(LDrawDirective directive) {
//		synchronized (directiveList) {
//			if (directiveList.contains(directive) == false)
//				return;
//		}

		if (selectedDirectives.contains(directive) == false) {
			if (directive instanceof LDrawPart) {

				((LDrawPart) directive).isDraggingPart(true);
				startTransformMatrixMap.put(directive,
						directive.transformationMatrix());
				initialTransformMatrixMap.put(directive,
						directive.transformationMatrix());
			}else if (directive instanceof LDrawLSynth) {
				lastSelectedLSynth = (LDrawLSynth) directive;
			}
			directive.setSelected(true);
			selectedDirectives.addDirective(directive);
			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.LDrawDirectiveDidSelected,
					new LDrawDirectiveSelected(directive));
		}

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedRedraw);
	}

	public void clearAllDirective(boolean updateConnectivity) {
		synchronized (directiveList) {
			directiveList.clear();
		}

		selectedDirectives.clear();

		synchronized (projectedLDrawDirectiveVerticesMap) {
			projectedLDrawDirectiveVerticesMap.clear();
		}

		if (updateConnectivity) {
			GlobalConnectivityManager.getInstance().updateMatrixAll();
			updateScreenProjectionVerticesMapAll();
		}
	}

	public void clearSelection() {
		clearSelection(true);
	}

	public void clearSelection(boolean updateConnectivityMatrix) {
		// System.out.println("Clear BrickSelectionManager: "
		// + updateConnectivityMatrix);
		if (isEmpty())
			return;
		// ArrayList<LDrawPart> copy = getSelectedPartList();

		for (LDrawDirective directive : selectedDirectives.getDirectiveList()) {
			directive.setSelected(false);
			if (directive instanceof LDrawPart) {
				((LDrawPart) directive).isDraggingPart(false);
				GlobalConnectivityManager.getInstance().updateMatrix(
						(LDrawPart) directive);
			}
		}
		selectedDirectives.clear();

		BrickMovementGuideRenderer.getInstance().setLDrawPart(null);
		BrickMovementGuideRenderer.getInstance().clear();
		updateScreenProjectionVerticesMapAll();
		connectivityManager.clear();

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidSelected);
	}

	public boolean containsInSelection(LDrawDirective directive) {
		return selectedDirectives.contains(directive);
	}

	public Matrix4 getInitialMoveTransformMatrix(LDrawDirective directive) {
		if (initialTransformMatrixMap.containsKey(directive))
			return initialTransformMatrixMap.get(directive);
		return directive.transformationMatrix();
	}

	public int getNumOfSelectedDirectives() {
		return selectedDirectives.size();
	}

	public ArrayList<LDrawDirective> getSelectedDirectiveList() {
		ArrayList<LDrawDirective> copy = new ArrayList<LDrawDirective>(
				selectedDirectives.getDirectiveList());
		return copy;
	}

	public Vector3f getSelectedDirectiveCenter() {
		Vector3f retCenter = new Vector3f(0, 0, 0);

		for (LDrawDirective directive : selectedDirectives.getDirectiveList()) {
			if (directive instanceof LDrawPart == false)
				continue;

			Vector3f pos = new Vector3f(((LDrawPart) directive).position());
			retCenter = retCenter.add(pos);
		}
		retCenter = retCenter.div((float) selectedDirectives.size());

		return retCenter;
	}

	public Matrix4 getStartMoveTransformMatrix(LDrawDirective dirctive) {
		if (startTransformMatrixMap.containsKey(dirctive))
			return startTransformMatrixMap.get(dirctive);
		return dirctive.transformationMatrix();
	}

	public boolean isAllSelectedPartConnectible() {
		boolean isAllConnectible = true;
		if (BuilderConfigurationManager.getInstance().isUseConnectivity() == false)
			return isAllConnectible;

		for (LDrawDirective directive : selectedDirectives.getDirectiveList()) {
			if (LDrawPart.class.isInstance(directive) == false)
				continue;

			if (GlobalConnectivityManager.getInstance()
					.isConnectable((LDrawPart) directive).getResultType() == ConnectivityTestResultT.False) {
				isAllConnectible = false;
				break;
			}
		}
		return isAllConnectible;
	}

	public boolean isEmpty() {
		return selectedDirectives.isEmpty();
	}

	public boolean isTheOnlySelectedDirective(LDrawDirective directive) {
		boolean isTrue = false;
		if (selectedDirectives.size() == 1
				&& selectedDirectives.contains(directive))
			isTrue = true;

		return isTrue;
	}

	public void moveSelectedDirectiveBy(LDrawDirective pointingDirective) {
		selectedDirectives.applyTransform(pointingDirective,
				pointingDirective.transformationMatrix());
	}

	public void removeDirective(LDrawDirective directive) {
		if (directive instanceof LDrawPart) {
			((LDrawPart) directive).isDraggingPart(false);
			connectivityManager.removePart((LDrawPart) directive);
		}

		if (directive instanceof LDrawLSynth) {
			for (LDrawDirective constraint : ((LDrawLSynth) directive)
					.subdirectives())
				removeDirective(constraint);
		} else if (directive instanceof LDrawStep) {
			for (LDrawDirective subDirective : ((LDrawStep) directive)
					.subdirectives())
				removeDirective(subDirective);
		}
		directive.setSelected(false);
		selectedDirectives.removeDirective(directive);
		synchronized (directiveList) {
			directiveList.remove(directive);
		}
	}

	public void removeDirectiveFromSelection(LDrawDirective directive) {
		if (selectedDirectives.contains(directive) == false)
			return;

		if (directive instanceof LDrawPart) {
			((LDrawPart) directive).isDraggingPart(false);
			connectivityManager.removePart((LDrawPart) directive);
			GlobalConnectivityManager.getInstance().updateMatrix(
					(LDrawPart) directive);
		}

		if (directive instanceof LDrawLSynth)
			for (LDrawDirective constraint : ((LDrawLSynth) directive)
					.subdirectives())
				removeDirectiveFromSelection(constraint);
		else if (directive instanceof LDrawStep) {
			for (LDrawDirective subDirective : ((LDrawStep) directive)
					.subdirectives())
				removeDirectiveFromSelection(subDirective);
		}

		selectedDirectives.removeDirective(directive);
		directive.setSelected(false);
		
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidSelected);
	}

	public void selectByDragging(Box2 bounds) {
		// long nano = System.nanoTime();
		Vector2f origin = bounds.origin;
		Size2 size = bounds.size;
		float max_x, max_y;
		max_x = origin.getX() + size.getWidth();
		max_y = origin.getY() + size.getHeight();

		for (LDrawDirective directive : directiveList) {
			if (directive instanceof LDrawPart == false)
				continue;

			LDrawPart part = (LDrawPart) directive;
			if (part.isPartDataExist() == false)
				continue;
			ArrayList<Vector2f> projectedVertices = projectedLDrawDirectiveVerticesMap
					.get(part);

			if (projectedVertices == null)
				continue;

			boolean isAllSmallerThanBoundsX = true;
			boolean isAllSmallerThanBoundsY = true;
			boolean isAllLargerThanBoundsX = true;
			boolean isAllLargerThanBoundsY = true;
			for (Vector2f pos : projectedVertices) {
				if (pos.getX() > origin.getX())
					isAllSmallerThanBoundsX = false;
				if (pos.getY() > origin.getY())
					isAllSmallerThanBoundsY = false;
				if (pos.getX() < max_x)
					isAllLargerThanBoundsX = false;
				if (pos.getY() < max_y)
					isAllLargerThanBoundsY = false;
			}
			if (isAllLargerThanBoundsX || isAllLargerThanBoundsY
					|| isAllSmallerThanBoundsX || isAllSmallerThanBoundsY) {
				if (part.isSelected())
					removeDirectiveFromSelection(part);
				continue;
			}

			Vector2f[] poly = new Vector2f[3];
			boolean isIntersected = false;
			for (int i = 0; i < 6; i++) {
				for (int j = i + 1; j < 7; j++) {
					for (int k = j + 1; k < 8; k++) {
						poly[0] = projectedVertices.get(i);
						poly[1] = projectedVertices.get(j);
						poly[2] = projectedVertices.get(k);
						if (MatrixMath.V2BoxIntersectsPolygon(bounds, poly, 3)) {
							isIntersected = true;
							break;
						}
					}
					if (isIntersected)
						break;
				}
				if (isIntersected)
					break;
			}
			if (isIntersected == true) {
				if (part.isSelected() == false)
					addDirectiveToSelection(part);
			} else if (part.isSelected())
				removeDirectiveFromSelection(part);
		}
		// System.out.println("selectByDragging: " + (System.nanoTime() -
		// nano));
	}

	public void updateScreenProjectionVerticesMap(LDrawDirective directive) {
		// System.out.println("updateScreenProjectionVerticesMap");
		if (directive instanceof LDrawPart == false)
			return;
		
		if(((LDrawPart)directive).isHidden()){
			projectedLDrawDirectiveVerticesMap.remove(directive);
			return;
		}

		LDrawPart part = (LDrawPart) directive;
		Box3 boundingBox = part.boundingBox3();
		if (boundingBox != null) {
			Vector3f[] vertices = part.getCachedOOB();
			ArrayList<Vector2f> projectedVertices = projectedLDrawDirectiveVerticesMap
					.get(part);
			if (projectedVertices == null)
				projectedVertices = new ArrayList<Vector2f>();
			else
				projectedVertices.clear();

			MainCamera camera = MOCBuilder.getInstance().getCamera();
			for (int i = 0; i < vertices.length; i++) {
				Vector2f pos = camera.getWorldToScreenPos(vertices[i]);
				if (pos == null) {
					projectedVertices.clear();
					break;
				}
				projectedVertices.add(pos);
			}
			if (projectedVertices.size() != 0)
				projectedLDrawDirectiveVerticesMap.put(part, projectedVertices);
		}
	}

	public void updateScreenProjectionVerticesMapAll() {
		// System.out.println("updateScreenProjectionVerticesMap All");
		MOCBuilder.getInstance().getCamera().tickle();
		projectedLDrawDirectiveVerticesMap.clear();
		for (LDrawDirective directive : directiveList) {
			updateScreenProjectionVerticesMap(directive);
		}
	}

	public void updateStartMoveTransformMatrixMap() {
		// System.out.println("Updated");
		for (LDrawDirective directive : selectedDirectives.getDirectiveList()) {
			startTransformMatrixMap.put(directive,
					directive.transformationMatrix());
		}

	}

	public DirectiveGroupForTransform getBrickGroupForTransform() {
		return selectedDirectives;
	}

	public ConnectivityManager getConnectivityManager(Vector3f hittedPos) {
		// System.out.println("getConnectivityManager");
		connectivityManager.clear();
		for (LDrawDirective directive : selectedDirectives.getDirectiveList()) {
			if (directive instanceof LDrawPart == false)
				continue;
			LDrawPart part = (LDrawPart) directive;
			if (part.getConnectivityList() == null)
				continue;
			for (Connectivity conn : part.getConnectivityList()) {
				conn.updateConnectivityOrientationInfo();
				if (conn instanceof ICustom2DField)
					continue;
				if (conn.getCurrentPos(part.transformationMatrix())
						.sub(hittedPos).length() <= LDrawGridTypeT.CoarseX3
						.getYValue())
					connectivityManager.addConn(conn);
			}

			if (part.getConnectivityMatrixItemList() == null)
				continue;
			for (MatrixItem matrixItem : part.getConnectivityMatrixItemList()) {
				matrixItem.updateConnectivityOrientationInfo();
				if (matrixItem.getRowIndex() % 2 == 0)
					continue;
				if (matrixItem.getColumnIndex() % 2 == 0)
					continue;
				if (matrixItem.getCurrentPos().sub(hittedPos).length() <= LDrawGridTypeT.CoarseX3
						.getYValue())
					connectivityManager.addMatrixItem(matrixItem);
			}
		}
		return connectivityManager;
	}

	public LDrawDirective getFirstSelectedDirective() {
		synchronized (selectedDirectives) {
			return selectedDirectives.getDirectiveList().get(0);
		}
	}

	public LDrawDirective getSelectedLastDirective() {
		synchronized (selectedDirectives) {
			return selectedDirectives.getDirectiveList().get(
					selectedDirectives.getDirectiveList().size() - 1);
		}

	}

	public int getSelectedDirectiveSize() {
		synchronized (selectedDirectives) {
			return selectedDirectives.size();
		}
	}

	public LDrawPart getPartHavingMinY() {
		Float minYPos = null;
		LDrawPart minYPosPart = null;
		for (LDrawDirective testDirective : getSelectedDirectiveList()) {
			if (testDirective instanceof LDrawPart == false)
				continue;
			if (minYPos == null) {
				minYPos = testDirective.boundingBox3().getMax().y;
				minYPosPart = (LDrawPart) testDirective;
			} else if (testDirective.boundingBox3().getMax().y > minYPos) {
				minYPos = testDirective.boundingBox3().getMax().y;
				minYPosPart = (LDrawPart) testDirective;
			}
		}		
		return minYPosPart;
	}

	public MatrixItem getMatrixItemHavingMinY() {
		Vector3f minPos = null;
		MatrixItem minItem = null;
		for (LDrawDirective testDirective : DirectiveSelectionManager
				.getInstance().getSelectedDirectiveList()) {

			if (testDirective instanceof LDrawPart == false)
				continue;
			if (((LDrawPart) testDirective).getConnectivityMatrixItemList() == null)
				continue;
			for (MatrixItem item : ((LDrawPart) testDirective)
					.getConnectivityMatrixItemList()) {
				if (item.getParent() instanceof Hole) {
					if (item.getRowIndex() % 2 != 1
							|| item.getColumnIndex() % 2 != 1)
						continue;
					if (item.getDirection() != Direction6T.Y_Minus)
						continue;

					if (minPos == null) {
						minPos = item.getCurrentPos(new Matrix4()).add(
								item.getParent().getParent().position());
						minItem = item;
					} else if (minPos.y < item.getCurrentPos(new Matrix4())
							.add(item.getParent().getParent().position()).y) {
						minPos = item.getCurrentPos(new Matrix4()).add(
								item.getParent().getParent().position());
						minItem = item;
					}
				}
			}
		}
		return minItem;
	}

	public LDrawLSynth getLastSelectedLSynth() {
		return lastSelectedLSynth;
	}
}
