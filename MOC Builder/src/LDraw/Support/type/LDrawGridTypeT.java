package LDraw.Support.type;

import Common.Vector3f;

public enum LDrawGridTypeT {
	CoarseX10(200, 240, 0), CoarseX3(60, 72, 0), Coarse(20, 24, 90), Medium(10,12,45), Fine(2,2,45);
	
	private int xzValue;
	private int yValue;
	private int rotationValue;
	
	private LDrawGridTypeT(int xValue, int yValue, int degree){
		this.xzValue = xValue;
		this.yValue = yValue;
		this.rotationValue = degree;
	}
	
	public int getXZValue(){
		return this.xzValue;
	}
	
	public int getYValue(){
		return this.yValue;
	}
	
	public int getRotationValue(){
		return this.rotationValue;
	}
	
	public static Vector3f getSnappedPos(Vector3f originalPos, LDrawGridTypeT gridUnit){
		Vector3f newPos = new Vector3f(originalPos);
		newPos.x = Math.round(Math.round(newPos.x)*1.0f/gridUnit.getXZValue())*gridUnit.getXZValue();
		newPos.y = Math.round(Math.round(newPos.y)*1.0f/gridUnit.getYValue())*gridUnit.getYValue();
		newPos.z = Math.round(Math.round(newPos.z)*1.0f/gridUnit.getXZValue())*gridUnit.getXZValue();
		
		return newPos;
	}
}
