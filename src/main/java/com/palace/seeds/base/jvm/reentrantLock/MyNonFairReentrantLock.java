package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.Validate;
import org.junit.Test;

import com.palace.seeds.utils.WRunnable;
import com.palace.seeds.utils.WThread;

public class MyNonFairReentrantLock {

	static final ReentrantLock rLock=new ReentrantLock();
	
	@Test
	public void unpark() {
		final AtomicInteger st = new AtomicInteger(0);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("start");
				int i = 0;
				while(true) {
					if(i > 999999) {
						i = 0;
					}
					cut(i);
					i++;
					if(st.get() == 3) {
						LockSupport.park(this);
					}
				}
			}
		});
		thread.start();
		//st.set(3);
		System.out.println("unparkThread");
		st.set(4);
		LockSupport.unpark(thread);
		System.out.println("unparkThread end");
	}
	
	static void cut(int n){
		double y=1.0;
		for(int i=0;i<=n;i++){
			double π=3*Math.pow(2, i)*y;
			System.out.println("第"+i+"次切割,为正"+(6+6*i)+"边形，圆周率π≈"+π);
			y=Math.sqrt(2-Math.sqrt(4-y*y));
		}
		
	}
 
	
	public static void mainRun(){
		otherThread();
		otherThread1();
		otherThread2();
		otherThread3();
		otherThreadTimeOut();
		otherThreadInterrupt();
	}
	
	static Condition cond;
	public static void condtion(){
		cond = rLock.newCondition();
		
	}
	public static void otherThread(){
		WThread.run("thread-0", new WRunnable() {
			@Override
			public void call() throws Exception {
				rLock.lock();
				System.out.println(" already run 0");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 0-1");
				rLock.unlock();
				
			}
		});
	}

	public static void otherThread1(){
		final Thread t = WThread.run("thread-1", new WRunnable() {
			@Override
			public void call() throws Exception {
				rLock.lock();
				System.out.println(" already run 1");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 1-1");
				rLock.unlock();
				
			}
		});
	}
		 
	 
	public static void otherThread2(){
		WThread.run("thread-2", new WRunnable() {
			@Override
			public void call() throws Exception {
				rLock.lock();
				System.out.println(" already run 2");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 1-2");
				rLock.unlock();
				
			}
		});
	}

	public static void otherThread3(){
		WThread.run("thread-3", new WRunnable() {
			@Override
			public void call() throws Exception {
				rLock.lock();
				System.out.println(" already run 3");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 1-3");
				rLock.unlock();
			}
		});
	}
	
	public static void otherThreadTimeOut(){
		WThread.run("thread-timeOut", new WRunnable() {
			@Override
			public void call() throws Exception {
				rLock.tryLock(10,TimeUnit.SECONDS);
				System.out.println(" already run TimeOut");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 1-TimeOut");
				rLock.unlock();
			}
		});
	}
	
	public static void otherThreadInterrupt() {
		WThread.run("thread-3", new WRunnable() {
			@Override
			public void call() throws Exception {
				rLock.lockInterruptibly();
				System.out.println(" already run interrupt");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run interrupt");
				rLock.unlock();
			}
		});
	}
	public static void main(String[] args) {
		mainRun();
		
		while(true){
			try {
				Thread.currentThread().sleep(60*3600*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
