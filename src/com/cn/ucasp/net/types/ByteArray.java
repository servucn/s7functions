package com.cn.ucasp.net.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ByteArray {
	List<java.lang.Byte> list = new ArrayList<java.lang.Byte>();

	public byte[] getArray() {
		if (list == null || list.size() < 0)
			return null;
		byte[] bytes = new byte[list.size()];
		int i = 0;
		Iterator<java.lang.Byte> iterator = list.iterator();
		while (iterator.hasNext()) {
			bytes[i] = iterator.next();
			i++;
		}

		return bytes;
	}

	public ByteArray() {
		list = new ArrayList<java.lang.Byte>();
	}

	public ByteArray(int size) {
		list = new ArrayList<java.lang.Byte>(size);
	}

	public void Clear() {
		list = new ArrayList<java.lang.Byte>();
	}

	public void Add(byte item) {
		list.add(item);
	}

	public void Add(byte[] items) {
		for (java.lang.Byte bt : items) {
			list.add(bt);
		}
	}
}
