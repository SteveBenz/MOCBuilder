package Builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Command.LDrawPart;
import Common.Matrix4;
import Connectivity.ConnectivityTestResultT;
import Connectivity.GlobalConnectivityManager;
import LDraw.Support.LDrawDirective;

public class DirectiveGroupForTransform {
	private HashMap<LDrawDirective, Matrix4> initialTransform;
	private ArrayList<LDrawDirective> directiveList;

	public DirectiveGroupForTransform() {
		initialTransform = new HashMap<LDrawDirective, Matrix4>();
		directiveList = new ArrayList<LDrawDirective>();
	}

	public void addDirective(LDrawDirective directive) {
		synchronized (initialTransform) {
			initialTransform.put(directive, directive.transformationMatrix());
		}

		synchronized (directiveList) {
			directiveList.add(directive);
		}
	}

	public void removeDirective(LDrawDirective directive) {
		synchronized (initialTransform) {
			initialTransform.remove(directive);
		}
		synchronized (directiveList) {
			directiveList.remove(directive);
		}
	}

	public void applyTransform(LDrawDirective directive, Matrix4 transform) {
		if (transform == null)
			return;
		synchronized (initialTransform) {
			if (initialTransform.get(directive) == null)
				return;
			Matrix4 newTransform = Matrix4
					.multiply(Matrix4.inverse(initialTransform.get(directive)),
							transform);
			for (Entry<LDrawDirective, Matrix4> entry : initialTransform
					.entrySet()) {
				entry.getKey().setTransformationMatrix(
						Matrix4.multiply(entry.getValue(), newTransform));
			}

			for (LDrawDirective d : initialTransform.keySet()) {
				initialTransform.put(d, d.transformationMatrix());
			}
		}
	}

	public boolean contains(LDrawDirective directive) {
		synchronized (initialTransform) {
			return initialTransform.containsKey(directive);
		}
	}

	public void dispose() {
		synchronized (initialTransform) {
			initialTransform.clear();
			initialTransform = null;
		}
	}

	public void clear() {
		synchronized (initialTransform) {
			for (LDrawDirective directive : initialTransform.keySet())
				directive.isDraggingDirective(false);
			initialTransform.clear();
		}

		synchronized (directiveList) {
			directiveList.clear();
		}

	}

	public int size() {
		synchronized (directiveList) {
			return directiveList.size();
		}
	}

	public ArrayList<LDrawDirective> getDirectiveList() {
		synchronized (directiveList) {
			return directiveList;
		}

	}

	public boolean isEmpty() {
		synchronized (initialTransform) {
			return initialTransform.isEmpty();
		}

	}

	public boolean isAllMovable(LDrawDirective directive, Matrix4 transform) {
		if (BuilderConfigurationManager.getInstance().isUseConnectivity() == false)
			return true;
		if (transform == null)
			return false;

		boolean isAllMovable = true;
		Matrix4 initialTransformMatrix = null;
		synchronized (initialTransform) {
			initialTransformMatrix = initialTransform.get(directive);
		}

		if (initialTransformMatrix == null)
			return false;
		Matrix4 newTransform = Matrix4.multiply(
				Matrix4.inverse(initialTransformMatrix), transform);
		GlobalConnectivityManager connectivityManager = GlobalConnectivityManager
				.getInstance();

		isAllMovable = true;
		synchronized (initialTransform) {
			for (Entry<LDrawDirective, Matrix4> entry : initialTransform
					.entrySet()) {
				if (entry.getKey() instanceof LDrawPart == false)
					continue;

				if (connectivityManager.isConnectable_Exact(
						((LDrawPart) entry.getKey()),
						Matrix4.multiply(entry.getValue(), newTransform))
						.getResultType() == ConnectivityTestResultT.False) {
					isAllMovable = false;
					break;
				}
			}
		}
		return isAllMovable;
	}
}
