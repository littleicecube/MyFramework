package com.palace.seeds.net.netty;

import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Random;

import io.netty.channel.ChannelOption;

public class SimpleServer {
	
	public static void main(String[] args) throws Exception {
		SimpleNioEventLoopGroup bossGroup = new SimpleNioEventLoopGroup(1);
		SimpleNioEventLoopGroup workerGroup = new SimpleNioEventLoopGroup(4);
		
		SimpleServerBootstrap b = new SimpleServerBootstrap();
        b.group(bossGroup, workerGroup)
         .channel(SimpleNioServerSocketChannel.class)
         .option(ChannelOption.SO_BACKLOG, 100)
         .bind(8848);
	}
}
	/**
	 * 事件循环类,作为一个独立的线程,在线程启动后通过循环不断的执行selector.select(xxx)操作获取到来的事件
	 * 1)创建selector
	 * 2)将创建的channel(有ServerSocketChannel和SocketChannel类型)注册到selector上
	 * 3)注册channel的同时并监听感兴趣的事件,如ServerSocketChannel类型的channel会监听SelectionKey.OP_ACCEPT类型的事件
	 * 4)在循环中通过selector.select(xxx)获取到来的事件key
	 * 5)通过获取和key绑定的处理实例来处理到来的事件
	 */
	class SimpleEventLoop {
		 Selector selector;
		 SimpleSelectedSelectionKeySet selectedKeySet;
		 
		 public SimpleEventLoop(SelectorProvider  selectorProvider) {
			 try {
				 selector = selectorProvider.openSelector();
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		 public void doRun() {
			 try {
				 while(true) {
					 int c = selector.select(3000);
					 if(c > 0) {
						 SelectionKey[] keySet = selectedKeySet.flip();
						 //遍历建立的连接
					for(SelectionKey seleKey : keySet) {
						//处理建立的连接
								if(seleKey != null) {
									System.out.println(seleKey.toString());
									Object obj = seleKey.attachment();
									if(obj instanceof SimpleNioServerSocketChannel) {
										((SimpleNioServerSocketChannel) obj).doRead(seleKey);
									}else if(obj instanceof SimpleNioSocketChannel) {
										((SimpleNioSocketChannel) obj).doRead(seleKey);
									}
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
	
	class SimpleNioEventLoopGroup{
		 SelectorProvider selectorProvider = SelectorProvider.provider();
		 SimpleEventLoop[] eventLoopArr = null;
		 public SimpleNioEventLoopGroup(int nThread) {
			 for(int i=0;i<nThread;i++) {
				 eventLoopArr[i] = new SimpleEventLoop(selectorProvider);
			 }
		 }
		 public SimpleEventLoop chooseOne() {
			 if(eventLoopArr.length ==0) {
				 return eventLoopArr[0];
			 }else{
				 return eventLoopArr[new Random().nextInt()%eventLoopArr.length];
			 }
		 }
	}
	
	
	
	
	class SimpleServerBootstrap{
		 //创建一个线程数组,实例中只包含一个事件循环线程,用来处理注册在selector上的channel的连接创建事件
		SimpleNioEventLoopGroup bossGroup = new SimpleNioEventLoopGroup(1);
		//创建一个线程数组,实例中只包含多个事件循环线程,用来处理注册在selector上的channel的读写事件
		SimpleNioEventLoopGroup workerGroup = new SimpleNioEventLoopGroup(4);
		//连接创建事件的channel和其事件到来的处理代码
		Class<SimpleNioServerSocketChannel> simpleNioServerSocketChannel;
		//读写建事件的channel和其事件到来的处理代码
		Class<SimpleNioSocketChannel> simpleNioSocketChannel;
		
		public SimpleServerBootstrap option(Object key,Object val) {
			return this;
		}
		
		public SimpleServerBootstrap group(SimpleNioEventLoopGroup bossGroup,SimpleNioEventLoopGroup workerGroup) {
			this.bossGroup  = bossGroup;
			this.workerGroup = workerGroup;
			return this;
		}             
		public SimpleServerBootstrap channel(Class<SimpleNioServerSocketChannel>  clazz) {
			this.simpleNioServerSocketChannel = clazz;
			return this;
		}
		public void bind(int port) throws Exception, IllegalAccessException {
			SimpleNioServerSocketChannel simpleNioServerSocketChannel  = this.simpleNioServerSocketChannel.newInstance();
			simpleNioServerSocketChannel.register(bossGroup.chooseOne());
			simpleNioServerSocketChannel.serverSocketChannel.bind(new InetSocketAddress(port),100);
		}
	}

	 class SimpleChannelPipeline{
		 
	 }
	 class  SimpleNioServerSocketChannel{
		SimpleServerBootstrap bootStrap ;
		SelectorProvider defaultProvider = SelectorProvider.provider();
		//作为锚点,监听到来的连接
		ServerSocketChannel serverSocketChannel;
		//事件循环线程,线程中通过循环不断监听到来的连接事件
		SimpleEventLoop eventLoop;
		//和监听channel绑定的管道服务
		SimpleChannelPipeline channelPipeline;
		public SimpleNioServerSocketChannel() {
			try {
				serverSocketChannel = defaultProvider.openServerSocketChannel();
				channelPipeline = new SimpleChannelPipeline();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//将创建的监听channel注册到,循环线程的selector上
		public void register(SimpleEventLoop eventLoop) throws Exception {
			this.eventLoop = eventLoop;
			serverSocketChannel.register(eventLoop.selector, SelectionKey.OP_ACCEPT, this);
		}
		
		public void doRead(SelectionKey sk) throws Exception {
			SelectableChannel channel = sk.channel();
			SimpleNioSocketChannel socketChannel = new SimpleNioSocketChannel(this, channel);
			socketChannel.register(bootStrap.workerGroup.chooseOne());
		}
	 }
 
	 class SimpleNioSocketChannel{
		 //事件循环线程,线程中通过循环不断监听到来的读写事件
		 SimpleEventLoop eventLoop;
		 //serverChannel是父级channel,是监听channel
		 SimpleNioServerSocketChannel parentChannel;
		 //建立连接时创建的channel
		 SelectableChannel socketChannel;
		 //连接channel的管道服务
		 SimpleChannelPipeline channelPipeline;
		 public SimpleNioSocketChannel(SimpleNioServerSocketChannel parent,SelectableChannel channel) {
			try {
				parentChannel = parent;
				socketChannel = channel;
				channelPipeline = new SimpleChannelPipeline();
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
		 public void register(SimpleEventLoop eventLoop) throws Exception {
			this.eventLoop = eventLoop;
			//将连接建立的channel注册到读写事件专用的seletor上
			socketChannel.register(eventLoop.selector, SelectionKey.OP_READ, this);
		 }	 
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
