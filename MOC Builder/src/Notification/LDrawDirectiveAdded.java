package Notification;

import LDraw.Support.LDrawDirective;

public class LDrawDirectiveAdded implements INotificationMessage{
	private LDrawDirective parent;
	private LDrawDirective directive;
	
	public LDrawDirectiveAdded(LDrawDirective parent, LDrawDirective directive){
		this.parent = parent;	
		this.directive = directive;
	}

	public LDrawDirective getParent() {
		return parent;
	}

	public LDrawDirective getDirective() {
		return directive;
	}
	
	
	
	
}
