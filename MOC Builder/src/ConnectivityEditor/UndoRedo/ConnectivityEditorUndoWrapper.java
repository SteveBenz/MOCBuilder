package ConnectivityEditor.UndoRedo;

import java.util.ArrayList;

import Command.LDrawPart;
import Common.Matrix4;
import Common.Vector3f;
import Connectivity.CollisionBox;
import Connectivity.Connectivity;
import Connectivity.IConnectivity;
import ConnectivityEditor.ConnectivityControlGuide.ConnectivityMovementGuideRenderer;
import ConnectivityEditor.Window.ConnectivityEditor;
import ConnectivityEditor.Window.ConnectivitySelectionManager;
import LDraw.Support.LDrawDirective;
import UndoRedo.AddNRemoveDirectiveAction;
import UndoRedo.LDrawUndoRedoManager;

public class ConnectivityEditorUndoWrapper {
	private static ConnectivityEditorUndoWrapper _instance = null;

	private ConnectivityEditor cEditor = null;

	private ConnectivityEditorUndoWrapper() {
		cEditor = ConnectivityEditor.getInstance();
	}

	public synchronized static ConnectivityEditorUndoWrapper getInstance() {
		if (_instance == null)
			_instance = new ConnectivityEditorUndoWrapper();
		return _instance;
	}

	public void removeSelectedDirective() {
		if (ConnectivitySelectionManager.getInstance().isEmpty())
			return;
		AddNDeleteConnAction action = new AddNDeleteConnAction();
		action.removeConnectivities(ConnectivitySelectionManager.getInstance()
				.getSelectedConnectivityList());

		for (Connectivity conn : ConnectivitySelectionManager.getInstance()
				.getSelectedConnectivityList()) {
			cEditor.removeConnectivity(conn);
		}
		handleBrickControlGuideDisplay(null);
		
		ConnectivityEditorUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void addConnectivity(Connectivity conn) {
		AddNDeleteConnAction action = new AddNDeleteConnAction();
		action.addConnectivity(conn);
		cEditor.addConnectivity(conn);
		ConnectivityEditorUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void handleTransformSelectedDirective() {
		MoveConnectivityAction action = new MoveConnectivityAction();
		Matrix4 originalMatrix;
		ArrayList<Connectivity> connectivityList = ConnectivitySelectionManager
				.getInstance().getSelectedConnectivityList();
		for (Connectivity connectivity : connectivityList) {
			originalMatrix = ConnectivitySelectionManager.getInstance()
					.getInitialMoveTransformMatrix(connectivity);
			if (originalMatrix == null)
				continue;
			action.addMoveConnectivity(connectivity, originalMatrix,
					connectivity.getTransformMatrix());
		}

		ConnectivityEditorUndoRedoManager.getInstance().pushUndoAction(action);
	}

	public void rotateSelectedDirectiveBy(Vector3f degree) {
		cEditor.rotateSelectedConnectivity(degree);
		handleTransformSelectedDirective();
	}

	private void handleBrickControlGuideDisplay(IConnectivity conn) {
		ConnectivityMovementGuideRenderer.getInstance().setConn(conn);
	}
}
