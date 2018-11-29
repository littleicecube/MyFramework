package com.palace.seeds.base.local;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ThreadLocalClass {

	static ThreadLocal<String> local = new ThreadLocal<String>();
	
	
	@Test
	public void local1() throws InterruptedException {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				local.set("value");
				Object obj = local.get();
				System.out.println(obj);
			}
		}).start();
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				Object obj = local.get();
				System.out.println(obj);
			}
		}).start();
		
		Thread.currentThread().sleep(500000000);
	}
	
	
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
