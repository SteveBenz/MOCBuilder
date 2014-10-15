package Command;

import java.nio.FloatBuffer;
import java.util.StringTokenizer;

import LDraw.Support.ColorLibrary;
import LDraw.Support.LDrawDirective;
import LDraw.Support.LDrawKeywords;
import LDraw.Support.NSComparisonResult;
import LDraw.Support.type.LDrawColorMaterialT;

public class LDrawColor extends LDrawDirective implements Cloneable {
	/**
	 * @uml.property name="colorCode"
	 * @uml.associationEnd
	 */
	LDrawColorT colorCode;
	/**
	 * @uml.property name="colorRGBA" multiplicity="(0 -1)" dimension="1"
	 */
	float colorRGBA[] = new float[4]; // range [0.0 - 1.0]
	/**
	 * @uml.property name="edgeColorCode"
	 * @uml.associationEnd
	 */
	LDrawColorT edgeColorCode; // == LDrawColorBogus if not used
	/**
	 * @uml.property name="edgeColorRGBA" multiplicity="(0 -1)" dimension="1"
	 */
	float edgeColorRGBA[] = new float[4];
	/**
	 * @uml.property name="hasExplicitAlpha"
	 */
	boolean hasExplicitAlpha;
	/**
	 * @uml.property name="hasLuminance"
	 */
	boolean hasLuminance;
	/**
	 * @uml.property name="luminance"
	 */
	int luminance;
	/**
	 * @uml.property name="material"
	 * @uml.associationEnd
	 */
	LDrawColorMaterialT material;
	/**
	 * @uml.property name="materialParameters"
	 */
	String materialParameters;
	/**
	 * @uml.property name="name"
	 */
	String name;

	/**
	 * @uml.property name="fakeComplimentColor"
	 * @uml.associationEnd
	 */
	LDrawColor fakeComplimentColor; // synthesized, not according to !COLOUR
									// rules

	public LDrawColor() {
		init();
	}

	// ========== init
	// ==============================================================
	//
	// Purpose: Initialize a new object.
	//
	// ==============================================================================
	public LDrawColor init() {
		super.init();

		colorRGBA = new float[4];

		setColorCode(LDrawColorT.LDrawColorBogus);
		setEdgeColorCode(LDrawColorT.LDrawColorBogus);
		setMaterial(LDrawColorMaterialT.LDrawColorMaterialNone);
		setName("");

		colorRGBA[3] = 1.0f; // alpha.

		return this;

	}// end init
	
	// ========== finishParsing:
	// ====================================================
	//
	// Purpose: -[LDrawMetaCommand initWithLines:inRange:] is
	// responsible for parsing out the line code and color command
	// (i.e., "0 !COLOUR"); now we just have to finish the
	// color-command specific syntax.
	//
	// ==============================================================================
	public boolean finishParsing(StringTokenizer strTokenizer) throws Exception {
		String parsedField = null;

		// Name
		// [scanner scanUpToCharactersFromSet:[NSCharacterSet
		// whitespaceCharacterSet] intoString:&field);
		parsedField = strTokenizer.nextToken();
		setName(parsedField);

		// Color Code
		if (strTokenizer.hasMoreTokens() == false
				|| strTokenizer.nextToken().equals("CODE") == false)
			throw new Exception("BricksmithParseExceptio: "
					+ "Bad !COLOUR syntax");
		if (strTokenizer.hasMoreTokens() == false)
			throw new Exception("BricksmithParseException: "
					+ "Bad !COLOUR syntax");
		parsedField = strTokenizer.nextToken();
		int value = Integer.parseInt(parsedField);
		for (LDrawColorT color : LDrawColorT.values()) {
			if (color.getValue() == value) {
				colorCode = color;
				break;
			}
		}

		// Color Components
		if (strTokenizer.hasMoreTokens() == false
				|| strTokenizer.nextToken().equals("VALUE") == false)
			throw new Exception("BricksmithParseExceptio: "
					+ "Bad !COLOUR syntax");
		if (strTokenizer.hasMoreTokens() == false)
			throw new Exception("BricksmithParseException: "
					+ "Bad !COLOUR syntax");
		parsedField = strTokenizer.nextToken();
		colorRGBA[0] = Integer.parseInt(parsedField.substring(1, 3), 16) / 255.0f;
		colorRGBA[1] = Integer.parseInt(parsedField.substring(3, 5), 16) / 255.0f;
		colorRGBA[2] = Integer.parseInt(parsedField.substring(5), 16) / 255.0f;
		colorRGBA[3] = 1.0f;

		//
		// // Edge
		if (strTokenizer.hasMoreTokens() == false
				|| strTokenizer.nextToken().equals("EDGE") == false)
			throw new Exception("BricksmithParseExceptio: "
					+ "Bad !COLOUR syntax");
		if (strTokenizer.hasMoreTokens() == false)
			throw new Exception("BricksmithParseException: "
					+ "Bad !COLOUR syntax");
		parsedField = strTokenizer.nextToken();
		if (parsedField.length() == 7) {
			edgeColorRGBA[0] = Integer
					.parseInt(parsedField.substring(1, 3), 16) / 255.0f;
			edgeColorRGBA[1] = Integer
					.parseInt(parsedField.substring(3, 5), 16) / 255.0f;
			edgeColorRGBA[2] = Integer.parseInt(parsedField.substring(5), 16) / 255.0f;
			edgeColorRGBA[3] = 1.0f;
		} else {
			value = Integer.parseInt(parsedField);
			for (LDrawColorT color : LDrawColorT.values()) {
				if (color.getValue() == value) {
					edgeColorCode = color;
					break;
				}
			}
		}

		// Optional Fields
		while (strTokenizer.hasMoreTokens()) {
			parsedField = strTokenizer.nextToken();

			if (parsedField.equals("ALPHA")) {			// - Alpha
				value = Integer.parseInt(strTokenizer.nextToken());
				colorRGBA[3] = value / 255.0f;
				hasExplicitAlpha = true;
			} else if (parsedField.equals("LUMINANCE")) {		 // - Luminance
				value = Integer.parseInt(strTokenizer.nextToken());
				setLuminance(value);
			}else if (parsedField.equals(LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_CHROME)) {		 
				setMaterial(LDrawColorMaterialT.LDrawColorMaterialChrome);
			}else if (parsedField.equals(LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_PEARLESCENT)) {		 
				setMaterial(LDrawColorMaterialT.LDrawColorMaterialPearlescent);
			}else if (parsedField.equals(LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_RUBBER)) {		 
				setMaterial(LDrawColorMaterialT.LDrawColorMaterialRubber);
			}else if (parsedField.equals(LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_MATTE_METALLIC)) {		 
				setMaterial(LDrawColorMaterialT.LDrawColorMaterialMatteMetallic);
			}else if (parsedField.equals(LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_METAL)) {		 
				setMaterial(LDrawColorMaterialT.LDrawColorMaterialMetal);
			}else if (parsedField.equals(LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_CUSTOM)) {		 
				setMaterial(LDrawColorMaterialT.LDrawColorMaterialCustom);		
				String parameters = "";
				while (strTokenizer.hasMoreTokens()) {
					parameters += strTokenizer.nextToken()+" ";
				}
				setMaterialParameters(parameters);
			}
		}
		return true;

	}// end lineWithDirectiveText

	// ---------- blendedColorForCode:
	// ------------------------------------[static]--
	//
	// Purpose: Returns pseduocolors according to logic found in LDRAW.EXE.
	//
	// Notes: James Jessiman's original DOS-based LDraw was limited in to 16
	// colors (in 1995!), so he developed a hack to accommodate a
	// bigger palette: dithering. Two colors would be combined in a
	// pixel-checkerboard pattern. Transparent colors were implemented
	// with a dither overlay, as was a huge swath of color codes which
	// would combine two colors.
	//
	// All of this is utterly, pathologically obsolete. For one thing,
	// computers can display 16.7 million colors per pixel. For another
	// thing, dithering was really ugly. And finally, LDConfig and the
	// !COLOUR meta-command provide a way of specifying any one of
	// those 16.7 million colors.
	//
	// Unfortunately, MLCad displayed dithered colors in its color
	// picker up until 2010. Worse yet, woe betide us, part authors
	// used these dithered colors to model certain stickers and printed
	// bricks.
	//
	// Bricksmith will grudgingly support blended colors strictly for
	// purposes of displaying those stickers. But it will falseT, EVER
	// show these colors in its color picker. This functionality should
	// be sent back to the early nineties where it deserved to die.
	//
	// ------------------------------------------------------------------------------
	public static LDrawColor blendedColorForCode(LDrawColorT colorCode) {
		int ldrawEXEColorTable[][] = { { 51, 51, 51 }, { 0, 51, 178 },
				{ 0, 127, 51 }, { 0, 181, 166 }, { 204, 0, 0 },
				{ 255, 51, 153 }, { 102, 51, 0 }, { 153, 153, 153 },
				{ 102, 102, 88 }, { 0, 128, 255 }, { 51, 255, 102 },
				{ 171, 253, 249 }, { 255, 0, 0 }, { 255, 176, 204 },
				{ 255, 229, 0 }, { 255, 255, 255 } };

		int blendCode1 = 0;
		int blendCode2 = 0;
		float[] blendedComponents = new float[4];
		LDrawColor blendedColor = new LDrawColor();
		blendedColor.init();

		// Find the two base indexes of the blended color's dither.
		blendCode1 = (colorCode.getValue() - 256) / 16; // div (integer division)
		blendCode2 = (colorCode.getValue() - 256) % 16;

		// Derive the components. Hold your nose.
		// Obviously, we don't support dithering. We average the colors to
		// produce
		// something which looks nicer.
		blendedComponents[0] = (float) (ldrawEXEColorTable[blendCode1][0] + ldrawEXEColorTable[blendCode2][0]) / 2 / 255; // red
		blendedComponents[1] = (float) (ldrawEXEColorTable[blendCode1][1] + ldrawEXEColorTable[blendCode2][1]) / 2 / 255; // green
		blendedComponents[2] = (float) (ldrawEXEColorTable[blendCode1][2] + ldrawEXEColorTable[blendCode2][2]) / 2 / 255; // blue
		blendedComponents[3] = 1.0f; // alpha

		// Create a color to hold them.
		blendedColor.setColorCode(colorCode);
		blendedColor.setColorRGBA(blendedComponents);
		blendedColor.setName("BlendedColor" + colorCode.getValue());

		return blendedColor;

	}// end blendedColorForCode:

	// #pragma mark -
	// #pragma mark DIRECTIVES
	// #pragma mark -

	// ========== draw:viewScale:parentColor:
	// =======================================
	//
	// Purpose: "Draws" the color.
	//
	// ==============================================================================
	public void collectColor() {
		// Need to add this color to the model's color library.
		ColorLibrary colorLibrary = (ColorLibrary) enclosingDirective()
				.enclosingModel().colorLibrary();

		colorLibrary.addColor(this);

	}// end draw:viewScale:parentColor:

	// ========== write
	// =============================================================
	//
	// Purpose: Returns a line that can be written out to a file.
	// Line format:
	// 0 !COLOUR name CODE x VALUE v EDGE e [ALPHA a] [LUMINANCE l]
	// [ CHROME | PEARLESCENT | RUBBER | MATTE_METALLIC |
	// METAL | MATERIAL <params> ]</params>
	//
	// Notes: This does not try to preserve spacing a la ldconfig.ldr, mainly
	// because %17@ doesn't work.
	//
	// ==============================================================================
	public String write() {
		String line = null;

		line = String.format("0 %s %s %s %d %s %s",
						// | | |
						LDrawKeywords.LDRAW_COLOR_DEFINITION, name(),
						// | |
						LDrawKeywords.LDRAW_COLOR_DEF_CODE, colorCode,
						// |
						LDrawKeywords.LDRAW_COLOR_DEF_VALUE,
						hexStringForRGB(colorRGBA));

		// if(edgeColorCode() == LDrawColorT.LDrawColorBogus)
		// line+= new String().format(" %s %s",
		// LDrawKeywords.LDRAW_COLOR_DEF_EDGE, hexStringForRGB(edgeColorRGBA);
		// else
		// line+= new String().format(" %s %d",
		// LDrawKeywords.LDRAW_COLOR_DEF_EDGE, edgeColorCode));
		//
		// if(hasExplicitAlpha == true)
		// line+= new String().format(" %s %d",
		// LDrawKeywords.LDRAW_COLOR_DEF_ALPHA, (int)(colorRGBA[3] * 255));
		//
		// if(hasLuminance == true)
		// line+= new String().format(" %s %d",
		// LDrawKeywords.LDRAW_COLOR_DEF_LUMINANCE, luminance);
		//
		// switch(material)
		// {
		// case LDrawColorMaterialNone:
		// break;
		//
		// case LDrawColorMaterialChrome:
		// line+= new String().format(" %s",
		// LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_CHROME);
		// break;
		//
		// case LDrawColorMaterialPearlescent:
		// line+= new String().format(" %s",
		// LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_PEARLESCENT);
		// break;
		//
		// case LDrawColorMaterialRubber:
		// line+= new String().format(" %s",
		// LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_RUBBER);
		// break;
		//
		// case LDrawColorMaterialMatteMetallic:
		// line+= new String().format(" %s",
		// LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_MATTE_METALLIC);
		// break;
		//
		// case LDrawColorMaterialMetal:
		// line+= new String().format(" %s",
		// LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_METAL);
		// break;
		//
		// case LDrawColorMaterialCustom:
		// line+= new String().format(" %s %s",
		// LDrawKeywords.LDRAW_COLOR_DEF_MATERIAL_CUSTOM, materialParameters);
		// break;
		// }

		return line;

	}// end write

	// #pragma mark -
	// #pragma mark DISPLAY
	// #pragma mark -

	// ========== browsingDescription
	// ===============================================
	//
	// Purpose: Returns a representation of the directive as a short string
	// which can be presented to the user.
	//
	// ==============================================================================
	public String browsingDescription() {
		return name();

	}// end browsingDescription

	// ========== iconName
	// ==========================================================
	//
	// Purpose: Returns the name of image file used to display this kind of
	// object, or null if there is no icon.
	//
	// ==============================================================================
	public String iconName() {
		return "ColorDroplet";

	}// end iconName

	// ========== inspectorClassName
	// ================================================
	//
	// Purpose: Returns the name of the class used to inspect this one.
	//
	// ==============================================================================
	public String inspectorClassName() {
		return null;

	}// end inspectorClassName

	// #pragma mark -
	// #pragma mark ACCESSORS
	// #pragma mark -

	// ========== colorCode
	// =========================================================
	// ==============================================================================
	public LDrawColorT colorCode() {
		return colorCode;

	}// end colorCode

	// ========== complimentColor
	// ===================================================
	//
	// Purpose: Returns the color which should be used for drawing
	// LDrawEdgeColor for this color.
	//
	// ==============================================================================
	public LDrawColor complimentColor() {
		// LDConfig compliment colors look ugly. Bricksmith uses
		// internally-derived
		// compliments which look more like the original LDraw.
		if (fakeComplimentColor == null) {
			fakeComplimentColor = new LDrawColor();
			fakeComplimentColor.init();

			float fakeComplimentComponents[] = new float[4];
			// complimentColor(colorRGBA, fakeComplimentComponents);

			fakeComplimentColor.setColorCode(LDrawColorT.LDrawEdgeColor);
			fakeComplimentColor.setColorRGBA(fakeComplimentComponents);
		}

		return fakeComplimentColor;
	}

	// ========== edgeColorCode
	// =====================================================
	//
	// Purpose: Return the LDraw color code to be used when drawing the
	// compilement of this color. If the compliment is stored as actual
	// components instead, this call will return LDrawColorBogus. When
	// that code is encountered, you should instead call edgeColorRGBA
	// for the actual color values.
	//
	// ==============================================================================
	public LDrawColorT edgeColorCode() {
		return edgeColorCode;

	}// end edgeColorCode

	// ========== getColorRGBA:
	// =====================================================
	//
	// Purpose: Fills the inComponents array with the RGBA components of this
	// color.
	//
	// ==============================================================================
	public void getColorRGBA(float inComponents[]) {
		// memcpy(inComponents, colorRGBA, sizeof(float) * 4);
		for (int i = 0; i < 4; i++)
			inComponents[i] = colorRGBA[i];

	}// end getColorRGBA:

	// ========== getEdgeColorRGBA:
	// =================================================
	//
	// Purpose: Returns the actual color components specified for the compliment
	// of this color.
	//
	// Notes: These values MAY falseT BE VALID. To determine if they are in
	// force, you must first call -edgeColorCode. If it returns a value
	// other than LDrawColorBogus, look up the color for that code
	// instead. Otherwise, use the values returned by this method.
	//
	// ==============================================================================
	public void getEdgeColorRGBA(float inComponents[]) {
		for (int i = 0; i < 4; i++)
			inComponents[i] = edgeColorRGBA[i];

	}// end getEdgeColorRGBA:

	// ========== localizedName
	// =====================================================
	//
	// Purpose: Returns the name for the specified color code. If possible, the
	// name will be localized. For colors which have no localization
	// defined, this will default to the actual color name from the
	// config file, with any underscores converted to spaces.
	//
	// Notes: If, in some bizarre aberration, this color has a code
	// corresponding to a standard LDraw code, but the color is falseT
	// actually representing this color, you will get the localized
	// name of the standard color. Deal with it.
	//
	// ==============================================================================
	public String localizedName() {
		String nameKey = null;
		String colorName = null;

		// Find the color's name in the localized string file.
		// Color names are conveniently keyed.
		nameKey = String.format("LDraw: %d", colorCode);
		colorName = nameKey;// NSLocalizedString(nameKey , null);

		// If no localization was defined, then fall back on the name defined in
		// the
		// color directive.
		if (colorName == nameKey) {
			// Since spaces are verboten in !COLOUR directives, color names tend
			// to
			// have a bunch of unsightly underscores in them. We don't want to
			// show
			// that to the user.
			String fixedName = name;
			fixedName.replaceAll("_", " ");
			colorName = fixedName;

			// Alas! 10.5 only!
			// colorName = [name] stringByReplacingOccurrencesOfString:@"_"
			// withString:@" ");
		}

		return colorName;

	}// end localizedName

	// ========== luminance
	// =========================================================
	// ==============================================================================
	public int luminance() {
		return luminance;

	}// end luminance

	// ========== material
	// ==========================================================
	// ==============================================================================
	public LDrawColorMaterialT material() {
		return material;

	}// end material

	// ========== materialParameters
	// ================================================
	// ==============================================================================
	public String materialParameters() {
		return materialParameters;

	}// end materialParameters

	// ========== name
	// ==============================================================
	// ==============================================================================
	public String name() {
		return name;

	}// end name

	// #pragma mark -

	// ========== setColorCode:
	// =====================================================
	//
	// Purpose: Sets the LDraw integer code for this color.
	//
	// ==============================================================================
	/**
	 * @param newCode
	 * @uml.property name="colorCode"
	 */
	public void setColorCode(LDrawColorT newCode) {
		colorCode = newCode;

	}// end setColorCode:

	// ========== setColorRGBA:
	// =====================================================
	//
	// Purpose: Sets the actual RGBA component values for this color.
	//
	// ==============================================================================
	public void setColorRGBA(float newComponents[]) {
		// memcpy(colorRGBA, newComponents, sizeof(float[4]));
		for (int i = 0; i < 4; i++)
			colorRGBA[i] = newComponents[i];

	}// end setColorRGBA:

	// ========== setEdgeColorCode:
	// =================================================
	//
	// Purpose: Sets the code of the color to use as this color's compliment
	// color. That value will have to be resolved by the color library.
	//
	// Notes: Edge colors may be specified either as real color components or
	// as a color-code reference. Only one is valid. To signal that the
	// components should be used instead of this color code, pass
	// LDrawColorBogus.
	//
	// ==============================================================================
	/**
	 * @param newCode
	 * @uml.property name="edgeColorCode"
	 */
	public void setEdgeColorCode(LDrawColorT newCode) {
		edgeColorCode = newCode;

	}// end setEdgeColorCode:

	// ========== setEdgeColorRGBA:
	// =================================================
	//
	// Purpose: Sets actual color components for the edge color.
	//
	// Notes: Edge colors may be specified either as real color components or
	// as a color-code reference. Only one is valid. If you call this
	// method, it is assumed you are choosing the components variation.
	// The edge color code will automatically be set to
	// LDrawColorBogus.
	//
	// ==============================================================================
	public void setEdgeColorRGBA(float newComponents[]) {
		// memcpy(edgeColorRGBA, newComponents, sizeof(float[4]));
		for (int i = 0; i < 4; i++)
			edgeColorRGBA[i] = newComponents[i];

		// Disable the edge color code, since we have real color values for it
		// now.
		setEdgeColorCode(LDrawColorT.LDrawColorBogus);

	}// end setEdgeColorRGBA:

	// ========== setLuminance:
	// =====================================================
	//
	// Purpose: Brightness for colors that glow (range 0-255). Luminance is not
	// generally used by LDraw renderers (including this one), but may
	// be used for translation to other rendering systems. LUMINANCE is
	// optional.
	//
	// ==============================================================================
	/**
	 * @param newValue
	 * @uml.property name="luminance"
	 */
	public void setLuminance(int newValue) {
		luminance = newValue;
		hasLuminance = true;

	}// end setLuminance:

	// ========== setMaterial:
	// ======================================================
	//
	// Purpose: Sets the material associated with this color.
	//
	// Notes: Bricksmith doesn't use this value, it just preserves it in the
	// color directive.
	//
	// ==============================================================================
	/**
	 * @param newValue
	 * @uml.property name="material"
	 */
	public void setMaterial(LDrawColorMaterialT newValue) {
		material = newValue;

	}// end setMaterial:

	// ========== setMaterialParameters:
	// ============================================
	//
	// Purpose: Custom (implementation-dependent) values associated with a
	// custom material.
	//
	// Notes: Bricksmith doesn't use this value, it just preserves it in the
	// color directive.
	//
	// ==============================================================================
	/**
	 * @param newValue
	 * @uml.property name="materialParameters"
	 */
	public void setMaterialParameters(String newValue) {
		materialParameters = newValue;
	}// end setMaterialParameters:

	// ========== setName:
	// ==========================================================
	//
	// Purpose: Sets the name of the color. Spaces are represented by
	// underscores.
	//
	// ==============================================================================
	/**
	 * @param newName
	 * @uml.property name="name"
	 */
	public void setName(String newName) {
		name = newName;
	}// end setName:

	// #pragma mark -
	// #pragma mark UTILITIES
	// #pragma mark -

	// ========== isEqual:
	// ==========================================================
	//
	// Purpose: Allow these objects to serve as keys in a dictionary (used in
	// LDrawVertexes); -isEqual: and -hash are both required.
	//
	// We only expect one instance of each unique color to exist, so
	// the trivial comparison should be valid. After all, two different
	// (file-local) colors could share the same color code, but they
	// aren't equal.
	//
	// ==============================================================================
	public boolean isEqual(LDrawColor anObject) {
		boolean isEqual = false;

		// If two objects are equal, they must return the same hash. The hash is
		// a
		// pain to compute, so we don't want to do anything fancy with equality.
		// if([anObject isMemberOfClass:[LDrawColor class]])
		// {
		// isEqual = (colorCode == [anObject colorCode]);
		// }

		isEqual = (anObject == this);

		return isEqual;
	}

	// ========== hash
	// ==============================================================
	//
	// Purpose: Allow these objects to serve as keys in a dictionary (used in
	// LDrawVertexes).
	//
	// ==============================================================================
	public Object hash() {
		return this;
	}

	// ========== compare:
	// ==========================================================
	//
	// Purpose: Compatibility method directing to our specialized comparison.
	//
	// ==============================================================================
	public NSComparisonResult compare(LDrawColor otherColor) {
		return HSVACompare(otherColor);
	}

	// ========== HSVACompare:
	// ======================================================
	//
	// Purpose: Orders colors according to their Hue, Saturation, and
	// Brightness.
	//
	// ==============================================================================
	public NSComparisonResult HSVACompare(LDrawColor otherColor) {
		// NSComparisonResult result = NSOrderedSame;
		// float ourHSV[] = new float[4];
		// float otherColorHSV[] = new float[4];
		//
		// // Convert both to Hue-saturation-brightness
		// RGBtoHSV(colorRGBA[0], colorRGBA[1], colorRGBA[2],
		// ourHSV[0], ourHSV[1], ourHSV[2]);
		//
		// RGBtoHSV(otherColor.colorRGBA[0], otherColor.colorRGBA[1],
		// otherColor.colorRGBA[2],
		// otherColorHSV[0], otherColorHSV[1], otherColorHSV[2]);
		//
		// // Alpha
		// ourHSV[3] = colorRGBA[3];
		// otherColorHSV[3] = colorRGBA[3];
		//
		// // Hue
		// if( ourHSV[0] > otherColorHSV[0] )
		// result = NSOrderedDescending;
		// else if( ourHSV[0] < otherColorHSV[0] )
		// result = NSOrderedAscending;
		// else
		// {
		// // Saturation
		// if( ourHSV[1] > otherColorHSV[1] )
		// result = NSOrderedDescending;
		// else if( ourHSV[1] < otherColorHSV[1] )
		// result = NSOrderedAscending;
		// else
		// {
		// // Brightness
		// if( ourHSV[2] > otherColorHSV[2] )
		// result = NSOrderedDescending;
		// else if( ourHSV[2] < otherColorHSV[2] )
		// result = NSOrderedAscending;
		// else
		// {
		// // Alpha
		// if( ourHSV[3] > otherColorHSV[3] )
		// result = NSOrderedDescending;
		// else if( ourHSV[3] < otherColorHSV[3] )
		// result = NSOrderedAscending;
		// else
		// {
		// result = NSOrderedSame;
		// }
		// }
		// }
		// }

		// return result;
		return null;

	}// end HSVACompare:

	// ========== hexStringForRGB:
	// ==================================================
	//
	// Purpose: Returns a hex string for the given RGB components, formatted in
	// the syntax required by the LDraw Colour Definition Language
	// extension.
	//
	// ==============================================================================
	public String hexStringForRGB(float[] components) {
		String hexString = String.format("#%02X%02X%02X",
				(int) (components[0] * 255), (int) (components[1] * 255),
				(int) (components[2] * 255));
		return hexString;

	}// end hexStringForRGB:

	// ========== scanHexString:intoRGB:
	// ============================================
	//
	// Purpose: Parses the given Hexidecimal string into the first three
	// elements of the components array, dividing each hexidecimal byte
	// by 255.
	//
	// Notes: hexString must be prefixed by either "#" or "0x". The LDraw spec
	// is not clear on the case of the hex letters; we will assume both
	// are valid.
	//
	// Example: #77CC00 becomes (R = 0.4666; G = 0.8; B = 0.0)
	//
	// ==============================================================================
	public static boolean scanHexString(String hexScanner, float components[]) {
		boolean success = false;
		String hexStr = null;
		// Make sure it has the required prefix, whichever it might be
		// todo
		if (hexScanner.contains("#") == true) {
			int index = hexScanner.indexOf("#");
			hexStr = hexScanner.substring(index + 1, index + 7);
		} else if (hexScanner.contains("0x") == true) {
			int index = hexScanner.indexOf("0x");
			hexStr = hexScanner.substring(index + 1, index + 7);
		}

		if (hexStr != null) {
			// Scan the hex bytes into a packed integer, because that's the
			// easiest
			// thing to do with this NSScanner API.
			// hexScanner.scanHexInt(hexBytes);

			// Colors will be stored in the integer as follows: xxRRGGBB
			int value = Integer.parseInt(hexStr.substring(0, 2), 16);
			components[0] = (float) (value / 255); // Red
			value = Integer.parseInt(hexStr.substring(2, 4), 16);
			components[1] = (float) (value / 255); // Green
			value = Integer.parseInt(hexStr.substring(4, 6), 16);
			components[2] = (float) (value / 255); // Blue
			components[3] = 1.0f; // we shall assume alpha
		}

		return success;

	}// end parseHexString:intoRGB:

	//
	// #pragma mark -
	// #pragma mark DESTRUCTOR
	// #pragma mark -

	// #pragma mark -

	// ========== RGBtoHSV
	// ==========================================================
	//
	// Purpose: Converts an RGB color into Hue-Saturation-Brightness
	//
	// Parameters: r,g,b values are from 0 to 1
	// h = [0,360], s = [0,1], v = [0,1]
	// if s == 0, then h = -1 (undefined)
	//
	// Notes: from http://www.cs.rit.edu/~ncs/color/t_convert.html
	//
	// ==============================================================================
	void RGBtoHSV(float r, float g, float b, FloatBuffer h, FloatBuffer s,
			FloatBuffer v) {
		float min, max, delta;

		min = Math.min(r, Math.min(g, b));
		max = Math.max(r, Math.max(g, b));
		v.put(0, max); // v

		delta = max - min;

		if (max != 0)
			s.put(0, delta / max); // s
		else {
			// r = g = b = 0 // s = 0, v is undefined
			s.put(0, 0);
			h.put(0, -1);
			return;
		}

		if (r == max)
			h.put(0, (g - b) / delta); // between yellow & magenta
		else if (g == max)
			h.put(0, 2 + (b - r) / delta); // between cyan & yellow
		else
			h.put(0, 4 + (r - g) / delta); // between magenta & cyan

		h.put(0, h.get(0) * 60); // degrees
		if (h.get(0) < 0)
			h.put(0, h.get(0) + 360);
	}

	// ========== HSVtoRGB
	// ==========================================================
	//
	// Purpose: Converts an HSV color into Red-Green-Blue
	//
	// Parameters: r,g,b values are from 0 to 1
	// h = [0,360], s = [0,1], v = [0,1]
	// if s == 0, then h = -1 (undefined)
	//
	// Notes: from http://www.cs.rit.edu/~ncs/color/t_convert.html
	//
	// ==============================================================================
	void HSVtoRGB(float h, float s, float v, FloatBuffer r, FloatBuffer g,
			FloatBuffer b) {
		int i;
		float f, p, q, t;

		if (s == 0) {
			// achromatic (grey)
			r.put(0, v);
			g.put(0, v);
			b.put(0, v);
			return;
		}

		h /= 60; // sector 0 to 5
		i = (int) Math.floor(h);
		f = h - i; // factorial part of h
		p = v * (1 - s);
		q = v * (1 - s * f);
		t = v * (1 - s * (1 - f));

		switch (i) {
		case 0:
			r.put(v);
			g.put(t);
			b.put(p);
			break;
		case 1:
			r.put(q);
			g.put(v);
			b.put(p);
			break;
		case 2:
			r.put(p);
			g.put(v);
			b.put(t);
			break;
		case 3:
			r.put(p);
			g.put(q);
			b.put(v);
			break;
		case 4:
			r.put(t);
			g.put(p);
			b.put(v);
			break;
		default: // case 5:
			r.put(v);
			g.put(p);
			b.put(q);
			break;
		}
	}

	/**
	 * @return
	 * @uml.property name="colorCode"
	 */
	public LDrawColorT getColorCode() {
		return colorCode;
	}

	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}
