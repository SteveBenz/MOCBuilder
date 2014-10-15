package Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import Command.LDrawPart;
import Common.Matrix4;
import Connectivity.ConnectivityTestResultT;
import Connectivity.GlobalConnectivityManager;

public class BrickGroupForTransform {
	private HashMap<LDrawPart, Matrix4> initialTransform;
	private ArrayList<LDrawPart> partList;

	public BrickGroupForTransform() {
		initialTransform = new HashMap<LDrawPart, Matrix4>();
		partList = new ArrayList<LDrawPart>();
	}

	public void addPart(LDrawPart part) {
		initialTransform.put(part, part.transformationMatrix());
		partList.add(part);
	}

	public void removePart(LDrawPart part) {
		initialTransform.remove(part);
		partList.remove(part);
	}

	public void applyTransform(LDrawPart part, Matrix4 transform) {
		if(transform==null)return;
		if(initialTransform.get(part)==null)return;
		Matrix4 newTransform = Matrix4.multiply(
				Matrix4.inverse(initialTransform.get(part)), transform);
		for (Entry<LDrawPart, Matrix4> entry : initialTransform.entrySet()) {
			entry.getKey().setTransformationMatrix(
					Matrix4.multiply(entry.getValue(), newTransform));			
		}
		
		for (LDrawPart p : initialTransform.keySet()) {
			initialTransform.put(p, p.transformationMatrix());
		}		
	}

	public boolean contains(LDrawPart part) {
		return initialTransform.containsKey(part);
	}

	public void dispose() {
		initialTransform.clear();
		initialTransform = null;
	}

	public void clear() {
		for (LDrawPart part : initialTransform.keySet())
			part.isDraggingPart(false);
		initialTransform.clear();
		partList.clear();
	}

	public int size() {
		return partList.size();
	}

	public ArrayList<LDrawPart> getPartList() {
		return partList;
	}

	public boolean isEmpty() {
		return initialTransform.isEmpty();
	}

	public boolean isAllMovable(LDrawPart part, Matrix4 transform) {
		if(BuilderConfigurationManager.getInstance().isUseConnectivity()==false)return true;
		if(transform==null)return false;

		boolean isAllMovable = true;
		Matrix4 initialTransformMatrix = initialTransform.get(part);
		if(initialTransformMatrix==null)return false;
		Matrix4 newTransform = Matrix4.multiply(
				Matrix4.inverse(initialTransformMatrix), transform);
		GlobalConnectivityManager connectivityManager = GlobalConnectivityManager
				.getInstance();

		isAllMovable = true;
		for (Entry<LDrawPart, Matrix4> entry : initialTransform.entrySet()) {
			if (connectivityManager.isConnectable_Exact(entry.getKey(),
					Matrix4.multiply(entry.getValue(), newTransform))
					.getResultType() == ConnectivityTestResultT.False) {
				isAllMovable = false;
				break;
			}
		}
		return isAllMovable;
	}
}
