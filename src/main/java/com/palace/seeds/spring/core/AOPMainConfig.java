package com.palace.seeds.spring.core;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.palace.seeds.utils.WThread;

@Configuration
@EnableTransactionManagement
@ComponentScan("com.palace.seeds.spring.core")
public class AOPMainConfig {
//	@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
	
	public static AnnotationConfigApplicationContext ctx = null;
	public static void main(String[] args) {
		ctx = new AnnotationConfigApplicationContext(AOPMainConfig.class);
		run(); 
		WThread.sleep(4000000);
 	}
	private static void run() {
		UserService user = (UserService) ctx.getBean(UserService.class);
		//user.query();
		user.update();
	}
}
