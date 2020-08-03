package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyReentrantLock {

	static final ReentrantLock rLock = new ReentrantLock();
	static Condition cond;
	public static void mainRun(){
		t0();
		t1();
		t2();
		t3();
		//t4();
		//t5();
		//inter();
	}
	
	static Thread t0;
	public static void t0(){
		t0 = new Thread(()->{
			rLock.lock();
			System.out.println(" already run 0");
			rLock.unlock();
		},"t0");
		t0.start();
		
	}

	static Thread t1;
	public static void t1(){
		t1 = new Thread(()->{
			rLock.lock();
			System.out.println(" already run 1");
			rLock.unlock();
		},"t1");
		t1.start();
	}
		 
	 
	static Thread t2;
	public static void t2(){
		t2 = new Thread(()->{
			rLock.lock();
			System.out.println(" already run 2");
			rLock.unlock();
		},"t2");
		t2.start();
	}

	static Thread t3;
	public static void t3(){
		t3 = new Thread(()->{
			rLock.lock();
			System.out.println(" already run 3");
			rLock.unlock();
		},"t3");
		t3.start();
	}
	static Thread t4;
	public static void t4(){
		t4 = new Thread(()->{
			rLock.lock();
			System.out.println(" already run 4");
			rLock.unlock();
		},"t4");
		t4.start();
	}
	static Thread t5;
	public static void t5(){
		t5 = new Thread(()->{
			rLock.lock();
			System.out.println(" already run 5");
			rLock.unlock();
		},"t5");
		t5.start();
	}
	public static void inter() {
		System.err.println("inter");
		t5.interrupt();
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
