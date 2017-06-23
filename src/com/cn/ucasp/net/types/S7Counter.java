package com.cn.ucasp.net.types;

public class S7Counter {

	public static int FromByteArray(byte[] bytes) {

		return FromBytes(bytes[1] & 0xff, bytes[0] & 0xff);
	}

	public static int FromBytes(int LoVal, int HiVal) {
		return (HiVal * 256 + LoVal);
	}

	public static byte[] ToByteArray(int value) {
		byte[] bytes = new byte[2];
		int x = 2;
		long valLong = (long) ((int) value);
		for (int cnt = 0; cnt < x; cnt++) {
			long x1 = (long) Math.pow(256, (cnt));
			long x3 = (long) (valLong / x1);
			bytes[x - cnt - 1] = (byte) (x3 & 0xff);
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
		int[] values = new int[bytes.length / 2];

		int counter = 0;
		for (int cnt = 0; cnt < bytes.length / 2; cnt++)
			values[cnt] = FromByteArray(new byte[] { bytes[counter++], bytes[counter++] });
		return values;
	}
}
