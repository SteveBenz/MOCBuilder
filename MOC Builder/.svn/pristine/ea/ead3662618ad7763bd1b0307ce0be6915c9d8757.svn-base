package Common;


public class Box3 implements Cloneable{
	private static Box3 invalidBox = new Box3();

	/**
	 * @uml.property  name="min"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Vector3f min;
	/**
	 * @uml.property  name="max"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	private Vector3f max;
	
	public Box3(){
		this.min = new Vector3f();
		this.max = new Vector3f();
	}
	
	public Box3(Vector3f min, Vector3f max){
		this.min = min;
		this.max = max;
	}
		
	public static Box3 getInvalidBox() {
		return invalidBox;
	}

	/**
	 * @return
	 * @uml.property  name="min"
	 */
	public Vector3f getMin() {
		return min;
	}

	/**
	 * @param min
	 * @uml.property  name="min"
	 */
	public void setMin(Vector3f min) {
		this.min = min;
	}

	/**
	 * @return
	 * @uml.property  name="max"
	 */
	public Vector3f getMax() {
		return max;
	}

	/**
	 * @param max
	 * @uml.property  name="max"
	 */
	public void setMax(Vector3f max) {
		this.max = max;
	}
	
	public Object clone()  throws CloneNotSupportedException{
		Box3 a = (Box3) super.clone();
		a.max = (Vector3f)max.clone();
		a.min = (Vector3f)min.clone();
		return a;
	}
}
