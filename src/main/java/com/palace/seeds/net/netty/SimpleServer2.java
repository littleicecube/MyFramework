package com.palace.seeds.net.netty;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;

import org.junit.Test;

public class SimpleServer2 {
	
	@Test
	 public void testServer()  {
		 try {
			 SelectorProvider selectorProvider = SelectorProvider.provider();
			 
			 //创建一个selector
			 Selector selector = selectorProvider.openSelector();
			 //通过反射修改selector中的属性信息
			 SimpleSelectedSelectionKeySet selectedKeySet = selectorKey(selector);
	         //创建一个事件处理实例
	         SimpleNioServerSocketChannel simpNioServerSocketChannel = new SimpleNioServerSocketChannel();
	         //创建一个serverChannel实例
			 ServerSocketChannel  serverChannel = selectorProvider.openServerSocketChannel();
			 //设置为非阻塞
			 serverChannel.configureBlocking(false);
			 //将serverChannel实例注册到selectorIns上,并监听连接创建事件
			 serverChannel.register(selector, SelectionKey.OP_ACCEPT,simpNioServerSocketChannel);
			 //将serverChannel和本地端口进行绑定
			 serverChannel.bind(new InetSocketAddress(8848),100);
			 
			 while(true) {
				 int c = selector.select(3000);
				 if(c > 0) {
					 SelectionKey[] keySet = selectedKeySet.flip();
					 //遍历建立的连接
					for(SelectionKey seleKey : keySet) {
						//处理建立的连接
						if(seleKey != null) {
							//获取和连接绑定的处理程序
							SimpleNioServerSocketChannel sss = (SimpleNioServerSocketChannel) seleKey.attachment();
							//用绑定的处理程序来处理到来的连接
							sss.doRead(seleKey);
						}
					}
				 }
			 }
		 }catch(Exception e) {
			 e.printStackTrace();
		 }
	 }
	 
	 //=============================================================================
	 
	 class SimpleServerSocektChannel{
		public void doRead(SelectionKey sk) throws Exception {
	 
		}
	 }
	 
	 
	 
	 public static SimpleSelectedSelectionKeySet selectorKey(Selector selector) throws Exception {
		 //创建一个set通过反射设置到selector中
		 SimpleSelectedSelectionKeySet selectedKeySet = new SimpleSelectedSelectionKeySet();
		 
		 Class selectorImplClass = sun.nio.ch.SelectorImpl.class;
		 //获取Selector中声明的字段
        Field selectedKeysField = selectorImplClass.getDeclaredField("selectedKeys");
        selectedKeysField.setAccessible(true);
        //将上面创建的SelectorKeySet设置到selectorIns实例字段selectedKeys中
        selectedKeysField.set(selector, selectedKeySet);
        
		 //获取Selector中声明的字段
        Field publicSelectedKeysField = selectorImplClass.getDeclaredField("publicSelectedKeys");
        publicSelectedKeysField.setAccessible(true);
        //将上面创建的SelectorKeySet设置到selectorIns实例字段publicSelectedKeys中
        publicSelectedKeysField.set(selector, selectedKeySet);
		 return selectedKeySet;
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
}
