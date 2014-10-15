package Renderer;

import javax.media.opengl.GL2;


////////////////////////////////////////////////////////////////////////////////////////////////////
//
//LDrawRenderer
//
////////////////////////////////////////////////////////////////////////////////////////////////////

//Renderer class - it visits each directive, which calls the various state routines.
//It provies stacks for color, transform, wire frame, and texture.
//
//When we actually want to draw a mesh, we use the begin/end/draw DL routine to create a display
//list containing the mesh.  beginDL provides a "collector" protocol capable of actually receiving
//the mesh.

public interface ILDrawRenderer {
	// Matrix stack.  The new matrix is accumulated onto the existing transform.
	void pushMatrix(float[] matrix);
	void popMatrix();

	// Returns a cull code indicating whether the AABB from minXYZ to maxXYZ is on screen and big enough
	// to be worth drawing.
	CullingT checkCull(float[] minXYZ, float[] maxXYZ);

	// This draws a plane AABB cube in the current color from minXYZ to maxXYZ.
	// It can be used for cheap bouding-box approximations of small bricks.
	void drawBoxFrom(GL2 gl2, float[] minXyz, float[] maxXyz);

	// Color stack.  Pushing a color overrides the current color.  If no one ever sets the current color we get
	// that generic beige that is the RGBA of color 16.
	void pushColor(float[] color);
	void popColor();

	// Wire frame count - if a non-zero number of wire frame requests are outstanding, we render in wireframe.
	void pushWireFrame(GL2 gl);
	void popWireFrame(GL2 gl);

	// Texture stack - sets up new texturing.  When the stack is totally popped, no texturing is applied.
	void pushTexture(LDrawTextureSpec tex_spec);
	void popTexture();

	// Draw drag handle at a given location (3 floats).  The coordinates are within the current
	// transform.  The size is in screen pixels.
	void drawDragHandle(float[] xyz, float size);

	// Begin/end for a display list.  Multiple display lists can be "open" for recording at one time);
	// each one returns its own collector object.  However, only the most recently (innermost)
	// display list can be accumulated into at one time.  (This is a bit of a defect of the API that we
	// should consider some day fixing.)
	ILDrawCollector beginDL();	
	ILDrawDLHandle endDL(GL2 gl2, LDrawDLCleanup_f func);		// Returns NULL if the display list is empty (e.g. no calls between begin/end)

	void drawDL(GL2 gl2, ILDrawDLHandle dl);
}
