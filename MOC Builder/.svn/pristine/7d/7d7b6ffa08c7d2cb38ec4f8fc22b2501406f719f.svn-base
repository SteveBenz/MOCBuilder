package Window;

import Command.LDrawColor;

public class DNDTransfer {
	private static DNDTransfer transfer = new DNDTransfer();
	private Object object;
	private LDrawColor color;
	
	public synchronized static DNDTransfer getInstance() {
		return transfer;
	}
	
	public void setData (Object object){
		this.object = object;
	}
	public Object getData(){
		return object;
	}
	
	public void setColor (LDrawColor color){
		this.color = color;
	}
	
	public LDrawColor getColor (){
		return color;
	}
	
	public void end (){
		object = null;
		color = null;
	}
}
