package com.cn.ucasp.net.types;

public class S7String {

	public static byte[] ToByteArray(String value) {
		String txt = (String) value;
		char[] ca = txt.toCharArray();
		byte[] bytes = new byte[txt.length()];
		char c=ca[0];
		for (int cnt = 0; cnt <= ca.length - 1; cnt++)
			bytes[cnt] = (byte) ca[cnt];
		return bytes;
	}

	public static String FromByteArray(byte[] bytes) {
		return new String(bytes);
	}


}
