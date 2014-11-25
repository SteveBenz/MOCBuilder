package Notification;

import LDraw.Support.LDrawDirective;

public class LDrawDirectiveSelected implements INotificationMessage{
	private LDrawDirective directive;
	
	public LDrawDirectiveSelected(LDrawDirective directive){
		this.directive = directive;
	}

	public LDrawDirective getDirective() {
		return directive;
	}
}
