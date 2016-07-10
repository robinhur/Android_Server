package com.android.icmp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EchoAdvancedThreadServer {
	public static void main(String args[]) {
		try {
			ServerThread echothread = new ServerThread(6000);
			echothread.start();
			
			echothread.join();
			System.out.println("Completed shutdown");
		} catch (Exception e) {
			System.err.println("Interrupted before accept thread completed");
			System.exit(1);
		}
	}
}

class ServerThread extends Thread {
	
	private ServerSocket server;
	private boolean loop;
	private final List<AdvancedEchoThread> threadList;

	public ServerThread(int port) throws IOException {
		super();
		server = new ServerSocket(port);
		server.setSoTimeout(3000);
		threadList = new ArrayList<AdvancedEchoThread>();
		loop = true;
	}
	
	public void run() {
		System.out.println("Connection Waiting...");
		
		while(loop) {
			try{
				Socket sock = server.accept();
				AdvancedEchoThread thread = new AdvancedEchoThread(sock);
				thread.start();
				threadList.add(thread);
			} catch (IOException e) {
				System.out.println("Server Thread cannot started "+e.toString());				
			}
		}
	}
	
	public void destroy() {
		loop = false;
		for (int i = 0; i<threadList.size(); i++) {
			AdvancedEchoThread t = threadList.remove(i);
			t.quit();
			t.interrupt();
			if (t.isAlive()) {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}

class AdvancedEchoThread extends Thread{
	private Socket sock;
	private int mCount;

	public AdvancedEchoThread(Socket sock) {
		this.sock = sock;
		mCount = 0;
	}
	
	public void quit() {
		if(sock != null) {
			try{
				sock.close();
				sock = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void run() {
		try {
			InetAddress inetaddr = sock.getInetAddress();
			System.out.println(inetaddr.getHostAddress() + " has connected");
			
			OutputStream out = sock.getOutputStream();
			InputStream in = sock.getInputStream();
			PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
			
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "MS949"));
			String line = null;
			try {
				while ((line=br.readLine()) != null) {
					System.out.println("Received string from client : " + line);
					pw.println(line);
					pw.flush();
				}
			} catch (IOException e) {
				System.out.println("Timeout Exception : " + mCount++);
			} finally {
				System.out.println(inetaddr.getHostAddress() + " disconnected");
				pw.close();
				br.close();
				sock.close();
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}