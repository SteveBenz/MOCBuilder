package LDraw.Support;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
//==============================================================================
//
//File:		LDrawVertexes.m
//
//Purpose:		Receives primitives and transfers their vertexes into an 
//				OpenGL-optimized object. Drawing instances of this object will 
//				draw all the contained vertexes. 
//
//Notes:		OpenGL has historically offered several ways of submitting 
//				vertexes, most of which proved highly suboptimal for graphics 
//				cards. Regretfully, those were also the easiest ones to program. 
//
//				Since immediate mode is deprecated and on its way out (and 
//				display lists with it), Bricksmith must resort to this 
//				intermediary object which collects, packs into a buffer, and 
//				draws all the vertexes for a model's geometry. 
//
//Modified:	11/16/2010 Allen Smith. Creation Date.
//
//==============================================================================
import java.util.TreeSet;

import Command.LDrawLine;
import Command.LDrawQuadrilateral;
import Command.LDrawTriangle;
import Common.Box2;
import Common.Box3;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;

public class LDrawVertices extends LDrawDirective implements Cloneable{
	/**
	 * @uml.property name="triangles"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="Command.LDrawTriangle"
	 */
	ArrayList<LDrawTriangle> triangles;
	/**
	 * @uml.property name="quadrilaterals"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="Command.LDrawQuadrilateral"
	 */
	ArrayList<LDrawQuadrilateral> quadrilaterals;
	/**
	 * @uml.property name="lines"
	 * @uml.associationEnd multiplicity="(0 -1)" elementType="Command.LDrawLine"
	 */
	ArrayList<LDrawLine> lines;

	/**
	 * @uml.property name="everythingElse"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     elementType="LDraw.Support.LDrawDirective"
	 */
	ArrayList<LDrawDirective> everythingElse;
	/**
	 * @uml.property name="acceptsNonPrimitives"
	 */
	boolean acceptsNonPrimitives;

	public LDrawVertices init() {
		super.init();

		lines = new ArrayList<LDrawLine>();
		triangles = new ArrayList<LDrawTriangle>();
		quadrilaterals = new ArrayList<LDrawQuadrilateral>();
		everythingElse = new ArrayList<LDrawDirective>();

		return this;
	}

	// #pragma mark -
	// #pragma mark DRAWING
	// #pragma mark -

	
	// ========== hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Hit-test the geometry.
	//
	// Notes: This being an optimized structure really intended only for
	// drawing, the idea of hit-testing the geometry is dubvious. This
	// is here because we use LDrawVertexes objects to draw bounding
	// boxes, and it's easier to leverage the existing hit test code in
	// the contained directives.
	//
	// ==============================================================================
	public void hitTest(Ray3 pickRay, Matrix4 transform, float scaleFactor,
			boolean boundsOnly, LDrawDirective creditObject,
			HashMap<LDrawDirective, Float> hits) {
		
		LDrawDirective currentDirective = null;
		int counter = 0;

		// Triangles		
		for (counter = 0; counter < triangles.size(); counter++) {
			currentDirective = triangles.get(counter);
			currentDirective.hitTest(pickRay, transform, scaleFactor,
					boundsOnly, creditObject, hits);
		}
		// Quadrilaterals		
		for (counter = 0; counter < quadrilaterals.size(); counter++) {
			currentDirective = quadrilaterals.get(counter);
			currentDirective.hitTest(pickRay, transform, scaleFactor,
					boundsOnly, creditObject, hits);
		}
		// Lines
		for (counter = 0; counter < lines.size(); counter++) {
			currentDirective = lines.get(counter);
			currentDirective.hitTest(pickRay, transform, scaleFactor,
					boundsOnly, creditObject, hits);
		}
		// All else
		for (counter = 0; counter < everythingElse.size(); counter++) {
			currentDirective = everythingElse.get(counter);
			currentDirective.hitTest(pickRay, transform, scaleFactor,
					boundsOnly, creditObject, hits);
		}
	}

	// ========== boxTest:transform:boundsOnly:creditObject:hits:
	// ===================
	//
	// Purpose: Check for intersections with screen-space geometry.
	//
	// ==============================================================================
	public boolean boxTest(Box2 bounds, Matrix4 transform, boolean boundsOnly,
			LDrawDirective creditObject, TreeSet<LDrawDirective> hits) {
		
		LDrawDirective currentDirective = null;
		int counter = 0;

		// Triangles		
		for (counter = 0; counter < triangles.size(); counter++) {
			currentDirective = triangles.get(counter);
			if (currentDirective.boxTest(bounds, transform, boundsOnly,
					creditObject, hits))
				if (creditObject != null)
					return true;
		}
		// Quadrilaterals		
		for (counter = 0; counter < quadrilaterals.size(); counter++) {
			currentDirective = quadrilaterals.get(counter);
			if (currentDirective.boxTest(bounds, transform, boundsOnly,
					creditObject, hits))
				if (creditObject != null)
					return true;
		}
		// Lines		
		for (counter = 0; counter < lines.size(); counter++) {
			currentDirective = lines.get(counter);
			if (currentDirective.boxTest(bounds, transform, boundsOnly,
					creditObject, hits))
				if (creditObject != null)
					return true;
		}
		// All else		
		for (counter = 0; counter < everythingElse.size(); counter++) {
			currentDirective = everythingElse.get(counter);
			if (currentDirective.boxTest(bounds, transform, boundsOnly,
					creditObject, hits))
				if (creditObject != null)
					return true;
		}
		return false;
	}// end boxTest:transform:boundsOnly:creditObject:hits:

	// ==========
	// depthTest:inBox:transform:creditObject:bestObject:bestDepth:=======
	//
	// Purpose: depthTest finds the closest primitive (in screen space)
	// overlapping a given point, as well as its device coordinate
	// depth.
	//
	// ==============================================================================
	public void depthTest(Vector2f testPt, Box2 bounds, Matrix4 transform,
			LDrawDirective creditObject, ArrayList<LDrawDirective> bestObject,
			FloatBuffer bestDepth) {
		
		LDrawDirective currentDirective = null;
		int counter = 0;

		// Triangles		
		for (counter = 0; counter < triangles.size(); counter++) {
			currentDirective = triangles.get(counter);
			currentDirective.depthTest(testPt, bounds, transform, creditObject,
					bestObject, bestDepth);
		}
		// Quadrilaterals		
		for (counter = 0; counter < quadrilaterals.size(); counter++) {
			currentDirective = quadrilaterals.get(counter);
			currentDirective.depthTest(testPt, bounds, transform, creditObject,
					bestObject, bestDepth);
		}
		// Lines		
		for (counter = 0; counter < lines.size(); counter++) {
			currentDirective = lines.get(counter);
			currentDirective.depthTest(testPt, bounds, transform, creditObject,
					bestObject, bestDepth);
		}
		// All else		
		for (counter = 0; counter < everythingElse.size(); counter++) {
			currentDirective = everythingElse.get(counter);
			currentDirective.depthTest(testPt, bounds, transform, creditObject,
					bestObject, bestDepth);
		}
	}// end depthTest:inBox:transform:creditObject:bestObject:bestDepth:

	// ========== setAcceptsNonPrimitives:
	// ==========================================
	//
	// Purpose: Whether the receiver maintains an everythingElse array to track
	// non-primitive objects.
	//
	// Notes: LDrawVertexes held by an LDrawModel to draw child primitives
	// should not track non-primitive objects, because the model itthis
	// will draw them.
	//
	// LDrawVertexes owned by the PartLibrary which are surrogate
	// drawables for library parts *do* need to track non-primitives in
	// order to make sure they are drawn.
	//
	// ==============================================================================
	public void setAcceptsNonPrimitives(boolean flag) {
		if (flag == false) {
			// everythingElse = null;
		} else {
			everythingElse = new ArrayList<LDrawDirective>();
		}
	}

	// ========== setLines:triangles:quadrilaterals:other:
	// ==========================
	//
	// Purpose: Sets the primitives this container will be responsible for
	// converting into a vertex array and drawing.
	//
	// ==============================================================================
	public void setLines(ArrayList<LDrawLine> linesIn,
			ArrayList<LDrawTriangle> trianglesIn,
			ArrayList<LDrawQuadrilateral> quadrilateralsIn,
			ArrayList<LDrawDirective> everythingElseIn) {
		lines.clear();
		triangles.clear();
		quadrilaterals.clear();
		everythingElse.clear();

		lines.addAll(linesIn);
		triangles.addAll(trianglesIn);
		quadrilaterals.addAll(quadrilateralsIn);
		if (everythingElseIn != null)
			everythingElse.addAll(everythingElseIn);

	}// end setLines:triangles:quadrilaterals:other:

	// ========== addDirective:
	// =====================================================
	//
	// Purpose: Register a directive of an arbitrary type (type will be deduced
	// correctly).
	//
	// ==============================================================================
	public void addDirective(LDrawDirective directive) {
		if (LDrawLine.class.isInstance(directive)) {
			addLine((LDrawLine) directive);
		} else if (LDrawTriangle.class.isInstance(directive)) {
			addTriangle((LDrawTriangle) directive);
		} else if (LDrawQuadrilateral.class.isInstance(directive)) {
			addQuadrilateral((LDrawQuadrilateral) directive);
		} else {
			addOther(directive);
		}

	}// end addDirective:

	// ========== addLine:
	// ==========================================================
	//
	// Purpose: Register a line to be included in the optimized vertexes. The
	// object must be re-optimized now.
	//
	// ==============================================================================
	public void addLine(LDrawLine line) {
		lines.add(line);
	}

	// ========== addTriangle:
	// ======================================================
	//
	// Purpose: Register a triangle to be included in the optimized vertexes.
	// The object must be re-optimized now.
	//
	// ==============================================================================
	public void addTriangle(LDrawTriangle triangle) {
		triangles.add(triangle);
	}

	// ========== addQuadrilateral:
	// =================================================
	//
	// Purpose: Register a quadrilateral to be included in the optimized
	// vertexes. The object must be re-optimized now.
	//
	// ==============================================================================
	public void addQuadrilateral(LDrawQuadrilateral quadrilateral) {
		quadrilaterals.add(quadrilateral);
	}

	// ========== addOther:
	// =========================================================
	//
	// Purpose: Register a other to be included in the optimized vertexes. The
	// object must be re-optimized now.
	//
	// ==============================================================================
	public void addOther(LDrawDirective other) {
		everythingElse.add(other);
	}

	// #pragma mark -

	// ========== removeDirective:
	// ==================================================
	//
	// Purpose: Register a directive of an arbitrary type (type will be deduced
	// correctly).
	//
	// ==============================================================================
	public void removeDirective(LDrawDirective directive) {

		if (LDrawLine.class.isInstance(directive)) {
			removeLine((LDrawLine) directive);
		} else if (LDrawTriangle.class.isInstance(directive)) {
			removeTriangle((LDrawTriangle) directive);
		} else if (LDrawQuadrilateral.class.isInstance(directive)) {
			removeQuadrilateral((LDrawQuadrilateral) directive);
		} else {
			removeOther(directive);
		}

	}// end removeDirective:

	// ========== removeLine:
	// =======================================================
	//
	// Purpose: De-registers a line to be included in the optimized vertexes.
	// The object must be re-optimized now.
	//
	// ==============================================================================
	public void removeLine(LDrawLine line) {
		lines.remove(line);
	}

	// ========== removeTriangle:
	// ===================================================
	//
	// Purpose: De-registers a line to be included in the optimized vertexes.
	// The object must be re-optimized now.
	//
	// ==============================================================================
	public void removeTriangle(LDrawTriangle triangle) {
		triangles.remove(triangle);
	}

	// ========== removeQuadrilateral:
	// ==============================================
	//
	// Purpose: De-registers a quadrilateral to be included in the optimized
	// vertexes. The object must be re-optimized now.
	//
	// ==============================================================================
	public void removeQuadrilateral(LDrawQuadrilateral quadrilateral) {
		quadrilaterals.remove(quadrilateral);
	}

	// ========== removeOther:
	// ======================================================
	//
	// Purpose: De-registers a other to be included in the optimized vertexes.
	// The object must be re-optimized now.
	//
	// ==============================================================================
	public void removeOther(LDrawDirective other) {
		everythingElse.remove(other);
	}

	public Box3 boundingBox3() {
		assert true : "How is this getting called?";
		return Box3.getInvalidBox();
	}
	
	public Object clone() throws CloneNotSupportedException{
		LDrawVertices newVertices = (LDrawVertices)super.clone();
		newVertices.triangles = (ArrayList<LDrawTriangle>)triangles.clone();
		newVertices.lines = (ArrayList<LDrawLine>)lines.clone();
		newVertices.quadrilaterals = (ArrayList<LDrawQuadrilateral>)quadrilaterals.clone();
		newVertices.everythingElse = (ArrayList<LDrawDirective>)everythingElse.clone();
						
		return newVertices;
	}
}
