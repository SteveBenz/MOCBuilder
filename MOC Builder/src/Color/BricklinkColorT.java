package Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

//0 !LDRAW_ORG Configuration UPDATE 2012-09-13
public enum BricklinkColorT {
	Aqua(41), Black(11), Blue(7), Blue_Violet(97), BrightGreen(36), BrightLightBlue(
			105), BrightLightOrange(110), BrightLightYellow(103), BrightPink(
			104), Brown(8), ChromeAntiqueBrass(57), ChromeBlack(122), ChromeBlue(
			52), ChromeGold(21), ChromeGreen(64), ChromePink(82), ChromeSilver(
			22), Copper(84), DarkAzure(153), DarkBlue(63), DarkBlue_Violet(109), DarkBluishGray(
			85), DarkBrown(120), DarkFlesh(91), DarkGray(10), DarkGreen(80), DarkOrange(
			68), DarkPink(47), DarkPurple(89), DarkRed(59), DarkTan(69), DarkTurquoise(
			39), EarthOrange(29), FabulandBrown(106), FabulandOrange(160), FlatDarkGold(
			81), FlatSilver(95), Flesh(28), GlitterTrans_Clear(101), GlitterTrans_DarkPink(
			100), GlitterTrans_Purple(102), GlowInDarkOpaque(46), GlowInDarkTrans(
			118), GlowinDarkWhite(159), Green(6), Lavender(154), LightAqua(152), LightBlue(
			62), LightBluishGray(86), LightFlesh(90), LightGray(9), LightGreen(
			38), LightLime(35), LightOrange(32), LightPink(56), LightPurple(93), LightSalmon(
			26), LightTurquoise(40), LightViolet(44), LightYellow(33), Lime(34), MaerskBlue(
			72), Magenta(71), MediumAzure(156), MediumBlue(42), MediumDarkFlesh(
			150), MediumDarkPink(94), MediumGreen(37), MediumLavender(157), MediumLime(
			76), MediumOrange(31), MediumViolet(73), MetalBlue(78), MetallicGold(
			65), MetallicGreen(70), MetallicSilver(67), MilkyWhite(60), MxAquaGreen(
			142), MxBlack(128), MxBrown(132), MxBuff(133), MxCharcoalGray(126), MxClear(
			149), MxLemon(139), MxLightBluishGray(124), MxLightGray(125), MxLightOrange(
			136), MxLightYellow(137), MxMediumBlue(144), MxOchreYellow(138), MxOliveGreen(
			140), MxOrange(135), MxPastelBlue(145), MxPastelGreen(141), MxPink(
			148), MxPinkRed(130), MxRed(129), MxTealBlue(146), MxTerracotta(134), MxTileBlue(
			143), MxTileBrown(131), MxTileGray(127), MxViolet(147), MxWhite(123), OliveGreen(
			155), Orange(4), PearlDarkGray(77), PearlGold(115), PearlLightGold(
			61), PearlLightGray(66), PearlVeryLightGray(119), PearlWhite(83), Pink(
			23), Purple(24), Red(5), ReddishBrown(88), Rust(27), Salmon(25), SandBlue(
			55), SandGreen(48), SandPurple(54), SandRed(58), SkyBlue(87), SpeckleBlack_Copper(
			116), SpeckleBlack_Gold(151), SpeckleBlack_Silver(111), SpeckleDBGray_Silver(
			117), Tan(2), Trans_Black(13), Trans_BrightGreen(108), Trans_Clear(
			12), Trans_DarkBlue(14), Trans_DarkPink(50), Trans_Green(20), Trans_LightBlue(
			15), Trans_LightPurple(114), Trans_MediumBlue(74), Trans_NeonGreen(
			16), Trans_NeonOrange(18), Trans_NeonYellow(121), Trans_Orange(98), Trans_Pink(
			107), Trans_Purple(51), Trans_Red(17), Trans_VeryLtBlue(113), Trans_Yellow(
			19), VeryLightBluishGray(99), VeryLightGray(49), VeryLightOrange(96), Violet(
			43), White(1), Yellow(3), YellowishGreen(158);

	public static void main(String args[]) {
		ArrayList<String> strList = new ArrayList<String>();
		for (BricklinkColorT blColorT : BricklinkColorT.values()) {
			strList.add("" + blColorT);
		}
		Collections.sort(strList, new Comparator<String>() {

			@Override
			public int compare(String arg0, String arg1) {
				return arg0.compareTo(arg1);
			}
		});
		for (String str : strList) {
			System.out.print(str + "("
					+ BricklinkColorT.valueOf(str).getValue() + "), ");
		}
	}

	// public static void main(String args[]) {
	// File file = new File("j:/color.txt");
	// try {
	// FileReader fr = new FileReader(file);
	// BufferedReader br = new BufferedReader(fr);
	//
	// String line = br.readLine();
	// while (line != null) {
	// String item[] = line.split("\\t");
	// if (item.length == 9) {
	// try {
	// Integer.parseInt(item[0].trim());
	// System.out.print(item[3].replaceAll(" ", "")
	// .replaceAll("-", "_")
	// + "("
	// + item[0].trim()
	// + "), ");
	// } catch (Exception e) {
	// }
	// }
	//
	// line = br.readLine();
	// }
	// } catch (FileNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

	/**
	 * @uml.property name="value"
	 */
	private int value;
	private String colorCode;
	private BricklinkColorT(int value) {
		this.value = value;
	}
	
	public void setColorCode(String colorCode){
		this.colorCode = colorCode;
	}
	
	public String getColorCode(){
		return colorCode;
	}

	/**
	 * @return
	 * @uml.property name="value"
	 */
	public int getValue() {
		return value;
	}

	public static BricklinkColorT byValue(int value) {
		for (BricklinkColorT colorT : BricklinkColorT.values())
			if (colorT.getValue() == value)
				return colorT;
		return null;
	}
}