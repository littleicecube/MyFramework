package com.palace.seeds.base.theByte.buddy;

import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteAdvice {
	
	/**
	 * 对静态方法执行jvm级别的切面操作
	 */
	@Test
	public void testStatic() {
		ByteBuddyAgent.install();
		new ByteBuddy().redefine(StaticMethod.class)
		.visit(Advice.to(StaticMethodAdvice.class).on(ElementMatchers.named("getVal")))
		.make().load(this.getClass().getClassLoader(),ClassReloadingStrategy.fromInstalledAgent());
		String na = new StaticMethod().getVal("nameval");
		System.out.println(na);
	}
	
	
	
	public static class StaticMethod{
		public static String getVal(String key) {
			return key+":curr";
		}
	}
	public static class StaticMethodAdvice{
		
		@Advice.OnMethodExit
		public static String getData(@Advice.Argument(0) String key) {
			System.out.println("##staticMethodAdvice##call");
			return key+":Advice";
		}
	}
	
	/**
	 *对已加载的方法进行切面处理
	 *首先获取当前jvm的instrument
	 *重新定义要进行切面的类-->指定进行切面操作时要调用的类-->将切面类应用到要进行切面处理的方法上
	 *
	 *这种切面处理方法是针对字节码级别的,当jvm启动后会加载类的字节码数据到进程中并且在相同的classloader下只有一份
	 *切面处理的时候获取classloader下的对应的类的字节码,然后在字节码上添加锚,完成后将修改后的字节码替换jvm中
	 *加载的class字节码,这种修改是针对classloader的,修改完成以后,所有其他的线程在根据被修改后的字节码创建实例的
	 *时候都会调用新的字节码
	 *
	 * Ad.class
	 */
	@Test
	public void testAdvice() {
		ByteBuddyAgent.install();
		new ByteBuddy()
		.redefine(Ad.class)
		.visit(Advice.to(InnerMultAdvice.class).on(ElementMatchers.named("mult")))
		.visit(Advice.to(InnerAddAdvice.class).on(ElementMatchers.named("add")))
		.make().load(this.getClass().getClassLoader(),ClassReloadingStrategy.fromInstalledAgent());
		Ad ad = new Ad();
		int r = ad.mult(2, 5);
		System.out.println("mult:"+r);
		r = ad.add(3, 7);
		System.out.println("add:"+r);
	}
	
	public static class  Ad{
		public int mult(int a,int b) {
			return a*b;
		}
		public int add(int a,int b) {
			if(true) {
				throw new ExitException("exception test");
			}
			return a+b;
		}
	}
	public static class InnerMultAdvice{
		@Advice.OnMethodEnter
        private static int enter(@Advice.Argument(0) int a1,@Advice.Argument(1) int a2) {
			System.out.println(" mult method Enter ");
            return a1+a2;
        }

		@Advice.OnMethodExit
		private static int  advice(@Advice.Argument(0) int a1,@Advice.Argument(1) int a2) {
			System.out.println(" mult method Exit ");
			return a1*a1+1;
		}
	}
	
	public static class InnerAddAdvice{
		@Advice.OnMethodEnter
        private static int enter(@Advice.Argument(0) int a1,@Advice.Argument(1) int a2) {
			System.out.println(" add method Enter ");
            return a1+a2;
        }

		@Advice.OnMethodExit(onThrowable = ExitException.class)
		private static int  advice(@Advice.Argument(0) int a1,@Advice.Argument(1) int a2) {
			System.out.println(" add method Exit ");
			return a1*a1+1;
		}
	}
	public static class ExitException extends RuntimeException{
		public ExitException(String msg) {
			super(msg);
		}
	}
	
}
