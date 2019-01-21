package com.palace.seeds.spring.transaction.aspectj;

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement(mode = AdviceMode.ASPECTJ)
@ComponentScan("com.palace.seeds.spring.transaction.aspectj")
public class TrasactionAspectjTest {
	
	public static void main(String[] args) throws Exception {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TrasactionAspectjTest.class);
		UserService user = (UserService) ctx.getBean(UserService.class);
		user.update();
		System.out.println("###end###");
 	}
}
