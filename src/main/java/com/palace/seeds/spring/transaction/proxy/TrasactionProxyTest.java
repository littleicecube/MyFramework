package com.palace.seeds.spring.transaction.proxy;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement(mode = AdviceMode.PROXY)
@ComponentScan("com.palace.seeds.spring.transaction.proxy")
public class TrasactionProxyTest {
	
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TrasactionProxyTest.class);
		UserService user = (UserService) ctx.getBean(UserService.class);
		user.update();
		user.query();
		System.out.println("###end###");
 	}
}
