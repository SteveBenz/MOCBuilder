package Command;

import LDraw.Support.LDrawDirective;
import Notification.LDrawDirectiveDidChanged;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;

public class LDrawLSynthDirective extends LDrawPart {

	private String stringValue;

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getStringValue() {
		return this.stringValue;
	}

	@Override
	public void setLDrawColor(LDrawColor newColor) {
		if (newColor == getLDrawColor())
			return;

		super.setLDrawColor(newColor);
		LDrawDirective enclosingDirective = enclosingDirective();
		if (enclosingDirective != null
				&& enclosingDirective instanceof LDrawLSynth) {
			((LDrawLSynth) enclosingDirective).setLDrawColor(newColor);
			NotificationCenter.getInstance().postNotification(
					NotificationMessageT.LDrawDirectiveDidChanged,
					new LDrawDirectiveDidChanged(enclosingDirective));
		}
	}
}
