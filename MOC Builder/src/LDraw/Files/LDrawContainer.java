package LDraw.Files;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import Command.LDrawColor;
import Command.LDrawComment;
import Command.LDrawLine;
import Command.LDrawPart;
import Command.LDrawQuadrilateral;
import Command.LDrawTriangle;
import Common.Box2;
import Common.Box3;
import Common.Matrix3;
import Common.Matrix4;
import LDraw.Support.ILDrawObservable;
import LDraw.Support.ILDrawObserver;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawMetaCommand;
import LDraw.Support.PartReport;
import LDraw.Support.type.CacheFlagsT;
import LDraw.Support.type.MessageT;

//==============================================================================
//
//File:		LDrawContainer.m
//
//Purpose:		Abstract subclass for LDrawDirectives which represent a 
//				collection of related directives.
//
//Created by Allen Smith on 3/31/05.
//Copyright (c) 2005. All rights reserved.
//==============================================================================

/**
 * @Class LDrawContainer
 * 
 * @Purpose Abstract subclass for LDrawDirectives which represent a collection
 *          of related directives.
 * @Represent LDrawContainer.(h, m) of Bricksmith
 * 
 * @author funface2
 * @since 2014-03-13
 * 
 */
public abstract class LDrawContainer extends LDrawDirective implements
		ILDrawObserver, Cloneable {

	/**
	 * @uml.property name="postsNotifications"
	 */
	protected boolean postsNotifications;
	Lock mutex;

	/**
	 * @uml.property name="containedObjects"
	 * @uml.associationEnd multiplicity="(0 -1)"
	 *                     inverse="enclosingDirective:LDraw.Support.LDrawDirective"
	 */
	private ArrayList<LDrawDirective> containedObjects;

	public LDrawContainer() {
		mutex = new ReentrantLock(true);
		init();
	}

	// ========== init
	// ==============================================================
	//
	// Purpose: Creates a new container with absolutely nothing in it, but
	// ready to receive objects.
	//
	// ==============================================================================
	public LDrawContainer init() {
		super.init();

		containedObjects = new ArrayList<LDrawDirective>();
		postsNotifications = false;
		return this;
	}// end init

	// ========== allEnclosedElements
	// ===============================================
	//
	// Purpose: Returns all of the terminal (leaf-node) subdirectives contained
	// within this object and all enclosed containers. Does not return
	// the enclosed containers themselves.
	//
	// ==============================================================================
	public ArrayList<LDrawDirective> allEnclosedElements() {
		mutex.lock();
		ArrayList<LDrawDirective> subelements = new ArrayList<LDrawDirective>();
		for (LDrawDirective currentDirective : containedObjects) {
			if (LDrawContainer.class.isInstance(currentDirective))
				subelements.addAll(((LDrawContainer) currentDirective)
						.allEnclosedElements());
			else
				subelements.add(currentDirective);
		}
		mutex.unlock();
		return subelements;

	}// end allEnclosedElements

	public long getFlattenWeight(int depth) {
		long weight = 0;
		mutex.lock();
		for (LDrawDirective currentDirective : containedObjects) {
			if (currentDirective instanceof LDrawPart){
				((LDrawPart) currentDirective).resolvePart();
				if(((LDrawPart) currentDirective).getCacheModel()!=null)
					weight += ((LDrawPart) currentDirective).getCacheModel()
							.getFlattenWeight(depth * 2);				
			}
			else if (currentDirective instanceof LDrawStep)
				weight += ((LDrawStep) currentDirective).getFlattenWeight(depth *2);			
			else if(currentDirective instanceof LDrawMetaCommand ==false)
				weight += depth;
		}
		mutex.unlock();

		return weight;
	}

	//

	// ========== boundingBox3
	// ======================================================
	//
	// Purpose: Returns the minimum and maximum points of the box which
	// perfectly contains this object.
	//
	// ==============================================================================
	// - (Box3) boundingBox3
	// {
	// return [LDrawUtilities boundingBox3ForDirectives:self->containedObjects];
	//
	// }//end boundingBox3

	// ========== postsNotifications
	// ================================================
	// ==============================================================================
	public boolean postsNotifications() {
		return postsNotifications;
	}

	// ========== projectedBoundingBoxWithModelView:projection:view:
	// ================
	//
	// Purpose: Returns the 2D projection (you should ignore the z) of the
	// object's bounds.
	//
	// ==============================================================================
	public Box3 projectedBoundingBoxWithModelView(Matrix4 modelView,
			Matrix4 projection, Box2 viewport) {
		Box3 bounds = Box3.getInvalidBox();
		// Box3 partBounds = Box3.getInvalidBox();
		// LDrawDirective currentDirective = null;
		// int numberOfDirectives = containedObjects.size();
		// int counter = 0;

		// for (counter = 0; counter < numberOfDirectives; counter++) {
		// currentDirective = containedObjects.get(counter);
		// }

		return bounds;

	}// end projectedBoundingBoxWithModelView:projection:view:

	// ========== indexOfDirective:
	// =================================================
	//
	// Purpose: Adds directive into the collection at position index.
	//
	// ==============================================================================
	public int indexOfDirective(LDrawDirective directive) {
		mutex.lock();
		int index = containedObjects.indexOf(directive);
		mutex.unlock();
		return index;

	}// end indexOfDirective:

	// ========== subdirectives
	// =====================================================
	//
	// Purpose: Returns the LDraw directives stored in this collection.
	//
	// ==============================================================================
	public ArrayList<LDrawDirective> subdirectives() {
		mutex.lock();
		ArrayList<LDrawDirective> subDirectives = new ArrayList<LDrawDirective>(
				containedObjects);
		mutex.unlock();
		return subDirectives;
	}// end subdirectives

	public int size() {
		return containedObjects.size();
	}

	// ========== setPostsNotifications:
	// ============================================
	//
	// Purpose: Sets whether the container posts
	// LDrawDirectiveDidChangeNotifications when its contents change.
	//
	// Notes: Posting notifications is extremely time-consuming and only
	// needed for editable containers. Given the huge number of
	// container changes which occur during parsing, you generally want
	// this flag off except in parseable directives.
	//
	// ==============================================================================
	/**
	 * @param flag
	 * @uml.property name="postsNotifications"
	 */
	public void setPostsNotifications(boolean flag) {
		postsNotifications = flag;

		// Apply new setting to children
		// for (LDrawDirective childDirective : containedObjects) {
		// }
	}// end setPostsNotifications:

	// ========== setVertexesNeedRebuilding
	// =========================================
	//
	// Purpose: Marks all the vertex optimizations of this container as needing
	// rebuilding.
	//
	// ==============================================================================
	public void setVertexesNeedRebuilding() {
		// pass to the superclass; subclasses can override to redirect this
		// message
		// to vertexes they manage.
		if (enclosingDirective() != null)
			enclosingDirective().setVertexesNeedRebuilding();
	}

	// ========== addDirective:
	// =====================================================
	//
	// Purpose: Adds directive into the collection at the end of the list.
	//
	// ==============================================================================
	public void addDirective(LDrawDirective directive) {
		int index = containedObjects.size();
		insertDirective(directive, index);
	}// end addDirective:

	// ========== collectPartReport:
	// ================================================
	//
	// Purpose: Collects a report on all the parts in this container, no
	// matter
	// how deeply they may be contained.
	//
	// ==============================================================================
	public void collectPartReport(PartReport report) {
		LDrawDirective currentDirective = null;
		int counter = 0;

		// for (counter = 0; counter < containedObjects.size(); counter++) {
		// currentDirective = containedObjects.get(counter);
		// todo
		// Related to GUI event
		// if(currentDirective
		// respondsToSelector:@selector(collectPartReport:)])
		// [currentDirective collectPartReport:report];
		// }

	}// end collectPartReport:

	// ========== removeDirective:
	// ==================================================
	//
	// Purpose: Removes the specified LDraw directive stored in this
	// collection.
	//
	// If it isn't in the collection, well, that's that.
	//
	// ==============================================================================
	public void removeDirective(LDrawDirective doomedDirective) {
		// First, find the object (making sure it's actually there in the
		// process)
		int indexOfObject = indexOfDirective(doomedDirective);

		if (indexOfObject != -1) {
			// We found it; kill it!
			removeDirectiveAtIndex(indexOfObject);
		}
	}// end removeDirective:

	// ========== insertDirective:atIndex:
	// ==========================================
	//
	// Purpose: Adds directive into the collection at position index.
	//
	// ==============================================================================
	public void insertDirective(LDrawDirective directive, int index) {
		// Insert
		mutex.lock();
		if (index >= containedObjects.size())
			containedObjects.add(directive);
		else
			containedObjects.add(index, directive);
		mutex.unlock();
		directive.setEnclosingDirective(this);

		// Apply notification policy to new children
		// todo
		// related with GUI
		// if([directive respondsToSelector:@selector(setPostsNotifications:)]
		// ==
		// YES)
		// {
		// if([(LDrawContainer*)directive postsNotifications] !=
		// self->postsNotifications)
		// {
		// [(LDrawContainer*)directive
		// setPostsNotifications:self->postsNotifications];
		// }
		// }

		// We have to do this FIRST - otherwise, our cache gets rebuilt by
		// the
		// notification handlers before the view hierarchy is fully wired
		// up and things go pretty sideways from there.
		directive.addObserver(this);

		if (postsNotifications == true) {
			noteNeedsDisplay();
		}

	}// end insertDirective:atIndex:

	// ========== removeDirectiveAtIndex:
	// ===========================================
	//
	// Purpose: Removes the LDraw directive stored at index in this
	// collection.
	//
	// ==============================================================================
	public void removeDirectiveAtIndex(int index) {
		LDrawDirective doomedDirective = containedObjects.get(index);

		if (doomedDirective.enclosingDirective() == this)
			doomedDirective.setEnclosingDirective(null); // no parent anymore;
		// it's an
		// orphan now.

		// Do this first...the actual remove may drop the directive's ref
		// count.
		// In that
		// case we'll puke.
		doomedDirective.removeObserver(this);

		mutex.lock();
		containedObjects.remove(index); // or disowned at least.
		mutex.unlock();

		if (postsNotifications == true) {
			noteNeedsDisplay();
		}

	}// end removeDirectiveAtIndex:

	// ========== setSubdirectiveSelected:
	// ==========================================
	//
	// Purpose: Called by a subdirective when it's been selected. This
	// allows
	// container directives to act on child selection. Override in
	// subclasses.
	//
	// ==============================================================================
	public void setSubdirectiveSelected(boolean subdirective) {
		// stub
	}

	// ========== containsReferenceTo:
	// ==============================================
	//
	// Purpose: Returns if this object (or any of its children)
	// references a
	// model with the given name.
	//
	// ==============================================================================
	public boolean containsReferenceTo(String name) {
		ArrayList<LDrawDirective> subdirectives = subdirectives();

		boolean containsReference = false;

		for (LDrawDirective currentDirective : subdirectives) {
			containsReference = currentDirective.containsReferenceTo(name);
			if (containsReference)
				break;
		}

		return containsReference;
	}

	// ========== acceptsDroppedDirective:
	// ==========================================
	//
	// Purpose: Returns YES if this container will accept a directive
	// dropped
	// on
	// it. Intended to be overridden by subclasses
	//
	// ==============================================================================
	public boolean acceptsDroppedDirective(LDrawDirective directive) {
		return true;
	}// end acceptsDroppedDirective:

	// ==========
	// flattenIntoLines:triangles:quadrilaterals:other:currentColor: =====
	//
	// Purpose: Appends the directive into the appropriate container.
	//
	// ==============================================================================
	public void flattenIntoLines(ArrayList<LDrawLine> lines,
			ArrayList<LDrawTriangle> triangles,
			ArrayList<LDrawQuadrilateral> quadrilaterals,
			ArrayList<LDrawDirective> everythingElse, LDrawColor parentColor,
			Matrix4 transform, Matrix3 normalTransform, boolean recursive) {
		ArrayList<LDrawDirective> subdirectives = subdirectives();

		for (LDrawDirective currentDirective : subdirectives) {
			currentDirective.flattenIntoLines(lines, triangles, quadrilaterals,
					everythingElse, parentColor, transform, normalTransform,
					recursive);
		}

	}// end flattenIntoLines:triangles:quadrilaterals:other:currentColor:

	// ==========
	// observableSaysGoodbyeCruelWorld====================================
	//
	// Purpose: this is the message receiving method for observers when
	// their
	// observable is yanked out from under them. Observation is a weak
	// reference so the thing you are observing may be nuked.
	//
	// In the case of the container, we only observe the directives
	// we contain and we are supposed to remove all directives before
	// they die (and containing them is a strong reference). So...
	// If any dying directives call out to us, it's a programming error.
	// Just log it out for now?
	//
	// ==============================================================================
	public void observableSaysGoodbyeCruelWorld(
			ILDrawObservable doomedObservable) {
		if (containedObjects.indexOf(doomedObservable) != -1) {
			System.out
					.println("Observer's observable is dying but we have no idea who it is...");
		}
	}

	// ========== statusInvalidated
	// =================================================
	//
	// Purpose: The things we watch call this when one of their states
	// that
	// we
	// might have cached is no longer valid. This tells us to not rely
	// on that data.
	//
	// ==============================================================================
	public void statusInvalidated(CacheFlagsT flags, ILDrawObservable observable) {
		invalCache(flags);
	}

	// ========== receiveMessage
	// ====================================================
	//
	// Purpose: The things we observe call this when something one-time
	// and
	// eventful happens - we can respond if desired.
	//
	// ==============================================================================
	public void receiveMessage(MessageT msg, ILDrawObservable observable) {
	}

	public Object clone() throws CloneNotSupportedException {
		LDrawContainer a = (LDrawContainer) super.clone();
		a.containedObjects = new ArrayList<LDrawDirective>();
		for (LDrawDirective item : containedObjects)
			if (item instanceof LDrawComment == false)
				a.containedObjects.add((LDrawDirective) item.clone());
		return a;
	}
	
	public void clear(){
		containedObjects.clear();
	}
}
