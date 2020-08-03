package com.palace.seeds.base.jvm.reentrantLock;

import java.util.concurrent.CyclicBarrier;

import com.palace.seeds.utils.WRunnable;
import com.palace.seeds.utils.WThread;

public class MyCyclicBarrier {

	static CyclicBarrier barrier = new CyclicBarrier(2, new Runnable() {
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + " 完成最后任务");
		}
	});
	public static void mainRun() throws Exception {
		WThread.run(new WRunnable() {
			@Override
			public void call() throws Exception {
				barrier.await();
			}
		});
		WThread.run(new WRunnable() {
			@Override
			public void call() throws Exception {
				barrier.await();
			}
		});
		WThread.sleep(9000 * 1000);
	}

	public static void main(String[] args) {
		try {
			mainRun();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
