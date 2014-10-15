package Renderer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

import LDraw.Support.GLMatrixMath;
import LDraw.Support.LDrawGlobalFlag;
import LDraw.Support.MatrixMath;

import com.jogamp.common.nio.Buffers;

public class LDrawDLSession {
	public static final int INST_RING_BUFFER_COUNT = 4; // Number of VBOs to
	// rotate for hw
	// instancing - doesn't
	// actually help, it
	// turns out.
	
	/**
	 * @uml.property  name="dl_head"
	 * @uml.associationEnd  
	 */
	LDrawDL dl_head; // Linked list of all DLs that will be instance-drawn, with
						// count.
	/**
	 * @uml.property  name="dl_count"
	 */
	int dl_count;

	/**
	 * @uml.property  name="sorted_head"
	 * @uml.associationEnd  
	 */
	LDrawDLSortedInstanceLink sorted_head; // Linked list + count for DLs being
											// drawn later to Z sort.
	/**
	 * @uml.property  name="sort_count"
	 */
	int sort_count;

	/**
	 * @uml.property  name="model_view"
	 */
	float model_view[]; // Model-view matrix, used to Z sort translucent
						// objects.
	/**
	 * @uml.property  name="inst_ring"
	 */
	int inst_ring; // If using more than one instancing buffer, this tells which
					// one we use.
	
	// ========== LDrawDLSessionCreate
	// ================================================
	//
	// Purpose: Create a new drawing  Drawing sessions sit entirely in a
	// BDP
	// for speed - most of our linked lists are just null.
	//
	// ================================================================================
	public static IntBuffer inst_vbo_ring[] = new IntBuffer[INST_RING_BUFFER_COUNT];
	public static int inst_ring_last = 0;
	
	public LDrawDLSession(float model_view[]){
		dl_head = null;
		dl_count = 0;
		sorted_head = null;
		sort_count = 0;
		this.model_view = new float[16];
		
		System.arraycopy(model_view, 0, this.model_view, 0,
				model_view.length);
		inst_ring = inst_ring_last;
		// each session picks up a new buffer in the ring of instance buffers.
		inst_ring_last = (inst_ring_last + 1) % INST_RING_BUFFER_COUNT;
	}
	
	/**
	 * @return
	 * @uml.property  name="dl_head"
	 */
	public LDrawDL getDl_head() {
		return dl_head;
	}

	/**
	 * @param dl_head
	 * @uml.property  name="dl_head"
	 */
	public void setDl_head(LDrawDL dl_head) {
		this.dl_head = dl_head;
	}

	/**
	 * @return
	 * @uml.property  name="dl_count"
	 */
	public int getDl_count() {
		return dl_count;
	}

	/**
	 * @param dl_count
	 * @uml.property  name="dl_count"
	 */
	public void setDl_count(int dl_count) {
		this.dl_count = dl_count;
	}

	/**
	 * @return
	 * @uml.property  name="sorted_head"
	 */
	public LDrawDLSortedInstanceLink getSorted_head() {
		return sorted_head;
	}

	/**
	 * @param sorted_head
	 * @uml.property  name="sorted_head"
	 */
	public void setSorted_head(LDrawDLSortedInstanceLink sorted_head) {
		this.sorted_head = sorted_head;
	}

	/**
	 * @return
	 * @uml.property  name="sort_count"
	 */
	public int getSort_count() {
		return sort_count;
	}

	/**
	 * @param sort_count
	 * @uml.property  name="sort_count"
	 */
	public void setSort_count(int sort_count) {
		this.sort_count = sort_count;
	}

	/**
	 * @return
	 * @uml.property  name="model_view"
	 */
	public float[] getModel_view() {
		return model_view;
	}

	/**
	 * @param model_view
	 * @uml.property  name="model_view"
	 */
	public void setModel_view(float[] model_view) {
		this.model_view = model_view;
	}

	/**
	 * @return
	 * @uml.property  name="inst_ring"
	 */
	public int getInst_ring() {
		return inst_ring;
	}

	/**
	 * @param inst_ring
	 * @uml.property  name="inst_ring"
	 */
	public void setInst_ring(int inst_ring) {
		this.inst_ring = inst_ring;
	}

	// ========== LDrawDLSessionDrawAndDestroy
		// ========================================
		//
		// Purpose: Draw any DLs that were deferred during drawing, then nuke the
		// session object.
		//
		// ================================================================================
		public void drawAndDestroy(GL2 gl2) {
			LDrawDLInstance inst;
			LDrawDL dl;

			// INSTANCED DRAWING CASE

			if (dl_head != null) {
				// Build a var-sized array of segments to record our instances for
				// hardware instancing. We may not need it for every DL but that's
				// okay.
				LDrawDLSegment[] segments = new LDrawDLSegment[dl_count];
				for (LDrawDLSegment segment : segments)
					segment = new LDrawDLSegment();
				int segmentIndex = 0;
				LDrawDLSegment cur_segment = segments[segmentIndex];

				// If we do not yet have a VBO for instancing, build one now.
				if (inst_vbo_ring[inst_ring].get(0) == 0)
					gl2.glGenBuffers(1, inst_vbo_ring[inst_ring]);

				// Map our instance buffer so we can write instancing data.
				gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER,
						inst_vbo_ring[inst_ring].get(0));
				gl2.glBufferData(GL2.GL_ARRAY_BUFFER, LDrawDisplayList.INST_MAX_COUNT * Float.SIZE
						/ 8 * 24, null, GL2.GL_DYNAMIC_DRAW);
				ByteBuffer inst_base = gl2.glMapBuffer(GL2.GL_ARRAY_BUFFER,
						GL2.GL_WRITE_ONLY);
				float[] inst_data = inst_base.asFloatBuffer().array();
				int inst_remain = LDrawDisplayList.INST_MAX_COUNT;

				// Main loop 1: we will walk every instanced DL and either
				// accumulate its instances (for hardware instancing) or just draw
				// now
				// (For attribute instancing).
				while (dl_head != null) {
					dl = dl_head;

					if (dl.instance_count >= LDrawDisplayList.get_instance_cutoff(gl2)
							&& inst_remain >= dl.instance_count) {
						// If we have capacity for hw instancing and this DL is used
						// enough, create a segment record and fill it out.
						cur_segment.geo_vbo = dl.geo_vbo;
						if (LDrawGlobalFlag.WANT_SMOOTH != false) {
							cur_segment.idx_vbo = dl.idx_vbo;
						}
						cur_segment.dl = dl.texes[0];
						// todo
						// cur_segment.inst_base = null;
						// cur_segment.inst_base += (inst_data - inst_base);
						cur_segment.inst_count = dl.instance_count;

						// Now walk the instance list, copying the instances into
						// the instance VBO one by one.

						int inst_data_offset = 0;
						for (inst = dl.instance_head; inst != null; inst = inst.next) {
							MatrixMath.copy_vec4(inst_data, inst_data_offset, inst.color, 0);
							MatrixMath.copy_vec4(inst_data, inst_data_offset + 4, inst.comp, 0);
							inst_data[inst_data_offset + 8] = inst.transform[0]; // Note:
																					// copy
																					// on
																					// transpose
																					// to
																					// get
																					// matrix
																					// into
																					// right
																					// form!
							inst_data[inst_data_offset + 9] = inst.transform[4];
							inst_data[inst_data_offset + 10] = inst.transform[8];
							inst_data[inst_data_offset + 11] = inst.transform[12];
							inst_data[inst_data_offset + 12] = inst.transform[1];
							inst_data[inst_data_offset + 13] = inst.transform[5];
							inst_data[inst_data_offset + 14] = inst.transform[9];
							inst_data[inst_data_offset + 15] = inst.transform[13];
							inst_data[inst_data_offset + 16] = inst.transform[2];
							inst_data[inst_data_offset + 17] = inst.transform[6];
							inst_data[inst_data_offset + 18] = inst.transform[10];
							inst_data[inst_data_offset + 19] = inst.transform[14];
							inst_data[inst_data_offset + 20] = inst.transform[3];
							inst_data[inst_data_offset + 21] = inst.transform[7];
							inst_data[inst_data_offset + 22] = inst.transform[11];
							inst_data[inst_data_offset + 23] = inst.transform[15];
							inst_data_offset += 24;
							--inst_remain;
						}
						cur_segment = segments[++segmentIndex];

					} else {
						// Immediate mode instancing - we draw now! So bind up the
						// mesh of this DL.
						gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, dl.geo_vbo.get(0));
						if (LDrawGlobalFlag.WANT_SMOOTH != false) {
							gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,
									dl.idx_vbo.get(0));
						}
						// float * p = null;
						ByteBuffer p = Buffers
								.newDirectByteBuffer(10 * Float.SIZE / 8);
						gl2.glVertexAttribPointer(
								AttributeT.attr_position.getValue(), 3,
								GL2.GL_FLOAT, false, LDrawDisplayList.VERT_STRIDE * Float.SIZE / 8,
								p);
						gl2.glVertexAttribPointer(
								AttributeT.attr_normal.getValue(), 3, GL2.GL_FLOAT,
								false, LDrawDisplayList.VERT_STRIDE * Float.SIZE / 8, p);
						gl2.glVertexAttribPointer(AttributeT.attr_color.getValue(),
								4, GL2.GL_FLOAT, false, LDrawDisplayList.VERT_STRIDE * Float.SIZE
										/ 8, p);

						// Now walk the instance list...push instance data into
						// attributes in immediate mode and draw.
						for (inst = dl.instance_head; inst != null; inst = inst.next) {

							int i;
							for (i = 0; i < 4; ++i)
								gl2.glVertexAttrib4f(
										AttributeT.attr_transform_x.getValue() + i,
										inst.transform[i], inst.transform[4 + i],
										inst.transform[8 + i],
										inst.transform[12 + i]);
							gl2.glVertexAttrib4fv(
									AttributeT.attr_color_current.getValue(),
									inst.color, 0);
							gl2.glVertexAttrib4fv(
									AttributeT.attr_color_compliment.getValue(),
									inst.comp, 0);

							LDrawDLPerTex tptr = dl.texes[0];

							if (LDrawGlobalFlag.WANT_SMOOTH != false) {
								if (tptr.line_count != 0)
									gl2.glDrawElements(GL2.GL_LINES,
											tptr.line_count, GL2.GL_UNSIGNED_INT,
											tptr.line_off);
								if (tptr.tri_count != 0)
									gl2.glDrawElements(GL2.GL_TRIANGLES,
											tptr.tri_count, GL2.GL_UNSIGNED_INT,
											tptr.tri_off);
								if (tptr.quad_count != 0)
									gl2.glDrawElements(GL2.GL_QUADS,
											tptr.quad_count, GL2.GL_UNSIGNED_INT,
											tptr.quad_off);

							}
						}
					}

					dl.instance_head = dl.instance_tail = null;
					dl.instance_count = 0;
					if (dl.flags.get(LDrawDLT.dl_needs_destroy)) {
						dl.destroy(gl2);
					}
					dl_head = dl.next_dl;
					dl.next_dl = null;
				}

				// Hardware instancing: unmap our hardware instance buffer and if we
				// got data,
				// set up the GPU for hardware instancing.

				gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER,
						inst_vbo_ring[inst_ring].get(0));
				gl2.glUnmapBuffer(GL2.GL_ARRAY_BUFFER);

				if (segments[segmentIndex] != cur_segment) {
					gl2.glEnableVertexAttribArray(AttributeT.attr_transform_x
							.getValue());
					gl2.glEnableVertexAttribArray(AttributeT.attr_transform_y
							.getValue());
					gl2.glEnableVertexAttribArray(AttributeT.attr_transform_z
							.getValue());
					gl2.glEnableVertexAttribArray(AttributeT.attr_transform_w
							.getValue());
					gl2.glEnableVertexAttribArray(AttributeT.attr_color_current
							.getValue());
					gl2.glEnableVertexAttribArray(AttributeT.attr_color_compliment
							.getValue());
					// todo
					// gl2.glVertexAttribDivisorARB(AttributeT.attr_transform_x.getValue(),1);
					// gl2.glVertexAttribDivisorARB(AttributeT.attr_transform_y.getValue(),1);
					// gl2.glVertexAttribDivisorARB(AttributeT.attr_transform_z.getValue(),1);
					// gl2.glVertexAttribDivisorARB(AttributeT.attr_transform_w.getValue(),1);
					// gl2.glVertexAttribDivisorARB(AttributeT.attr_color_current.getValue(),1);
					// gl2.glVertexAttribDivisorARB(AttributeT.attr_color_compliment.getValue(),1);

					// Main loop 2 over DLs - for each DL that had hw-instances we
					// built a segment
					// in our array. Bind the DL itself, as well as the instance
					// pointers, and do an instanced-draw.

					LDrawDLSegment s;
					segmentIndex = 0;
					for (s = segments[0]; s != cur_segment; s = segments[++segmentIndex]) {

						gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, s.geo_vbo.get(0));
						if (LDrawGlobalFlag.WANT_SMOOTH != false)
							gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,
									s.idx_vbo.get(0));

						// float [] p = null;
						ByteBuffer p = Buffers
								.newDirectByteBuffer(10 * Float.SIZE / 8);
						gl2.glVertexAttribPointer(
								AttributeT.attr_position.getValue(), 3,
								GL2.GL_FLOAT, false, LDrawDisplayList.VERT_STRIDE * Float.SIZE / 8,
								p);
						gl2.glVertexAttribPointer(
								AttributeT.attr_normal.getValue(), 3, GL2.GL_FLOAT,
								false, LDrawDisplayList.VERT_STRIDE * Float.SIZE / 8, p);
						gl2.glVertexAttribPointer(AttributeT.attr_color.getValue(),
								4, GL2.GL_FLOAT, false, LDrawDisplayList.VERT_STRIDE * Float.SIZE
										/ 8, p);

						gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER,
								inst_vbo_ring[inst_ring].get());

						p = ByteBuffer.allocate(4 * Float.SIZE / 8);
						gl2.glVertexAttribPointer(
								AttributeT.attr_color_current.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						for (int i = 0; i < 4 * Float.SIZE / 8; i++)
							s.inst_base.put(0 + i, p.get(i));
						p.clear();
						gl2.glVertexAttribPointer(
								AttributeT.attr_color_compliment.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						gl2.glVertexAttribPointer(
								AttributeT.attr_color_current.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						for (int i = 0; i < 4 * Float.SIZE / 8; i++)
							s.inst_base.put(4 + i, p.get(i));
						p.clear();
						gl2.glVertexAttribPointer(
								AttributeT.attr_transform_x.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						gl2.glVertexAttribPointer(
								AttributeT.attr_color_current.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						for (int i = 0; i < 4 * Float.SIZE / 8; i++)
							s.inst_base.put(8 + i, p.get(i));
						p.clear();
						gl2.glVertexAttribPointer(
								AttributeT.attr_transform_y.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						gl2.glVertexAttribPointer(
								AttributeT.attr_color_current.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						for (int i = 0; i < 4 * Float.SIZE / 8; i++)
							s.inst_base.put(12 + i, p.get(i));
						p.clear();
						gl2.glVertexAttribPointer(
								AttributeT.attr_transform_z.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						gl2.glVertexAttribPointer(
								AttributeT.attr_color_current.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						for (int i = 0; i < 4 * Float.SIZE / 8; i++)
							s.inst_base.put(16 + i, p.get(i));
						p.clear();
						gl2.glVertexAttribPointer(
								AttributeT.attr_transform_w.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						gl2.glVertexAttribPointer(
								AttributeT.attr_color_current.getValue(), 4,
								GL2.GL_FLOAT, false, 24 * Float.SIZE / 8, p);
						for (int i = 0; i < 4 * Float.SIZE / 8; i++)
							s.inst_base.put(20 + i, p.get(i));
						p.clear();

						if (LDrawGlobalFlag.WANT_SMOOTH != false) {
							if (s.dl.line_count != 0)
								gl2.glDrawElementsInstanced(GL2.GL_LINES,
										s.dl.line_count, GL2.GL_UNSIGNED_INT,
										s.dl.line_off, s.inst_count);
							if (s.dl.tri_count != 0)
								gl2.glDrawElementsInstanced(GL2.GL_TRIANGLES,
										s.dl.tri_count, GL2.GL_UNSIGNED_INT,
										s.dl.tri_off, s.inst_count);
							if (s.dl.quad_count != 0)
								gl2.glDrawElementsInstanced(GL2.GL_QUADS,
										s.dl.quad_count, GL2.GL_UNSIGNED_INT,
										s.dl.quad_off, s.inst_count);
						} else {
							if (s.dl.line_count != 0)
								gl2.glDrawArraysInstanced(GL2.GL_LINES,
										s.dl.line_off, s.dl.line_count,
										s.inst_count);
							if (s.dl.tri_count != 0)
								gl2.glDrawArraysInstanced(GL2.GL_TRIANGLES,
										s.dl.tri_off, s.dl.tri_count, s.inst_count);
							if (s.dl.quad_count != 0)
								gl2.glDrawArraysInstanced(GL2.GL_QUADS,
										s.dl.quad_off, s.dl.quad_count,
										s.inst_count);
						}
					}

					gl2.glDisableVertexAttribArray(AttributeT.attr_transform_x
							.getValue());
					gl2.glDisableVertexAttribArray(AttributeT.attr_transform_y
							.getValue());
					gl2.glDisableVertexAttribArray(AttributeT.attr_transform_z
							.getValue());
					gl2.glDisableVertexAttribArray(AttributeT.attr_transform_w
							.getValue());
					gl2.glDisableVertexAttribArray(AttributeT.attr_color_current
							.getValue());
					gl2.glDisableVertexAttribArray(AttributeT.attr_color_compliment
							.getValue());
					// todo
					// gl2.glVertexAttribDivisorARB(attr_transform_x,0);
					// gl2.glVertexAttribDivisorARB(attr_transform_y,0);
					// gl2.glVertexAttribDivisorARB(attr_transform_z,0);
					// gl2.glVertexAttribDivisorARB(attr_transform_w,0);
					// gl2.glVertexAttribDivisorARB(attr_color_current,0);
					// gl2.glVertexAttribDivisorARB(attr_color_compliment,0);

				}

			}

			// MAIN LOOP 3: sorted deferred drawing (!)

			LDrawDLSortedInstanceLink l;
			if (sorted_head != null) {
				// If we have any sorting to do, allocate an array of the size of
				// all sorted geometry for sorting purposes.
				LDrawDLSortedInstanceLink arr[] = new LDrawDLSortedInstanceLink[sort_count];
				for (int i = 0; i < sort_count; i++)
					arr[i] = new LDrawDLSortedInstanceLink();
				LDrawDLSortedInstanceLink p = arr[0];
				int index_arr = 0;

				// Copy each sorted instance into our array. "Eval" is the
				// measurement of distance - calculate eye-space Z and use that.
				for (l = sorted_head; l != null; l = l.next) {
					float v[] = { l.transform[12], l.transform[13],
							l.transform[14], 1.0f };
					try {
						arr[index_arr] = (LDrawDLSortedInstanceLink) l.clone();
					} catch (CloneNotSupportedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					p = arr[index_arr];

					// memcpy(p,l,sizeof(LDrawDLSortedInstanceLink));
					float v_eye[] = new float[4];
					GLMatrixMath.applyMatrix(v_eye, model_view, v);
					p.eval = v_eye[2];
					p = arr[++index_arr];
				}

				// Now: sort our array ascending to get far to near in eye space.
				// todo
				// qsort(arr,sort_count,sizeof(LDrawDLSortedInstanceLink),compare_sorted_link);

				// NOW we can walk our sorted array and draw each brick, 1x1. This
				// code is a rehash of the "draw now"
				// code in LDrawDLDraw and could be factored.
				index_arr = 0;
				l = arr[index_arr];
				int lc;
				for (lc = 0; lc < sort_count; ++lc) {
					int i;
					for (i = 0; i < 4; ++i)
						gl2.glVertexAttrib4f(AttributeT.attr_transform_x.getValue()
								+ i, l.transform[i], l.transform[4 + i],
								l.transform[8 + i], l.transform[12 + i]);
					gl2.glVertexAttrib4fv(AttributeT.attr_color_current.getValue(),
							l.color, 0);
					gl2.glVertexAttrib4fv(
							AttributeT.attr_color_compliment.getValue(), l.comp, 0);

					dl = l.dl;
					gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, dl.geo_vbo.get(0));
					if (LDrawGlobalFlag.WANT_SMOOTH != false)
						gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER,
								dl.idx_vbo.get(0));

					ByteBuffer p2 = ByteBuffer.allocate(LDrawDisplayList.VERT_STRIDE * Float.SIZE
							/ 8);
					gl2.glVertexAttribPointer(AttributeT.attr_position.getValue(),
							3, GL2.GL_FLOAT, false, LDrawDisplayList.VERT_STRIDE * Float.SIZE / 8,
							p2);
					gl2.glVertexAttribPointer(AttributeT.attr_normal.getValue(), 3,
							GL2.GL_FLOAT, false, LDrawDisplayList.VERT_STRIDE * Float.SIZE / 8, p2);
					gl2.glVertexAttribPointer(AttributeT.attr_color.getValue(), 4,
							GL2.GL_FLOAT, false, LDrawDisplayList.VERT_STRIDE * Float.SIZE / 8, p2);

					LDrawDLPerTex tptr = dl.texes[0];
					int tptrIndex = 0;

					int t;
					for (t = 0; t < dl.tex_count; ++t, tptr = dl.texes[++tptrIndex]) {
						if (tptr.spec.tex_obj != 0) {
							LDrawDisplayList.setup_tex_spec(gl2, tptr.spec);
						} else
							LDrawDisplayList.setup_tex_spec(gl2, l.spec);

						if (LDrawGlobalFlag.WANT_SMOOTH != false) {
							if (tptr.line_count != 0)
								gl2.glDrawElements(GL2.GL_LINES, tptr.line_count,
										GL2.GL_UNSIGNED_INT, tptr.line_off);
							if (tptr.tri_count != 0)
								gl2.glDrawElements(GL2.GL_TRIANGLES,
										tptr.tri_count, GL2.GL_UNSIGNED_INT,
										tptr.tri_off);
							if (tptr.quad_count != 0)
								gl2.glDrawElements(GL2.GL_QUADS, tptr.quad_count,
										GL2.GL_UNSIGNED_INT, tptr.quad_off);
						} else {
							if (tptr.line_count != 0)
								gl2.glDrawArrays(GL2.GL_LINES, tptr.line_off,
										tptr.line_count);
							if (tptr.tri_count != 0)
								gl2.glDrawArrays(GL2.GL_TRIANGLES, tptr.tri_off,
										tptr.tri_count);
							if (tptr.quad_count != 0)
								gl2.glDrawArrays(GL2.GL_QUADS, tptr.quad_off,
										tptr.quad_count);
						}
					}
					l = l.next;
				}
			}

			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, 0);
			if (LDrawGlobalFlag.WANT_SMOOTH != false) {
				gl2.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, 0);
			}

			// Finally done - all allocations for session (including our own obj)
			// come from a BDP, so cleanup is quick.
			// Instance VBO remains to be reused.
			// DLs themselves live on beyond 
			// LDrawBDPDestroy(alloc);

		}// end LDrawDLSessionDrawAndDestroy
		
}
