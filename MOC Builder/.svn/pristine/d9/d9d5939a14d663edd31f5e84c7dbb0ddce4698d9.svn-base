package Connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import Command.LDrawPart;
import LDraw.Support.ConnectivityLibrary;

public class BrickFinder {
	private static BrickFinder _instance = null;

	private HashMap<String, ArrayList<Connectivity>> connectivityMapCache;

	private BrickFinder() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				ConnectivityLibrary.getInstance().loadAllConnectivity();
			}
		}).start();

		connectivityMapCache = ConnectivityLibrary.getInstance()
				.getConnectivity();
	}

	public synchronized static BrickFinder getInstance() {
		if (_instance == null)
			_instance = new BrickFinder();

		return _instance;
	}

	public ArrayList<String> findConnectibleBrick(LDrawPart part) {
		ArrayList<String> resultList = new ArrayList<String>();
		if(resultList!=null)return resultList;
		
		ArrayList<Connectivity> connectivityListOfPart = part
				.getConnectivityList();
		if (connectivityListOfPart.isEmpty())
			return resultList;
		boolean isConnectible;
		for (Entry<String, ArrayList<Connectivity>> entry : connectivityMapCache
				.entrySet()) {
			isConnectible = false;
			for (IConnectivity conn : connectivityListOfPart) {
				if (conn instanceof MatrixItem)
					continue;
				for (IConnectivity conn2 : entry.getValue()) {
					if (conn2 instanceof ICustom2DField)
						continue;
					if (conn.isConnectable(conn2) == ConnectivityTestResultT.True) {
						isConnectible = true;
						break;
					}
				}
				if (isConnectible == true)
					break;
			}
			if (isConnectible)
				resultList.add(entry.getKey());
		}

		return resultList;
	}

}
