# s7functions
西门子PLC 通信协议

```C#


S7TCPConnection connection = new S7TCPConnection()
				.withIp("192.168.72.146") //plc ip地址
				.withSolt(1) //安装槽
				.withCpu(CpuType.S71500)		// cpu类型		
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
```
