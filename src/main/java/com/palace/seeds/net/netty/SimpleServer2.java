package com.palace.seeds.net.netty;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.AbstractSet;
import java.util.Iterator;

import org.junit.Test;

public class SimpleServer2 {
	
	 @Test
	 public void testServer()  {
		 try {
			 
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
	 @Test
	 public void testClient() {
		 try {
			SelectorProvider.provider().openSocketChannel().connect(new InetSocketAddress("127.0.0.1",8848));
			Thread.currentThread().sleep(10*60*1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 //SingleThreadEventExecutor
	 public static class SimpleNioEventLoopGroup{
		 SelectorProvider selectorProvider = SelectorProvider.provider();
		 EventLoop[] eventLoop = null;
		 public SimpleNioEventLoopGroup(int nThread,SelectorProvider provider) {
			 for(int i=0;i<nThread;i++) {
				 eventLoop[i] = new EventLoop(selectorProvider);
			 }
		 }
		 
	 }
	 
	 public static class EventLoop {
		 Selector selectorIns;
		 SimpleSelectedSelectionKeySet selectedKeySet;
		 
		 public EventLoop(SelectorProvider  selectorProvider) {
			 try {
				 selectorIns = selectorProvider.openSelector();
				 //创建一个set通过反射设置到selector中
				 selectedKeySet = new SimpleSelectedSelectionKeySet();
				 
				 Class selectorImplClass = sun.nio.ch.SelectorImpl.class;
				 //获取Selector中声明的字段
		         Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
		         selectedKeysField.setAccessible(true);
		         //将上面创建的SelectorKeySet设置到selectorIns实例字段selectedKeys中
		         selectedKeysField.set(selectorIns, selectedKeySet);
		         
				 //获取Selector中声明的字段
		         Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
		         publicSelectedKeysField.setAccessible(true);
		         //将上面创建的SelectorKeySet设置到selectorIns实例字段publicSelectedKeys中
		         publicSelectedKeysField.set(selectorIns, selectedKeySet);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void doRun() {
			 try {
				 while(true) {
					 int c = selectorIns.select(3000);
					 if(c > 0) {
						 SelectionKey[] keySet = selectedKeySet.flip();
						 //遍历建立的连接
						for(SelectionKey seleKey : keySet) {
							//处理建立的连接
							if(seleKey != null) {
								System.out.println(seleKey.toString());
								selectedKeySet.remove(seleKey);
							}
						}
					 }
				 }
			 }catch(Exception e) {
				 e.printStackTrace();
			 }
			 
		 }
		 
		 Thread thread  = new Thread(new Runnable() {
			@Override
			public void run() {
				doRun();
			}
		});
		 
		 public void Register() {
			 thread.start();
		 }
		 
	 }
	
	 static class SimpleServerSocektChannel{
			 SelectorProvider defaultProvider = SelectorProvider.provider();
			 SocketChannel serverChannel;
			 
			 public SimpleServerSocektChannel() {
				 try {
					serverChannel = defaultProvider.openSocketChannel();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
	
		 }
	}
