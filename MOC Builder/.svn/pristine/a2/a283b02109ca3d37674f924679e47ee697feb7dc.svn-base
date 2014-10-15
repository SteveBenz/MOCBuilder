package OtherTools;

import Command.LDrawColorT;

public class Syringe {
	private static Syringe _instance = null;

	private Syringe() {
	}

	public synchronized static Syringe getInstance() {
		if (_instance == null)
			_instance = new Syringe();
		return _instance;
	}

	private LDrawColorT colorColde = null;
	private boolean isActivated=false;

	public void clear() {
		colorColde = null;
		isActivated=false;
	}

	public LDrawColorT getColorCode() {
		return colorColde;
	}

	public void setColorCode(LDrawColorT colorCode) {
		this.colorColde = colorCode;
	}
	
	public boolean isActivated(){
		return isActivated;
	}
	
	public void activate(){
		this.isActivated = true;
	}
}
