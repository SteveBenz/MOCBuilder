package LDraw.Support;

import LDraw.Files.LDrawFile;
import LDraw.Files.LDrawModel;

public class ModelManager {
	static ModelManager sharedModelManager;
	public static ModelManager sharedModelManager() {
		if (sharedModelManager == null) {
			sharedModelManager = new ModelManager();
		}
		return sharedModelManager;
		
	}
	public LDrawModel requestModel(String referenceName, LDrawFile enclosingFile) {
		// TODO Auto-generated method stub
		return null;
	}

}
