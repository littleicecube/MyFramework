package com.palace.seeds.net.netty.simple.demo;

public class SimpleEntity {

	public int magicNum = 1;
	public int len;
	public String msg;
	
	public SimpleEntity() {
		
	}
	public SimpleEntity(String msg) {
		this.msg = msg;
		this.len = msg.getBytes().length;
	}
	@Override
	public String toString() {
		return "SimpleEntity [magicNum=" + magicNum + ", len=" + len + ", msg=" + msg + "]";
	}
	
	
}
