package com.palace.seeds.base.theByte.buddy;

import org.junit.Before;
import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class SimpleBuddy {
	
	@Before
	public void bef() {
	}

	/**
	 * 拦截方法:首先指定被拦截的类(User.class)-->被拦截的方法(mult)-->指定拦截的类(UserDela.class)-->生成新的类
	 * 生成新的类相当于拷贝了一份原有的类,然后在拷贝的类上做修改,生成一个新的类,把要添加的字节码信息添加到新拷贝的
	 * 类上去,对原有的类没有影响,调用时需要根据新的类创建实例,然后执行被拦截的方法,那么拦截方法就会执行
	 * @throws Exception
	 */
	@Test
	public void testMult() throws Exception {
		Class clazz = new ByteBuddy()
       .subclass(User.class)
       .method(ElementMatchers.named("mult"))
       .intercept(MethodDelegation.to(new UserDela()))
       .make()
       .load(this.getClass().getClassLoader(),ClassLoadingStrategy.Default.WRAPPER)
       .getLoaded();
		int r = ((User)clazz.newInstance()).mult(12, 2);
		r = new User().mult(10, 5);
		System.out.println(r);
	}
	
	public static class User{
		public int mult(int a,int b) {
			return a*b+11;
		}
	}
	
	public static class UserDela{
		public int mult(int a,int b) {
			System.out.println("###UserDela intercept");
			return a * b + 1;
		}
	}
}
