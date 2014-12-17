package LDraw.Support;

import java.util.ArrayList;
import java.util.HashMap;

import Color.ColorCategoryT;
import Command.LDrawColor;
import Command.LDrawColorT;
import LDraw.Files.LDrawFile;

public class ColorLibrary implements Cloneable {

	private static HashMap<ColorCategoryT, ArrayList<LDrawColorT>> colorCategory;

	private HashMap<LDrawColorT, LDrawColor> colors; // keys are
														// LDrawColorT
														// codes; objects
														// are LDrawColors
	private HashMap<LDrawColorT, LDrawColor> privateColors; // colors we
															// might be
															// asked to
															// display,
															// but
															// should
															// NOT be in
															// the color
															// picker

	private static ColorLibrary sharedColorLibrary = null;

	public ColorLibrary() {
		colors = new HashMap<LDrawColorT, LDrawColor>();
		privateColors = new HashMap<LDrawColorT, LDrawColor>();
	}

	// ---------- sharedColorLibrary
	// --------------------------------------[static]--
	//
	// Purpose: Returns the global color library available to all LDraw objects.
	// The colors are dynamically read from ldconfig.ldr.
	//
	// ------------------------------------------------------------------------------

	public synchronized static ColorLibrary sharedColorLibrary() {
		if (sharedColorLibrary == null) {

			String ldconfigPath = null;
			LDrawFile ldconfigFile = null;
			// ---------- Read colors in ldconfig.ldr
			// -------------------------------

			// Read it in.
			ldconfigPath = LDrawPaths.getInstance().ldconfigPath();
			ldconfigFile = LDrawFile.fileFromContentsAtPath(ldconfigPath);

			// "Draw" it so that all the colors are recorded in the library
			HashMap<Integer, Boolean> optionMask = new HashMap<Integer, Boolean>();
			optionMask.put(LDrawGlobalFlag.DRAW_NO_OPTIONS, true);
			ldconfigFile.collectColor();
			// [ldconfigFile draw: viewScale:1.0 parentColor:null];

			sharedColorLibrary = ldconfigFile.activeModel().colorLibrary();

			// ---------- Special Colors
			// --------------------------------------------
			// These meta-colors are chameleons that are interpreted based on
			// the
			// context. But we still need to create entries for them in the
			// library
			// so that they can be selected in the color palette.

			LDrawColor currentColor = new LDrawColor();
			LDrawColor edgeColor = new LDrawColor();

			float currentColorRGBA[] = { 0.5f, 0.5f, 0.5f, 1.0f };
			float edgeColorRGBA[] = { 0.5f, 0.5f, 0.5f, 1.0f };

			// Make the "current color" a blah sort of beige. We display parts
			// in
			// the part browser using this "color"; that's the only time we'll
			// ever
			// see it.
			currentColor.setColorCode(LDrawColorT.LDrawCurrentColor);
			currentColor.setColorRGBA(currentColorRGBA);

			// The edge color is never seen in models, but it still appears in
			// the
			// color panel, so we need to give it something.
			edgeColor.setColorCode(LDrawColorT.LDrawEdgeColor);
			edgeColor.setColorRGBA(edgeColorRGBA);

			// Register both special colors in the library
			sharedColorLibrary.addColor(currentColor);
			sharedColorLibrary.addColor(edgeColor);

			// ---------- Dithered Colors
			// -------------------------------------------
			// I'm only providing these to be a nice team player in the LDraw
			// world.

			LDrawColor blendedColor = null;

			// Provide dithered colors for the entire valid range from LDRAW.EXE
			for (LDrawColorT color : LDrawColorT.values()) {
				if (color.getValue() < 256)
					continue;
				blendedColor = LDrawColor.blendedColorForCode(color);
				if (blendedColor != null)
					sharedColorLibrary.addPrivateColor(blendedColor);
			}

			// init color library
			initColorCategory();

		}

		return sharedColorLibrary;
	}

	// ========== addPrivateColor:
	// ==================================================
	//
	// Purpose: Adds the given color to the receiver, but doesn't make it
	// visible to the color picker.
	//
	// Notes: This supports LDRAW.EXE's "dithered" colors, which sadly wormed
	// their way into the part library, but should absolutely never be
	// used in modeling. Keeping the "private" allows us to display
	// old parts which may have been created with them without allowing
	// them to otherwise pollute the user experience.
	//
	// ==============================================================================

	private static void initColorCategory() {
		colorCategory = new HashMap<ColorCategoryT, ArrayList<LDrawColorT>>();
		for (ColorCategoryT category : ColorCategoryT.values())
			colorCategory.put(category, new ArrayList<LDrawColorT>());

		// Solid
		for (LDrawColorT colorCode : LDrawColorT.values()) {
			int colorValue = colorCode.getValue();
			switch (colorValue) {
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
			case 8:
			case 9:
			case 10:
			case 11:
			case 12:
			case 13:
			case 14:
			case 15:
			case 17:
			case 18:
			case 19:
			case 20:
			case 22:
			case 23:
			case 25:
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 31:
			case 68:
			case 70:
			case 71:
			case 72:
			case 73:
			case 74:
			case 77:
			case 78:
			case 84:
			case 85:
			case 86:
			case 89:
			case 92:
			case 100:
			case 110:
			case 112:
			case 115:
			case 118:
			case 120:
			case 125:
			case 151:
			case 191:
			case 212:
			case 216:
			case 226:
			case 232:
			case 272:
			case 288:
			case 308:
			case 313:
			case 320:
			case 321:
			case 322:
			case 323:
			case 326:
			case 335:
			case 351:
			case 366:
			case 373:
			case 378:
			case 379:
			case 450:
			case 462:
			case 484:
			case 503:
				colorCategory.get(ColorCategoryT.Solid).add(colorCode);
				break;
			case 47:
			case 40:
			case 36:
			case 38:
			case 57:
			case 54:
			case 46:
			case 42:
			case 35:
			case 34:
			case 33:
			case 41:
			case 43:
			case 39:
			case 44:
			case 52:
			case 37:
			case 45:
				colorCategory.get(ColorCategoryT.Transparent).add(colorCode);
				break;
			case 334:
			case 383:
			case 60:
			case 64:
			case 61:
			case 62:
			case 63:
				colorCategory.get(ColorCategoryT.Chrome).add(colorCode);
				break;
			case 80:
			case 81:
			case 82:
			case 83:
			case 87:
				colorCategory.get(ColorCategoryT.Metallic).add(colorCode);
				break;
			case 75:
			case 76:
			case 132:
			case 133:
				colorCategory.get(ColorCategoryT.Speckle).add(colorCode);
				break;
			case 180:
			case 150:
			case 135:
			case 179:
			case 148:
			case 137:
			case 142:
			case 297:
			case 178:
			case 134:
				colorCategory.get(ColorCategoryT.Pearl).add(colorCode);
				break;
			case 114:
			case 117:
			case 129:
				colorCategory.get(ColorCategoryT.Glitter).add(colorCode);
				break;
			case 79:
			case 21:
			case 294:
				colorCategory.get(ColorCategoryT.Milky).add(colorCode);
				break;
			case 65:
			case 66:
			case 67:
			case 256:
			case 273:
			case 324:
			case 375:
			case 406:
			case 449:
			case 490:
			case 496:
			case 504:
			case 511:
				colorCategory.get(ColorCategoryT.Rubber).add(colorCode);
				break;
			case 16:
			case 24:
			case 32:
			case 493:
			case 494:
			case 495:
				colorCategory.get(ColorCategoryT.CommonMaterial).add(colorCode);
				break;
			}
		}
	}

	public ArrayList<LDrawColorT> getColorTList(ColorCategoryT category) {
		return colorCategory.get(category);
	}

	public void addPrivateColor(LDrawColor newColor) {
		LDrawColorT colorCode = newColor.colorCode();
		privateColors.put(colorCode, newColor);

	}

	// ========== addColor:
	// =========================================================
	//
	// Purpose: Adds the given color to the receiver.
	//
	// ==============================================================================

	public void addColor(LDrawColor newColor) {
		LDrawColorT colorCode = newColor.colorCode();
		colors.put(colorCode, newColor);
	}

	// ========== colorForCode:
	// =====================================================
	//
	// Purpose: Returns the LDrawColor object representing colorCode, or null if
	// no such color number is registered. This method also searches
	// the shared library, since its colors have global scope.
	//
	// ==============================================================================

	public LDrawColor colorForCode(LDrawColorT colorCode) {
		LDrawColor color = colors.get(colorCode);

		// Try searching the private colors.
		if (color == null) {
			color = privateColors.get(colorCode);
		}

		// Try the shared library.
		if (color == null && this != sharedColorLibrary) {
			color = ColorLibrary.sharedColorLibrary().colorForCode(colorCode);
		}

		// Return something!
		if (color == null) {
			color = ColorLibrary.sharedColorLibrary
					.colorForCode(LDrawColorT.LDrawCurrentColor);
		}

		return color;

	}

	// ========== complimentColorForCode:
	// ===========================================
	//
	// Purpose: Returns the color that should be used when the compliment color
	// is requested for the given code. Compliment colors are usually
	// used to draw lines on the edges of parts.
	//
	// Notes: It may seem odd to have the method in the Color Library rather
	// than the color object it. The reason is that a color may
	// specify its compliment color either as actual color components
	// or as another color code. Since colors have no actual knowledge
	// of the library in which they are contained, we must look up the
	// actual code here.
	//
	// Also note that the default ldconfig.ldr file defines most
	// compliment colors as black, which is well and good for printed
	// instructions, but less than stellar for onscreen display. The
	// visual looks a lot more realistic when red has an edge color of,
	// say, pink.
	//
	// ==============================================================================

	public void getComplimentRGBA(float[] complimentRGBA, LDrawColorT colorCode) {
		// TODO Auto-generated method stub
		LDrawColor mainColor = colorForCode(colorCode);
		LDrawColorT edgeColorCode = LDrawColorT.LDrawColorBogus;

		if (mainColor != null) {
			edgeColorCode = mainColor.edgeColorCode();

			// If the color has a defined RGBA edge color, use it. Otherwise,
			// look
			// up the components of the color it points to.
			if (edgeColorCode == LDrawColorT.LDrawColorBogus)
				mainColor.getEdgeColorRGBA(complimentRGBA);
			else
				colorForCode(edgeColorCode).getColorRGBA(complimentRGBA);
		}
	}

	// ========== complimentColor()
	// =================================================
	//
	// Purpose: Changes the given RGBA color into a "complimentary" color, which
	// stands out in the original color, but maintains the same hue.
	//
	// ==============================================================================
	public void complimentColor(float[] originalColor, float[] complimentColor) {
		float brightness = 0.0f;

		// Isolate the color's grayscale intensity
		// http://en.wikipedia.org/wiki/Grayscale
		brightness = originalColor[0] * 0.30f + originalColor[1] * 0.59f
				+ originalColor[2] * 0.11f;

		// compliment dark colors with light ones and light colors with dark
		// ones.

		if (brightness > 0.5f) {
			final float adjusingValue = brightness * 0.1f;
			// Darken
			complimentColor[0] = Math.max(originalColor[0] - adjusingValue,
					0.0f);
			complimentColor[1] = Math.max(originalColor[1] - adjusingValue,
					0.0f);
			complimentColor[2] = Math.max(originalColor[2] - adjusingValue,
					0.0f);
			// for(int i=0; i < 3; i++)
			// complimentColor[i] = originalColor[i];
		} else {
			final float adjusingValue = (1 - brightness * brightness) * 0.2f;
			// Lighten
			complimentColor[0] = Math.min(originalColor[0] + adjusingValue,
					1.0f);
			complimentColor[1] = Math.min(originalColor[1] + adjusingValue,
					1.0f);
			complimentColor[2] = Math.min(originalColor[2] + adjusingValue,
					1.0f);
		}

		complimentColor[3] = originalColor[3];

	}// end complimentColor

}
