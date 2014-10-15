package Window;

public enum BrickControlModeT {
	BrickControl, BrickControl_Direct, BrickControl_Guide, BrickSelectingDrag, None;
	
	public static BrickControlModeT currentControlMode = BrickControlModeT.None; 
}
