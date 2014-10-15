package Renderer;

public class LDrawDLBuilderPerTex{
	/**
	 * @uml.property  name="next"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderPerTex	next;
	/**
	 * @uml.property  name="spec"
	 * @uml.associationEnd  
	 */
	LDrawTextureSpec				spec;
	/**
	 * @uml.property  name="tri_head"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderVertexLink 	tri_head;
	/**
	 * @uml.property  name="tri_tail"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderVertexLink 	tri_tail;
	/**
	 * @uml.property  name="quad_head"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderVertexLink 	quad_head;
	/**
	 * @uml.property  name="quad_tail"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderVertexLink 	quad_tail;
	/**
	 * @uml.property  name="line_head"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderVertexLink 	line_head;
	/**
	 * @uml.property  name="line_tail"
	 * @uml.associationEnd  
	 */
	LDrawDLBuilderVertexLink 	line_tail;
	
	public LDrawDLBuilderPerTex(){
		next = null;
		spec = new LDrawTextureSpec();
		tri_head = null;
		tri_tail = null;
		quad_head = null;
		quad_tail = null;
		line_head = null;
		line_tail = null;
	}
	/**
	 * @return
	 * @uml.property  name="next"
	 */
	public LDrawDLBuilderPerTex getNext() {
		return next;
	}
	/**
	 * @param next
	 * @uml.property  name="next"
	 */
	public void setNext(LDrawDLBuilderPerTex next) {
		this.next = next;
	}
	/**
	 * @return
	 * @uml.property  name="spec"
	 */
	public LDrawTextureSpec getSpec() {
		return spec;
	}
	/**
	 * @param spec
	 * @uml.property  name="spec"
	 */
	public void setSpec(LDrawTextureSpec spec) {
		this.spec = spec;
	}
	/**
	 * @return
	 * @uml.property  name="tri_head"
	 */
	public LDrawDLBuilderVertexLink getTri_head() {
		return tri_head;
	}
	/**
	 * @param tri_head
	 * @uml.property  name="tri_head"
	 */
	public void setTri_head(LDrawDLBuilderVertexLink tri_head) {
		this.tri_head = tri_head;
	}
	/**
	 * @return
	 * @uml.property  name="tri_tail"
	 */
	public LDrawDLBuilderVertexLink getTri_tail() {
		return tri_tail;
	}
	/**
	 * @param tri_tail
	 * @uml.property  name="tri_tail"
	 */
	public void setTri_tail(LDrawDLBuilderVertexLink tri_tail) {
		this.tri_tail = tri_tail;
	}
	/**
	 * @return
	 * @uml.property  name="quad_head"
	 */
	public LDrawDLBuilderVertexLink getQuad_head() {
		return quad_head;
	}
	/**
	 * @param quad_head
	 * @uml.property  name="quad_head"
	 */
	public void setQuad_head(LDrawDLBuilderVertexLink quad_head) {
		this.quad_head = quad_head;
	}
	/**
	 * @return
	 * @uml.property  name="quad_tail"
	 */
	public LDrawDLBuilderVertexLink getQuad_tail() {
		return quad_tail;
	}
	/**
	 * @param quad_tail
	 * @uml.property  name="quad_tail"
	 */
	public void setQuad_tail(LDrawDLBuilderVertexLink quad_tail) {
		this.quad_tail = quad_tail;
	}
	/**
	 * @return
	 * @uml.property  name="line_head"
	 */
	public LDrawDLBuilderVertexLink getLine_head() {
		return line_head;
	}
	/**
	 * @param line_head
	 * @uml.property  name="line_head"
	 */
	public void setLine_head(LDrawDLBuilderVertexLink line_head) {
		this.line_head = line_head;
	}
	/**
	 * @return
	 * @uml.property  name="line_tail"
	 */
	public LDrawDLBuilderVertexLink getLine_tail() {
		return line_tail;
	}
	/**
	 * @param line_tail
	 * @uml.property  name="line_tail"
	 */
	public void setLine_tail(LDrawDLBuilderVertexLink line_tail) {
		this.line_tail = line_tail;
	}

	
}
