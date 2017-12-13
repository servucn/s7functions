package com.cn.ucasp.net;

import com.cn.ucasp.net.types.S7Word;

public class program {
	public static void main(String[] args) throws Exception {
		int fbit = Float.floatToIntBits(0.1f);  
	      
	    byte[] b = new byte[4];    
	    for (int i = 0; i < 4; i++) {    
	        b[i] = (byte) (fbit >> (24 - i * 8));    
	    }   
		S7TCPConnection connection = new S7TCPConnection()
				.withIp("192.168.72.146")
				.withSolt(1)
				.withCpu(CpuType.S71500)				
				.Open();
		//Object d=connection.Read("DB1.DBD2");
		System.out.println(connection.getErrorCode());
		//System.out.print(d);
		Object by=connection.Read(DataType.DataBlock, 1, 0,VarType.S7Real, 1);
		Object in=connection.Read("DB1.DBD0");
		System.out.println(connection.getErrorCode());
		Object by2=connection.Read(DataType.DataBlock, 1, 4,VarType.S7Real, 1);
		System.out.println(connection.getErrorCode());
		//connection.Write(DataType.DataBlock, 1, 2, 0.456f);
		System.out.println(by);
		System.out.println(by2);
		/*connection.Write("DB1.DBB0","1");		
		Object in=connection.Read("DB1.DBB0");
		System.out.println(in);
		
		
		
		connection.Write("DB1.DBW4",99);
		in=connection.Read("DB1.DBW4");
		System.out.println(in);
		
		connection.Write("DB1.DBD8",88);
		in=connection.Read("DB1.DBD8");
		System.out.println(in);
		
		
		connection.Write("DB1.DBX12.0",true);
		in=connection.Read("DB1.DBX12.0");
		System.out.println(in);
		
		Object object=connection.Read(DataType.DataBlock, 2, 0, VarType.S7Real, 1);
		System.out.println(connection.getErrorCode());
		
		System.out.println(object);*/
	}

}
