package com.android.icmp;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LookupAll {
	public static void main(String[] args) {
		InetAddress address[]=null;
		
		try {
			address = InetAddress.getAllByName(args[0]);
			for (InetAddress each: address) {
				System.out.println("Name : "+each.getHostName());
				System.out.println("Addr : "+each.getHostAddress());
				System.out.println("Canonical : "+each.getCanonicalHostName());
			}
		} catch (UnknownHostException e) {
			System.out.println("Name : "+args[0]);
		}
	}
}
