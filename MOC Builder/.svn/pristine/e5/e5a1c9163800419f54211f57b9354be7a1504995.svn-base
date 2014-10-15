package ConnectivityEditor.Connectivity;

/**
 * String[] type = new String[] { "o socket 1", "o axle 1", "+ socket 1",
 * "+ axle 1", "o socekt 2", "o axle 2", "o socket 3", "o socket 3", "o axle 4",
 * "pin socket 1", "pin 1", "+ axle 2", "o axle 5", "o axle 6" }; int[]
 * typeValue = new int[] { 2, 3, 4, 5, 6, 7, 8, 12, 13, 14, 15, 17, 19, 21 };
 * 
 */
public enum AxleT {
	Socket_O_1(2), Axle_O_1(3), Socket_Cross_1(4), Axle_Cross_1(5), Socket_O_2(
			6), Axle_O_2(7), Socket_O_3(8), Socket_O_4(12), Axle_O_4(13), Socket_Pin_1(
			14), Axle_Pin_1(15), Axle_Cross_2(17), Axle_O_5(19), Axle_O_6(21);
	private int value;

	private AxleT(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static AxleT byValue(int value) {
		for (AxleT type : AxleT.values())
			if (type.getValue() == value)
				return type;
		return null;
	}
}
