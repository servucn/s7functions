package com.cn.ucasp.net;

import lombok.Getter;
import lombok.Setter;

public enum  CpuType {
    S7200(0),    S7300(10),    S7400(20),    S71200(30),    S71500(40);	
	@Setter
	@Getter
	private int Index ;
     
    private CpuType(  int index ){
        this.Index = index ;
    }

}
