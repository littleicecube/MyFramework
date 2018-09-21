package com.palace.seeds.base.executor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Test;

import com.palace.seeds.utils.WThread;

public class MainExecutor {
	public static AtomicLong count = new AtomicLong();
	
	
	
	
	
	@Test
	public void testExecutorThread(){
		final ThreadPoolExecutor service = new ThreadPoolExecutor(2,4, 100,TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(3),new RejectedExecutionHandler() {
			@Override
			public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
				System.out.println(" r is RejectHandler");
			}
		});
		
		for(int i=0;i<30;i++){
			if(i==4){
 				System.out.println(" thread is 5");
			}  
 			if(i==9){
				System.out.println("thread is 10 ");
			}
			if(service.getQueue().size()==20){
				System.out.println(" queue is 20 ");
			}
			if(i==30){
				System.out.println("i is 30");
			}
			System.out.println("i="+i);
			FutureTask<Object>  future = (FutureTask<Object>) service.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println("curr:"+count.incrementAndGet());
					long b = System.currentTimeMillis();
					WThread.sleep(1*20*1000); 
					System.out.println("time:"+(System.currentTimeMillis()-b)/1000);
				}
			},new Object());
			
			future.hashCode();
		}
		service.shutdown();
		service.shutdownNow();
		System.out.println("======================");
		WThread.sleep(60*1000*10);
	}
	
	
	
	/**
	 * 默认情况下，使用的是LinkedBlockingQueue阻塞队列，队列的大小是Integer.MAX_Val;
	 * 自定义最大和最小线程数，并自定义队列的长度，当前待处理的数据超过核心线程个数后，数据被提交到队列中，如果待处理的
	 * 数据超过自定义的队列的大小，且队列的大小没有超过最大线程数，则创建新的线程，如果操作最大线程数，
	 * 
	 * 就会调用默认的拒绝添加异常，线程池内部调用的是队列的off方法返回类型为boolean值，而不是put方法，
	 * 如果是put方法，超过队列的上线的话，线程会被阻塞。
	 * 
	 */
	@Test
	public void testExecutorSelf(){
		ExecutorService service = new ThreadPoolExecutor(10,10, 100,TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(100));
		for(int i=0;i<Long.MAX_VALUE;i++){
			System.out.println("i="+i);
			@SuppressWarnings("unchecked")
			FutureTask<Object>  future = (FutureTask<Object>) service.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println("curr:"+count.incrementAndGet());
					long b = System.currentTimeMillis();
					WThread.sleep(1*60*1000); 
					System.out.println("time:"+(System.currentTimeMillis()-b)/1000);
				}
			},new Object());
		}
		System.out.println("======================");
		WThread.sleep(60*1000*10);
	}
	
	@Test
	public void testExecutorBlock(){
		ExecutorService service = Executors.newFixedThreadPool(5);
	 
		for(int i=0;i<Long.MAX_VALUE;i++){
			System.out.println("i="+i);
			service.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println("curr:"+count.incrementAndGet());
					long b = System.currentTimeMillis();
					WThread.sleep(1*60*1000); 
					System.out.println("time:"+(System.currentTimeMillis()-b)/1000);
				}
			});
		}
		System.out.println("======================");
		WThread.sleep(60*1000*10);
	}
	
	
	@Test
	public void testExecutor(){
		ExecutorService service = Executors.newFixedThreadPool(5);
		for(int i=0;i<10;i++){
			service.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println("curr:"+count.incrementAndGet());
					long b = System.currentTimeMillis();
					WThread.sleep(1*60*1000); 
					System.out.println("time:"+(System.currentTimeMillis()-b)/1000);
				}
			});
		}
		WThread.sleep(60*1000*10);
	}
	
	/**
	 * 
	 */
	@Test
	public void testExecutorException(){
		ExecutorService service  = Executors.newFixedThreadPool(4);
		
		final AtomicInteger counter =new AtomicInteger();
		while(true){
			if(counter.get() ==0 ){
				//在进行计数的时候需要捕捉service.execute抛出的异常
				counter.getAndIncrement();
				try{
					service.execute(new Runnable() {
						@Override
						public void run() {
							try{
								//do something
								throw new RuntimeException("name");
							}finally{
								counter.decrementAndGet();
							}
							
						}
					});
				}catch(Exception e){
					counter.getAndDecrement();
					e.printStackTrace();
				}
			}
			
			try {
				Thread.currentThread().sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
		}
		
	}
	
	
	@Test
	public void testExe(){
		
		ExecutorService service = Executors.newFixedThreadPool(8);
		for(int i=0;i<100;i++){
			System.out.println("begin");
			service.submit(new Runnable() {
				@Override
				public void run() {
					System.out.println(System.currentTimeMillis());
					try {
						Thread.currentThread().sleep(1*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			System.out.println("i="+i);
		}
		
		
		try {
			Thread.currentThread().sleep(10*60*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
