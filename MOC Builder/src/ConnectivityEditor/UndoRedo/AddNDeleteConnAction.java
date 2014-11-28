package ConnectivityEditor.UndoRedo;

import java.util.ArrayList;

import Command.LDrawPart;
import Connectivity.CollisionBox;
import Connectivity.Connectivity;
import ConnectivityEditor.Window.ConnectivityEditor;
import UndoRedo.IAction;

public class AddNDeleteConnAction implements IAction {

	private ArrayList<Connectivity> connList;
	private ArrayList<Boolean> flagList;

	public AddNDeleteConnAction() {
		connList = new ArrayList<Connectivity>();
		flagList = new ArrayList<Boolean>();
	}

	private void add(Connectivity conn, boolean flag) {
		connList.add(conn);
		flagList.add(flag);
	}

	public void addConnectivity(Connectivity conn) {
		add(conn, true);
	}

	public void removeConnectivity(Connectivity conn) {
		add(conn, false);

	}

	public void removeConnectivities(ArrayList<Connectivity> connList) {
		for (Connectivity conn : connList) {
			add(conn, false);
		}
	}

	@Override
	public void undoAction() {
		for (int i = 0; i < connList.size(); i++) {
			Connectivity conn = connList.get(i);
			if (flagList.get(i)) {
				ConnectivityEditor.getInstance().removeConnectivity(conn);
			} else {
				ConnectivityEditor.getInstance().addConnectivity(conn);
			}
		}
	}

	@Override
	public void redoAction() {
		for (int i = 0; i < connList.size(); i++) {
			Connectivity conn = connList.get(i);

			if (!flagList.get(i)) {
				ConnectivityEditor.getInstance().removeConnectivity(conn);
			} else {
				ConnectivityEditor.getInstance().addConnectivity(conn);
			}
		}
	}
}
