package Renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Locale;

import LDraw.Support.Range;

public class Mesh {
	/**
	 * @uml.property name="vertex_count"
	 */
	int vertex_count; // Number of vertices so far.
	/**
	 * @uml.property name="vertex_capacity"
	 */
	int vertex_capacity; // Number of vertices we have storage for in our vertex
							// array.
	/**
	 * @uml.property name="unique_vertex_count"
	 */
	int unique_vertex_count;// Number of actual unique vertices after merging
							// for export.
	/**
	 * @uml.property name="vertices"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	ArrayList<Vertex> vertices; // malloc'd array of vertices.

	/**
	 * @uml.property name="face_count"
	 */
	int face_count; // Number of total faces so far.
	/**
	 * @uml.property name="tri_count"
	 */
	int tri_count; // Number of triangle faces??
	/**
	 * @uml.property name="quad_count"
	 */
	int quad_count; // Number of quad faces.
	/**
	 * @uml.property name="poly_count"
	 */
	int poly_count; // Number of quad + triangle faces.
	/**
	 * @uml.property name="line_count"
	 */
	int line_count; // Number of line faces. Lines must be AFTER quads and tris.
	/**
	 * @uml.property name="face_capacity"
	 */
	int face_capacity; // Face capacity reserved in array.
	/**
	 * @uml.property name="faces"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	Face[] faces; // Malloc'd face memory.

	/**
	 * @uml.property name="rtree"
	 * @uml.associationEnd
	 */
	RTree rtree;// index; // Root node of r-tree that indexes vertices.

	// todo
	// #if DEBUG
	// int flags; // For debugging, we can flag various conditions that aren't
	// errors but are strange (due to LDraw precision issues).
	// #endif
	/**
	 * @uml.property name="highest_tid"
	 */
	int highest_tid; // Highest TID - we have this + 1 total textures in this
						// mesh.

	// Create a new mesh to smooth. You must pass in the _exact_ number of
	// tris,
	// quads and lines that you will later pass in.
	public Mesh(int tri_count, int quad_count, int line_count) {
		vertex_count = 0;
		vertex_capacity = tri_count * 3 + quad_count * 4 + line_count * 2;
		vertices = new ArrayList<Vertex>(vertex_capacity);

		for (int i = 0; i < vertex_capacity; i++)
			vertices.add(new Vertex());

		face_count = 0;
		face_capacity = tri_count + quad_count + line_count;
		poly_count = tri_count + quad_count;
		this.line_count = line_count;
		this.tri_count = tri_count;
		this.quad_count = quad_count;

		faces = new Face[face_capacity];
		for (int i = 0; i < face_capacity; i++) {
			faces[i] = new Face();
		}

		highest_tid = 0;
	}

	/**
	 * @return
	 * @uml.property name="vertex_count"
	 */
	public int getVertex_count() {
		return vertex_count;
	}

	/**
	 * @param vertex_count
	 * @uml.property name="vertex_count"
	 */
	public void setVertex_count(int vertex_count) {
		this.vertex_count = vertex_count;
	}

	/**
	 * @return
	 * @uml.property name="vertex_capacity"
	 */
	public int getVertex_capacity() {
		return vertex_capacity;
	}

	/**
	 * @param vertex_capacity
	 * @uml.property name="vertex_capacity"
	 */
	public void setVertex_capacity(int vertex_capacity) {
		this.vertex_capacity = vertex_capacity;
	}

	/**
	 * @return
	 * @uml.property name="unique_vertex_count"
	 */
	public int getUnique_vertex_count() {
		return unique_vertex_count;
	}

	/**
	 * @param unique_vertex_count
	 * @uml.property name="unique_vertex_count"
	 */
	public void setUnique_vertex_count(int unique_vertex_count) {
		this.unique_vertex_count = unique_vertex_count;
	}

	/**
	 * @return
	 * @uml.property name="vertices"
	 */
	public ArrayList<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * @param vertices
	 * @uml.property name="vertices"
	 */
	public void setVertices(ArrayList<Vertex> vertices) {
		this.vertices = vertices;
	}

	/**
	 * @return
	 * @uml.property name="face_count"
	 */
	public int getFace_count() {
		return face_count;
	}

	/**
	 * @param face_count
	 * @uml.property name="face_count"
	 */
	public void setFace_count(int face_count) {
		this.face_count = face_count;
	}

	/**
	 * @return
	 * @uml.property name="tri_count"
	 */
	public int getTri_count() {
		return tri_count;
	}

	/**
	 * @param tri_count
	 * @uml.property name="tri_count"
	 */
	public void setTri_count(int tri_count) {
		this.tri_count = tri_count;
	}

	/**
	 * @return
	 * @uml.property name="quad_count"
	 */
	public int getQuad_count() {
		return quad_count;
	}

	/**
	 * @param quad_count
	 * @uml.property name="quad_count"
	 */
	public void setQuad_count(int quad_count) {
		this.quad_count = quad_count;
	}

	/**
	 * @return
	 * @uml.property name="poly_count"
	 */
	public int getPoly_count() {
		return poly_count;
	}

	/**
	 * @param poly_count
	 * @uml.property name="poly_count"
	 */
	public void setPoly_count(int poly_count) {
		this.poly_count = poly_count;
	}

	/**
	 * @return
	 * @uml.property name="line_count"
	 */
	public int getLine_count() {
		return line_count;
	}

	/**
	 * @param line_count
	 * @uml.property name="line_count"
	 */
	public void setLine_count(int line_count) {
		this.line_count = line_count;
	}

	/**
	 * @return
	 * @uml.property name="face_capacity"
	 */
	public int getFace_capacity() {
		return face_capacity;
	}

	/**
	 * @param face_capacity
	 * @uml.property name="face_capacity"
	 */
	public void setFace_capacity(int face_capacity) {
		this.face_capacity = face_capacity;
	}

	/**
	 * @return
	 * @uml.property name="faces"
	 */
	public Face[] getFaces() {
		return faces;
	}

	/**
	 * @param faces
	 * @uml.property name="faces"
	 */
	public void setFaces(Face[] faces) {
		this.faces = faces;
	}

	/**
	 * @return
	 * @uml.property name="rtree"
	 */
	public RTree getRtree() {
		return rtree;
	}

	/**
	 * @param rtree
	 * @uml.property name="rtree"
	 */
	public void setRtree(RTree rtree) {
		this.rtree = rtree;
	}

	/**
	 * @return
	 * @uml.property name="highest_tid"
	 */
	public int getHighest_tid() {
		return highest_tid;
	}

	/**
	 * @param highest_tid
	 * @uml.property name="highest_tid"
	 */
	public void setHighest_tid(int highest_tid) {
		this.highest_tid = highest_tid;
	}

	// Add one face to the mesh. Quads and tris can be added in any order but
	// all
	// quads and tris (polygons) must be added before all lines.
	// When passing a face, simply passnull for any 'extra' vertices - that
	// is,
	// to create a line, passnull for p3 and p4; to create a triangle, pass
	// null for
	// p3. The color is the color of the entire face in RGBA; the face normal
	// is
	// computed for you.
	//
	// tid is the 'texture ID', a 0-based counted number identifying which
	// texture
	// state this face gets. Texture IDs must be consecutive, zero based and
	// positive, but do not need to be submitted in any particular order, and
	// the
	// highest TID does not have to be pre-declared; the library simply
	// watches
	// the TIDs on input.
	//
	// Technically lines have TIDs as well - typically TID 0 is used to mean
	// the
	// 'untextured texture group' and is used for lines and untextured
	// polygons.
	//
	// The TIDs are used to output sets of draw commands that share common
	// texture state -
	// that is, faces, quads and lines are ouput in TID order.
	public void add_face(float p1[], float p2[], float p3[], float p4[],
			float color[], int tid) {
		int i;
		// grab a new face, grab verts for it
		Face f = faces[face_count++];
		f.setTid(tid);
		if (tid > highest_tid)
			highest_tid = tid;
		if (p3 != null) {
			float v1[] = { p2[0] - p1[0], p2[1] - p1[1], p2[2] - p1[2] };
			float v2[] = { p3[0] - p1[0], p3[1] - p1[1], p3[2] - p1[2] };
			MeshSmooth.vec3_cross(f.getNormal(), v1, v2);
			MeshSmooth.vec3f_normalize(f.getNormal());
		} else {
			float normal[] = f.getNormal();
			normal[0] = normal[2] = 0.0f;
			normal[1] = 1.0f;
		}

		f.setDegree(p4 != null ? 4 : (p3 != null ? 3 : 2));

		f.setVertexAtIndex(0, vertices.get(vertex_count++));
		f.setVertexAtIndex(1, vertices.get(vertex_count++));

		if (p3 != null)
			f.setVertexAtIndex(2, vertices.get(vertex_count++));
		else
			f.setVertexAtIndex(2, null);
		if (p4 != null)
			f.setVertexAtIndex(3, vertices.get(vertex_count++));
		else
			f.setVertexAtIndex(3, null);

		Vertex[] vertex = f.getVertex();
		Face[] neighbor = f.getNeighbor();
		neighbor[0] = neighbor[1] = neighbor[2] = neighbor[3] = null;
		f.setNeighbor(neighbor);

		VertexInsert[] t_list = f.getT_list();
		t_list[0] = t_list[1] = t_list[2] = t_list[3] = null;
		f.setT_list(t_list);

		int[] index = f.getIndex();
		index[0] = index[1] = index[2] = index[3] = -1;
		f.setIndex(index);

		int[] flip = f.getFlip();
		flip[0] = flip[1] = flip[2] = flip[3] = -1;
		f.setFlip(flip);

		MeshSmooth.vec4f_copy(f.getColor(), color);

		for (i = 0; i < f.getDegree(); ++i) {
			MeshSmooth.vec3f_copy(vertex[i].getNormal(), f.getNormal());
			MeshSmooth.vec4f_copy(vertex[i].getColor(), color);

			vertex[i].setPrev(null);
			vertex[i].setNext(null);
		}

		MeshSmooth.vec3f_copy(vertex[0].getLocation(), p1);
		MeshSmooth.vec3f_copy(vertex[1].getLocation(), p2);
		if (p3 != null)
			MeshSmooth.vec3f_copy(vertex[2].getLocation(), p3);
		if (p4 != null)
			MeshSmooth.vec3f_copy(vertex[3].getLocation(), p4);

		vertex[0].setIndex(0);
		vertex[1].setIndex(1);
		if (vertex[2] != null)
			vertex[2].setIndex(2);
		if (vertex[3] != null)
			vertex[3].setIndex(3);

		vertex[0].setFace(f);
		vertex[1].setFace(f);

		if (vertex[2] != null)
			vertex[2].setFace(f);
		if (vertex[3] != null)
			vertex[3].setFace(f);
	}

	// public static void vec3_cross(float[] dst, float[] v1, float[] v2) {
	// dst[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
	// dst[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
	// dst[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
	// }

	// public static void vec3f_normalize(float N[]) {
	// float len = (float) Math.sqrt(N[0] * N[0] + N[1] * N[1] + N[2] * N[2]);
	// if (len != 0) {
	// len = 1.0f / len;
	// N[0] *= len;
	// N[1] *= len;
	// N[2] *= len;
	// }
	// }

	// copy vec4: d = s
	// public static void vec4f_copy(float[] d, float[] s) {
	// d[0] = s[0];
	// d[1] = s[1];
	// d[2] = s[2];
	// d[3] = s[3];
	// }

	// copy vec3: d = s.
	// public static void vec3f_copy(float[] d, float[] s) {
	// d[0] = s[0];
	// d[1] = s[1];
	// d[2] = s[2];
	// }

	// This function does a bunch of post-geometry-adding processing:
	// 1. It sorts the vertices in XYZ order for correct indexing. This
	// forces colocated vertices together in the list.
	// 2. It indexes vertices into an R-tree.
	// 3. It performs a two-step snapping process by
	// 3a. Locating rings of too-close vertices and
	// 3b. Setting each member of the ring to the ring's centroid location.
	// 4. Vertices are resorted AGAIN.
	// 5. The links from faces to vertices must be rebuilt due to sorting.
	// 6. Degenerate quads/tris are marked as 'creased' on all sides.
	//
	// Notes:
	// 2 and 4 are BOTH necessary - the first sort is needed to pre-sorted
	// the data for the R-tree interface.
	// The second sort is needed because the order of sort is ruined by
	// changing XYZ geometry locations.
	//
	// Re: 6, we don't want to delete degenerate quads (a degen quad
	// might be a visible triangle) but passing degenerate geometry to the
	// smoother causes problems - so instead we 'seal off' this geometry to
	// avoid further problems.
	void finish_faces_and_sort() {
		int v, f;
		// int total_before = 0, total_after = 0;

		// sort vertices by 10 params
		MeshSmooth.sort_vertices_3(vertices, vertex_count);

		// rtree = new RTree(vertices, vertex_count);

		// for (v = 0; v < vertex_count; ++v) {
		// if (v == 0
		// || MeshSmooth.compare_points(vertices.get(v - 1).getLocation(),
		// 0, vertices.get(v).getLocation(), 0) != 0) {
		// ++total_before;
		// Vertex vi = vertices.get(v);
		// float mib[] = { vi.getLocation()[0] - MeshSmooth.EPSI,
		// vi.getLocation()[1] - MeshSmooth.EPSI,
		// vi.getLocation()[2] - MeshSmooth.EPSI };
		// float mab[] = { vi.getLocation()[0] + MeshSmooth.EPSI,
		// vi.getLocation()[1] + MeshSmooth.EPSI,
		// vi.getLocation()[2] + MeshSmooth.EPSI };
		// // todo
		// //rtree.scan_rtree(index, mib, mab, visit_vertex_to_snap, vi);
		// }
		// }

		for (v = 0; v < vertex_count; ++v)
			if (v == 0
					|| MeshSmooth
							.compare_points(vertices.get(v - 1).getLocation(),
									0, vertices.get(v).getLocation(), 0) != 0)
				if (vertices.get(v).prev == null) {
					if (vertices.get(v).next != null) {
						Vertex i;
						float count = 0.0f;
						float p[] = new float[3];
						;
						for (i = vertices.get(v); i != null; i = i.getNext()) {
							count += 1.0f;
							p[0] += i.getLocation()[0];
							p[1] += i.getLocation()[1];
							p[2] += i.getLocation()[2];
						}

						assert (count > 0.0f);
						count = 1.0f / count;
						p[0] *= count;
						p[1] *= count;
						p[2] *= count;

						i = vertices.get(v);
						while (i != null) {
							boolean has_more = false;
							Vertex k = i;
							i = i.getNext();
							do {
								has_more = 1 < vertex_count
										&& MeshSmooth.compare_points(k
												.getLocation(), 0, k.getNext()
												.getLocation(), 0) == 0;

								k.getLocation()[0] = p[0];
								k.getLocation()[1] = p[1];
								k.getLocation()[2] = p[2];

								k = k.getNext();
								k.getPrev().setPrev(null);
								k.getPrev().setNext(null);

							} while (has_more);
						}
					}

					// ++total_after;
				}
		// printf("BEFORE: %d, AFTER: %d\n", total_before, total_after);

		MeshSmooth.sort_vertices_3(vertices, vertex_count);

		// then re-build ptr indices into faces since we moved vertices
		for (v = 0; v < vertex_count; ++v) {
			vertices.get(v).face.setVertexAtIndex(vertices.get(v).getIndex(),
					vertices.get(v));
		}

		for (f = 0; f < face_count; ++f) {
			if (faces[f].degree == 3) {
				float[] p1 = faces[f].vertex[0].getLocation();
				float[] p2 = faces[f].vertex[1].getLocation();
				float[] p3 = faces[f].vertex[2].getLocation();
				if (MeshSmooth.compare_points(p1, 0, p2, 0) == 0
						|| MeshSmooth.compare_points(p2, 0, p3, 0) == 0
						|| MeshSmooth.compare_points(p1, 0, p3, 0) == 0) {
					faces[f].neighbor[0] = faces[f].neighbor[1] = faces[f].neighbor[2] = faces[f].neighbor[3] = null;
				}

			}
			if (faces[f].degree == 4) {
				float[] p1 = faces[f].vertex[0].getLocation();
				float[] p2 = faces[f].vertex[1].getLocation();
				float[] p3 = faces[f].vertex[2].getLocation();
				float[] p4 = faces[f].vertex[3].getLocation();

				if (MeshSmooth.compare_points(p1, 0, p2, 0) == 0
						|| MeshSmooth.compare_points(p2, 0, p3, 0) == 0
						|| MeshSmooth.compare_points(p1, 0, p3, 0) == 0
						|| MeshSmooth.compare_points(p3, 0, p4, 0) == 0
						|| MeshSmooth.compare_points(p2, 0, p4, 0) == 0
						|| MeshSmooth.compare_points(p1, 0, p4, 0) == 0) {
					faces[f].neighbor[0] = faces[f].neighbor[1] = faces[f].neighbor[2] = faces[f].neighbor[3] = null;
				}
			}
		}
	}

	// This marks every line added to the mesh as a crease. This
	// ensures we won't smooth across our type 2 lines.
	void add_creases() {
		int fi;
		Face f;

		for (fi = poly_count; fi < face_count; ++fi) {
			f = faces[fi];
			assert (f.getDegree() == 2);
			Vertex[] vertex = f.getVertex();
			add_crease(vertex[0].getLocation(), vertex[1].getLocation());
		}
	}

	// Utility function: this marks one edge as a crease - the edge is
	// identified by its
	// location.
	public void add_crease(float p1[], float p2[]) {
		int begin, end;
		Vertex v;

		float pp1[] = { p1[0], p1[1], p1[2] };
		float pp2[] = { p2[0], p2[1], p2[2] };

		Range resultRange = MeshSmooth.range_for_point(vertices, 0,
				vertex_count, pp1);
		begin = resultRange.getLocation();
		end = resultRange.getMaxRange();

		for (int counter = begin; counter <= end; counter++) {
			v = vertices.get(counter);
			Face f = v.face;

			// CCW The index of neighbor "A" is index; the index of neighbor b
			// is
			// CCW.
			// / \ The index of neighbor C is CW.
			// b a So...if CW=p2 we found c;
			// / \ if CCW=p2 we found a.
			// CW---c---INDEX
			//
			//
			// // //
			int ccw = MeshSmooth.CCW(f, v.getIndex());
			int cw = MeshSmooth.CW(f, v.getIndex());

			Vertex[] vertex = f.getVertex();
			Face[] neighbor = f.getNeighbor();
			int[] index = f.getIndex();
			if (MeshSmooth.compare_points(vertex[cw].getLocation(), 0, pp2, 0) == 0) {
				// We found "C" - nuke at 'cw'
				neighbor[cw] = null;
				index[cw] = -1;
			}

			if (MeshSmooth.compare_points(vertex[ccw].getLocation(), 0, pp2, 0) == 0) {
				// We fond "A" - nuke at 'index'
				neighbor[v.getIndex()] = null;
				index[v.getIndex()] = -1;
			}
		}

	}

	// #pragma mark -
	// ==============================================================================
	// TRIANGLE MESH UTILS
	// ==============================================================================

	// This routine finds and removes all T junctions from the mesh. It does
	// this by...
	// For each non-creased edge (we don't de-T creases for speed) we R-tree
	// search for
	// all T-forming vertices and put them in a sorted linked list by edge.
	//
	// Then we build a brand new copy of our mesh and peel off ears for each
	// interference
	// as new triangles. When done, we're left with triangles that we also add.
	//
	// Finall we redo a bunch of processing we already did, now that we have a
	// new mesh.
	void find_and_remove_t_junctions() {
		assert (vertex_count == vertex_capacity);
		assert (face_count == face_capacity);

		t_finder_info_t info = new t_finder_info_t();
		int fi;
		info.inserted_pts = 0;
		info.split_quads = 0;

		for (fi = 0; fi < poly_count; ++fi) {
			info.f = faces[fi];
			if (info.f.degree > 2)
				for (info.i = 0; info.i < info.f.degree; ++info.i) {
					// Sad -- this is not a win - this info is not yet
					// available. :-(
					if (info.f.neighbor[info.i] == null)
						continue;

					info.v1 = info.f.vertex[info.i];
					info.v2 = info.f.vertex[(info.i + 1) % info.f.degree];

					if (MeshSmooth.vec3f_eq(info.v1.getLocation(),
							info.v2.getLocation()))
						continue;

					info.line_dir[0] = info.v2.getLocation()[0]
							- info.v1.getLocation()[0];
					info.line_dir[1] = info.v2.getLocation()[1]
							- info.v1.getLocation()[1];
					info.line_dir[2] = info.v2.getLocation()[2]
							- info.v1.getLocation()[2];
					// vec3f_normalize(info.line_dir);

					float mib[] = {
							Math.min(info.v1.getLocation()[0],
									info.v2.getLocation()[0])
									- MeshSmooth.EPSI,
							Math.min(info.v1.getLocation()[1],
									info.v2.getLocation()[1])
									- MeshSmooth.EPSI,
							Math.min(info.v1.getLocation()[2],
									info.v2.getLocation()[2])
									- MeshSmooth.EPSI };

					float mab[] = {
							Math.max(info.v1.getLocation()[0],
									info.v2.getLocation()[0])
									+ MeshSmooth.EPSI,
							Math.max(info.v1.getLocation()[1],
									info.v2.getLocation()[1])
									+ MeshSmooth.EPSI,
							Math.max(info.v1.getLocation()[2],
									info.v2.getLocation()[2])
									+ MeshSmooth.EPSI };

					// todo
					// rtree.scan_rtree(mib, mab, visit_possible_t_junc, &info);
				}
		}

		// printf("Subdivided %d quads and added %d pts.\n",
		// info.split_quads,info.inserted_pts);
		if (info.inserted_pts > 0) {
			int f;
			Mesh new_mesh;
			assert (info.split_quads <= quad_count);
			new_mesh = new Mesh(tri_count + info.inserted_pts + 2
					* info.split_quads, quad_count - info.split_quads,
					line_count);

			for (f = 0; f < face_count; ++f) {
				Face fp = faces[f];
				if (fp.t_list[0] == null && fp.t_list[1] == null
						&& fp.t_list[2] == null && fp.t_list[3] == null) {
					switch (fp.degree) {
					case 2:
						new_mesh.add_face(fp.vertex[0].getLocation(),
								fp.vertex[1].getLocation(), null, null,
								fp.color, fp.tid);
						break;
					case 3:
						new_mesh.add_face(fp.vertex[0].getLocation(),
								fp.vertex[1].getLocation(),
								fp.vertex[2].getLocation(), null, fp.color,
								fp.tid);
						break;
					case 4:
						new_mesh.add_face(fp.vertex[0].getLocation(),
								fp.vertex[1].getLocation(),
								fp.vertex[2].getLocation(),
								fp.vertex[3].getLocation(), fp.color, fp.tid);
						break;
					default:
						assert true : "bad degree.";
					}
				} else {
					int i;
					int total_pts = 0;
					VertexInsert vp;
					float[] poly;
					int write_cnt = 0;
					for (i = 0; i < fp.degree; ++i) {
						++total_pts;
						for (vp = fp.t_list[i]; vp != null; vp = vp.next)
							++total_pts;
					}

					poly = new float[3 * total_pts];
					write_cnt = 0;

					for (i = 0; i < fp.degree; ++i) {
						for (int j = 0; j < 3; j++)
							poly[write_cnt + j] = fp.vertex[i].getLocation()[j];
						write_cnt += 3;

						for (vp = fp.t_list[i]; vp != null; vp = vp.next) {
							for (int j = 0; j < 3; j++)
								poly[write_cnt + j] = vp.vert.getLocation()[j];
							write_cnt += 3;
						}
					}

					while (total_pts > 3) {
						add_ear_and_remove(poly, total_pts, new_mesh, fp.color,
								fp.tid);
						--total_pts;
					}

					float[] p1, p2, p3;
					p1 = new float[3];
					p2 = new float[3];
					p3 = new float[3];

					FloatBuffer tempFloatBuffer = FloatBuffer.wrap(poly);
					tempFloatBuffer.get(p1, 3, 3);
					tempFloatBuffer.get(p2, 6, 3);
					new_mesh.add_face(poly, p1, p2, null, fp.color, fp.tid);
				}
			}

			// assert(new_vertex_count == new_vertex_capacity);
			// assert(new_face_count == new_face_capacity);

			new_mesh.finish_faces_and_sort();
			new_mesh.add_creases();

			Mesh temp;

			Mesh.swap(new_mesh, this);

			// memcpy(&temp,mesh,sizeof(Mesh));
			// memcpy(mesh,new_mesh,sizeof(Mesh));
			// memcpy(new_mesh,&temp,sizeof(Mesh));

			// destroy_mesh(new_mesh);

		}
	}

	public void setMesh(Mesh newMesh) {
		setVertex_count(newMesh.getVertex_count());
		setVertex_capacity(newMesh.getVertex_capacity());
		setUnique_vertex_count(newMesh.getUnique_vertex_count());
		setVertices(newMesh.getVertices());
		setFace_capacity(newMesh.getFace_capacity());
		setFace_count(newMesh.getFace_count());
		setFaces(newMesh.getFaces());
		setTri_count(newMesh.getTri_count());
		setQuad_count(newMesh.getQuad_count());
		setPoly_count(newMesh.getPoly_count());
		setLine_count(newMesh.getLine_count());
		setRtree(newMesh.getRtree());
		setHighest_tid(newMesh.getHighest_tid());
	}

	// (int tri_count, int quad_count, int line_count) {
	private static void swap(Mesh new_mesh, Mesh mesh) {
		Mesh temp = new Mesh(0, 0, 0);
		temp.setMesh(new_mesh);
		new_mesh.setMesh(mesh);
		;
		mesh.setMesh(temp);
	}

	void add_ear_and_remove(float[] poly, int pt_count, Mesh target_mesh,
			float[] color, int tid) {
		int i, p, n, b = -1;
		float best_dot = -99.0f;

		for (i = 0; i < pt_count; ++i) {
			float[] p1, p2, p3;
			float v1[], v2[];
			v1 = new float[3];
			v2 = new float[3];
			float dot;

			p = (i + pt_count - 1) % pt_count;
			n = (i + 1) % pt_count;
			p1 = new float[3];
			p2 = new float[3];
			p3 = new float[3];
			FloatBuffer tempFloatBuffer = FloatBuffer.wrap(poly);
			tempFloatBuffer.get(p1, 3 * p, 3);
			tempFloatBuffer.get(p2, 3 * i, 3);
			tempFloatBuffer.get(p3, 3 * n, 3);

			MeshSmooth.vec3f_diff(v1, p2, p1);
			MeshSmooth.vec3f_diff(v2, p2, p3);
			MeshSmooth.vec3f_normalize(v1);
			MeshSmooth.vec3f_normalize(v2);

			dot = MeshSmooth.vec3f_dot(v1, v2);
			if (dot > best_dot || b == -1) {
				best_dot = dot;
				b = i;
			}
		}

		assert (b >= 0);
		assert (b < pt_count);

		p = (b + pt_count - 1) % pt_count;
		n = (b + 1) % pt_count;

		float[] p1, p2, p3;
		p1 = new float[3];
		p2 = new float[3];
		p3 = new float[3];

		FloatBuffer tempFloatBuffer = FloatBuffer.wrap(poly);
		tempFloatBuffer.get(p1, 3 * p, 3);
		tempFloatBuffer.get(p2, 3 * b, 3);
		tempFloatBuffer.get(p3, 3 * n, 3);
		target_mesh.add_face(p1, p2, p3, null, color, tid);

		if (b != pt_count - 1) {
			for (int j = 0; j < (pt_count - b - 1) * 3; j++)
				poly[3 * b + j] = poly[3 * b + 3 + j];
		}

	}

	// Once all creases have been marked, this routine locates all colocated
	// mesh
	// edges going in opposite directions (opposite direction colocated edges
	// mean
	// the faces go in the same direction) that are not already marked as
	// neighbors
	// or creases. If the potential join between faces is too sharp, it is
	// marked
	// as a crease, otherwise the edges are recorded as neighbors of each other.
	// When we are done every polygon edge is a crease or neighbor of someone.
	void finish_creases_and_join() {
		int fi;
		int i;
		Face f;

		for (fi = 0; fi < poly_count; ++fi) {
			f = faces[fi];
			assert (f.degree >= 3);
			for (i = 0; i < f.degree; ++i) {
				if (f.neighbor[i] == null) {
					// CCW(i)/P1
					// / \ The directed edge we want goes FROM i TO ccw.
					// / i So p2 = ccw, p1 = i, that is, we want our OTHER
					// / \ neighbor to go FROM cw TO CCW
					// .---------i/P2
					int p1Index = MeshSmooth.CCW(f, i);
					Vertex p1 = f.vertex[p1Index];

					Vertex p2 = f.vertex[i];
					int begin, end, v;

					Range result = MeshSmooth.range_for_vertex(vertices, 0,
							vertex_count, p1);
					begin = result.getLocation();
					end = result.getMaxRange();

//					System.out.println("start");
//					System.out.println(p1.getLocation()[0] + ","
//							+ p1.getLocation()[1] + ","
//							+ p1.getLocation()[2]);
//					System.out.println(result.getLocation()+", "+result.getMaxRange());
//					for (Vertex vertex : vertices) {
//						System.out.println(vertex.getLocation()[0] + ","
//								+ vertex.getLocation()[1] + ","
//								+ vertex.getLocation()[2]);
//					}
					

					for (v = begin; v <= end; v++) {
						if (vertices.get(v).face == f)
							continue;

						// P1/v-----x Normal case - Since p1.p2 is the ideal
						// direction of our
						// \ / neighbor, p2 = ccw(v). Thus p1(v) names our edge.
						// v /
						// \ /
						// P2/CCW(V)

						// P1/v-----x Backward winding case - thus p2 is CW from
						// P1,
						// \ / and P2 (cw(v) names our edge.
						// cw(v) /
						// \ /
						// P2/CW(V)

						assert (MeshSmooth.compare_points(p1.getLocation(), 0,
								vertices.get(v).getLocation(), 0) == 0);

						Face n = vertices.get(v).face;
						Vertex dst = n.vertex[MeshSmooth.CCW(n, vertices.get(v)
								.getIndex())];

						Vertex inv = n.vertex[MeshSmooth.CW(n, vertices.get(v)
								.getIndex())];

						if (dst.face.degree > 2)
							if (MeshSmooth.compare_points(dst.getLocation(), 0,
									p2.getLocation(), 0) == 0) {
								int ni = vertices.get(v).getIndex();
								assert (f.neighbor[i] == null);
								if (n.neighbor[ni] == null) {

									if (MeshSmooth.is_crease(f.normal,
											n.normal, false)) {
										f.neighbor[i] = null;
										n.neighbor[ni] = null;
										f.index[i] = -1;
										n.index[ni] = -1;
										break;
									} else

									{
										// v.dst matches p1.p2. We have
										// neighbors.
										// Store both - avoid half the work when
										// we get to our neighbor.
										f.neighbor[i] = n;
										n.neighbor[ni] = f;
										f.index[i] = ni;
										n.index[ni] = i;
										f.flip[i] = 0;
										n.flip[ni] = 0;
										break;
									}
								}
							}

						if (inv.face.degree > 2)
							if (MeshSmooth.compare_points(inv.getLocation(), 0,
									p2.getLocation(), 0) == 0) {
								int ni = MeshSmooth.CW(vertices.get(v).face,
										vertices.get(v).getIndex());
								assert (f.neighbor[i] == null);
								if (n.neighbor[ni] == null) {
									if (MeshSmooth.is_crease(f.normal,
											n.normal, true)) {
										f.neighbor[i] = null;
										n.neighbor[ni] = null;
										f.index[i] = -1;
										n.index[ni] = -1;
										break;
									} else {
										// v.dst matches p1.p2. We have
										// neighbors.
										// Store both - avoid half the work when
										// we get to our neighbor.
										f.neighbor[i] = n;
										n.neighbor[ni] = f;
										f.index[i] = ni;
										n.index[ni] = i;
										f.flip[i] = 1;
										n.flip[ni] = 1;
										break;
									}
								}
							}

					}
				}
				if (f.neighbor[i] == null) {
					f.neighbor[i] = null;
					f.index[i] = -1;
				}
			}
		}
	}

	// Once all neighbors have been found, this routine calculates the
	// actual per-vertex smooth normals. This is done by circulating
	// each vertex (via its neighbors) to find all contributing triangles,
	// computing a weighted average (from for each triangle) and applying
	// the new averaged normal to all participating vertices.
	//
	// A few key points:
	// - Circulation around the vertex only goes by neigbhor. So creases
	// (lack of a neighbor) partition the triangles around our vertex into
	// adjacent groups, each of which get their own smoothing.
	// - This is what makes a 'creased' shape flat-shaded: the creases keep
	// us from circulating more than one triangle.
	// - We weight our average normal by the angle the triangle spans around
	// the vertex, not just a straight average of all participating triangles.
	// We do not want to bias our normal toward the direction of more small
	// triangles.
	void smooth_vertices() {
		int f;
		int i;
		for (f = 0; f < poly_count; ++f)
			for (i = 0; i < faces[f].degree; ++i) {
				// For each vertex, we are going to circulate around attached
				// faces, averaging up our normals.

				Vertex v = faces[f].vertex[i];

				// First, go clock-wise around, starting at ourselves, until we
				// loop back on ourselves (a closed smooth
				// circuite - the center vert on a stud top is like this) or we
				// run out of vertices.

				Vertex c = v;
				float N[] = new float[3];
				IntBuffer circ_dir = IntBuffer.allocate(1);
				circ_dir.put(-1);
				float w;
				do {
					// System.out.println(String.format(Locale.US,
					// "\tAdd: %f,%f,%f\n", c.normal[0], c.normal[1],
					// c.normal[2]));

					w = MeshSmooth.weight_for_vertex(c);

					if (MeshSmooth.vec3f_dot(v.face.normal, c.face.normal) > 0.0f) {
						N[0] += w * c.face.normal[0];
						N[1] += w * c.face.normal[1];
						N[2] += w * c.face.normal[2];
					} else {
						N[0] -= w * c.face.normal[0];
						N[1] -= w * c.face.normal[1];
						N[2] -= w * c.face.normal[2];
					}

					c = MeshSmooth.circulate_any(c, circ_dir);

				} while (c != null && c != v);

				// Now if we did NOT make it back to ourselves it means we are a
				// disconnected circulation. For example
				// a semi-circle fan's center will do this if we start from a
				// middle tri.
				// Circulate in the OTHER direction, skipping ourselves, until
				// we run out.

				if (c != v) {
					circ_dir.put(0, 1);
					c = MeshSmooth.circulate_any(v, circ_dir);
					while (c != null) {
						// System.out.println(String.format(Locale.US,
						// "\tAdd: %f,%f,%f\n", c.normal[0], c.normal[1],
						// c.normal[2]));
						w = MeshSmooth.weight_for_vertex(c);
						if (MeshSmooth.vec3f_dot(v.face.normal, c.face.normal) > 0.0f) {
							N[0] += w * c.face.normal[0];
							N[1] += w * c.face.normal[1];
							N[2] += w * c.face.normal[2];
						} else {
							N[0] -= w * c.face.normal[0];
							N[1] -= w * c.face.normal[1];
							N[2] -= w * c.face.normal[2];
						}

						c = MeshSmooth.circulate_any(c, circ_dir);

						// Invariant: if we did NOT close-loop up top, we should
						// NOT close-loop down here - that would imply
						// a triangulation where our neighbor info was
						// assymetric, which would be "bad".
						assert (c != v);
					}
				}

				MeshSmooth.vec3f_normalize(N);
				// System.out.println(String.format(Locale.US,
				// "Final: %f %f %f\t%f %f %f\n", v.getLocation()[0],
				// v.getLocation()[1], v.getLocation()[2], N[0], N[1],
				// N[2]));
				v.normal[0] = N[0];
				v.normal[1] = N[1];
				v.normal[2] = N[2];
			}
	}

	// This routine merges vertices that have the same complete (10-float)
	// value to optimize down the size of geometry in VRAM. We do this
	// by resorting by all components and then recognizing adjacently equal
	// vertices. This routine rebuilds the ptrs from triangles so that all
	// faces see the 'shared' vertex. By convention the first of a group
	// of equal vertices in the vertex array is the one we will keep/use.
	void merge_vertices() {
		// Once smoothing is done, indexing the mesh is actually pretty easy:
		// First, we re-sort the vertex list; now that our normals and colors
		// are consolidated, equal-value vertices in the final mesh will pool
		// together.
		//
		// Then (having moved our vertices) we need to rebuild the face.vertex
		// pointers...when we do this, we simply use the FIRST vertex among
		// equals for every face.
		//
		// The result is that for every redundent vertex, every face will
		// agree on which ptr to use. This means that we can build an indexed
		// mesh off of the ptrs and get maximum sharing.

		int v;

		int unique = 0;
		Vertex first_of_equals = vertices.get(0);

		// Resort according ot our xyz + normal + color
		MeshSmooth.sort_vertices_10(vertices, vertex_count);

		// Re-set the tri ptrs again, but...for each IDENTICAL source vertex,
		// use the FIRST of them as the ptr
		for (v = 0; v < vertex_count; ++v) {
			if (MeshSmooth.compare_vertices(first_of_equals, vertices.get(v)) != 0) {
				first_of_equals = vertices.get(v);
			}
			vertices.get(v).face.vertex[vertices.get(v).index] = first_of_equals;
			if (vertices.get(v) == first_of_equals) {
				vertices.get(v).index = -1;
				++unique;
			} else {
				vertices.get(v).index = -2;
			}
		}

		unique_vertex_count = unique;
		// System.out.println("Before: "+vertex_count+ ", after: "+unique);
	}

	// This returns the final counts for vertices and indices in a mesh - after
	// merging,
	// subdivising, etc. our original counts may be changed, so clients need to
	// know
	// how much VBO space to allocate.
	void get_final_mesh_counts(IntBuffer total_vertices, IntBuffer total_indices) {
		total_vertices.put(0, unique_vertex_count);
		total_indices.put(0, vertex_count);
	}

	// This cleans our mesh, deallocating all internal memory.
	void destroy_mesh() {
		// nothing to do. everything will be done by GC.
	}

	// This routine writes out the final smoothed mesh. It takes:
	// - Buffer space for the vertex table (10x floats per vertex)
	// - Buffer space for the indices (1 uint per index)
	// - Pointers to variable-sized arrays to take the start/count for
	// each kind of primitive for each TID.
	// In other words, out_line_starts[0] contains the offset into our
	// index buffer of the lines for TID 0. out_quad_counts[2] contains
	// the number of indices for all quads in TID 2.
	// max(tids)+1 ints should be allocated for each output array.
	//
	// The indices are written in TID order, so that at most three draw calls
	// (one each for tris, lines and quads) can be used to draw each TID's
	// collection of geometry. Primitives are also output in order.
	//
	// (In other words, the primary sort key is TID, second is primitive
	// type.)
	void write_indexed_mesh(int vertex_table_size, FloatBuffer io_vertex_table,
			int index_table_size, IntBuffer io_index_table, int index_base,
			IntBuffer out_line_starts, IntBuffer out_line_counts,
			IntBuffer out_tri_starts, IntBuffer out_tri_counts,
			IntBuffer out_quad_starts, IntBuffer out_quad_counts) {
		IntBuffer starts[] = { null, null, out_line_starts, out_tri_starts,
				out_quad_starts };
		IntBuffer counts[] = { null, null, out_line_counts, out_tri_counts,
				out_quad_counts };

		FloatBuffer vert_ptr = io_vertex_table;
		int vertCount = 0;

		IntBuffer index_ptr = io_index_table;
		int indexCount = 0;

		int cur_idx = index_base;

		int d, i, vi, ti;
		Vertex v, vv;
		Face f;

		// Outer loop: we are going to make one pass over the vertex array
		// for each depth of primitive - in other words, we are going to
		// 'fish out' all lines first, then all tris, then all quads.
		for (ti = 0; ti <= highest_tid; ++ti)
			for (d = 2; d <= 4; ++d) {
				starts[d].put(ti, indexCount);

				for (vi = 0; vi < vertex_count; ++vi) {
					v = vertices.get(vi);
					f = v.face;

					// For each vertex, we look at its face if it qualifies.
					// This way we write the faces in sorted vertex order.
					if (f.degree == d)
						if (f.tid == ti) {
							for (i = 0; i < d; ++i) {
								vv = f.vertex[i];
								assert (vv.getIndex() != -2);
								// To write out our vertices, we MAY need to
								// write out the vertex if it is first used.
								// Thus the vertices go down in approximate
								// usage
								// order, which is good.
								if (vv.getIndex() == -1) {
									vv.setIndex(cur_idx++);

									vert_ptr.put(vertCount++,
											vv.getLocation()[0]);
									vert_ptr.put(vertCount++,
											vv.getLocation()[1]);
									vert_ptr.put(vertCount++,
											vv.getLocation()[2]);

									vert_ptr.put(vertCount++, vv.normal[0]);
									vert_ptr.put(vertCount++, vv.normal[1]);
									vert_ptr.put(vertCount++, vv.normal[2]);

									vert_ptr.put(vertCount++, vv.color[0]);
									vert_ptr.put(vertCount++, vv.color[1]);
									vert_ptr.put(vertCount++, vv.color[2]);
									vert_ptr.put(vertCount++, vv.color[3]);
								}

								assert (vv.getIndex() >= 0);

								index_ptr.put(indexCount++, (vv.getIndex()));
								;
							}

							// when the face is done, we mark it via degree = 0
							// so we
							// don't hit it again when we hit one of its
							// vertices due to
							// sharing.
							f.setDegree(0);

						} // end of face write-out for matched faces

				} // end of linear vertex walk

				counts[d].put(ti, (indexCount - starts[d].get(ti)));

			} // end of primitve sort

		// assert(vert_ptr == vert_stop);
		// assert(index_ptr == index_stop);
	}

	public int getTotalVertices() {
		return unique_vertex_count;
	}

	public int getTotalIndices() {
		return vertex_count;
	}
}

class t_finder_info_t {
	/**
	 * @uml.property name="split_quads"
	 */
	int split_quads; // The number of quads that have been split. Each quad with
						// a
						// subdivision must be triangulated, changing our face
						// count, so
						// we have to track this.
	/**
	 * @uml.property name="inserted_pts"
	 */
	int inserted_pts; // Number of points inserted into edges - this increases
						// triangle
						// count so we must track it.
	/**
	 * @uml.property name="v1"
	 * @uml.associationEnd
	 */
	Vertex v1; // Start and end vertices of the edge we are testing.
	/**
	 * @uml.property name="v2"
	 * @uml.associationEnd
	 */
	Vertex v2;
	/**
	 * @uml.property name="f"
	 * @uml.associationEnd
	 */
	Face f; // The face that v1/v2 belong to.
	/**
	 * @uml.property name="i"
	 */
	int i; // The side index i of face f that we are tracking, e.g.
			// f.vertices[i] == v1
	/**
	 * @uml.property name="line_dir"
	 */
	float line_dir[]; // A normalized direction vector from v1 to v2, used to
						// order the intrusions.

	/**
	 * @return
	 * @uml.property name="split_quads"
	 */
	public int getSplit_quads() {
		return split_quads;
	}

	/**
	 * @param split_quads
	 * @uml.property name="split_quads"
	 */
	public void setSplit_quads(int split_quads) {
		this.split_quads = split_quads;
	}

	/**
	 * @return
	 * @uml.property name="inserted_pts"
	 */
	public int getInserted_pts() {
		return inserted_pts;
	}

	/**
	 * @param inserted_pts
	 * @uml.property name="inserted_pts"
	 */
	public void setInserted_pts(int inserted_pts) {
		this.inserted_pts = inserted_pts;
	}

	/**
	 * @return
	 * @uml.property name="v1"
	 */
	public Vertex getV1() {
		return v1;
	}

	/**
	 * @param v1
	 * @uml.property name="v1"
	 */
	public void setV1(Vertex v1) {
		this.v1 = v1;
	}

	/**
	 * @return
	 * @uml.property name="v2"
	 */
	public Vertex getV2() {
		return v2;
	}

	/**
	 * @param v2
	 * @uml.property name="v2"
	 */
	public void setV2(Vertex v2) {
		this.v2 = v2;
	}

	/**
	 * @return
	 * @uml.property name="f"
	 */
	public Face getF() {
		return f;
	}

	/**
	 * @param f
	 * @uml.property name="f"
	 */
	public void setF(Face f) {
		this.f = f;
	}

	/**
	 * @return
	 * @uml.property name="i"
	 */
	public int getI() {
		return i;
	}

	/**
	 * @param i
	 * @uml.property name="i"
	 */
	public void setI(int i) {
		this.i = i;
	}

	/**
	 * @return
	 * @uml.property name="line_dir"
	 */
	public float[] getLine_dir() {
		return line_dir;
	}

	/**
	 * @param line_dir
	 * @uml.property name="line_dir"
	 */
	public void setLine_dir(float[] line_dir) {
		this.line_dir = line_dir;
	}

};
