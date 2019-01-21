package com.palace.seeds.spring.aop;

import java.lang.reflect.Method;

import org.springframework.aop.Advisor;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;
import org.springframework.aop.target.SingletonTargetSource;

public class AopTest {
	
	
	public static void main(String[] args) throws Exception {
		
		UserServiceForAop us = new UserServiceForAop();
		
		ProxyFactory proxyFactory = new ProxyFactory();
		//代理当前class
		proxyFactory.setProxyTargetClass(true);
		//设置Advisor
		proxyFactory.addAdvisors(getAdvisor());
		proxyFactory.setFrozen(false);
		proxyFactory.setTargetSource(new SingletonTargetSource(us));
		Object proxy = proxyFactory.getProxy(Thread.currentThread().getContextClassLoader());
		
		us = (UserServiceForAop)proxy;
		us.update();
		System.out.println("########################");
		us.query();
		System.out.println("########################");
 	}

	private static Advisor[] getAdvisor() {
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedNames("update","query");
		DefaultPointcutAdvisor befAdvisor = new DefaultPointcutAdvisor();
		befAdvisor.setAdvice(new MethodBeforeAdvice() {
			@Override
			public void before(Method method, Object[] args, Object target) throws Throwable {
				System.out.println("###before advice ,methodName:"+method.getName());
			}
		});
		return new AbstractGenericPointcutAdvisor[] {befAdvisor};
	}
	
	
	public static class UserServiceForAop {
		
		public String query() {
			System.out.println("####query exe");
			return "queryRet:xiaoming";
		}
		public void update() {
			System.out.println("###update exe");
		}

	}
	
	/**
		<aop:config>
		    <aop:aspect ref="userServiceProxy">
				标记出那些类的那些方法是需要执行代理方法
				<aop:pointcut id="pointcut" expression="execution(* com.seeds.service.UserService.add(..))" />
				
		        在执行pointcut中指定的userService的某个方法前先执行beforeExe
		        <aop:before method="beforeExe" pointcut-ref="pointcut" ></aop:before>
				
				在执行pointcut中指定的userService的某个方法前先执行afterExe
				<aop:after method="afterExe" pointcut-ref="pointcut"/>
				
				在执行pointcut中指定的userService的某些方法之后执行afterReturn
				<aop:after-returning method="afterReturn" pointcut-ref="pointcut"  returning="returnVal" />
				
				在执行pointcut中指定的userService的某些方法时执行aroundExe
				<aop:around method="aroundExe" pointcut-ref="pointcut"  />
		
				在执行pointcut中指定的userService的某些方法异常时时执行afterThrowExe
		        <aop:after-throwing method="afterThrowExe" pointcut-ref="pointcut" throwing="throwable"/>
		    </aop:aspect>
		</aop:config>

	 */
	
}


