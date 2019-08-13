package com.palace.seeds.net.netty.simple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

public class SimpleServerNio {
	//channel创建事件的处理代码
	abstract class SimpleServerSocektChannel{
		public void doRead(SelectionKey sk) {}
	}
	//channel读写事件的处理代码
	abstract class SimpleSocektChannel{
		public void doRead(SelectionKey sk) {}
		public void doWrite(SelectionKey sk) {}
		public void handler(SelectableChannel ch) {}
	}
	
	@Test
	public void server() throws Exception  {
		final SelectorProvider selectorProvider = SelectorProvider.provider();
		//channel创建事件的selector
		final Selector selectorForChannelAccept = selectorProvider.openSelector();
		//channel读事件,写事件的selector
		final Selector selectorForChannelReadWrite= selectorProvider.openSelector();
		
		//读写事件的处理逻辑
		final SimpleSocektChannel socektChannel = new SimpleSocektChannel() {
			@Override
			public void doRead(SelectionKey sk) {
				SocketChannel  ch =(SocketChannel) sk.channel();
				ByteBuffer dataBuff = ByteBuffer.allocate(1024);
				try {
					int len = ch.read(dataBuff);
					System.out.println("readVal:"+new String(dataBuff.array()));
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				System.out.println("readVal:"+sk.toString());
			}
			@Override
			public void doWrite(SelectionKey sk) {
				System.out.println("writeVal:"+sk.toString());
			}
		};
		//连接创建事件的处理逻辑
		final SimpleServerSocektChannel serverSocektChannel = new SimpleServerSocektChannel() {
			@Override
			public void doRead(SelectionKey sk)  {
				try {
					ServerSocketChannel serverSocketChannel = (ServerSocketChannel)sk.channel();
					//获取建立连接的channel
					final SelectableChannel ch = serverSocketChannel.accept();
					//设置为非阻塞
					ch.configureBlocking(false);
					//将创建的channel注册到读写事件的selector上
					ch.register(selectorForChannelReadWrite, SelectionKey.OP_READ | SelectionKey.OP_WRITE, socektChannel);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		};

		//连接创建事件处理线程
		Thread	acceptThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
 					//创建一个serverChannel实例
					ServerSocketChannel  serverChannel = selectorProvider.openServerSocketChannel();
					//设置为非阻塞
					serverChannel.configureBlocking(false);
					//将serverChannel实例注册到selectorIns上,并监听连接创建事件
					serverChannel.register(selectorForChannelAccept, SelectionKey.OP_ACCEPT,serverSocektChannel);
					//将serverChannel和本地端口进行绑定
					serverChannel.bind(new InetSocketAddress(8848),100);
					while(true) {
						int c = selectorForChannelAccept.select(3000);
						if(c > 0) {
							Set<SelectionKey> keySet = selectorForChannelAccept.selectedKeys();
						    Iterator<SelectionKey> ite = keySet.iterator();
						    while(ite.hasNext()) {
						    	SelectionKey seleKey = ite.next();
								//处理建立的连接
								if(seleKey != null) {
									//获取和连接绑定的处理程序
									Object obj = seleKey.attachment();
									SimpleServerSocektChannel serverSocketChannel = (SimpleServerSocektChannel) obj;
									//用绑定的处理程序来处理到来的连接
									serverSocketChannel.doRead(seleKey);
									ite.remove();
								}
							
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		},"连接创建事件处理线程");
		
		//读写事件处理线程
		Thread readAndWriteThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while(true) {
						int c = selectorForChannelReadWrite.select(3000);
						if(c > 0) {
							Set<SelectionKey> keySet = selectorForChannelReadWrite.selectedKeys();
							//遍历到来的读写事件
							for(SelectionKey seleKey : keySet) {
								//处理读写事件
								if(seleKey != null) {
									//获取和连接绑定的处理程序
									SimpleSocektChannel scoketChannel = (SimpleSocektChannel) seleKey.attachment();
									//用绑定的处理代码来处理读写事件
									scoketChannel.doRead(seleKey);
								}
							}
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		},"读写事件处理线程"); 
		//启动连接创建线程
		acceptThread.start();
		//启动读写事件线程
		readAndWriteThread.start();
		Thread.currentThread().sleep(30*60*1000);
	 
	}
	 
	 @Test
	 public void testClient() {
		 try {
			SocketChannel ch = SelectorProvider.provider().openSocketChannel();
			if(ch.connect(new InetSocketAddress("127.0.0.1",8848))) {
				String msg = " helle world";
				ByteBuffer buffer = ByteBuffer.allocate(msg.getBytes().length);
				buffer.put(msg.getBytes());
				buffer.flip();
				while(buffer.hasRemaining()) {
					ch.write(buffer);
				}
			}
			Thread.currentThread().sleep(10*60*1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}
