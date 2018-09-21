package com.palace.experience.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class SocketManager {

	public static void main(String[] args) {
		new Thread(()->{
			try {
				server();
				Thread.currentThread().sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}) .start();
	}
	
	public static void server() throws Exception {
		ServerSocket server = new ServerSocket(8088);
		Socket ss =server.accept();
		InputStream is = ss.getInputStream();
		new Thread(()->{
			while(true) {
				byte[] b = new byte[1024];
				try {
					is.read(b);
					System.out.println("readPrint:"+new String(b));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		OutputStream os = ss.getOutputStream();
		new Thread(()->{
			while(true) {
				try {
					os.write("string val".getBytes());
					Thread.currentThread().sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
