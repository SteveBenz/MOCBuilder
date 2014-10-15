package LDraw.Support;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.media.opengl.GL2;
import javax.swing.undo.UndoManager;

import Command.LDrawColor;
import Command.LDrawLine;
import Command.LDrawQuadrilateral;
import Command.LDrawTriangle;
import Common.Box2;
import Common.Box3;
import Common.Matrix3;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;
import Common.Vector3f;
import LDraw.Files.LDrawContainer;
import LDraw.Files.LDrawFile;
import LDraw.Files.LDrawMPDModel;
import LDraw.Files.LDrawModel;
import LDraw.Files.LDrawStep;
import LDraw.Support.type.CacheFlagsT;
import LDraw.Support.type.MessageT;
import Renderer.ILDrawCollector;
import Renderer.ILDrawRenderer;

//==============================================================================
//
//File:		LDrawDirective.h
//
//Purpose:		This is an abstract base class for all elements of an LDraw 
//				document.
//
//Created by Allen Smith on 2/19/05.
//Copyright 2005. All rights reserved.
//==============================================================================

/**
 * /////////////////////////////////////////////////////////////////////////////
 * /// // //OBSERVABLE/OBSERVER PROTOCOLS FOR DIRECTIVES //
 * /////////////////////////////////////////////////////////// The observer
 * protocol builds a one-way DAG out of our directives allowing directives to
 * note changes in their child directives and manage cached data appropriately.
 * The protocol rules:
 * 
 * 1. An observer/observable relationship is a pair of _weak_ references. No
 * retain counts are maintained, and it is always possible that either party
 * could end the relationship by dying. Observers who have good reason why their
 * observables should not go away (or vice versa) should maintain retain counts
 * separately as part of a separate parallel structure; this is only for message
 * flow.
 * 
 * 2. An observer begins observation by requesting that the observable at it to
 * an internal hypothetical observer list. Observables do not start the
 * relationship.
 * 
 * Similarly, an observer ends observation by requesting its observable to
 * remove me from the list.
 * 
 * 3. Death: if the observer dies first, it is responsible for terminating the
 * relationship in the usual way by calling removeObserver on its observable
 * with itself as the direct object.
 * 
 * But if the observable dies first (while being observed) it sends a
 * "goodbye cruel world" message to all observers currently watching it. Those
 * observers note that the observable is no longer, um, observable but they do
 * _not_ need to call back with a removeObservable message.
 * 
 * The method receiveMessage is used to send a set of specific messages to all
 * observing. This is for one-time, relatively rare, non-deallocation events
 * that happen.
 * 
 * Observables maintain a bit-field of flags about the status of cachable
 * information; an invalidate cache message is sent to all observers once each
 * time cachable info is changed until _any_ external caller reads that
 * property. (When a caller reads the property, the cache is rebuilt and a new
 * invalidate message will be generated.)
 * 
 * CACHING BEHAVIORS
 * 
 * The idea behind the caching flags is this: an observer that produces the sum
 * or union from many observables can benefit from knowing that none of the
 * observables has changed. (E.g. it's nice for a step to know that no bricks
 * have moved.) The correct caching behavior is this:
 * 
 * - Every time the observer reads an observable's property, the observable
 * clears the flag for that property, because the observer and observable are
 * now in sync. If the property requires expensive computation in the
 * observable, the observable probably updates its own internal cache.
 * 
 * - Every time the observable changes that property, it sends a notification
 * only IF the cache flag is clear; it then sets the cache flag.
 * 
 * - An observer who receives an invalidate message may in turn invalidate its
 * own cache (if necessary), causing a cascade up the observation tree.
 * 
 * Thus if the position of an object is changed 8 times between any external
 * code reading the object, an inval message is sent to observers only once. See
 * invalCache and revalCache for more details.
 */

/**
 * @Class LDrawDirective
 * 
 * @Purpose This is an abstract base class for all elements of an LDraw
 * @Represent LDrawDirective.(h, m) of Bricksmith
 * 
 * @author funface2
 * @since 2014-03-13
 * 
 */
public class LDrawDirective implements ILDrawObservable, ILDrawObserver,
		Comparable<LDrawDirective>, Cloneable {

	/**
	 * @uml.property name="enclosingDirective"
	 * @uml.associationEnd inverse="containedObjects:LDraw.Files.LDrawContainer"
	 */
	protected LDrawContainer enclosingDirective; // LDraw files are a hierarchy.

	/**
	 * @uml.property name="observers"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private LDrawFastSet<ILDrawObserver> observers;
	/**
	 * @uml.property name="invalFlags"
	 * @uml.associationEnd readOnly="true"
	 */
	private HashMap<CacheFlagsT, Boolean> invalFlags;
	/**
	 * @uml.property name="isSelected"
	 */
	boolean isSelected;
	/**
	 * @uml.property name="iconName"
	 */
	String iconName;

	// ========== init
	// ==============================================================
	//
	// Purpose: Start me up. This should be called before any other subclass
	// initialization code.
	//
	// ==============================================================================
	/**
	 * @Purpose Start me up. This should be called before any other subclass
	 *          initialization code.
	 */
	public LDrawDirective() {
		init();
	}

	// ========== init
	// ==============================================================
	//
	// Purpose: Return the base icon name for this type of LDraw directive
	//
	// ==============================================================================
	public static String defaultIconName() {
		return null;
	}

	// ========== init
	// ==============================================================
	//
	// Purpose: Start me up. This should be called before any other subclass
	// initialization code.
	//
	// ==============================================================================
	public LDrawDirective init() {
		enclosingDirective = null;
		iconName = "";

		observers = new LDrawFastSet<ILDrawObserver>();
		invalFlags = new HashMap<CacheFlagsT, Boolean>();
		invalFlags.put(CacheFlagsT.CacheFlagBounds, true);
		invalFlags.put(CacheFlagsT.DisplayList, true);
		invalFlags.put(CacheFlagsT.ContainerInvalid, true);
		return this;
	}

	// Initialization
	// ========== initWithLines:inRange:
	// ============================================
	//
	// Purpose: Convenience method to perform a blocking parse operation
	//
	// ==============================================================================
	public LDrawDirective initWithLines(ArrayList<String> lines, Range range) {
		LDrawDirective directive = null;
		DispatchGroup group = null;

		group = new DispatchGroup();
		try {
			directive = (LDrawDirective) initWithLines(lines, range, group);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		group._wait();
		group._release();

		return directive;
	}

	// ========== initWithLines:inRange:parentGroup:
	// ================================
	//
	// Purpose: Returns the LDraw directive based on lineFromFile, a single line
	// of LDraw code from a file.
	//
	// This method is intended to be overridden by subclasses.
	// LDrawDirective's implementation simply returns a useless empty
	// directive.
	//
	// A subclass implementation would look something like:
	// ---------------------------------------------------------------
	//
	// Class LineTypeClass = [LDrawUtilities
	// classForDirectiveBeginningWithLine:lineFromFile];
	// // Then initialize whatever subclass we came up with for this line.
	//
	// ==============================================================================

	public LDrawDirective initWithLines(ArrayList<String> lines, Range range,
			DispatchGroup parentGroup) throws Exception {
		if (lines.size() == 0) {
			return null;
		}
		return this;
	}

	// ---------- rangeOfDirectiveBeginningAtIndex:inLines:maxIndex:
	// ------[static]--
	//
	// Purpose: Returns the range from the first to the last LDraw line of the
	// directive which starts at index.
	//
	// This is a core method of the LDraw parser. It allows supporting
	// multiline directives and parallelization in parsing.
	//
	// Parameters: index - Index of first line to be considered for the
	// directive
	// lines - (Potentially) All the lines of the enclosing file. The
	// directive is represented by a subset of the lines in
	// the range between index and maxIndex.
	// maxIndex- Index of the last line which could possibly be part of
	// the directive.
	//
	// Notes: Subclasses of LDrawDirective override this method. You should
	// ALWAYS call this method on a subclass. Find the subclass using
	// +[LDrawUtilities classForDirectiveBeginningWithLine:].
	//
	// ------------------------------------------------------------------------------
	public static Range rangeOfDirectiveBeginningAtIndex(int index,
			ArrayList<String> lines, int maxIndex) {
		// Most LDraw directives are only one line. For those that aren't the
		// subclass should override this method and perform its own parsing.

		return new Range(index, 1);
	}

	// ========== collectColor:viewScale:parentColor:
	// =======================================
	//
	// Purpose: Issues the OpenGL code necessary to draw this element.
	//
	// This method is intended to be overridden by subclasses.
	// LDrawDirective's implementation does nothing.
	//
	// ==============================================================================
	// Directives
	public void collectColor() {
		// subclasses should override this with OpenGL code to draw the line.
	}

	// ========== collectSelf:
	// ========================================================
	//
	// Purpose: Collect self is called on each directive by its parents to
	// accumulate _mesh_ data into a display list for later drawing.
	// The collector protocol passed in is some object capable of
	// remembering the collectable data.
	//
	// Notes: As a general rule, directives that participate in display lists
	// need to re-validate their display list cache bit when this is
	// called so that the next data edit can move the DL state to
	// invalid. (Once data is invalid, further invalidations are
	// ignored.)
	//
	// This requirement falls on both real mesh-participants like
	// LDrawTriangle but also their direct containers like LDrawSteps
	// and LDrawTextures.
	//
	// ================================================================================

	public void collectSelf(ILDrawCollector renderer) {
		// Default implementation collects...nothing.

	}

	// ========== boundingBox3
	// ======================================================
	//
	// Purpose: return the bounds (in model space) of the directive.
	//
	// Notes: This routine is cached - the observers have a flag for whether
	// bounding box is invalidated. Thus implementations that have a
	// sane bounding box should call revalCache before returning a
	// value.
	//
	// Directives that don't have spatial meaning (e.g. hidden
	// directives and comments) can return InvalidBox.
	//
	// ==============================================================================
	public Box3 boundingBox3() {
		return Box3.getInvalidBox();
	}

	// ========== debugDrawboundingBox
	// ==============================================
	//
	// Purpose: Draw a translucent visualization of our bounding box to test
	// bounding box caching.
	//
	// Notes: The base class draws the geometry; derived classes can add
	// iteration to sub-directives and transforms.
	//
	// The calling code gets us into our GL state ahead of time.
	//
	// ==============================================================================
	public void debugDrawboundingBox(GL2 gl2) {
		Box3 my_bounds = boundingBox3();
		Vector3f max = my_bounds.getMax();
		Vector3f min = my_bounds.getMin();

		if (min.getX() <= max.getX() && min.getY() <= max.getY()
				&& min.getZ() <= max.getZ()) {
			float verts[] = { min.getX(), min.getY(), min.getZ(), min.getX(),
					min.getY(), max.getZ(), min.getX(), max.getY(), max.getZ(),
					min.getX(), max.getY(), min.getZ(),

					max.getX(), min.getY(), min.getZ(), max.getX(), min.getY(),
					max.getZ(), max.getX(), max.getY(), max.getZ(), max.getX(),
					max.getY(), min.getZ(),

					min.getX(), min.getY(), min.getZ(), min.getX(), max.getY(),
					min.getZ(), max.getX(), max.getY(), min.getZ(), max.getX(),
					min.getY(), min.getZ(),

					min.getX(), min.getY(), max.getZ(), min.getX(), max.getY(),
					max.getZ(), max.getX(), max.getY(), max.getZ(), max.getX(),
					min.getY(), max.getZ(),

					min.getX(), min.getY(), min.getZ(), min.getX(), min.getY(),
					max.getZ(), max.getX(), min.getY(), max.getZ(), max.getX(),
					min.getY(), min.getZ(),

					min.getX(), max.getY(), min.getZ(), min.getX(), max.getY(),
					max.getZ(), max.getX(), max.getY(), max.getZ(), max.getX(),
					max.getY(), min.getZ() };

			gl2.glVertexPointer(3, GL2.GL_FLOAT, 0, FloatBuffer.wrap(verts));
			gl2.glDrawArrays(GL2.GL_QUADS, 0, 24);
		}
	}

	public void compareRange(float[] range, Vector3f point) {
		if (range[0] > point.x)
			range[0] = point.x; // minx
		if (range[1] < point.x)
			range[1] = point.x; // maxx
		if (range[2] > point.z)
			range[2] = point.z; // minz
		if (range[3] < point.z)
			range[3] = point.z; // maxz
		if (range[4] > point.y)
			range[4] = point.y; // bottom
		if (range[5] < point.y)
			range[5] = point.y; // top
	}

	public void getRange(Matrix4 transform, float[] range) {
	}

	// Hit testing primitives

	// ========== hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Tests the directive and any of its children for intersections
	// between the pickRay and the directive's drawn content.
	//
	// Parameters: pickRay - in world coordinates
	// transform - transformation to apply to directive points to get
	// to world coordinates
	// scaleFactor - the window zoom level (1.0 == 100%)
	// boundsOnly - test the bounding box, rather than the
	// fully-detailed geometry
	// creditObject - object which should get credit if the
	// current object has been hit. (Used to credit nested
	// geometry to its parent.) If nil, the hit object credits
	// itself.
	// hits - keys are hit objects. Values are NSNumbers of hit depths.
	//
	// ==============================================================================

	public void hitTest(Ray3 pckRay, Matrix4 transform, float scaleFactor,
			boolean boundsOnly, LDrawDirective creditObject,
			HashMap<LDrawDirective, Float> hits) {
		// subclasses should override this with hit-detection code
	}

	public void hitTest(Ray3 pckRay, Matrix4 transform,
			LDrawDirective creditObject, HashMap<LDrawDirective, Float> hits) {
		// subclasses should override this with hit-detection code
	}

	// ========== boxTest:transform:boundsOnly:creditObject:hits:
	// ===================
	//
	// Purpose: Tests the directive and any of its children for intersections
	// between the directive's drawn form and the bounding box in the
	// XY plane, after perspective divide.
	//
	// Parameters: bounds - the box to test against, in post-projection (clip)
	// coordinates
	// transform - transformation to apply to directive points to get
	// to clip coordinates - perspective divide is required!
	// creditObject - object which should get credit if the
	// current object has been hit. (Used to credit nested
	// geometry to its parent.) If nil, the hit object credits
	// itself.
	// hits - a set of hit directives that we have accumulated so far
	// this routine adds more as found.
	//
	// Return: This function returns true if the _credit object_ was added to
	// the set. This allows hierarchies below the credit object to
	// early-exit.
	//
	// Notes: This test is used to do marquee selection - the marquee is
	// converted back from viewport to clip coordinates, and then
	// the primitive is forward-transformed to clip coordinates, for a
	// simple 2-d screen-space test.
	//
	// My original attempt to implement this used world-space clip
	// planes but it is surprisingly expensive to intersect two 3-d
	// polygons in arbitrary space. By working in screen space we
	// ensure that the selection box is an axis-aligned bounding box,
	// which greatly simplifies the algorithm.
	//
	// (To catch the case where the marquee is fully inside the interior
	// of the primitive, in screen space, but using 3-d primitives, we
	// have to calculate the union of two convex polyhedra. That's not
	// that hard but it requires memory allocations...2-d is much
	// simpler.)
	//
	// ==============================================================================

	public boolean boxTest(Box2 bounds, Matrix4 transform, boolean boundsOnly,
			LDrawDirective creditObject, TreeSet<LDrawDirective> hits) {
		// subclasses should override this with hit-detection code
		return false;
	}

	// ==========
	// depthTest:inBox:transform:creditObject:bestObject:bestDepth:=======
	//
	// Purpose: depthTest finds the closest primitive (in screen space)
	// overlapping a given point, as well as its device coordinate
	// depth.
	//
	// Parameters: pt - the 2-d location (in screen space) to intersect.
	// inBox - a bounding box in XY (in screen space) surrounding the
	// test point. The size of the box (e.g. how much bigger
	// it is than the point) defines the "slop" for testing
	// infinitely thin primitives like lines and drag handles.
	// transform - a model view and projection matrix to transform from
	// the directive's model coordinates to screen space.
	// creditObject - if not nil, we credit this object with the hit;
	// otherwise we use self.
	// bestObject - a ptr to an object that is rewritten with the new
	// best object if one is found.
	// depth - a ptr to a depth (in normalized device coordinates: -1
	// is max near, 1 is max far) of that best object. If a
	// hit is recorded, depth is updated.
	//
	// Notes: Depth testing uses "replace if closer" semantics to provide
	// return results; thus bestDepth should be initialized to point
	// to 1.0f (the far clip plane) before being called. The bounding
	// box needs to be enough bigger than the hit point to provide a
	// few pixels of slop. The depth should be measured at the hit
	// point.
	//
	// ==============================================================================

	public void depthTest(Vector2f testPt, Box2 bounds, Matrix4 transform,
			LDrawDirective creditObject, ArrayList<LDrawDirective> bestObject,
			FloatBuffer bestDepth) {
		// subclasses should override this.
	}

	// ========== write
	// =============================================================
	//
	// Purpose: Returns the LDraw code for this directive, which can then be
	// written out to a LDraw file and read by any LDraw interpreter.
	//
	// This method is intended to be overridden by subclasses.
	// LDrawDirective's implementation does nothing.
	//
	// ==============================================================================

	public String write() {
		// Returns a representation of the line which can be written out to a
		// file.
		return ""; // empty string; subclasses should override this method.

	}

	// Display
	// ========== browsingDescription
	// ===============================================
	//
	// Purpose: Returns a representation of the directive as a short string
	// which can be presented to the user.
	//
	// ==============================================================================

	public String browsingDescription() {
		return this.getClass().getName();
	}

	// ========== iconName
	// ==========================================================
	//
	// Purpose: Returns the name of image file used to display this kind of
	// object.
	//
	// ==============================================================================

	public String iconName() {
		if (this.iconName != null)
			return iconName;
		else
			return "";
	}

	// ========== inspectorClassName
	// ================================================
	//
	// Purpose: Returns the name of the class used to inspect this one.
	//
	// ==============================================================================

	public String inspectorClassName() {
		return "";
	}

	// Accessors
	// ========== ancestors
	// =========================================================
	//
	// Purpose: Returns the ancestors enclosing this directive (as well as the
	// directive itself), with the oldest ancestor (highest node) at
	// the first index.
	//
	// ==============================================================================

	public ArrayList<LDrawDirective> ancestors() {
		ArrayList<LDrawDirective> ancestors = new ArrayList<LDrawDirective>(3);
		LDrawDirective currentAncestor = this;

		while (currentAncestor != null) {
			ancestors.add(0, currentAncestor);
			currentAncestor = currentAncestor.enclosingDirective();
		}

		return ancestors;
	}

	// ========== enclosingDirective
	// ================================================
	//
	// Purpose: Bricksmith imposes a rigid hierarchy on the data in a file:
	//
	// LDrawFile
	// |
	// |-----> LDrawMPDModels
	// |
	// |-----> LDrawSteps
	// |
	// |-----> LDrawParts
	// |
	// |-----> LDraw Primitives
	// |
	// |-----> LDrawMetaCommands
	//
	// With the exception of LDrawFile at the root, all directives
	// must be enclosed within another directive. This method returns
	// the directive in which this one is stored.
	//
	// Notes: LDrawFiles return nil.
	//
	// ==============================================================================

	public LDrawContainer enclosingDirective() {
		return enclosingDirective;
	}

	// ========== enclosingFile
	// =====================================================
	//
	// Purpose: Returns the highest LDrawFile which contains this directive, or
	// nil if the directive is not in the hierarchy of an LDrawFile.
	//
	// ==============================================================================

	public LDrawFile enclosingFile() {
		LDrawDirective currentAncestor = this;
		boolean foundIt = false;

		while (currentAncestor != null) {
			if (LDrawFile.class.isInstance(currentAncestor)) {
				foundIt = true;
				break;
			}
			currentAncestor = currentAncestor.enclosingDirective();
		}

		if (foundIt == true)
			return (LDrawFile) currentAncestor;
		else
			return null;

	}

	// ========== enclosingModel
	// ====================================================
	//
	// Purpose: Returns the highest LDrawModel which contains this directive, or
	// nil if the directive is not in the hierarchy of an LDrawModel.
	//
	// ==============================================================================

	public LDrawModel enclosingModel() {
		LDrawDirective currentAncestor = this;
		boolean foundIt = false;

		while (currentAncestor != null) {
			if (LDrawModel.class.isInstance(currentAncestor)) {
				foundIt = true;
				break;
			}
			currentAncestor = currentAncestor.enclosingDirective();
		}

		if (foundIt == true)
			return (LDrawModel) currentAncestor;
		else
			return null;

	}

	// ========== enclosingStep
	// =====================================================
	//
	// Purpose: Returns the highest LDrawStep which contains this directive, or
	// nil if the directive is not in the hierarchy of an LDrawStep.
	//
	// ==============================================================================

	public LDrawStep enclosingStep() {
		LDrawDirective currentAncestor = this;
		boolean foundIt = false;

		while (currentAncestor != null) {
			if (LDrawStep.class.isInstance(currentAncestor)) {
				foundIt = true;
				break;
			}
			currentAncestor = currentAncestor.enclosingDirective();
		}

		if (foundIt == true)
			return (LDrawStep) currentAncestor;
		else
			return null;

	}

	// ========== isSelected
	// ========================================================
	//
	// Purpose: Returns whether this directive thinks it's selected.
	//
	// ==============================================================================

	/**
	 * @return
	 * @uml.property name="isSelected"
	 */
	public boolean isSelected() {
		return isSelected;
	}

	// ========== setEnclosingDirective:
	// ============================================
	//
	// Purpose: Just about all directives can be nested inside another one, so
	// this is where this method landed.
	//
	// ==============================================================================

	/**
	 * @param newParent
	 * @uml.property name="enclosingDirective"
	 */
	public void setEnclosingDirective(LDrawContainer newParent) {

		enclosingDirective = newParent;
	}

	// ========== setSelected:
	// ======================================================
	//
	// Purpose: Somebody make this a protocol method.
	//
	// ==============================================================================

	public void setSelected(boolean flag) {
		isSelected = flag;
	}

	// ========== setIconName:
	// ======================================================
	//
	// Purpose: Set the icon name
	//
	// ==============================================================================
	/**
	 * @param icon
	 * @uml.property name="iconName"
	 */
	public void setIconName(String icon) {
		iconName = icon;
	}

	// protocol Inspectable
	// ========== lockForEditing
	// ====================================================
	//
	// Purpose: Provide thread-safety for this object during inspection.
	//
	// ==============================================================================

	public void lockForEditing() {
		enclosingFile().lockForEditing();
	}

	// ========== unlockEditor
	// ======================================================
	//
	// Purpose: Provide thread-safety for this object during inspection.
	//
	// ==============================================================================

	public void unlockEditor() {
		enclosingFile().unlockEditor();
	};

	// Utilities
	// ========== containsReferenceTo:
	// ==============================================
	//
	// Purpose: Overridden by subclasses to indicate if the object (or any of
	// its potential children) references a model with the given name.
	//
	// ==============================================================================

	public boolean containsReferenceTo(String name) {
		return false;
	}

	// ========== description
	// =======================================================
	//
	// Purpose: Overrides NSObject method to get a more meaningful description
	// suitable for printing to the console.
	//
	// ==============================================================================

	public String description() {
		return new String(this.getClass().getName() + "\r\n" + write());
	}

	// ========== flattenIntoLines:triangles:quadrilaterals:other:currentColor:
	// =====
	//
	// Purpose: Appends the directive (or a copy of the directive) into the
	// appropriate container.
	//
	// Notes: This is used to flatten a complicated hiearchy of primitives and
	// part references to files containing yet more primitives into a
	// single flat list, which may be drawn to produce a shape visually
	// identical to the original structure. The flattened structure,
	// however, has the advantage that it is much faster to traverse
	// during drawing.
	//
	// This is the core of -[LDrawModel optimizeStructure].
	//
	// ==============================================================================

	public void flattenIntoLines(ArrayList<LDrawLine> lines,
			ArrayList<LDrawTriangle> triangles,
			ArrayList<LDrawQuadrilateral> quadriaterals,
			ArrayList<LDrawDirective> everythingElse, LDrawColor parentColor,
			Matrix4 transform, Matrix3 normalTransform, boolean recursive) {
		// By default, a directive does not add itself to the list, an
		// indication
		// that it is not drawn. Subclasses override this routine to add
		// themselves
		// to the appropriate list.
	}

	// ========== isAncestorInList:
	// =================================================
	//
	// Purpose: Given a list of LDrawContainers, returns YES if any of the
	// containers is a direct ancestor of the receiver. An ancestor is
	// specified by enclosingDirective; each enclosingDirective can
	// also have an ancestor. This method searchs the whole chain.
	//
	// Note: I think this method is potentially buggy. Shouldn't we be doing
	// pointer equality tests?
	//
	// ==============================================================================

	public boolean isAncestorInList(ArrayList<LDrawContainer> containers) {
		LDrawDirective ancestor = this;
		boolean foundInList = false;

		do {
			ancestor = ancestor.enclosingDirective;
			foundInList = containers.contains(ancestor);

		} while (ancestor != null && foundInList == false);

		return foundInList;
	}

	// ========== noteNeedsDisplay
	// ==================================================
	//
	// Purpose: An object can certainly be displayed in multiple views, and we
	// don't really care to find out which ones here. So we just post
	// a notification, and anyone can pick that up.
	//
	// ==============================================================================

	public void noteNeedsDisplay() {
		// [[NSNotificationCenter defaultCenter]
		// postNotificationName:LDrawDirectiveDidChangeNotification
		// object:self];
	}

	// ========== registerUndoActions:
	// ==============================================
	//
	// Purpose: Registers the undo actions that are unique to this subclass,
	// not to any superclass.
	//
	// ==============================================================================

	public void registerUndoActions(UndoManager undoManager) {
		// LDrawDirectives are fairly abstract, so all undoable attributes come
		// from subclasses.

	}

	// ========== addObserver:
	// ========================================================
	//
	// Purpose: Adds a directive as an observer of _this_ directive. Implements
	// the observable protocol.
	//
	// ================================================================================

	public void addObserver(ILDrawObserver observer) {
		observers.add(observer);
	}

	// ========== removeObserver:
	// ========================================================
	//
	// Purpose: Removes an observer that was watching us for notifications.
	// Implements the observable protocol.
	//
	// ================================================================================

	public void removeObserver(ILDrawObserver observer) {
		observers.remove(observer);
	}

	// ========== drawSelf:
	// ===========================================================
	//
	// Purpose: Draw this directive and its subdirectives by calling APIs on
	// the passed in renderer, then calling drawSelf on children.
	//
	// Notes: The drawSelf API draws existing DLs and changes GL state; some
	// directives will, as part of drawing, (re)build their DLs on the
	// fly. Thus VBO build-up is a by-product of drawing a frame where
	// the DL is needed. So we don't actually build VBOs for all models
	// on document-open - only the ones we can see!
	//
	// ================================================================================

	public void drawSelf(GL2 gl2, ILDrawRenderer renderer) {
		// Default implementation does ... nothing.

	}

	// These methods should really be "protected" methods for sub-classes to use
	// when acting like observables.
	// Obj-C doesn't give us compiler-level support to stop externals from
	// calling them.
	// ============ sendMessageToObservers
	// ==========================================
	//
	// Purpose: This is a utility to send a message to every observer.
	// Subclasses use it to reach observers since the observer
	// set is private.
	//
	// ==============================================================================

	protected void sendMessageToObservers(MessageT msg) // Send a specific
														// message to all
														// observers.
	{
		for (ILDrawObserver observer : observers.getAllItems()) {
			observer.receiveMessage(msg, this);
		}
	}

	// ============ invalCache
	// ======================================================
	//
	// Purpose: This is a utility that marks the cache flags as invalid for a
	// given subset of flags. If the flags were not already dirty,
	// observers are notified.
	//
	// Usage: Observables should call invalCache with the flag for a bit of
	// data EVERY TIME that data changes. Most of the time this will
	// result in a no-op or a small quantity of messages. The
	// internals take care of tracking cached state.
	//
	// ==============================================================================

	protected void invalCache(CacheFlagsT flags) // Invalidate cache bits - this
													// notifies observers as
													// needed. Flags are the
													// bits to invalidate, not
													// the net effect.
	{
		boolean flagState = false;
		if (invalFlags.containsKey(flags) == true)
			flagState = invalFlags.get(flags);

		invalFlags.put(flags, true);

		if (flagState == false) {
			for (ILDrawObserver observer : observers.getAllItems()) {
				observer.statusInvalidated(flags, this);

			}
		}
	}

	// ============== revalCache
	// ====================================================
	//
	// Purpose: This is a utility that clears out cache flags. Clients call
	// this when they rebuild their own cached data as it is queried
	// by clients.
	//
	// Return: The function returns the flags that were previously dirty from
	// among the set specified.
	//
	// Usage 1: For an observable that does not need to cache its internals:
	// The observable should call this with the flag for the data when
	// the accessor is called. This "re-arms" inval notifications for
	// observers.
	//
	// Usage 2: For an observable that uses a cache with lazy rebuilding for a
	// property:
	// The observer should call revalCache with the flag for the
	// property. Then IF the return is the flag passed in, it should
	// rebuild the cache. finally, it should return the cache.
	//
	// In case 2, the cache is being lazily rebuilt when needed and
	// notifications rearmed at the same time.
	//
	// ==============================================================================

	protected CacheFlagsT revalCache(CacheFlagsT flags) // Revalidate flags - no
														// notifications are
														// sent, but internals
														// are updated. Returns
														// which flags _were_
														// dirty.
	{
		CacheFlagsT were_dirty = invalFlags.get(flags) ? flags : null;
		invalFlags.put(flags, false);
		return were_dirty;
	}

	public LDrawMPDModel activeModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void observableSaysGoodbyeCruelWorld(
			ILDrawObservable doomedObservable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void statusInvalidated(CacheFlagsT flag, ILDrawObservable observable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(MessageT msg, ILDrawObservable observable) {
		// TODO Auto-generated method stub

	}

	public void insertDirective(LDrawDirective directive, int index) {
		// TODO Auto-generated method stub

	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int compareTo(LDrawDirective arg0) {
		return this.hashCode() - arg0.hashCode();
	}

}
