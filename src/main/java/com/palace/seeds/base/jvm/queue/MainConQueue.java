package com.palace.seeds.base.jvm.queue;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.junit.Test;

import com.palace.seeds.model.User;
import com.palace.seeds.utils.WRunnable;
import com.palace.seeds.utils.WThread;

public class MainConQueue {
	
	public static ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();
	
	
	@Test
	public void testMsg(){
		User u1=new User().setName("xiaoming");
		User u2=u1;
		u1=new User().setName("xiaozhang");
		System.out.println("");
	}
	public static void main(String[] args) {
		WThread.run("offer1",new WRunnable() {
			@Override
			public void call() throws Exception {
				while(true){
					queue.offer("name"+System.currentTimeMillis());
					System.out.println("nn");
				}
				
			}
		});
		
		WThread.run("offer2",new WRunnable() {
			@Override
			public void call() throws Exception {
				while(true){
					queue.offer("name"+System.currentTimeMillis());
					System.out.println("nn");
				}
				
			}
		});
		
		WThread.run("offer3",new WRunnable() {
			@Override
			public void call() throws Exception {
				while(true){
					queue.offer("name"+System.currentTimeMillis());
					System.out.println("nn");
				}
				
			}
		});
		
		WThread.sleep(10*60000);
	}
	
	
}
