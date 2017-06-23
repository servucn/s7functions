package com.cn.ucasp.net;

import lombok.*;

public enum ErrorCode {
	NoError(0), WrongCPU_Type(1), ConnectionError(2), IPAdressNotAvailable(3),

	WrongVarFormat(10), WrongNumberReceivedBytes(11),

	SendData(20), ReadData(30), WriteData(50);
	@Setter
	@Getter
	private int Index;

	private ErrorCode(int i) {
		this.Index = i;
	}
}
