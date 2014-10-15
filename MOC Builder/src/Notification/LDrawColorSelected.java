package Notification;

import Command.LDrawColorT;

public class LDrawColorSelected implements INotificationMessage{
	private LDrawColorT colorT;
	
	public LDrawColorSelected(LDrawColorT colorT){
		this.colorT = colorT;
	}


	public LDrawColorT getColorCode() {
		return colorT;
	}
	
	
	
	
}
