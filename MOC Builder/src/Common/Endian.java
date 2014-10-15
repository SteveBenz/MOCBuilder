package Common;

public class Endian {
	static short changeByteOrder(short x) {
		return (short)((x << 8) | ((x >> 8) & 0xff));
	}

	static char changeByteOrder(char x) {
		return (char)((x << 8) | ((x >> 8) & 0xff));
	}

	static int changeByteOrder(int x) {
		return (int)((changeByteOrder((short)x) << 16) | (changeByteOrder((short)(x >> 16)) & 0xffff));
	}

	static long changeByteOrder(long x) {
	return (long)(((long)changeByteOrder((int)(x)) << 32) |
			((long)changeByteOrder((int)(x >> 32)) & 0xffffffffL));
	}

	public static float changeByteOrder(float x) {
		return Float.intBitsToFloat(changeByteOrder(Float.floatToRawIntBits(x)));
	}

	static double changeByteOrder(double x) {
		return Double.longBitsToDouble(changeByteOrder(Double.doubleToRawLongBits(x)));
	}
}
