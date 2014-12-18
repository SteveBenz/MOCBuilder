package Renderer;

import java.nio.IntBuffer;
import java.util.ArrayList;

import LDraw.Support.Range;

//==============================================================================
//
//File: MeshSmooth
//
//MeshSmooth is a set of C functions that merge triangle meshes and calculate
//smoothed normals in a way that happens to be usefor for LDraw models.  
//MeshSmooth takes care of the following processing:
//
//- "Welding" very-close vertices that do not have the exact same location due
// to rounding errors in sub-part matrix transforms.
//
//- Optionally locating T junctions and subdividing the faces.
//
//- Determining smooth and creased edges based on the presence of lines and 
// crease angles.
//
//- Resolving BFC errors.  (The normals are generated correctly for two-sided
// lighting, but no attempt to determine a front is made; the output must 
// still support two-sided lighting and have culling disabled.)
//
//- Calculating smooth normals for shared vertices.
//
//- Merging vertices that are completely equal and calculating mesh indices.
//
//Usage:
//
//A client creates a mesh structure with a pre-declared count of tris, quads
//and lines, then adds them.
//
//Once all data is added, a series of processing functions are called to
//transform the data.
//
//Finally, for output, the final mesh counts are queried and written to storage
//provided by the client.  This API is suitable for writing directly to memory-
//mapped VBOs.
//
//Textures:
//
//Faces can be tagged with an integer "texture ID" TID; the API will track face
//TID and output the mesh in TID order.  This allows a single mesh to be drawn
//as a series of sub-draw-calls with texture changes in between them.
//
//Texture IDs should be sequential and zero based.
//
//==============================================================================

public class MeshSmooth {
	public static final float EPSI = 0.05f;
	public static final float EPSI2 = 0.00025f;

	// Compare two unique 3-d points in space for location-sameness.
	public static int compare_points(float[] p1, int offset_1, float[] p2,
			int offset_2) {
		for (int i = 0; i < 3; i++) {
			if (p1[offset_1 + i] < p2[offset_2 + i])
				return -1;
			if (p1[offset_1 + i] > p2[offset_2 + i])
				return 1;
		}

		return 0;
	}

	public static int compare_points(float[] p1, float[] p2) {
		return compare_points(p1, 0, p2, 0);
	}

	// Compare two vertices for complete match-up of all vertices - vertex,
	// normal, color.
	// If these all match, we could merge the vertices on the graphics card.
	public static int compare_vertices(Vertex v1, Vertex v2) {
		float[] location_v1 = v1.getLocation();
		float[] location_v2 = v2.getLocation();

		float[] normal_v1 = v1.getNormal();
		float[] normal_v2 = v2.getNormal();

		float[] color_v1 = v1.getColor();
		float[] color_v2 = v2.getColor();

		int returnValue = 0;
		if ((returnValue = compare_points(location_v1, location_v2)) != 0)
			return returnValue;

		if ((returnValue = compare_points(normal_v1, normal_v2)) != 0)
			return returnValue;

		if ((returnValue = compare_points(color_v1, color_v2)) != 0)
			return returnValue;

		if (color_v1[3] < color_v2[3])
			return -1;
		if (color_v1[3] > color_v2[3])
			return 1;

		return 0;
	}

	// Compare only the "Nth" location field, e.g. only x, y, or z.
	// Used to organize points along a single axis.
	public static int compare_nth(Vertex v1, Vertex v2, int n) {
		float[] location_v1 = v1.getLocation();
		float[] location_v2 = v2.getLocation();

		if (location_v1[n] < location_v2[n])
			return -1;
		if (location_v1[n] > location_v2[n])
			return 1;
		return 0;
	}

	// 10-coordinate bubble sort - in other words, the array of vertices is
	// sorted
	// lexicographically based on all 10 coords (position, normal, and color).
	// WHY a bubble sort??! Well, it turns out that when we run this, we are
	// already
	// sorted by location, and thus the 'disturbance' of orders are very
	// localized
	// and small. So it is faster to run bubble sort - while its worst case time
	// is
	// really quite bad, it gets fast when we are nearly sorted.
	public static void bubble_sort_10(ArrayList<Vertex> vertices, int count) {
		boolean swapped;

		do {
			int high_count = 0;
			swapped = false;

			for (int i = 1; i < count; ++i)
				if (compare_vertices(vertices.get(i - 1), vertices.get(i)) > 0) {
					swapVertexInArrayList(vertices, i - 1, i);
					swapped = true;
					high_count = i;
				}
			count = high_count;

		} while (swapped);
	}

	private static void swapVertexInArrayList(ArrayList<Vertex> vertices,
			int i, int j) {
		Vertex temp_i = vertices.get(i);
		vertices.set(i, vertices.get(j));
		vertices.set(j, temp_i);

	}

	// sort APIs are wrapped in functions that don't have an algo, e.g. "just
	// sort by
	// 10 coords" so we can easily try different algos and see which is fastest.
	public static void sort_vertices_10(ArrayList<Vertex> vertices, int count) {
		MeshSmooth.bubble_sort_10(vertices, count);
	}

	// 3-coordinate quick-sort. The range of arr from [left to right]
	// (inclusive!!)
	// is sorted using quick-sort. For totally unsorted data, this is a good
	// sort
	// choice. Only location is used to sort.
	public static void quickSort_3(ArrayList<Vertex> vertices, int left,
			int right) {
		int i = left, j = right;

		Vertex pivot_ptr = vertices.get((left + right) / 2);
		float pivot[] = { pivot_ptr.getLocation()[0],
				pivot_ptr.getLocation()[1], pivot_ptr.getLocation()[2] };

		/* partition */
		while (i <= j) {

			while (MeshSmooth.compare_points(vertices.get(i).getLocation(), 0,
					pivot, 0) < 0) {
				++i;
			}

			while (MeshSmooth.compare_points(vertices.get(j).getLocation(), 0,
					pivot, 0) > 0) {
				--j;
			}

			if (i <= j) {
				if (i != j) {
					swapVertexInArrayList(vertices, i, j);
				}
				++i;
				--j;
			}
		}

		if (left < j)
			quickSort_3(vertices, left, j);

		if (i < right)
			quickSort_3(vertices, i, right);

	}

	// Quick-sort, but based only on the "nth" coordinate - lets us rapidly
	// sort by x, y, or z. We want quicksort because changing the sort axis
	// is likely to radically change the order, and thus we are not near-sorted
	// to begin with.
	public static void quickSort_n(ArrayList<Vertex> vertices, int left,
			int right, int n) {
		int i = left, j = right;

		Vertex pivot_ptr = vertices.get((left + right) / 2);
		Vertex pivot = pivot_ptr;

		/* partition */
		while (i <= j) {

			while (compare_nth(vertices.get(i), pivot, n) < 0) {
				i++;
			}

			while (compare_nth(vertices.get(j), pivot, n) > 0) {
				j--;
			}

			if (i <= j) {
				if (i != j) {
					swapVertexInArrayList(vertices, i, j);
				}
				++i;
				--j;
			}
		}

		if (left < j)
			quickSort_n(vertices, left, j, n);

		if (i < right)
			quickSort_n(vertices, i, right, n);
	}

	// General sort by location API, see sort_vertices_10 for
	// logic.
	public static void sort_vertices_3(ArrayList<Vertex> vertices, int count) {
		quickSort_3(vertices, 0, count - 1);
	}

	// Search primitive. Given a sorted (by location) array of vertices and a
	// target point (p3) this routine finds the range
	// [begin, end) that has points of equal location to p. begin == end if
	// there are on points matching p; in this case,
	// begin and end will _not_ be "near" p in any way.
	//
	// The beginning of the range is found via binary search; the end is found
	// by linearly walking forward to find the end.
	// Since we have relatively small numbers of equal points, this linear walk
	// is fine.
	public static Range range_for_point(ArrayList<Vertex> vertices, int base,
			int stop, float p[]) {
		int len = stop - base;
		int index = 0;
		int begin;
		int end;
		while (len > 0) {
			int half = len / 2;
			Vertex middle = vertices.get(index + half);

			int res = MeshSmooth.compare_points(middle.getLocation(), 0, p, 0);

			if (res < 0) {
				index = index + half + 1;
				len = len - half - 1;
			} else {
				len = half;
			}
		}

		begin = index;

		while (index != stop
				&& MeshSmooth.compare_points(vertices.get(index).getLocation(),
						0, p, 0) == 0) {
			++index;
		}

		end = index;
		return new Range(begin, end - begin);
	}

	// Given a vertex q already in our array of sorted vertices [base, stop) we
	// find the range [begin, end) that is entirely colocated
	// with q. Q will be in the range [begin, end). We do this with a linear
	// walk - we know all these vertices are near each other,
	// so we just go find them without jumping.
	public static Range range_for_vertex(ArrayList<Vertex> vertices, int base,
			int stop, Vertex q) {
		int begin = 0, end = 0;

		int b = vertices.indexOf(q);
		int e = b;


		while (b >= base
				&& compare_points(vertices.get(b).getLocation(),
						q.getLocation()) == 0)
			--b;
		++b;

		while (e < stop
				&& compare_points(vertices.get(e).getLocation(),
						q.getLocation()) == 0)
			++e;

		begin = b;
		end = e;
		return new Range(begin, end - begin);

	}

	// Utilities to return the next vertex index of a face from i, in either the
	// clock-wise
	// or counter-clockwise direction.
	public static int CCW(Face f, int i) {
		assert (i >= 0 && i < f.getDegree());
		return (i + 1) % f.getDegree();
	}

	public static int CW(Face f, int i) {
		assert (i >= 0 && i < f.getDegree());
		return (i + f.getDegree() - 1) % f.getDegree();
	}

	// Predicate: do the two face normals n1 and n2 form a crease? Flip should
	// be true if the winding order
	// of the two tris is flipped.
	public static boolean is_crease(float n1[], float n2[], boolean flip) {
		float dot = vec3f_dot(n1, n2);
		if (flip == true) {
			return (dot > -0.5f);
		} else
			return (dot < 0.5f);
	}

	// #pragma mark -
	// ==============================================================================
	// 3-D MATH UTILS
	// ==============================================================================

	// vec3 and vec4 APIs refer to "vector" numbers, e.g. small arrays of float.
	// So vec3 . float[3], and vec4 . float[4].

	// Normalize vector N in place if not zero-length.
	public static void vec3f_normalize(float N[]) {
		float len = (float) Math.sqrt(N[0] * N[0] + N[1] * N[1] + N[2] * N[2]);
		if (len != 0) {
			len = 1.0f / len;
			N[0] *= len;
			N[1] *= len;
			N[2] *= len;
		}
	}

	// copy vec3: d = s.
	public static void vec3f_copy(float[] d, float[] s) {
		d[0] = s[0];
		d[1] = s[1];
		d[2] = s[2];
	}

	// copy vec4: d = s
	public static void vec4f_copy(float[] d, float[] s) {
		d[0] = s[0];
		d[1] = s[1];
		d[2] = s[2];
		d[3] = s[3];
	}

	// return dot product of vec3's d1 and d2.
	public static float vec3f_dot(float[] v1, float[] v2) {
		return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
	}

	// vec3: dst = b - a. (or: vector dst points from A to B).
	public static void vec3f_diff(float[] dst, float[] a, float[] b) {
		dst[0] = b[0] - a[0];
		dst[1] = b[1] - a[1];
		dst[2] = b[2] - a[2];
	}

	// Return the square of the length of the distance between two vec3 points
	// p1, p2.
	public static float vec3f_length2(float[] p1, float[] p2) {
		float d[] = new float[3];
		vec3f_diff(d, p1, p2);
		return vec3f_dot(d, d);
	}

	// 3-d comparison of p1 and p2 (simple true/false, not a comparator).
	public static boolean vec3f_eq(float[] p1, float[] p2) {
		return p1[0] == p2[0] && p1[1] == p2[1] && p1[2] == p2[2];
	}

	// vec3 cross product, e.g. dst = v1 x v2.
	public static void vec3_cross(float[] dst, float[] v1, float[] v2) {
		dst[0] = (v1[1] * v2[2]) - (v1[2] * v2[1]);
		dst[1] = (v1[2] * v2[0]) - (v1[0] * v2[2]);
		dst[2] = (v1[0] * v2[1]) - (v1[1] * v2[0]);
	}

	// Returns true if the projection of B onto the line AC is in between (but
	// not on) A and C.
	public boolean in_between_line(float[] a, float[] b, float[] c) {
		float ab[] = new float[3];
		float ac[] = new float[3];
		float cb[] = new float[3];
		vec3f_diff(ab, a, b);
		vec3f_diff(ac, a, c);
		vec3f_diff(cb, c, b);
		return vec3f_dot(ab, ac) > 0.0f && vec3f_dot(cb, ac) < 0.0f;
	}

	// project p onto the line along v through o, return it in proj.
	public void proj_onto_line(float[] proj, float[] o, float[] v, float[] p) {
		float op[] = new float[3];
		vec3f_diff(op, o, p);
		float scalar = vec3f_dot(op, v) / vec3f_dot(v, v);

		proj[0] = o[0] + scalar * v[0];
		proj[1] = o[1] + scalar * v[1];
		proj[2] = o[2] + scalar * v[2];
	}

	//
	// #pragma mark -

	//

	// #define mirror(f,n) ((f)->index[(n)])

	// Given a vertex, this routine returns a colocated vertex from the
	// neighboring triangle
	// if the mesh is circulated around V counter-clockwise.
	//
	// null is returned if there is no adjacent triangle to V's triangle in the
	// CCW direction.
	// Note that when a line 'creases' the mesh, the triangles are _not_
	// connected, so we
	// get back null.
	//
	// the int pointed to by did_reverse is set to 0 if v's and the return
	// vertex's triangle
	// have the same winding direction; it is set to 1 if the winding direction
	// of the two
	// tris is opposite.
	// todo
	public static Vertex circulate_ccw(Vertex v, IntBuffer did_reverse) {
		// .------V,M We use "leading neighbor" syntax, so (2) is the cw(v)
		// neighbor of 1.
		// \ / \ Conveniently, "M" is the defining vertex for edge X as defined
		// by 2.
		// \ 1 x \ So 1->neigbhor(cw(v)) is M's index.
		// \ / 2 \ One special case: if we are flipped, we need to go CCW from
		// 'M'.
		// cw-------.

		Face face_1 = v.getFace();
		int cw = CW(face_1, v.getIndex());
		Face face_2 = face_1.neighbor[cw];
		assert (face_2 != null);
		if (face_2 == null)
			return null;
		int M = v.face.index[cw];

		// int face1_flip[] = face_1.getFlip();
		did_reverse.put(0, face_1.flip[cw]);

		Vertex ret = (face_1.flip[cw] != 0) ? face_2.vertex[CCW(face_2, M)]
				: face_2.vertex[M];
		assert (MeshSmooth.compare_points(v.getLocation(), 0,
				ret.getLocation(), 0) == 0);
		assert (ret != v);
		return ret;
	}

	// Same as above, but the circulation is done in the clockwise direction.
	public static Vertex circulate_cw(Vertex v, IntBuffer did_reverse) {
		// .-------V V itself defines the edge we want to traverse, but M is out
		// of position - to
		// \ / \ recover V we want CCW(M). But if we are flipped, we just need M
		// itself.
		// \ 2 x \ ...
		// \ / 1 \ ...
		// M-------.

		Face face_1 = v.face;
		Face face_2 = face_1.neighbor[v.getIndex()];
		assert (face_2 != null);
		if (face_2 == null)
			return null;
		int M = v.face.index[v.getIndex()];
		did_reverse.put(0, face_1.flip[v.getIndex()]);
		Vertex ret = (face_1.flip[v.getIndex()] != 0) ? face_2.vertex[M]
				: face_2.vertex[CCW(face_2, M)];
		assert (MeshSmooth.compare_points(v.getLocation(), 0,
				ret.getLocation(), 0) == 0);
		assert (ret != v);
		return ret;
	}

	// This routine circulates V in the CCW direction if *dir is 1, and CW
	// if *dir is 0. Return semantics match the circulators above.
	// If the next triangle reverses winding, *dir is negated so that an
	// additional call with *dir's new value will have the same _effective_
	// direction.
	public static Vertex circulate_any(Vertex v, IntBuffer dir) {
		IntBuffer did_reverse = IntBuffer.allocate(1);
		Vertex ret;

		if (dir.get(0) > 0)
			ret = circulate_ccw(v, did_reverse);
		else
			ret = circulate_cw(v, did_reverse);

		if (did_reverse.get() != 0)
			dir.put(0, dir.get(0) * -1);
		return ret;
	}

	//
	// #pragma mark -
	// //==============================================================================
	// // VALIDATION UTILITIES
	// //==============================================================================
	// //
	// // These routines validate that the invariances of the internal mesh
	// structure
	// // haven't been stomped on. They are meant only for debug builds, slow
	// the algo
	// // down, and call a fatal assert() when things go bad.
	//
	// #if DEBUG
	//
	// // Validates that the meshes are sorted in ascending order according to
	// all
	// // ten params (vertex, normal and color.
	// void validate_vertex_sort_10(struct Mesh * mesh)
	// {
	// int i;
	// for(i = 1; i < mesh->vertex_count; ++i)
	// {
	// assert(compare_vertices(mesh->vertices+i-1,mesh->vertices+i) <= 0);
	// }
	// }
	//
	// // Validates that the meshes are sorted in ascending order according to
	// // position, ignoring normal and color.
	// void validate_vertex_sort_3(struct Mesh * mesh)
	// {
	// int i;
	// for(i = 1; i < mesh->vertex_count; ++i)
	// {
	// int comp =
	// compare_points(mesh->vertices[i-1].getLocation(),mesh->vertices[i].getLocation());
	// if(comp > 0)
	// {
	// assert(!"out of order");
	// }
	// }
	// }
	//
	// // This verifies that all vertices and faces link to each other
	// symetrically.
	// void validate_vertex_links(struct Mesh * mesh)
	// {
	// int i, j;
	// for(i = 0; i < mesh->vertex_count; ++i)
	// {
	// assert(mesh->vertices[i].face.vertex[mesh->vertices[i].index] ==
	// mesh->vertices+i);
	// }
	// for(i = 0; i < mesh->face_count; ++i)
	// for(j = 0; j < mesh->faces[i].degree; ++j)
	// {
	// assert(mesh->faces[i].vertex[j]->face == mesh->faces+i);
	// }
	// }
	//
	// // This validates that all neighboring triangles share the same
	// // located vertices on their shared edge, the shared edge index
	// // number is sane, and that the "reversed" flag is set only when
	// // the winding order really is reversed.
	// void validate_neighbors(struct Mesh * mesh)
	// {
	// int f,i;
	// for(f = 0; f < mesh->face_count; ++f)
	// for(i = 0; i < mesh->faces[f].degree; ++i)
	// {
	// Face face = mesh->faces+f;
	// if(face.neighbor[i] && face.neighbor[i] != null)
	// {
	// Face n = face.neighbor[i];
	// int ni = face.index[i];
	// assert(n->neighbor[ni] == face);
	//
	// assert(face.flip[i] == n->flip[ni]);
	//
	// Vertex p1 = face.vertex[ i ];
	// Vertex p2 = face.vertex[CCW(face,i)];
	//
	// Vertex n1 = n->vertex[ ni ];
	// Vertex n2 = n->vertex[CCW(n,ni)];
	//
	// int okay_fwd =
	// (compare_points(n1.getLocation(),p2.getLocation()) == 0) &&
	// (compare_points(n2.getLocation(),p1.getLocation()) == 0);
	//
	// int okay_rev =
	// (compare_points(n1.getLocation(),p1.getLocation()) == 0) &&
	// (compare_points(n2.getLocation(),p2.getLocation()) == 0);
	//
	// assert(!okay_fwd || face.flip[i] == 0);
	// assert(!okay_rev || face.flip[i] == 1);
	//
	// assert(okay_fwd != okay_rev);
	// #if WANT_CREASE
	// assert(!okay_fwd || vec3f_dot(face.normal,n->normal) > 0.0);
	// assert(!okay_rev || vec3f_dot(face.normal,n->normal) < 0.0);
	// #endif
	// }
	// }
	// }
	// #endif
	//
	// #pragma mark -
	// //==============================================================================
	// // MAIN API IMPLEMENTATION
	// //==============================================================================
	//

	// // Add one face to the mesh. Quads and tris can be added in any order but
	// all
	// // quads and tris (polygons) must be added before all lines.
	// // When passing a face, simply pass null for any 'extra' vertices - that
	// is,
	// // to create a line, pass null for p3 and p4; to create a triangle, pass
	// null for
	// // p3. The color is the color of the entire face in RGBA; the face normal
	// is
	// // computed for you.
	// //
	// // tid is the 'texture ID', a 0-based counted number identifying which
	// texture
	// // state this face gets. Texture IDs must be consecutive, zero based and
	// // positive, but do not need to be submitted in any particular order, and
	// the
	// // highest TID does not have to be pre-declared; the library simply
	// watches
	// // the TIDs on input.
	// //
	// // Technically lines have TIDs as well - typically TID 0 is used to mean
	// the
	// // 'untextured texture group' and is used for lines and untextured
	// polygons.
	// //
	// // The TIDs are used to output sets of draw commands that share common
	// texture state -
	// // that is, faces, quads and lines are ouput in TID order.
	// void add_face(struct Mesh * mesh, float p1[3], float p2[3], float p3[3],
	// float p4[3], float color[4], int tid)
	// {
	// #if SLOW_CHECKING
	// if(vec3f_length2(p1,p2) <= EPSI2) mesh->flags |= TINY_INITIAL_TRIANGLE;
	// if(p3)
	// {
	// if(vec3f_length2(p1,p3) <= EPSI2) mesh->flags |= TINY_INITIAL_TRIANGLE;
	// if(vec3f_length2(p2,p3) <= EPSI2) mesh->flags |= TINY_INITIAL_TRIANGLE;
	// }
	// if(p4)
	// {
	// if(vec3f_length2(p1,p4) <= EPSI2) mesh->flags |= TINY_INITIAL_TRIANGLE;
	// if(vec3f_length2(p2,p4) <= EPSI2) mesh->flags |= TINY_INITIAL_TRIANGLE;
	// if(vec3f_length2(p3,p4) <= EPSI2) mesh->flags |= TINY_INITIAL_TRIANGLE;
	// }
	// #endif
	// int i;
	//
	//
	// // grab a new face, grab verts for it
	// Face f = mesh->faces + mesh->face_count++;
	// f->tid = tid;
	// if(tid > mesh->highest_tid)
	// mesh->highest_tid = tid;
	// if(p3)
	// {
	// float v1[3] = { p2[0]-p1[0],p2[1]-p1[1],p2[2]-p1[2]};
	// float v2[3] = { p3[0]-p1[0],p3[1]-p1[1],p3[2]-p1[2]};
	// vec3_cross(f->normal,v1,v2);
	// vec3f_normalize(f->normal);
	// }
	// else
	// {
	// f->normal[0] = f->normal[2] = 0.0f;
	// f->normal[1] = 1.0f;
	// }
	//
	// f->degree = p4 ? 4 : (p3 ? 3 : 2);
	//
	// f->vertex[0] = mesh->vertices + mesh->vertex_count++;
	// f->vertex[1] = mesh->vertices + mesh->vertex_count++;
	// f->vertex[2] = p3 ? mesh->vertices + mesh->vertex_count++ : null;
	// f->vertex[3] = p4 ? (mesh->vertices + mesh->vertex_count++) : null;
	//
	// f->neighbor[0] = f->neighbor[1] = f->neighbor[2] = f->neighbor[3] =
	// null;
	// f->t_list[0] = f->t_list[1] = f->t_list[2] = f->t_list[3] = null;
	//
	// f->index[0] = f->index[1] = f->index[2] = f->index[3] = -1;
	// f->flip[0] = f->flip[1] = f->flip[2] = f->flip[3] = -1;
	//
	// vec4f_copy(f->color, color);
	//
	//
	// for(i = 0; i < f->degree; ++i)
	// {
	// vec3f_copy(f->vertex[i]->normal,f->normal);
	// vec4f_copy(f->vertex[i]->color,color);
	// f->vertex[i]->prev = f->vertex[i]->next = null;
	// }
	//
	// vec3f_copy(f->vertex[0].getLocation(),p1);
	// vec3f_copy(f->vertex[1].getLocation(),p2);
	// if(p3)
	// vec3f_copy(f->vertex[2].getLocation(),p3);
	// if(p4)
	// vec3f_copy(f->vertex[3].getLocation(),p4);
	//
	// f->vertex[0]->index = 0;
	// f->vertex[1]->index = 1;
	// if(f->vertex[2])
	// f->vertex[2]->index = 2;
	// if(f->vertex[3])
	// f->vertex[3]->index = 3;
	//
	// f->vertex[0]->face =
	// f->vertex[1]->face = f;
	// if(f->vertex[2])
	// f->vertex[2]->face = f;
	// if(f->vertex[3])
	// f->vertex[3]->face = f;
	// }
	//
	// // Utility: this is the visior used to snap vertices to each other.
	// // Snapping is done by linking nearby vertices into a ring whose
	// // centroid is later found.
	// public static void visit_vertex_to_snap(Vertex v, void * ref)
	// {
	// Vertex o = (Vertex) ref;
	// Vertex p, * n;
	// if(o != v)
	// {
	// assert(!vec3f_eq(o.getLocation(),v.getLocation()));
	//
	// if(vec3f_length2(o.getLocation(), v.getLocation()) < EPSI2)
	// {
	//
	// // Check if o is already in v's sybling list BEFORE v. If so, bail.
	// for(n = v.prev; n; n = n->prev)
	// if(n == o)
	// return;
	//
	// // Scan forward to find last node in v's list.
	// n = v;
	// assert(n != o);
	// while(n->next)
	// {
	// n = n->next;
	// if(n == o) // Already connected to o? Eject!
	// return;
	// }
	//
	// p = o;
	// assert(p != v);
	// while(p->prev)
	// {
	// p = p->prev;
	// assert(p != v); // this would imply our linkage is not doubly linked.
	// }
	//
	// assert(n->next == null);
	// assert(p->prev == null);
	// n->next = p;
	// p->prev = n;
	// }
	// }
	// }
	//
	// // This function does a bunch of post-geometry-adding processing:
	// // 1. It sorts the vertices in XYZ order for correct indexing. This
	// // forces colocated vertices together in the list.
	// // 2. It indexes vertices into an R-tree.
	// // 3. It performs a two-step snapping process by
	// // 3a. Locating rings of too-close vertices and
	// // 3b. Setting each member of the ring to the ring's centroid location.
	// // 4. Vertices are resorted AGAIN.
	// // 5. The links from faces to vertices must be rebuilt due to sorting.
	// // 6. Degenerate quads/tris are marked as 'creased' on all sides.
	// //
	// // Notes:
	// // 2 and 4 are BOTH necessary - the first sort is needed to pre-sorted
	// // the data for the R-tree interface.
	// // The second sort is needed because the order of sort is ruined by
	// // changing XYZ geometry locations.
	// //
	// // Re: 6, we don't want to delete degenerate quads (a degen quad
	// // might be a visible triangle) but passing degenerate geometry to the
	// // smoother causes problems - so instead we 'seal off' this geometry to
	// // avoid further problems.
	// void finish_faces_and_sort(struct Mesh * mesh)
	// {
	// int v, f;
	// int total_before = 0, total_after = 0;
	//
	// // sort vertices by 10 params
	// sort_vertices_3(mesh->vertices,mesh->vertex_count);
	//
	// mesh->index = index_vertices(mesh->vertices,mesh->vertex_count);
	//
	// #if DEBUG
	// validate_vertex_sort_3(mesh);
	// #endif
	//
	//
	// for(v = 0; v < mesh->vertex_count; ++v)
	// {
	// if(v == 0 ||
	// compare_points(mesh->vertices[v-1].getLocation(),mesh->vertices[v].getLocation())
	// != 0)
	// {
	// ++total_before;
	// Vertex vi = mesh->vertices + v;
	// float mib[3] = { vi.getLocation()[0] - EPSI, vi.getLocation()[1] - EPSI,
	// vi.getLocation()[2] - EPSI };
	// float mab[3] = { vi.getLocation()[0] + EPSI, vi.getLocation()[1] + EPSI,
	// vi.getLocation()[2] + EPSI };
	// scan_rtree(mesh->index, mib, mab, visit_vertex_to_snap, vi);
	// }
	// }
	//
	// for(v = 0; v < mesh->vertex_count; ++v)
	// if(v == 0 ||
	// compare_points(mesh->vertices[v-1].getLocation(),mesh->vertices[v].getLocation())
	// != 0)
	// if(mesh->vertices[v].prev == null)
	// {
	// if(mesh->vertices[v].next != null)
	// {
	// Vertex i;
	// float count = 0.0f;
	// float p[3] = { 0 };
	// for(i=mesh->vertices+v;i;i=i->next)
	// {
	// count += 1.0f;
	// p[0] += i.getLocation()[0];
	// p[1] += i.getLocation()[1];
	// p[2] += i.getLocation()[2];
	// }
	//
	// assert(count > 0.0f);
	// count = 1.0f / count;
	// p[0] *= count;
	// p[1] *= count;
	// p[2] *= count;
	//
	// i = mesh->vertices+v;
	// while(i)
	// {
	// int has_more = 0;
	// Vertex k = i;
	// i = i->next;
	// do
	// {
	// has_more =
	// (k+1) < mesh->vertices+mesh->vertex_count &&
	// compare_points(k.getLocation(),(k+1).getLocation()) == 0;
	//
	// k.getLocation()[0] = p[0];
	// k.getLocation()[1] = p[1];
	// k.getLocation()[2] = p[2];
	// k->prev = null;
	// k->next = null;
	// ++k;
	// } while(has_more);
	//
	// }
	// }
	//
	// ++total_after;
	// }
	// // printf("BEFORE: %d, AFTER: %d\n", total_before, total_after);
	//
	// sort_vertices_3(mesh->vertices,mesh->vertex_count);
	//
	// // then re-build ptr indices into faces since we moved vertices
	// for(v = 0; v < mesh->vertex_count; ++v)
	// {
	// mesh->vertices[v].face.vertex[mesh->vertices[v].index] =
	// mesh->vertices+v;
	// }
	//
	// for(f = 0; f < mesh->face_count; ++f)
	// {
	// if(mesh->faces[f].degree == 3)
	// {
	// float [] p1 = mesh->faces[f].vertex[0].getLocation();
	// float [] p2 = mesh->faces[f].vertex[1].getLocation();
	// float [] p3 = mesh->faces[f].vertex[2].getLocation();
	// if (compare_points(p1,p2)==0 ||
	// compare_points(p2,p3)==0 ||
	// compare_points(p1,p3)==0)
	// {
	// mesh->faces[f].neighbor[0] =
	// mesh->faces[f].neighbor[1] =
	// mesh->faces[f].neighbor[2] =
	// mesh->faces[f].neighbor[3] = null;
	// }
	//
	// }
	// if(mesh->faces[f].degree == 4)
	// {
	// float [] p1 = mesh->faces[f].vertex[0].getLocation();
	// float [] p2 = mesh->faces[f].vertex[1].getLocation();
	// float [] p3 = mesh->faces[f].vertex[2].getLocation();
	// float [] p4 = mesh->faces[f].vertex[3].getLocation();
	//
	// if (compare_points(p1,p2)==0 ||
	// compare_points(p2,p3)==0 ||
	// compare_points(p1,p3)==0 ||
	// compare_points(p3,p4)==0 ||
	// compare_points(p2,p4)==0 ||
	// compare_points(p1,p4)==0)
	// {
	// mesh->faces[f].neighbor[0] =
	// mesh->faces[f].neighbor[1] =
	// mesh->faces[f].neighbor[2] =
	// mesh->faces[f].neighbor[3] = null;
	// }
	// }
	// }
	//
	// #if DEBUG
	// validate_vertex_sort_3(mesh);
	// validate_vertex_links(mesh);
	//
	// #if SLOW_CHECKING
	// {
	// int i, j;
	// for(i = 0; i < mesh->vertex_count; ++i)
	// for(j = 0; j < i; ++j)
	// {
	// if(!vec3f_eq(mesh->vertices[i].getLocation(),mesh->vertices[j].getLocation()))
	// {
	// if(vec3f_length2(mesh->vertices[i].getLocation(),mesh->vertices[j].getLocation())
	// <= CHECK_EPSI2)
	// mesh->flags |= TINY_RESULTING_GEOM;
	// }
	// }
	// }
	// #endif
	//
	// #endif
	//
	// }
	//

	//
	// // Once all creases have been marked, this routine locates all colocated
	// mesh
	// // edges going in opposite directions (opposite direction colocated edges
	// mean
	// // the faces go in the same direction) that are not already marked as
	// neighbors
	// // or creases. If the potential join between faces is too sharp, it is
	// marked
	// // as a crease, otherwise the edges are recorded as neighbors of each
	// other.
	// // When we are done every polygon edge is a crease or neighbor of
	// someone.
	// void finish_creases_and_join(struct Mesh * mesh)
	// {
	// int fi;
	// int i;
	// Face f;
	//
	// for(fi = 0; fi < mesh->poly_count; ++fi)
	// {
	// f = mesh->faces+fi;
	// assert(f->degree >= 3);
	// for(i = 0; i < f->degree; ++i)
	// {
	// if(f->neighbor[i] == null)
	// {
	// // CCW(i)/P1
	// // / \ The directed edge we want goes FROM i TO ccw.
	// // / i So p2 = ccw, p1 = i, that is, we want our OTHER
	// // / \ neighbor to go FROM cw TO CCW
	// // .---------i/P2
	//
	// Vertex p1 = f->vertex[CCW(f,i)];
	// Vertex p2 = f->vertex[ i ];
	// Vertex begin, * end, * v;
	// //
	// range_for_point(mesh->vertices,mesh->vertex_count,&begin,&end,p1.getLocation());
	// range_for_vertex(mesh->vertices,mesh->vertices +
	// mesh->vertex_count,&begin,&end,p1);
	// for(v = begin; v != end; ++v)
	// {
	// if(v.face == f)
	// continue;
	//
	// // P1/v-----x Normal case - Since p1->p2 is the ideal direction of our
	// // \ / neighbor, p2 = ccw(v). Thus p1(v) names our edge.
	// // v /
	// // \ /
	// // P2/CCW(V)
	//
	// // P1/v-----x Backward winding case - thus p2 is CW from P1,
	// // \ / and P2 (cw(v) names our edge.
	// // cw(v) /
	// // \ /
	// // P2/CW(V)
	//
	//
	// assert(compare_points(p1.getLocation(),v.getLocation())==0);
	//
	// Face n = v.face;
	// Vertex dst = n->vertex[CCW(n,v.getIndex())];
	// #if WANT_INVERTS
	// Vertex inv = n->vertex[ CW(n,v.getIndex())];
	// #endif
	// if(dst->face.degree > 2)
	// if(compare_points(dst.getLocation(),p2.getLocation())==0)
	// {
	// int ni = v.getIndex();
	// assert(f->neighbor[i] == null);
	// if(n->neighbor[ni] == null)
	// {
	// #if WANT_CREASE
	// if(is_crease(f->normal,n->normal,false))
	// {
	// f->neighbor[i] = null;
	// n->neighbor[ni] = null;
	// f->index[i] = -1;
	// n->index[ni] = -1;
	// break;
	// }
	// else
	// #endif
	// {
	// // v.dst matches p1->p2. We have neighbors.
	// // Store both - avoid half the work when we get to our neighbor.
	// f->neighbor[i] = n;
	// n->neighbor[ni] = f;
	// f->index[i] = ni;
	// n->index[ni] = i;
	// f->flip[i] = 0;
	// n->flip[ni] = 0;
	// break;
	// }
	// }
	// }
	// #if WANT_INVERTS
	// if(inv.face.degree > 2)
	// if(compare_points(inv.getLocation(),p2.getLocation())==0)
	// {
	// int ni = CW(v.face,v.getIndex());
	// assert(f->neighbor[i] == null);
	// if(n->neighbor[ni] == null)
	// {
	// #if WANT_CREASE
	// if(is_crease(f->normal,n->normal,true))
	// {
	// f->neighbor[i] = null;
	// n->neighbor[ni] = null;
	// f->index[i] = -1;
	// n->index[ni] = -1;
	// break;
	// }
	// else
	// #endif
	// {
	// // v.dst matches p1->p2. We have neighbors.
	// // Store both - avoid half the work when we get to our neighbor.
	// f->neighbor[i] = n;
	// n->neighbor[ni] = f;
	// f->index[i] = ni;
	// n->index[ni] = i;
	// f->flip[i] = 1;
	// n->flip[ni] = 1;
	// break;
	// }
	// }
	// }
	// #endif
	//
	// }
	// }
	// if(f->neighbor[i] == null)
	// {
	// f->neighbor[i] = null;
	// f->index[i] = -1;
	// }
	// }
	// }
	//
	// #if DEBUG
	// validate_neighbors(mesh);
	// #endif
	// }
	//
	// Utility function: given a vertex (for a specific face) this
	// returns a relative weighting for smoothing based on the normal
	// of this tri. This is doen using trig - the angle that the triangle
	// circulates around the vertex defines its contribution to smoothing.
	// This ensures subdivision of triangles produces weights that sum to be
	// equal to the original face, and thus subdivision doesn't change our
	// normals (which is what we would hope for).
	public static float weight_for_vertex(Vertex v) {
		return 1f;

		// Vertex prev = v.face.vertex[CCW(v.face, v.getIndex())];
		// Vertex next = v.face.vertex[CW(v.face, v.getIndex())];
		// float v1[], v2[], d;
		// v1 = new float[3];
		// v2 = new float[3];
		//
		// vec3f_diff(v1, v.getLocation(), prev.getLocation());
		// vec3f_diff(v2, v.getLocation(), next.getLocation());
		// vec3f_normalize(v1);
		// vec3f_normalize(v2);
		//
		// d = vec3f_dot(v1, v2);
		// if (d > 1.0f)
		// d = 1.0f;
		// if (d < -1.0f)
		// d = -1.0f;
		// return (float) Math.acos(d);
	}
	//
	// // Once all neighbors have been found, this routine calculates the
	// // actual per-vertex smooth normals. This is done by circulating
	// // each vertex (via its neighbors) to find all contributing triangles,
	// // computing a weighted average (from for each triangle) and applying
	// // the new averaged normal to all participating vertices.
	// //
	// // A few key points:
	// // - Circulation around the vertex only goes by neigbhor. So creases
	// // (lack of a neighbor) partition the triangles around our vertex into
	// // adjacent groups, each of which get their own smoothing.
	// // - This is what makes a 'creased' shape flat-shaded: the creases keep
	// // us from circulating more than one triangle.
	// // - We weight our average normal by the angle the triangle spans around
	// // the vertex, not just a straight average of all participating
	// triangles.
	// // We do not want to bias our normal toward the direction of more small
	// // triangles.
	// void smooth_vertices(struct Mesh * mesh)
	// {
	// int f;
	// int i;
	// for(f = 0; f < mesh->poly_count; ++f)
	// for(i = 0; i < mesh->faces[f].degree; ++i)
	// {
	// // For each vertex, we are going to circulate around attached faces,
	// averaging up our normals.
	//
	// Vertex v = mesh->faces[f].vertex[i];
	//
	// // First, go clock-wise around, starting at ourselves, until we loop back
	// on ourselves (a closed smooth
	// // circuite - the center vert on a stud top is like this) or we run out
	// of vertices.
	//
	// Vertex c = v;
	// float N[3] = { 0 };
	// int ctr = 0;
	// int circ_dir = -1;
	// float w;
	// do {
	// ++ctr;
	// //printf("\tAdd: %f,%f,%f\n",c->normal[0],c->normal[1],c->normal[2]);
	//
	// w = weight_for_vertex(c);
	//
	// if(vec3f_dot(v.face.normal,c->face.normal) > 0.0)
	// {
	// N[0] += w*c->face.normal[0];
	// N[1] += w*c->face.normal[1];
	// N[2] += w*c->face.normal[2];
	// }
	// else
	// {
	// N[0] -= w*c->face.normal[0];
	// N[1] -= w*c->face.normal[1];
	// N[2] -= w*c->face.normal[2];
	// }
	//
	// c = circulate_any(c,&circ_dir);
	//
	// } while(c != null && c != v);
	//
	// // Now if we did NOT make it back to ourselves it means we are a
	// disconnected circulation. For example
	// // a semi-circle fan's center will do this if we start from a middle tri.
	// // Circulate in the OTHER direction, skipping ourselves, until we run
	// out.
	//
	// if(c != v)
	// {
	// circ_dir = 1;
	// c = circulate_any(v,&circ_dir);
	// while(c)
	// {
	// ++ctr;
	// //printf("\tAdd: %f,%f,%f\n",c->normal[0],c->normal[1],c->normal[2]);
	// w = weight_for_vertex(c);
	// if(vec3f_dot(v.face.normal,c->face.normal) > 0.0)
	// {
	// N[0] += w*c->face.normal[0];
	// N[1] += w*c->face.normal[1];
	// N[2] += w*c->face.normal[2];
	// }
	// else
	// {
	// N[0] -= w*c->face.normal[0];
	// N[1] -= w*c->face.normal[1];
	// N[2] -= w*c->face.normal[2];
	// }
	//
	// c = circulate_any(c,&circ_dir);
	//
	// // Invariant: if we did NOT close-loop up top, we should NOT close-loop
	// down here - that would imply
	// // a triangulation where our neighbor info was assymetric, which would be
	// "bad".
	// assert(c != v);
	// }
	// }
	//
	// vec3f_normalize(N);
	// //printf("Final: %f %f %f\t%f %f %f (%d)\n",v.getLocation()[0],v.getLocation()[1],
	// v.getLocation()[2], N[0],N[1],N[2], ctr);
	// v.normal[0] = N[0];
	// v.normal[1] = N[1];
	// v.normal[2] = N[2];
	// #if DEBUG_SHOW_NORMALS_AS_COLOR
	// v.color[0] = N[0] * 0.5 + 0.5;
	// v.color[1] = N[1] * 0.5 + 0.5;
	// v.color[2] = N[2] * 0.5 + 0.5;
	// v.color[3] = 1.0f;
	// #endif
	//
	// }
	// }
	//
	// // This routine merges vertices that have the same complete (10-float)
	// // value to optimize down the size of geometry in VRAM. We do this
	// // by resorting by all components and then recognizing adjacently equal
	// // vertices. This routine rebuilds the ptrs from triangles so that all
	// // faces see the 'shared' vertex. By convention the first of a group
	// // of equal vertices in the vertex array is the one we will keep/use.
	// void merge_vertices(struct Mesh * mesh)
	// {
	// // Once smoothing is done, indexing the mesh is actually pretty easy:
	// // First, we re-sort the vertex list; now that our normals and colors
	// // are consolidated, equal-value vertices in the final mesh will pool
	// // together.
	// //
	// // Then (having moved our vertices) we need to rebuild the face.vertex
	// // pointers...when we do this, we simply use the FIRST vertex among
	// // equals for every face.
	// //
	// // The result is that for every redundent vertex, every face will
	// // agree on which ptr to use. This means that we can build an indexed
	// // mesh off of the ptrs and get maximum sharing.
	//
	// int v;
	//
	// int unique = 0;
	// Vertex first_of_equals = mesh->vertices;
	//
	// // Resort according ot our xyz + normal + color
	// sort_vertices_10(mesh->vertices,mesh->vertex_count);
	//
	// // Re-set the tri ptrs again, but...for each IDENTICAL source vertex, use
	// the FIRST of them as the ptr
	// for(v = 0; v < mesh->vertex_count; ++v)
	// {
	// if(compare_vertices(first_of_equals, mesh->vertices+v) != 0)
	// {
	// first_of_equals = mesh->vertices+v;
	// }
	// mesh->vertices[v].face.vertex[mesh->vertices[v].index] = first_of_equals;
	// if(mesh->vertices+v == first_of_equals)
	// {
	// mesh->vertices[v].index = -1;
	// ++unique;
	// }
	// else
	// mesh->vertices[v].index = -2;
	// }
	//
	// #if DEBUG
	// validate_vertex_sort_10(mesh);
	// #endif
	// mesh->unique_vertex_count = unique;
	// //printf("Before: %d vertices, after: %d\n", mesh->vertex_count, unique);
	// }
	//
	// // This returns the final counts for vertices and indices in a mesh -
	// after merging,
	// // subdivising, etc. our original counts may be changed, so clients need
	// to know
	// // how much VBO space to allocate.
	// void get_final_mesh_counts(struct Mesh * m, int []total_vertices,int
	// []total_indices)
	// {
	// *total_vertices = m->unique_vertex_count;
	// *total_indices = m->vertex_count;
	// }
	//
	// // This cleans our mesh, deallocating all internal memory.
	// void destroy_mesh(struct Mesh * mesh)
	// {
	// int f,i;
	// #if DEBUG
	// #if SLOW_CHECKING
	// if(mesh->flags & TINY_INITIAL_TRIANGLE)
	// printf("ERROR: TINY INITIAL TRIANGLE.\n");
	// if(mesh->flags & TINY_RESULTING_GEOM)
	// printf("ERROR: TINY RESULTING GEOMETRY.\n");
	// #endif
	// #endif
	//
	// destroy_rtree(mesh->index);
	//
	// for(f = 0; f < mesh->face_count; ++f)
	// {
	// Face fp = mesh->faces+f;
	// for(i = 0; i < fp->degree; ++i)
	// {
	// struct VertexInsert * tj, *k;
	// tj = fp->t_list[i];
	// while(tj)
	// {
	// k = tj;
	// tj = tj->next;
	// free(k);
	// }
	// }
	// }
	//
	// free(mesh->vertices);
	// free(mesh->faces);
	// free(mesh);
	// }
	//

	//
	//
	//
	// #pragma mark -
	// //==============================================================================
	// // T JUNCTION REMOVAL
	// //==============================================================================
	//
	// // This code attempts to remove "T" junctions from a mesh. Since an ASCII
	// // picture is worth 1000 words...
	// //
	// // B B
	// // /|\ /|\
	// // / | \ / | \
	// // A C--E -> A--C--E
	// // \ | / \ | /
	// // \|/ \|/
	// // D D
	// //
	// // Given 3 triangles ADB, BCE, and CDE, we have a T junction at "C" - the
	// vertex C
	// // is colinear with hte edge DB (as part of ADB), and this is bad. This
	// is bad
	// // because: (1) C's normal contributions won't include ADB (since C is
	// not a vertex
	// // of ADB) and (2) we may get a cracking artifact at C from the graphics
	// card.
	// //
	// // We try to fix this by subdividing DB at C to produce two triangles ADC
	// and ACB.
	// //
	// // IMHO: T junction removal sucks. It's hard to set up the heuristics to
	// get good
	// // junction removal, huge numbers of tiny triangles can be added, and the
	// models
	// // that need it are usually problematic enough that it doesn't work,
	// while slowing
	// // down smoothing and increasing vertex count. I recommend that clients
	// _not_ use
	// // this functionality - rather T junctions should probably be addressed
	// by:
	// //
	// // 1. Fixing the source parts that cause T junctions and
	// // 2. Aggressively adopting textures for complex patterned parts.
	// //
	//
	//
	// // When we are looking for T junctions, we use this structure to
	// 'remember' which
	// // edge we are working on from the R-tree visitor.
	//
	// struct t_finder_info_t {
	// int split_quads; // The number of quads that have been split. Each quad
	// with a
	// // subdivision must be triangulated, changing our face count, so
	// // we have to track this.
	// int inserted_pts; // Number of points inserted into edges - this
	// increases triangle
	// // count so we must track it.
	// Vertex v1; // Start and end vertices of the edge we are testing.
	// Vertex v2;
	// Face f; // The face that v1/v2 belong to.
	// int i; // The side index i of face f that we are tracking, e.g.
	// f->vertices[i] == v1
	// float line_dir[3]; // A normalized direction vector from v1 to v2, used
	// to order the intrusions.
	// };
	//
	//
	// // This is the call back that gets called once for each vertex V that
	// _might_ be near an edge (e.g.
	// // via a bounding box test). We project the point onto the line and see
	// how far the intruding point
	// // is from the projection on the line. If the point is close, it's a T
	// junction and we record it
	// // on a linked list ?or this side, in order of distanec along the line.
	// //
	// // Since (1) duplicate vertices are not processed and (2) near-duplicate
	// vertices were removed long ago
	// // and (3) the bounding box ensures that our point is 'within' the line
	// segment's span, any near-line
	// // point _is_ a T.
	// void visit_possible_t_junc(Vertex v, void * ref)
	// {
	// struct t_finder_info_t * info = (struct t_finder_info_t *) ref;
	// assert(!vec3f_eq(info.getLocation()_v1,info.getLocation()_v2));
	// if(!vec3f_eq(v.getLocation(),info.getLocation()_v1) &&
	// !vec3f_eq(v.getLocation(),info.getLocation()_v2))
	// if(in_between_line(info.getLocation()_v1,v.getLocation(),info.getLocation()_v2))
	// {
	// float proj_p[3];
	// proj_onto_line(proj_p, info.getLocation()_v1,info->line_dir,
	// v.getLocation());
	//
	// float dist2_lat = vec3f_length2(v.getLocation(), proj_p);
	// float dist2_lon = vec3f_length2(v.getLocation(), info.getLocation()_v1);
	//
	// if (dist2_lat < EPSI2)
	// {
	// assert(info->f->degree == 4 || info->f->degree == 3);
	// if(info->f->degree == 4)
	// if(info->f->t_list[0] == null &&
	// info->f->t_list[1] == null &&
	// info->f->t_list[2] == null &&
	// info->f->t_list[3] == null)
	// ++info->split_quads;
	// ++info->inserted_pts;
	// struct VertexInsert ** prev = &info->f->t_list[info->i];
	//
	// while(*prev && (*prev)->dist < dist2_lon)
	// prev = &(*prev)->next;
	//
	// struct VertexInsert * vi = (struct VertexInsert *) malloc(sizeof(struct
	// VertexInsert));
	// vi->dist = dist2_lon;
	// vi->vert = v;
	// vi->next = *prev;
	// *prev = vi;
	//
	// // printf("possible T %f: %f,%f,%f (%f,%f,%f -> %f,%f,%f)\n",
	// // sqrtf(dist2_lon),
	// // v.getLocation()[0],v.getLocation()[1],v.getLocation()[2],
	// //
	// info.getLocation()_v1[0],info.getLocation()_v1[1],info.getLocation()_v1[2],
	// //
	// info.getLocation()_v2[0],info.getLocation()_v2[1],info.getLocation()_v2[2]);
	// }
	// }
	// }
	//
	// // Given a convex polygon (specified by an interleaved XYZ array "poly"
	// and pt_count points, this routine
	// // cuts down the degree of the polygon by cutting off an 'ear' (that is,
	// a non-reflex vertex). The ear is
	// // added as a triangle, and the polygon loses a vertex. This is done by
	// cutting off the sharpest corners
	// // first.
	// //
	// // BEFORE: AFTER
	// //
	// // A--B--C A--B
	// // | | | \
	// // | | | \
	// // D E D E
	// // | | | |
	// // F--G--H F--G--H
	// //
	// // (BEC is added as a new trinagle.)
	//

	//
	//

}
