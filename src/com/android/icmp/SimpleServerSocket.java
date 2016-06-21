package com.android.icmp;

import java.io.*;
import java.net.*;

public class SimpleServerSocket {
	public static void main(String[] args) throws IOException {
		final int portNumber = 6000;
		System.out.println("Creataing server socket on port " + portNumber);
		ServerSocket serverSocket = new ServerSocket(portNumber);
		
		while(true) {
			Socket socket = serverSocket.accept();
			
			OutputStream os = socket.getOutputStream();
			PrintWriter pw = new PrintWriter(os, true);
			pw.println("What's your name");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String str = br.readLine();
			
			pw.println("Hello, " + str);
			socket.close();
			System.out.println("Just said hello to : " + str);
		}
	}
}
