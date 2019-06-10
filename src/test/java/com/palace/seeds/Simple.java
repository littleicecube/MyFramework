package com.palace.seeds;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class Simple {
	
	@Test
	public void printVal() {
		System.out.println(1<<2);//4
		System.out.println(3<<2);//12
		System.out.println(4<<2);//16
		System.out.println(5<<2);//20
	}
	public static void main(String[] args) {
		List<String> list = Lists.newArrayList("xiaoming","xiaozhang");
		String[] arr = list.toArray(new String[list.size()]);
		doSome(arr);
		 
	}
	public static void doSome(String ...args) {
		for(String str :args) {
			System.out.println(str);
		}
	}
	
}
