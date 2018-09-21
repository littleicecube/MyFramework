package com.jdk.newFeature;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.LockSupport;

import org.junit.Test;


public class CompleteFeature {

	
	@Test
	public void lock() throws Exception {
		final Object blocker = new Object();
		Thread t = new Thread(()->{
			System.out.println("inner thread start");
			LockSupport.park(blocker);
			System.out.println("innter thread end");
		});
		t.start();
		System.out.println("outer start");
		Thread.currentThread().sleep(10000);
		LockSupport.unpark(t);
		System.out.println("outer end");
		Thread.currentThread().sleep(30000);
	}
	@Test
	public void simpleCompletTest() throws Exception, ExecutionException {
		CompletableFuture<String> cmp = new CompletableFuture();
		new Thread(()->{
			try {
				Thread.currentThread().sleep(60000);
			} catch (InterruptedException e) {
			}
			System.out.println("innerThread end ");
			cmp.complete("end");
		}).start();
		System.out.println("outerRes start");
		String res = cmp.get();
		System.out.println("outerRes:"+res);
	}
}
