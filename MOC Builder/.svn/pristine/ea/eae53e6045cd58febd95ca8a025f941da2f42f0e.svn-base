package Builder;

import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class MouseControlMode {
	public enum MouseControlModeT {
		Seletion, MoveCamera, RotateCamera;
	}

	private static MouseControlModeT currentMode = MouseControlModeT.Seletion;

	public static MouseControlModeT getCurrentMode() {
		synchronized (currentMode) {
			return currentMode;
		}
	}

	public static void setCurrentMode(MouseControlModeT mode) {
		synchronized (currentMode) {
//			if (currentMode != mode) {
				currentMode = mode;
				NotificationCenter.getInstance().postNotification(
						NotificationMessageT.MouseControlModeChanged);
			}
		}
//	}
}
