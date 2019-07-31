package com.palace.seeds.base.jvm.reentrantLock;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

public class ConditionLock {
	/**
	调用Condition.await()操作可以是当前线程挂起，Condition.signal()可以唤醒在Condition上等待的线程
	但是为什么在调用await()和signal()前需要先获取锁
	await()和signal()是用来进行线程通信的，通信会涉及的数据传递，但是传递的数据并不是通过condition来传递的，是通过额外的一个变量
	如queue，那么通信状态的传递和通信数据的传递就需要一致性保证
	
	
	ReentrantLock lock = new ReentrantLock();
	Condition condition = lock.newCondition();
	Queue<String> queue = new LinkedList<>();
	
	假设调用signal()和await()之前没有获取锁
	在T1时刻在生产线程和消费线程执行完成后，生产线程如果不在往队列里添加数据，那么前一笔数据
	就会被卡在队列中，永远不被消费
	public void add(String ss) {
		queue.add(ss);
		condition.signal();
		T1时刻生产线程执行到此处，数据被添加到队列，调用singal()通知消费线程成功
	}

	public String take() throws Exception {
		while(queue.isEmpty()) {
			T1时刻消费线程准备执行下面一行代码，将当前线程挂起
			condition.await();
		}
		return queue.remove();
	}
	

	
	 **/
	
	final ReentrantLock lock = new ReentrantLock();
	final Condition condition = lock.newCondition();
	final Queue<String> queue = new LinkedList<>();
	
	@Test
	public void TestPC() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					add(new Object().toString());
				}
			}
			public void add(String ss) {
				lock.lock();
				queue.add(ss);
				System.out.println("productor:"+ss);
				condition.signal();
			}
		},"thread-pro").start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					take();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			public String take() throws Exception {
				while(queue.isEmpty()) {
					condition.await();
				}
				String val = queue.remove();
				System.out.println("consumer:"+val);
				return val;
			}
		},"thread-consumer").start();
		
		
		try {
			Thread.currentThread().sleep(10*60*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 
	
}
