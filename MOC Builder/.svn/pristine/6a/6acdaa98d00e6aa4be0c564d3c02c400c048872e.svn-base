package Renderer;

import java.util.ArrayList;

public class RTree {
	/**
	 * @uml.property name="rootNode"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	RTree_node rootNode;

	public RTree() {
		rootNode = new RTree_node();
	}

	// ==============================================================================
	// R-TREE ROUTINES
	// ==============================================================================

	public RTree(ArrayList<Vertex> vertices, int vertex_count) {
		index_vertices(vertices, vertex_count);
	}

	// This routine builds an R-tree node containing ptrs to all vertices within
	// begin/end. Begin/end
	// will be sorted multiple times as needed to accomplish this. "depth" is
	// the level on the tree -
	// we use this to resort the vertices. When called with a depth of 0 (first
	// call) it is expected
	// that the vertices are sorted by X coordinate already.
	// We return a node ptr with the lsb set or cleared depending on whether we
	// made an internal or
	// leaf node.
	public RTree_node index_vertices_recursive(ArrayList<Vertex> vertices, int begin,
			int end, int depth) {
		int i;
		int count = 0;
		if (end >= begin)
			count = end - begin;

		if (count <= RTree_leaf.LEAF_DIM) {
			// Leaf node case: we have so few nodes, we can fit them into a
			// single leaf.
			// / Build the leaf node, compute the bounding box, and return the
			// node with
			// its LSB set.
			RTree_leaf leaf = new RTree_leaf();
			float[] minBounds = leaf.getMinBounds();
			float[] maxBounds = leaf.getMaxBounds();

			float[] location = vertices.get(begin).getLocation();

			minBounds[0] = maxBounds[0] = location[0];
			minBounds[1] = maxBounds[1] = location[1];
			minBounds[2] = maxBounds[2] = location[2];

			leaf.setCount(count);
			for (i = 0; i < count; ++i, begin++) {
				location = vertices.get(begin).getLocation();
				minBounds[0] = Math.min(minBounds[0], location[0]);
				maxBounds[0] = Math.max(maxBounds[0], location[0]);
				minBounds[1] = Math.min(minBounds[1], location[1]);
				maxBounds[1] = Math.max(maxBounds[1], location[1]);
				minBounds[2] = Math.min(minBounds[2], location[2]);
				maxBounds[2] = Math.max(maxBounds[2], location[2]);

				leaf.setVertex(i, vertices.get(begin));
			}
			return (RTree_node) leaf;
		} else {
			// Intermediate node case. We will sort the nodes by X, Y, or Z
			// depending
			// on the axis - by changing the axis we naturally isolate vertices
			// in N dimensions.

			// Optimization: avoid one full sort of all vertices since we know
			// our source
			// input is passed in in X-sorted order!
			if (depth > 0)
				MeshSmooth.quickSort_n(vertices, begin, count-1, depth % 3);
			int split = count / 2;

			// Now recurse on each half of the vertices to get our two child
			// nodes.
			RTree_node left = index_vertices_recursive(vertices, begin, begin+split,
					depth + 1);
			RTree_node right = index_vertices_recursive(vertices, begin+split, end,
					depth + 1);

			// Build our node around our two child nodes; our bounds are the
			// union of our
			// child bounds. (We don't want to re-check the bounds of all of our
			// vertices.)
			RTree_node n = new RTree_node();
			n.setLeft(left);
			n.setRight(right);

			// todo
			// left = GET_CLEAN(left);
			// right = GET_CLEAN(right);

			float minBounds[] = new float[3];
			float maxBounds[] = new float[3];

			float minBounds_left[] = left.getMinBounds();
			float minBounds_right[] = right.getMinBounds();

			float maxBounds_left[] = left.getMaxBounds();
			float maxBounds_right[] = right.getMaxBounds();

			for (i = 0; i < 3; ++i) {
				minBounds[i] = Math.min(minBounds_left[i], minBounds_right[i]);
				maxBounds[i] = Math.max(maxBounds_left[i], maxBounds_right[i]);
			}
			n.setMinBounds(minBounds);
			n.setMaxBounds(maxBounds);
			return n;
		}
	}

	// Top-level call to index nodes. Returns the root node of our r-tree. See
	// note below about not indexing co-colocated nodes!!
	public RTree_node index_vertices(ArrayList<Vertex> vertices, int count) {
		int i;
		ArrayList<Vertex> arr = new ArrayList<Vertex>(count);
		RTree_node return_node;

		// We only R-tree the FIRST of a RANGE of points that are mathematically
		// equal.
		// Code doing the query can re-construct the rest of the range by
		// walking forward.
		// This cuts our R-tree down a LOT - in the case of the 48x48 baseplate
		// as a whole,
		// this cuts vertex count by 80% (!). Since the R-tree build is
		// O(NlogNlogN) - that
		// is, THE most time-complexity-expensive operation in the algo, this is
		// sort of a big deal.
		int index = 0;
		for (i = 0; i < count; ++i) {
			if (i == 0
					|| MeshSmooth.compare_points(vertices.get(i - 1).getLocation(),
							0, vertices.get(i).getLocation(), 0) != 0) {
				arr.add(vertices.get(i));
				index++;
			}
		}

		return_node = index_vertices_recursive(arr, 0, index, 0);
		this.rootNode = return_node;

		return return_node;
	}

	// Utility: Returns true if two 3-d AABBs (stored as min XYZ and max XYZ)
	// overlap, including
	// overlaps of their edges.
	public static int overlap(float b1_min[], float b1_max[], float b2_min[],
			float b2_max[]) {
		if (b1_min[0] > b2_max[0])
			return 0;
		if (b2_min[0] > b1_max[0])
			return 0;
		if (b1_min[1] > b2_max[1])
			return 0;
		if (b2_min[1] > b1_max[1])
			return 0;
		if (b1_min[2] > b2_max[2])
			return 0;
		if (b2_min[2] > b1_max[2])
			return 0;

		return 1;
	}

	// Returns true if a point is inside an AABB, or on its edges.
	public static int inside(float b1_min[], float b1_max[], float p[]) {
		if (p[0] >= b1_min[0] && p[0] <= b1_max[0])
			if (p[1] >= b1_min[1] && p[1] <= b1_max[1])
				if (p[2] >= b1_min[2] && p[2] <= b1_max[2])
					return 1;
		return 0;
	}

	// todo
	// R-tree scanning routine. A functor "visitor" is called (with "ref" passed
	// each time) for every vertex in the Rtree "N" whose bounds
	// are within (inclusive of edges) min_bounds -> max_bounds.
	// public void scan_rtree(RTree_node n, float min_bounds[3], float
	// max_bounds[3], void (* visitor)(Vertexv, void * ref), void * ref)
	// {
	// if(IS_LEAF(n))
	// {
	// struct RTree_leaf * l = (GET_LEAF(n));
	// if(overlap(minBounds,maxBounds,min_bounds,max_bounds))
	// {
	// int i;
	// for(i = 0; i < l->count; ++i)
	// if(inside(min_bounds,max_bounds,l->vertices[i].getLocation()))
	// {
	// visitor(l->vertices[i], ref);
	// }
	// }
	// }
	// else
	// {
	// if(overlap(n->min_bounds,n->max_bounds,min_bounds,max_bounds))
	// {
	// scan_rtree(n->left, min_bounds,max_bounds, visitor, ref);
	// scan_rtree(n->right, min_bounds,max_bounds, visitor, ref);
	// }
	// }
	// }
	// }

	// ==============================================================================
	// R-TREE DATASTRUCTURES
	// ==============================================================================

	// http://en.wikipedia.org/wiki/R-tree
	//
	// Our R-tree stores vertices by their 3-d AABBs; for internal nodes, we
	// store a
	// pair of child nodes; for leaf nodes we store up to 8 individual vertices.
	// Because the R-tree has diminishing returns as we get to a small scale, it
	// makes
	// sense to store more than one triangle per leaf - it cuts down nodes and
	// gets
	// us
	// better memory access patterns.
	//
	// We set the LSB of our pointers to 1 for leaf nodes as a way to indicate
	// the
	// type of the node in its parent.
}

class RTree_node {
	/**
	 * @uml.property name="min_bounds" multiplicity="(0 -1)" dimension="1"
	 */
	float min_bounds[];
	/**
	 * @uml.property name="max_bounds" multiplicity="(0 -1)" dimension="1"
	 */
	float max_bounds[];
	/**
	 * @uml.property name="left"
	 * @uml.associationEnd inverse="right:Renderer.RTree_node"
	 */
	RTree_node left;
	/**
	 * @uml.property name="right"
	 * @uml.associationEnd inverse="left:Renderer.RTree_node"
	 */
	RTree_node right;

	/**
	 * @uml.property name="leaf"
	 * @uml.associationEnd
	 */
	RTree_leaf leaf = null;

	public RTree_node() {
		min_bounds = new float[3];
		max_bounds = new float[3];
		left = null;
		right = null;
	}

	public void setMaxBounds(float[] maxBounds) {
		for (int i = 0; i < 3; i++)
			max_bounds[i] = maxBounds[i];

	}

	public void setMinBounds(float[] minBounds) {
		for (int i = 0; i < 3; i++)
			min_bounds[i] = minBounds[i];

	}

	public float[] getMaxBounds() {
		return max_bounds;
	}

	public float[] getMinBounds() {
		return min_bounds;
	}

	/**
	 * @param right
	 * @uml.property name="right"
	 */
	public void setRight(RTree_node right) {
		this.right = right;
	}

	/**
	 * @param left
	 * @uml.property name="left"
	 */
	public void setLeft(RTree_node left) {
		this.left = left;
	}
}

class RTree_leaf extends RTree_node {
	public static final int LEAF_DIM = 8;
	/**
	 * @uml.property name="count"
	 */
	int count; // Number of actual vertices, might be less than
	/**
	 * @uml.property name="vertices"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 */
	Vertex // leaf DIM.
	vertices[];

	public RTree_leaf() {
		super();

		vertices = new Vertex[LEAF_DIM];
	}

	public void setVertex(int i, Vertex vertex) {
		vertices[i] = vertex;
	}

	/**
	 * @param count
	 * @uml.property name="count"
	 */
	public void setCount(int count) {
		this.count = count;
	}

	public float[] getMinBounds() {
		return this.min_bounds;
	}

	public float[] getMaxBounds() {
		return this.max_bounds;
	}

	/**
	 * @return
	 * @uml.property name="count"
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @return
	 * @uml.property name="vertices"
	 */
	public Vertex[] getVertices() {
		return vertices;
	}

}
