package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyFairReentrantLock {

	static final ReentrantLock rLock=new ReentrantLock();
	static final Condition cond = rLock.newCondition() ;
	public static void mainRun(){
		condtion();
		condtion1();
		signal();
		signal1();

/*		
		condtion2();
		condtion3();
		otherThread(); 
		otherThread1();
		otherThread2();
		otherThread3();*/

	}
	
	public static void signal(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					rLock.unlock();
					rLock.lock();
					cond.signal();
					cond.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
				}
				
			}
		},"signal-0").start();
	}
	public static void signal1(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					rLock.lock();
					cond.signal();
					cond.await();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}finally{
				}
				
			}
		},"signal-1").start();
	}
	public static void condtion(){
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
		},"cond-0").start();
	}
	
	public static void condtion1(){
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
		},"cond-1").start();
	}
	public static void condtion2(){
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
		},"cond-2").start();
	}
	public static void condtion3(){
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
		},"cond-3").start();
	}
	public static void condtion4(){
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
		},"cond-4").start();
	}
	public static void otherThread(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				rLock.lock();
				System.out.println(" already run 0");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 0-1");
				rLock.unlock();
			}
		},"thread-0").start();
	}
	public static void otherThread1(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				rLock.lock();
				System.out.println(" already run 1");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 1-1");
				rLock.unlock();
			}
		},"thread-1").start();
	}
	public static void otherThread2(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				rLock.lock();
				System.out.println(" already run 2");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 1-2");
				rLock.unlock();
			}
		},"thread-2").start();
	}

	public static void otherThread3(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				rLock.lock();
				System.out.println(" already run 3");
				rLock.unlock();
				
				rLock.lock();
				System.out.println(" already run 1-3");
				rLock.unlock();
			}
		},"thread-2").start();
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
