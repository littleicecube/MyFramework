package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyReentrantLockCondtion {

	static final ReentrantLock rLock=new ReentrantLock();
	static final Condition cond = rLock.newCondition() ;
	public static void mainRun(){
		await();
		await1();
		//await2();
		//condtionTimeOut();		
		signal();
		signal1();
 
	}
	
	public static void signal(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				rLock.lock();
				cond.signal();
				rLock.unlock();
			}
		},"signal").start();
	}
	public static void signal1(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				rLock.lock();
				cond.signal();
				rLock.unlock();
			}
		},"signal-1").start();
	}
	public static void await(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					rLock.lock();
					cond.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					//cond.signal();
				}
			}
		},"await").start();
	}
	
	public static void await1(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					rLock.lock();
					cond.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					//cond.signal();
				}
				
			}
		},"await-1").start();
	}
	public static void await2(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					rLock.lock();
					cond.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					//cond.signal();
				}
				
			}
		},"await-2").start();
	}
	public static void awaitTimeOut(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					rLock.lock();
					cond.await(10,TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
					//cond.signal();
				}
				
			}
		},"await-TimeOut").start();
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
