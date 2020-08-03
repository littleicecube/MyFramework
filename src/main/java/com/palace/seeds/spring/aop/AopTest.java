package com.palace.seeds.spring.aop;

import java.lang.reflect.Method;

import org.springframework.aop.Advisor;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.aspectj.AspectJAfterAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AbstractGenericPointcutAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
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
		us.query();
		System.out.println("########################");
		us.update();
		System.out.println("########################");
 	}

	private static Advisor[] getAdvisor() {
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedNames("query");
		DefaultPointcutAdvisor befAdvisor = new DefaultPointcutAdvisor();
		befAdvisor.setPointcut(pointcut);
		befAdvisor.setAdvice(new MethodBeforeAdvice() {
			@Override
			public void before(Method method, Object[] args, Object target) throws Throwable {
				System.out.println("###before advice ,methodName:"+method.getName());
			}
		});
		/**

		 */
//		mryt_spell_order tuew#8686
//		mryt_spell_order aspp_order
//		mryt_spell_tms	aspp_tms aspp_mall
		
		DefaultPointcutAdvisor afterAdvisor = new DefaultPointcutAdvisor();
		try {
			AspectJExpressionPointcut cut = new AspectJExpressionPointcut();
			cut.setExpression("execution(* UserServiceForAop(..))");
			cut.getMethodMatcher().matches(UserServiceForAop.class.getMethod("update"),UserServiceForAop.class);
			AspectJAfterAdvice afterAdvice = new AspectJAfterAdvice(UserServiceForAop.class.getMethod("update"),cut,null);
			afterAdvice.getAspectJAdviceMethod();
			afterAdvisor.setAdvice(afterAdvice);
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
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
	 * 
	 * 
	 * 
	 * 在那些被选择的方法上执行那些操作。
	 * 在pointcut选择的方法上执行 before,after,after-returning,around,after-throwing操作
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

		@Aspect
		public class SimInterceptor {
			@Pointcut("execution(* AopTest.method(..))")
			public void printMethod() {
			}
			@Before("printMethod()")
			public void printBeforeAdvice() {
				System.out.println("printBeforeAdvice()!");
			}
			@AfterReturning(pointcut = "printMethod()",returning = "flag")
			public void printAfterAdvice(String flag) {
				System.out.println("printAfterAdvice()!" + flag);
			}
			@After("printMethod()")
			public void finallyAdvice() {
				System.out.println("fianllyAdvice()!");
			}
			@Around("printMethod() && args(name)")
			public Object printAroundAdvice(ProceedingJoinPoint pjp,String name) throws Throwable {
				Object result = null;
				if (name.equals("whc")) {
					pjp.proceed();
				}else {
					System.out.println("printMsg 方法以及被拦截。。。");
				}
				return result;
			}
		}
*/
	
}


