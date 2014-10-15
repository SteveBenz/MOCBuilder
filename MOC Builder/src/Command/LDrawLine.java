package Command;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.media.opengl.GL2;
import javax.swing.undo.UndoManager;

import Common.Box2;
import Common.Box3;
import Common.Matrix3;
import Common.Matrix4;
import Common.Ray3;
import Common.Segment3;
import Common.Vector2f;
import Common.Vector3f;
import LDraw.Support.DispatchGroup;
import LDraw.Support.ILDrawDragHandler;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawDragHandle;
import LDraw.Support.LDrawGlobalFlag;
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
//File:		LDrawLine.m
//
//Purpose:		Line command.
//				Draws a line between two points.
//
//				Line format:
//				2 colour x1 y1 z1 x2 y2 z2 
//
//				where
//
//				* colour is a colour code: 0-15, 16, 24, 32-47, 256-511
//				* x1, y1, z1 is the position of the first point
//				* x2, y2, z2 is the position of the second point 
//
//Created by Allen Smith on 2/19/05.
//Copyright (c) 2005. All rights reserved.
//==============================================================================

public class LDrawLine extends LDrawDrawableElement implements ILDrawDragHandler{	
	/**
	 * @uml.property  name="vertex1"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f		vertex1= Vector3f.getZeroVector3f();
	/**
	 * @uml.property  name="vertex2"
	 * @uml.associationEnd  multiplicity="(1 1)"
	 */
	Vector3f		vertex2= Vector3f.getZeroVector3f();

	/**
	 * @uml.property  name="dragHandles"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="LDraw.Support.LDrawDragHandle"
	 */
	ArrayList<LDrawDragHandle>		dragHandles;

	//========== initWithLines:inRange:parentGroup: ================================
	//
	// Purpose:		Returns the LDraw directive based on lineFromFile, a single line 
//					of LDraw code from a file.
	//
//					directive should have the format:
	//
//					2 colour x1 y1 z1 x2 y2 z2 
	//
	//==============================================================================
	public LDrawLine initWithLines(ArrayList<String> lines, 				Range range,
			 DispatchGroup parentGroup)
	{
		String    workingLine    = lines.get(range.getLocation());
		String    parsedField    = null;
		Vector3f      workingVertex   = Vector3f.getZeroVector3f();
		LDrawColor  parsedColor    = null;
		
		try {
			super.initWithLines(lines, range, parentGroup);
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
			if(Integer.parseInt(parsedField) == 2)
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
			}
			else
				throw new Exception("BricksmithParseException: "+"Bad line syntax");
		}
		catch(Exception e)
		{	
			System.out.println(String.format("the line primitive %s was fatally invalid", lines.get(range.getLocation())));
			System.out.println(String.format(" raised exception %s", e.getMessage()));
		}
		
		return this;
		
	}//end initWithLines:inRange:

	//========== drawSelf: ===========================================================
	//
	// Purpose:		Draw this directive and its subdirectives by calling APIs on 
//					the passed in renderer, then calling drawSelf on children.
	//
	// Notes:		Lines use this message to get their drag handles drawn if
//					needed.  They do not draw their actual GL primitive because that
//					has already been "collected" by some parent capable of 
//					accumulating a mesh.
	//
	//================================================================================
	public void drawSelf(GL2 gl2, ILDrawRenderer renderer)
	{
		revalCache(CacheFlagsT.DisplayList);
		if(hidden == false)
		{
			if(dragHandles!=null)
			{
				for(LDrawDragHandle handle : dragHandles)
				{				
					handle.drawSelf(renderer);
				}
			}
		}
	}//end drawSelf:


	//========== collectSelf: ========================================================
	//
	// Purpose:		Collect self is called on each directive by its parents to
//					accumulate _mesh_ data into a display list for later drawing.
//					The collector protocol passed in is some object capable of 
//					remembering the collectable data.
	//
//					Real GL primitives participate by passing their color and
//					geometry data to the collector.
	//
	//================================================================================
	public void collectSelf(ILDrawCollector renderer)
	{
		revalCache(CacheFlagsT.DisplayList);
		if(color==null)return;
		if(hidden == false)
		{
			if (LDrawGlobalFlag.NO_LINE_DRWAING==false){
			float	v[] = { 
				vertex1.getX(), vertex1.getY(), vertex1.getZ(),
				vertex2.getX(), vertex2.getY(), vertex2.getZ() };
			float n[] = { 0, -1, 0 };

			if(color.getColorCode() == LDrawColorT.LDrawCurrentColor)
				renderer.drawLine(v, n, LDrawRenderColorT.LDrawRenderCurrentColor.getValue());
			else if(color.getColorCode() == LDrawColorT.LDrawEdgeColor){
				renderer.drawLine(v, n, LDrawRenderColorT.LDrawRenderComplimentColor.getValue());
			}
			else
			{				
				float	rgba[] = new float[4];
				color.getColorRGBA(rgba);
				renderer.drawLine(v, n, rgba);
			}
			}
		}
	}//end collectSelf:


	//========== hitTest:transform:viewScale:boundsOnly:creditObject:hits: =======
	//
	// Purpose:		Tests the directive and any of its children for intersections 
//					between the pickRay and the directive's drawn content. 
	//
	//==============================================================================
	public void hitTest(Ray3 pickRay, Matrix4 transform, float scaleFactor,
			boolean boundsOnly, LDrawDirective creditObject, HashMap<LDrawDirective, Float> hits)
	{
		if(hidden == false)
		{
			Vector3f     worldVertex1    = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
			Vector3f     worldVertex2    = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);
			Segment3    segment         = new Segment3(worldVertex1, worldVertex2);
			float       tolerance       = 1.0f / scaleFactor;
			FloatBuffer       intersectDepth  = FloatBuffer.allocate(1);
			boolean        intersects      = false;
			
			// Lines drawn to 1 pixel regardless of scale, so the pick tolerance must be 
			// 1 pixel. We approximate this by relying on the view providing a 1 pixel 
			// to 1 unit correspondence at 100%. 
			tolerance   = 1.0f / scaleFactor; 
			tolerance  *= 1.5; // allow a little fudge

			intersects = MatrixMath.V3RayIntersectsSegment(pickRay, segment, tolerance, intersectDepth);
			
			if(intersects)
			{
				LDrawUtilities.registerHitForObject(this, intersectDepth, creditObject, hits);
			}
			
			if(dragHandles!=null)
			{
				for(LDrawDragHandle handle : dragHandles)
				{
					handle.hitTest(pickRay, transform, scaleFactor, boundsOnly, null, hits);
				}
			}
		}
	}//end hitTest:transform:viewScale:boundsOnly:creditObject:hits:
	
	public void hitTest(Ray3 pickRay, Matrix4 transform, LDrawDirective creditObject, HashMap<LDrawDirective, Float> hits){
		if(hidden == false)
		{
			Vector3f     worldVertex1    = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
			Vector3f     worldVertex2    = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);
			Segment3    segment         = new Segment3(worldVertex1, worldVertex2);
			float       tolerance       = 1.0f;
			FloatBuffer       intersectDepth  = FloatBuffer.allocate(1);
			boolean        intersects      = false;
			
			// Lines drawn to 1 pixel regardless of scale, so the pick tolerance must be 
			// 1 pixel. We approximate this by relying on the view providing a 1 pixel 
			// to 1 unit correspondence at 100%.
			tolerance  *= 1.5; // allow a little fudge

			intersects = MatrixMath.V3RayIntersectsSegment(pickRay, segment, tolerance, intersectDepth);			
			if(intersects)
			{
				LDrawUtilities.registerHitForObject(this, intersectDepth, creditObject, hits);
			}			
		}
	}


	//========== boxTest:transform:boundsOnly:creditObject:hits: ===================
	//
	// Purpose:		Check for intersections with screen-space bounding box.
	//
	//==============================================================================
	public boolean boxTest(Box2 bounds, Matrix4 transform, boolean boundsOnly,
			LDrawDirective creditObject, TreeSet<LDrawDirective> hits) 
	{
		if(hidden == false)
		{
			Vector3f worldVertex1    = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
			Vector3f worldVertex2    = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);

			Vector2f	line[] = { 
				MatrixMath.V2Make(worldVertex1.getX(),worldVertex1.getY()),
				MatrixMath.V2Make(worldVertex2.getX(),worldVertex2.getY()) };

			if(MatrixMath.V2BoxIntersectsPolygon(bounds, line, 2))
			{
				LDrawUtilities.registerHitForObject(this, creditObject, hits);
				if(creditObject != null)
					return true;
			}
		}
		return false;
	}//end boxTest:transform:boundsOnly:creditObject:hits:


	//========== depthTest:inBox:transform:creditObject:bestObject:bestDepth:=======
	//
	// Purpose:		depthTest finds the closest primitive (in screen space) 
//					overlapping a given point, as well as its device coordinate
//					depth.
	//
	//==============================================================================
	public void depthTest(Vector2f pt, Box2 bounds, Matrix4 transform,
			LDrawDirective creditObject, ArrayList<LDrawDirective> bestObject, FloatBuffer bestDepth)
	{
		if(hidden == false)
		{
			Vector3f worldVertex1    = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
			Vector3f worldVertex2    = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);
			float tolerance2   = (bounds.getSize().getWidth()*bounds.getSize().getWidth()+bounds.getSize().getHeight()*bounds.getSize().getHeight())*0.25f;

			Vector3f probe = new Vector3f(pt.getX() , pt.getY(), bestDepth.get(0));

			if(MatrixMath.DepthOnLineSegment(worldVertex1,worldVertex2,tolerance2, probe))
			{
				if(probe.getZ() <= bestDepth.get(0))
				{
					bestDepth.put(0, probe.getZ());
					bestObject.add((LDrawDirective) (creditObject!=null ? creditObject : this));
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


	//========== write =============================================================
	//
	// Purpose:		Returns a line that can be written out to a file.
//					Line format:
//					2 colour x1 y1 z1 x2 y2 z2 
	//
	//==============================================================================
	public String write()
	{
		return String.format(
					"2 %s %s %s %s %s %s %s",
					LDrawUtilities.outputStringForColor(color),
					
					LDrawUtilities.outputStringForFloat(vertex1.getX()),
					LDrawUtilities.outputStringForFloat(vertex1.getY()),
					LDrawUtilities.outputStringForFloat(vertex1.getZ()),
					
					LDrawUtilities.outputStringForFloat(vertex2.getX()),
					LDrawUtilities.outputStringForFloat(vertex2.getY()),
					LDrawUtilities.outputStringForFloat(vertex2.getZ())				
				);
	}//end write

	//========== browsingDescription ===============================================
	//
	// Purpose:		Returns a representation of the directive as a short string 
//					which can be presented to the user.
	//
	//==============================================================================
	public String browsingDescription()
	{
		return "Line";
		
	}//end browsingDescription


	//========== iconName ==========================================================
	//
	// Purpose:		Returns the name of image file used to display this kind of 
//					object, or null if there is no icon.
	//
	//==============================================================================
	public String iconName()
	{
		return "Line";
		
	}//end iconName


	//========== inspectorClassName ================================================
	//
	// Purpose:		Returns the name of the class used to inspect this one.
	//
	//==============================================================================
	public String inspectorClassName()
	{
		return "InspectionLine";
		
	}//end inspectorClassName


//	#pragma mark -
//	#pragma mark ACCESSORS
//	#pragma mark -

	//========== boundingBox3 ======================================================
	//
	// Purpose:		Returns the minimum and maximum points of the box which 
//					perfectly contains this object.
	//
	//==============================================================================
	public Box3 boundingBox3()
	{
		revalCache(CacheFlagsT.CacheFlagBounds);

		if (hidden == true)
			return Box3.getInvalidBox();

		Box3 bounds =MatrixMath.V3BoundsFromPoints(vertex1, vertex2);
		
		return bounds;
		
	}//end boundingBox3


	//========== position ==========================================================
	//
	// Purpose:		Returns some position for the element. This is used by 
//					drag-and-drop. This is not necessarily human-usable information.
	//
	//==============================================================================
	public Vector3f position()
	{
		return vertex1;
		
	}//end position


	//========== vertex1 ===========================================================
	//
	// Purpose:		Returns the line's start point.
	//
	//==============================================================================
	public Vector3f vertex1()
	{
		return vertex1;
		
	}//end vertex1

	//========== vertex2 ===========================================================
	//
	// Purpose:		Returns the line's end point.
	//
	//==============================================================================
	public Vector3f vertex2()
	{
		return vertex2;
		
	}//end vertex2


//	#pragma mark -

	//========== setSelected: ======================================================
	//
	// Purpose:		Somebody make this a protocol method.
	//
	//==============================================================================
	public void setSelected(boolean flag)
	{
		super.setSelected(flag);
		
		if(flag == true)
		{
			LDrawDragHandle handle1 = new LDrawDragHandle();
			handle1.initWithTag(1, vertex1);
			
			LDrawDragHandle handle2 =new LDrawDragHandle();
			handle1.initWithTag(2, vertex2);
			
			handle1.setTarget(this);
			handle2.setTarget(this);
			
			handle1.setAction(SelT.DragHandleChanged);
			handle2.setAction(SelT.DragHandleChanged);
			
			dragHandles = new ArrayList<LDrawDragHandle>();
			dragHandles.add(handle1);
			dragHandles.add(handle2);
		}
		else
		{
			dragHandles = null;
		}
		
	}//end setSelected:


	//========== setVertex1: =======================================================
	//
	// Purpose:		Sets the line's start point.
	//
	//==============================================================================
	/**
	 * @param newVertex
	 * @uml.property  name="vertex1"
	 */
	public void setVertex1(Vector3f newVertex)
	{
		vertex1.set(newVertex);
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);
		
		if(dragHandles!=null)
		{
			dragHandles.get(0).setPosition(newVertex, false);
		}

		if(enclosingDirective()!=null)
			enclosingDirective().setVertexesNeedRebuilding();
		
	}//end setVertex1:


	//========== setVertex2: =======================================================
	//
	// Purpose:		Sets the line's end point.
	//
	//==============================================================================
	/**
	 * @param newVertex
	 * @uml.property  name="vertex2"
	 */
	public void setVertex2(Vector3f newVertex)
	{
		vertex2.set(newVertex);
		invalCache(CacheFlagsT.CacheFlagBounds);
		invalCache(CacheFlagsT.DisplayList);
		
		if(dragHandles!=null)
		{
			dragHandles.get(1).setPosition(newVertex, false);
		}
		if(enclosingDirective()!=null)
		enclosingDirective().setVertexesNeedRebuilding();
		
	}//end setVertex2:


//	#pragma mark -
//	#pragma mark ACTIONS
//	#pragma mark -

	//========== dragHandleChanged: ================================================
	//
	// Purpose:		One of the drag handles on our vertexes has changed.
	//
	//==============================================================================
	public void dragHandleChanged(LDrawDragHandle sender)
	{
		LDrawDragHandle handle         = (LDrawDragHandle )sender;
		Vector3f          newPosition     =handle.position();
		int       vertexNumber    = handle.tag();
		
		switch(vertexNumber)
		{
			case 1: setVertex1(newPosition); break;
			case 2: setVertex2(newPosition); break;
		}
	}//end dragHandleChanged:


	//========== moveBy: ============================================================
	//
	// Purpose:		Moves the receiver in the specified direction.
	//
	//==============================================================================
	public void moveBy(Vector3f moveVector)
	{
		Vector3f newVertex1 = MatrixMath.V3Add(vertex1, moveVector);
		Vector3f newVertex2 = MatrixMath.V3Add(vertex2, moveVector);
		
		setVertex1(newVertex1);
		setVertex2(newVertex2);

	}//end moveBy:


//	#pragma mark -
//	#pragma mark UTILITIES
//	#pragma mark -

	//========== flattenIntoLines:triangles:quadrilaterals:other:currentColor: =====
	//
	// Purpose:		Appends the directive into the appropriate container. 
	//
	//==============================================================================
	public void flattenIntoLines(ArrayList<LDrawLine> lines, ArrayList<LDrawTriangle> triangles,
			ArrayList<LDrawQuadrilateral> quadriaterals, ArrayList<LDrawDirective> everythingElse,
			LDrawColor parentColor, Matrix4 transform, Matrix3 normalTransform,
			boolean recursive) 
	{
		super.flattenIntoLines(lines, triangles, quadriaterals,
				everythingElse, parentColor, transform, normalTransform,
				recursive);		
		
		vertex1 = MatrixMath.V3MulPointByProjMatrix(vertex1, transform);
		vertex2 = MatrixMath.V3MulPointByProjMatrix(vertex2, transform);
		
		lines.add(this);
		
	}//end flattenIntoLines:triangles:quadrilaterals:other:currentColor:


	//========== registerUndoActions ===============================================
	//
	// Purpose:		Registers the undo actions that are unique to this subclass, 
//					not to any superclass.
	//
	//==============================================================================
	public void registerUndoActions(UndoManager undoManager)
	{

		super.registerUndoActions(undoManager);

		//todo
//		[[undoManager prepareWithInvocationTarget:self] setVertex2:vertex2]);
//		[[undoManager prepareWithInvocationTarget:self] setVertex1:vertex1]);
		
//		[undoManager setActionName:NSLocalizedString(@"UndoAttributesLine", null));
		
	}//end registerUndoActions:

}

