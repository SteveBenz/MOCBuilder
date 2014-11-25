package UndoRedo;

import LDraw.Files.LDrawStep;
import Notification.LDrawDirectiveDidChanged;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class RenameGroupAction implements IAction {
	private LDrawStep step;
	private String newName;
	private String oldName;

	public RenameGroupAction(LDrawStep step, String newName) {
		this.step = step;
		this.oldName = step.getStepName();
		this.newName = newName;
	}

	@Override
	public void undoAction() {
		step.setStepName(oldName);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(step));
	}

	@Override
	public void redoAction() {
		step.setStepName(newName);
		NotificationCenter.getInstance().postNotification(
				NotificationMessageT.LDrawDirectiveDidChanged,
				new LDrawDirectiveDidChanged(step));
	}
}
