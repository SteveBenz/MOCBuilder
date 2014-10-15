package Renderer;

////////////////////////////////////////////////////////////////////////////////////////////////////
//
//LDrawCollector
//
////////////////////////////////////////////////////////////////////////////////////////////////////

//An LDraw collector accumulates meshes in a fixed coordinate system.  A texture stack can be used
//to push/pop texture state; if no texture state is pushed, the mesh ends up capable of "taking current
//texture."
//

public interface ILDrawCollector {
	// Texture stack - sets up new texturing. When the stack is totally popped,
	// no texturing is applied.
	void pushTexture(LDrawTextureSpec tex_spec);

	void popTexture();

	// Raw drawing APIs to push one quad/tri/line. Vertices are consecutive
	// float verts, e.g. 12 for quad, 9 for tri, 6 for line/
	// Color can be null to use the current color. Normal is a float[3] normal
	// ptr.
	void drawQuad(float[] vertices, float[] normal, float[] color);

	void drawTri(float[] vertices, float[] normal, float[] color);

	void drawLine(float[] vertices, float[] normal, float[] color);
}
