package com.palace.seeds.base.jvm.thread;

import org.junit.Test;

public class MainThread {
	
	@Test
	public void testSleep() {
		
		try {
			Thread.currentThread().sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * jvm执行引擎也是建立在操作系统的进程、线程上的，当操作系统内核调度线程执行时，调用线程中的执行代码，
	 * 假设执行引擎是c代码编写，c代码中获取一个变量，变量描述的是一个jvm字节码，
	 */
	
	@Test
	public void TestWait() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("begin");
				synchronized (Object.class) {
					Object.class.notify();
					try {
						Object.class.wait(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName()+":begin sync");
				synchronized (Object.class) {
					System.out.println(Thread.currentThread().getName()+":enter sync");
					System.out.println(Thread.currentThread().getName()+":before wait");
					try {
						Object.class.wait(10000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName()+":after wait");
				}
				System.out.println(Thread.currentThread().getName()+"：end sync");
			}
		}).start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName()+":begin sync");
				synchronized (Object.class) {
					System.out.println(Thread.currentThread().getName()+":enter sync");
					System.out.println(Thread.currentThread().getName()+":before wait");
					try {
						Object.class.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(Thread.currentThread().getName()+":after wait");
				}
				System.out.println(Thread.currentThread().getName()+"：end sync");
			}
		}).start();
		
		try {
			Thread.currentThread().sleep(5000*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
