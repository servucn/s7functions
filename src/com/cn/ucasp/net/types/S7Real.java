package com.cn.ucasp.net.types;

public class S7Real {

	public static float FromByteArray(byte[] bytes) {
		
			final int iValue = ((bytes[3] & 0xFF) << 0) | ((bytes[2] & 0xFF) << 8) | ((bytes[1] & 0xFF) << 16)
					| ((bytes[0] & 0xFF) << 24);
			return Float.intBitsToFloat(iValue);
		
	}

	public static float FromDWord(int value) {
		byte[] b = S7DInt.ToByteArray(value);
		float d = FromByteArray(b);
		return d;
	}

	public static float FromDWord(long value) {
		byte[] b = S7DWord.ToByteArray(value);
		float d = FromByteArray(b);
		return d;
	}

	public static byte[] ToByteArray(float value) {
		byte[] bytes = new byte[4];
		int fbit = Float.floatToIntBits(value);

		for (int i = 0; i < 4; i++) {
			bytes[i] = (byte) (fbit >> (24 - i * 8));
		}
		return bytes;
	}

	public static byte[] ToByteArray(float[] value) {
		ByteArray arr = new ByteArray();
		for (Float val : value)
			arr.Add(ToByteArray(val));
		return arr.getArray();
	}

	public static float[] ToArray(byte[] bytes) {
		float[] values = new float[bytes.length / 4];

		int counter = 0;
		for (int cnt = 0; cnt < bytes.length / 4; cnt++)
			values[cnt] = FromByteArray(
					new byte[] { bytes[counter++], bytes[counter++], bytes[counter++], bytes[counter++] });

		return values;
	}



}
