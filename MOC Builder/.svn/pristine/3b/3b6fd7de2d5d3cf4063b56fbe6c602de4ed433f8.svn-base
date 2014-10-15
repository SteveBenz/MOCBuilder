package Command;

import java.util.ArrayList;

import Common.Box2;
import Common.Box3;
import Common.Matrix3;
import Common.Matrix4;
import Common.Vector3f;
import LDraw.Support.LDrawDirective;
import LDraw.Support.MatrixMath;
import LDraw.Support.type.CacheFlagsT;

public class LDrawDrawableElement extends LDrawDirective implements
		LDrawColorable, LDrawMovableDirective {

	/**
	 * @uml.property  name="color"
	 * @uml.associationEnd  
	 */
	protected LDrawColor color;
	/**
	 * @uml.property  name="hidden"
	 */
	protected boolean hidden; // YES if we don't draw this.
	
	// ========== init
	// ==============================================================
	//
	// Purpose: Create a fresh object. This is the default initializer.
	//
	// ==============================================================================
	public LDrawDrawableElement init() {
		super.init();

		hidden = false;

		return this;

	}// end init
	
	// ========== boundingBox3
	// ======================================================
	//
	// Purpose: Returns the minimum and maximum points of the box which
	// perfectly contains this object.
	//
	// ==============================================================================
	public Box3 boundingBox3() {
		Box3 bounds = Box3.getInvalidBox();

		// You shouldn't be here. Look in a subclass.

		return bounds;

	}// end boundingBox3

	// ========== projectedBoundingBoxWithModelView:projection:view:
	// ================
	//
	// Purpose: Returns the 2D projection (you should ignore the z) of the
	// object's bounds.
	//
	// ==============================================================================
	public Box3 projectedBoundingBoxWithModelView(Matrix4 modelView,
			Matrix4 projection, Box2 viewport) {
		Box3 bounds = boundingBox3();
		Vector3f windowPoint = Vector3f.getZeroVector3f();
		Box3 projectedBounds = Box3.getInvalidBox();

		if (MatrixMath.V3EqualBoxes(bounds, Box3.getInvalidBox()) == false) {
			// front lower left
			windowPoint = MatrixMath.V3Project(bounds.getMin(), modelView,
					projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);

			// front lower right
			windowPoint = MatrixMath.V3Project(MatrixMath.V3Make(
					bounds.getMax().getX(), bounds.getMin().getY(), bounds.getMin().getZ()),
					modelView, projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);

			// front upper right
			windowPoint = MatrixMath.V3Project(MatrixMath.V3Make(
					bounds.getMax().getX(), bounds.getMax().getY(), bounds.getMin().getZ()),
					modelView, projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);

			// front upper left
			windowPoint = MatrixMath.V3Project(MatrixMath.V3Make(
					bounds.getMin().getX(), bounds.getMax().getY(), bounds.getMin().getZ()),
					modelView, projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);

			// back lower left
			windowPoint = MatrixMath.V3Project(MatrixMath.V3Make(
					bounds.getMin().getX(), bounds.getMin().getY(), bounds.getMax().getZ()),
					modelView, projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);

			// back lower right
			windowPoint = MatrixMath.V3Project(MatrixMath.V3Make(
					bounds.getMax().getX(), bounds.getMin().getY(), bounds.getMax().getZ()),
					modelView, projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);

			// back upper right
			windowPoint = MatrixMath.V3Project(MatrixMath.V3Make(
					bounds.getMax().getX(), bounds.getMax().getY(), bounds.getMax().getZ()),
					modelView, projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);

			// back upper left
			windowPoint = MatrixMath.V3Project(MatrixMath.V3Make(
					bounds.getMin().getX(), bounds.getMax().getY(), bounds.getMax().getZ()),
					modelView, projection, viewport);
			projectedBounds = MatrixMath.V3UnionBoxAndPoint(projectedBounds,
					windowPoint);
		}

		return projectedBounds;

	}// end projectedBoundingBoxWithModelView:projection:view:

	// ========== isHidden
	// ==========================================================
	//
	// Purpose: Returns whether this element will be drawn or not.
	//
	// ==============================================================================
	/**
	 * @return
	 * @uml.property  name="hidden"
	 */
	public boolean isHidden() {
		return hidden;

	}// end isHidden

	// ========== LDrawColor
	// ========================================================
	//
	// Purpose: Returns the LDraw color code of the receiver.
	//
	// ==============================================================================
	public LDrawColor getLDrawColor() {
		return color;

	}// end LDrawColor

	// ========== position
	// ==========================================================
	//
	// Purpose: Returns some position for the element. This is used by
	// drag-and-drop. This is not necessarily human-usable information.
	//
	// ==============================================================================
	public Vector3f position() {
		return Vector3f.getZeroVector3f();

	}// end position

	// #pragma mark -

	// ========== setHidden:
	// ========================================================
	//
	// Purpose: Sets whether this part will be drawn, or whether it will be
	// skipped during drawing. This setting only affects drawing;
	// hidden parts will always be written out. Also, note that
	// hiddenness is a temporary state; it is not saved and restored.
	//
	// ==============================================================================
	/**
	 * @param flag
	 * @uml.property  name="hidden"
	 */
	public void setHidden(boolean flag) {
		if (hidden != flag) {
			hidden = flag;
			if(enclosingDirective()!=null)
			enclosingDirective().setVertexesNeedRebuilding();
			invalCache(CacheFlagsT.CacheFlagBounds);
			invalCache(CacheFlagsT.DisplayList);
		}
	}// end setHidden:

	// ========== setLDrawColor:
	// ====================================================
	//
	// Purpose: Sets the color of this element.
	//
	// ==============================================================================
	public void setLDrawColor(LDrawColor newColor) {
		color = newColor;
		invalCache(CacheFlagsT.DisplayList); // Needed to force anyone who is
												// cached to recompute the new
												// DL with possibly baked color!

	}// end setLDrawColor:

	// #pragma mark -
	// #pragma mark MOVEMENT
	// #pragma mark -

	// ========== displacementForNudge:
	// =============================================
	//
	// Purpose: Returns the amount by which the element wants to move, given a
	// "nudge" in the specified direction. A "nudge" is generated by
	// pressing the arrow keys. If they feel it appropriate, subclasses
	// are perfectly welcome to scale this value. (LDrawParts do this.)
	//
	// ==============================================================================
	public Vector3f displacementForNudge(Vector3f nudgeVector) {
		// possibly refined by subclasses.
		return nudgeVector;

	}// end displacementForNudge:

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
		// implemented by subclasses.

	}// end moveBy:

	// ========== position:snappedToGrid:
	// ===========================================
	//
	// Purpose: Orients position at discrete points separated by the given grid
	// spacing.
	//
	// Notes: This method may be overridden by subclasses to provide more
	// intelligent grid alignment.
	//
	// This method is provided mainly as a service to drag-and-drop.
	// In the case of LDrawParts, you should generally avoid this
	// method in favor of
	// -[LDrawPart components:snappedToGrid:minimumAngle:].
	//
	// ==============================================================================
	public Vector3f position(Vector3f position, float gridSpacing) {
		position.setX(Math.round(position.getX() / gridSpacing) * gridSpacing);
		position.setY(Math.round(position.getY() / gridSpacing) * gridSpacing);
		position.setZ(Math.round(position.getZ() / gridSpacing) * gridSpacing);

		return position;

	}// end position:snappedToGrid:

	// #pragma mark -
	// #pragma mark UTILITIES
	// #pragma mark -

	// ========== flattenIntoLines:triangles:quadrilaterals:other:currentColor:
	// =====
	//
	// Purpose: Appends the directive into the appropriate container.
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
			ArrayList<LDrawQuadrilateral> quadrilaterals,
			ArrayList<LDrawDirective> everythingElse, LDrawColor parentColor,
			Matrix4 transform, Matrix3 normalTransform, boolean recursive) {
		// Resolve the correct color and set it. Our subclasses will be
		// responsible
		// for then adding themselves to the correct list.

		// Figure out the actual color of the directive.

		if (color.colorCode() == LDrawColorT.LDrawCurrentColor) {
			if (parentColor.colorCode() == LDrawColorT.LDrawCurrentColor) {
				// just add
			} else {
				// set directiveCopy to parent color
				setLDrawColor(parentColor);
			}
		} else if (color.colorCode() == LDrawColorT.LDrawEdgeColor) {
			if (parentColor.colorCode() == LDrawColorT.LDrawCurrentColor) {
				// just add
			} else {
				// set directiveCopy to compliment color
				LDrawColor complimentColor = parentColor.complimentColor();

				setLDrawColor(complimentColor);

				// then add.
			}
		} else {
			// This directive is already explicitly colored. Just add.
		}

	}// end flattenIntoLines:triangles:quadrilaterals:other:currentColor:

}
