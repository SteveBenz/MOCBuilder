package UndoRedo;

import java.util.ArrayList;
import java.util.HashMap;

import Builder.DirectiveSelectionManager;
import Command.LDrawDrawableElement;
import Common.Matrix4;
import Connectivity.GlobalConnectivityManager;

public class MoveDirectivesAction implements IAction {
	private ArrayList<LDrawDrawableElement> elementList;
	private HashMap<LDrawDrawableElement, Matrix4> originalTransformMap;
	private HashMap<LDrawDrawableElement, Matrix4> newTransformMap;

	public MoveDirectivesAction() {
		elementList = new ArrayList<LDrawDrawableElement>();
		originalTransformMap = new HashMap<LDrawDrawableElement, Matrix4>();
		newTransformMap = new HashMap<LDrawDrawableElement, Matrix4>();
	}

	public void addElement(LDrawDrawableElement element, Matrix4 originalTransform,
			Matrix4 newTransform) {
		elementList.add(element);
		originalTransformMap.put(element, originalTransform);
		newTransformMap.put(element, newTransform);
	}

	@Override
	public void undoAction() {		
//		System.out.println("undo");
		for (LDrawDrawableElement element : elementList)
			element.setTransformationMatrix(originalTransformMap.get(element));
		
		GlobalConnectivityManager.getInstance().updateMatrixAll();
		DirectiveSelectionManager.getInstance().updateScreenProjectionVerticesMapAll();
	}

	@Override
	public void redoAction() {
		for (LDrawDrawableElement element : elementList)
			element.setTransformationMatrix(newTransformMap.get(element));		
		GlobalConnectivityManager.getInstance().updateMatrixAll();
		DirectiveSelectionManager.getInstance().updateScreenProjectionVerticesMapAll();
	}
}
