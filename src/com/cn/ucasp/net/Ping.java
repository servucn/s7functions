package com.cn.ucasp.net;

import java.io.IOException;
import java.net.InetAddress;

public class Ping {
	public boolean isReachable(String ip) throws IOException {
		int timeOut = 3000; 
		boolean status = InetAddress.getByName(ip).isReachable(timeOut);
		return status;
	}
}
