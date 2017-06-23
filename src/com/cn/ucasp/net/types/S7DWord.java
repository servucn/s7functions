package com.cn.ucasp.net.types;

public class S7DWord {

	public static int FromByteArray(byte[] bytes) {
		return FromBytes(bytes[3] & 0xff, bytes[2] & 0xff, bytes[1] & 0xff, bytes[0] & 0xff);
	}

	public static int FromBytes(int v1, int v2, int v3, int v4) {
		return (int) (v1 + v2 * Math.pow(2, 8) + v3 * Math.pow(2, 16) + v4 * Math.pow(2, 24));
	}

	public static byte[] ToByteArray(long value) {
		byte[] bytes = new byte[4];
		int x = 4;
		long valLong = (long) ((int) value);
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

}
