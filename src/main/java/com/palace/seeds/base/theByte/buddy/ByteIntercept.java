package com.palace.seeds.base.theByte.buddy;

import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Super;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteIntercept {
	
	 
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testStaticIntercept() throws Exception {
		ByteBuddyAgent.install();
		new ByteBuddy()
		.redefine(StaticMethod.class)
       .method(ElementMatchers.named("getVal"))
       .intercept(MethodDelegation.to(StaticMethodSuperIntercept.class))
       .make()
       .load(this.getClass().getClassLoader(),ClassReloadingStrategy.fromInstalledAgent());
		StaticMethod sm = new StaticMethod();
		System.out.println(sm.getVal("resval"));
	}
	/**
	 * 重新定义jvm已经加载的class,修改class为其中添加intercept代码
	 */
	@Test
	public void testMult() throws Exception {
		ByteBuddyAgent.install();
		new ByteBuddy()
		.redefine(StaticMethod.class)
       .method(ElementMatchers.named("getVal"))
       .intercept(MethodDelegation.to(StaticMethodIntercept.class))
       .method(ElementMatchers.named("getName"))
       .intercept(MethodDelegation.to(StaticMethodIntercept.class))
       .make()
       .load(this.getClass().getClassLoader(),ClassReloadingStrategy.fromInstalledAgent());
		 
		StaticMethod sm = new StaticMethod();
		System.out.println(sm.getVal("val11"));
		System.out.println(sm.getName("xiaoming"));
	}
	
	
	public static class StaticMethod{
		public final String getVal(String key) {
			return key+":curr";
		}
		final public static String getName(String name) {
			return name;
		}
	}
	public static class StaticMethodIntercept{
		public static String getVal(String key) {
			return "getVal intercept:"+key;
		}
		public static String getName(String name) {
			return "getName intercept"+name;
		}
	}
	
	
	public static class StaticMethodSuperIntercept{
		public static String getVal(@Super StaticMethod sr,String key) {
			return key+":intercept"+sr;
		}
	}
	
	 
}
