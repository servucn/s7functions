package com.cn.ucasp.net.types;

import com.cn.ucasp.net.VarType;

public class DataSizeByte {
	public int GetSizeByte(VarType type) {
		switch (type) {
		case S7Boolean:
		case S7Byte:
			return 1;
		case S7Word:
		case S7Int:
		case S7Counter:
			return 2;
		case S7DInt:
		case S7Real:
		case S7DWord:
			return 4;		
		default:
			return 0;
		}
	}
}
