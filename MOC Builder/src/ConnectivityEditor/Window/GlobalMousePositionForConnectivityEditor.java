package ConnectivityEditor.Window;

import Common.Vector2f;

public class GlobalMousePositionForConnectivityEditor {
	private static GlobalMousePositionForConnectivityEditor _instance = null;
	
	private Vector2f position;
	
	public synchronized static GlobalMousePositionForConnectivityEditor getInstance(){
		if(_instance==null)
			_instance = new GlobalMousePositionForConnectivityEditor();
		return _instance;
	}
	
	private GlobalMousePositionForConnectivityEditor(){
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
