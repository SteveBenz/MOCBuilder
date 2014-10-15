package Window;

import Common.Vector2f;

public class GlobalMousePosition {
	private static GlobalMousePosition _instance = null;
	
	private Vector2f position;
	
	public synchronized static GlobalMousePosition getInstance(){
		if(_instance==null)
			_instance = new GlobalMousePosition();
		return _instance;
	}
	
	private GlobalMousePosition(){
		this.position = new Vector2f(400, 400);
	}
	
	public Vector2f getPos(){
		return this.position;
	}
	
	public void setPos(float x, float y){
		position.setX(x);
		position.setY(y);
	}

}
