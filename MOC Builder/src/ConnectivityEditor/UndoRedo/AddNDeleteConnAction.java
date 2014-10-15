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
		LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();

		for (int i = 0; i < connList.size(); i++) {
			Connectivity conn = connList.get(i);

			if (flagList.get(i)) {
				if (conn instanceof CollisionBox)
					part.getCollisionBoxList().remove(conn);
				else
					part.getConnectivityList().remove(conn);
			} else {
				if (conn instanceof CollisionBox)
					part.getCollisionBoxList().add((CollisionBox) conn);
				else
					part.getConnectivityList().add(conn);
			}
		}
	}

	@Override
	public void redoAction() {
		LDrawPart part = ConnectivityEditor.getInstance().getWorkingPart();

		for (int i = 0; i < connList.size(); i++) {
			Connectivity conn = connList.get(i);

			if (!flagList.get(i)) {
				if (conn instanceof CollisionBox)
					part.getCollisionBoxList().remove(conn);
				else
					part.getConnectivityList().remove(conn);
			} else {
				if (conn instanceof CollisionBox)
					part.getCollisionBoxList().add((CollisionBox) conn);
				else
					part.getConnectivityList().add(conn);
			}
		}
	}
}
