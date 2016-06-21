package com.android.icmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Reachable {
	public static void main(String[] args) throws InterruptedException {
		InetAddress thisComputer = null;
		
		while (true) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter names and IP Addresses. Enter \"exit\" to quit");
		
		try {
			while (true) {
				String host = in.readLine();
				if (host.equalsIgnoreCase("exit") || host.equalsIgnoreCase("quit")){
					break;
				}
				
				try {
					thisComputer = InetAddress.getByName(host);
					System.out.println(thisComputer.getHostAddress());
					Process p1 = Runtime.getRuntime().exec("ping -n 1 " + thisComputer.getHostAddress());
					int returnVal = p1.waitFor();
					boolean reachable = (returnVal==0);
					
					if (reachable)
						System.out.printf("%s is reachable \n", thisComputer.getHostName());
					else
						System.out.printf("%s is unreachable \n", thisComputer.getHostName());
				} catch (UnknownHostException e) {
					System.out.println("Cannot find host " + host);
				}
			}
		} catch (IOException e) {
			System.err.println(e);
		}
		}
	}
}
