package Renderer;


public class LDrawTextureSpec implements Cloneable {
	/**
	 * @uml.property name="projection"
	 */
	int projection;
	/**
	 * @uml.property name="tex_obj"
	 */
	int tex_obj;
	/**
	 * @uml.property name="plane_s"
	 */
	float plane_s[] = new float[4];
	/**
	 * @uml.property name="plane_t"
	 */
	float plane_t[] = new float[4];

	/**
	 * @return
	 * @uml.property name="projection"
	 */
	public int getProjection() {
		return projection;
	}

	/**
	 * @param projection
	 * @uml.property name="projection"
	 */
	public void setProjection(int projection) {
		this.projection = projection;
	}

	/**
	 * @return
	 * @uml.property name="tex_obj"
	 */
	public int getTex_obj() {
		return tex_obj;
	}

	/**
	 * @param tex_obj
	 * @uml.property name="tex_obj"
	 */
	public void setTex_obj(int tex_obj) {
		this.tex_obj = tex_obj;
	}

	/**
	 * @return
	 * @uml.property name="plane_s"
	 */
	public float[] getPlane_s() {
		return plane_s;
	}

	/**
	 * @param plane_s
	 * @uml.property name="plane_s"
	 */
	public void setPlane_s(float[] plane_s) {
		this.plane_s = plane_s;
	}

	/**
	 * @return
	 * @uml.property name="plane_t"
	 */
	public float[] getPlane_t() {
		return plane_t;
	}

	/**
	 * @param plane_t
	 * @uml.property name="plane_t"
	 */
	public void setPlane_t(float[] plane_t) {
		this.plane_t = plane_t;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	public boolean equals(Object b) {
		LDrawTextureSpec theOther = (LDrawTextureSpec) b;
		if (projection != theOther.getProjection())
			return false;
		if (tex_obj != theOther.getTex_obj())
			return false;
		float plane_s_temp[] = theOther.getPlane_s();
		float plane_t_temp[] = theOther.getPlane_t();
		for (int i = 0; i < 4; i++) {
			if (plane_s[i] != plane_s_temp[i])
				return false;
			if (plane_t[i] != plane_t_temp[i])
				return false;
		}
		return true;
	}

}
