package com.palace.seeds.base.theByte.buddy;

import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

public class Simple {
	

	/**
	 * 
	 * java中提供方法可以在jvm运行的过程中对在运行的代码进行修改并重新加载的功能,相当于实现了jvm级别的aop,拦截注入等功能
	 * 1)线上在排查问题时,排查到某个方法而这个方法中没有日志打印此时可以动态修改被调用方法的字节码打印出入参等信息方便排查
	 * 2)可以动态的修改类中的相关方法的字节码,实现方法的调用前,调用后等aop操作,此处的aop操作可以是jvm级别上的aop操作,在同源classesloader的情况下
	 * 	每当调用被进行切面操作的方法时都会引起定义aop方法的执行
	 * 3)可以动态修改线程执行过程中所有我们关心的方法,为其添加aop操作记录其调用的入参和返回结果,完成一个线程内的简单的调用链记录
	 * 4)动态的修改字节码在测试时可以拦截某个方法的末端点,返回我们测试时希望返回的结果方便测试
	 * 
	 * 基础:
	 * 	java中的agent是什么
	 * 		从字面上理解是一个代理是jvm的代理,将jvm内部的某些功能暴露出来供我们使用,在此处我们主要讨论暴露出来的jvm层面上字节码的修改功能
	 * 	实现原理是什么
	 * 		jvm内部暴露的接口可以动态的修改已经被加载的字节码并且不需要重启jvm就可以生效
	 *  	java平台中java.lang.instrument.Instrumentation包下提供的方法可以实现对现有已经实现加载的类和方法进行替换,重新定义等操作主要方法有:
	 *  	java.lang.instrument.Instrumentation.retransformClasses
	 *  	java.lang.instrument.Instrumentation.redefineClasses
	 * 	怎么操作
	 * 		如果们要修改jvm加载的字节码,我们需要和jvm进行交互,获取jvm内部可以修改字节码的实例的引用即Instrumentation的引用
	 *  	a.获取jvm平台中Instrumentation实例的引用,
	 *  		a1)创建一个特定格式的代理jar包,自定义代理jar可以是premain(String args, Instrumentation inst).形式的也可以是agentmain(String args, Instrumentation inst)形式的本篇只介绍agentmain形式的.
	 *  			a11)创建jar如:myAgent.jar,jar中的包含一个方法,方法的签名为:agentmain(String args, Instrumentation inst).
	 *					如:
	 *					public static class MyAgent{
		 					private static Instrumentation inst ;
							public static void agentmain(String agentOps, Instrumentation inst) {
							       System.out.println("###开始执行agentmain");
							       //将Instrumentation保存到当前类中,以便后续重定义操作使用
							       MyAgent.inst = inst ;
						    }
							//供外部操作调用用来重定义class等
							public void redefineClass(Class newClassByteCode) throws UnmodifiableClassException {
								MyAgent.inst.retransformClasses(newClassByteCode);
							}
	 					}
	/**  			a12)jar中MANIFEST.MF配置定义
	 * 					在进行打包操作时设置MANIFEST.MF中的属性信息
	 * 					MANIFEST.MF
	 * 						|---Agent-Class:xxx.xxx.MyAgent
	 * 						|---Can-Redefine-Classes:true
	 * 						|---Can-Retransform-Classes:true
	 * 						|---Can-Set-Native-Method-Prefix:true
	 * 						|---Manifest-Version:1.0
	 * 			a2)调用方法或服务将创建的myAgent.jar包应用到jvm中,加载myAgent.jar的方法有几种
	 *  			a21)在程序启动时通过指定的/xxx/xxx/myAgent.jar的路径
	 *  			a22)通过调用jdk目录../lib/tools.jar中提供的方法来加载myAgent.jar
	 *  			a23)通过jvm提供的attach命令来加载jattach PID load instrument   /xxx/xxx/myAgent.jar
	 *  		a3)在a21)加载myAgent.jar过程中,jvm启动后做好准备工作
	 *  				如果是premain形式的代理,则会在调用程序的main方法前调用premain方法然后将Instrumentation引用传递到自定的代码中
	 *  		a4)在经过a1)->a2)-a3)步骤以后Instrumentation的引用就被传递到我们自定义的代码中,在获取这个引用后就可以修改指定类的字节码了并使之在当前
	 *  			jvm进程内生效
	 *  
	 *  利用ByteBuddy实现方法的aop和方法拦截操作:
	 *  	本文只是介绍ByteBuddy在jvm层面上动态的实现aop和方法拦截操作,ByteBuddy还有其他的一些功能.
	 *		ByteBuddy在jvm层面上的aop和拦截操作原理如上所述,ByteBuddy将类代码的修改->编译成字节码->应用到jvm中做了封装.
	 *		第一步:创建代理jar包.是通过代码自动生成一个代理jar包如:myAgent.jar
	 * 			精简代码:
			    public File resolve() throws IOException {
					//创建jar文件
			    	File agentJar;
			    	InputStream inputStream = Installer.class.getResourceAsStream('/' + Installer.class.getName().replace('.', '/') + "CLASS_FILE_EXTENSION");
			        agentJar = File.createTempFile("AGENT_FILE_NAME", "JAR_FILE_EXTENSION");
			        agentJar.deleteOnExit(); 
			        //设置jar问价的属性
			        Manifest manifest = new Manifest();
			        manifest.getMainAttributes().put("Manifest-Version", "MANIFEST_VERSION_VALUE");
			        manifest.getMainAttributes().put("Agent-Class", Installer.class.getName());
			        manifest.getMainAttributes().put("Can-Redefine-Classes", Boolean.TRUE.toString());
			        manifest.getMainAttributes().put("Can-Retransform-Classes", Boolean.TRUE.toString());
			        manifest.getMainAttributes().put("Can-Set-Native-Method-Prefix", Boolean.TRUE.toString());
			        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(agentJar), manifest);
			        //将内存中的jar信息写入本地
			        jarOutputStream.write("buffer".getBytes(), 0, 2048);
			        //返回jar
			        return agentJar;
			    }
			第二步:获取当前jvm进程id
				精简代码:
		  		String runtimeName = ManagementFactory.getRuntimeMXBean().getName();
		  		//进程id
	            int processIdIndex = runtimeName.indexOf('@');
			第三步:将代理jar包应用到jvm平台内
				根据环境信息决定利用../lib/tools.jar中提供的方法来加载myAgent.jar还是通过进程间通信jattach PID将代理myAgent.jar应用到jvm平台
				然后myAgent.jar中的agentmain方法会被执行将Instrumentation的引用保存到一个静态变量中
			第四步:修改指定的类代码,并生成目标字节码,将新的字节码通过Instrumentation的引用应用大jvm中
	 */ 
	
	/**
	 * 动态的为jvm已经加载的class中的方法添加切面方法
	 */
	@Test
	public void testAdvice() {
		//创建代理jar,并应用到当前jvm中,保存Instrumentation应用到静态变量中
		ByteBuddyAgent.install();
		//重定义Ad.class类,修改其中方法mult,add为其添加切面方法
		new ByteBuddy()
		.redefine(Ad.class)
		.visit(Advice.to(InnerMultAdvice.class).on(ElementMatchers.named("mult")))
		.visit(Advice.to(InnerAddAdvice.class).on(ElementMatchers.named("add")))
		.make()
		//将修改后的字节码应用到jvm中
		.load(this.getClass().getClassLoader(),ClassReloadingStrategy.fromInstalledAgent());
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

	
	
	/**
	 * 动态的拦截jvm已经加载的class中的方法
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
 
}
