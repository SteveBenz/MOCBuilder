package Common;

import LDraw.Support.MatrixMath;

public class Size2 {

	/**
	 * @uml.property  name="width"
	 */
	private float width;
	/**
	 * @uml.property  name="height"
	 */
	private float height;

	public Size2(){
		this(0, 0);
	}
	public Size2(float width, float height) {
		this.width = width;
		this.height = height;
	}

	public static Size2 getZeroSize2() {
		// TODO Auto-generated method stub
		return new Size2(0, 0);
	}

	/**
	 * @param width
	 * @uml.property  name="width"
	 */
	public void setWidth(float width) {
		this.width = width;
	}
	
	/**
	 * @param height
	 * @uml.property  name="height"
	 */
	public void setHeight(float height) {
		this.height = height;
	}
	
	/**
	 * @return
	 * @uml.property  name="width"
	 */
	public float getWidth(){
		return width;
	}	
	
	
	/**
	 * @return
	 * @uml.property  name="height"
	 */
	public float getHeight(){
		return height;
	}
	
	public String toString(){
		return width+", "+height;
	}
}
