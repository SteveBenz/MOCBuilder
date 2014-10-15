package LDraw.Support;

public class LDrawGlobalFlag {
	
	public static final float DecimalPoint = 0.001f;
	//basic
	public static final boolean NEW_RENDERER =true;
	
	// LDrawLine
	public static final boolean NO_LINE_DRWAING = false;

	// LDrawPart
	public static final boolean SHRINK_SEAMS = false;

	// LDrawQuadrilateral
	public static final boolean TESSELATE_QUADS = false;

	// LDrawModel
	public static final boolean NO_CULL_SMALL_BRICKS = true;

	// LDrawDirective
	public static final int NEW_SET = 1;
	public static int DRAW_NO_OPTIONS = 0;
	public static int DRAW_WIREFRAME = 1 << 1;
	public static int DRAW_BOUNDS_ONLY = 1 << 3;

	// LDrawGLRenderer
	public static final boolean WANT_TWOPASS_BOXTEST = false; // this enables the two-pass box-test. It is actually faster to _not_ do this now that hit testing is optimized.
	public static final boolean TIME_BOXTEST = false; // output timing data for how long box tests and marquee drags take.
	public static final boolean DEBUG_BOUNDING_BOX = false; // attempts to draw debug bounding box visualization on the model.
	
	//LDrawVertices
	public static final boolean USE_AUTOMATIC_WIREFRAMES = false;
	
	//LDrawDisplayList
	public static final boolean ONLY_USE_TRIS = false;

	public static final boolean WANT_SMOOTH = true;

	// This times smoothing of parts.
	public static final boolean TIME_SMOOTHING = false;

	public static final boolean WANT_STATS = false;
	
	//MeshSmooth
	public static final boolean SLOW_CHECKING = false;
	public static final boolean WANT_CREASE = true;
	public static final boolean WANT_INVERTS = true;
	public static final boolean DEBUG_SHOW_NORMALS_AS_COLOR = false;	
	
}
