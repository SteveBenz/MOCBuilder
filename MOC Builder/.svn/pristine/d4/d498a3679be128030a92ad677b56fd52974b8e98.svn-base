package LDraw.Files;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.TreeSet;

import javax.media.opengl.GL2;

import Command.LDrawLSynthDirective;
import Command.LDrawPart;
import Common.Box2;
import Common.Box3;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;
import Common.Vector3f;
import LDraw.Support.DispatchGroup;
import LDraw.Support.ILDrawObservable;
//==============================================================================
//
//File:		LDrawFile.h
//
//Purpose:		Represents an LDraw file, composed of one or more models.
//				In Bricksmith, each file is interpreted as a Multi-Part Document
//				having multiple submodels. Only LDrawMPDModels can be contained 
//				in the file's subdirective array. However, when the document is 
//				written out, the MPD commands are stripped if there is only 
//				one model in the file.
//
//Threading:	An LDrawFile can be drawn by multiple threads simultaneously. 
//				What we must not do is edit while drawing or draw while editing. 
//				To prevent such unpleasantries, bracket any editing to this File 
//				(or any descendant directives) with calls to -lockForEditing and 
//				-unlockEditor.
//
//Created by Allen Smith on 2/19/05.
//Copyright (c) 2005. All rights reserved.
//==============================================================================
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.PartReport;
import LDraw.Support.Range;
import LDraw.Support.type.MessageT;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Renderer.ILDrawCollector;
import Renderer.ILDrawRenderer;

/**
 * @Class LDrawFile
 * 
 * @Purpose Represents an LDraw file, composed of one or more models. In
 *          Bricksmith, each file is interpreted as a Multi-Part Document having
 *          multiple submodels. Only LDrawMPDModels can be contained in the
 *          file's subdirective array. However, when the document is written
 *          out, the MPD commands are stripped if there is only one model in the
 *          file.
 * @Represent LDrawFile.(h, m) of Bricksmith
 * 
 * @author funface2
 * @since 2014-03-18
 * 
 */
public class LDrawFile extends LDrawContainer {

	//public static final String LDrawFileActiveModelDidChangeNotification = "LDrawFileActiveModelDidChangeNotification";

	/**
	 * @uml.property name="nameModelDict"
	 * @uml.associationEnd 
	 *                     qualifier="toLowerCase:java.lang.String LDraw.Files.LDrawMPDModel"
	 */
	Dictionary<String, LDrawMPDModel> nameModelDict;
	/**
	 * @uml.property name="activeModel"
	 * @uml.associationEnd
	 */
	LDrawMPDModel activeModel;
	/**
	 * @uml.property name="filePath"
	 */
	String filePath; // where this file came from on disk.

	public static LDrawFile newEditableFile() {
		LDrawFile file = new LDrawFile();

		LDrawMPDModel firstModel = LDrawMPDModel.model();
		// Fill it with one empty model.
		file.addSubmodel(firstModel);
		file.setActiveModel(firstModel);
		return file;
	}

	private LDrawFile() {
		init();
	}
	
	public static LDrawFile newEmptyFile(){
		LDrawFile file = new LDrawFile();
		return file;
	}

	public static LDrawFile fileFromContentsAtPath(String path) {
		LDrawFile file = null;
		String fileContents = LDrawUtilities.stringFromFile(path);

		if (fileContents != null && !fileContents.equals("")) {
			file = parseFromFileContents(fileContents);
			file.setPath(path);
		}
		return file;

	}// end fileFromContentsAtPath:

	// ---------- parseFromFileContents:
	//
	// Purpose: Reads a file out of the raw file contents.
	//
	// ------------------------------------------------------------------------------
	private static LDrawFile parseFromFileContents(String fileContents) {
		String[] lines = fileContents.replaceAll("\r", "").split("\n");
		ArrayList<String> lineArray = new ArrayList<String>();
		for (String line : lines)
			lineArray.add(line);
		
		LDrawFile file = new LDrawFile();
		file.initWithLines(lineArray, new Range(0, lines.length));
		return file;
	}// end parseFromFileContents:allowThreads:

	// ========== init
	// ==============================================================
	//
	// Purpose: Creates a new file with absolutely nothing in it.
	//
	// ==============================================================================
	public LDrawFile init() {
		super.init(); // initializes an empty list of subdirectives--in this
		// case, the models in the file.

		activeModel = null;
		// drawCount = 0;
		// editLock = [[NSConditionLock alloc] initWithCondition:0];
		return this;
	}// end init

	// ========== updateModelLookupTable
	// ============================================
	//
	// Purpose: Rebuilds the optimized lookup table for models. This is now
	// an internal method, run when we add or remove a directive,
	// after coder init, and any time one of our children renames
	// itself.
	//
	// ==============================================================================
	public void updateModelLookupTable() {
		// ArrayList<LDrawMPDModel> submodels = submodels();
		// ArrayList<String> names = new ArrayList<String>(submodels.size());

		nameModelDict = new Hashtable<String, LDrawMPDModel>();

		for (LDrawMPDModel model : submodels()) {
			// always use lowercase for comparison
			nameModelDict.put(model.modelName().toLowerCase(), model);
		}
	}

	// ========== initWithLines:inRange:parentGroup:
	// ================================
	//
	// Purpose: Parses the MPD models out of the lines. If lines contains a
	// single non-MPD model, it will be wrapped in an MPD model.
	//
	// ==============================================================================

	public LDrawFile initWithLines(ArrayList<String> lines, Range range,
			DispatchGroup parentGroup) {
		Range modelRange = range;
		int modelStartIndex = range.getLocation();
		ArrayList<LDrawMPDModel> submodels = null;
		int insertIndex = 0;

		try {
			if (super.initWithLines(lines, range, parentGroup) != null) {
				submodels = new ArrayList<LDrawMPDModel>();
				DispatchGroup dispatchGroup = null;
				// todo
				// #if USE_BLOCKS
				// dispatch_queue_t queue =
				// dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT,
				// 0);
				// dispatchGroup = dispatch_group_create();
				//
				// if(parentGroup != NULL)
				// dispatch_group_enter(parentGroup);
				// #endif

				// Search through all the lines in the file, and separate them
				// out
				// into
				// submodels.
				do {

					modelRange = LDrawMPDModel
							.rangeOfDirectiveBeginningAtIndex(modelStartIndex,
									lines, range.getMaxRange());
					// Parse
					// todo
					// #if USE_BLOCKS
					// dispatch_group_async(dispatchGroup, queue,
					// ^{
					// #endif
					LDrawMPDModel newModel = new LDrawMPDModel();
					newModel.initWithLines(lines, modelRange, dispatchGroup);
					// Store non-retaining, but *thread-safe* container
					// (NSMutableArray is NOT). Since it doesn't retain, we
					// mustn't
					// autorelease newDirective.
					submodels.add(insertIndex, newModel);
					// #if USE_BLOCKS
					// });
					// #endif

					modelStartIndex = modelRange.getMaxRange()+1;
					insertIndex += 1;
				} while (modelStartIndex <= range.getMaxRange());

				// #if USE_BLOCKS
				// dispatch_group_notify(dispatchGroup,queue,
				// ^{
				// #endif
				int counter = 0;
				LDrawMPDModel currentModel = null;

				// Add all the models in order
				for (counter = 0; counter < insertIndex; counter++) {
					currentModel = submodels.get(counter);

					addSubmodel(currentModel);
				}

				if (submodels().size() > 0)
					setActiveModel(submodels().get(0));

				// free(submodels);

				// #if USE_BLOCKS
				// if(parentGroup != NULL)
				// dispatch_group_leave(parentGroup);
				//
				// });
				// dispatch_release(dispatchGroup);
				// #endif
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return this;

	}// end initWithLines:inRange:

	// ========== collectColor:viewScale:parentColor:
	// =======================================
	//
	// Purpose: Collect color information from ldconfig.ldr. It is used for ColorLibrary initialization.
	// ==============================================================================
	public void collectColor() {
		// this is like calling the non-existent method
		// [editLock setCondition:([editLock condition] + 1)]
		// [editLock lock]; //lock unconditionally
		// self->drawCount += 1;
		// [editLock unlockWithCondition:(self->drawCount)]; //don't block
		// multiple simultaneous draws!
		//
		// Draw!
		// (only the active model.)

		activeModel.collectColor();

		// done drawing; decrement the lock's condition
		// [editLock lock];
		// self->drawCount -= 1;
		// [editLock unlockWithCondition:(self->drawCount)];

	}// end draw:viewScale:parentColor:

	// ========== drawSelf:
	// ===========================================================
	//
	// Purpose: Draw this directive and its subdirectives by calling APIs on
	// the passed in renderer, then calling drawSelf on children.
	//
	// ================================================================================
	public void drawSelf(GL2 gl2, ILDrawRenderer renderer) {
		activeModel.drawSelf(gl2, renderer);
	}// end drawSelf:

	// ========== collectSelf:
	// ========================================================
	//
	// Purpose: Collect self is called on each directive by its parents to
	// accumulate _mesh_ data into a display list for later drawing.
	// The collector protocol passed in is some object capable of
	// remembering the collectable data.
	//
	// Notes: The file should never be 'collected', because parts do not
	// reference files - rather they reference the models WITHIN
	// files. So while we have a release implementation of passing
	// the message on, we have an assert to catch this case.
	//
	// ================================================================================
	public void collectSelf(ILDrawCollector renderer) {
		assert true : "Why are we here?";
		activeModel.collectSelf(renderer);
	}// end collectSelf:

	// ========== debugDrawboundingBox
	// ==============================================
	//
	// Purpose: Draw a translucent visualization of our bounding box to test
	// bounding box caching.
	//
	// ==============================================================================
	public void debugDrawboundingBox(GL2 gl2) {
		activeModel.debugDrawboundingBox(gl2);
	}// end debugDrawboundingBox

	public void getRange( Matrix4 transform, float[] range )
	{
		activeModel.getRange( transform, range );
	}
	// ========== hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Hit-test the geometry.
	//
	// ==============================================================================
	public void hitTest(Ray3 pickRay, Matrix4 transform, float scaleFactor,
			boolean boundsOnly, LDrawDirective creditObject,
			HashMap<LDrawDirective, Float> hits) {
		activeModel.hitTest(pickRay, transform, scaleFactor, boundsOnly,
				creditObject, hits);
	}
	public void hitTest(Ray3 pckRay, Matrix4 transform, 
			LDrawDirective creditObject, HashMap<LDrawDirective, Float> hits) {
		// subclasses should override this with hit-detection code
		activeModel.hitTest(pckRay, transform, creditObject, hits);
	}

	// ========== boxTest:transform:boundsOnly:creditObject:hits:
	// ===================
	//
	// Purpose: Check for intersections with screen-space geometry.
	//
	// ==============================================================================
	public boolean boxTest(Box2 bounds, Matrix4 transform, boolean boundsOnly,
			LDrawDirective creditObject, TreeSet<LDrawDirective> hits) {
		return activeModel.boxTest(bounds, transform, boundsOnly, creditObject,
				hits);
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
			LDrawDirective creditObject, ArrayList<LDrawDirective> bestObject, FloatBuffer bestDepth) {
		activeModel.depthTest(testPt, bounds, transform, creditObject, bestObject,
				bestDepth);
	}// end depthTest:inBox:transform:creditObject:bestObject:bestDepth:

	// ========== write
	// =============================================================
	//
	// Purpose: Write out all the submodels sequentially.
	//
	// ==============================================================================
	public String write() {
		String written = new String();
		String CRLF = "\r\n";

		LDrawMPDModel currentModel = null;
		ArrayList<LDrawMPDModel> modelsInFile = submodels();

		int numberModels = modelsInFile.size();
		int counter = 0;

		// If there is only one submodel, this hardly qualifies as an MPD
		// document.
		// So write out the single model without the MPD FILE/NOFILE wrapper.
		if (numberModels == 1) {
			currentModel = modelsInFile.get(0);
			// Write out the model, without MPD wrappers.
			written += currentModel.writeModel();
		} else {
			// Write out each MPD submodel, one after another.
			for (counter = 0; counter < numberModels; counter++) {
				currentModel = modelsInFile.get(counter);
				written += currentModel.write();
				written += CRLF;
			}
		}

		// Trim off any final newline characters.
		return written.trim();

	}// end write

	// ========== lockForEditing
	// ====================================================
	//
	// Purpose: Aquires a mutex lock to allow safe editing. Calling this
	// method
	// will guarantee that no other thread draws or edits the file
	// while you are modifying it. Calls to this method must be
	// subsequently balanced by a call to -unlockEditor.
	//
	// If you are editing some subdirective buried deep down the file's
	// hierarchy, it is still your responsibility to call this method.
	// For performance reasons, it does NOT happen automatically!
	//
	// ==============================================================================
	public void lockForEditing() {
		// This was once a sure-fire way to hang the program if this lock
		// code is
		// enabled:
		// 1. Create a new model
		// 2. Add a part
		// 3. Open Inspector for that part.
		// 4. Click in the name field and change the part's name. DO NOT HIT
		// RETURN
		// to confirm the edit.
		// 5. Click back in the document.
		// 6. Drag the part out of the document into oblivion to delete it.
		// 7. Bricksmith deadlocks. This occurs because the program tries to
		// commit
		// the editing from step 4 immediately after deleting the part. Both
		// those operations try to aquire this lock, which they can't do
		// because
		// it isn't recursive.
		//
		// The bug here was really in step 5, in which the Inspector was
		// erroneously
		// not commiting its changes. I've fixed that bug now, so this
		// sequence
		// can't hang the application anymore. However, the warning still
		// stands:
		// this lock is not reentrant.
		// aquire the lock once nobody is drawing the File. The condition on
		// this
		// lock
		// tracks the number of threads currently drawing the File. We don't
		// want
		// to
		// go modifying data at the same time someone else is trying to draw
		// it!
		// [self->editLock lockWhenCondition:0];

	}// end lockForEditing

	// ========== unlockEditor
	// ======================================================
	//
	// Purpose: Releases the mutual-exclusion lock that prevents
	// concurrent
	// drawing or editing. A call to this method must be balanced by a
	// preceeding call to -lockForEditing.
	//
	// ==============================================================================
	public void unlockEditor() {
		// the condition tracks number of outstanding draws. We aren't a draw,
		// and
		// can't aquire this lock unless there are no draws. So we stay at 0.
		// [self->editLock unlockWithCondition:0];

	}// end unlockEditor
		//
		//
		// #pragma mark -
		// #pragma mark ACCESSORS
		// #pragma mark -
		//
		// ========== activeModel
		// =======================================================

	//
	// Purpose: Returns the name of the currently-active model in the
	// file.
	//
	// ==============================================================================

	public LDrawMPDModel activeModel() {
		return activeModel;

	}// end activeModel

	// ========== firstModel
	// =======================================================
	//
	// Purpose: Returns the first model in the file, which is the one to
	// use
	// when referred to from a separate peer file.
	//
	// ==============================================================================
	public LDrawMPDModel firstModel() {
		return (LDrawMPDModel) subdirectives().get(0);
	}// end firstModel

	// ========== draggingDirectives
	// ================================================
	//
	// Purpose: Returns the objects that are currently being displayed as
	// part
	// of drag-and-drop.
	//
	// ==============================================================================
	public ArrayList<LDrawDirective> draggingDirectives() {
		return activeModel().draggingDirectives();

	}// end draggingDirectives

	// ========== modelNames
	// ========================================================
	//
	// Purpose: Returns the the names of all the submodels in the file.
	//
	// ==============================================================================
	public ArrayList<String> modelNames() {
		ArrayList<LDrawMPDModel> submodels = submodels();
		
		ArrayList<String> modelNames = new ArrayList<String>();
		
		for(LDrawMPDModel submodel : submodels){
			modelNames.add(submodel.modelName());
		}

		return modelNames;

	}// end modelNames

	// ========== modelWithName:
	// ====================================================
	//
	// Purpose: Returns the submodel with the given name, or nil if one
	// couldn't
	// be found.
	//
	// ==============================================================================
	public LDrawMPDModel modelWithName(String soughtName) {
		String referenceName = soughtName.toLowerCase();// we
		// standardized on lower-case names for searching.
		LDrawMPDModel foundModel = nameModelDict.get(referenceName);

		return foundModel;

	}// end modelWithName:
		//
		// ========== path
		// ==============================================================

	//
	// Purpose: Returns the filesystem path at which this file was
	// resides,
	// or
	// nil if that information is undetermined. Only files that are
	// read by the user will have their paths set; parts from the
	// library disregard this information.
	//
	// ==============================================================================

	public String path() {
		return filePath;
	}// end path

	// ========== submodels
	// =========================================================
	//
	// Purpose: Returns an array of the LDrawModels (or more likely, the
	// LDrawMPDModels) which constitute this file.
	//
	// ==============================================================================
	public ArrayList<LDrawMPDModel> submodels() {
		ArrayList<LDrawMPDModel> arrayList = new ArrayList<LDrawMPDModel>();
		for (LDrawDirective item : subdirectives())
			arrayList.add((LDrawMPDModel) item);
		return arrayList;
	}// end submodels
		//
		//
		// #pragma mark -
		//
		// ========== setActiveModel:
		// ===================================================

	//
	// Purpose: Sets newModel to be the currently-active model in the
	// file.
	// The active model is the only one drawn.
	//
	// ==============================================================================

	/**
	 * @param newModel
	 * @uml.property name="activeModel"
	 */
	public void setActiveModel(LDrawMPDModel newModel) {
		NotificationCenter notificationCenter = NotificationCenter.getInstance();

		if (submodels().contains(newModel)) {
			// Don't bother doing anything if we aren't really changing models.
			if (newModel != activeModel) {
				// Update the active model and note that something happened.
				activeModel = newModel;
				
				 if(postsNotifications)
					 notificationCenter.postNotification(NotificationMessageT.LDrawFileActiveModelDidChange, null);
			}
		} else if (newModel == null) {

			activeModel = null;
		} else
			System.out
					.println("Attempted to set the active model to one which is not in the file!");

	}// end setActiveModel:

	// ========== setDraggingDirectives:
	// ============================================
	//
	// Purpose: Sets the parts which are being manipulated in the model
	// via
	// drag-and-drop.
	//
	// Notes: This is a convenience method for LDrawGLView, which might
	// not
	// care to wonder whether it's displaying a model or a file. In
	// either event, we just want to drag-and-drop, and that's defined
	// in the model.
	//
	// ==============================================================================
	public void setDraggingDirectives(ArrayList<LDrawDirective> directives) {
		activeModel().setDraggingDirectives(directives);
	}// end setDraggingDirectives:

	// ========== setEnclosingDirective:
	// ============================================
	//
	// Purpose: In other containers, this method would set the object
	// which
	// encloses this one. LDrawFiles, however, are intended to be at
	// the root of the LDraw container hierarchy, and thus calling this
	// method should have no effect.
	//
	// ==============================================================================
	public void setEnclosingDirective(LDrawContainer newParent) {
		// Do Nothing.

	}// end setEnclosingDirective:

	// ========== setPath:
	// ==========================================================
	//
	// Purpose: Sets the filesystem path at which this file was resides.
	// Only
	// files that are read by the user will have their paths set; parts
	// from the library disregard this information.
	//
	// ==============================================================================
	public void setPath(String newPath) {
		this.filePath = newPath;

	}// end setPath:

	// ========== removeDirective:
	// ==================================================
	//
	// Purpose: In other containers, this method would set the object
	// which
	// encloses this one. LDrawFiles, however, are intended to be at
	// the root of the LDraw container hierarchy, and thus calling this
	// method should have no effect.
	//
	// ==============================================================================
	public void removeDirective(LDrawDirective doomedDirective) {
		boolean removedActiveModel = false;

		if (doomedDirective == activeModel)
			removedActiveModel = true;

		super.removeObserver(doomedDirective);

		if (removedActiveModel == true) {
			if (submodels().size() > 0)
				setActiveModel(submodels().get(0));
			else
				setActiveModel(null); // this is probably not a good thing.
		}

	}// end removeDirective:

	//
	// #pragma mark -
	// #pragma mark ACTIONS
	// #pragma mark -
	//
	// ========== addSubmodel:
	// ======================================================
	//
	// Purpose: Adds a new submodel to the file. This method only accepts
	// MPD
	// models, because adding additional submodels is meaningless
	// outside of MPD models.
	//
	// ==============================================================================
	public void addSubmodel(LDrawMPDModel newSubmodel) {
		insertDirective(newSubmodel, subdirectives().size());

	}// end addSubmodel:

	// ========== insertDirective:atIndex:
	// ==========================================
	//
	// Purpose: Adds directive into the collection at position index.
	//
	// ==============================================================================
	public void insertDirective(LDrawDirective directive, int index) {
		super.insertDirective(directive, index);
		updateModelLookupTable();

		// Post a notification on ourself that a model was added - missing
		// parts
		// need
		// to know this to re-check whether they match this model.
		// todo
		// [[NSNotificationCenter defaultCenter]
		// postNotificationName:LDrawMPDSubModelAdded object:self ];

	}// end insertDirective:atIndex:

	//
	// ========== removeDirectiveAtIndex:
	// ===========================================
	//
	// Purpose: Removes the LDraw directive stored at index in this
	// collection.
	//
	// ==============================================================================
	public void removeDirectiveAtIndex(int index) {
		super.removeDirectiveAtIndex(index);
		updateModelLookupTable();
	}// end removeDirectiveAtIndex:
		//
		//
		// #pragma mark -
		// #pragma mark UTILITIES
		// #pragma mark -
		//
		// ========== acceptsDroppedDirective:
		// ==========================================

	//
	// Purpose: Returns YES if this container will accept a directive
	// dropped
	// on
	// it. Explicitly excludes LDrawLSynthDirectives such as
	// INSIDE/OUTSIDE
	//
	// ==============================================================================

	public boolean acceptsDroppedDirective(LDrawDirective directive) {
		// explicitly disregard LSynth directives
		if (LDrawLSynthDirective.class.isInstance(directive)) {
			return false;
		}
		return true;
	}

	// ========== boundingBox3
	// ======================================================
	//
	// Purpose: Returns the minimum and maximum points of the box which
	// perfectly contains the part of this file being displayed.
	//
	// ==============================================================================
	public Box3 boundingBox3() {
		// todo
		// revalCache(CacheFlagBounds);
		Box3 ret = activeModel().boundingBox3();

		return ret;

	}// end boundingBox3

	// ========== projectedBoundingBoxWithModelView:projection:view:
	// ================
	//
	// Purpose: Returns the 2D projection (you should ignore the z) of
	// the
	// object's bounds.
	//
	// ==============================================================================
	public Box3 projectedBoundingBoxWithModelView(Matrix4 modelView,
			Matrix4 projection, Box2 viewport) {
		return activeModel().projectedBoundingBoxWithModelView(modelView,
				projection, viewport);

	}// end projectedBoundingBoxWithModelView:projection:view:

	// ========== renameModel:toName:
	// ===============================================
	//
	// Purpose: Sets the name of the given member submodel to the new
	// name,
	// and
	// updates all internal references to the submodel to use the new
	// name as well.
	//
	// ==============================================================================
	public void renameModel(LDrawMPDModel submodel, String newName) {
		ArrayList<LDrawMPDModel> submodels = submodels();
		boolean containsSubmodel = submodels.contains(submodel);
		String oldName = submodel.modelName();
		PartReport partReport = null;
		ArrayList<LDrawPart> allParts = null;
		LDrawPart currentPart = null;
		int counter = 0;

		if (containsSubmodel == true && oldName != newName) {
			// Update the model name itself
			submodel.setModelName(newName);

			// Update all references to the old name
			partReport = PartReport.partReportForContainer(this);
			allParts = partReport.allParts();

			for (counter = 0; counter < allParts.size(); counter++) {
				currentPart = allParts.get(counter);

				// If the part points to the old name, change it to the new one.
				// Since the user can enter these values and Bricksmith is
				// case-insensitive, make sure to ignore case.
				if (currentPart.referenceName().toLowerCase() == oldName
						.toLowerCase()) {
					currentPart.setDisplayName(newName);
				}
			}
		}

	}// end renameModel:toName:
		//
		//
		// #pragma mark -
		// #pragma mark OBSERVATION
		// #pragma mark -
		//
		//
		// ========== receiveMessage:who:
		// ===============================================

	//
	// Purpose: LDrawFile overrides the message handler to get access to
	// name
	// change announcements from its contained MDP models. In this
	// way it can rebuild the lookup table.
	//
	// Notes: Someday if we want to get clever we could rebuild only part
	// of the lookup table based on the actual object that changed.
	// But since renames are rare it's probably not worth it.
	//
	// ==============================================================================

	public void receiveMessage(MessageT msg, ILDrawObservable observable) {
		if (msg == MessageT.MessageNameChanged)
			updateModelLookupTable();		

		super.receiveMessage(msg, observable);
	}

	//
	//
	// #pragma mark -
	// #pragma mark DESTRUCTOR
	// #pragma mark -
	//
	// //========== dealloc
	// ===========================================================
	// //
	// // Purpose: Takin' care o' business.
	// //
	// //==============================================================================
	// - (void) dealloc
	// {
	// //NSLog(@"File %s going away.\n", [filePath UTF8String]);
	// [nameModelDict release];
	// [activeModel release];
	// [filePath release];
	// // [editLock release];
	//
	// [super dealloc];
	//
	// }//end dealloc
	public Vector3f rotationCenter() {
		assert true;
		return null;
	}

	// }
}