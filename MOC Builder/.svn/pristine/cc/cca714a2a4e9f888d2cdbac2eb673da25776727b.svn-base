package ConnectivityEditor.UndoRedo;

import java.util.Stack;

import UndoRedo.IAction;
import Window.MOCBuilder;

public class ConnectivityEditorUndoRedoManager {
	private static ConnectivityEditorUndoRedoManager _instance = null;
	private Stack<IAction> undoActionStack;
	private Stack<IAction> redoActionStack;

	private final int stackSize = 100;

	private ConnectivityEditorUndoRedoManager() {

		undoActionStack = new Stack<IAction>();
		redoActionStack = new Stack<IAction>();
	}

	public synchronized static ConnectivityEditorUndoRedoManager getInstance() {
		if (_instance == null)
			_instance = new ConnectivityEditorUndoRedoManager();

		return _instance;
	}

	public void pushUndoAction(IAction action) {
		MOCBuilder.getInstance().setChanged();
		undoActionStack.push(action);
		if (undoActionStack.size() > stackSize * 2)
			for (int i = 0; i < stackSize; i++)
				undoActionStack.remove(i);
	}

	public void undo() {
		if (undoActionStack.isEmpty())
			return;
		IAction action = undoActionStack.pop();
		if (action == null)
			return;
		action.undoAction();
		redoActionStack.push(action);
		if (redoActionStack.size() > stackSize * 2)
			for (int i = 0; i < stackSize; i++)
				redoActionStack.remove(i);
	}

	public void redo() {
		if (redoActionStack.isEmpty())
			return;
		IAction action = redoActionStack.pop();
		if (action == null)
			return;
		action.redoAction();
		undoActionStack.push(action);
		if (undoActionStack.size() > stackSize * 2)
			for (int i = 0; i < stackSize; i++)
				undoActionStack.remove(i);
	}

	public void clear() {
		undoActionStack.clear();
		redoActionStack.clear();
	}
}
