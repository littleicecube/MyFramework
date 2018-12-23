package com.palace.seeds.base.jvm.thread;

import org.junit.Test;

public class MainWait {
	
	static Object monitor = new Object();	
	@Test
	public void testWait() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				synchronized (monitor) {
					System.out.println(name+" enter wait");
					try {
						monitor.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(name+" exit wait");
				}
			}
		},"first-Thread").start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				synchronized (monitor) {
					System.out.println(name+" enter wait");
					try {
						monitor.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println(name+" exit wait");
				}
			}
		},"second-Thread").start();
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<100;i++) {
					synchronized (monitor) {
						monitor.notify();
					}
				}
			}
		}).start();
		
		
		try {
			Thread.currentThread().sleep(30*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	 
	}

	
	@Test
	public void testSync() {
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				synchronized (monitor) {
					System.out.println(name+" enter wait");
					System.out.println(name);
					System.out.println(name+" exit wait");
				}
			}
		},"first-Thread").start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				String name = Thread.currentThread().getName();
				synchronized (monitor) {
					System.out.println(name+" enter wait");
					System.out.println(name);
					System.out.println(name+" exit wait");
				}
			}
		},"second-Thread").start();
		
		try {
			Thread.currentThread().sleep(30*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	 
	}
 
}
