package com.palace.seeds.base.jvm.reentrantLock;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PC {
	static int max;
	static volatile List<String> list = new LinkedList();
	static ReentrantLock lock = new ReentrantLock();
	static Condition condition = lock.newCondition();

	public static void producer() {
		lock.lock();
		try {
			//如果list非空,则阻塞线程
			if(!list.isEmpty()) {
				condition.await();
			}
			String val = "mils:"+System.currentTimeMillis();
			System.err.println("生产者添加数据:"+val);
			list.add(val);
			condition.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static void consumer() {
		lock.lock();
		try {
			//如果list为空,则让线程等待
			if(list.isEmpty()) {
				condition.await();
			}
			//从list中移除消费数据
			String val = list.remove(0);
			System.err.println("消费者消费数据:"+val);
			condition.signal();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) throws Exception {
		//创建两个消费者线程
		new Thread(()->{consumer();}).start();
		new Thread(()->{consumer();}).start();
		//延时确保两个消费者已经挂起等待被唤醒.
		Thread.currentThread().sleep(2*1000);
		//创建一个生产者线程
		new Thread(()->{producer();}).start();
		//阻塞当前线程
		synchronized(Object.class) {
			Object.class.wait();
		}
	}
}
