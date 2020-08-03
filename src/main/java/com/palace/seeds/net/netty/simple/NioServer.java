package com.palace.seeds.net.netty.simple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.Set;

import org.junit.Test;

public class NioServer {

	static class EventLoop {
		final Selector selector;

		public EventLoop() throws Exception {
			selector = Selector.open();
		}

		public void run() throws Exception {
			// 循环检查仓库中是否存在待处理的数据
			while (true) {
				// 每隔1000毫秒检查一下是否有待处理的数据
				int count = selector.select(5000);
				// 没有待处理的数据,继续循环等待
				if (count == 0) {
					continue;
				} else {
					// 获取待处理的数据
					Set<SelectionKey> keySet = selector.selectedKeys();
					for (SelectionKey key : keySet) {
						Object att = key.attachment();
						ServerSocketChannel channel = (ServerSocketChannel) key.channel();
						SocketChannel socketChannel = (SocketChannel) channel.accept();
						System.err.println(socketChannel.socket().getLocalPort());
					}
				}

			}
		}
	}

	static class NioServerSocketChannelConfig {
		public ByteBuffer alloctor() {
			return ByteBuffer.allocate(256);
		}
	}

	static class ChannelPipeline {
		ChannelPipeline head, tail;

	}

	static class NioServerSocketChannel {
		ServerSocketChannel serverSocketChannel;
		NioServerSocketChannelConfig config;
		ChannelPipeline pipeline;

		public NioServerSocketChannel() throws Exception {
			serverSocketChannel = ServerSocketChannel.open();
			config = new NioServerSocketChannelConfig();
			pipeline = new ChannelPipeline();
		}

		public void register(EventLoop el) throws Exception {
			serverSocketChannel.register(el.selector, SelectionKey.OP_ACCEPT, this);
		}

	}

	@Test
	public void client() throws Exception {
		SocketChannel channel = SocketChannel.open();
		channel.connect(new InetSocketAddress(8848));
		Scanner reader = new Scanner(System.in);
		ByteBuffer buffer = ByteBuffer.allocate(1024);

		while (true) {
			System.out.println("put message for send to Server >");
			String line = reader.nextLine();
			if (line.equals("exit")) {
				break;
			}
			buffer.put(line.getBytes("UTF-8"));
			buffer.flip();
			channel.write(buffer);
			buffer.clear();

			int readLength = channel.read(buffer);
			if (readLength == -1) {
				break;
			}
			// 重置缓存游标
			buffer.flip();
			byte[] datas = new byte[buffer.remaining()];

			// 读取数据到数组
			buffer.get(datas);
			System.out.println("from server : " + new String(datas, "UTF-8"));
			// 清空缓存
			buffer.clear();
		}
	}

}
