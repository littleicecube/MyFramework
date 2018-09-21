package com.palace.seeds.base.jvm.reentrantLock.readWriteLock;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MainReadWriteLock {

	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	public void lock1(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.readLock().lock();
				System.out.println("thread 1");
				/*for(int i=0;i<65534;i++){
					lock.readLock().lock();
				}*/
				lock.readLock().lock();
				lock.readLock().unlock();
				lock.readLock().unlock();
			}
		},"读线程1").start();
	}
	
	public void lock2(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.readLock().lock();
				System.out.println("thread 2");
				lock.readLock().unlock();
			}
		},"读线程2").start();
	}
	
	public void lock3(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.readLock().lock();
				System.out.println("thread 3");
				lock.readLock().unlock();
			}
		},"读线程3").start();
	}
	public void lock4(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.writeLock().lock();
				System.out.println("thread 4");
				lock.writeLock().unlock();
			}
		},"写线程1").start();
	}
	
	public void lock5(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				lock.writeLock().lock();
				System.out.println("thread 5");
				lock.writeLock().unlock();
			}
		},"写线程2").start();
	}
	
	public static void main(String[] args) {
		MainReadWriteLock m = new MainReadWriteLock();
		m.lock1();
		m.lock2();
		m.lock3();
		m.lock4();
		m.lock5();
	}
}
