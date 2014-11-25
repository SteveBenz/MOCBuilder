package UndoRedo;

import LDraw.Support.LDrawMetaCommand;
import Notification.LDrawDirectiveDidChanged;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class ModifyCommentAction implements IAction {
	private LDrawMetaCommand comment;
	private String newString;
	private String oldString;

	public ModifyCommentAction(LDrawMetaCommand comment, String newString) {
		this.comment = comment;
		this.oldString = comment.stringValue();
		this.newString = newString;
	}

	@Override
	public void undoAction() {
		comment.setStringValue(oldString);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(comment));
	}

	@Override
	public void redoAction() {
		comment.setStringValue(newString);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(comment));
	}
}
