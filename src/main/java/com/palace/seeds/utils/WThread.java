package com.palace.seeds.utils;

import java.util.concurrent.TimeUnit;

public class WThread {

	public static void sleep(long l){
		try {
			Thread.currentThread().sleep(l);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void sleep1(){
		try {
			TimeUnit.SECONDS.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void sOne(){
		sleep(1000);
	}
	public static void sThree(){
		sleep(5000);
	}
	public static void sFive(){
		sleep(5000);
	}
	
	public static void sTen(){
		sleep(10000);
	}
	
	public static Thread run(String name,final WRunnable runnable){
		
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					runnable.call();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},name);
		t.start();
		return t;
	}
	
	
	public static void run(final WRunnable runnable){
		run("测试-"+System.currentTimeMillis(),runnable);
	}
	
	
}
