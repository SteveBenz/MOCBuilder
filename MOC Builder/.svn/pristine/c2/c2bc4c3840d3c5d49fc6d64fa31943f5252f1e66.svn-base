package UndoRedo;

import java.util.Stack;

import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Window.MOCBuilder;

public class LDrawUndoRedoManager {
	private static LDrawUndoRedoManager _instance = null;
	private Stack<IAction> undoActionStack;
	private Stack<IAction> redoActionStack;

	private final int stackSize = 100;

	private LDrawUndoRedoManager() {

		undoActionStack = new Stack<IAction>();
		redoActionStack = new Stack<IAction>();

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.UndoRedoManagerUpdated);
	}

	public synchronized static LDrawUndoRedoManager getInstance() {
		if (_instance == null)
			_instance = new LDrawUndoRedoManager();

		return _instance;
	}

	public void pushUndoAction(IAction action) {
		MOCBuilder.getInstance().setChanged();
		undoActionStack.push(action);
		if (undoActionStack.size() > stackSize * 2)
			for (int i = 0; i < stackSize; i++)
				undoActionStack.remove(i);

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.UndoRedoManagerUpdated);
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

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.UndoRedoManagerUpdated);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedReDraw);

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

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.UndoRedoManagerUpdated);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.NeedReDraw);
	}

	public void clear() {
		undoActionStack.clear();
		redoActionStack.clear();

		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.UndoRedoManagerUpdated);
	}

	public boolean isEmptyRedoStack() {
		return this.redoActionStack.isEmpty();
	}

	public boolean isEmptyUndoStack() {
		return this.undoActionStack.isEmpty();
	}
}
