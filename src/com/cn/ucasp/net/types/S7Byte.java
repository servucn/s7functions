package com.cn.ucasp.net.types;

public class S7Byte {

	public static byte[] ToByteArray(byte value) {
		byte[] bytes = new byte[] { value };
		return bytes;
	}

	public static byte FromByteArray(byte[] bytes) {
		return bytes[0];
	}
}
