package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.palace.seeds.utils.WRunnable;
import com.palace.seeds.utils.WThread;

public class MyNonFairReentrantLock {

	static final ReentrantLock rLock=new ReentrantLock();
	public static void mainRun(){
		otherThread();
		otherThread1();
		otherThread2();
		otherThread3();
		otherThreadTimeOut();
	}
	
	static Condition cond;
	public static void condtion(){
		cond = rLock.newCondition();
		
	}
	public static void otherThread(){
		WThread.run("thread-0", new WRunnable() {
			@Override
			public void call() throws Exception {
				rLock.tryLock();
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
		WThread.run("thread-3", new WRunnable() {
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
