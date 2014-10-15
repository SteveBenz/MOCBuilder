package LDraw.Support;

import Common.Box2;
import Common.Box3;
import Common.Matrix4;
import Common.Size2;
import Common.Vector2f;
import Common.Vector3f;
import Common.Vector4f;
import LDraw.Support.type.LocationModeT;
import LDraw.Support.type.ProjectionModeT;

/*
 Who owns what data?

 Things appkit knows about:

 Scroll position
 owned by Appkit.

 Zoom
 owned by camera.  Clip view scale factor owned by NS and slaved from zoom by camera _sometimes_.

 Document Size
 owned by GL view, controlled by camera

 Things OpenGL knows about:
 viewport - always set to visible area of GL drawable by view code - the camera assumes this is true.
 transform matrices - always owned by camera.

 */
/*
 Who owns what data?

 Things appkit knows about:

 Scroll position
 owned by Appkit.

 Zoom
 owned by camera.  Clip view scale factor owned by NS and slaved from zoom by camera _sometimes_.

 Document Size
 owned by GL view, controlled by camera

 Things OpenGL knows about:
 viewport - always set to visible area of GL drawable by view code - the camera assumes this is true.
 transform matrices - always owned by camera.

 */

public class LDrawGLCamera {
	public static final int NO_ROUNDING_DOC_SIZE = 0;

	// controls perspective; cameraLocation = modelSize * CAMERA_DISTANCE_FACTOR
	public static final float CAMERA_DISTANCE_FACTOR = 6.5f;

	// Turn-table view changes how rotations work
	public static final boolean USE_TURNTABLE = false;

	public static final float WALKTHROUGH_NEAR = 20.0f;
	public static final float WALKTHROUGH_FAR = 20000.0f;

	protected	ILDrawGLCameraScroller scroller;

	protected	float[] projection = new float[16];
	protected	float[] modelView = new float[16];
	protected	float[] orientation = new float[16];

	protected	ProjectionModeT projectionMode;
	protected	LocationModeT locationMode;
	Box3 modelSize;

	boolean viewportExpandsToAvailableSize;
	float zoomFactor;

	protected	float cameraDistance; // location of camera on the z-axis; distance from
							// (0,0,0);
	protected	Vector3f rotationCenter;
	Size2 snugFrameSize;

	int mute; // Counted 'mute' to stop re-entrant calls to tickle...

	// Normally the doc size is rounded so that it doesn't jump per frame as we
	// nudge; we can turn this OFF to debug editing.

	public LDrawGLCamera() {
		init();
	}

	// ========== init
	// ==============================================================
	//
	// Purpose: Sets up the new camera.
	//
	// Notes: The camera isn't really useful until a scroller is attached and
	// the camera is then tickled. Without a scroller, the camera
	// cannot complete its setup.
	//
	// ==============================================================================
	public LDrawGLCamera init() {
		viewportExpandsToAvailableSize = true;

		zoomFactor = 100; // percent
		cameraDistance = -10000;
		projectionMode = ProjectionModeT.ProjectionModePerspective;
		locationMode = LocationModeT.LocationModeModel;
		modelSize = Box3.getInvalidBox();

		rotationCenter = Vector3f.getZeroVector3f();
		snugFrameSize = Size2.getZeroSize2();

		GLMatrixMath.buildRotationMatrix(orientation, 180, 1, 0, 0);
		GLMatrixMath.buildIdentity(modelView);
		GLMatrixMath.buildIdentity(projection);

		return this;
	}// end init

	// ========== setScroller:
	// ======================================================
	//
	// Purpose: Specifies a scroller protocol that the camera uses to get
	// information about the document.
	//
	// Notes: While the simplest design might be for the camer to control all
	// apsects of viewing, it can't own scrolling; AppKit needs to own
	// scrolling state and having the data exist in two places is a
	// recipe for chaos.
	//
	// So the scroller gives the camera an abstract way to ask
	// _someone_ what's going on in the NS world with scrolling without
	// having to have our app's NS structure coded into the camera.
	//
	// ==============================================================================
	public void setScroller(ILDrawGLCameraScroller newScroller) {
		scroller = newScroller;
	}// end setScroller:

	// #pragma mark -
	// #pragma mark PUBLIC ACCESSORS
	// #pragma mark -

	// ========== getProjection
	// =====================================================
	//
	// Purpose: Returns the current projection matrix as a float[16] ptr.
	// The projection matrix handles the effects of scrolling and
	// zoom.
	//
	// Notes: The camera class does not talk to OpenGL directly, and thus
	// does not need context access. The current matrices are owned
	// by the camera. Rendering engine code is responsible for syncing
	// OpenGL to the camera, or shoveling these matrices into its
	// custom shaders.
	//
	// ==============================================================================
	public float[] getProjection() {
		return projection;

	}// end getProjection

	// ========== getModelView
	// ======================================================
	//
	// Purpose: Returns the current modelview matrix as a float[16] ptr.
	// The modelview matrix accounts for camera view distance, model
	// rotation and model center changes.
	//
	// ==============================================================================
	public float[] getModelView() {
		return modelView;

	}// end getModelView

	// ========== zoomPercentage
	// ====================================================
	//
	// Purpose: Returns the current zoom percentage.
	//
	// ==============================================================================
	public float zoomPercentage() {
		return zoomFactor;

	}// end zoomPercentage

	// ========== projectionMode
	// ====================================================
	//
	// Purpose: Returns the current projection mode (perspective or ortho).
	//
	// ==============================================================================
	public ProjectionModeT projectionMode() {
		return projectionMode;

	}// end projectionMode

	// ========== locationMode
	// ====================================================
	//
	// Purpose: Returns the current location mode.
	//
	// ==============================================================================
	public LocationModeT locationMode() {
		return locationMode;

	}// end locationMode

	// ========== viewingAngle
	// ======================================================
	//
	// Purpose: Returns the current viewing angle as a triplet of Euler angles.
	//
	// ==============================================================================
	public Vector3f viewingAngle() {
		Matrix4 transformation = Matrix4.getIdentityMatrix4();
		TransformComponents components = TransformComponents
				.getIdentityComponents();
		Vector3f degrees = Vector3f.getZeroVector3f();

		transformation = MatrixMath.Matrix4CreateFromGLMatrix4(getModelView());
		transformation = MatrixMath.Matrix4Rotate(transformation,
				MatrixMath.V3Make(180, 0, 0)); // LDraw is upside-down
		MatrixMath.Matrix4DecomposeTransformation(transformation, components);
		degrees = components.rotate;

		degrees.setX((float) Math.toDegrees(degrees.getX()));
		degrees.setY((float) Math.toDegrees(degrees.getY()));
		degrees.setZ((float) Math.toDegrees(degrees.getZ()));

		return degrees;

	}// end viewingAngle

	public Vector3f rotationCenter() {
		return rotationCenter;
	}

	// #pragma mark -
	// #pragma mark INTERNAL UTILITIES
	// #pragma mark -

	// ========== scrollCenterToPoint:
	// ==============================================
	//
	// Purpose: Scrolls a given model point to the center of the visible window.
	//
	// Notes: This utility does not 'tickle' the camera - client code must do
	// this.
	//
	// ==============================================================================
	public void scrollCenterToPoint(Vector2f newCenter) {
		Box2 newVisibleRect = scroller.getVisibleRect();
		Vector2f scrollOrigin = MatrixMath
				.V2Make(newCenter.getX()
						- MatrixMath.V2BoxWidth(scroller.getVisibleRect()) / 2,
						newCenter.getY()
								- MatrixMath.V2BoxHeight(scroller
										.getVisibleRect()) / 2);
		// Sanity check
		if (scrollOrigin.getX() < 0) {
			scrollOrigin.setX(0);
		}
		if (scrollOrigin.getY() < 0) {
			scrollOrigin.setY(0);
		}

		newVisibleRect.setOrigin(scrollOrigin);

		scroller.setScrollOrigin(newVisibleRect.getOrigin());

	}// end scrollCenterToPoint:

	// ========== fieldDepth
	// ========================================================
	//
	// Purpose: Returns the depth range of our view - that is, the distance
	// between the near an far clip planes in model coordinates. The
	// model origin is centered in this range.
	//
	// ==============================================================================
	public float fieldDepth() {
		float fieldDepth = 0;

		// This is effectively equivalent to infinite field depth
		fieldDepth = Math.max(snugFrameSize.getHeight(),
				snugFrameSize.getWidth());
		fieldDepth *= 2;

		return fieldDepth;

	}// end fieldDepth

	// ========== nearOrthoClippingRectFromVisibleRect:
	// ============================
	//
	// Purpose: Returns the rect of the near clipping plane which should be used
	// for an orthographic projection. The coordinates are in model
	// coordinates, located on the plane at
	// z = - fieldDepth() / 2.
	//
	// ==============================================================================
	public Box2 nearOrthoClippingRectFromVisibleRect(Box2 visibleRectIn) {
		Box2 visibilityPlane = Box2.getZeroBox2();

		float y = MatrixMath.V2BoxMinY(visibleRectIn);
		if (true)// isFlipped] == true;)
		{
			y = scroller.getDocumentSize().getHeight() - y
					- MatrixMath.V2BoxHeight(visibleRectIn);
		}

		// The projection plane is stated in model coordinates.
		visibilityPlane.getOrigin().setX(
				MatrixMath.V2BoxMinX(visibleRectIn)
						- scroller.getDocumentSize().getWidth() / 2);
		visibilityPlane.getOrigin().setY(
				y - scroller.getDocumentSize().getHeight() / 2);
		visibilityPlane.getSize()
				.setWidth(MatrixMath.V2BoxWidth(visibleRectIn));
		visibilityPlane.getSize().setHeight(
				MatrixMath.V2BoxHeight(visibleRectIn));

		return visibilityPlane;

	}// end nearOrthoClippingRectFromVisibleRect:

	// ========== nearFrustumClippingRectFromVisibleRect:
	// ==========================
	//
	// Purpose: Returns the rect of the near clipping plane which should be used
	// for an perspective projection. The coordinates are in model
	// coordinates, located on the plane at
	// z = - fieldDepth() / 2.
	//
	// Notes: We want perspective and ortho views to show objects at the
	// origin as the same size. Since perspective viewing is defined
	// by a frustum (truncated pyramid), we have to shrink the
	// visibily plane--which is located on the near clipping plane--in
	// such a way that the slice of the frustum at the origin will
	// have the dimensions of the desired visibility plane. (Remember,
	// slices grow *bigger* as they go deeper into the view. Since the
	// origin is deeper, that means we need a near visibility plane
	// that is *smaller* than the desired size at the origin.)
	//
	// ==============================================================================
	public Box2 nearFrustumClippingRectFromVisibleRect(Box2 visibleRectIn) {
		Box2 orthoVisibilityPlane = nearOrthoClippingRectFromVisibleRect(visibleRectIn);
		Box2 visibilityPlane = orthoVisibilityPlane;
		float fieldDepth = fieldDepth();

		// Find the scaling percentage betwen the frustum slice through
		// (0,0,0) and the slice that defines the near clipping plane.
		float visibleProportion = (Math.abs(cameraDistance) - fieldDepth / 2)
				/ Math.abs(cameraDistance);

		// scale down the visibility plane, centering it in the full-size one.
		visibilityPlane.getOrigin().setX(
				MatrixMath.V2BoxMinX(orthoVisibilityPlane)
						+ MatrixMath.V2BoxWidth(orthoVisibilityPlane)
						* (1 - visibleProportion) / 2);
		visibilityPlane.getOrigin().setY(
				MatrixMath.V2BoxMinY(orthoVisibilityPlane)
						+ MatrixMath.V2BoxHeight(orthoVisibilityPlane)
						* (1 - visibleProportion) / 2);
		visibilityPlane.getSize()
				.setWidth(
						MatrixMath.V2BoxWidth(orthoVisibilityPlane)
								* visibleProportion);
		visibilityPlane.getSize().setHeight(
				MatrixMath.V2BoxHeight(orthoVisibilityPlane)
						* visibleProportion);

		return visibilityPlane;

	}// end nearFrustumClippingRectFromVisibleRect:

	// ========== nearOrthoClippingRectFromNearFrustumClippingRect:
	// =================
	//
	// Purpose: Returns the near clipping rectangle which would be used if the
	// given perspective view were converted to an orthographic
	// projection.
	//
	// ==============================================================================
	public Box2 nearOrthoClippingRectFromNearFrustumClippingRect(
			Box2 visibilityPlane) {
		Box2 orthoVisibilityPlane = Box2.getZeroBox2();
		float fieldDepth = fieldDepth();

		// Find the scaling percentage betwen the frustum slice through
		// (0,0,0) and the slice that defines the near clipping plane.
		float visibleProportion = (Math.abs(cameraDistance) - fieldDepth / 2)
				/ Math.abs(cameraDistance);

		// Enlarge the ortho plane
		orthoVisibilityPlane.getSize().setWidth(
				visibilityPlane.getSize().getWidth() / visibleProportion);
		orthoVisibilityPlane.getSize().setHeight(
				visibilityPlane.getSize().getHeight() / visibleProportion);

		// Move origin according to enlargement
		orthoVisibilityPlane.getOrigin().setX(
				MatrixMath.V2BoxMinX(visibilityPlane)
						- MatrixMath.V2BoxWidth(orthoVisibilityPlane)
						* (1 - visibleProportion) / 2);
		orthoVisibilityPlane.getOrigin().setY(
				MatrixMath.V2BoxMinY(visibilityPlane)
						- MatrixMath.V2BoxHeight(orthoVisibilityPlane)
						* (1 - visibleProportion) / 2);

		return orthoVisibilityPlane;

	}// end nearOrthoClippingRectFromNearFrustumClippingRect:

	// ========== visibleRectFromNearOrthoClippingRect:
	// =============================
	//
	// Purpose: Returns the Cocoa view visible rectangle which would result in
	// the given orthographic clipping rect.
	//
	// ==============================================================================
	public Box2 visibleRectFromNearOrthoClippingRect(Box2 visibilityPlane) {
		Box2 newVisibleRect = Box2.getZeroBox2();

		// Convert from model coordinates back to Cocoa view coordinates.

		newVisibleRect.getOrigin().setX(
				visibilityPlane.getOrigin().getX()
						+ scroller.getDocumentSize().getWidth() / 2);
		newVisibleRect.getOrigin().setY(
				visibilityPlane.getOrigin().getY()
						+ scroller.getDocumentSize().getHeight() / 2);
		newVisibleRect.setSize(visibilityPlane.getSize());

		if (true)// isFlipped] == true;)
		{
			newVisibleRect.getOrigin().setY(
					scroller.getDocumentSize().getHeight()
							- MatrixMath.V2BoxHeight(visibilityPlane)
							- MatrixMath.V2BoxMinY(newVisibleRect));
		}

		return newVisibleRect;

	}// end visibleRectFromNearOrthoClippingRect:

	// ========== visibleRectFromNearFrustumClippingRect:
	// ===========================
	//
	// Purpose: Returns the Cocoa view visible rectangle which would result in
	// the given frustum clipping rect.
	//
	// ==============================================================================
	public Box2 visibleRectFromNearFrustumClippingRect(Box2 visibilityPlane) {
		Box2 orthoClippingRect = Box2.getZeroBox2();
		Box2 newVisibleRect = Box2.getZeroBox2();

		orthoClippingRect = nearOrthoClippingRectFromNearFrustumClippingRect(visibilityPlane);
		newVisibleRect = visibleRectFromNearOrthoClippingRect(orthoClippingRect);

		return newVisibleRect;

	}// end visibleRectFromNearFrustumClippingRect:

	// ========== makeProjection
	// ====================================================
	//
	// Purpose: Returns the Cocoa view visible rectangle which would result in
	// the given frustum clipping rect.
	//
	// ==============================================================================
	public void makeProjection() {
		float fieldDepth = fieldDepth();
		Box2 visibilityPlane = Box2.getZeroBox2();

		// ULTRA-IMPORTANT falseTE: this method assumes that you have already
		// made our
		// openGLContext the current context

		// Start from scratch
		if (locationMode == LocationModeT.LocationModeWalkthrough) {
			Size2 viewportSize = scroller.getMaxVisibleSizeDoc();
			float aspect_ratio = viewportSize.getWidth()
					/ viewportSize.getHeight();

			GLMatrixMath.buildFrustumMatrix(projection, -WALKTHROUGH_NEAR
					/ (zoomFactor / 100.0f), +WALKTHROUGH_NEAR
					/ (zoomFactor / 100.0f), -WALKTHROUGH_NEAR
					/ (zoomFactor / 100.0f) / aspect_ratio, +WALKTHROUGH_NEAR
					/ (zoomFactor / 100.0f) / aspect_ratio, WALKTHROUGH_NEAR,
					WALKTHROUGH_FAR);
		}

		else if (projectionMode == ProjectionModeT.ProjectionModePerspective) {
			visibilityPlane = nearFrustumClippingRectFromVisibleRect(scroller
					.getVisibleRect());

			assert (visibilityPlane.getSize().getWidth() > 0.0);
			assert (visibilityPlane.getSize().getHeight() > 0.0);

			GLMatrixMath.buildFrustumMatrix(projection,
					MatrixMath.V2BoxMinX(visibilityPlane), // left
					MatrixMath.V2BoxMaxX(visibilityPlane), // right
					MatrixMath.V2BoxMinY(visibilityPlane), // bottom
					MatrixMath.V2BoxMaxY(visibilityPlane), // top
					Math.abs(cameraDistance) - fieldDepth / 2, // near (closer
																// points are
																// clipped);
																// distance from
																// CAMERA
																// LOCATION
					Math.abs(cameraDistance) + fieldDepth / 2 // far (points
																// beyond this
																// are clipped);
																// distance from
																// CAMERA
																// LOCATION
			);
		} else {
			visibilityPlane = nearOrthoClippingRectFromVisibleRect(scroller
					.getVisibleRect());

			assert (visibilityPlane.getSize().getWidth() > 0.0);
			assert (visibilityPlane.getSize().getHeight() > 0.0);

			GLMatrixMath.buildOrthoMatrix(projection,
					MatrixMath.V2BoxMinX(visibilityPlane), // left
					MatrixMath.V2BoxMaxX(visibilityPlane), // right
					MatrixMath.V2BoxMinY(visibilityPlane), // bottom
					MatrixMath.V2BoxMaxY(visibilityPlane), // top
					Math.abs(cameraDistance) - fieldDepth / 2, // near (points
																// beyond these
																// are clipped)
					Math.abs(cameraDistance) + fieldDepth / 2); // far
		}

	}// end makeProjection

	// ========== makeModelView
	// =====================================================
	//
	// Purpose: Rebuilds the model-view matrix from the camera distance,
	// rotation and center - call this if any of these change.
	//
	// ==============================================================================
	public void makeModelView() {
		float cam_trans[] = new float[16];
		float[] center_trans = new float[16];
		float[] flip = new float[16];
		float[] temp1 = new float[16];
		float[] temp2 = new float[16];

		GLMatrixMath.buildRotationMatrix(flip, 0, 1, 0, 0);
		GLMatrixMath.buildTranslationMatrix(cam_trans, 0, 0, cameraDistance);
		GLMatrixMath.buildTranslationMatrix(center_trans,
				-rotationCenter.getX(), -rotationCenter.getY(),
				-rotationCenter.getZ());

		if (locationMode == LocationModeT.LocationModeModel) {
			GLMatrixMath.buildIdentity(temp1);
			GLMatrixMath.multMatrices(temp2, temp1, cam_trans);
			GLMatrixMath.multMatrices(temp1, temp2, orientation);
			GLMatrixMath.multMatrices(temp2, temp1, center_trans);
			GLMatrixMath.multMatrices(modelView, temp2, flip);
		} else {
			GLMatrixMath.buildIdentity(temp1);
			GLMatrixMath.multMatrices(temp2, temp1, orientation);
			GLMatrixMath.multMatrices(temp1, temp2, center_trans);
			GLMatrixMath.multMatrices(modelView, temp1, flip);
		}

	}// end makeModelView

	// ========== tickle
	// ============================================================
	//
	// Purpose: Cause the camera to recompute the document size, scrolling
	// position, and all matrices.
	//
	// Notes: This routine must be called any time the external scroller
	// properties change, so that the camera can 'react' to the change.
	//
	// ==============================================================================
	public void tickle() {
		if(mute!=0)return;
		// At init we get tickled before we are wired - avoid seg fault or NaNs.
		if (scroller != null) {
			//
			// First, recalculate the document size based on the current model
			// size, zoom, and current window size.
			// We will recalculate camera distance and rebuild the MV matrix.
			// /

			Vector3f origin = new Vector3f(new float[] { 0, 0, 0 });
			Vector2f centerPoint = MatrixMath.V2Make(
					MatrixMath.V2BoxMidX(scroller.getVisibleRect()),
					MatrixMath.V2BoxMidY(scroller.getVisibleRect()));
			Box3 newBounds = modelSize;

			if (MatrixMath.V3EqualBoxes(newBounds, Box3.getInvalidBox()) == true
					|| newBounds.getMin().getX() >= newBounds.getMax().getX()
					|| newBounds.getMin().getY() >= newBounds.getMax().getY()
					|| newBounds.getMin().getZ() >= newBounds.getMax().getZ()) {
				newBounds = MatrixMath.V3BoundsFromPoints(
						MatrixMath.V3Make(-1, -1, -1),
						MatrixMath.V3Make(1, 1, 1));
			}

			//
			// Find bounds size, based on model dimensions.
			//

			float distance1 = MatrixMath.V3DistanceBetween2Points(origin,
					newBounds.getMin());
			float distance2 = MatrixMath.V3DistanceBetween2Points(origin,
					newBounds.getMax());
			float newSize = Math.max(distance1, distance2) + 40; // 40 is just
																	// to
																	// provide a
																	// margin.

			// The canvas resizing is set to a fairly large granularity so
			// it doesn't constantly change on people.
			if (NO_ROUNDING_DOC_SIZE == 0)
				newSize = (float) (Math.ceil(newSize / 384) * 384);

			cameraDistance = -(newSize) * CAMERA_DISTANCE_FACTOR;

			makeModelView(); // New camera distance means rebuild Mv.

			//
			// Second, resize the document based on the model size and the
			// parent window size.
			// We will restore scrolling, which can get borked when the document
			// size changes.
			//

			Size2 oldFrameSize = scroller.getDocumentSize();
			Size2 newFrameSize = Size2.getZeroSize2();

			snugFrameSize = MatrixMath.V2MakeSize(newSize * 2, newSize * 2);

			if (viewportExpandsToAvailableSize == true) {
				// Make the frame either just a little bit bigger than the
				// size of the model, or the same as the scroll view,
				// whichever is larger.
				newFrameSize = MatrixMath.V2MakeSize(
						Math.max(snugFrameSize.getWidth(), scroller
								.getMaxVisibleSizeDoc().getWidth()), Math.max(
								snugFrameSize.getHeight(), scroller
										.getMaxVisibleSizeDoc().getHeight()));
			} else {
				newFrameSize = snugFrameSize;
			}
			newFrameSize.setWidth((float) Math.floor(newFrameSize.getWidth()));
			newFrameSize
					.setHeight((float) Math.floor(newFrameSize.getHeight()));

			// The canvas size changes will effectively be distributed equally
			// on all sides, because the model is always drawn in the center of
			// the canvas. So, our effective viewing center will only change by
			// half the size difference.
			centerPoint.setX(centerPoint.getX()
					+ (newFrameSize.getWidth() - oldFrameSize.getWidth()) / 2);
			centerPoint
					.setY(centerPoint.getY()
							+ (newFrameSize.getHeight() - oldFrameSize
									.getHeight()) / 2);

			if (locationMode == LocationModeT.LocationModeModel) {
				// I have only seen this on Lion and later: when we set the
				// document size the scroll point is set to something totally
				// silly. Because of this, the visible rect is empty, and the
				// entire camera calculation NaNs out.
				// To 'work around' this, we ignore the tickle that comes back
				// from the reshape that is a result of the doc frame size
				// changing; we don't need it since we're going to re-scroll and
				// redo the MV projection in the next few lines.

				mute++;
				scroller.setDocumentSize(newFrameSize);
				scrollCenterToPoint(centerPoint); // Restore centering -
													// changing the doc size
													// causes AppKit to whack
													// scrolling.
				mute--;

			} else {
				mute++;
				scroller.setDocumentSize(scroller.getMaxVisibleSizeDoc());
				mute--;
			}
			// Rebuild projection based on latest scroll data from AppKit.
			makeProjection();

		}
	}// end tickle

	// #pragma mark -
	// #pragma mark CAMERA CONTROL API
	// #pragma mark -

	// ========== setModelSize:
	// =====================================================
	//
	// Purpose: Tell the camera the new size of the model it is viewing.
	//
	// Notes: The tickle command will recompute the document size and then
	// request a scrolling update.
	//
	// ==============================================================================
	public void setModelSize(Box3 inModelSize) {
		assert (inModelSize.getMin().getX() != inModelSize.getMax().getX()
				|| inModelSize.getMin().getY() != inModelSize.getMax().getY() || inModelSize
				.getMin().getZ() != inModelSize.getMax().getZ());
		modelSize = inModelSize;
		tickle();
	}// end setModelSize:

	// ========== setRotationCenter:
	// =============================================
	//
	// Purpose: Change the rotation center to a new location, and center that
	// location.
	//
	// ==============================================================================
	public void setRotationCenter(Vector3f point) {
		if (MatrixMath.V3EqualPoints(rotationCenter, point) == false) {
			rotationCenter = point;
			makeModelView(); // Recalc model view - needed before we can scroll
								// to a given point!
			scrollModelPoint(rotationCenter, MatrixMath.V2Make(0.5f, 0.5f)); // scroll
																				// to
																				// new
																				// center
																				// (tickles
																				// itself,
																				// public
																				// API)
		}
	}// end setRotationCenter:

	// ========== setZoomPercentage:
	// ================================================
	//
	// Purpose: Change the zoom of the camera. This is called by the zoom
	// text field and zoom commands. It resizes the document and
	// tickles the camera to make everything take effect.
	//
	// ==============================================================================
	public void setZoomPercentage(float newPercentage) {

		// assert(!isnan(newPercentage));
		// assert(!isinf(newPercentage));

		if (newPercentage < 1.0f) // Hard clamp against crazy-small zoom-out.
			newPercentage = 1.0f;

		float currentZoomPercentage = zoomFactor;

		// Don't zoom if the zoom level isn't actually changing (to avoid
		// unnecessary re-draw)
		if (currentZoomPercentage == newPercentage)
			return;

		Vector2f centerPoint = MatrixMath.V2Make(
				MatrixMath.V2BoxMidX(scroller.getVisibleRect()),
				MatrixMath.V2BoxMidY(scroller.getVisibleRect()));
		Vector2f centerFraction = MatrixMath.V2Make(centerPoint.getX()
				/ scroller.getDocumentSize().getWidth(), centerPoint.getY()
				/ scroller.getDocumentSize().getHeight());

		zoomFactor = newPercentage;

		// Tell NS that sizes have changed - once we do this, we can request a
		// re-scroll.

		mute++;

		if (locationMode == LocationModeT.LocationModeWalkthrough)
			scroller.setScaleFactor(1.0f);
		else
			scroller.setScaleFactor(zoomFactor / 100.0f);

		centerPoint.setX(centerFraction.getX()
				* scroller.getDocumentSize().getWidth());
		centerPoint.setY(centerFraction.getY()
				* scroller.getDocumentSize().getHeight());

		if (locationMode != LocationModeT.LocationModeWalkthrough)
			scrollCenterToPoint(centerPoint); // Request that NS change
												// scrolling to restore
												// centering.

		mute--;
		tickle(); // Rebuild ourselves based on the new zoom, scroll, etc.
	}// end setZoomPercentage:

	// ========== setZoomPercentage:preservePoint:
	// ==================================
	//
	// Purpose: Set the zoom percentage, keeping a particular model point fixed
	// on screen.
	//
	// Notes: To do this, we figure out where on screen the model point is,
	// then we zoom, and then we re-scroll that 3-d point to its new
	// location.
	//
	// ==============================================================================
	public void setZoomPercentage(float newPercentage, Vector3f modelPoint) {
		Box2 viewport = MatrixMath.V2MakeBox(0, 0, 1, 1); // Fake view-port -
															// this gets us our
															// scaled point in
															// viewport-proportional
															// units.

		// - Near clipping plane unprojection
		Vector3f nearModelPoint = MatrixMath.V3Project(modelPoint,
				MatrixMath.Matrix4CreateFromGLMatrix4(modelView),
				MatrixMath.Matrix4CreateFromGLMatrix4(projection), viewport);

		Vector2f viewportProportion = MatrixMath.V2Make(nearModelPoint.getX(),
				nearModelPoint.getY());

		setZoomPercentage(newPercentage);
		scrollModelPoint(modelPoint, viewportProportion); // (tickles itself,
															// public API)
	}// end setZoomPercentage:preservePoint:

	// ========== scrollModelPoint:toViewportProportionalPoint:
	// =====================
	//
	// Purpose: Scroll a given 3-d point on our model to a particular location
	// on screen. The view location is a ratio of the visible portion
	// of the screen, e.g. 0.5, 0.5 is the center of the screen.
	//
	// ==============================================================================
	public void scrollModelPoint(Vector3f modelPoint, Vector2f viewportPoint) {
		if (locationMode == LocationModeT.LocationModeWalkthrough)
			return;

		Vector2f newCenter = Vector2f.getZeroVector2f();
		float zEval = 0;
		float zNear = 0;
		Matrix4 modelViewMatrix = MatrixMath
				.Matrix4CreateFromGLMatrix4(modelView);
		Vector4f transformedPoint = Vector4f.getZeroVector4f();
		Box2 newVisibleRect = Box2.getZeroBox2();
		Box2 currentClippingRect = Box2.getZeroBox2();
		Box2 newClippingRect = Box2.getZeroBox2();

		// For the camera calculation, we need effective world coordinates, not
		// model coordinates.
		transformedPoint = MatrixMath.V4MulPointByMatrix(
				MatrixMath.V4FromVector3f(modelPoint), modelViewMatrix);

		// Perspective distortion makes this more complicated. The camera is in
		// a
		// fixed position, but the frustum changes with the scrollbars. We need
		// to
		// calculate the world point we just clicked on, then derive a new
		// frustum
		// projection centered on that point.
		if (projectionMode == ProjectionModeT.ProjectionModePerspective) {
			currentClippingRect = nearFrustumClippingRectFromVisibleRect(scroller
					.getVisibleRect());

			// Consider how perspective projection works: you can think of the
			// frustum as having two
			// effects on X and Y coordinates:
			//
			// (1) it makes them get closer together as they get farther from
			// the camera. Think of train
			// tracks converging on the horizon.
			//
			// (2) it rescales the entire mess of coordinates from an arbitrary
			// range of camera X and Y
			// to -1..1 (after perspective divide) which then go to the
			// viewport.
			//
			// You can think of part 2 as happening via the
			// left/right/top/bottom inputs to the frustum.
			// The near plane is used to implement idea 1 - drawing _at_ the
			// near clip plane goes on to
			// step 2 unmodified. Anything farther than the near clip plane
			// becomes smaller.
			//
			// (The far clip plane never actually shows up in the final
			// computation of clip-space x or y.)
			//
			// So...transformedPoint is a point in eye space and we want to know
			// where in our NS document
			// it is - but our viewport is in model coordinates. IF the point
			// were on the near clip plane,
			// this would be no problem; the eye coordinates are what we want.
			//
			// So what we do is calculate that 'foreshortening ratio' - that is,
			// the fraction that makes
			// the tracks closer to the origin at farther distances. We apply
			// that to our point, finding
			// where it 'looks' to the user (farther away is closer to the
			// camera origin) and we pass that
			// without ever using step (2) to go from model units to -1..1.

			// We need the near clip plane - note that it will have a negative
			// value - since +Z looks at
			// us EVERYTHING you ever see in GL (in eye coordinates) has
			// negative Z.
			zNear = (cameraDistance + fieldDepth() / 2);

			// The ratio of 'far away' is given by near/z. Both zNear and our
			// point's Z are negative, so
			// the ratio is positive, as expected. At the near clip plane the
			// ratio is 1. Note that IF
			// we could draw in front of the near clip plane without, y'know,
			// clipping, then zEval would
			// become larger than 1 and rapidly head off to infinity as our
			// transformed point approached
			// zero. In other words, things heading at us through the near clip
			// plane would get
			// infinitely big just as they crash into our eyeballs.
			zEval = zNear / transformedPoint.getZ();

			// New center is eye coordinates of our point scaled in to account
			// for perspective.
			newCenter.setX(zEval * transformedPoint.getX());
			newCenter.setY(zEval * transformedPoint.getY());

			// Calculate a NEW frustum clipping rect centered on the clicked
			// point's
			// projection onto the near clipping plane.
			newClippingRect.setSize(currentClippingRect.getSize());
			newClippingRect.getOrigin().setX(
					newCenter.getX()
							- MatrixMath.V2BoxWidth(currentClippingRect)
							* viewportPoint.getX());
			newClippingRect.getOrigin().setY(
					newCenter.getY()
							- MatrixMath.V2BoxHeight(currentClippingRect)
							* viewportPoint.getY());

			// Reverse-derive the correct Cocoa view visible rect which will
			// result
			// in the desired clipping rect to be used.
			newVisibleRect = visibleRectFromNearFrustumClippingRect(newClippingRect);
		} else {
			currentClippingRect = nearOrthoClippingRectFromVisibleRect(scroller
					.getVisibleRect());

			// Ortho centers are trivial.
			newCenter.setX(transformedPoint.getX());
			newCenter.setY(transformedPoint.getY());

			// Calculate a clipping rect centered on the clicked point's
			// projection.
			newClippingRect.setSize(currentClippingRect.getSize());
			newClippingRect.getOrigin().setX(
					newCenter.getX()
							- MatrixMath.V2BoxWidth(currentClippingRect)
							* viewportPoint.getX());
			newClippingRect.getOrigin().setY(
					newCenter.getY()
							- MatrixMath.V2BoxHeight(currentClippingRect)
							* viewportPoint.getY());

			// Reverse-derive the correct Cocoa view visible rect which will
			// result
			// in the desired clipping rect to be used.
			newVisibleRect = visibleRectFromNearOrthoClippingRect(newClippingRect);
		}

		// Scroll to it. -makeProjection will now derive the exact frustum or
		// ortho
		// projection which will make the clicked point appear in the center.
		scroller.setScrollOrigin(newVisibleRect.getOrigin());
		tickle(); // Tickle to rebuild all matrices based on external change.

	}// end scrollModelPoint:toViewportProportionalPoint:

	// ========== setViewingAngle:
	// ==================================================
	//
	// Purpose: Change the viewing angle to a specific angle.
	//
	// ==============================================================================
	public void setViewingAngle(Vector3f newAngle) {
		float gl_angle[] = new float[16];
		float[] gl_flip = new float[16];
		Matrix4 angle = MatrixMath.Matrix4RotateModelview(
				Matrix4.getIdentityMatrix4(), newAngle);

		MatrixMath.Matrix4GetGLMatrix4(angle, gl_angle);
		GLMatrixMath.buildRotationMatrix(gl_flip, 180, 1, 0, 0);
		GLMatrixMath.multMatrices(orientation, gl_flip, gl_angle);

		makeModelView();

	}// end setViewingAngle:

	// ========== setProjectionMode:
	// ================================================
	//
	// Purpose: Change projection modes.
	//
	// Notes: This is a special-case - normally we'd tickle, but we only need
	// to make the projection matrix over because right now all
	// projection modes keep the same document size.
	//
	// ==============================================================================
	public void setProjectionMode(ProjectionModeT newProjectionMode) {
		projectionMode = newProjectionMode;
		makeProjection(); // This doesn't need a full tickle because proj mode
							// doesn't change the doc size.

	}// end setProjectionMode:

	// ========== setLocationMode:
	// ================================================
	//
	// Purpose: Change Location modes.
	//
	// ==============================================================================
	public void setLocationMode(LocationModeT newLocationMode) {
		if (locationMode != newLocationMode) {
			locationMode = newLocationMode;

			// Tell NS that sizes have changed - once we do this, we can request
			// a re-scroll.
			if (locationMode == LocationModeT.LocationModeWalkthrough)
				scroller.setScaleFactor(1.0f);
			else
				scroller.setScaleFactor(zoomFactor / 100.0f);

			tickle();
		}

	}// end setProjectionMode:

	// ========== rotationDragged
	// ===================================================
	//
	// Purpose: Rotate the camera based on a 2-d drag vector.
	//
	// ==============================================================================
	public void rotationDragged(Vector2f viewDirection) {
		float deltaX = viewDirection.getX();
		float deltaY = -viewDirection.getY(); // Apple's delta is backwards, for
												// some reason.

		// Get the percentage of the window we have swept over. Since half the
		// window represents 180 degrees of rotation, we will eventually
		// multiply this percentage by 180 to figure out how much to rotate.
		float percentDragX = deltaX / scroller.getDocumentSize().getWidth();
		float percentDragY = deltaY / scroller.getDocumentSize().getHeight();

		// Remember, dragging on y means rotating about x.
		float rotationAboutY = +(percentDragX * 180);
		float rotationAboutX = -(percentDragY * 180); // multiply by -1,
		// as we need to convert our drag into a proper rotation
		// direction. See notes in function header.

		if (USE_TURNTABLE) {
			Vector3f view_now = viewingAngle();
			if (view_now.getX() * view_now.getY() * view_now.getZ() < 0.0)
				rotationAboutY = -rotationAboutY;
		}

		// Get the current transformation matrix. By using its inverse, we can
		// convert projection-coordinates back to the model coordinates they
		// are displaying.
		Matrix4 inversed = MatrixMath.Matrix4Invert(MatrixMath
				.Matrix4CreateFromGLMatrix4(getModelView()));

		// clear any translation resulting from a rotation center
		float[][] element = inversed.getElement();
		element[3][0] = 0;
		element[3][1] = 0;
		element[3][2] = 0;

		// Now we will convert what appears to be the vertical and horizontal
		// axes into the actual model vectors they represent.
		Vector4f vectorX = new Vector4f(1, 0, 0, 1); // unit vector i along
														// x-axis.
		Vector4f vectorY = new Vector4f(0, 1, 0, 1); // unit vector j along
														// y-axis.
		Vector4f transformedVectorX;
		Vector4f transformedVectorY;

		// We do this conversion from screen to model coordinates by multiplying
		// our screen points by the modelview matrix inverse. That has the
		// effect of "undoing" the model matrix on the screen point, leaving us
		// a model point.
		transformedVectorX = MatrixMath.V4MulPointByMatrix(vectorX, inversed);
		transformedVectorY = MatrixMath.V4MulPointByMatrix(vectorY, inversed);

		if (USE_TURNTABLE) {
			rotationAboutY = -rotationAboutY;
			transformedVectorY = vectorY;
		}

		// Now rotate the model around the visual "up" and "down" directions.

		GLMatrixMath.applyRotationMatrix(orientation, rotationAboutX,
				transformedVectorX.getX(), transformedVectorX.getY(),
				transformedVectorX.getZ());
		GLMatrixMath.applyRotationMatrix(orientation, rotationAboutY,
				transformedVectorY.getX(), transformedVectorY.getY(),
				transformedVectorY.getZ());
		makeModelView();

	}// end rotationDragged

	// ========== rotateByDegrees:
	// ==================================================
	//
	// Purpose: Rotate the camera by a fixed angle - used by the trackpad twist
	// gesture, this rotates aronud the screen Y axis.
	//
	// ==============================================================================
	public void rotateByDegrees(float angle) {
		GLMatrixMath.applyRotationMatrix(orientation, angle, 0, -1, 0);
		makeModelView();

	}// end rotateByDegrees:
}
