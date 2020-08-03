package com.palace.seeds.net.netty.simple.demo;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class SimpleDec extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int headLen = 8;
		//如果读取的长度符合一个head的长度
		if(in.readableBytes() < headLen) {
			return;
		}
		int magicNum = in.readInt();
		int len = in.readInt();
		int entLen = len;
		if(in.readableBytes() < entLen) {
			return;
		}
		SimpleEntity ent = new SimpleEntity();
		ent.magicNum = magicNum;
		ent.len = len;
		byte[] data = new byte[len];
		in.readBytes(data);
		ent.msg = new String(data);
		System.err.println("readData->"+ent.toString());
		out.add(ent);
	}

}
