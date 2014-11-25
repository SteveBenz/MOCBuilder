package LDraw.Files;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.media.opengl.GL2;
import javax.swing.undo.UndoManager;

import Command.LDrawColorT;
import Command.LDrawLSynthDirective;
import Command.LDrawLine;
import Command.LDrawPart;
import Command.LDrawQuadrilateral;
import Command.LDrawTriangle;
import Common.Box2;
import Common.Box3;
import Common.Matrix3;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;
import Common.Vector3f;
import LDraw.Support.ColorLibrary;
import LDraw.Support.DispatchGroup;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawKeywords;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.MatrixMath;
import LDraw.Support.Range;
import LDraw.Support.RangeException;
import LDraw.Support.type.CacheFlagsT;
import LDraw.Support.type.MessageT;
import LDraw.Support.type.ViewOrientationT;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Renderer.ILDrawCollector;
import Renderer.ILDrawDLHandle;
import Renderer.ILDrawRenderer;
import Renderer.LDrawDL;
import Renderer.LDrawDLCleanup_f;

//==============================================================================
//
//File:		LDrawModel
//
//Purpose:		Represents a collection of Lego bricks that form a single model.
//
//Created by Allen Smith on 2/19/05.
//Copyright (c) 2005. All rights reserved.
//==============================================================================
/**
 * @Class LDrawModel
 * 
 * @Purpose Represents a collection of Lego bricks that form a single model.
 * @Represent LDrarwModel.(h, m) of Bricksmith
 * 
 * @author funface2
 * @since 2014-03-13
 * 
 */
// ==============================================================================
//
// File: LDrawModel.h
//
// Purpose: Represents a collection of Lego bricks that form a single model.
//
// Bricksmith imposes an arbitrary requirement that a model be
// composed of a series of steps. Each model must have at least one
// step in it, and only LDrawSteps can be put into the model's
// subdirective array. Each LDraw model contains at least one step
// even if it contains no 0 STEP commands, since the final step in
// the model is not required to have step marker.
//
// Created by Allen Smith on 2/19/05.
// Copyright (c) 2005. All rights reserved.
// ==============================================================================

public class LDrawModel extends LDrawContainer implements Cloneable {
	/**
	 * @uml.property name="modelDescription"
	 */
	String modelDescription;
	/**
	 * @uml.property name="fileName"
	 */
	String fileName;
	/**
	 * @uml.property name="author"
	 */
	String author;
	/**
	 * @uml.property name="rotationCenter"
	 * @uml.associationEnd
	 */
	Vector3f rotationCenter;
	/**
	 * @uml.property name="colorLibrary"
	 * @uml.associationEnd
	 */
	ColorLibrary colorLibrary; // in-scope !COLOURS local to the model
	/**
	 * @uml.property name="stepDisplayActive"
	 */
	boolean stepDisplayActive; // YES if we are only display steps
								// 1-currentStepDisplayed
	/**
	 * @uml.property name="currentStepDisplayed"
	 */
	int currentStepDisplayed; // display up to and including this step index

	/**
	 * @uml.property name="cachedBounds"
	 * @uml.associationEnd
	 */
	Box3 cachedBounds; // bounds of the model - only covers steps that are
						// showing

	// steps are stored in the superclass.

	// Drag and Drop
	/**
	 * @uml.property name="draggingDirectives"
	 * @uml.associationEnd
	 */
	LDrawStep draggingDirectives;

	/**
	 * @uml.property name="isOptimized"
	 */
	boolean isOptimized; // Were we ever structure-optimized - used to optimize
							// out
							// some drawing on library parts.
	/**
	 * @uml.property name="dl"
	 * @uml.associationEnd
	 */
	private HashMap<GL2, ILDrawDLHandle> dl = new HashMap<GL2, ILDrawDLHandle>(); // Cached
	// DL
	// if
	// we
	// have
	// one.
	/**
	 * @uml.property name="dl_dtor"
	 * @uml.associationEnd
	 */
	LDrawDLCleanup_f dl_dtor;

	public LDrawModel() {
		init();
	}

	// ---------- model
	// ---------------------------------------------------[static]--
	//
	// Purpose: Creates a new model ready to be edited.
	//
	// ------------------------------------------------------------------------------
	public static LDrawModel model() {
		LDrawModel newModel = new LDrawModel();

		// Then fill it up with useful initial attributes
		newModel.setModelDescription("UntitledModel");
		newModel.setFileName("");

		newModel.setAuthor(LDrawUtilities.defaultAuthor());

		// Need to create a blank step.
		newModel.addStep();
		return newModel;

	}// end model

	// ========== init
	// ==============================================================
	//
	// Purpose: Creates a new, completely blank model file.
	//
	// ==============================================================================
	public LDrawModel init() {
		super.init();
		colorLibrary = new ColorLibrary();
		cachedBounds = Box3.getInvalidBox();
		setModelDescription("");
		setFileName("");
		setAuthor("");

		rotationCenter = Vector3f.getZeroVector3f();

		setStepDisplay(false);
		return this;
	}// end init

	// ========== initWithLines:inRange:parentGroup:
	// ================================
	//
	// Purpose: Creates a new model file based on the lines from a file.
	// These lines of strings should only describe one model, not
	// multiple ones.
	//
	// This method divides the model into steps. A step may be ended
	// by:
	// * a 0 STEP line
	// * a 0 ROTSTEP line
	// * the end of the file
	//
	// A STEP or ROTSTEP command is part of the step they end, so they
	// are the last line IN the step.
	//
	// The final step marker is optional. Thus a file that has no step
	// markers still has one step.
	//
	// ==============================================================================
	public LDrawModel initWithLines(ArrayList<String> lines, Range range,
			DispatchGroup parentGroup) {
		int contentStartIndex = 0;
		Range stepRange = range;
		int maxLineIndex = 0;
		int insertIndex = 0;
		ArrayList<LDrawDirective> substeps = null;

		// Start with a nice blank model.
		try {
			super.initWithLines(lines, range, parentGroup);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cachedBounds = Box3.getInvalidBox();

		substeps = new ArrayList<LDrawDirective>();

		// Try and get the header out of the file. If it's there, the lines
		// returned
		// will not contain it.
		contentStartIndex = parseHeaderFromLines(lines, range.getLocation());
		maxLineIndex = range.getMaxRange();
		
		DispatchGroup modelDispatchGroup = null;
		modelDispatchGroup = new DispatchGroup();
		if(parentGroup!=null)
			modelDispatchGroup.extendsFromParent(parentGroup);
		
		do {
			LDrawStep newStep = new LDrawStep();
			stepRange = newStep.rangeOfDirectiveBeginningAtIndex(
					contentStartIndex, lines, maxLineIndex);			
			
			try {
				newStep.initWithLines(lines, stepRange, modelDispatchGroup);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			substeps.add(newStep);
			++insertIndex;

			contentStartIndex = stepRange.getMaxRange() + 1;

		} while (contentStartIndex <= range.getMaxRange());

		// #if USE_BLOCKS
		// dispatch_group_notify(modelDispatchGroup,queue,
		// ^{
		// #endif
		int counter = 0;
		for (counter = 0; counter < insertIndex; counter++) {
			LDrawStep step = (LDrawStep) substeps.get(counter);
			addStep(step);
		}

		// Degenerate case: utterly empty file. Create one empty step, because
		// it is
		// illegal to have a 0-step model in Bricksmith.
		if (steps().size() == 0) {
			addStep();
		}
		// #if USE_BLOCKS
		// if(parentGroup != NULL)
		// dispatch_group_leave(parentGroup);
		// });
		// dispatch_release(modelDispatchGroup);
		// #endif
		return this;

	}// end initWithLines:inRange:

	// ========== draw:viewScale:parentColor:
	// =======================================
	//
	// Purpose: Simply draw all the steps; they will worry about drawing all
	// their constituents.
	//
	// ==============================================================================
	public void collectColor()

	{
		ArrayList<LDrawDirective> steps = subdirectives();
		int maxIndex = maxStepIndexToOutput(steps);
		LDrawStep currentDirective = null;
		int counter = 0;

		// Draw all the steps in the model
		for (counter = 0; counter <= maxIndex; counter++) {
			currentDirective = (LDrawStep) steps.get(counter);
			currentDirective.collectColor();
		}
	}// end draw:viewScale:parentColor:

	// ========== drawSelf:
	// ===========================================================
	//
	// Purpose: Draw this directive and its subdirectives by calling APIs on
	// the passed in renderer, then calling drawSelf on children.
	//
	// Notes: The LDrawModel serves as the display-list holder for all
	// primitives directly "underneath" it. Thus when we hit drawSelf
	// We revalidate our DL and then just draw it.
	//
	// "Our" DL is a DL containing only the mesh primitives DIRECTLY
	// underneath us. Triangles that are part of a model that is
	// referenced by a PART underneath us are not collected - that is,
	// collection is not recursive. We count on the library being
	// flattened to ensure one VBO per library part.
	//
	// ================================================================================
	public void drawSelf(GL2 gl2, ILDrawRenderer renderer) {
		// DL cache control: we may have to throw out our old DL if it has gone
		// stale. EITHER WAY we mark our DL bit as validated per the rules of
		// the observable protocol.
		if (dl.containsKey(gl2) == true) {			
			if (revalCache(CacheFlagsT.DisplayList) == CacheFlagsT.DisplayList) {
				if (dl.get(gl2) != null)
					((LDrawDL) (dl.get(gl2))).destroy(gl2);
				dl_dtor = null;
				dl.remove(gl2);
//				 System.out.println("detroy DL");
			}
		} else
			revalCache(CacheFlagsT.DisplayList);

		// Now: if we do not have a DL (no DL or we threw it out because it
		// was invalid) build one now: get a collector and call "collect" on
		// ourselves, which will walk our tree picking up primitives.
		if (dl.containsKey(gl2) == false) {
			ILDrawCollector collector = renderer.beginDL();
			collectSelf(collector);
			dl.put(gl2, renderer.endDL(gl2, dl_dtor));
			
//			System.out.println("create DL");
		}
		
		// Finally: if we have a DL (cached or brand new, draw it!!)
		if (dl.get(gl2) != null){			
			renderer.drawDL(gl2, dl.get(gl2));
		}

		if (!isOptimized) {
			// Slow stuff part 1, skipped on library parts for speed.

			// First: recurse the 'drawSelf message. This is needed for:
			// - Parts, which draw, not collect and
			// - Drag handles for selected primitives.
			// Library parts are guaranteed to be only steps of primitives,
			// so there is no need for this.

			ArrayList<LDrawDirective> steps = subdirectives();
			int maxIndex = maxStepIndexToOutput(steps);
			LDrawDirective currentDirective = null;
			int counter = 0;
			for (counter = 0; counter <= maxIndex; counter++) {
				currentDirective = steps.get(counter);
				currentDirective.drawSelf(gl2, renderer);
			}
		}
	}// drawSelf:

	// ========== collectSelf:
	// ========================================================
	//
	// Purpose: Collect self is called on each directive by its parents to
	// accumulate _mesh_ data into a display list for later drawing.
	// The collector protocol passed in is some object capable of
	// remembering the collectable data.
	//
	// Models simply recurse to their steps.
	//
	// Notes: We do NOT revalidate our display list, because we do not expect
	// to hit this case from a 'parent'. Rather, we expect a part to
	// call "draw" on us (a model) and then we bulid our OWN DL.
	//
	// See drawSelf: implementation above for cached DL handling!
	//
	// ================================================================================
	public void collectSelf(ILDrawCollector renderer) {
		ArrayList<LDrawDirective> steps = subdirectives();
		int maxIndex = maxStepIndexToOutput(steps);
		LDrawDirective currentDirective = null;
		int counter = 0;

		// Draw all the steps in the model
		for (counter = 0; counter <= maxIndex; counter++) {
			currentDirective = steps.get(counter);
			currentDirective.collectSelf(renderer);
		}
	}// end collectSelf:

	// ========== debugDrawboundingBox
	// ==============================================
	//
	// Purpose: Draw a translucent visualization of our bounding box to test
	// bounding box caching.
	//
	// ==============================================================================
	public void debugDrawboundingBox(GL2 gl2) {
		ArrayList<LDrawDirective> steps = subdirectives();
		int maxIndex = maxStepIndexToOutput(steps);
		LDrawDirective currentDirective = null;
		int counter = 0;

		// Draw all the steps in the model
		for (counter = 0; counter <= maxIndex; counter++) {
			currentDirective = steps.get(counter);
			currentDirective.debugDrawboundingBox(gl2);
		}

		super.debugDrawboundingBox(gl2);
	}// end debugDrawboundingBox

	public void getRange(Matrix4 transform, float[] range) {
		ArrayList<LDrawDirective> steps = subdirectives();
		int maxIndex = maxStepIndexToOutput(steps);
		LDrawStep currentDirective = null;
		int counter = 0;

		// Draw all the steps in the model
		for (counter = 0; counter <= maxIndex; counter++) {
			currentDirective = (LDrawStep) steps.get(counter);
			currentDirective.getRange(transform, range);
		}
	}

	// ========== hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Hit-test the geometry.
	//
	// ==============================================================================
	public void hitTest(Ray3 pickRay, Matrix4 transform,
			LDrawDirective creditObject, HashMap<LDrawDirective, Float> hits) {

		ArrayList<LDrawDirective> steps = subdirectives();
		int maxIndex = maxStepIndexToOutput(steps);
		LDrawStep currentDirective = null;
		int counter = 0;

		// Draw all the steps in the model
		for (counter = 0; counter <= maxIndex; counter++) {
			currentDirective = (LDrawStep) steps.get(counter);
			currentDirective.hitTest(pickRay, transform, creditObject, hits);
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
		if (!MatrixMath
				.VolumeCanIntersectBox(boundingBox3(), transform, bounds)) {
			return false;
		}

		ArrayList<LDrawDirective> steps = subdirectives();
		int maxIndex = maxStepIndexToOutput(steps);
		LDrawStep currentDirective = null;
		int counter = 0;

		// Draw all the steps in the model
		for (counter = 0; counter <= maxIndex; counter++) {
			currentDirective = (LDrawStep) steps.get(counter);
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

		if (!MatrixMath.VolumeCanIntersectPoint(boundingBox3(), transform,
				bounds, bestDepth.get(0))) {
			return;
		}

		ArrayList<LDrawDirective> steps = subdirectives();
		int maxIndex = maxStepIndexToOutput(steps);
		LDrawStep currentDirective = null;
		int counter = 0;

		// Draw all the steps in the model
		for (counter = 0; counter <= maxIndex; counter++) {
			currentDirective = (LDrawStep) steps.get(counter);
			currentDirective.depthTest(testPt, bounds, transform, creditObject,
					bestObject, bestDepth);
		}
	}// end depthTest:inBox:transform:creditObject:bestObject:bestDepth:

	// ========== write
	// =============================================================
	//
	// Purpose: Writes out the MPD submodel, wrapped in the MPD file
	// commands.
	//
	// ==============================================================================
	public String write() {
		String written = new String();
		String CRLF = "\r\n";// we need a DOS line-end marker,
		// because
		// LDraw is predominantly DOS-based.
		ArrayList<LDrawDirective> steps = subdirectives();
		int numberSteps = steps.size();
		LDrawStep currentStep = null;
		String stepOutput = null;
		int counter = 0;

		// Write out the file header in all of its irritating glory.
		written = written.concat(String.format("0 %s%s", modelDescription(),
				CRLF));
		written = written.concat(String.format("0 %s %s%s",
				LDrawKeywords.LDRAW_HEADER_NAME, fileName(), CRLF));
		written = written.concat(String.format("0 %s %s%s",
				LDrawKeywords.LDRAW_HEADER_AUTHOR, author(), CRLF));

		// Write out all the steps in the file.
		for (counter = 0; counter < numberSteps; counter++) {
			currentStep = (LDrawStep) steps.get(counter);

			// Omit the 0 STEP command for 1-step models, which probably aren't
			// being built with steps in mind anyway.
			if (numberSteps == 1)
				stepOutput = currentStep.writeWithStepCommand(false);
			else
				stepOutput = currentStep.write();

			written = written.concat(String.format("%s%s", stepOutput, CRLF));
		}

		// Now remove that last CRLF.
		return written.substring(0, written.length() - CRLF.length());
	}// end write
		//
		//
		// #pragma mark -
		// #pragma mark DISPLAY
		// #pragma mark -
		//
		// ========== browsingDescription
		// ===============================================

	//
	// Purpose: Returns a representation of the directive as a short
	// string
	// which can be presented to the user.
	//
	// ==============================================================================

	public String browsingDescription() {
		return modelDescription();

	}// end browsingDescription

	// ========== iconName
	// ==========================================================
	//
	// Purpose: Returns the name of image file used to display this kind
	// of
	// object, or nil if there is no icon.
	//
	// ==============================================================================
	public String iconName() {
		return "Document";

	}// end iconName

	// #pragma mark -
	// #pragma mark ACCESSORS
	// #pragma mark -
	//
	// ========== boundingBox3
	// ======================================================
	//
	// Purpose: Returns the minimum and maximum points of the box which
	// perfectly contains this object.
	//
	// We optimize this calculation on models whose dimensions are
	// known to be constant--parts from the library, for instance.
	//
	// ==============================================================================
	public Box3 boundingBox3() {
		Box3 totalBounds = Box3.getInvalidBox();
		Box3 draggingBounds = Box3.getInvalidBox();

		if (revalCache(CacheFlagsT.CacheFlagBounds) == CacheFlagsT.CacheFlagBounds) {
			cachedBounds = Box3.getInvalidBox();

			ArrayList<LDrawDirective> steps = subdirectives();
			int maxIndex = maxStepIndexToOutput(steps);
			LDrawDirective currentDirective = null;
			int counter = 0;

			// Draw all the steps in the model
			for (counter = 0; counter <= maxIndex; counter++) {
				currentDirective = steps.get(counter);
				cachedBounds = MatrixMath.V3UnionBox(cachedBounds,
						currentDirective.boundingBox3());
			}
		}
		totalBounds = cachedBounds;

		// If drag-and-drop objects are present, add them into the bounds.
		if (draggingDirectives != null) {
			draggingBounds = LDrawUtilities
					.boundingBox3ForDirectives(draggingDirectives
							.subdirectives());
			totalBounds = MatrixMath.V3UnionBox(draggingBounds, totalBounds);
		}

		return totalBounds;

	}// end boundingBox3

	// ========== category
	// ==========================================================
	//
	// Purpose: Returns the category to which this model belongs. This is
	// determined from the description field, which is the first line
	// of the file for non-MPD documents. For instance:
	//
	// 0 Brick 2 x 4
	//
	// This part would be in the category "Brick", and has the
	// description "Brick  2 x  4".
	//
	// ==============================================================================
	public String category() {
		String category = null;
		int firstSpace; // range of the category string in the first line.

		// The category name is the first word in the description.
		firstSpace = modelDescription.indexOf(" ");
		if (firstSpace != -1)
			category = modelDescription.substring(0, firstSpace);
		else
			category = modelDescription;

		// Clean category name of any weird notational marks
		if (category.charAt(0) == '_' || category.charAt(0) == '~')
			category = category.substring(1);

		return category;

	}// end category

	// ========== colorLibrary
	// ======================================================
	//
	// Purpose: Returns the color library object which accumulates the
	// !COLOURS
	// defined locally within the model.
	//
	// Notes: According to the LDraw color spec, local colors having
	// scoping:
	// they become active at the point of definition and fall out of
	// scope at the end of the model. As a convenience in Bricksmith,
	// the color library will still contain all the local model colors
	// after a draw is complete--the library will not be purged just
	// for scoping's sake. It may be purged at the beginning of
	// drawing, however.
	//
	// ==============================================================================
	public ColorLibrary colorLibrary() {
		return colorLibrary;

	}// end colorLibrary

	// ========== draggingDirectives
	// ================================================
	//
	// Purpose: Returns the objects that are currently being displayed as
	// part
	// of drag-and-drop.
	//
	// ==============================================================================
	public ArrayList<LDrawDirective> draggingDirectives() {
		if (draggingDirectives == null)
			return null;

		return draggingDirectives.subdirectives();

	}// end draggingDirectives

	// ========== enclosingFile
	// =====================================================
	//
	// Purpose: Returns the file in which this model is stored.
	//
	// ==============================================================================
	public LDrawFile enclosingFile() {
		return (LDrawFile) enclosingDirective;

	}// end enclosingFile

	// ========== modelDescription
	// ==================================================
	//
	// Purpose: Returns the model description, which is the first line of
	// the
	// model. (i.e., Brick 2 x 4)
	//
	// ==============================================================================
	public String modelDescription() {
		return modelDescription;

	}// end modelDescription

	// ========== fileName
	// ==========================================================
	//
	// Purpose: Returns the name the model is ostensibly saved under in
	// the
	// file system.
	//
	// ==============================================================================
	public String fileName() {
		return fileName;

	}// end fileName

	// ========== author
	// ============================================================
	//
	// Purpose: Returns the person who created the document.
	//
	// ==============================================================================
	public String author() {
		return author;

	}// end author

	// ========== maximumStepIndexDisplayed
	// =========================================
	//
	// Purpose: Returns the index of the last step which will be drawn.
	// The
	// value only has meaning if the model is in step-display mode.
	//
	// ==============================================================================
	public int maximumStepIndexForStepDisplay() {
		return currentStepDisplayed;
	}// end maximumStepIndexDisplayed

	// ========== rotationAngleForStepAtIndex:
	// ======================================
	//
	// Purpose: Returns the viewing angle which should be used when
	// displaying
	// the given step in Step Display mode.
	//
	// Notes: Rotations are NOT built up in a stack. One would think End
	// Rotation removes an item from the stack, but actually it
	// restores the default view. Each step rotation completely
	// replaces the previous rotation, although computing the new value
	// may require consulting the value about to be replaced.
	//
	// The actual rotation to use is whatever we get by calculating all
	// the rotations up to and including the specified step.
	//
	// Neither the step, the model, nor any other data-level class is
	// responsible for enforcing this angle when drawing. It is up to
	// the document to enforce or ignore the step rotation angle. In
	// Bricksmith, the document only sets the step rotation when in
	// Step Display mode, when the step being viewed is changed.
	//
	// ==============================================================================
	public Vector3f rotationAngleForStepAtIndex(int stepNumber) {
		ArrayList<LDrawStep> steps = steps();

		LDrawStep currentStep = null;
		LDrawStepRotationT rotationType = LDrawStepRotationT.LDrawStepRotationNone;
		Vector3f stepRotationAngle = Vector3f.getZeroVector3f();
		Vector3f previousRotation = Vector3f.getZeroVector3f();
		Vector3f newRotation = Vector3f.getZeroVector3f();
		Vector3f totalRotation = Vector3f.getZeroVector3f();
		Matrix4 rotationMatrix = Matrix4.getIdentityMatrix4();
		int counter = 0;

		// Start with the default 3D angle onto the stack. If no rotation is
		// ever
		// specified, that is the one we use.
		newRotation = LDrawUtilities
				.angleForViewOrientation(ViewOrientationT.ViewOrientation3D);
		totalRotation = newRotation;

		// Build the rotation stack
		for (counter = 0; counter <= stepNumber && counter < steps.size(); counter++) {
			currentStep = (LDrawStep) steps.get(counter);
			rotationType = currentStep.stepRotationType();
			stepRotationAngle = currentStep.rotationAngle();

			switch (rotationType) {
			case LDrawStepRotationNone:
				// Nothing to do here. This means "use whatever was on the stack
				// last."
				newRotation = totalRotation;
				break;

			case LDrawStepRotationRelative:

				// Start with the default 3D rotation
				previousRotation = LDrawUtilities
						.angleForViewOrientation(ViewOrientationT.ViewOrientation3D);

				// Add the new value to it.
				rotationMatrix = MatrixMath.Matrix4Rotate(
						Matrix4.getIdentityMatrix4(), stepRotationAngle);
				rotationMatrix = MatrixMath.Matrix4Rotate(rotationMatrix,
						previousRotation);
				newRotation = MatrixMath
						.Matrix4DecomposeXYZRotation(rotationMatrix);

				// convert from radians to degrees
				newRotation.setX((float) Math.toDegrees(newRotation.getX()));
				newRotation.setY((float) Math.toDegrees(newRotation.getY()));
				newRotation.setZ((float) Math.toDegrees(newRotation.getZ()));
				break;

			case LDrawStepRotationAbsolute:

				// Use the step's angle directly
				newRotation = stepRotationAngle;
				break;

			case LDrawStepRotationAdditive:

				// Peek at the previous rotation on the stack
				previousRotation = totalRotation;

				// Add the new value to it.
				rotationMatrix = MatrixMath.Matrix4Rotate(
						Matrix4.getIdentityMatrix4(), stepRotationAngle);
				rotationMatrix = MatrixMath.Matrix4Rotate(rotationMatrix,
						previousRotation);
				newRotation = MatrixMath
						.Matrix4DecomposeXYZRotation(rotationMatrix);

				// convert from radians to degrees
				newRotation.setX((float) Math.toDegrees(newRotation.getX()));
				newRotation.setY((float) Math.toDegrees(newRotation.getY()));
				newRotation.setZ((float) Math.toDegrees(newRotation.getZ()));
				break;

			case LDrawStepRotationEnd:

				// This means end all rotations and restore the default angle.
				// It's not a stack. Bizarre.
				newRotation = LDrawUtilities
						.angleForViewOrientation(ViewOrientationT.ViewOrientation3D);
				break;
			}

			// Replace the cumulative rotation with the newly-computed one
			totalRotation = newRotation;
		}

		// Return the final calculated angle. This is the absolute rotation
		// to
		// which
		// we are to set the view.

		return totalRotation;

	}// end rotationAngleForStepAtIndex:

	// ========== rotationCenter
	// ====================================================
	// ==============================================================================
	public Vector3f rotationCenter() {
		return rotationCenter;
	}

	// ========== stepDisplay
	// =======================================================
	//
	// Purpose: Returns YES if the receiver only displays the steps
	// through the index of the currentStepDisplayed instance variable.
	//
	// ==============================================================================
	public boolean stepDisplay() {
		return stepDisplayActive;

	}// end stepDisplay

	// ========== steps
	// =============================================================
	//
	// Purpose: Returns the steps which constitute this model.
	//
	// ==============================================================================
	public ArrayList<LDrawStep> steps() {
		ArrayList<LDrawStep> steps = new ArrayList<LDrawStep>();
		for (LDrawDirective item : subdirectives())
			steps.add((LDrawStep) item);
		return steps;

	}// end steps

	// ========== visibleStep
	// =======================================================
	//
	// Purpose: Returns the last step which would be drawn if this model
	// were
	// drawn right now.
	//
	// ==============================================================================
	public LDrawStep visibleStep() {
		ArrayList<LDrawStep> steps = steps();
		LDrawStep lastStep = null;

		if (stepDisplay() == true)
			lastStep = steps.get(maxStepIndexToOutput(subdirectives()));
		else
			lastStep = steps.get(steps.size() - 1);

		return lastStep;

	}// end visibleStep
		//
		//
		// #pragma mark -
		//
		// ========== setDraggingDirectives:
		// ============================================

	//
	// Purpose: Sets the parts which are being manipulated in the model
	// via
	// drag-and-drop.
	//
	// ==============================================================================

	public void setDraggingDirectives(ArrayList<LDrawDirective> directives) {
		LDrawStep dragStep = null;
		LDrawDirective currentDirective = null;
		int counter = 0;

		// Remove primitives from the previous dragging directives from the
		// optimized vertexes
		if (draggingDirectives != null) {
			ArrayList<LDrawLine> lines = new ArrayList<LDrawLine>();
			ArrayList<LDrawTriangle> triangles = new ArrayList<LDrawTriangle>();
			ArrayList<LDrawQuadrilateral> quadrilaterals = new ArrayList<LDrawQuadrilateral>();

			draggingDirectives.flattenIntoLines(lines, triangles,
					quadrilaterals, null, ColorLibrary.sharedColorLibrary()
							.colorForCode(LDrawColorT.LDrawCurrentColor),
					Matrix4.getIdentityMatrix4(), Matrix3.getIdentityMatrix3(),
					false);
		}

		// When we get sent nil directives, nil out the drag step.
		if (directives != null) {
			dragStep = LDrawStep.emptyStep();

			// The law of Bricksmith is that all parts in a model must be
			// enclosed in
			// a
			// step. Resistance is futile.
			for (counter = 0; counter < directives.size(); counter++) {
				currentDirective = directives.get(counter);
				dragStep.addDirective(currentDirective);
			}

			// Tell the element that it lives in us now. This is important for
			// submodel references being dragged; without it, they have no way
			// of
			// resolving their part reference, and thus can't draw during their
			// drag.
			dragStep.setEnclosingDirective(this);

			// ---------- Optimize primitives
			// ---------------------------------------
			ArrayList<LDrawLine> lines = new ArrayList<LDrawLine>();
			ArrayList<LDrawTriangle> triangles = new ArrayList<LDrawTriangle>();
			ArrayList<LDrawQuadrilateral> quadrilaterals = new ArrayList<LDrawQuadrilateral>();

			dragStep.flattenIntoLines(
					lines,
					triangles,
					quadrilaterals,
					null,
					ColorLibrary.sharedColorLibrary().colorForCode(
							LDrawColorT.LDrawCurrentColor),
					Matrix4.getIdentityMatrix4(), Matrix3.getIdentityMatrix3(),
					false);
		}

		draggingDirectives = dragStep;
	}// end setDraggingDirectives:
		// ========== setModelDescription:
		// ==============================================

	//
	// Purpose: Sets a new model description.
	//
	// ==============================================================================

	/**
	 * @param newDescription
	 * @uml.property name="modelDescription"
	 */
	public void setModelDescription(String newDescription) {
		modelDescription = newDescription;
	}// end setModelDescription:

	// ========== setFileName:
	// ======================================================
	//
	// Purpose: Sets the name the model is ostensibly saved under in the
	// file system. This may take on a rather different meaning in
	// multi-part documents. It also has no real connection with the
	// actual filesystem name.
	//
	// ==============================================================================
	/**
	 * @param newName
	 * @uml.property name="fileName"
	 */
	public void setFileName(String newName) {
		fileName = newName;
	}// end setFileName:

	// ========== setAuthor:
	// ========================================================
	//
	// Purpose: Changes the name of the person who created the model.
	//
	// ==============================================================================
	/**
	 * @param newAuthor
	 * @uml.property name="author"
	 */
	public void setAuthor(String newAuthor) {
		// LLW - Don't allow author to be set to nil, as this causes funky
		// behavior in the inspector
		if (newAuthor == null)
			newAuthor = "";
		author = newAuthor;
	}// end setAuthor:

	// ========== setMaximumStepIndexForStepDisplay:
	// ================================
	//
	// Purpose: Sets the index of the last step drawn. If the model is
	// not
	// currently in step-display mode, this call will NOT cause it to
	// enter step display.
	//
	// ==============================================================================
	public void setMaximumStepIndexForStepDisplay(int stepIndex)
			throws RangeException {
		// Need to check and make sure this step number is not overflowing the
		// bounds.
		int maximumIndex = steps().size() - 1;

		if (stepIndex > maximumIndex)
			throw new RangeException(String.format(
					"index (%ld) beyond maximum step index %ld",
					(long) stepIndex, (long) maximumIndex));
		else {
			invalCache(CacheFlagsT.CacheFlagBounds);
			invalCache(CacheFlagsT.DisplayList);
			currentStepDisplayed = stepIndex;
		}

	}// end setMaximumStepIndexForStepDisplay:

	// ========== setRotationCenter:
	// ================================================
	//
	// Purpose: Returns the point around which the model should be spun
	// while
	// being viewed.
	//
	// ==============================================================================
	/**
	 * @param newPoint
	 * @uml.property name="rotationCenter"
	 */
	public void setRotationCenter(Vector3f newPoint) {
		Vector3f oldPoint = rotationCenter;

		rotationCenter = newPoint;
	}

	// ========== setStepDisplay
	// ====================================================
	//
	// Purpose: Sets whether the receiver only displays the steps through
	// the index of the currentStepDisplayed instance variable.
	//
	// ==============================================================================
	public void setStepDisplay(boolean flag) {
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);
		stepDisplayActive = flag;

	}// end setStepDisplay:

	// ========== addStep
	// ===========================================================
	//
	// Purpose: Creates a new blank step at the end of the model. Returns
	// the
	// new step created.
	//
	// ==============================================================================
	public LDrawStep addStep() {
		LDrawStep newStep = LDrawStep.emptyStep();

		addDirective(newStep); // adds the step and tells it who it

		// belongs
		// to.

		return newStep;

	}// end addStep

	public LDrawStep addStep(int index) {
		LDrawStep newStep = LDrawStep.emptyStep();

		insertDirective(newStep, index); // adds the step and tells it who it

		// belongs
		// to.

		return newStep;

	}// end addStep

	// ========== addStep:
	// ==========================================================
	//
	// Purpose: Adds newStep at the end of the model.
	//
	// ==============================================================================
	public void addStep(LDrawStep newStep) {
		addDirective(newStep);
	}// end addStep:

	// ========== makeStepVisible:
	// ==================================================
	//
	// Purpose: Guarantees that the given step is visible in this model.
	//
	// ==============================================================================
	public void makeStepVisible(LDrawStep step) {
		int stepIndex = indexOfDirective(step);

		// If we're in step display, but below this step, make it visible.
		if (stepIndex != -1
				&& stepIndex > maxStepIndexToOutput(subdirectives())) {
			try {
				setMaximumStepIndexForStepDisplay(stepIndex);
			} catch (RangeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Otherwise, we see everything, so by definition this step is
		// visible.

	}// end makeStepVisible

	// ========== removeDirectiveAtIndex:
	// ===========================================
	//
	// Purpose: Removes one directive from our container. We override
	// this
	// to find out our directive index _before_ the removal so we can
	// keep our current step in sync!
	//
	// ==============================================================================
	public void removeDirectiveAtIndex(int index) {
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);
		if (index <= currentStepDisplayed && currentStepDisplayed > 0)
			--currentStepDisplayed;

		super.removeDirectiveAtIndex(index);
	}

	public void insertDirective(LDrawDirective directive, int index) {
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);
		super.insertDirective(directive, index);
	}

	// ========== acceptsDroppedDirective:
	// ==========================================
	//
	// Purpose: Returns YES if this container will accept a directive
	// dropped
	// on
	// it. Explicitly excludes LDrawLSynthDirectives such as
	// INSIDE/OUTSIDE
	// and self-referencing model "parts"
	//
	// ==============================================================================
	public boolean acceptsDroppedDirective(LDrawDirective directive) {
		// explicitly disregard LSynth directives
		if (LDrawLSynthDirective.class.isInstance(directive)) {
			return false;
		}

		// explicitly disregard self-references if the dropped directive is a
		// model "part"
		else if (LDrawPart.class.isInstance(directive)) {
			String referenceName = ((LDrawPart) directive).referenceName();
			String enclosingModelName = "";

			LDrawDirective enclosingModel = enclosingModel();

			if (LDrawMPDModel.class.isInstance(enclosingModel)) {
				enclosingModelName = ((LDrawMPDModel) enclosingModel())
						.modelName();
			}

			if (enclosingModelName == referenceName) {
				return false;
			}
		}

		return true;
	}

	// ========== maxStepIndexToOutput
	// ==============================================
	//
	// Purpose: Returns the index of the last step which should be
	// displayed.
	//
	// Notes: This is always supposed to return an index >= 0, simply
	// because
	// it is illegal for a model to have no steps in Bricksmith.
	//
	// ==============================================================================
	public int maxStepIndexToOutput(ArrayList<LDrawDirective> steps) {
		int maxStep = 0;

		// If step display is active, we want to display only as far as the
		// specified step, or the maximum step if the one specified exceeds
		// the
		// number of steps.
		if (stepDisplayActive == true) {
			maxStep = Math.min(steps.size() - 1, // subtract one to get last
													// step index in model.
					currentStepDisplayed);
		} else {
			maxStep = steps.size() - 1;
		}

		return maxStep;

	}// end maxStepIndexToOutput

	// ========== numberElements
	// ====================================================
	//
	// Purpose: Returns the number of elements found in this model.
	// Currently
	// this does not recurse into MPD submodels which have been
	// included.
	//
	// ==============================================================================
	public int numberElements() {
		int numberElements = 0;

		for (LDrawStep step : steps())
			numberElements += step.subdirectives().size();

		return numberElements;

	}// end numberElements

	// ========== parseHeaderFromLines:beginningAtIndex:
	// ============================
	//
	// Purpose: Given lines from an LDraw document, fill in the model
	// header
	// info. It should be of the following format:
	//
	// 0 7140 X-Wing Fighter
	// 0 Name: main.ldr
	// 0 Author: Tim Courtney <tim@zacktron.com>
	// 0 LDraw.org Official Model Repository
	// 0 http://www.ldraw.org/repository/official/
	//
	// Note, however, that this information is *not* required, so it
	// may not be there. Consequently, the code below is a nightmarish
	// unmaintainable mess.
	//
	// Returns the line index of the first non-header line.
	//
	// ==============================================================================
	private int parseHeaderFromLines(ArrayList<String> lines, int index) {
		String currentLine = null;
		int counter = 0;
		boolean lineValidForHeader = false;
		int firstNonHeaderIndex = index;
		ByteBuffer payload = ByteBuffer.allocate(400);

		try {
			// First line. Should be a description of the model.
			currentLine = lines.get(index);
			if (line(currentLine, "FILE", payload)) {
				byte[] strByte = new byte[payload.position()];
				payload.clear();
				payload.get(strByte);

				setModelDescription(new String(strByte));
				firstNonHeaderIndex++;
			}

			// There are at least three more lines in a valid header.
			// Read the first four lines, and try to get the model info out of
			// them.
			lineValidForHeader = true;
			for (counter = firstNonHeaderIndex; counter < firstNonHeaderIndex + 3
					&& lineValidForHeader == true && counter < lines.size(); counter++) {				
				currentLine = lines.get(counter);
				lineValidForHeader = false; // assume not, then disprove
				payload.clear();

				// Second line. Should be file name.
				if (line(currentLine, LDrawKeywords.LDRAW_HEADER_NAME, payload)) {
					byte[] strByte = new byte[payload.position()];
					payload.position(0);
					payload.get(strByte);

					setFileName(new String(strByte));
					lineValidForHeader = true;
				}
				// Third line. Should be author name.
				else if (line(currentLine, LDrawKeywords.LDRAW_HEADER_AUTHOR,
						payload)) {
					byte[] strByte = new byte[payload.position()];
					payload.position(0);
					payload.get(strByte);
					setAuthor(new String(strByte));
					lineValidForHeader = true;
				}
				// Fourth line. MLCad used it as a nonstandard way of indicating
				// official status. Since it was nonstandard, nobody used it.
				else if (line(currentLine, "", payload)) {
					byte[] strByte = new byte[payload.position()];
					payload.position(0);
					payload.get(strByte);
					String str = new String(strByte);
					if (str.equals("LDraw.org Official Model Repository")
							|| str.equals("Unofficial Model")) {
						// Bricksmith followed MLCad spewing out this garbage
						// for
						// years. It is unnecessary. Now I am just stripping it
						// out
						// of any file I encounter.
						lineValidForHeader = true;
					}
				}

				if (lineValidForHeader == true) {
					firstNonHeaderIndex++;
				}
			}
		} catch (Exception e) {
			// Ran out of lines in the file. Oh well. We got what we got.
			e.printStackTrace();
		}

		return firstNonHeaderIndex;

	}// end parseHeaderFromLines

	// ========== line:isValidForHeader:
	// ============================================
	//
	// Purpose: Determines if the given line of LDraw is formatted to be
	// the
	// the specified field in a model header.
	//
	// ==============================================================================
	private boolean line(String line, String headerKey, ByteBuffer infoPtr) {
		String parsedField = null;
		boolean isValid = false;

		StringTokenizer strTokenizer = new StringTokenizer(line);
		if (strTokenizer.hasMoreElements())
			parsedField = strTokenizer.nextToken();
		else
			return isValid;

		if (parsedField.equals("0")) {
			if (headerKey.length() > 0 && strTokenizer.hasMoreTokens()) {				
				parsedField = strTokenizer.nextToken();
				isValid = parsedField.equals(headerKey);
			} else {
				isValid = true;
			}

			if (isValid) {
				if (infoPtr != null) {
					String infoStr = "";
					while (strTokenizer.hasMoreTokens()) {
						infoStr += strTokenizer.nextToken();
						if (strTokenizer.hasMoreTokens())
							infoStr += " ";
					}
					infoPtr.put(infoStr.getBytes());
				}
			}
		}

		return isValid;

	}// end line:isValidForHeader:

	// ========== optimizeStructure
	// =================================================
	//
	// Purpose: Arranges the directives in such a way that the file will be
	// drawn faster. This method should *never* be called on files
	// which the user has created himself, since it reorganizes the
	// file contents. It is intended only for parts read from the part
	// library.
	//
	// To optimize, we flatten all the primitives referenced by a part
	// into a non-nested structure, then separate all the directives
	// out by the type: all triangles go in a step, all quadrilaterals
	// go in their own step, etc.
	//
	// Then when drawing, we need not call glBegin() each time. The
	// result is a speed increase of over 1000%.
	//
	// 1000%. That is not a typo.
	//
	// ==============================================================================
	public void optimizeStructure() {
		if(isOptimized==true)return;
		
		ArrayList<LDrawDirective> steps = subdirectives();

		ArrayList<LDrawLine> lines = new ArrayList<LDrawLine>();
		ArrayList<LDrawTriangle> triangles = new ArrayList<LDrawTriangle>();
		ArrayList<LDrawQuadrilateral> quadrilaterals = new ArrayList<LDrawQuadrilateral>();
		ArrayList<LDrawDirective> everythingElse = new ArrayList<LDrawDirective>();

		LDrawStep linesStep = LDrawStep
				.emptyStepWithFlavor(LDrawStepFlavorT.LDrawStepLines);
		LDrawStep trianglesStep = LDrawStep
				.emptyStepWithFlavor(LDrawStepFlavorT.LDrawStepTriangles);
		LDrawStep quadrilateralsStep = LDrawStep
				.emptyStepWithFlavor(LDrawStepFlavorT.LDrawStepQuadrilaterals);
		LDrawStep everythingElseStep = LDrawStep
				.emptyStepWithFlavor(LDrawStepFlavorT.LDrawStepAnyDirectives);

		// Traverse the entire hiearchy of part references and sort out each
		// primitive type into a flat list. This allows staggering speed
		// increases.
		//
		// If we were to only sort without flattening, we would get a 100% speed
		// increase. But flattening and sorting yields over 1000%.
		flattenIntoLines(
				lines,
				triangles,
				quadrilaterals,
				everythingElse,
				ColorLibrary.sharedColorLibrary().colorForCode(
						LDrawColorT.LDrawCurrentColor),
				Matrix4.getIdentityMatrix4(), Matrix3.getIdentityMatrix3(),
				true);

		// Now that we have everything separated, remove the main step (it's the
		// one
		// that has the entire model in it) and .
		int directiveCount, counter;
		directiveCount = steps.size();
		for (counter = (directiveCount - 1); counter >= 0; counter--) {
			removeDirectiveAtIndex(counter);
		}

		// Replace the original directives with the categorized steps we've
		// created
		if (lines.size() > 0) {
			for (LDrawDirective directive : lines) {
				linesStep.addDirective(directive);
			}
			addDirective(linesStep);
		}

		if (triangles.size() > 0) {
			for (LDrawDirective directive : triangles) {
				trianglesStep.addDirective(directive);
			}
			addDirective(trianglesStep);
		}

		if (quadrilaterals.size() > 0) {
			for (LDrawDirective directive : quadrilaterals) {
				quadrilateralsStep.addDirective(directive);
			}
			addDirective(quadrilateralsStep);
		}
		if (everythingElse.size() > 0 || subdirectives().size() == 0) { // Make
																		// sure
																		// there
																		// is at
																		// least
																		// one
																		// step
																		// in
																		// the
																		// model!
			for (LDrawDirective directive : everythingElse) {
				everythingElseStep.addDirective(directive);
			}
			addDirective(everythingElseStep);
		}

		isOptimized = true;

	}// end optimizeStructure

	// ========== registerUndoActions
	// ===============================================
	//
	// Purpose: Registers the undo actions that are unique to this
	// subclass,
	// not to any superclass.
	//
	// ==============================================================================
	public void registerUndoActions(UndoManager undoManager) {
		super.registerUndoActions(undoManager);

		// todo
		// undoManager.prepareWithInvocationTarget(this).setAuthor(author());
		// undoManager.prepareWithInvocationTarget(this).setFileName(fileName());
		// undoManager.prepareWithInvocationTarget(this).setModelDescription(modelDescription());
		//
		// undoManager.setActionName("UndoAttributesModel");

	}// end registerUndoActions:

	public Object clone() throws CloneNotSupportedException {
		LDrawModel a = (LDrawModel) super.clone();
		// a.rotationCenter = (Vector3f) rotationCenter.clone();
		// a.colorLibrary = colorLibrary;
		// a.cachedBounds = (Box3) cachedBounds.clone();
		// if (draggingDirectives != null)
		// a.draggingDirectives = (LDrawStep) draggingDirectives.clone();
		return a;
	}
	
	public void notifyChanged(){
		sendMessageToObservers(MessageT.MessageObservedChanged);
	}
}
