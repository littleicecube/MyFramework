package com.jdk.newFeature.scheduler;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class SchedulerService {
	static AtomicInteger c = new AtomicInteger();
	static ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
	
	@Test
	public void uuid() {
		String ss = UUID.randomUUID().toString();
		System.out.println(ss);
	}
	@Test
	public void testSchdulerCallStack() throws Exception {
		sche("be");
		synchronized (c) {
			c.wait(1000000);
		}
	}
	
	public static void sche(String sr) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				System.out.println("sr:"+c.incrementAndGet());
				sche(sr);
			}
		};
		service.schedule(r, 1, TimeUnit.SECONDS);
	}
	@Test
	public void testScheduler() throws Exception {
		ScheduledThreadPoolExecutor se = new ScheduledThreadPoolExecutor(1);
		se.execute(()->{System.out.println("msg");});
		se.schedule(()->{System.out.println("msg:sche");}, 3, TimeUnit.SECONDS);
		se.scheduleAtFixedRate(()->{System.out.println("msg:atFixedRate");}, 1, 2, TimeUnit.SECONDS);
		Thread.currentThread().sleep(30000);
	}
	
	ThreadPoolExecutor threadPool =null;
	@Test
	public void testVal() throws Exception {
		for(int i=0;i< 200;i++) {
			threadPool = new ThreadPoolExecutor(1, 1,20, TimeUnit.SECONDS,new LinkedBlockingQueue(500),new DiscardPolicy());
		}
		
		Thread.currentThread().sleep(5*60*1000);
	}
}
