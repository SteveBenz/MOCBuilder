package Notification;

import Command.LDrawPart;

public class LDrawPartSelect implements INotificationMessage{
	private LDrawPart part;
	
	public LDrawPartSelect(LDrawPart part){
		this.part = part;
	}


	public LDrawPart getPart() {
		return part;
	}
	
	
	
	
}
