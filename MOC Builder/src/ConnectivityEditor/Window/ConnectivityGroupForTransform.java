package ConnectivityEditor.Window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Builder.BuilderConfigurationManager;
import Command.LDrawPart;
import Common.Matrix4;
import Connectivity.Connectivity;
import Connectivity.ConnectivityTestResultT;
import Connectivity.GlobalConnectivityManager;

public class ConnectivityGroupForTransform {
	private HashMap<Connectivity, Matrix4> initialTransform;
	private ArrayList<Connectivity> connList;

	public ConnectivityGroupForTransform() {
		initialTransform = new HashMap<Connectivity, Matrix4>();
		connList = new ArrayList<Connectivity>();
	}

	public void add(Connectivity conn) {
		initialTransform.put(conn, conn.getTransformMatrix());
		connList.add(conn);
	}

	public void remove(Connectivity part) {
		initialTransform.remove(part);
		connList.remove(part);
	}

	public void applyTransform(Connectivity conn, Matrix4 transform) {
		Matrix4 newTransform = Matrix4.multiply(
				Matrix4.inverse(initialTransform.get(conn)), transform);
		for (Entry<Connectivity, Matrix4> entry : initialTransform.entrySet()) {
			entry.getKey().setTransformMatrix(
					Matrix4.multiply(entry.getValue(), newTransform));			
		}
		
		for (Connectivity p : initialTransform.keySet()) {
			initialTransform.put(p, p.getTransformMatrix());
		}		
	}

	public boolean contains(Connectivity part) {
		return initialTransform.containsKey(part);
	}

	public void dispose() {
		initialTransform.clear();
		initialTransform = null;
	}

	public void clear() {		
		initialTransform.clear();
		connList.clear();
	}

	public int size() {
		return connList.size();
	}

	public ArrayList<Connectivity> getConnectivityList() {
		return connList;
	}

	public boolean isEmpty() {
		return initialTransform.isEmpty();
	}
}
