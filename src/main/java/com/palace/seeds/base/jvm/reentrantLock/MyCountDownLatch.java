package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class MyCountDownLatch {

	static final ReentrantLock rLock=new ReentrantLock();
	
	public static void mainRun() throws Exception{
		 CountDownLatch latch = new CountDownLatch(3);
		 //latch.countDown();
		 new Thread(()->{ latch.countDown();}) .start();
		 latch.await();
	}
	
	public static void main(String[] args) {
		try {
			mainRun();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
