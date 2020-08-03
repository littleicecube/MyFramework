package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.Semaphore;

import com.palace.seeds.utils.WThread;

public class MySemphere {

	static Semaphore sp = new Semaphore(1);
	
	public static void main(String[] args) throws Exception {
		WThread.run(()->{
			sp.acquire();
			System.err.println("do something0");
			sp.release();
		});
		WThread.run(()->{
			sp.acquire();
			System.err.println("do something1");
			sp.release();
		});
		WThread.run(()->{
			sp.acquire();
			System.err.println("do something2");
			sp.release();
		});
		WThread.run(()->{
			sp.release();
			System.err.println("do something3");
			sp.release();
		});
		Thread.currentThread().sleep(900000l);
	}
	
}
