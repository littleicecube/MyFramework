package com.palace.seeds.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.junit.Test;

import com.palace.seeds.utils.WThread;

public class Client {
	
	@Test
	public void commClient() throws Exception{
		run(8899);
		WThread.sleep(100000000);
	}
	@Test
	public void myClientForNetty() throws Exception{
		run(8007);
		WThread.sleep(100000000);
	}
	
	public void run(int port) throws Exception{
		final Socket socket =new Socket("127.0.0.1",  port);
		System.out.println("client connect...");
		//socket.setSoTimeout(1000*20);
		final OutputStream out = socket.getOutputStream();
		final InputStream in  = socket.getInputStream();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						System.out.println("please input something:");
						Scanner scan = new Scanner(System.in);
						String msg = scan.next();
						msg = "clientMsg_"+msg;
						out.write(msg.getBytes());
						out.flush();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	 
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] bb = new byte[1024];
					int len = 0;
					while((len = in.read(bb)) > 0) {
						System.out.println("client recv msg:"+new String(bb));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	 
	}
}
