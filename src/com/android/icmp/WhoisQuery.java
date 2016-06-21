package com.android.icmp;

import java.io.*;
import java.net.*;

public class WhoisQuery {
	public static void main(String[] args) {
		String domainNameToCheck = "www.google.com";
		try {
			performWhoisQuery("whois.internic.net", 43, domainNameToCheck);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void performWhoisQuery(String host, int port, String query) throws Exception {
		System.out.println("**** Performing whois query for '" + query + "' at " + host + ":" + port);
		
		//Socket socket = new Socket(host, port);
		Socket socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(host, port);
		int timeout = 2000;
		socket.connect(socketAddress, timeout);
		
		Writer out = new OutputStreamWriter(socket.getOutputStream(), "ISO-8859-1");
		out.write(query);
		out.write("\r\n");
		out.flush();
		
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader in = new BufferedReader(isr);
		
		String line = "";
		while ((line = in.readLine()) != null) {
			System.out.println(line);
		}
	}
}
