package Renderer;

import java.nio.FloatBuffer;

import javax.media.opengl.GL2;

/*

 LDrawDisplayList - THEORY OF OPERATION

 This API provides display-list-like capabilities for a GL2.GL 2.0 shader/VBO-based renderer.
 Our display lists consist of a mesh of lines, quads, and tris, some of which may be textured,
 and all of which have normals and colors.  The colors are straight RGBA tuples - the color stack
 behavior that we get in the shader renderer comes from the higher level code.

 Unlike the GL2.GL, we can open more than one display list for construction at a time - a 
 LDrawDLBuilder opaque ptr gives us a context for DL creation.  When we close the
 builder we get a DL, or null if the DL would have been empty.  

 SESSIONS

 We draw one or more DLs using a drawing 'session', which is also an opaque ptr (just
 like the rest of the API).  The session accumulates requests to draw with given color/transform
 texture state and performs optimizations to improve drawing performance.  When the session is
 destroyed, any 'deferred' drawing takes place.

 Besides attempting to use hw instancing, the session will also draw translucent DLs last in 
 back-to-front order to improve transparency performance.

 FEATURES

 The DL API will draw translucent geomtry back-to-front ordered (the DLs are reordered, not the
 geometry in the DLs).  The API dynamically detects translucency from passed in colors, including
 meta-colors  (Meta colors are assumed to have alpha=0.0f).

 The API will draw non-textured, non-translucent geometry via instancing, either 
 attribute-instancing for small count or hardware instancing with attrib-array-divisor for large
 numbers of bricks.	

 */
/*

 INSTANCING IMPLEMENTATION NOTES

 Instancing is just fancy talk for drawing one thing many times in some efficient way.  Instancing is a good fit for BrickSmith
 because we draw the same bricks over and over and over again.

 When we instance, we identify the 'per instance' data - that is, data that is different for every instance.  In the case of
 BrickSmith, the current/compliment color and transform are per instance data; the mesh and non-meta colors of the mesh are invariant.

 (As an example, when drawing the plate with red wheels, the red color of the wheels and the shape of the part are invariant;
 the current color used for the plate and the location of the whole part are per-instance data.)

 "Attribute" instancing means changing the instance data by changing GL2.GL attributes.  The theory is that the GL2.GL can change attribute
 data faster than uniform data, so changing the current location and color via attributes should be quite cheap.

 "Hardware" instancing implies using one of the native GL2.GL instancing APIs like GL2.GL_ARB_instanced_arrays.  In this case, we put our instance
 attributes into their own VBO (of consecutive interleaved "instances"), give the GL2.GL the base pointer and tell it to draw N copies of
 our mesh, using the instanced data from the VBO.

 When hardware instancing works right, it can lead to much higher throughput than attributes, which are faster in turn than uniforms.
 In practice, this is hugely dependent on what driver we're running on.

 DEFERRING DRAWING FOR INSTANCING

 When a DL can be drawn via instancing (either attribute or hw) it is not drawn - it is saved on the session; when the session is
 destroyed we draw out every DL we have deferred, in order (e.g. all instances of one DL) to avoid swapping VBOs.

 During that deferred draw-out we either build a hw instance list or simply draw.

 DEFERRED DARWING FOR Z SORTING

 When a DL does not have to be drawn immediately and has translucency, we always try to save it to the sorted list.

 Then when the session is destroyed, we sort all of these "sort-deferred" DLs by their local origin and draw back to front.
 This helps keep translucency looking good.

 */

public class LDrawDisplayList {
	

	public static final int VERT_STRIDE = 10; // Stride of our vertices - we
												// always write X Y Z NX NY NZ R
												// G B A
	public static final int INST_CUTOFF = 5; // Minimum instances to use hw
												// case, which has higher
												// overhead to set up.
	public static final int INST_MAX_COUNT = (1024 * 128); // Maximum instances
															// to write per draw
															// before going to
															// immediate mode -
															// avoids unbounded
															// VRAM use.
	

	// ========== get_instance_cutoff
	// =================================================
	//
	// Purpose: Determine whether we can use hardware instancing.
	//
	// Notes: Pre-DX10 Mac GPUs on older operating systems don't support
	// instancing;
	// This routine checks for the GL2.GL_ARB_instanced_arrays extension string,
	// which will always be present since we are using legacy 2.1-style
	// contexts. (If we go to the core profile we'll need to also look at the
	// GL2.GL version.)
	//
	// If the hardware won't instance, we simply set the instancing min limit
	// to an insanely high limit so that we never hit that case.
	//
	// ================================================================================
	public static int has_instancing = -1;
	public static int get_instance_cutoff(GL2 gl2) {
		if (has_instancing == -1) {
			String ext_str = gl2.glGetString(GL2.GL_EXTENSIONS);
			if (ext_str.indexOf("GL2.GL_ARB_instanced_arrays") != -1)
				has_instancing = 1;
			else
				has_instancing = 0;
		}
		return has_instancing != 0 ? INST_CUTOFF : Integer.MAX_VALUE;
	}


	// ========== setup_tex_spec
	// ======================================================
	//
	// Purpose: Set up the GL2.GL with texturing info.
	//
	// Ntes: DL implementation uses object-plane coordinate generation; when a
	// sub-DL inherits a projection, that projection is transformed with the
	// sub-DL to keep things in sync.
	//
	// The attr_texture_mix attribute controls whether the texture is visible
	// or not - a temporary hack until we can get a clear texture.
	//
	// ================================================================================
	public static void setup_tex_spec(GL2 gl2, LDrawTextureSpec spec) {

		if (spec != null && spec.tex_obj != 0) {
			gl2.glVertexAttrib1f(AttributeT.attr_texture_mix.getValue(), 1.0f);
			gl2.glBindTexture(GL2.GL_TEXTURE_2D, spec.tex_obj);
			gl2.glTexGenfv(GL2.GL_S, GL2.GL_OBJECT_PLANE,
					FloatBuffer.wrap(spec.plane_s));
			gl2.glTexGenfv(GL2.GL_T, GL2.GL_OBJECT_PLANE,
					FloatBuffer.wrap(spec.plane_t));
		} else {
			gl2.glVertexAttrib1f(AttributeT.attr_texture_mix.getValue(), 0.0f);
			// TODO: what texture IS bound when "untextured"? We should
			// set up a 'white' texture 1x1 pixel so that (1) our texture state
			// is not illegal and (2) we waste NO bandwidth on texturing.
			// gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
		}
	}// end setup_tex_spec


	// ========== compare_sorted_link
	// =================================================
	//
	// Purpose: Functor to compare two sorted instances by their "eval" value,
	// which
	// is eye space Z right now. API fits C qsort.
	//
	// ================================================================================
	public static int compare_sorted_link(LDrawDLSortedInstanceLink lhs,
			LDrawDLSortedInstanceLink rhs) {
		float value = lhs.eval - rhs.eval;
		if (value == 0)
			if (lhs.eval > rhs.eval)
				return 1;
			else if (lhs.eval == rhs.eval)
				return 0;
			else
				return -1;
		return (int) value;
	}// end compare_sorted_link

	

}
