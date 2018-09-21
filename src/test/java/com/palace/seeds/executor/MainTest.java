package com.palace.seeds.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class MainTest {

	ExecutorService service=Executors.newFixedThreadPool(10);
	
	
	
	//测试队列填满后的拒绝操作
	@Test
	public void newExecutor(){
		
		ThreadPoolExecutor exe = new ThreadPoolExecutor(1, 1, 10, TimeUnit.SECONDS,new LinkedBlockingDeque(3),new RejectedExecutionHandler() {
			
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				System.out.println(r);
				
			}
		});
		for(int i=0;i<10;i++){
			if(false)
				if(i==3){
					try {
						Thread.currentThread().sleep(3*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			
			
			exe.submit(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.currentThread().sleep(10*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("exit");
				}
			});
			
			
		}
		
		try {
			Thread.currentThread().sleep(50*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void subTest(){
		//返回值为void
		service.execute(new Runnable() {
			@Override
			public void run() {
				
			}
		});
		
		Future future = service.submit(new Runnable() {
			@Override
			public void run() {
				
			}
		});
		
		
		
		
	}
	
}
