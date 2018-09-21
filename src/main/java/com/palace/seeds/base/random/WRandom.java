package com.palace.seeds.base.random;

import java.util.UUID;

import org.junit.Test;

public class WRandom {

	@Test
	public void testLen(){
		System.out.println(getString(32).length());
	}
	public static String getString(int len){
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<len/32;i++){
			sb.append(UUID.randomUUID().toString().replace("-", ""));
		}
		String str = UUID.randomUUID().toString().replace("-","").substring(0,len%32);
		sb.append(str);
		return sb.toString();
	}
}
