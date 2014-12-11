package Command;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.media.opengl.GL2;
import javax.swing.undo.UndoManager;

import Common.Box2;
import Common.Box3;
import Common.Matrix3;
import Common.Matrix4;
import Common.Ray3;
import Common.Vector2f;
import Common.Vector3f;
import Common.Vector4f;
import LDraw.Support.DispatchGroup;
import LDraw.Support.GLMatrixMath;
import LDraw.Support.ILDrawDragHandler;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawDragHandle;
import LDraw.Support.LDrawUtilities;
import LDraw.Support.MatrixMath;
import LDraw.Support.Range;
import LDraw.Support.SelT;
import LDraw.Support.type.CacheFlagsT;
import Renderer.ILDrawCollector;
import Renderer.ILDrawRenderer;
import Renderer.LDrawRenderColorT;

//==============================================================================
//
//File:		LDrawTriangle.m
//
//Purpose:		Triangle command.
//				Draws a filled triangle between three points.
//
//				Line format:
//				3 colour x1 y1 z1 x2 y2 z2 x3 y3 z3 
//
//				where
//
//				* colour is a colour code: 0-15, 16, 24, 32-47, 256-511
//				* x1, y1, z1 is the position of the first point
//				* x2, y2, z2 is the position of the second point
//				* x3, y3, z3 is the position of the third point 
//
//Created by Allen Smith on 2/19/05.
//Copyright (c) 2005. All rights reserved.
//==============================================================================

public class LDrawTriangle extends LDrawDrawableElement implements ILDrawDragHandler{
	/**
	 * @uml.property  name="vertex1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f vertex1= Vector3f.getZeroVector3f();
	/**
	 * @uml.property  name="vertex2"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f vertex2= Vector3f.getZeroVector3f();
	/**
	 * @uml.property  name="vertex3"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f vertex3= Vector3f.getZeroVector3f();

	/**
	 * @uml.property  name="normal"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f normal= Vector3f.getZeroVector3f();

	/**
	 * @uml.property  name="dragHandles"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="LDraw.Support.LDrawDragHandle"
	 */
	ArrayList<LDrawDragHandle> dragHandles;

	// ========== initWithLines:inRange:parentGroup:
	// ================================
	//
	// Purpose: Returns a triangle initialized from line of LDraw code beginning
	// at the given range.
	//
	// directive should have the format:
	//
	// 3 colour x1 y1 z1 x2 y2 z2 x3 y3 z3
	//
	// ==============================================================================
	public LDrawTriangle initWithLines(ArrayList<String> lines,
				 Range range
			 ,DispatchGroup parentGroup)
	{
		String    workingLine    = lines.get(range.getLocation());
		String    parsedField    = null;
		Vector3f      workingVertex   = Vector3f.getZeroVector3f();
		LDrawColor  parsedColor    = null;
		
		try {
			super.initWithLines(lines, range,parentGroup);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//A malformed part could easily cause a string indexing error, which would 
		// raise an exception. We don't want this to happen here.
		try
		{
			//Read in the line code and advance past it.
			StringTokenizer strTokenizer = new StringTokenizer(workingLine);
			parsedField = strTokenizer.nextToken();
			//Only attempt to create the part if this is a valid line.
			if(Integer.parseInt(parsedField) == 3)
			{
				//Read in the color code.
				// (color)
				parsedField = strTokenizer.nextToken();
				parsedColor = LDrawUtilities.parseColorFromField(parsedField);
				setLDrawColor(parsedColor);
				
				//Read Vertex 1.
				// (x1)
				parsedField = strTokenizer.nextToken();
				workingVertex.setX(Float.parseFloat(parsedField));
				// (y1)
				parsedField = strTokenizer.nextToken();
				workingVertex.setY(Float.parseFloat(parsedField));
				// (z1)
				parsedField = strTokenizer.nextToken();
				workingVertex.setZ(Float.parseFloat(parsedField));
				
				setVertex1(workingVertex);
					
				//Read Vertex 2.
				// (x2)
				parsedField = strTokenizer.nextToken();
				workingVertex.setX(Float.parseFloat(parsedField));
				// (y2)
				parsedField = strTokenizer.nextToken();
				workingVertex.setY(Float.parseFloat(parsedField));
				// (z2)
				parsedField = strTokenizer.nextToken();
				workingVertex.setZ(Float.parseFloat(parsedField));
				
				setVertex2(workingVertex);
				
				//Read Vertex 3.
				// (x3)
				parsedField = strTokenizer.nextToken();
				workingVertex.setX(Float.parseFloat(parsedField));
				// (y3)
				parsedField = strTokenizer.nextToken();
				workingVertex.setY(Float.parseFloat(parsedField));
				// (z3)
				parsedField = strTokenizer.nextToken();
				workingVertex.setZ(Float.parseFloat(parsedField));
				
				setVertex3(workingVertex);
			}
			else
				throw new Exception("BricksmithParseException: "+"Bad line syntax");
		}
		catch(Exception e)
		{	
			System.out.println(String.format("the triangle primitive %s was fatally invalid", lines.get(range.getLocation())));
			System.out.println(String.format(" raised exception %s", e.getMessage()));
		}
		
		return this;
		
	}//end initWithLines:inRange:

	
	// ========== drawElement:viewScale:withColor:
	// ==================================
	//
	// Purpose: Draws the graphic of the element represented. This call is a
	// subroutine of -draw: in LDrawDrawableElement.
	//
	// ==============================================================================
	public void drawElement(GL2 gl2, HashMap<Integer, Boolean> optionsMask,
			float scaleFactor, LDrawColor drawingColor) {
		if (dragHandles!=null) {
			if (dragHandles != null) {
				for (LDrawDragHandle handle : dragHandles) {
					handle.draw(gl2, optionsMask, scaleFactor, drawingColor);
				}
			}
		}

	}// end drawElement:parentColor:

	// ========== drawSelf:
	// ===========================================================
	//
	// Purpose: Draw this directive and its subdirectives by calling APIs on
	// the passed in renderer, then calling drawSelf on children.
	//
	// Notes: Triangles use this message to get their drag handles drawn if
	// needed. They do not draw their actual GL primitive because that
	// has already been "collected" by some parent capable of
	// accumulating a mesh.
	//
	// ================================================================================
	public void drawSelf(GL2 gl2, ILDrawRenderer renderer) {
		revalCache(CacheFlagsT.DisplayList);
		if (hidden == false) {
			if (dragHandles != null) {
				for (LDrawDragHandle handle : dragHandles) {
					handle.drawSelf(gl2, renderer);
				}
			}
		}
	}// end drawSelf:

	// ========== collectSelf:
	// ========================================================
	//
	// Purpose: Collect this is called on each directive by its parents to
	// accumulate _mesh_ data into a display list for later drawing.
	// The collector protocol passed in is some object capable of
	// remembering the collectable data.
	//
	// Real GL primitives participate by passing their color and
	// geometry data to the collector.
	//
	// ================================================================================
	public void collectSelf(ILDrawCollector renderer) {
		// We must mark our DL as valid - otherwise we will not invalidate our
		// DL when edited, and if we don't do that, we won't pass the message
		// to our parents that our DL is invalid. This passing the invalid DL up
		// is what PRIMES our parent model to rebuild DLs as needed.
		revalCache(CacheFlagsT.DisplayList);
		if(color==null)return;
		if (hidden == false) {
			float v[] = { vertex1.getX(), vertex1.getY(), vertex1.getZ(),
					vertex2.getX(), vertex2.getY(), vertex2.getZ(),
					vertex3.getX(), vertex3.getY(), vertex3.getZ() };

			float n[] = { normal.getX(), normal.getY(), normal.getZ() };
			if (color.getColorCode() == LDrawColorT.LDrawCurrentColor)
				renderer.drawTri(v, n, LDrawRenderColorT.LDrawRenderCurrentColor.getValue());
			else if (color.getColorCode() == LDrawColorT.LDrawEdgeColor)
				renderer.drawTri(v, n,
						LDrawRenderColorT.LDrawRenderComplimentColor.getValue());
			else {
				float rgba[] = new float[4];
				color.getColorRGBA(rgba);
				renderer.drawTri(v, n, rgba);
			}
		}
	}// end collectSelf:

	
	public void getRange( Matrix4 transform, float[] range )
	{
		if(hidden == false)
		{
			Vector3f     worldVertex1    = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
			Vector3f     worldVertex2    = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);
			Vector3f	 worldVertex3    = MatrixMath.V3MulPointByProjMatrix(vertex3, transform);

			compareRange( range, worldVertex1 );
			compareRange( range, worldVertex2 );
			compareRange( range, worldVertex3 );

			if(dragHandles!=null)
			{				
				for(LDrawDragHandle handle : dragHandles)
				{
					handle.getRange(transform, range);
				}
			}
		}
	}
	
	// ========== hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	// =======
	//
	// Purpose: Tests the directive and any of its children for intersections
	// between the pickRay and the directive's drawn content.
	//
	// ==============================================================================
	
	public void hitTest(Ray3 pickRay, Matrix4 transform, LDrawDirective creditObject, HashMap<LDrawDirective, Float> hits)
	{
		if(hidden == false)
		{
			Vector3f     worldVertex1    = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
			Vector3f     worldVertex2    = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);
			Vector3f worldVertex3    = MatrixMath.V3MulPointByProjMatrix(vertex3, transform);
			
			FloatBuffer       intersectDepth  = FloatBuffer.allocate(1);
			boolean    intersects      = false;
			
			intersects = MatrixMath.V3RayIntersectsTriangle(pickRay,
												 worldVertex1, worldVertex2, worldVertex3,
												 intersectDepth, null);
			if(intersects)
			{
				LDrawUtilities.registerHitForObject(this, intersectDepth, creditObject, hits);
			}

			if(dragHandles!=null)
			{				
				for(LDrawDragHandle handle : dragHandles)
				{
					handle.hitTest(pickRay, transform, null, hits);
				}
			}
		}
	}//end hitTest:transform:viewScale:boundsOnly:creditObject:hits:

	// ========== boxTest:transform:boundsOnly:creditObject:hits:
	// ===================
	//
	// Purpose: Check for intersections with screen-space geometry.
	//
	// ==============================================================================
	public boolean boxTest(Box2 bounds, Matrix4 transform, boolean boundsOnly,
			LDrawDirective creditObject, TreeSet<LDrawDirective> hits) 
	{
		if(hidden == false)
		{
			Vector4f clipVertex1    = MatrixMath.V4MulPointByMatrix(MatrixMath.V4FromVector3f(vertex1), transform);
			Vector4f clipVertex2    = MatrixMath.V4MulPointByMatrix(MatrixMath.V4FromVector3f(vertex2), transform);
			Vector4f clipVertex3    = MatrixMath.V4MulPointByMatrix(MatrixMath.V4FromVector3f(vertex3), transform);

			float h_tri[] = { 
							clipVertex1.getX(), clipVertex1.getY(), clipVertex1.getZ(), clipVertex1.getW(),
							clipVertex2.getX(), clipVertex2.getY(), clipVertex2.getZ(), clipVertex2.getW(),
							clipVertex3.getX(), clipVertex3.getY(), clipVertex3.getZ(), clipVertex3.getW() };
							
			float ndc_tris[] = new float[18];			
			int triCount = GLMatrixMath.clipTriangle(h_tri,ndc_tris);
			int i;
			for(i = 0; i < triCount; ++i)
			{
				Vector2f	tri[] = { 
					MatrixMath.V2Make(ndc_tris[i*9+0],ndc_tris[i*9+1]),
					MatrixMath.V2Make(ndc_tris[i*9+3],ndc_tris[i*9+4]),
					MatrixMath.V2Make(ndc_tris[i*9+6],ndc_tris[i*9+7])
				};

				if(MatrixMath.V2BoxIntersectsPolygon(bounds, tri, 3))
				{
					LDrawUtilities.registerHitForObject(this, creditObject, hits);
					if(creditObject!=null) 
						return true;
				}
			}
		}
		return false;
	}//end boxTest:transform:boundsOnly:creditObject:hits:

	// ==========
	// depthTest:inBox:transform:creditObject:bestObject:bestDepth:=======
	//
	// Purpose: depthTest finds the closest primitive (in screen space)
	// overlapping a given point, as well as its device coordinate
	// depth.
	//
	// ==============================================================================
	public void depthTest(Vector2f pt, Box2 bounds, Matrix4 transform,
			LDrawDirective creditObject, ArrayList<LDrawDirective> bestObject, FloatBuffer bestDepth)
	{
		if(hidden == false)
		{
			Vector4f clipVertex1    = MatrixMath.V4MulPointByMatrix(MatrixMath.V4FromVector3f(vertex1), transform);
			Vector4f clipVertex2    =  MatrixMath.V4MulPointByMatrix(MatrixMath.V4FromVector3f(vertex2), transform);
			Vector4f clipVertex3    =  MatrixMath.V4MulPointByMatrix(MatrixMath.V4FromVector3f(vertex3), transform);
			
			Vector3f probe = new Vector3f(pt.getX() , pt.getY(), bestDepth.get(0));
			
			float h_tri[] = { 
							clipVertex1.getX(), clipVertex1.getY(), clipVertex1.getZ(), clipVertex1.getW(),
							clipVertex2.getX(), clipVertex2.getY(), clipVertex2.getZ(), clipVertex2.getW(),
							clipVertex3.getX(), clipVertex3.getY(), clipVertex3.getZ(), clipVertex3.getW() };
							
			float ndc_tris[] = new float[18];			
			int triCount = GLMatrixMath.clipTriangle(h_tri,ndc_tris);
			int i;
			for(i = 0; i < triCount; ++i)
			{
				Vector3f	ndcVertex1 = MatrixMath.V3Make(ndc_tris[i*9+0],ndc_tris[i*9+1],ndc_tris[i*9+2]);
				Vector3f	ndcVertex2 = MatrixMath.V3Make(ndc_tris[i*9+3],ndc_tris[i*9+4],ndc_tris[i*9+5]);
				Vector3f	ndcVertex3 = MatrixMath.V3Make(ndc_tris[i*9+6],ndc_tris[i*9+7],ndc_tris[i*9+8]);

				if(MatrixMath.DepthOnTriangle(ndcVertex1,ndcVertex2,ndcVertex3,probe))
				{
					if(probe.getZ() <= bestDepth.get(0))
					{
						bestDepth.put(0, probe.getZ());
						bestObject.add((LDrawDirective) (creditObject!=null ? creditObject : this));
					}
				}			
			}			
			
			if(dragHandles!=null)
			{
				for(LDrawDragHandle handle : dragHandles)
				{					
					handle.depthTest(pt, bounds, transform, creditObject, bestObject, bestDepth);
				}
			}
			
		}
	}//end depthTest:inBox:transform:creditObject:bestObject:bestDepth:

	// ========== write
	// =============================================================
	//
	// Purpose: Returns a line that can be written out to a file.
	// Line format:
	// 3 colour x1 y1 z1 x2 y2 z2 x3 y3 z3
	//
	// ==============================================================================
	public String write() {
		return String.format(Locale.US,"3 %s %s %s %s %s %s %s %s %s %s",
				LDrawUtilities.outputStringForColor(color),

				LDrawUtilities.outputStringForFloat(vertex1.getX()),
				LDrawUtilities.outputStringForFloat(vertex1.getY()),
				LDrawUtilities.outputStringForFloat(vertex1.getZ()),

				LDrawUtilities.outputStringForFloat(vertex2.getX()),
				LDrawUtilities.outputStringForFloat(vertex2.getY()),
				LDrawUtilities.outputStringForFloat(vertex2.getZ()),

				LDrawUtilities.outputStringForFloat(vertex3.getX()),
				LDrawUtilities.outputStringForFloat(vertex3.getY()),
				LDrawUtilities.outputStringForFloat(vertex3.getZ())

		);
	}// end write

	// ========== browsingDescription
	// ===============================================
	//
	// Purpose: Returns a representation of the directive as a short string
	// which can be presented to the user.
	//
	// ==============================================================================
	public String browsingDescription() {
		return "Triangle";

	}// end browsingDescription

	// ========== iconName
	// ==========================================================
	//
	// Purpose: Returns the name of image file used to display this kind of
	// object, or null if there is no icon.
	//
	// ==============================================================================
	public String iconName() {
		return "Triangle";

	}// end iconName

	// ========== inspectorClassName
	// ================================================
	//
	// Purpose: Returns the name of the class used to inspect this one.
	//
	// ==============================================================================
	public String inspectorClassName() {
		return "InspectionTriangle";

	}// end inspectorClassName

	// #pragma mark -
	// #pragma mark ACCESSORS
	// #pragma mark -

	// ========== boundingBox3
	// ======================================================
	//
	// Purpose: Returns the minimum and maximum points of the box which
	// perfectly contains this object.
	//
	// ==============================================================================
	public Box3 boundingBox3() {
		// Raw directive doesn't cache - we just compute our bbox on the fly.
		// But
		// keep our parents "in sync".
		revalCache(CacheFlagsT.CacheFlagBounds);

		if (hidden == true)
			return Box3.getInvalidBox();

		Box3 bounds;

		// Compare first two points.
		bounds = MatrixMath.V3BoundsFromPoints(vertex1, vertex2);

		// Now toss the third vertex into the mix.
		bounds = MatrixMath.V3UnionBoxAndPoint(bounds, vertex3);

		return bounds;

	}// end boundingBox3

	// ========== position
	// ==========================================================
	//
	// Purpose: Returns some position for the element. This is used by
	// drag-and-drop. This is not necessarily human-usable information.
	//
	// ==============================================================================
	public Vector3f position() {
		return vertex1;

	}// end position

	// ========== vertex1
	// ===========================================================
	// ==============================================================================
	public Vector3f vertex1() {
		return vertex1;

	}// end vertex1

	// ========== vertex2
	// ===========================================================
	// ==============================================================================
	public Vector3f vertex2() {
		return vertex2;

	}// end vertex2

	// ========== vertex3
	// ===========================================================
	// ==============================================================================
	public Vector3f vertex3() {
		return vertex3;

	}// end vertex3

	// #pragma mark -

	// ========== setSelected:
	// ======================================================
	//
	// Purpose: Somebody make this a protocol method.
	//
	// ==============================================================================
	public void setSelected(boolean flag) {
		super.setSelected(flag);

		if (flag == true) {
			LDrawDragHandle handle1 = new LDrawDragHandle();
			handle1.initWithTag(1, vertex1);

			LDrawDragHandle handle2 = new LDrawDragHandle();
			handle1.initWithTag(2, vertex2);

			LDrawDragHandle handle3 = new LDrawDragHandle();
			handle1.initWithTag(3, vertex3);

			handle1.setTarget(this);
			handle2.setTarget(this);
			handle3.setTarget(this);

			 handle1.setAction(SelT.DragHandleChanged);
			 handle2.setAction(SelT.DragHandleChanged);
			 handle3.setAction(SelT.DragHandleChanged);

			dragHandles = new ArrayList<LDrawDragHandle>();
			dragHandles.add(handle1);
			dragHandles.add(handle2);
			dragHandles.add(handle3);
		} else {
			dragHandles = null;
		}

	}// end setSelected:

	// ========== setVertex1:
	// =======================================================
	//
	// Purpose: Sets the triangle's first vertex.
	//
	// ==============================================================================
	/**
	 * @param newVertex
	 * @uml.property  name="vertex1"
	 */
	public void setVertex1(Vector3f newVertex) {
		vertex1.set(newVertex);
		recomputeNormal();
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);

		if (dragHandles != null) {
			dragHandles.get(0).setPosition(newVertex, false);
		}
		if(enclosingDirective()!=null)
		enclosingDirective().setVertexesNeedRebuilding();

	}// end setVertex1:

	// ========== setVertex2:
	// =======================================================
	//
	// Purpose: Sets the triangle's second vertex.
	//
	// ==============================================================================
	/**
	 * @param newVertex
	 * @uml.property  name="vertex2"
	 */
	public void setVertex2(Vector3f newVertex) {
		vertex2.set(newVertex);
		recomputeNormal();
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);

		if (dragHandles != null) {
			dragHandles.get(1).setPosition(newVertex, false);
		}
		if(enclosingDirective()!=null)
		enclosingDirective().setVertexesNeedRebuilding();

	}// end setVertex2:

	// ========== setVertex3:
	// =======================================================
	//
	// Purpose: Sets the triangle's last vertex.
	//
	// ==============================================================================
	/**
	 * @param newVertex
	 * @uml.property  name="vertex3"
	 */
	public void setVertex3(Vector3f newVertex) {
		vertex3.set(newVertex);
		recomputeNormal();
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);

		if (dragHandles != null) {
			dragHandles.get(2).setPosition(newVertex, false);
		}
		if(enclosingDirective()!=null)
		enclosingDirective().setVertexesNeedRebuilding();

	}// end setVertex3:

	//
	// #pragma mark -
	// #pragma mark ACTIONS
	// #pragma mark -

	// ========== dragHandleChanged:
	// ================================================
	//
	// Purpose: One of the drag handles on our vertexes has changed.
	//
	// ==============================================================================
	public void dragHandleChanged(LDrawDragHandle sender) {
		LDrawDragHandle handle = (LDrawDragHandle) sender;
		Vector3f newPosition = handle.position();
		int vertexNumber = handle.tag();

		switch (vertexNumber) {
		case 1:
			setVertex1(newPosition);
			break;
		case 2:
			setVertex2(newPosition);
			break;
		case 3:
			setVertex3(newPosition);
			break;
		}
	}// end dragHandleChanged:

	// ========== moveBy:
	// ===========================================================
	//
	// Purpose: Moves the receiver in the specified direction.
	//
	// ==============================================================================
	public void moveBy(Vector3f moveVector) {
		Vector3f newVertex1 = MatrixMath.V3Add(vertex1, moveVector);
		Vector3f newVertex2 = MatrixMath.V3Add(vertex2, moveVector);
		Vector3f newVertex3 = MatrixMath.V3Add(vertex3, moveVector);

		setVertex1(newVertex1);
		setVertex2(newVertex2);
		setVertex3(newVertex3);

	}// end moveBy:

	// #pragma mark -
	// #pragma mark UTILITIES
	// #pragma mark -

	// ========== flattenIntoLines:triangles:quadrilaterals:other:currentColor:
	// =====
	//
	// Purpose: Appends the directive into the appropriate container.
	//
	// ==============================================================================

	public void flattenIntoLines(ArrayList<LDrawLine> lines,
			ArrayList<LDrawTriangle> triangles,
			ArrayList<LDrawQuadrilateral> quadriaterals,
			ArrayList<LDrawDirective> everythingElse, LDrawColor parentColor,
			Matrix4 transform, Matrix3 normalTransform, boolean recursive) {
		super.flattenIntoLines(lines, triangles, quadriaterals, everythingElse,
				parentColor, transform, normalTransform, recursive);

		vertex1 = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
		vertex2 = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);
		vertex3 = MatrixMath.V3MulPointByProjMatrix(vertex3, transform);

		normal = MatrixMath.V3RotateByTransformMatrix(normal, transform);
//		normal = MatrixMath.V3MulPointByMatrix(normal, normalTransform);

		triangles.add(this);

	}// end flattenIntoLines:triangles:quadrilaterals:other:currentColor:

	// ========== recomputeNormal
	// ===================================================
	//
	// Purpose: Finds the normal vector for this surface.
	//
	// ==============================================================================
	public void recomputeNormal() {
		Vector3f vector1, vector2;

		vector1 = MatrixMath.V3Sub(vertex2, vertex1);
		vector2 = MatrixMath.V3Sub(vertex3, vertex1);

		normal = MatrixMath.V3Cross(vector1, vector2);

	}// end recomputeNormal

	// ========== registerUndoActions
	// ===============================================
	//
	// Purpose: Registers the undo actions that are unique to this subclass,
	// not to any superclass.
	//
	// ==============================================================================
	public void registerUndoActions(UndoManager undoManager) {

		super.registerUndoActions(undoManager);

		// todo
		// [[undoManager prepareWithInvocationTarget:self] setVertex3:vertex3]);
		// [[undoManager prepareWithInvocationTarget:self] setVertex2:vertex2]);
		// [[undoManager prepareWithInvocationTarget:self] setVertex1:vertex1]);

		// [undoManager setActionName:NSLocalizedString(@"UndoAttributesLine",
		// null));

	}// end registerUndoActions:


	public void setToCW() {
		Vector3f tempVector = vertex2;
		vertex2 = vertex3;
		vertex3 = tempVector;
		recomputeNormal();
	}

}
