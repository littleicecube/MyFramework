package com.palace.seeds.net.netty;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.AbstractSet;
import java.util.Iterator;

import org.junit.Test;
import org.springframework.expression.spel.ast.Selection;

public class SimpleServer {
	
	 @Test
	 public void testServer()  {
		 try {
			 SelectorProvider selectorProvider = SelectorProvider.provider();
			 
			 //创建一个selector
			 Selector selectorIns = selectorProvider.openSelector();
			 
			 //创建一个set通过反射设置到selector中
			 SimpleSelectedSelectionKeySet selectedKeySet = new SimpleSelectedSelectionKeySet();
			 
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
			 
	         //创建一个事件处理实例
	         SimpleNioServerSocketChannel simpNioServerSocketChannel = new SimpleNioServerSocketChannel();
	         //创建一个serverChannel实例
			 ServerSocketChannel  serverChannel = selectorProvider.openServerSocketChannel();
			 //设置为非阻塞
			 serverChannel.configureBlocking(false);
			 //将serverChannel实例注册到selectorIns上,并监听连接创建事件
			 serverChannel.register(selectorIns, SelectionKey.OP_ACCEPT,simpNioServerSocketChannel);
			 //将serverChannel和本地端口进行绑定
			 serverChannel.bind(new InetSocketAddress(8848),100);
			 
			 while(true) {
				 int c = selectorIns.select(3000);
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
		 SimpleEventLoop[] eventLoop = null;
		 public SimpleNioEventLoopGroup(int nThread,SelectorProvider provider) {
			 for(int i=0;i<nThread;i++) {
				 eventLoop[i] = new SimpleEventLoop(selectorProvider);
			 }
		 }
		 
	 }
	 
	 public static class SimpleEventLoop {
		 Selector selectorIns;
		 SimpleSelectedSelectionKeySet selectedKeySet;
		 
		 public SimpleEventLoop(SelectorProvider  selectorProvider) {
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
			 ServerSocketChannel serverSocketChannel;
			 SimpleEventLoop eventLoop;
			 public SimpleServerSocektChannel() {
				 try {
					 serverSocketChannel = defaultProvider.openServerSocketChannel();
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
			 
			 public void register(SimpleEventLoop eventLoop) throws Exception {
				 this.eventLoop = eventLoop;
				 serverSocketChannel.register(eventLoop.selectorIns, SelectionKey.OP_ACCEPT, this);
			 }
		 }
	}

	class  SimpleNioServerSocketChannel{
		
		public void doRead(SelectionKey sk) {
			
		}
	}



	class SimpleSelectedSelectionKeySet extends AbstractSet<SelectionKey> {
	    private SelectionKey[] keysA;
	    private int keysASize;
	    private SelectionKey[] keysB;
	    private int keysBSize;
	    private boolean isA = true;
	
	    SimpleSelectedSelectionKeySet() {
	        keysA = new SelectionKey[1024];
	        keysB = keysA.clone();
	    }
	
	    @Override
	    public boolean add(SelectionKey o) {
	        if (o == null) {
	            return false;
	        }
	
	        if (isA) {
	            int size = keysASize;
	            keysA[size ++] = o;
	            keysASize = size;
	            if (size == keysA.length) {
	                doubleCapacityA();
	            }
	        } else {
	            int size = keysBSize;
	            keysB[size ++] = o;
	            keysBSize = size;
	            if (size == keysB.length) {
	                doubleCapacityB();
	            }
	        }
	
	        return true;
	    }
	
	    private void doubleCapacityA() {
	        SelectionKey[] newKeysA = new SelectionKey[keysA.length << 1];
	        System.arraycopy(keysA, 0, newKeysA, 0, keysASize);
	        keysA = newKeysA;
	    }
	
	    private void doubleCapacityB() {
	        SelectionKey[] newKeysB = new SelectionKey[keysB.length << 1];
	        System.arraycopy(keysB, 0, newKeysB, 0, keysBSize);
	        keysB = newKeysB;
	    }
	
	    SelectionKey[] flip() {
	        if (isA) {
	            isA = false;
	            keysA[keysASize] = null;
	            keysBSize = 0;
	            return keysA;
	        } else {
	            isA = true;
	            keysB[keysBSize] = null;
	            keysASize = 0;
	            return keysB;
	        }
	    }
	
	    @Override
	    public int size() {
	        if (isA) {
	            return keysASize;
	        } else {
	            return keysBSize;
	        }
	    }
	
	    @Override
	    public boolean remove(Object o) {
	        return false;
	    }
	
	    @Override
	    public boolean contains(Object o) {
	        return false;
	    }
	
	    @Override
	    public Iterator<SelectionKey> iterator() {
	        throw new UnsupportedOperationException();
	    }
	}