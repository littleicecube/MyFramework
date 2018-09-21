package com.palace.seeds.net.nio;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;



public class Server {
	
	static Selector selector;
	static{
		try {
			selector = Selector.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run() throws Exception{
		
		ServerSocketChannel serverChannel  = ServerSocketChannel.open();
		
	}
}
