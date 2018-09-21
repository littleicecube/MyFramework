package com.palace.seeds.base.jvm.queue;

import java.util.concurrent.LinkedBlockingQueue;

import com.palace.seeds.utils.WRunnable;
import com.palace.seeds.utils.WThread;

public class MainBlockingQueue {
	
	
	public static LinkedBlockingQueue<String> lQueue=new LinkedBlockingQueue<String>();
	
	public static void main(String[] args) {
		
		WThread.run("test",new WRunnable() {
			@Override
			public void call() {
				lQueue.remove("name");
				lQueue.poll();
				lQueue.peek();
				//lQueue.remove();
				try {
					lQueue.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	
		//offer
		WThread.run("offer",new WRunnable() {
			@Override
			public void call() {
				lQueue.offer("string");
				
			}
		});
		
		//add里面调用的是offer方法
		WThread.run("add",new WRunnable() {
			@Override
			public void call() {
				while(true){
					lQueue.add(new String("str"));
					System.out.println("add");
				}
				
			}
		});
		
		WThread.run("put",new WRunnable() {
			@Override
			public void call() {
				while(true){
					try {
						lQueue.put("str");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("add2");
				}
				
			}
		});
		
		WThread.run("peek",new WRunnable() {
			@Override
			public void call() {
				while(true){
					String str = lQueue.peek();
					System.out.println(str);
				}
				
			}
		});
	}
	
}
