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
				.withIp("127.0.0.1")
				.Open();
		connection.Write("DB1.DBB0","1");		
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
		System.out.println(object);
	}
}
