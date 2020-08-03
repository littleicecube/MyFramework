package com.palace.seeds.net.netty.simple.demo;

import java.nio.ByteBuffer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public final class SimpleServer {

	/**

	 */
    static final boolean SSL = System.getProperty("ssl") != null;
    static final int PORT = Integer.parseInt(System.getProperty("port", "8007"));

    public static void main(String[] args) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class)
             .option(ChannelOption.SO_BACKLOG, 100)
             .handler(new LoggingHandler(LogLevel.INFO))
             .childHandler(new ChannelInitializer<SocketChannel>() {
            	    @Override
            	    public void initChannel(SocketChannel ch) throws Exception {
            	        ChannelPipeline pipeline = ch.pipeline();
            	        pipeline.addLast(new SimpleDec());
            	        pipeline.addLast(new SimpleEnc());
            	        pipeline.addLast(new IdleStateHandler(10,20,30));
            	        pipeline.addLast(new SimpleChannelInboundHandler<SimpleEntity>() {
							@Override
							protected void channelRead0(ChannelHandlerContext ctx, SimpleEntity msg) throws Exception {
								System.err.println("receiveEntity-->"+msg);
							}
						    @Override
						    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
						        cause.printStackTrace();
						        ctx.close();
						    }
						});
            	    }
			});
            ChannelFuture f = b.bind(PORT).sync();
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
    public void something() {
    	ByteBuffer.allocateDirect(100).duplicate();
    }
    
}
