package com.jdk.newFeature;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.Test;

import com.palace.seeds.model.User;


public class UtilMethod {

	@FunctionalInterface
	public static interface NewFunc{
		public Function newFunc(Function f);
	}
	public void nestCall(Function func) {
		System.out.println("callee:nextCall:start");
		Function<String,String> ff = func;
		System.out.println("callee:nextCall:end");
	}
	
	public void nestSupplier(Supplier<Function<String,String>> supp) {
		System.out.println("nestSupplier:start");
		nestCall(supp.get());
		System.out.println("nestSupplier:end");
	}
	
	public Function<String,String> getFunc(Function<String,String> sr){
		System.out.println("getFunc:start");
		return sr;
	}
	
	@Test
	public void testNestFunc() {
		//funcCode嵌套funcCode,灾难
	}

	@Test
	public void testNest() {
		//funcCode嵌套funcCode,灾难
		nestSupplier(()->getFunc((name)->{System.out.println("funcCode"); return "funcCode:return";}));
	}

	public void passData(Function<String,String> func) {
		System.out.println("callee:passData:start");
		Function<String,String> ff = func;
		System.out.println("callee:passData:end");
	}
	
	@Test
	public void testPassData() {
		System.out.println("callerStart");
		passData((name)->{System.out.println(name);System.out.println("params code");return "paramsCodeReturn:xiaoming";});
		System.out.println("callerEnd");
	}
	
	public void jastDoFunc(Function<String,String> func) {
		System.out.println("callee:start");
		func.apply("callee:params:name");
		System.out.println("callee:end");
	}
	
	@Test
	public void testJustDoFunct() {
		System.out.println("callerStart");
		jastDoFunc((name)->{System.out.println(name);System.out.println("params code");return "paramsCodeReturn:xiaoming";});
		System.out.println("callerEnd");
		
		/*结果:
			callerStart
			callee:start
			callee:params:name
			params code
			callee:end
			callerEnd
			表明代码参数首先被传递到被调用方法中,然后代码参数开始执行
			Function中的函数方法有一个入参,且只有一个入参,因此在编写代码参数时也需要有个入参,且类型应和泛型中一直
			(name)->{System.out.println(name);System.out.println("params code");return "paramsCodeReturn:xiaoming";}
			
		*/
	}
	
	
	public void SimSupplier(Supplier<User> supp) {
		System.out.println("supplierFianValSart");
		String finalVal = supp.get().getName();
		System.out.println("supplierFinalEndVal:"+finalVal);
	}
	
	@Test
	public void testSupplier() {
		//SimSupplier(()->new User());
		SimSupplier(()->{int a =12;System.out.println("lambda");return new User();});
	}
	
	@Test
	public void testListForeach() {
		Arrays.asList("xiaoming","xiaohong","xiaozhang").forEach((String key)->System.out.println(key));
		Arrays.asList("xiaoming","xiaohong","xiaozhang").forEach((String key)->{int a=12;System.out.println(key);});
		Arrays.asList("xiaoming","xiaohong","xiaozhang").sort((s,n)->{return s.compareTo(n);});

	}
}
