package com.palace.seeds.dubbox.consumer;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.palace.seeds.dubbox.api.IHello;

public class HelloConsumer {

	public static void main(String[] args) {
		ClassPathXmlApplicationContext cx=new ClassPathXmlApplicationContext("spring/dubbo-demo-consumer.xml");
		IHello  helloService=(IHello) cx.getBean("helloService");
		helloService.sayHello("daXiaoMing");
	}
}
