package com.palace.seeds.base.local;

import org.junit.Test;

public class ThreadLocalClass {

	static ThreadLocal<String> local = new ThreadLocal<String>();
	
	@Test
	public void getLocalValue(){
		local.set("name");
		String str = local.get();
		Thread t = Thread.currentThread();
		
		System.out.println(str);
	}
	public static void getMap(Thread t) {
		 
	}
}
