package com.palace.seeds.net.netty.simple.demo;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class SimpleEnc extends MessageToByteEncoder<SimpleEntity>{

	@Override
	protected void encode(ChannelHandlerContext ctx, SimpleEntity msg, ByteBuf out) throws Exception {
		out.writeInt(msg.magicNum);
		out.writeInt(msg.len);
		out.writeBytes(msg.msg.getBytes());
		System.err.println("sendMsg->"+msg.toString());
		ctx.writeAndFlush(out);
	}

	 

}
