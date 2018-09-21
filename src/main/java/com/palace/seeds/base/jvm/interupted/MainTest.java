package com.palace.seeds.base.jvm.interupted;

public class MainTest {

	
	public static void main(String[] args) throws Exception {
		Thread t1= new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("running");
				try {
					System.out.println("sleep");
					Thread.currentThread().sleep(10*1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("t1 interupted");
			}
		});
		t1.start();
		
		Thread.currentThread().sleep(2000);
		if(t1.isAlive()){
			System.out.println("true");
		}
		try {
			Thread.currentThread().sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("begin tnterrupt");
		t1.interrupt();
		System.out.println("end tnterrupt");
	}
}
