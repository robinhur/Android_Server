package com.android.icmp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Lookup {
	public static void main(String[] args) {
		InetAddress address = null;
		try {
			address = InetAddress.getByName(args[0]);
			System.out.println("Name : " + address.getHostName());
			System.out.println("Addr : " + address.getHostAddress());
		} catch (UnknownHostException e){
			System.out.println("Name : " + args[0]);
			System.out.println("Addr : no_ip");
		}
	}
}
