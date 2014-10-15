package LDraw.Support;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.media.opengl.GL2;

import Command.LDrawColor;
import Command.LDrawDrawableElement;
import Common.Box2;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;
import Common.Vector3f;
import Renderer.ILDrawRenderer;

public class LDrawDragHandle extends LDrawDrawableElement {
	// Shared tag to draw the standard drag handle sphere
	private static IntBuffer vaoTag;
	private static IntBuffer vboTag;
	private static IntBuffer vboVertexCount;

	private static final float HandleDiameter = 7.0f;

	int tag;
	Vector3f position;
	Vector3f initialPosition;

	ILDrawDragHandler target;
	SelT action;
	
	public LDrawDragHandle(){
		vaoTag = IntBuffer.allocate(1);
		vboTag = IntBuffer.allocate(1);
		vboVertexCount = IntBuffer.allocate(1);
		
		position = Vector3f.getZeroVector3f();
		initialPosition = Vector3f.getZeroVector3f();		
	}

	// ========== initWithTag:position:
	// =============================================
	//
	// Purpose: Initialize the object with a tag to identify what vertex it is
	// connected to.
	//
	// ==============================================================================
	public LDrawDragHandle initWithTag(int tagIn, Vector3f positionIn) {
		super.init();

		tag = tagIn;
		position = positionIn;
		initialPosition = positionIn;

		//makeSphereWithLongitudinalCount(gl2, 8, 8);

		return this;

	}// end initWithTag:position:

	// ========== initialPosition
	// ===================================================
	//
	// Purpose: Returns the coordinate this handle what at when initialized.
	//
	// ==============================================================================
	public Vector3f initialPosition() {
		return initialPosition;
	}

	// ========== isSelected
	// ========================================================
	//
	// Purpose: Drag handles only show up when their associated primitive is
	// selected, so we always report being selected. This will make us
	// more transparent to the view selection code.
	//
	// ==============================================================================
	public boolean isSelected() {
		return true;
	}

	// ========== position
	// ==========================================================
	//
	// Purpose: Returns the world-coordinate location of the handle.
	//
	// ==============================================================================
	public Vector3f position() {
		return position;
	}

	// ========== tag
	// ===============================================================
	//
	// Purpose: Returns the identifier for this handle. Used to associate the
	// handle with a vertex.
	//
	// ==============================================================================
	public int tag() {
		return tag;
	}

	// ========== target
	// ============================================================
	//
	// Purpose: Returns the object which owns the drag handle.
	//
	// ==============================================================================
	public ILDrawDragHandler target() {
		return target;
	}

	// ========== setAction:
	// ========================================================
	//
	// Purpose: Sets the method to invoke when the handle is repositioned.
	//
	// ==============================================================================
	public void setAction(SelT actionIn) {
		action = actionIn;
	}

	// ========== setPosition:updateTarget:
	// =========================================
	//
	// Purpose: Updates the current handle position, and triggers the action if
	// update flag is true.
	//
	// ==============================================================================
	public void setPosition(Vector3f positionIn, boolean update) {
		position = positionIn;

		if (update) {
			if (action == SelT.DragHandleChanged)
				target.dragHandleChanged(this);
		}
	}// end setPosition:updateTarget:

	// ========== setTarget:
	// ========================================================
	//
	// Purpose: Sets the object to invoke the action on.
	//
	// ==============================================================================
	public void setTarget(ILDrawDragHandler sender) {
		target = sender;
	}

	// ========== draw:viewScale:parentColor:
	// =======================================
	//
	// Purpose: Draw the drag handle.
	//
	// ==============================================================================
	public void draw(GL2 gl2, HashMap<Integer, Boolean> optionsMask, float scaleFactor,
			LDrawColor parentColor) {
		float handleScale = 0.0f;
		float drawRadius = 0.0f;

		handleScale = 1.0f / scaleFactor;
		drawRadius = HandleDiameter / 2 * handleScale;
		gl2.glDisable(GL2.GL_TEXTURE_2D);
		gl2.glPushMatrix();
		{
			gl2.glTranslatef(position.getX(), position.getY(), position.getZ());
			gl2.glScalef(drawRadius, drawRadius, drawRadius);

			gl2.glBindVertexArray(vaoTag.get(0));
			gl2.glDrawArrays(GL2.GL_TRIANGLE_STRIP, 0, vboVertexCount.get(0));
			gl2.glBindVertexArray(0); // Failing to unbind can cause bizarre
										// crashes if other VAOs are in display
										// lists
		}
		gl2.glPopMatrix();
		gl2.glEnable(GL2.GL_TEXTURE_2D);

	}// end draw:viewScale:parentColor:

	// ========== drawSelf:
	// ===========================================================
	//
	// Purpose: Draw this directive and its subdirectives by calling APIs on
	// the passed in renderer, then calling drawSelf on children.
	//
	// Notes: Drag handles don't use DLs - they simply push their pos
	// to the renderer immediately.
	//
	// ================================================================================
	public void drawSelf(ILDrawRenderer renderer) {
		float xyz[] = { position.getX(), position.getY(), position.getZ() };
		renderer.drawDragHandle(xyz, HandleDiameter / 2);
	}// end drawSelf:

	// ========== hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Tests the directive for an intersection between the pickRay and
	// spherical drag handle.
	//
	// ==============================================================================
	public void hitTest(Ray3 pickRay, Matrix4 transform, float scaleFactor,
			boolean boundsOnly, LDrawDirective creditObject,
			HashMap<LDrawDirective, Float> hits) {
		float handleScale = 0.0f;
		float drawRadius = 0.0f;
		FloatBuffer intersectDepth = FloatBuffer.allocate(1);
		boolean intersects = false;

		handleScale = 1.0f / scaleFactor;
		drawRadius = HandleDiameter / 2 * handleScale;
		drawRadius *= 1.5; // allow a little fudge

		intersects = MatrixMath.V3RayIntersectsSphere(pickRay, position,
				drawRadius, intersectDepth);

		if (intersects) {
			LDrawUtilities.registerHitForObject(this, intersectDepth,
					creditObject, hits);
		}
	}// end hitTest:transform:viewScale:boundsOnly:creditObject:hits:

	// ==========
	// depthTest:inBox:transform:creditObject:bestObject:bestDepth:=======
	//
	// Purpose: depthTest finds the closest primitive (in screen space)
	// overlapping a given point, as well as its device coordinate
	// depth.
	//
	// ==============================================================================
	public void depthTest(Vector2f pt, Box2 bounds, Matrix4 transform,
			LDrawDirective creditObject, ArrayList<LDrawDirective> bestObject,
			FloatBuffer bestDepth) {
		Vector3f v1 = MatrixMath.V3MulPointByProjMatrix(position, transform);
		if (MatrixMath.V2BoxContains(bounds,
				MatrixMath.V2Make(v1.getX(), v1.getY()))) {
			if (v1.getZ() <= bestDepth.get(0)) {
				bestDepth.put(v1.getZ());
				if (creditObject != null)
					bestObject.add(creditObject);
				else
					bestObject.add(this);
			}
		}
	}// end depthTest:inBox:transform:creditObject:bestObject:bestDepth:

	// ========== moveBy:
	// ===========================================================
	//
	// Purpose: Displace the receiver by the given amounts in each direction.
	// The amounts in moveVector or relative to the element's current
	// location.
	//
	// Subclasses are required to move by exactly this amount. Any
	// adjustments they wish to make need to be returned in
	// -displacementForNudge:.
	//
	// ==============================================================================
	public void moveBy(Vector3f moveVector) {
		Vector3f newPosition = MatrixMath.V3Add(position, moveVector);

		setPosition(newPosition, true);

	}// end moveBy:
}
