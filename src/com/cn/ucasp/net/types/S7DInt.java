package com.cn.ucasp.net.types;

/* Dint 
 * 时间表示的是int32 由高位到地位的4位int型 
 * 
 * 
 * 
 */
public class S7DInt {

	public static int FromByteArray(byte[] bytes) {
		return FromBytes(bytes[3], bytes[2], bytes[1], bytes[0]);
	}

	public static int FromBytes(int v1, int v2, int v3, int v4) {
		return (int)((v2)<<8 )+(int)(v1)+(int)((v3)<<16 )+(int)(v4<<24);
	}

	public static byte[] ToByteArray(int value) {
		byte[] bytes = new byte[4];
		int x = 4;
		long valLong = value;
		for (int cnt = 0; cnt < x; cnt++) {
			long x1 = (long) Math.pow(256, (cnt));

			long x3 = (long) (valLong / x1);
			bytes[x - cnt - 1] = (byte) (x3 & 255);
			valLong -= bytes[x - cnt - 1] * x1;
		}
		return bytes;
	}

	public static byte[] ToByteArray(int[] value) {
		ByteArray arr = new ByteArray();
		for (int val : value)
			arr.Add(ToByteArray(val));
		return arr.getArray();
	}

	public static int[] ToArray(byte[] bytes) {
		int[] values = new int[bytes.length / 4];

		int counter = 0;
		for (int cnt = 0; cnt < bytes.length / 4; cnt++)
			values[cnt] = FromByteArray(
					new byte[] { bytes[counter++], bytes[counter++], bytes[counter++], bytes[counter++] });
		return values;
	}

	public static long CDWord(long value) {
		if (value > Integer.MAX_VALUE) {
			value -= (long) Integer.MAX_VALUE + 1;
			value = (long) Integer.MAX_VALUE + 1 - value;
			value *= -1;
		}
		return (int) value;
	}
}
