package com.palace.seeds.spring.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.palace.seeds.spring.core")
public class TrasactionTest {
	
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TrasactionTest.class);
		UserService user = (UserService) ctx.getBean(UserService.class);
		user.update();
 	}
}
