package UndoRedo;

import java.util.ArrayList;
import java.util.HashMap;

import Builder.BrickSelectionManager;
import Command.LDrawPart;
import Common.Matrix4;
import Connectivity.GlobalConnectivityManager;

public class MovePartsAction implements IAction {
	private ArrayList<LDrawPart> partList;
	private HashMap<LDrawPart, Matrix4> originalTransformMap;
	private HashMap<LDrawPart, Matrix4> newTransformMap;

	public MovePartsAction() {
		partList = new ArrayList<LDrawPart>();
		originalTransformMap = new HashMap<LDrawPart, Matrix4>();
		newTransformMap = new HashMap<LDrawPart, Matrix4>();
	}

	public void addMovePart(LDrawPart part, Matrix4 originalTransform,
			Matrix4 newTransform) {
		partList.add(part);
		originalTransformMap.put(part, originalTransform);
		newTransformMap.put(part, newTransform);
	}

	@Override
	public void undoAction() {		
//		System.out.println("undo");
		for (LDrawPart part : partList)
			part.setTransformationMatrix(originalTransformMap.get(part));
		
		GlobalConnectivityManager.getInstance().updateMatrixAll();
		BrickSelectionManager.getInstance().updateScreenProjectionVerticesMapAll();
	}

	@Override
	public void redoAction() {
		for (LDrawPart part : partList)
			part.setTransformationMatrix(newTransformMap.get(part));		
		GlobalConnectivityManager.getInstance().updateMatrixAll();
		BrickSelectionManager.getInstance().updateScreenProjectionVerticesMapAll();
	}
}
