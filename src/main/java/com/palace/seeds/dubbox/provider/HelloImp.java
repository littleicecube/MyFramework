package com.palace.seeds.dubbox.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.palace.seeds.dubbox.api.IHello;
public class HelloImp implements IHello{

	public String sayHello(String name){
		System.out.println(name+" say hello ");
		return name;
	}
	
	public static void main(String[] args) {
	
		new Thread(new Runnable() {
			@Override
			public void run() {
				ClassPathXmlApplicationContext cx=new ClassPathXmlApplicationContext("spring/dubbo-demo-provider.xml");
			}
		}).start();
		
		while(true){
			try {
				Thread.currentThread().sleep(40000);

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
