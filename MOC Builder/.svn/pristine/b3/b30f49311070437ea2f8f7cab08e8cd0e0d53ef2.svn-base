package LDraw.Support;

import Common.Vector3f;
import Common.Vector4f;

public class TransformComponents {
	/**
	 * @uml.property  name="scale"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f scale;
	/**
	 * @uml.property  name="shear_XY"
	 */
	float shear_XY;
	/**
	 * @uml.property  name="shear_XZ"
	 */
	float shear_XZ;
	/**
	 * @uml.property  name="shear_YZ"
	 */
	float shear_YZ;
	/**
	 * @uml.property  name="rotate"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f rotate; //in radians
	/**
	 * @uml.property  name="translate"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f translate;
	/**
	 * @uml.property  name="perspective"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector4f perspective;
	
	public TransformComponents(){
		this.scale= new Vector3f(1, 1, 1);
		this.shear_XY = 0;
		this.shear_XZ = 0;
		this.shear_YZ = 0;
		this.rotate = new Vector3f(0, 0, 0);
		this.translate = new Vector3f(0, 0, 0);
		this.perspective = new Vector4f(0, 0, 0, 0);
	}

	public static TransformComponents getIdentityComponents() {
		return new TransformComponents();
	}

	/**
	 * @return
	 * @uml.property  name="translate"
	 */
	public Vector3f getTranslate() {
		return this.translate;
	}

	/**
	 * @return
	 * @uml.property  name="scale"
	 */
	public Vector3f getScale() {
		return scale;
	}

	/**
	 * @param scale
	 * @uml.property  name="scale"
	 */
	public void setScale(Vector3f scale) {
		this.scale = scale;
	}

	/**
	 * @return
	 * @uml.property  name="shear_XY"
	 */
	public float getShear_XY() {
		return shear_XY;
	}

	/**
	 * @param shear_XY
	 * @uml.property  name="shear_XY"
	 */
	public void setShear_XY(float shear_XY) {
		this.shear_XY = shear_XY;
	}

	/**
	 * @return
	 * @uml.property  name="shear_XZ"
	 */
	public float getShear_XZ() {
		return shear_XZ;
	}

	/**
	 * @param shear_XZ
	 * @uml.property  name="shear_XZ"
	 */
	public void setShear_XZ(float shear_XZ) {
		this.shear_XZ = shear_XZ;
	}

	/**
	 * @return
	 * @uml.property  name="shear_YZ"
	 */
	public float getShear_YZ() {
		return shear_YZ;
	}

	/**
	 * @param shear_YZ
	 * @uml.property  name="shear_YZ"
	 */
	public void setShear_YZ(float shear_YZ) {
		this.shear_YZ = shear_YZ;
	}

	/**
	 * @return
	 * @uml.property  name="rotate"
	 */
	public Vector3f getRotate() {
		return rotate;
	}

	/**
	 * @param rotate
	 * @uml.property  name="rotate"
	 */
	public void setRotate(Vector3f rotate) {
		this.rotate = rotate;
	}

	/**
	 * @return
	 * @uml.property  name="perspective"
	 */
	public Vector4f getPerspective() {
		return perspective;
	}

	/**
	 * @param perspective
	 * @uml.property  name="perspective"
	 */
	public void setPerspective(Vector4f perspective) {
		this.perspective = perspective;
	}

	/**
	 * @param translate
	 * @uml.property  name="translate"
	 */
	public void setTranslate(Vector3f translate) {
		this.translate = translate;
	}
	
	
}
