package LDraw.Support;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TreeSet;

import javax.media.opengl.GL2;

import Command.LDrawColor;
import Command.LDrawColorT;
import Command.LDrawColorable;
import Common.Box2;
import Common.Box3;
import Common.Matrix4;
import Common.Size2;
import Common.Vector2f;
import Common.Vector3f;
import Common.Vector4f;
import LDraw.Files.LDrawFile;
import LDraw.Files.LDrawModel;
import LDraw.Support.type.LocationModeT;
import LDraw.Support.type.ProjectionModeT;
import LDraw.Support.type.RotationDrawModeT;
import LDraw.Support.type.ViewOrientationT;
import Notification.ILDrawSubscriber;
import Notification.INotificationMessage;
import Notification.NotificationCenter;
import Notification.NotificationMessageT;
import Renderer.LDrawShaderRenderer;

//==============================================================================
//
//File:		LDrawGLRenderer.h
//
//Purpose:		Draws an LDrawFile with OpenGL.
//
//Modified:	4/17/05 Allen Smith. Creation Date.
//
//==============================================================================
//==============================================================================
//
//File:		LDrawGLRenderer.m
//
//Purpose:		Draws an LDrawFile with OpenGL.
//
//				This class is responsible for all platform-independent logic, 
//				including math and OpenGL operations. It also contains a number 
//				of methods which would be called in response to events; it is 
//				the responsibility of the platform layer to receive and 
//				interpret those events and pass them to us. 
//
//				The "event" type methods here take high-level parameters. For 
//				example, we don't check -- or want to know! -- if the option key 
//				is down. The platform layer figures out stuff like that, and 
//				more importantly, figures out what it *means*. The *meaning* is 
//				what the renderer's methods care about. 
//
//Created by Allen Smith on 4/17/05.
//Copyright 2005. All rights reserved.
//==============================================================================

/**
 * @Class LDrawGLRenderer
 * 
 * @Purpose This is an abstract base class for all elements of an LDraw
 * @Represent LDrawGLRenderer.(h, m) of Bricksmith
 * 
 * @author funface2
 * @since 2014-03-17
 * 
 */
public class LDrawGLRenderer implements LDrawColorable, ILDrawSubscriber {

	public static final float SIMPLIFICATION_THRESHOLD = 0.3f; // seconds

	public static final int HANDLE_SIZE = 3;

	/**
	 * @uml.property name="delegate"
	 * @uml.associationEnd
	 */
	LDrawGLRendererDelegate delegate;
	/**
	 * @uml.property name="scroller"
	 * @uml.associationEnd
	 */
	ILDrawGLCameraScroller scroller;
	/**
	 * @uml.property name="target"
	 */
	Object target;
	/**
	 * @uml.property name="backAction"
	 * @uml.associationEnd
	 */
	SelT backAction;
	/**
	 * @uml.property name="forwardAction"
	 * @uml.associationEnd
	 */
	SelT forwardAction;
	/**
	 * @uml.property name="nudgeAction"
	 * @uml.associationEnd
	 */
	SelT nudgeAction;
	/**
	 * @uml.property name="allowsEditing"
	 */
	boolean allowsEditing;

	/**
	 * @uml.property name="fileBeingDrawn"
	 * @uml.associationEnd
	 */
	LDrawDirective fileBeingDrawn; // Should only be an LDrawFile or LDrawModel.
	// if you want to do anything else, you must
	// tweak the selection code in LDrawDrawableElement
	// and here in -mouseUp: to handle such cases.

	/**
	 * @uml.property name="camera"
	 * @uml.associationEnd
	 */
	LDrawGLCamera camera;

	// Drawing Environment
	/**
	 * @uml.property name="color"
	 * @uml.associationEnd
	 */
	LDrawColor color;// default color to draw parts if none is specified
	/**
	 * @uml.property name="glBackgroundColor" multiplicity="(0 -1)"
	 *               dimension="1"
	 */
	float glBackgroundColor[] = new float[4];
	/**
	 * @uml.property name="selectionMarquee"
	 * @uml.associationEnd
	 */
	Box2 selectionMarquee;// in view coordinates. ZeroBox2 means no marquee.
	/**
	 * @uml.property name="rotationDrawMode"
	 * @uml.associationEnd
	 */
	RotationDrawModeT rotationDrawMode; // // drawing detail while rotating.
	/**
	 * @uml.property name="viewOrientation"
	 * @uml.associationEnd
	 */
	ViewOrientationT viewOrientation;// our orientation
	/**
	 * @uml.property name="fpsStartTime"
	 */
	Date fpsStartTime = Calendar.getInstance().getTime();
	/**
	 * @uml.property name="framesSinceStartTime"
	 */
	int framesSinceStartTime;

	// Event Tracking
	/**
	 * @uml.property name="gridSpacing"
	 */
	float gridSpacing;
	/**
	 * @uml.property name="isGesturing"
	 */
	boolean isGesturing; // true if performing a multitouch trackpad gesture.
	/**
	 * @uml.property name="isTrackingDrag"
	 */
	boolean isTrackingDrag; // true if the last mousedown was followed by a
							// drag, and we're tracking it (drag-and-drop
							// doesn't count)
	/**
	 * @uml.property name="isStartingDrag"
	 */
	boolean isStartingDrag; // this is the first event in a drag
	/**
	 * @uml.property name="mouseDownTimer"
	 */
	Timer mouseDownTimer; // countdown to beginning drag-and-drop
	/**
	 * @uml.property name="canBeginDragAndDrop"
	 */
	boolean canBeginDragAndDrop; // the next mouse-dragged will initiate a
									// drag-and-drop.
	/**
	 * @uml.property name="didPartSelection"
	 */
	boolean didPartSelection; // tried part selection during this click
	/**
	 * @uml.property name="dragEndedInOurDocument"
	 */
	boolean dragEndedInOurDocument; // YES if the drag we initiated ended in the
									// document we display
	/**
	 * @uml.property name="draggingOffset"
	 * @uml.associationEnd
	 */
	Vector3f draggingOffset; // displacement between part 0's position and the
								// initial click point of the drag
	/**
	 * @uml.property name="initialDragLocation"
	 * @uml.associationEnd readOnly="true"
	 */
	Vector3f initialDragLocation; // point in model where part was positioned at
									// draggingEntered
	/**
	 * @uml.property name="nudgeVector"
	 * @uml.associationEnd readOnly="true"
	 */
	Vector3f nudgeVector; // direction of nudge action (valid only in
							// nudgeAction callback)
	/**
	 * @uml.property name="activeDragHandle"
	 * @uml.associationEnd readOnly="true"
	 */
	LDrawDragHandle activeDragHandle; // drag handle hit on last mouse-down (or

	private boolean isReadyToUse = false;

	// nil)

	/**
	 * @uml.property name="gl2"
	 * @uml.associationEnd readOnly="true"
	 */

	// ========== init
	// ==============================================================
	//
	// Purpose: Initialize the object.
	//
	// ==============================================================================
	public LDrawGLRenderer initWithBounds(Size2 boundsIn) {
		// self = [super init];

		// ---------- Initialize instance variables
		// ---------------------------------

		ColorLibrary colorLibrary = (ColorLibrary) ColorLibrary
				.sharedColorLibrary();
		setLDrawColor(colorLibrary.colorForCode(LDrawColorT.LDrawCurrentColor));

		camera = new LDrawGLCamera();

		isTrackingDrag = false;
		selectionMarquee = Box2.getZeroBox2();
		rotationDrawMode = RotationDrawModeT.LDrawGLDrawNormal;
		gridSpacing = 20.0f;

		setViewOrientation(ViewOrientationT.ViewOrientation3D);

		return this;

	}// end initWithFrame:

	public LDrawGLRenderer initWithBoundsCamera(Size2 boundsIn,
			LDrawGLCamera ldrawCamera) {
		isReadyToUse = true;
		// self = [super init];

		// ---------- Initialize instance variables
		// ---------------------------------

		ColorLibrary colorLibrary = (ColorLibrary) ColorLibrary
				.sharedColorLibrary();
		setLDrawColor(colorLibrary.colorForCode(LDrawColorT.LDrawCurrentColor));

		camera = ldrawCamera;

		isTrackingDrag = false;
		selectionMarquee = Box2.getZeroBox2();
		rotationDrawMode = RotationDrawModeT.LDrawGLDrawNormal;
		gridSpacing = 20.0f;

		setViewOrientation(ViewOrientationT.ViewOrientation3D);

		return this;

	}// end initWithFrame:

	// ========== prepareOpenGL
	// =====================================================
	//
	// Purpose: The context is all set up; this is where we prepare our OpenGL
	// state.
	//
	// ==============================================================================

	public void prepareOpenGL(GL2 gl2) {

		gl2.glEnable(GL2.GL_DEPTH_TEST);
		gl2.glEnable(GL2.GL_BLEND);
		gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl2.glEnable(GL2.GL_MULTISAMPLE); // antialiasing

		gl2.glEnable(GL2.GL_TEXTURE_2D);
		gl2.glEnable(GL2.GL_TEXTURE_GEN_S);
		gl2.glEnable(GL2.GL_TEXTURE_GEN_T);

		// This represents the "default" GL state, at least until we change that
		// policy.
		gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glEnableClientState(GL2.GL_NORMAL_ARRAY);
		gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);

		setBackgroundColor(gl2, 1.0f, 1.0f, 1.0f); // white

		//
		// Define the lighting.
		//

		// Our light position is transformed by the modelview matrix. That means
		// we need to have a standard model matrix loaded to get our light to
		// land in the right place! But our modelview might have already been
		// affected by someone calling -setViewOrientation:. So we restore the
		// default here.
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();
		gl2.glRotatef(180, 1, 0, 0); // convert to standard, upside-down LDraw
										// orientation.

		// ---------- Material
		// ------------------------------------------------------

		// GLfloat ambient[4] = { 0.2, 0.2, 0.2, 1.0 };
		// GLfloat diffuse[4] = { 0.5, 0.5, 0.5, 1.0 };
		// float specular[] = { 0.0f, 0.0f, 0.0f, 1f };
		// float shininess = 64.0f; // range [0-128]

		// glMaterialfv( GL_FRONT_AND_BACK, GL_AMBIENT, ambient );
		// glMaterialfv( GL_FRONT_AND_BACK, GL_DIFFUSE, diffuse ); //don't
		// bother; overridden by glColorMaterial
		// FloatBuffer specularFB = FloatBuffer.wrap(specular);
		// gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, specularFB);
		// gl2.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, shininess);

		// glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE); // this
		// is the default anyway

		gl2.glShadeModel(GL2.GL_SMOOTH);
		gl2.glEnable(GL2.GL_NORMALIZE);
		gl2.glEnable(GL2.GL_COLOR_MATERIAL);

		// ---------- Light Model
		// ---------------------------------------------------

		// The overall scene has ambient light to make the lighting less harsh.
		// But
		// too much ambient light makes everything washed out.
		float lightModelAmbient[] = { 0.3f, 0.3f, 0.3f, 0.0f };

		gl2.glLightModelf(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_FALSE);
		gl2.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT,
				FloatBuffer.wrap(lightModelAmbient));

		// ---------- Lights
		// --------------------------------------------------------

		// We are going to have two lights, one in a standard position (LIGHT0)
		// and
		// another pointing opposite to it (LIGHT1). The second light will
		// illuminate any inverted normals or backwards polygons.
		float position0[] = { 0, -0.0f, -1.0f, 0 };
		float position1[] = { 0, 0.0f, 1.0f, 0 };

		// Lessening the diffuseness also makes lighting less extreme.
		float light0Ambient[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		float light0Diffuse[] = { 0.8f, 0.8f, 0.8f, 1.0f };
		float light0Specular[] = { 0.0f, 0.0f, 0.0f, 1.0f };

		// normal forward light
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION,
				FloatBuffer.wrap(position0));
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT,
				FloatBuffer.wrap(light0Ambient));
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE,
				FloatBuffer.wrap(light0Diffuse));
		gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR,
				FloatBuffer.wrap(light0Specular));

		gl2.glLightf(GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, 1.0f);
		gl2.glLightf(GL2.GL_LIGHT0, GL2.GL_LINEAR_ATTENUATION, 0.0f);
		gl2.glLightf(GL2.GL_LIGHT0, GL2.GL_QUADRATIC_ATTENUATION, 0.0f);

		// opposing light to illuminate backward normals.
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION,
				FloatBuffer.wrap(position1));
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT,
				FloatBuffer.wrap(light0Ambient));
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE,
				FloatBuffer.wrap(light0Diffuse));
		gl2.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR,
				FloatBuffer.wrap(light0Specular));

		gl2.glLightf(GL2.GL_LIGHT1, GL2.GL_CONSTANT_ATTENUATION, 1.0f);
		gl2.glLightf(GL2.GL_LIGHT1, GL2.GL_LINEAR_ATTENUATION, 0.0f);
		gl2.glLightf(GL2.GL_LIGHT1, GL2.GL_QUADRATIC_ATTENUATION, 0.0f);

		gl2.glEnable(GL2.GL_LIGHTING);
		gl2.glEnable(GL2.GL_LIGHT0);
		gl2.glEnable(GL2.GL_LIGHT1);

		// Now that the light is positioned where we want it, we can restore the
		// correct viewing angle.
		setViewOrientation(viewOrientation);
	}

	// ========== draw
	// ==============================================================
	//
	// Purpose: Draw the LDraw content of the view.
	//
	// Notes: This method is, in theory at least, as thread-safe as Apple's
	// OpenGL implementation is. Which is to say, not very much.
	//
	// ==============================================================================
	public LDrawShaderRenderer ren;

	private boolean useWireFrame;

	public void draw(GL2 gl2) {
		// Make lines look a little nicer; Max width 1.0; 0.5 at 100% zoom
		gl2.glLineWidth((float) Math
				.min(zoomPercentageForGL() / 100 * 0.5, 1.0));

		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadMatrixf(camera.getProjection(), 0);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadMatrixf(camera.getModelView(), 0);
		if (ren == null) {
			ren = new LDrawShaderRenderer();
			ren.useWireFrame = this.useWireFrame;
		}
		ren.initWithScale(gl2, zoomPercentageForGL() / 100.0f,
				camera.getModelView(), camera.getProjection());

		fileBeingDrawn.drawSelf(gl2, ren);

		if (delegate != null)
			delegate.LDrawGLRendererNeedsFlush(this);
	}

	private float zoomPercentageForGL() {
		if (locationMode() == LocationModeT.LocationModeWalkthrough)
			return 100.0f;
		return camera.zoomPercentage();

	}// end draw:to

	// ========== isFlipped
	// =========================================================
	//
	// Purpose: This lets us appear in the upper-left of scroll views
	// rather
	// than the bottom. The view should draw just fine whether or not
	// it is flipped, though.
	//
	// ==============================================================================
	public boolean isFlipped() {
		return true;

	}// end isFlipped

	// ========== isOpaque
	// ==========================================================
	//
	// Note: Our content completely covers this view. (This is just here
	// as a
	// reminder; NSOpenGLViews are opaque by default.)
	//
	// ==============================================================================
	public boolean isOpaque() {
		return true;
	}

	// ========== activeDragHandle
	// ==================================================
	//
	// Purpose: Returns a drag handle if we are currently locked into a
	// drag-handle drag. Otherwise returns nil.
	//
	// ==============================================================================
	public LDrawDragHandle activeDragHandle() {
		return activeDragHandle;
	}

	// ========== centerPoint
	// =======================================================
	//
	// Purpose: Returns the point (in frame coordinates) which is
	// currently
	// at the center of the visible rectangle. This is useful for
	// determining the point being viewed in the scroll view.
	//
	// ==============================================================================
	public Vector2f centerPoint() {
		return MatrixMath.V2Make(
				MatrixMath.V2BoxMidX(scroller.getVisibleRect()),
				MatrixMath.V2BoxMidY(scroller.getVisibleRect()));

	}// end centerPoint

	// ========== didPartSelection
	// ==================================================
	//
	// Purpose: Returns whether the most-recent mouseDown resulted in a
	// part-selection attempt. This is only valid when called during a
	// mouse click.
	//
	// ==============================================================================
	public boolean didPartSelection() {
		return didPartSelection;
	}

	// ========== getInverseMatrix
	// ==================================================
	//
	// Purpose: Returns the inverse of the current modelview matrix. You
	// can
	// multiply points by this matrix to convert screen locations (or
	// vectors) to model points.
	//
	// Note: This function filters out the translation which is caused by
	// "moving" the camera with gluLookAt. That allows us to continue
	// working with the model as if it's positioned at the origin,
	// which means that points we generate with this matrix will
	// correspond to points in the LDraw model itself.
	//
	// ==============================================================================
	public Matrix4 getInverseMatrix() {
		Matrix4 transformation = MatrixMath.Matrix4CreateFromGLMatrix4(camera
				.getModelView());
		Matrix4 inversed = MatrixMath.Matrix4Invert(transformation);

		return inversed;

	}// end getInverseMatrix
		//
		//
		// ========== getMatrix
		// =========================================================

	//
	// Purpose: Returns the the current modelview matrix, basically.
	//
	// Note: This function filters out the translation which is caused by
	// "moving" the camera with gluLookAt. That allows us to continue
	// working with the model as if it's positioned at the origin,
	// which means that points we generate with this matrix will
	// correspond to points in the LDraw model itself.
	//
	// ==============================================================================

	public Matrix4 getMatrix() {
		return MatrixMath.Matrix4CreateFromGLMatrix4(camera.getModelView());

	}// end getMatrix

	// ========== isTrackingDrag
	// ====================================================
	//
	// Purpose: Returns YES if a mouse-drag is currently in progress.
	//
	// ==============================================================================
	/**
	 * @return
	 * @uml.property name="isTrackingDrag"
	 */
	public boolean isTrackingDrag() {
		return isTrackingDrag;
	}

	//

	// ========== LDrawColor
	// ========================================================
	//
	// Purpose: Returns the LDraw color code of the receiver.
	//
	// ==============================================================================
	public LDrawColor getLDrawColor() {
		return color;

	}// end color

	//
	// ========== LDrawDirective
	// ====================================================
	//
	// Purpose: Returns the file or model being drawn by this view.
	//
	// ==============================================================================
	public LDrawDirective getLDrawDirective() {
		return fileBeingDrawn;

	}// end LDrawDirective

	// ========== nudgeVector
	// =======================================================
	//
	// Purpose: Returns the direction of a keyboard part nudge. The
	// target of
	// our nudgeAction queries this method to find how to nudge the
	// selection.
	//
	// Notes: This value is only valid during the nudgeAction callback.
	//
	// ==============================================================================
	public Vector3f nudgeVector() {
		return nudgeVector;

	}// end nudgeVector

	// ========== projectionMode
	// ====================================================
	//
	// Purpose: Returns the current projection mode (perspective or
	// orthographic) used in the view.
	//
	// ==============================================================================
	public ProjectionModeT projectionMode() {
		return camera.projectionMode();

	}// end projectionMode

	// ========== locationMode
	// ====================================================
	//
	// Purpose: Returns the current location mode (model or walkthrough).
	//
	// ==============================================================================
	public LocationModeT locationMode() {
		return camera.locationMode();

	}// end locationMode

	// ========== selectionMarquee
	// ==================================================
	// ==============================================================================
	public Box2 selectionMarquee() {
		return selectionMarquee;
	}

	// ========== viewingAngle
	// ======================================================
	//
	// Purpose: Returns the modelview rotation, in degrees.
	//
	// Notes: These numbers do *not* include the fact that LDraw has an
	// upside-down coordinate system. So if this method returns
	// (0,0,0), that means "Front, looking right-side up."
	//
	// ==============================================================================
	public Vector3f viewingAngle() {
		return camera.viewingAngle();
	}// end viewingAngle

	// ========== viewOrientation
	// ===================================================
	//
	// Purpose: Returns the current camera orientation for this view.
	//
	// ==============================================================================
	public ViewOrientationT viewOrientation() {
		return viewOrientation;
	}// end viewOrientation

	// ========== viewport
	// ==========================================================
	//
	// Purpose: Returns the viewport. Origin is the lower-left.
	//
	// ==============================================================================
	public Box2 getViewport() {
		Box2 viewport = Box2.getZeroBox2();
		viewport.size = scroller.getMaxVisibleSizeGL();
		return viewport;
	}

	// ========== zoomPercentage
	// ====================================================
	//
	// Purpose: Returns the percentage magnification being applied to the
	// receiver. (200 means 2x magnification.) This is the 'nominal'
	// zoom the user sees - it should be used by UI and tool code.
	//
	// ==============================================================================
	public float zoomPercentage() {
		return camera.zoomPercentage();

	}// end zoomPercentage

	// ========== zoomPercentage
	// ====================================================
	//
	// Purpose: Returns the percentage magnification being applied to
	// drawing;
	// this represents the scale from GL viewport coordiantes (which
	// are always window manager pixels) to NS document coordinates
	// (which DO get scaled).
	//
	// Use this routine to convert between NS view and GL viewport
	// coordinates.
	//
	// Notes: When walk-through is engaged, zoom controls the camera FOV
	// but
	// leaves the document untouched at window size. So this routine
	// checks the camera mode and just returns 100.0.
	//
	// ==============================================================================
	public float getZoomPercentageForGL() {
		if (locationMode() == LocationModeT.LocationModeWalkthrough)
			return 100.0f;
		return camera.zoomPercentage();

	}// end zoomPercentageForGL

	// ========== setAllowsEditing:
	// =================================================
	//
	// Purpose: Sets whether the renderer supports part selection and
	// dragging.
	//
	// Notes: Querying a delegate isn't sufficient.
	//
	// ==============================================================================
	/**
	 * @param flag
	 * @uml.property name="allowsEditing"
	 */
	public void setAllowsEditing(boolean flag) {
		allowsEditing = flag;
	}

	// ========== setDelegate:
	// ======================================================
	//
	// Purpose: Sets the object that acts as the delegate for the
	// receiver.
	//
	// This object relies on the the delegate to interface with the
	// window manager to do things like scrolling.
	//
	// ==============================================================================
	/**
	 * @param object
	 * @param newScroller
	 * @uml.property name="delegate"
	 */
	public void setDelegate(LDrawGLRendererDelegate object,
			ILDrawGLCameraScroller newScroller) {
		// weak link.
		delegate = object;
		scroller = newScroller;
		camera.setScroller(newScroller);
	}// end setDelegate:

	// ========== setBackAction:
	// ====================================================
	//
	// Purpose: Sets the method called on the target when a backward
	// swipe is
	// received.
	//
	// ==============================================================================
	/**
	 * @param newAction
	 * @uml.property name="backAction"
	 */
	public void setBackAction(SelT newAction) {
		backAction = newAction;
	}// end setBackAction:

	// ========== setBackgroundColorRed:green:blue:
	// =================================
	//
	// Purpose: Sets the canvas background color.
	//
	// ==============================================================================
	public void setBackgroundColor(GL2 gl2, float red, float green, float blue) {

		glBackgroundColor[0] = red;
		glBackgroundColor[1] = green;
		glBackgroundColor[2] = blue;
		glBackgroundColor[3] = 1.0f;

		gl2.glClearColor(glBackgroundColor[0], glBackgroundColor[1],
				glBackgroundColor[2], glBackgroundColor[3]);

		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);
	}

	// ========== setDragEndedInOurDocument:
	// ========================================
	//
	// Purpose: When a dragging operation we initiated ends outside the
	// originating document, we need to know about it so that we can
	// tell the document to completely delete the directives it started
	// dragging. (They are merely hidden during the drag.) However,
	// each document can be represented by multiple views, so it is
	// insufficient to simply test whether the drag ended within this
	// view.
	//
	// So, when a drag ends in any LDrawGLView, it inspects the
	// dragging source to see if it represents the same document. If it
	// does, it sends the source this message. If this message hasn't
	// been received by the time the drag ends, this view will
	// automatically instruct its document to purge the source
	// directives, since the directives were actually dragged out of
	// their document.
	//
	// ==============================================================================
	/**
	 * @param flag
	 * @uml.property name="dragEndedInOurDocument"
	 */
	public void setDragEndedInOurDocument(boolean flag) {
		dragEndedInOurDocument = flag;

	}// end setDragEndedInOurDocument:

	// ========== setDraggingOffset:
	// ================================================
	//
	// Purpose: Sets the offset to apply to the first drag-and-drop
	// part's
	// position. This is used when initiating drag-and-drop while
	// clicking on a point other than the exact center of the part. We
	// want to maintain the clicked point under the cursor, but it is
	// internally easier to move the part's centerpoint. This offset
	// allows us to translate between the two.
	//
	// ==============================================================================
	/**
	 * @param offsetIn
	 * @uml.property name="draggingOffset"
	 */
	public void setDraggingOffset(Vector3f offsetIn) {
		draggingOffset = offsetIn;
	}

	// ========== setForwardAction:
	// =================================================
	//
	// Purpose: Sets the method called on the target when a forward swipe
	// is
	// received.
	//
	// ==============================================================================
	/**
	 * @param newAction
	 * @uml.property name="forwardAction"
	 */
	public void setForwardAction(SelT newAction) {
		forwardAction = newAction;
	}// end setForwardAction:

	// ========== setGridSpacing:
	// ===================================================
	//
	// Purpose: Sets the grid amount by which things are dragged.
	//
	// ==============================================================================
	/**
	 * @param newValue
	 * @uml.property name="gridSpacing"
	 */
	public void setGridSpacing(float newValue) {
		gridSpacing = newValue;
	}

	// ========== setLDrawColor:
	// ====================================================
	//
	// Purpose: Sets the base color for parts drawn by this view which
	// have
	// no
	// color themselves.
	//
	// ==============================================================================
	public void setLDrawColor(LDrawColor newColor) {
		// newColor.retain();
		color = newColor;

		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);

	}// end setColor

	// ========== LDrawDirective:
	// ===================================================
	//
	// Purpose: Sets the file being drawn in this view.
	//
	// We also do other housekeeping here associated with tracking the
	// model. We also automatically center the model in the view.
	//
	// ==============================================================================
	public void setLDrawDirective(LDrawDirective newFile) {
		boolean virginView = (fileBeingDrawn == null);
		Box3 bounds = Box3.getInvalidBox();

		// Update our variable.
		fileBeingDrawn = newFile;

		if (newFile != null)
			bounds = newFile.boundingBox3();
		if (camera != null)
			camera.setModelSize(bounds);
		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);

		if (virginView == true) {
			scrollModelPoint(Vector3f.getZeroVector3f(),
					MatrixMath.V2Make(0.5f, 0.5f));
		}

		// Register for important notifications.
		NotificationCenter notificationCenter = NotificationCenter
				.getInstance();
		notificationCenter.removeSubscriber(this,
				NotificationMessageT.LDrawDirectiveDidChange);
		notificationCenter.removeSubscriber(this,
				NotificationMessageT.LDrawFileActiveModelDidChange);
		notificationCenter.removeSubscriber(this,
				NotificationMessageT.LDrawModelRotationCenterDidChange);

		notificationCenter.addSubscriber(this,
				NotificationMessageT.LDrawDirectiveDidChange);
		notificationCenter.addSubscriber(this,
				NotificationMessageT.LDrawFileActiveModelDidChange);
		notificationCenter.addSubscriber(this,
				NotificationMessageT.LDrawModelRotationCenterDidChange);

		updateRotationCenter();
	}

	// ========== setMaximumVisibleSize:
	// ============================================
	//
	// Purpose: Sets the largest size (in frame coordinates) to which the
	// visible rect should be permitted to grow.
	//
	// ==============================================================================
	public void setMaximumVisibleSize(Size2 size) {
		camera.tickle();
		delegate.LDrawGLRendererNeedsRedisplay(this);
	}

	// ========== setNudgeAction:
	// ===================================================
	//
	// Purpose: Sets the action sent when the GLView wants to nudge a
	// part.
	//
	// You get the nudge vector by calling -nudgeVector within the body
	// of the action method.
	//
	// ==============================================================================
	/**
	 * @param newAction
	 * @uml.property name="nudgeAction"
	 */
	public void setNudgeAction(SelT newAction) {
		nudgeAction = newAction;
	}// end setNudgeAction:

	// ========== setProjectionMode:
	// ================================================
	//
	// Purpose: Sets the projection used when drawing the receiver:
	// - orthographic is like a Mercator map; it distorts deeper
	// objects.
	// - perspective draws deeper objects toward a vanishing point;
	// this is how humans see the world.
	//
	// ==============================================================================
	public void setProjectionMode(ProjectionModeT newProjectionMode) {
		camera.setProjectionMode(newProjectionMode);

		delegate.LDrawGLRendererNeedsRedisplay(this);

	} // end setProjectionMode:

	// ========== setLocationMode:
	// ================================================
	//
	// Purpose: Sets the location mode used when drawing the receiver.
	// - model points the camera at the model center from a distance.
	// - walk-through puts the camera _on_ the model center.
	//
	// ==============================================================================
	public void setLocationMode(LocationModeT newLocationMode) {
		camera.setLocationMode(newLocationMode);

		delegate.LDrawGLRendererNeedsRedisplay(this);
	} // end setLocationMode:

	// ========== setSelectionMarquee:
	// ==============================================
	//
	// Purpose: The box (in view coordinates) in which to draw the
	// selection
	// marquee.
	//
	// ==============================================================================
	/**
	 * @param newBox_view
	 * @uml.property name="selectionMarquee"
	 */
	public void setSelectionMarquee(Box2 newBox_view) {
		selectionMarquee = newBox_view;
	}

	// ========== setTarget:
	// ========================================================
	//
	// Purpose: Sets the object which is the receiver of this view's
	// action
	// methods.
	//
	// ==============================================================================
	/**
	 * @param newTarget
	 * @uml.property name="target"
	 */
	public void setTarget(Object newTarget) {
		target = newTarget;

	}// end setTarget:

	// ========== setViewingAngle:
	// ==================================================
	//
	// Purpose: Sets the modelview rotation, in degrees. The angle is
	// // applied
	// in
	// x-y-z order.
	//
	// Notes: These numbers do *not* include the fact that LDraw has an
	// upside-down coordinate system. So if this method returns
	// (0,0,0), that means "Front, looking right-side up."
	//
	// ==============================================================================
	public void setViewingAngle(Vector3f newAngle) {
		camera.setViewingAngle(newAngle);
		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);
	} // end setViewingAngle:

	// ========== setViewOrientation:
	// ===============================================
	//
	// Purpose: Changes the camera position from which we view the model.
	// i.e., ViewOrientationFront means we see the model head-on.
	//
	// ==============================================================================
	/**
	 * @param newOrientation
	 * @uml.property name="viewOrientation"
	 */
	public void setViewOrientation(ViewOrientationT newOrientation) {
		Vector3f newAngle = LDrawUtilities
				.angleForViewOrientation(newOrientation);

		viewOrientation = newOrientation;

		// Apply the angle itself.
		setViewingAngle(newAngle);
		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);

	}// end setViewOrientation:

	// ========== setZoomPercentage:
	// ================================================
	//
	// Purpose: Enlarges (or reduces) the magnification on this view. The
	// center
	// point of the original magnification remains the center point of
	// the new magnification. Does absolutely nothing if this view
	// isn't contained within a scroll view.
	//
	// Parameters: newPercentage: new zoom; pass 100 for 100%, etc.
	// Automatically
	// constrained to a minimum of 1%.
	//
	// ==============================================================================
	public void setZoomPercentage(float newPercentage) {
		camera.setZoomPercentage(newPercentage);
	}

	// ========== moveCamera:
	// =======================================================
	//
	// Purpose: Moves the camera's rotation center by a fixed offset.
	// Used to
	// walk around the walk-through camera, or to change the model's
	// center of rotation for the model camera.
	//
	// ==============================================================================
	public void moveCamera(Vector3f delta) {
		camera.setRotationCenter(MatrixMath.V3Add(camera.rotationCenter(),
				delta));
		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);
	}// end moveCamera

	// ========== zoomIn:
	// ===========================================================
	//
	// Purpose: Enlarge the scale of the current LDraw view.
	//
	// ==============================================================================
	public void zoomIn(Object sender) {
		float currentZoom = zoomPercentage();
		float newZoom = currentZoom * 2;

		setZoomPercentage(newZoom);
	}// end zoomIn:

	// ========== zoomOut:
	// ==========================================================
	//
	// Purpose: Shrink the scale of the current LDraw view.
	//
	// ==============================================================================
	public void zoomOut(Object sender) {
		float currentZoom = zoomPercentage();
		float newZoom = currentZoom / 2;

		setZoomPercentage(newZoom);
	}// end zoomOut:

	// ========== zoomToFit:
	// ========================================================
	//
	// Purpose: Enlarge or shrink the zoom and scroll the model such that
	// its
	// image perfectly fills the visible area of the view
	//
	// ==============================================================================
	// public void zoomToFit(Object sender)
	// {
	// Size2 maxContentSize = Size2.getZeroSize2();
	// Box3 boundingBox = Box3.getInvalidBox();
	// Point3 center = Point3.getZeroPoint3();
	// Matrix4 modelView = Matrix4.getIdentityMatrix4();
	// Matrix4 projection = Matrix4.getIdentityMatrix4();
	// Box2 viewport = Box2.getZeroBox2();
	// Box3 projectedBounds = Box3.getInvalidBox();
	// Box2 projectionRect = Box2.getZeroBox2();
	// Size2 zoomScale2D = Size2.getZeroSize2();
	// float zoomScaleFactor = 0.0f;
	//
	// // How many onscreen pixels do we have to work with?
	// maxContentSize.setWidth(MatrixMath.V2BoxWidth(scroller.getVisibleRect())
	// * zoomPercentage()/100.0);
	// maxContentSize.setHeight(MatrixMath.V2BoxHeight(scroller.getVisibleRect())
	// *zoomPercentage()/100.0);
	// // NSLog(@"windowVisibleRect = %@",
	// //NSStringFromRect(windowVisibleRect));
	// // NSLog(@"maxContentSize = %@", NSStringFromSize(maxContentSize));
	//
	// //�좎떎�몄삕 �좎뙏�듭삕�좎룞��.. �좎룞�쇿뜝�쒕벝��李썲뜝�숈삕�좎룞���좎룞�쇿뜝�숈삕 �좎룞���좎떍�몄삕 泥섇뜝�숈삕
	// �좎떦�붾벝���좎떦�붾벝��..
	// // Get bounds
	// if(fileBeingDrawn.respondsToSelector(boundingBox3))
	// {
	// boundingBox = [(id)self->fileBeingDrawn boundingBox3];
	// if(V3EqualBoxes(boundingBox, InvalidBox) == NO)
	// {
	// // Project the bounds onto the 2D "canvas"
	// modelView = Matrix4CreateFromGLMatrix4([camera getModelView]);
	// projection = Matrix4CreateFromGLMatrix4([camera getProjection]);
	// viewport = [self viewport];
	//
	// projectedBounds = [(id)self->fileBeingDrawn
	// projectedBoundingBoxWithModelView:modelView
	// projection:projection
	// view:viewport ];
	// projectionRect = V2MakeBox(projectedBounds.min.x,
	// projectedBounds.min.y,
	// // origin
	// projectedBounds.max.x - projectedBounds.min.x, // width
	// projectedBounds.max.y - projectedBounds.min.y); // height
	//
	//
	// //---------- Find zoom scale -----------------------------------
	// // Completely fill the viewport with the image
	//
	// zoomScale2D.width = maxContentSize.width /
	// V2BoxWidth(projectionRect);
	// zoomScale2D.height = maxContentSize.height /
	// V2BoxHeight(projectionRect);
	//
	// zoomScaleFactor = MIN(zoomScale2D.width, zoomScale2D.height);
	//
	//
	// //---------- Find visual center point --------------------------
	// // One might think this would be V3CenterOfBox(bounds). But it's
	// // not. It seems perspective distortion can cause the visual
	// // center of the model to be someplace else.
	//
	// Point2 graphicalCenter_viewport = V2Make( V2BoxMidX(projectionRect),
	// V2BoxMidY(projectionRect) );
	// Point2 graphicalCenter_view = [self
	// convertPointFromViewport:graphicalCenter_viewport];
	// Point3 graphicalCenter_model = ZeroPoint3;
	//
	// graphicalCenter_model = [self modelPointForPoint:graphicalCenter_view
	// depthReferencePoint:center];
	//
	//
	// //---------- Zoom to Fit! --------------------------------------
	//
	// [self setZoomPercentage:([self zoomPercentage] * zoomScaleFactor)];
	// [self scrollCenterToModelPoint:graphicalCenter_model];
	// }
	// }
	//
	// }//end zoomToFit:
	//
	//
	//
	// #pragma mark -
	// #pragma mark EVENTS
	// #pragma mark -
	//
	// //========== mouseMoved:
	// =======================================================
	// //
	// // Purpose: Mouse has moved to the given view point. (This method is
	// // optional.)
	// //
	// //==============================================================================
	// - (void) mouseMoved:(Point2)point_view
	// {
	// [self publishMouseOverPoint:point_view];
	// }
	//
	//
	// //========== mouseDown
	// =========================================================
	// //
	// // Purpose: Signals that a mouse-down has been received; clear
	// various
	// state
	// // flags in preparation for selection or dragging.
	// //
	// // Note: Our platform view is responsible for correct interpretation
	// of
	// // the event and routing it to the appropriate methods in the
	// // renderer class.
	// //
	// //==============================================================================
	// - (void) mouseDown
	// {
	// // Reset event tracking flags.
	// self->isTrackingDrag = NO;
	// self->didPartSelection = NO;
	//
	// // This might be the start of a new drag; start collecting frames per
	// second
	// fpsStartTime = [NSDate timeIntervalSinceReferenceDate];
	// framesSinceStartTime = 0;
	//
	// [self->delegate markPreviousSelection:self];
	// }
	//
	//
	// //========== mousedDragged
	// =====================================================
	// //
	// // Purpose: Signals that a mouse-drag has been received; clear
	// various
	// state
	// // flags in preparation for selection or dragging.
	// //
	// // Note: Our platform view is responsible for correct interpretation
	// of
	// // the event and routing it to the appropriate methods in the
	// // renderer class.
	// //
	// //==============================================================================
	// - (void) mouseDragged
	// {
	// self->isStartingDrag = (self->isTrackingDrag == NO); // first drag if
	// none to date
	// self->isTrackingDrag = YES;
	// }
	//
	//
	// //========== mouseUp
	// ===========================================================
	// //
	// // Purpose: Signals that a mouse-up has been received; clear various
	// state
	// // flags in preparation for selection or dragging.
	// //
	// // Note: Our platform view is responsible for correct interpretation
	// of
	// // the event and routing it to the appropriate methods in the
	// // renderer class.
	// //
	// //==============================================================================
	// - (void) mouseUp
	// {
	// // Redraw from our dragging operations, if necessary.
	// if( (self->isTrackingDrag == YES && rotationDrawMode ==
	// LDrawGLDrawExtremelyFast)
	// || V2BoxWidth(self->selectionMarquee) ||
	// V2BoxHeight(self->selectionMarquee) )
	// {
	// [self->delegate LDrawGLRendererNeedsRedisplay:self];
	// }
	//
	// self->activeDragHandle = nil;
	// self->isTrackingDrag = NO; //not anymore.
	// self->selectionMarquee = ZeroBox2;
	//
	// [self->delegate unmarkPreviousSelection:self];
	// }
	//
	//
	// #pragma mark - Clicking
	//
	// //========== mouseCenterClick:
	// =================================================
	// //
	// // Purpose: We have received a mouseDown event which is intended to
	// center
	// // our view on the point clicked.
	// //
	// //==============================================================================
	// - (void) mouseCenterClick:(Point2)viewClickedPoint
	// {
	// // Ben says: this function used to have a special case for
	// ortho-viewing.
	// // But since perspective-case code is fully general, we just now use
	// it
	// alway.
	//
	//
	// // Perspective distortion makes this more complicated. The camera is
	// in
	// // a fixed position, but the frustum changes with the scrollbars.
	// // We need to calculate the world point we just clicked on, then
	// derive
	// // a new frustum projection centered on that point.
	// Point3 clickedPointInModel = ZeroPoint3;
	//
	// // Find the point we clicked on. It would be more accurate to use
	// // -getDirectivesUnderMouse:::, but it has to actually draw parts,
	// which
	// // can be slow.
	// clickedPointInModel = [self modelPointForPoint:viewClickedPoint];
	//
	// [self scrollCenterToModelPoint:clickedPointInModel];
	//
	// }//end mouseCenterClick:
	//
	//
	// //========== mouseSelectionClick:extendSelection:
	// ==============================
	// //
	// // Purpose: Time to see if we should select something in the model.
	// We
	// // search the model geometry for intersection with the click point.
	// // Our delegate is responsible for managing the actual selection.
	// //
	// // This function returns whether it hit something - calling code can
	// // then do a part drag or marquee based on whether the user clicked
	// // on a part or on empty space.
	// //
	// //==============================================================================
	// - (BOOL) mouseSelectionClick:(Point2)point_view
	// selectionMode:(SelectionModeT)selectionMode
	// {
	// LDrawDirective *clickedDirective = nil;
	//
	// self->selectionMarquee = V2MakeBox(point_view.x, point_view.y, 0, 0);
	//
	// // Only try to select if we are actually drawing something, and can
	// actually
	// // select it.
	// if( self->fileBeingDrawn != nil
	// && self->allowsEditing == YES
	// && [self->delegate
	// respondsToSelector:@selector(LDrawGLRenderer:wantsToSelectDirective:byExtendingSelection:)]
	// )
	// {
	// Point2 point_viewport = [self convertPointToViewport:point_view];
	// Point2 bl =
	// V2Make(point_viewport.x-HANDLE_SIZE,point_viewport.y-HANDLE_SIZE);
	// Point2 tr =
	// V2Make(point_viewport.x+HANDLE_SIZE,point_viewport.y+HANDLE_SIZE);
	// GLfloat depth = 1.0;
	//
	// Box2 viewport = [self viewport];
	// // Get view and projection
	// Point2 point_clip = V2Make( (point_viewport.x - viewport.origin.x) *
	// 2.0
	// / V2BoxWidth(viewport) - 1.0,
	// (point_viewport.y - viewport.origin.y) * 2.0 / V2BoxHeight(viewport)
	// -
	// 1.0 );
	//
	// float x1 = (MIN(bl.x,tr.x) - viewport.origin.x) * 2.0 / V2BoxWidth
	// (viewport) - 1.0;
	// float x2 = (MAX(bl.x,tr.x) - viewport.origin.x) * 2.0 / V2BoxWidth
	// (viewport) - 1.0;
	// float y1 = (MIN(bl.y,tr.y) - viewport.origin.x) * 2.0 /
	// V2BoxHeight(viewport) - 1.0;
	// float y2 = (MAX(bl.y,tr.y) - viewport.origin.y) * 2.0 /
	// V2BoxHeight(viewport) - 1.0;
	//
	// Box2 test_box = V2MakeBoxFromPoints( V2Make(x1, y1), V2Make(x2, y2)
	// );
	//
	// Matrix4 mvp = Matrix4Multiply(
	// Matrix4CreateFromGLMatrix4([camera getModelView]),
	// Matrix4CreateFromGLMatrix4([camera getProjection]));
	//
	// id bestObject = nil;
	// [fileBeingDrawn depthTest:point_clip inBox:test_box transform:mvp
	// creditObject:nil bestObject:&bestObject bestDepth:&depth];
	//
	// clickedDirective = bestObject;
	//
	// // Primitive manipulation?
	// if([clickedDirective isKindOfClass:[LDrawDragHandle class]])
	// {
	// self->activeDragHandle = (LDrawDragHandle*)clickedDirective;
	// }
	// else
	// {
	// // Normal selection
	// self->activeDragHandle = nil;
	//
	// // If we end up actually selecting some single thing, the extension
	// happens if we are intersection (option-shift) or extend (shift).
	// BOOL extendSelection = selectionMode == SelectionExtend ||
	// selectionMode
	// == SelectionIntersection;
	//
	// BOOL has_sel_directive = clickedDirective != nil && [clickedDirective
	// isSelected];
	// BOOL has_any_directive = clickedDirective != nil;
	//
	// switch(selectionMode)
	// {
	// case SelectionReplace:
	// // Replacement mode? Select unless we hit an already hit one - we do
	// not
	// "deselect others" on a click.
	// if(!has_sel_directive)
	// [self->delegate LDrawGLRenderer:self
	// wantsToSelectDirective:clickedDirective
	// byExtendingSelection:extendSelection ];
	// break;
	//
	// case SelectionExtend:
	// // Extended selection. If we hit a part, toggle it - if we miss a
	// part,
	// don't do anything, nothing to do.
	// if(has_any_directive)
	// [self->delegate LDrawGLRenderer:self
	// wantsToSelectDirective:clickedDirective
	// byExtendingSelection:extendSelection ];
	// break;
	//
	// case SelectionIntersection:
	// // Intersection. If we hit an unselected directive, do the select to
	// grab
	// it - this will grab it (via option-shift).
	// // Then we copy. If we have no directive, the whole sel clears, which
	// is
	// the correct start for an intersection (since the
	// // marquee is empty).
	// if(!has_sel_directive)
	// [self->delegate LDrawGLRenderer:self
	// wantsToSelectDirective:clickedDirective
	// byExtendingSelection:extendSelection ];
	// break;
	//
	// case SelectionSubtract:
	// // Subtraction. If we have an UNSELECTED directive, we have to grab
	// it.
	// If we have a selected directive we do nothing so
	// // we can option-drag-copy thes el. And if we just miss everything,
	// the
	// subtraction hasn't nuked anything yet...again we do nothing.
	// if(has_any_directive && !has_sel_directive)
	// [self->delegate LDrawGLRenderer:self
	// wantsToSelectDirective:clickedDirective
	// byExtendingSelection:extendSelection ];
	// break;
	// }
	// }
	// }
	//
	// self->didPartSelection = YES;
	//
	// return (clickedDirective == nil) ? NO : YES;
	//
	// }//end mousePartSelection:
	//
	//
	// //========== mouseZoomInClick:
	// =================================================
	// //
	// // Purpose: Depending on the tool mode, we want to zoom in or out. We
	// also
	// // want to center the view on whatever we clicked on.
	// //
	// //==============================================================================
	// - (void) mouseZoomInClick:(Point2)viewClickedPoint
	// {
	// CGFloat currentZoom = [self zoomPercentage];
	// CGFloat newZoom = currentZoom * 2;
	//
	// [self setZoomPercentage:newZoom preservePoint:viewClickedPoint];
	//
	// }//end mouseZoomInClick:
	//
	//
	// //========== mouseZoomOutClick:
	// ================================================
	// //
	// // Purpose: Depending on the tool mode, we want to zoom in or out. We
	// also
	// // want to center the view on whatever we clicked on.
	// //
	// //==============================================================================
	// - (void) mouseZoomOutClick:(Point2)viewClickedPoint
	// {
	// CGFloat currentZoom = [self zoomPercentage];
	// CGFloat newZoom = currentZoom / 2;
	//
	// [self setZoomPercentage:newZoom preservePoint:viewClickedPoint];
	//
	// }//end mouseZoomOutClick:
	//
	//
	// #pragma mark - Dragging
	//
	// //========== dragHandleDragged:
	// ================================================
	// //
	// // Purpose: Move the active drag handle
	// //
	// //==============================================================================
	// - (void) dragHandleDraggedToPoint:(Point2)point_view
	// constrainDragAxis:(BOOL)constrainDragAxis
	// {
	// Point3 modelReferencePoint = [self->activeDragHandle position];
	// BOOL moved = NO;
	//
	// [self publishMouseOverPoint:point_view];
	//
	// // Give the document controller an opportunity for undo management!
	// if(self->isStartingDrag && [self->delegate
	// respondsToSelector:@selector(LDrawGLRenderer:willBeginDraggingHandle:)])
	// {
	// [self->delegate LDrawGLRenderer:self
	// willBeginDraggingHandle:self->activeDragHandle];
	// }
	//
	// // Update with new position
	// moved = [self updateDirectives:[NSArray
	// arrayWithObject:self->activeDragHandle]
	// withDragPosition:point_view
	// depthReferencePoint:modelReferencePoint
	// constrainAxis:constrainDragAxis];
	//
	// if(moved)
	// {
	// if([self->fileBeingDrawn
	// respondsToSelector:@selector(optimizeVertexes)])
	// {
	// [(id)self->fileBeingDrawn optimizeVertexes];
	// }
	//
	// [self->fileBeingDrawn noteNeedsDisplay];
	//
	// if([self->delegate
	// respondsToSelector:@selector(LDrawGLRenderer:dragHandleDidMove:)])
	// {
	// [self->delegate LDrawGLRenderer:self
	// dragHandleDidMove:self->activeDragHandle];
	// }
	// }
	//
	// }//end dragHandleDragged:
	//
	//
	// //========== panDragged:location:
	// ==============================================
	// //
	// // Purpose: Scroll the view as the mouse is dragged across it.
	// //
	// //==============================================================================
	// - (void) panDragged:(Vector2)viewDirection
	// location:(Point2)point_view
	// {
	// if(isStartingDrag)
	// {
	// self->initialDragLocation = [self modelPointForPoint:point_view];
	// }
	//
	// Box2 viewport = [self viewport];
	// Point2 point_viewport = [self convertPointToViewport:point_view];
	// Point2 proportion = V2Make(point_viewport.x, point_viewport.y);
	//
	// proportion.x /= V2BoxWidth(viewport);
	// proportion.y /= V2BoxHeight(viewport);
	//
	// if([self->delegate
	// respondsToSelector:@selector(LDrawGLRendererMouseNotPositioning:)])
	// [self->delegate LDrawGLRendererMouseNotPositioning:self];
	//
	// [self scrollModelPoint:self->initialDragLocation
	// toViewportProportionalPoint:proportion];
	//
	// }//end panDragged:
	//
	//
	// //========== rotationDragged:
	// ==================================================
	// //
	// // Purpose: Tis time to rotate the object!
	// //
	// // We need to translate horizontal and vertical 2-dimensional mouse
	// // drags into 3-dimensional rotations.
	// //
	// // +---------------------------------+ /// /- -\ \\\ (This thing is a
	// sphere.)
	// // | y /|\ | / / \ \ .
	// // | | | // / \ \\ .
	// // | |vertical | | /--+-----+-\ |
	// // | |motion (around x) |/// | | \\\|
	// // | | x | | | | |
	// // |<---------------+--------------->| | | | |
	// // | | horizontal | |\\\ | | ///|
	// // | | motion | | \--+-----+-/ |
	// // | | (around y) | \\ | | //
	// // | | | \ \ / /
	// // | \|/ | \\\ \ / ///
	// // +---------------------------------+ --------
	// //
	// // But 2D motion is not 3D motion! We can't just say that
	// // horizontal drag = rotation around y (up) axis. Why? Because the
	// // y-axis may be laying horizontally due to the rotation!
	// //
	// // The trick is to convert the y-axis *on the projection screen*
	// // back to a *vector in the model*. Then we can just call glRotate
	// // around that vector. The result that the model is rotated in the
	// // direction we dragged, no matter what its orientation!
	// //
	// // Last Note: A horizontal drag from left-to-right is a
	// // counterclockwise rotation around the projection's y axis.
	// // This means a positive number of degrees caused by a positive
	// // mouse displacement.
	// // But, a vertical drag from bottom-to-top is a clockwise
	// // rotation around the projection's x-axis. That means a
	// // negative number of degrees cause by a positive mouse
	// // displacement. That means we must multiply our x-rotation by
	// // -1 in order to make it go the right direction.
	// //
	// //==============================================================================
	// - (void) rotationDragged:(Vector2)viewDirection
	// {
	// if([self projectionMode] != ProjectionModePerspective)
	// {
	// [self setProjectionMode:ProjectionModePerspective];
	// self->viewOrientation = ViewOrientation3D;
	// }
	//
	// [camera rotationDragged:viewDirection];
	//
	// if([self->delegate
	// respondsToSelector:@selector(LDrawGLRendererMouseNotPositioning:)])
	// [self->delegate LDrawGLRendererMouseNotPositioning:self];
	//
	// [self->delegate LDrawGLRendererNeedsRedisplay:self];
	//
	//
	// }//end rotationDragged
	//
	// //========== zoomDragged:
	// ======================================================
	// //
	// // Purpose: Drag up means zoom in, drag down means zoom out. 1 px = 1
	// %.
	// //
	// //==============================================================================
	// - (void) zoomDragged:(Vector2)viewDirection
	// {
	// CGFloat pixelChange = -viewDirection.y; // Negative means down
	// CGFloat magnification = pixelChange/100; // 1 px = 1%
	// CGFloat zoomChange = 1.0 + magnification;
	// CGFloat currentZoom = [self zoomPercentage];
	//
	// [self setZoomPercentage:(currentZoom * zoomChange)];
	//
	// if([self->delegate
	// respondsToSelector:@selector(LDrawGLRendererMouseNotPositioning:)])
	// [self->delegate LDrawGLRendererMouseNotPositioning:self];
	//
	// }//end zoomDragged:
	//
	//
	// //========== mouseSelectionDragToPoint:extendSelection:
	// ========================
	// //
	// // Purpose: Selects objects under the dragged rectangle. Caller code
	// tracks
	// // the rectangle itself.
	// //
	// //==============================================================================
	// - (void) mouseSelectionDragToPoint:(Point2)point_view
	// selectionMode:(SelectionModeT) selectionMode
	// {
	// #if TIME_BOXTEST
	// NSDate * startTime = [NSDate date];
	// #endif
	//
	// #if WANT_TWOPASS_BOXTEST
	// NSArray *fastDrawParts = nil;
	// #endif
	// NSArray *fineDrawParts = nil;
	//
	// self->selectionMarquee = V2MakeBoxFromPoints(selectionMarquee.origin,
	// point_view);
	//
	// // Only try to select if we are actually drawing something, and can
	// actually
	// // select it.
	// if( self->fileBeingDrawn != nil
	// && self->allowsEditing == YES
	// && [self->delegate
	// respondsToSelector:@selector(LDrawGLRenderer:wantsToSelectDirective:byExtendingSelection:)]
	// )
	// {
	// // First do hit-testing on nothing but the bounding boxes; that is
	// very
	// // fast and likely eliminates a lot of parts.
	//
	// #if WANT_TWOPASS_BOXTEST
	// fastDrawParts = [self getDirectivesUnderRect:self->selectionMarquee
	// amongDirectives:[NSArray arrayWithObject:self->fileBeingDrawn]
	// fastDraw:YES];
	//
	// fineDrawParts = [self getDirectivesUnderRect:self->selectionMarquee
	// amongDirectives:fastDrawParts
	// fastDraw:NO];
	// #else
	// fineDrawParts = [self getDirectivesUnderRect:self->selectionMarquee
	// amongDirectives:[NSArray arrayWithObject:self->fileBeingDrawn]
	// fastDraw:NO];
	// #endif
	// [self->delegate LDrawGLRenderer:self
	// wantsToSelectDirectives:fineDrawParts
	// selectionMode:selectionMode ];
	//
	// }
	//
	// #if TIME_BOXTEST
	// NSTimeInterval drawTime = -[startTime timeIntervalSinceNow];
	// printf("Box: %lf\n", drawTime);
	// #endif
	//
	// self->didPartSelection = YES;
	//
	// }//end mouseSelectionDrag:to:extendSelection:
	//
	//
	// #pragma mark -
	// #pragma mark Gestures
	//
	// //========== beginGesture
	// ======================================================
	// //
	// // Purpose: Our platform host view is informing us that it is
	// starting
	// // gesture tracking.
	// //
	// //==============================================================================
	// - (void) beginGesture
	// {
	// self->isGesturing = YES;
	// }
	//
	//
	// //========== endGesture
	// ========================================================
	// //
	// // Purpose: Our platform host view is informing us that it is ending
	// // gesture tracking.
	// //
	// //==============================================================================
	// - (void) endGesture
	// {
	// self->isGesturing = NO;
	//
	// if(self->rotationDrawMode == LDrawGLDrawExtremelyFast)
	// {
	// [self->delegate LDrawGLRendererNeedsRedisplay:self];
	// }
	// }
	//
	//
	// //========== rotateWithEvent:
	// ==================================================
	// //
	// // Purpose: User is doing the twist (rotate) trackpad gesture. Rotate
	// // counterclockwise by the given degrees.
	// //
	// // I have decided to interpret this as spinning the "baseplate"
	// // plane of the model (that is, spinning around -y).
	// //
	// //==============================================================================
	// - (void) rotateByDegrees:(float)angle
	// {
	// if([self projectionMode] != ProjectionModePerspective)
	// {
	// [self setProjectionMode:ProjectionModePerspective];
	// self->viewOrientation = ViewOrientation3D;
	// }
	//
	// [camera rotateByDegrees:angle];
	//
	// }//end rotateWithEvent:
	//
	//
	// #pragma mark -
	// #pragma mark DRAG AND DROP
	// #pragma mark -
	//
	// //========== draggingEnteredAtPoint:
	// ===========================================
	// //
	// // Purpose: A drag-and-drop part operation entered this view. We need
	// to
	// // initiate interactive dragging.
	// //
	// //==============================================================================
	// - (void) draggingEnteredAtPoint:(Point2)point_view
	// directives:(NSArray *)directives
	// setTransform:(BOOL)setTransform
	// originatedLocally:(BOOL)originatedLocally
	// {
	// LDrawDrawableElement *firstDirective = [directives objectAtIndex:0];
	// LDrawPart *newPart = nil;
	// TransformComponents partTransform = IdentityComponents;
	// Point3 modelReferencePoint = ZeroPoint3;
	//
	// //---------- Initialize New Part?
	// ------------------------------------------
	//
	// if(setTransform == YES)
	// {
	// // Uninitialized elements are always new parts from the part browser.
	// newPart = [directives objectAtIndex:0];
	//
	// // Ask the delegate roughly where it wants us to be.
	// // We get a full transform here so that when we drag in new parts,
	// they
	// // will be rotated the same as whatever part we were using last.
	// if([self->delegate
	// respondsToSelector:@selector(LDrawGLRendererPreferredPartTransform:)])
	// {
	// partTransform = [self->delegate
	// LDrawGLRendererPreferredPartTransform:self];
	// [newPart setTransformComponents:partTransform];
	// }
	// }
	//
	//
	// //---------- Find Location
	// -------------------------------------------------
	// // We need to map our 2-D mouse coordinate into a point in the
	// model's
	// 3-D
	// // space.
	//
	// modelReferencePoint = [firstDirective position];
	//
	// // Apply the initial offset.
	// // This is the difference between the position of part 0 and the
	// actual
	// // clicked point. We do this so that the point you clicked always
	// remains
	// // directly under the mouse.
	// //
	// // Only applicable if dragging into the source view. Other views may
	// have
	// // different orientations. We might be able to remove that
	// requirement by
	// // zeroing the inapplicable component.
	// if(originatedLocally == YES)
	// {
	// modelReferencePoint = V3Add(modelReferencePoint,
	// self->draggingOffset);
	// }
	// else
	// {
	// [self setDraggingOffset:ZeroPoint3]; // no offset for future updates
	// either
	// }
	//
	// // For constrained dragging, we care only about the initial,
	// unmodified
	// // postion.
	// self->initialDragLocation = modelReferencePoint;
	//
	// // Move the parts
	// [self updateDirectives:directives
	// withDragPosition:point_view
	// depthReferencePoint:modelReferencePoint
	// constrainAxis:NO];
	//
	// // The drag has begun!
	// if([self->fileBeingDrawn
	// respondsToSelector:@selector(setDraggingDirectives:)])
	// {
	// [(id)self->fileBeingDrawn setDraggingDirectives:directives];
	//
	// [self->fileBeingDrawn noteNeedsDisplay];
	// }
	//
	// }//end draggingEntered:
	//
	//
	// //========== endDragging
	// =======================================================
	// //
	// // Purpose: Ends part drag-and-drop.
	// //
	// //==============================================================================
	// - (void) endDragging
	// {
	// if([self->fileBeingDrawn
	// respondsToSelector:@selector(setDraggingDirectives:)])
	// {
	// [(id)self->fileBeingDrawn setDraggingDirectives:nil];
	//
	// [self->fileBeingDrawn noteNeedsDisplay];
	// }
	// }
	//
	//
	// //========== updateDragWithPosition:constrainAxis:
	// =============================
	// //
	// // Purpose: Adjusts the directives so they align with the given drag
	// // location, in window coordinates.
	// //
	// //==============================================================================
	// - (void) updateDragWithPosition:(Point2)point_view
	// constrainAxis:(BOOL)constrainAxis
	// {
	// NSArray *directives = nil;
	// Point3 modelReferencePoint = ZeroPoint3;
	// LDrawDrawableElement *firstDirective = nil;
	// BOOL moved = NO;
	//
	// [self publishMouseOverPoint:point_view];
	//
	// if([self->fileBeingDrawn
	// respondsToSelector:@selector(draggingDirectives)])
	// {
	// directives = [(id)self->fileBeingDrawn draggingDirectives];
	// firstDirective = [directives objectAtIndex:0];
	// modelReferencePoint = [firstDirective position];
	// modelReferencePoint = V3Add(modelReferencePoint,
	// self->draggingOffset);
	//
	// moved = [self updateDirectives:directives
	// withDragPosition:point_view
	// depthReferencePoint:modelReferencePoint
	// constrainAxis:constrainAxis];
	// if(moved)
	// {
	// if([self->fileBeingDrawn
	// respondsToSelector:@selector(optimizeVertexes)])
	// {
	// [(id)self->fileBeingDrawn optimizeVertexes];
	// }
	//
	// [self->fileBeingDrawn noteNeedsDisplay];
	// }
	// }
	//
	// }//end updateDirectives:withDragPosition:
	//
	//
	// //========== updateDirectives:withDragPosition:
	// ================================
	// //
	// // Purpose: Adjusts the directives so they align with the given drag
	// // location, in window coordinates.
	// //
	// //==============================================================================
	// - (BOOL) updateDirectives:(NSArray *)directives
	// withDragPosition:(Point2)point_view
	// depthReferencePoint:(Point3)modelReferencePoint
	// constrainAxis:(BOOL)constrainAxis
	// {
	// LDrawDrawableElement *firstDirective = nil;
	// Point3 modelPoint = ZeroPoint3;
	// Point3 oldPosition = ZeroPoint3;
	// Point3 constrainedPosition = ZeroPoint3;
	// Vector3 displacement = ZeroPoint3;
	// Vector3 cumulativeDisplacement = ZeroPoint3;
	// NSUInteger counter = 0;
	// BOOL moved = NO;
	//
	// firstDirective = [directives objectAtIndex:0];
	//
	//
	// //---------- Find Location
	// ---------------------------------------------
	//
	// // Where are we?
	// oldPosition = modelReferencePoint;
	//
	// // and adjust.
	// modelPoint = [self modelPointForPoint:point_view
	// depthReferencePoint:modelReferencePoint];
	// displacement = V3Sub(modelPoint, oldPosition);
	// cumulativeDisplacement = V3Sub(modelPoint,
	// self->initialDragLocation);
	//
	//
	// //---------- Find Actual Displacement
	// ----------------------------------
	// // When dragging, we want to move IN grid increments, not move TO
	// grid
	// // increments. That means we snap the displacement vector itself to
	// the
	// // grid, not part's location. That's because the part may not have
	// been
	// // grid-aligned to begin with.
	//
	// // As is conventional in graphics programs, we allow dragging to be
	// // constrained to a single axis. We will pick that axis that is
	// furthest
	// // from the initial drag location.
	// if(constrainAxis == YES)
	// {
	// // Find the part's position along the constrained axis.
	// cumulativeDisplacement =
	// V3IsolateGreatestComponent(cumulativeDisplacement);
	// constrainedPosition = V3Add(self->initialDragLocation,
	// cumulativeDisplacement);
	//
	// // Get the displacement from the part's current position to the
	// // constrained one.
	// displacement = V3Sub(constrainedPosition, oldPosition);
	// }
	//
	// // Snap the displacement to the grid.
	// displacement = [firstDirective position:displacement
	// snappedToGrid:self->gridSpacing];
	//
	// //---------- Update the parts' positions
	// ------------------------------
	//
	// if(V3EqualPoints(displacement, ZeroPoint3) == NO)
	// {
	// // Move all the parts by that amount.
	// for(counter = 0; counter < [directives count]; counter++)
	// {
	// [[directives objectAtIndex:counter] moveBy:displacement];
	// }
	//
	// moved = YES;
	// }
	//
	// return moved;
	//
	// }//end updateDirectives:withDragPosition:
	//
	//
	// #pragma mark -
	// #pragma mark NOTIFICATIONS
	// #pragma mark -

	// ========== activeModelDidChange:
	// =============================================
	//
	// Purpose: The selected MPD model changed.
	//
	// ==============================================================================
	public void activeModelDidChange() {
		updateRotationCenter();
		camera.setModelSize(fileBeingDrawn.boundingBox3());
		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);
	}// end displayNeedsUpdating

	// ========== displayNeedsUpdating:
	// =============================================
	//
	// Purpose: Someone (likely our file) has notified us that it has
	// changed,
	// and thus we need to redraw.
	//
	// We also use this opportunity to grow the canvas if necessary.
	//
	// ==============================================================================
	public void displayNeedsUpdating() {
		camera.setModelSize(fileBeingDrawn.boundingBox3());
		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);

	}// end displayNeedsUpdating

	// ========== rotationCenterChanged:
	// ============================================
	//
	// Purpose: The active model changed the point around which it is to
	// be spun.
	//
	// ==============================================================================
	public void rotationCenterChanged() {
		updateRotationCenter();

		if (delegate != null)
			delegate.LDrawGLRendererNeedsRedisplay(this);

	}// end rotationCenterChanged:
		//
		//
		// #pragma mark -
		// #pragma mark UTILITIES
		// #pragma mark -
		//
		// ========== getDepthUnderPoint:
		// ===============================================
		//
		// Purpose: Returns the depth component of the nearest object under
		// the
		// view
		// point.
		//
		// Returns 1.0 if there is no object under the point.
		//
		// ==============================================================================

	public float getDepthUnderPoint(Vector2f point_view) {
		Vector2f point_viewport = convertPointToViewport(point_view);
		Vector2f bl = MatrixMath.V2Make(point_viewport.getX() - HANDLE_SIZE,
				point_viewport.getY() - HANDLE_SIZE);
		Vector2f tr = MatrixMath.V2Make(point_viewport.getX() + HANDLE_SIZE,
				point_viewport.getY() + HANDLE_SIZE);
		FloatBuffer depth = FloatBuffer.allocate(1);
		depth.put(0, 1.0f);

		Box2 viewport = getViewport();

		Vector2f point_clip = new Vector2f((point_viewport.getX() - viewport
				.getOrigin().getX())
				* 2.0f
				/ MatrixMath.V2BoxWidth(viewport)
				- 1.0f, (point_viewport.getY() - viewport.getOrigin().getY())
				* 2.0f / MatrixMath.V2BoxHeight(viewport) - 1.0f);

		float x1 = (float) ((Math.min(bl.getX(), tr.getX()) - viewport
				.getOrigin().getX()) * 2.0 / MatrixMath.V2BoxWidth(viewport) - 1.0f);

		float x2 = (float) ((Math.max(bl.getX(), tr.getX()) - viewport
				.getOrigin().getX()) * 2.0 / MatrixMath.V2BoxWidth(viewport) - 1.0);

		float y1 = (float) ((Math.min(bl.getY(), tr.getY()) - viewport
				.getOrigin().getX()) * 2.0 / MatrixMath.V2BoxHeight(viewport) - 1.0);
		float y2 = (float) ((Math.max(bl.getY(), tr.getY()) - viewport
				.getOrigin().getY()) * 2.0 / MatrixMath.V2BoxHeight(viewport) - 1.0);

		Box2 test_box = MatrixMath.V2MakeBox(x1, y1, x2 - x1, y2 - y1);

		Matrix4 mvp = MatrixMath.Matrix4Multiply(
				MatrixMath.Matrix4CreateFromGLMatrix4(camera.getModelView()),
				MatrixMath.Matrix4CreateFromGLMatrix4(camera.getProjection()));

		ArrayList<LDrawDirective> bestObject = new ArrayList<LDrawDirective>();
		fileBeingDrawn.depthTest(point_clip, test_box, mvp, null, bestObject,
				depth);

		return depth.get(0) * 0.5f + 0.5f;

	}// end getDepthUnderPoint

	// ========== getDirectivesUnderRect:amongDirectives:fastDraw:
	// ==================
	//
	// Purpose: Finds the directives under a given mouse-recangle. This
	// does a two-pass search so that clients can do a bounding box
	// test first.
	//
	// Parameters: bottom_left, top_right = the rectangle (in viewport
	// space)
	// in
	// which to test.
	// directives = the directives under consideration for being
	// clicked. This may be the whole File directive,
	// or a smaller subset we have already determined
	// (by a previous call) is in the area.
	// fastDraw = consider only bounding boxes for hit-detection.
	//
	// Returns: Array of all parts that are at least partly inside the
	// rectangle
	// in screen space.
	//
	// ==============================================================================
	public ArrayList<LDrawDirective> getDirectivesUnderRect(Box2 rect_view,
			ArrayList<LDrawDirective> directives, boolean fastDraw) {
		ArrayList<LDrawDirective> clickedDirectives = null;

		if (directives.size() == 0) {
			// If there's nothing to test in, there's no work to do!
			clickedDirectives = new ArrayList<LDrawDirective>();
		} else {
			Vector2f bottom_left = rect_view.getOrigin();
			Vector2f top_right = MatrixMath.V2Make(
					MatrixMath.V2BoxMaxX(rect_view),
					MatrixMath.V2BoxMaxY(rect_view));
			Vector2f bl = convertPointToViewport(bottom_left);
			Vector2f tr = convertPointToViewport(top_right);
			Box2 viewport = getViewport();
			TreeSet<LDrawDirective> hits = new TreeSet<LDrawDirective>();
			int counter = 0;

			float x1 = (float) ((Math.min(bl.getX(), tr.getX()) - viewport
					.getOrigin().getX())
					* 2.0
					/ MatrixMath.V2BoxWidth(viewport) - 1.0);
			float x2 = (float) ((Math.max(bl.getX(), tr.getX()) - viewport
					.getOrigin().getX())
					* 2.0
					/ MatrixMath.V2BoxWidth(viewport) - 1.0);
			float y1 = (float) ((Math.min(bl.getY(), tr.getY()) - viewport
					.getOrigin().getX())
					* 2.0
					/ MatrixMath.V2BoxHeight(viewport) - 1.0);
			float y2 = (float) ((Math.max(bl.getY(), tr.getY()) - viewport
					.getOrigin().getY())
					* 2.0
					/ MatrixMath.V2BoxHeight(viewport) - 1.0);

			Box2 test_box = MatrixMath.V2MakeBox(x1, y1, x2 - x1, y2 - y1);

			Matrix4 mvp = MatrixMath.Matrix4Multiply(MatrixMath
					.Matrix4CreateFromGLMatrix4(camera.getModelView()),
					MatrixMath.Matrix4CreateFromGLMatrix4(camera
							.getProjection()));

			// Do hit test
			for (counter = 0; counter < directives.size(); counter++) {
				directives.get(counter).boxTest(test_box, mvp, fastDraw, null,
						hits);

			}
			ArrayList<LDrawDirective> collected = new ArrayList<LDrawDirective>(
					hits.size());
			clickedDirectives = collected;

			for (LDrawDirective currentDirective : hits) {
				collected.add(currentDirective);
			}
		}
		return clickedDirectives;
	}// end getDirectivesUnderMouse:amongDirectives:fastDraw

	// ========== publishMouseOverPoint:
	// ============================================
	//
	// Purpose: Informs the delegate that the mouse is hovering over the
	// model
	// point under the view point.
	//
	// ==============================================================================
	public void publishMouseOverPoint(Vector2f point_view) {
		Vector3f modelPoint = Vector3f.getZeroVector3f();
		Vector3f modelAxisForX = Vector3f.getZeroVector3f(); // ZeroPoint3;
		Vector3f modelAxisForY = Vector3f.getZeroVector3f();// ZeroPoint3;
		Vector3f modelAxisForZ = Vector3f.getZeroVector3f();// ZeroPoint3;
		Vector3f confidence = Vector3f.getZeroVector3f();// ZeroPoint3;

		// todo
		// �좎룞�쇿뜝�뚯뒪 �좎룞�쇿뜝�숈삕 �좎떛釉앹삕���좎룞�쇿뜝�숈삕 �좎룞�쇿뜝�숈삕. �좎룞�쇿뜝�⑹슱���좎뙐�숈삕
		// �좎룞�쇿뜝�숈삕�좎뙏�듭삕 �좎떬�먯삕.
		// if([self->delegate
		// respondsToSelector:@selector(LDrawGLRenderer:mouseIsOverPoint:confidence:)])
		// {
		// modelPoint = [self modelPointForPoint:point_view];
		//
		// if([self projectionMode] == ProjectionModeOrthographic)
		// {
		// [self getModelAxesForViewX:&modelAxisForX Y:&modelAxisForY
		// Z:&modelAxisForZ];
		//
		// confidence = V3Add(modelAxisForX, modelAxisForY);
		// }
		//
		// [self->delegate LDrawGLRenderer:self mouseIsOverPoint:modelPoint
		// confidence:confidence];
		// }
	}

	// ========== setZoomPercentage:preservePoint:
	// ==================================
	//
	// Purpose: Performs cursor-centric zooming on the given point, in
	// view
	// coordinates. After the new zoom is applied, the 3D point
	// projected at viewPoint will still be in the same projected
	// location.
	//
	// ==============================================================================
	public void setZoomPercentage(float newPercentage, Vector2f viewPoint) {
		Vector3f modelPoint = modelPointForPoint(viewPoint);

		camera.setZoomPercentage(newPercentage, modelPoint);

	}// end setZoomPercentage:preservePoint:

	// ========== scrollCenterToModelPoint:
	// =========================================
	//
	// Purpose: Scrolls the receiver (if it is inside a scroll view) so
	// that
	// newCenter is at the center of the viewing area. newCenter is
	// given in LDraw model coordinates.
	//
	// ==============================================================================
	public void scrollCenterToModelPoint(Vector3f modelPoint) {
		scrollModelPoint(modelPoint, MatrixMath.V2Make(0.5f, 0.5f));
	}

	// ========== scrollModelPoint:toViewportProportionalPoint:
	// =====================
	//
	// Purpose: Scrolls viewport so the projection of the given 3D point
	// appears
	// at the given fraction of the viewport. (0,0) means the
	// bottom-right corner of the viewport; (0.5, 0.5) means the
	// center; (1.0, 1.0) means the top-right.
	//
	// ==============================================================================
	public void scrollModelPoint(Vector3f modelPoint, Vector2f viewportPoint) {
		camera.scrollModelPoint(modelPoint, viewportPoint);
	}// end scrollCenterToModelPoint:

	// ========== updateRotationCenter
	// ==============================================
	//
	// Purpose: Resync our copy of the rotationCenter with the one used
	// by
	// the
	// model.
	//
	// ==============================================================================
	public void updateRotationCenter() {
		Vector3f point = Vector3f.getZeroVector3f();

		if (LDrawFile.class.isInstance(fileBeingDrawn)) {
			point = (((LDrawFile) fileBeingDrawn).activeModel())
					.rotationCenter();
		} else if (LDrawModel.class.isInstance(fileBeingDrawn)) {
			point = ((LDrawModel) fileBeingDrawn).rotationCenter();
		}

		camera.setRotationCenter(point);
	}

	// ========== convertPointFromViewport:
	// =========================================
	//
	// Purpose: Converts the point from the viewport coordinate system to
	// the
	// view bounds' coordinate system.
	//
	// ==============================================================================
	public Vector2f convertPointFromViewport(Vector2f viewportPoint) {
		Vector2f point_visibleRect = Vector2f.getZeroVector2f();
		Vector2f point_view = Vector2f.getZeroVector2f();

		// Rescale to visible rect
		point_visibleRect.setX(viewportPoint.getX()
				/ (zoomPercentageForGL() / 100.0f));
		point_visibleRect.setY(viewportPoint.getY()
				/ (zoomPercentageForGL() / 100.0f));

		// The viewport origin is always at (0,0), so wo only need to
		// translate
		// if
		// the coordinate system is flipped.

		// Flip the coordinates
		if (isFlipped()) {
			// The origin of the viewport is in the lower-left corner.
			// The origin of the view is in the upper right (it is flipped)
			point_visibleRect.setY(MatrixMath.V2BoxHeight(scroller
					.getVisibleRect()) - point_visibleRect.getY());
		}

		// Translate to full bounds coordinates
		point_view.setX(point_visibleRect.getX()
				+ scroller.getVisibleRect().getOrigin().getX());
		point_view.setY(point_visibleRect.getY()
				+ scroller.getVisibleRect().getOrigin().getY());

		return point_view;

	}// end convertPointFromViewport:

	// ========== convertPointToViewport:
	// ===========================================
	//
	// Purpose: Converts the point from the view bounds' coordinate
	// system
	// into
	// the viewport's coordinate system.
	//
	// ==============================================================================
	public Vector2f convertPointToViewport(Vector2f point_view) {
		Vector2f point_visibleRect = Vector2f.getZeroVector2f();
		Vector2f point_viewport = Vector2f.getZeroVector2f();

		// Translate from full bounds coordinates to the visible rect
		point_visibleRect.setX(point_view.getX()
				- scroller.getVisibleRect().getOrigin().getX());
		point_visibleRect.setY(point_view.getY()
				- scroller.getVisibleRect().getOrigin().getY());

		// Flip the coordinates
		if (isFlipped()) {
			// The origin of the viewport is in the lower-left corner.
			// The origin of the view is in the upper right (it is flipped)
			point_visibleRect.setY(MatrixMath.V2BoxHeight(scroller
					.getVisibleRect()) - point_visibleRect.getY());
		}

		// Rescale to viewport pixels
		point_viewport.setX(point_visibleRect.getX()
				* (zoomPercentageForGL() / 100.0f));
		point_viewport.setY(point_visibleRect.getY()
				* (zoomPercentageForGL() / 100.0f));

		return point_viewport;

	}// end convertPointToViewport:

	// ========== getModelAxesForViewX:Y:Z:
	// =========================================
	//
	// Purpose: Finds the axes in the model coordinate system which most
	// closely
	// project onto the X, Y, Z axes of the view.
	//
	// Notes: The screen coordinate system is right-handed:
	//
	// +y
	// |
	// |
	// *-- +x
	// /
	// +z
	//
	// The choice between what is the "closest" axis in the model is
	// often arbitrary, but it will always be a unique and
	// sensible-looking choice.
	//
	// ==============================================================================
	public void getModelAxesForView(Vector3f outModelX, Vector3f outModelY,
			Vector3f outModelZ) {
		Vector4f screenX = new Vector4f(1, 0, 0, 0);
		Vector4f screenY = new Vector4f(0, 1, 0, 0);
		Vector4f unprojectedX, unprojectedY; // the vectors in the model which
		// are
		// projected onto x,y on screen
		Vector3f modelX, modelY, modelZ; // the closest model axes to which the
		// screen's x,y,z align

		// Translate the x, y, and z vectors on the surface of the screen
		// into
		// the
		// axes to which they most closely align in the model itself.
		// This requires the inverse of the current transformation matrix, so
		// we
		// can
		// convert projection-coordinates back to the model coordinates they
		// are
		// displaying.
		Matrix4 inversed = getInverseMatrix();

		// find the vectors in the model which project onto the screen's axes
		// (We only care about x and y because this is a two-dimensional
		// projection, and the third axis is consquently ambiguous. See
		// below.)
		unprojectedX = MatrixMath.V4MulPointByMatrix(screenX, inversed);
		unprojectedY = MatrixMath.V4MulPointByMatrix(screenY, inversed);

		// find the actual axes closest to those model vectors
		modelX = MatrixMath.V3FromV4(unprojectedX);
		modelY = MatrixMath.V3FromV4(unprojectedY);

		modelX = MatrixMath.V3IsolateGreatestComponent(modelX);
		modelY = MatrixMath.V3IsolateGreatestComponent(modelY);

		modelX = MatrixMath.V3Normalize(modelX);
		modelY = MatrixMath.V3Normalize(modelY);

		// The z-axis is often ambiguous because we are working backwards
		// from a
		// two-dimensional screen. Thankfully, while the process used for
		// deriving
		// the x and y vectors is perhaps somewhat arbitrary, it always
		// yields
		// sensible and unique results. Thus we can simply derive the
		// z-vector,
		// which will be whatever axis x and y *didn't* land on.
		modelZ = MatrixMath.V3Cross(modelX, modelY);

		if (outModelX != null)
			outModelX.set(modelX);
		if (outModelY != null)
			outModelY.set(modelY);
		if (outModelZ != null)
			outModelZ.set(modelZ);

	}// end getModelAxesForViewX:Y:Z:

	// ========== modelPointForPoint:
	// ===============================================
	//
	// Purpose: Unprojects the given point (in view coordinates) back
	// into a
	// point in the model which projects there, using existing data in
	// the depth buffer to infer the location on the z axis.
	//
	// Notes: The depth buffer is not super-accurate, but it's passably
	// close. But most importantly, it could be faster to read the
	// depth buffer than to redraw parts of the model under a pick
	// matrix.
	//
	// ==============================================================================
	public Vector3f modelPointForPoint(Vector2f viewPoint) {
		Vector2f viewportPoint = convertPointToViewport(viewPoint);
		float depth = 0.0f;
		TransformComponents partTransform = TransformComponents
				.getIdentityComponents();
		Vector3f contextPoint = Vector3f.getZeroVector3f();
		Vector3f modelPoint = Vector3f.getZeroVector3f();

		depth = getDepthUnderPoint(viewPoint);

		if (depth == 1.0) {
			// Error!
			// Maximum depth readings essentially tell us that no pixels were
			// drawn
			// at this point. So we have to make up a best guess now. This guess
			// will very likely be wrong, but there is little else which can be
			// done.

			// todo
			// �좎룞�쇿뜝�뚯뒪 �좎떛釉앹삕�� window �좎떛釉앹삕���좎떛�몄삕�좎떦�먯삕 �좎떥釉앹삕.
			// �좎떇�듭삕�좎뙏�쎌삕 java
			// �좎룞�쇿뜝�숈삕�좎룞�쇿뜝占��좎뙐�숈삕 吏쒎뜝�숈삕 �좎룞��
			// if(delegate.respondsToSelector:@selector(LDrawGLRendererPreferredPartTransform:)])
			// {
			// partTransform = [self->delegate
			// LDrawGLRendererPreferredPartTransform:self];
			// }

			modelPoint = modelPointForPoint(viewPoint,
					partTransform.getTranslate());
		} else {
			// Convert to 3D viewport coordinates
			contextPoint = MatrixMath.V3Make(viewportPoint.getX(),
					viewportPoint.getY(), depth);

			// Convert back to a point in the model.
			modelPoint = MatrixMath.V3Unproject(contextPoint, MatrixMath
					.Matrix4CreateFromGLMatrix4(camera.getModelView()),
					MatrixMath.Matrix4CreateFromGLMatrix4(camera
							.getProjection()), getViewport());
		}

		return modelPoint;

	}// end modelPointForPoint:

	// ========== modelPointForPoint:depthReferencePoint:
	// ===========================
	//
	// Purpose: Unprojects the given point (in view coordinates) back
	// into a
	// point in the model which projects there, calculating the
	// location on the z axis using the given depth reference point.
	//
	// Notes: Any point on the screen represents the projected location
	// of an
	// infinite number of model points, extending on a line from the
	// near to the far clipping plane.
	//
	// It's impossible to boil that down to a single point without
	// being given some known point in the model to determine the
	// desired depth. (Hence the depthPoint parameter.) The returned
	// point will lie on a plane which contains depthPoint and is
	// perpendicular to the model axis most closely aligned to the
	// computer screen's z-axis.
	//
	// * * * *
	//
	// When viewing the model with an orthographic projection and the
	// camera pointing parallel to one of the model's coordinate axes,
	// this method is useful for determining two of the three
	// coordinates over which the mouse is hovering. To find which
	// coordinate is bogus, we call -getModelAxesForViewX:Y:Z:. The
	// returned z-axis indicates the unreliable point.
	//
	// ==============================================================================
	public Vector3f modelPointForPoint(Vector2f viewPoint, Vector3f depthPoint) {
		Box2 viewport = getViewport();

		Vector2f contextPoint = convertPointToViewport(viewPoint);
		Vector3f nearModelPoint = Vector3f.getZeroVector3f();
		Vector3f farModelPoint = Vector3f.getZeroVector3f();
		Vector3f modelPoint = Vector3f.getZeroVector3f();
		Vector3f modelZ = Vector3f.getZeroVector3f();

		float t = 0.0f; // parametric variable

		// gluUnProject takes a window "z" coordinate. These values range
		// from
		// 0.0 (on the near clipping plane) to 1.0 (the far clipping plane).

		// - Near clipping plane unprojection
		nearModelPoint = MatrixMath.V3Unproject(MatrixMath.V3Make(
				contextPoint.getX(), contextPoint.getY(), 0.0f), MatrixMath
				.Matrix4CreateFromGLMatrix4(camera.getModelView()), MatrixMath
				.Matrix4CreateFromGLMatrix4(camera.getProjection()), viewport);

		// - Far clipping plane unprojection
		farModelPoint = MatrixMath.V3Unproject(MatrixMath.V3Make(
				contextPoint.getX(), contextPoint.getY(), 1.0f), MatrixMath
				.Matrix4CreateFromGLMatrix4(camera.getModelView()), MatrixMath
				.Matrix4CreateFromGLMatrix4(camera.getProjection()), viewport);

		// ---------- Derive the actual point from the depth point
		// --------------
		//
		// We now have two accurate unprojected coordinates: the near (P1)
		// and
		// far (P2) points of the line through 3-D space which projects onto
		// the
		// single screen point.
		//
		// The parametric equation for a line given two points is:
		//
		// / \ /
		// L = | 1 - t | P + t P (see? at t=0, L = P1 and at t=1, L = P2.
		// \ / 1 2
		//
		// So for example, z = (1-t)*z1 + t*z2
		// z = z1 - t*z1 + t*z2
		//
		// / \ /
		// z = z - t | z - z |
		// 1 \ 1 2/
		//
		//
		// z - z
		// 1 No need to worry about dividing
		// t = --------- by 0 because the axis we are
		// z - z inspecting will never be
		// 1 2 perpendicular to the screen.

		// Which axis are we going to use from the reference point?
		// Vector3f temp = Vector3f.getZeroVector3f();
		getModelAxesForView((Vector3f) null, (Vector3f) null, modelZ);

		// Find the value of the parameter at the depth point.
		if (modelZ.getX() != 0) {
			t = (nearModelPoint.getX() - depthPoint.getX())
					/ (nearModelPoint.getX() - farModelPoint.getX());
		} else if (modelZ.getY() != 0) {
			t = (nearModelPoint.getY() - depthPoint.getY())
					/ (nearModelPoint.getY() - farModelPoint.getY());
		} else if (modelZ.getZ() != 0) {
			t = (nearModelPoint.getZ() - depthPoint.getZ())
					/ (nearModelPoint.getZ() - farModelPoint.getZ());
		}
		// Evaluate the equation of the near-to-far line at the parameter for
		// the depth point.
		modelPoint.setX(MatrixMath.LERP(t, nearModelPoint.getX(),
				farModelPoint.getX()));
		modelPoint.setY(MatrixMath.LERP(t, nearModelPoint.getX(),
				farModelPoint.getY()));
		modelPoint.setZ(MatrixMath.LERP(t, nearModelPoint.getZ(),
				farModelPoint.getZ()));

		return modelPoint;

	}// end modelPointForPoint:depthReferencePoint:

	@Override
	public void receiveNotification(NotificationMessageT notificationType,
			INotificationMessage msg) {
		switch (notificationType) {
		case LDrawDirectiveDidChange:
		case LDrawFileActiveModelDidChange:
		case LDrawModelRotationCenterDidChange:
			displayNeedsUpdating();
			break;
		default:
			break;
		}

	}

	public boolean isReadyToUse() {
		return this.isReadyToUse;
	}

	public void setUseWireFrame(boolean b) {
		this.useWireFrame = b;
	}
}
