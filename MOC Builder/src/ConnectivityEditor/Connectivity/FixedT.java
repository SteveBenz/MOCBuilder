package ConnectivityEditor.Connectivity;

public enum FixedT {
	AirplaneFrontWindowFrame(78), PanForAirplaneFrontWindow(79), ArmorKnifeHanger_f(
			100), ArmorKnifeHanger_m(101), AxleExt_f(68), AxleExt_m(69), BackBottle_f(
			60), BackBottle_m(61), Ball_1_f(90), Ball_1_m(91), Ball_2_f(106), Ball_2_m(
			107), Caterpillar_f(102), Caterpillar_m(103), Cocoon_f(110), Cocoon_m(
			111), Coffin_f(94), Coffin_m(95), DoorFrame(6), PanForDoor(7), DoorWindowFrame(
			124), PanForDoorWindow(125), Frame(62), PanForFrame(63), FuncPlug_f(
			96), FuncPlug_m(97), HeadToHair_f(30), HeadToHair_m(31), HelicopterFrontWindowFrame(
			118), PanForHelicopterFrontWindow(119), HelmetScreen_1_f(88), HelmetScreen_1_m(
			89), HelmetScreen_2_f(104), HelmetScreen_2_m(105), HorseHeadDeco_f(
			58), HorseHeadDeco_m(59), HorseHeadArmor_f(108), HorseHeadArmor_m(
			109), InsectEye_f(98), InsectEye_m(99), KnifeHanger(92), KnifeHanger_f(
			93), MastConn_1_f(64), MastConn_1_m(65), MastConn_2_f(66), MastConn_2_m(
			67), MastConn_3_f(74), MastConn_3_m(75), MiniFigLegSocket(4), MiniFigLeg(
			5), MiniFigLegXX(120), MotoCycleFrame_f(52), MotoCycle_m(53), MotoCycleSeat_f(
			54), MotoCycleSeatToLeg(55), RockConn_f(76), RockConn_m(77), TrainWindowFrame_1(
			36), TrainWindow_1(37), TrainWindowFrame_2(50), TrainWindow_2(51), WindowScreenFrame(
			122), PanForWindowScreen(123), Railroad_f(20), Railroad_m(21), RJ45_f(
			28), RJ45_m(29), Tire_1(2), Wheel_1(3), Tire_2(8), Wheel_2(9), Tire_3(
			10), Wheel_3(11), Tire_4(12), Wheel_4(13), Tire_5(14), Wheel_5(15), Tire_6(
			16), Wheel_6(17), Tire_7(24), Wheel_7(25), Tire_8(26), Wheel_8(27), Tire_9(
			32), Wheel_9(33), Tire_10(38), Wheel_10(39), Tire_11(40), Wheel_11(
			41), Tire_12(46), Wheel_12(47), Tire_13(48), Wheel_13(49), Tire_14(
			70), Wheel_14(71), Tire_15(72), Wheel_15(73), Tire_16(80), Wheel_16(
			81), Tire_17(82), Wheel_17(83), Tire_18(114), Wheel_18(115), WallFrame_1(
			22), PanForWall_1(23), WallFrame_2(56), PanForWall_2(57), WindowFrame_1(
			86), PanForWindow_1(87), ;

	private int value;

	private FixedT(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static FixedT byValue(int value) {
		for (FixedT type : FixedT.values())
			if (type.getValue() == value)
				return type;
		return null;
	}
}
