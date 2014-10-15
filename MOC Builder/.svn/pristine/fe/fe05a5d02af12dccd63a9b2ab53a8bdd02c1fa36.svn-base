package Builder;

import java.util.HashMap;

import Common.Vector3f;

public class CameraMoveShortCutManager {
	private static CameraMoveShortCutManager _instance = null;
	private CameraMoveShortCutManager(){
		posMap = new HashMap<String, Vector3f>();
	}
	
	public synchronized static CameraMoveShortCutManager getInstance(){
		if(_instance==null)
			_instance = new CameraMoveShortCutManager();
		return _instance;
	}
	
	private HashMap<String, Vector3f> posMap;
	
	
	public void clear(){
		posMap.clear();
	}
	
	public void regPos(String key, Vector3f pos){
		posMap.put(key, pos);
	}
	
	public Vector3f getPos(String key){
		return posMap.get(key);
	}
}
