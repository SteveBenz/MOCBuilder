package Command;

import LDraw.Support.ColorLibrary;

//0 !LDRAW_ORG Configuration UPDATE 2012-09-13
public enum LDrawColorT {
	LDrawColorBogus(-1), // used for uninitialized colors.
	LDrawColorCustomRGB(-2), LDrawBlack(0), LDrawBlue(1), LDrawGreen(2), LDrawDarkTurquoise(
			3), LDrawRed(4), LDrawDarkPink(5), LDrawBrown(6), LDrawLightGray(7), LDrawDarkGray(
			8), LDrawLightBlue(9), LDrawBrightGreen(10), LDrawTurquiose(11), LDrawSalmon(
			12), LDrawPink(13), LDrawYellow(14), LDrawWhite(15), LDrawCurrentColor(
			16), // special non-color takes hue of whatever the previous
					// color was.
	LDrawLightGreen(17), LDrawLightYellow(18), LDrawTan(19), LDrawLightViolet(
			20), LDrawGlowInDarkOpaque(21), LDrawPurple(22), LDrawDarkBlueViolet(
			23), LDrawEdgeColor(24), // special non-color contrasts the
										// current color.
	LDrawOrange(25), LDrawMagenta(26), LDrawLime(27), LDrawDarkTan(28), LDrawBrightPink(
			29), LDrawMediumLavender(30), LDrawLavender(31), LDrawTransBlackIRLens(
			32), LDrawTransDarkBlue(33), LDrawTransGreen(34), LDrawTransBrightGreen(
			35), LDrawTransRed(36), LDrawTransDarkPink(37), LDrawTransNeonOrange(
			38), LDrawTransVeryLightBlue(39), LDrawTransBlack(40), LDrawTransMediumBlue(
			41), LDrawTransNeonGreen(42), LDrawTransLightBlue(43), LDrawTransLightPurple(
			44), LDrawTransPink(45), LDrawTransYellow(46), LDrawTransClear(47), LDrawTransPurple(
			52), LDrawTransNeonYellow(54), LDrawTransOrange(57), LDrawChromeAntiqueBrass(
			60), LDrawChromeBlue(61), LDrawChromeGreen(62), LDrawChromePink(63), LDrawChromeBlack(
			64), LDrawRubberYellow(65), LDrawRubberTransYellow(66), LDrawRubberTransClear(
			67), LDrawVeryLightOrange(68), LDrawLightPurple(69), LDrawReddishBrown(
			70), LDrawLightBluishGray(71), LDrawDarkBluishGray(72), LDrawMediumBlue(
			73), LDrawMediumGreen(74), LDrawSpeckleBlackCopper(75), LDrawSpeckleDarkBluishGraySilver(
			76), LDrawLighPink(77), LDrawLightFlesh(78), LDrawMilkyWhite(79), LDrawMetallicSilver(
			80), LDrawMetallicGreen(81), LDrawMetallicGold(82), LDrawMetallicBlack(
			83), LDrawMediumDarkFlesh(84), LDrawDarkPurple(85), LDrawDarkFlesh(
			86), LDrawMetallicDarkGray(87), LDrawBlueViolet(89), LDrawFlesh(92), LDrawLightSalmon(
			100), LDrawViolet(110), LDrawMediumViolet(112), LDrawGlitterTransDarkPink(
			114), LDrawMediumLime(115), LDrawGlitterTransClear(117), LDrawAqua(
			118), LDrawLightLime(120), LDrawLightOrange(125), LDrawGlitterTransPurple(
			129), LDrawSpeckleBlackSilver(132), LDrawSpeckleBlackGold(133), LDrawCopper(
			134), LDrawPearlGray(135), LDrawMetalBlue(137), LDrawPearlLightGold(
			142), LDrawPearlDarkGray(148), LDrawPearlVeryLightGrey(150), LDrawVeryLightBluishGray(
			151), LDrawFlatDarkGold(178), LDrawFlatSilver(179), LDrawPearlWhite(
			183), LDrawBrightLightOrange(191), LDrawBrightLightBlue(212), LDrawRust(
			216), LDrawBrightLightYellow(226), LDrawSkyBlue(232), LDrawRubberBlack(
			256), LDrawDarkBlue(272), LDrawRubberBlue(273), LDrawDarkGreen(288), LDrawGlowInDarkTrans(
			294), LDrawPearlGold(297), LDrawDarkBrown(308), LDrawMaerskBlue(313), LDrawDarkRed(
			320), LDrawDarkAzure(321), LDrawMediumAzure(322), LDrawLightAqua(
			323), LDrawRubberRed(324), LDrawYellowishGreen(326), LDrawOliveGreen(
			330), LDrawChromeGold(334), LDrawSandRed(335), LDrawRubberOrange(
			350), LDrawMediumDarkPink(351), LDrawEarthOrange(366), LDrawSandPurple(
			373), LDrawRubberLightGray(375), LDrawSandGreen(378), LDrawSandBlue(
			379), LDrawChromeSilver(383), LDrawRubberDarkBlue(406), LDrawRubberPurple(
			449), LDrawRubberLime(490), LDrawMagnet(493), LDrawElectricContactAlloy(
			494), LDrawFabulandBrown(450), LDrawMediumOrange(462), LDrawDarkOrange(
			484), LDrawElectricContact(494), LDrawElectricContactCopper(495), LDrawRubberLightBluishGray(
			496), LDrawVeryLightGray(503), LDrawRubberFlatSilver(504), LDrawRubberWhite(
			511);

	/**
	 * @uml.property name="value"
	 */
	private int value;

	private LDrawColorT(int value) {
		this.value = value;
	}

	/**
	 * @return
	 * @uml.property name="value"
	 */
	public int getValue() {
		return value;
	}
		
	public static LDrawColorT byValue(Integer ldrawColorValue) {
		for(LDrawColorT colorT : values())
			if(colorT.getValue() == ldrawColorValue)
				return colorT;
		return LDrawCurrentColor;
	}
	
	public int getDistance(LDrawColorT other){
		LDrawColor color = ColorLibrary.sharedColorLibrary().colorForCode(this);
		LDrawColor otherColor =ColorLibrary.sharedColorLibrary().colorForCode(other);
		
		float[] colorValue = new float[4];
		color.getColorRGBA(colorValue);
		
		float[] otherColorValue = new float[4];
		otherColor.getColorRGBA(otherColorValue);
		
		int distance = 0;
		for(int i=0; i < 4; i++)
			distance+=(int) Math.pow(255*(colorValue[i] - otherColorValue[i]), 2);
		
		System.out.println(this+"<->"+other+":"+distance);
		return distance;
	}
}