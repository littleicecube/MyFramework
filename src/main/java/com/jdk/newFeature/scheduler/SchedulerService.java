package com.jdk.newFeature.scheduler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;

import org.junit.Test;

public class SchedulerService {

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
