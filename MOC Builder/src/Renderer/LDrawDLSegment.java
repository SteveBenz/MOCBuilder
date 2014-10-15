package Renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class LDrawDLSegment {
	/**
	 * @uml.property  name="geo_vbo"
	 */
	IntBuffer geo_vbo; // VBO of the brick we are going to draw - contains the actual
					// brick mesh.
	/**
	 * @uml.property  name="idx_vbo"
	 */
	IntBuffer idx_vbo;
	/**
	 * @uml.property  name="dl"
	 * @uml.associationEnd  
	 */
	LDrawDLPerTex dl; // Ptr to the per-tex info for that brick - only untexed
						// bricks get instanced, so we only have one "per tex",
						// by definition.
	/**
	 * @uml.property  name="inst_base"
	 */
	ByteBuffer inst_base; // VBO-relative ptr to the instance data base in the
						// instance VBO.
	/**
	 * @uml.property  name="inst_count"
	 */
	int inst_count; // Number of instances startingat that offset.
	public LDrawDLSegment(int dl_count) {
		// TODO Auto-generated constructor stub
	}
	public LDrawDLSegment() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return
	 * @uml.property  name="geo_vbo"
	 */
	public IntBuffer getGeo_vbo() {
		return geo_vbo;
	}
	/**
	 * @param geo_vbo
	 * @uml.property  name="geo_vbo"
	 */
	public void setGeo_vbo(IntBuffer geo_vbo) {
		this.geo_vbo = geo_vbo;
	}
	/**
	 * @return
	 * @uml.property  name="idx_vbo"
	 */
	public IntBuffer getIdx_vbo() {
		return idx_vbo;
	}
	/**
	 * @param idx_vbo
	 * @uml.property  name="idx_vbo"
	 */
	public void setIdx_vbo(IntBuffer idx_vbo) {
		this.idx_vbo = idx_vbo;
	}
	/**
	 * @return
	 * @uml.property  name="dl"
	 */
	public LDrawDLPerTex getDl() {
		return dl;
	}
	/**
	 * @param dl
	 * @uml.property  name="dl"
	 */
	public void setDl(LDrawDLPerTex dl) {
		this.dl = dl;
	}
	/**
	 * @return
	 * @uml.property  name="inst_base"
	 */
	public ByteBuffer getInst_base() {
		return inst_base;
	}
	/**
	 * @param inst_base
	 * @uml.property  name="inst_base"
	 */
	public void setInst_base(ByteBuffer inst_base) {
		this.inst_base = inst_base;
	}
	/**
	 * @return
	 * @uml.property  name="inst_count"
	 */
	public int getInst_count() {
		return inst_count;
	}
	/**
	 * @param inst_count
	 * @uml.property  name="inst_count"
	 */
	public void setInst_count(int inst_count) {
		this.inst_count = inst_count;
	}	
}
