package com.palace.seeds.base.jvm.queue;

import java.util.concurrent.ConcurrentLinkedDeque;

import com.palace.seeds.utils.WRunnable;
import com.palace.seeds.utils.WThread;

public class MainConDeqe {
	
	public static ConcurrentLinkedDeque<String> queue = new ConcurrentLinkedDeque<String>();
	
	
	
	public static void main(String[] args) {
		WThread.run("add",new WRunnable() {
			@Override
			public void call() {
				while(true){
					queue.add(new String(System.currentTimeMillis()+""));
					System.out.println("add");
				}
				
			}
		});
		
		WThread.run("add2",new WRunnable() {
			@Override
			public void call() {
				while(true){
					queue.add(new String(System.currentTimeMillis()+""));
					System.out.println("add2");
				}
				
			}
		});
		
		WThread.run("peek",new WRunnable() {
			@Override
			public void call() {
				while(true){
					String str = queue.peek();
					System.out.println(str);
				}
				
			}
		});
		
	}
	
	public static void add(){
		 
		while(true){
			queue.add(new String(System.currentTimeMillis()+""));
			System.out.println("add");
		}
	}
	
	
	public static void offer(){
		while(true){
			queue.offer(new String(System.currentTimeMillis()+""));
			System.out.println("offer");
		}
	}
	
	
	public static void peek(){
		while(true){
			String str = queue.peek();
			System.out.println(str);
		}
	}
	
}
