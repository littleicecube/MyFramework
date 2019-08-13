package com.palace.seeds.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class Server {

	Map<String,String> map = new ConcurrentHashMap<String,String>();
	@Test
	public void myServer() throws Exception{
		final ServerSocket sSocket = new ServerSocket(8899);
		Socket socket ;
		while(true) {
			socket = sSocket.accept();
			System.out.println("accepted");
			socket.setSoTimeout(20*1000);
			int soTime = socket.getSoTimeout();
			System.out.println("soTime:"+soTime);
			read(socket.getInputStream());
			write(socket.getOutputStream());
		}
	}
	public void read(final InputStream is) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					byte[] b = new byte[1024];
					int l=0;
					while((l = is.read(b)) > 0){
						String str = new String(b);
						map.put(str, str);
						System.out.println("===server Recv Msg:"+str);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public void write(final OutputStream os) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true){
						if(map.size() > 0 ) {
							for(Map.Entry<String,String> ent : map.entrySet()) {
								String key = ent.getKey();
								String val = map.remove(key);
								String ss = "serverWriteMsg"+val+System.currentTimeMillis();
								os.write(ss.getBytes());
								os.flush();
								break;
							}
							
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
	
}
