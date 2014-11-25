package Notification;

import LDraw.Support.LDrawDirective;

public class LDrawDirectiveDidChanged implements INotificationMessage{
	private LDrawDirective parent;
	private LDrawDirective directive;
	
	public LDrawDirectiveDidChanged(LDrawDirective directive){
		this.parent = directive.enclosingDirective();
		this.directive = directive;
	}

	public LDrawDirective getParent() {
		return parent;
	}

	public LDrawDirective getDirective() {
		return directive;
	}
}
