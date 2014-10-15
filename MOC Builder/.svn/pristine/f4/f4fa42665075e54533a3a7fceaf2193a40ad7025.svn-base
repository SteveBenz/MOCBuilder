package Notification;

import LDraw.Support.LDrawDirective;

public class LDrawDirectiveModified implements INotificationMessage{
	private LDrawDirective parent;
	private LDrawDirective directive;
	
	public LDrawDirectiveModified(LDrawDirective directive){
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
