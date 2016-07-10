package com.android.icmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class EchoAdvancedThreadClient {
	public static void main(String args[]) {
		String host;
		int port = 6000;
		try{
			if (args.length > 0)
				host = args[0];
			else {
				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				System.out.println("Enter names and IP address\nEnter \"quit\" to exit.");
				host = in.readLine();
			}
			if (host.length() == 0)
				host = "127.0.0.1";
			
			AdvancedEchoThread thread = new AdvancedEchoThread(host, port);
			thread.start();
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	static class AdvancedEchoThread extends Thread {
		private Socket sock;

		public AdvancedEchoThread(String host, int port) throws IOException {
			sock = new Socket(host, port);
		}
		
		public void quit() {
			if (sock != null) {
				try {
					sock.close();
					sock = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void run(){ 
			try {
				System.out.println("Type string want to send to server");
				
				BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in, "MS949"));
				OutputStream out = sock.getOutputStream();
				InputStream in = sock.getInputStream();
				PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				
				String line = null;
				while((line = keyboard.readLine()) != null){
					if (line.equals("quit")) break;
					pw.println(line);
					pw.flush();
					String echo = br.readLine();
					System.out.println("Recevied from server : " + echo);
				}
				pw.close();
				br.close();
				sock.close();
				System.out.println("Connection closed");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
