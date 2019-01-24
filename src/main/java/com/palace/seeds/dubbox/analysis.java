package com.palace.seeds.dubbox;

import io.netty.buffer.PoolChunkListMetric;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.nio.NioTask;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.SocketUtils;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.ProxyFactory;




public class analysis {

	
 /**
  * 
  * 
  * 

class ExtensionLoader{
	//1)
	private final Class<?> type;
	//6)
	private volatile Class<?> cachedAdaptiveClass = null;
	//7)
	private final Holder<Object> cachedAdaptiveInstance = new Holder<Object>();
	private final ExtensionFactory objectFactory;
	//3)
	private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();
	//2)
	private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String,Class<?>>>();
	private final Map<String, Activate> cachedActivates = new ConcurrentHashMap<String, Activate>();
	//4)
	private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();
	private String cachedDefaultName;
	//5)
	private Set<Class<?>> cachedWrapperClasses;
	private final ExtensionFactory objectFactory;
	private final Map<String, Activate> cachedActivates = new ConcurrentHashMap<String, Activate>();
	private String cachedDefaultName;
}



要获取com.alibaba.dubbo.rpc.Protocol对应的描述,在Dubbo中用一个ExtensionLoader来描述

//1)表示当前ExtensionLoader实例描述的是com.alibaba.dubbo.rpc.Protocol信息
private final Class<?> type = com.alibaba.dubbo.rpc.Protocol;
//2)type类型可能有几种实现方式,程序启动后解析配置保存在当前实例中,根据名称获取对应的实现
private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String,Class<?>>>();
Holder:{
	"thrift":com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol,
	"dubbo":com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
}
//3)type类型可能有几种实现方式,程序启动后解析配置保存在当前实例中,根据实现获取对应的名称
private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();
cachedNames:{
	com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol:"thrift",
	com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol:"dubbo"
}
//4)type类型的实现方式实例化后存放的地方
private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();
cachedInstances:{
	"thrift":new com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol(),
	"dubbo":new com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol()
}
//5)type类型的实现可能需要被包装一下才能返回,cachedWrapperClasses中存放了包装的盒子
private Set<Class<?>> cachedWrapperClasses;
cachedWrapperClasses:[
	com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
	com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper
]
//6)如果配置文件中存在的class上配置有注解则保存在
private volatile Class<?> cachedAdaptiveClass = null;
//7)如果配置文件中存在的class上配置有注解实例化后则保存在
private final Holder<Object> cachedAdaptiveInstance = new Holder<Object>();



文件new File(dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol)中的内容摘录如下:
	filter=com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
	listener=com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper
	dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
	thrift=com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol
	registry=com.alibaba.dubbo.registry.integration.RegistryProtocol

其中dubbo和thrift是协议Protocol的两个实现,那么在调用的时候具体调用哪个实现呢?
ExtensionLoader中存在一个实例cachedAdaptiveInstance,通过传入参数调用其中的某个方法返回具体调用类型的哪个实现
cachedAdaptiveInstance的实现可以在new File(dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol)中配置
也可以通过字节码的方式动态生成一个Class类,然后加载到内存并创建对应的实例赋值给ExtensionLoader.cachedAdaptiveInstance

com.alibaba.dubbo.rpc.Protocol类型就是通过字节码技术动态创建一个类如下:
public class Protocol$Adpative implements  Protocol {
	public void destroy() {
		throw new UnsupportedOperationException("method public ");
	}
	public int getDefaultPort() {
		throw new UnsupportedOperationException("method public ");
	}
	public  Invoker refer( Class arg0, com.alibaba.dubbo.common.URL arg1) throws  Class {
		com.alibaba.dubbo.common.URL url = arg1;
		String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol();
		 Protocol extension = ( Protocol) ExtensionLoader.getExtensionLoader( Protocol.class).getExtension(extName);
		return extension.refer(arg0, arg1);
	}
	public  Exporter export( Invoker arg0) throws  Invoker {
		com.alibaba.dubbo.common.URL url = arg0.getUrl();
		String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol() ;
		Protocol extension = ( Protocol) ExtensionLoader.getExtensionLoader( Protocol.class).getExtension(extName);
		return extension.export(arg0);
	}
}

一次协议的调用:
Protocol.export(Invoker){
		//从Invoker中获取协议
		com.alibaba.dubbo.common.URL url = arg0.getUrl();
		//默认情况下是Dubbo类型的协议
		String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol() ;
		//1)调用ExtensionLoader.getExtensionLoader( Protocol.class)根据Protocol.class从配置文件中获取所有配置的实现类有dubbo,thrift两种类型
		//2)在调用getExtension(extName)根据extName从中选出一个实现,根据默认配置返回com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol创建的实例
		//3)Protocol.class中也配置了包装类ProtocolFilterWrapper和ProtocolListenerWrapper,在实例DubboProtocol创建完成后会被两个包装类进行包装
		Protocol extension = ( Protocol) ExtensionLoader.getExtensionLoader( Protocol.class).getExtension(extName);
		return extension.export(arg0);
}

关于filter和listener两配置的类型信息.在调用web服务时我们可以配置filter和listener,在调用dubbo类型的协议时也可以配置filter和listener
 
public T getExtension(String name) {
	Object 	instance = createExtension(name);
	return (T) instance;
}
private T createExtension(String name) {
	//根据名称获取DubboProtocol并创建实例
	Class<?> clazz = getExtensionClasses().get(name);
	T instance = clazz.newInstance();
	//先调用注入方法,同过instance中的set方法为其注入参数
	injectExtension(instance);
	//遍历所有的包装方法
	Set<Class<?>> wrapperClasses = cachedWrapperClasses;
	if (wrapperClasses != null && wrapperClasses.size() > 0) {
		for (Class<?> wrapperClass : wrapperClasses) {
			//1)wrapperClass.getConstructor(type).newInstance(instance)创建包装方法的实例信息,并将DubboProtocol的实例
			//作为构造参数传递到包装方法中
			//2) injectExtension(xxx)在为包装类实例通过其set方法注入参数
			//3)由配置可知会先创建ProtocolListenerWrapper实例将instance作为构造参数传入,然后执行参数注入方法
			//在创建ProtocolFilterWrapper的实例,并将上一次的实例作为构造参数传入,然后执行参数注入方法
			instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
		}
	}
	return instance;
}


利用ExtensionLoader加载com.alibaba.dubbo.rpc.Protocol的过程
A)ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getAdaptiveExtension()
获取com.alibaba.dubbo.rpc.Protocol的ExtensionLoader实例然后获取其配置的扩展信息
B)  
public T getAdaptiveExtension() {
	Object instance = cachedAdaptiveInstance.get();
	//调用C)获取配置信息
	instance = createAdaptiveExtension();
	return (T) instance;
}
C)
private T createAdaptiveExtension() {
	try {
		//先执行C1获取编译类型(com.alibaba.dubbo.common.compiler.Compiler.class)的ExtensionLoader配置信息,并创建实现的实例化信息
		//在调用D)为生成的实例通过set方法注入数据信息
		return injectExtension((T) getAdaptiveExtensionClass().newInstance());
	} catch (Exception e) {
		throw new IllegalStateException("Can not create adaptive extenstion " + type + ", cause: " + e.getMessage(), e);
	}
}
C1)
private Class<?> getAdaptiveExtensionClass() {
	//调用E)加载com.alibaba.dubbo.rpc.Protocol的扩展配置信息到当前实例中
	getExtensionClasses();
	//执行C2)创建com.alibaba.dubbo.common.compiler.Compiler.class类型配置的实例信息
	return cachedAdaptiveClass = createAdaptiveExtensionClass();
}
C2)
//以字节码的方式为要加载的类型创建一个实现类,用来获取指定配置文件中的某个实现参建link#Adaptive
private Class<?> createAdaptiveExtensionClass() {
	//为com.alibaba.dubbo.rpc.Protocol创建实现字节码(public class Protocol$Adpative implements  Protocol) 
	String code = createAdaptiveExtensionClassCode();
	ClassLoader classLoader = findClassLoader();
	//通过ExtensionLoader扩展机制加载com.alibaba.dubbo.common.compiler.Compiler.class,并获取对应的实例默认是javasist
	com.alibaba.dubbo.common.compiler.Compiler compiler = ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.common.compiler.Compiler.class).getAdaptiveExtension();
	//将Protocol$Adpative对应的字节码编译生成Class返回
	return compiler.compile(code, classLoader);
}
D)//遍历instance中以set开头的方法,通过反射调用为instance设置参数
private T injectExtension(T instance) {
	for (Method method : instance.getClass().getMethods()) {
		if (method.getName().startsWith("set")
				&& method.getParameterTypes().length == 1
				&& Modifier.isPublic(method.getModifiers())) {
				Class<?> pt = method.getParameterTypes()[0];
				String property = method.getName().length() > 3 ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) : "";
				Object object = objectFactory.getExtension(pt, property);
				if (object != null) {
					method.invoke(instance, object);
				}
		}
	}
	return instance;
}

E)
private Map<String, Class<?>> getExtensionClasses() {
	//执行D1)
	Map<String, Class<?>> classes = loadExtensionClasses();
	return classes;
}
E1)获取文件中内容new File(dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol);
文件中的内容摘录如下:
	registry=com.alibaba.dubbo.registry.integration.RegistryProtocol
	filter=com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
	listener=com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper
	dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
	//不存在别名
	com.alibaba.dubbo.rpc.protocol.http.HttpProtocol
	thrift=com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol

private void loadFile(Map<String, Class<?>> extensionClasses, String dir) {
  //读取每一行内容如:dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
  while ((line = reader.readLine()) != null) {
	//将com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol加载到内存
	Class<?> clazz = Class.forName(line, true, classLoader);
	//如果类上配置有Adaptive注解则存放到cachedAdaptiveClass
	if (clazz.isAnnotationPresent(Adaptive.class)) {
		cachedAdaptiveClass = clazz;
	}else{
		try{
			//如果被解析的类中不存在type参数的构造方法,说明这不是个包装类则在异常中处理
			//如果存在则添加到包装类集合中,到最后被实例化时,多个包装类会形成调用链
			clazz.getConstructor(type);
			Set<Class<?>> wrappers = cachedWrapperClasses;
			wrappers.add(clazz);
		} catch (NoSuchMethodException e) {
			clazz.getConstructor();
			//如果配置信息是默认配置,则从文件名称中解析name值
			if (name == null || name.length() == 0) {
				name = clazz.getSimpleName().substring(0, clazz.getSimpleName().length() - type.getSimpleName().length()).toLowerCase();
			}
			//如果类上配置有Adaptive注解则存放到cachedAdaptiveClass
			Activate activate = clazz.getAnnotation(Activate.class);
			if (activate != null) {
				cachedActivates.put(names[0], activate);
			}
			//缓存配置的类信息,值是类的别名
			cachedNames.put(clazz, n);
			//缓存配置的类信息
			extensionClasses.put(n, clazz);
		}
	}
  }
}


















<dubbo:service/>		服务配置		用于暴露一个服务，定义服务的元信息，一个服务可以用多个协议暴露，一个服务也可以注册到多个注册中心
<dubbo:reference/>		引用配置		用于创建一个远程服务代理，一个引用可以指向多个注册中心
<dubbo:protocol/>		协议配置		用于配置提供服务的协议信息，协议由提供方指定，消费方被动接受
<dubbo:application/>	应用配置		用于配置当前应用信息，不管该应用是提供者还是消费者
<dubbo:module/>			模块配置		用于配置当前模块信息，可选
<dubbo:registry/>		注册中心配置	用于配置连接注册中心相关信息
<dubbo:monitor/>		监控中心配置	用于配置连接监控中心相关信息，可选
<dubbo:provider/>		提供方配置		当 ProtocolConfig 和 ServiceConfig 某属性没有配置时，采用此缺省值，可选
<dubbo:consumer/>		消费方配置		当 ReferenceConfig 某属性没有配置时，采用此缺省值，可选
<dubbo:method/>			方法配置		用于 ServiceConfig 和 ReferenceConfig 指定方法级的配置信息
<dubbo:argument/>		参数配置		用于指定方法参数配置

package com.alibaba.dubbo.rpc;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
public class Protocol$Adpative implements  Protocol {
	public void destroy() {
		throw new UnsupportedOperationException("method public ");
	}
	public int getDefaultPort() {
		throw new UnsupportedOperationException("method public ");
	}
	public  Invoker refer( Class arg0, com.alibaba.dubbo.common.URL arg1) throws  Class {
		if (arg1 == null) 
			throw new IllegalArgumentException("url == null");
		com.alibaba.dubbo.common.URL url = arg1;
		String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol();
		if(extName == null) 
			throw new IllegalStateException("Fail to get extension( Protocol) name from url(" + url.toString() + ") use keys([protocol])");
		 Protocol extension = ( Protocol) ExtensionLoader.getExtensionLoader( Protocol.class).getExtension(extName);
		return extension.refer(arg0, arg1);
	}
	public  Exporter export( Invoker arg0) throws  Invoker {
		if (arg0 == null) 
			throw new IllegalArgumentException(" Invoker argument == null");
		if (arg0.getUrl() == null) 
			throw new IllegalArgumentException(" Invoker argument getUrl() == null");
		com.alibaba.dubbo.common.URL url = arg0.getUrl();
		String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol() ;
		if(extName == null) 
			throw new IllegalStateException("Fail to get extension( Protocol) name from url(" + url.toString() + ") use keys([protocol])");
		 Protocol extension = ( Protocol) ExtensionLoader.getExtensionLoader( Protocol.class).getExtension(extName);
		return extension.export(arg0);
	}
}

package com.alibaba.dubbo.rpc;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
public class ProxyFactory$Adpative implements com.alibaba.dubbo.rpc.ProxyFactory {
	public Invoker getInvoker( Object arg0,  Class arg1, com.alibaba.dubbo.common.URL arg2) throws  Object {
		if (arg2 == null) {
			throw new IllegalArgumentException("url == null");
		}
		com.alibaba.dubbo.common.URL url = arg2;
		String extName = url.getParameter("proxy", "javassist");
		if(extName == null){
			throw new IllegalStateException("Fail to get extension");
		}
		ProxyFactory extension = (ProxyFactory)ExtensionLoader.getExtensionLoader( ProxyFactory.class).getExtension(extName);
		return extension.getInvoker(arg0, arg1, arg2);
	}
	public  Object getProxy( Invoker arg0) throws  Invoker {
		if (arg0 == null) {
			throw new IllegalArgumentException(" Invoker argument == null");
		}
		if (arg0.getUrl() == null) {
			throw new IllegalArgumentException(" Invoker argument getUrl() == null");
		}
		com.alibaba.dubbo.common.URL url = arg0.getUrl();
		String extName = url.getParameter("proxy", "javassist");
		if(extName == null){
			throw new IllegalStateException("Fail to get extension");
		}
		 ProxyFactory extension = ( ProxyFactory)ExtensionLoader.getExtensionLoader( ProxyFactory.class).getExtension(extName);
		return extension.getProxy(arg0);
	}
}










com.alibaba.dubbo.config.RegistryConfig
com.alibaba.dubbo.config.ProtocolConfig
com.alibaba.dubbo.config.spring.ServiceBean
com.alibaba.dubbo.common.extension.ExtensionFactory



以类的全路径名称为文件名从路径下加载文件如加载class:com.alibaba.dubbo.rpc.Protocol
则加载路径dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol
文件中的内容:
registry=com.alibaba.dubbo.registry.integration.RegistryProtocol
filter=com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
listener=com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper
mock=com.alibaba.dubbo.rpc.support.MockProtocol
injvm=com.alibaba.dubbo.rpc.protocol.injvm.InjvmProtocol
dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
rmi=com.alibaba.dubbo.rpc.protocol.rmi.RmiProtocol
hessian=com.alibaba.dubbo.rpc.protocol.hessian.HessianProtocol
com.alibaba.dubbo.rpc.protocol.http.HttpProtocol
com.alibaba.dubbo.rpc.protocol.webservice.WebServiceProtocol
thrift=com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol
memcached=memcom.alibaba.dubbo.rpc.protocol.memcached.MemcachedProtocol
redis=com.alibaba.dubbo.rpc.protocol.redis.RedisProtocol



静态类型:
	private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<Class<?>, ExtensionLoader<?>>();
	private static final ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<Class<?>, Object>();

非静态类型:
	//要加载的类型,比如com.alibaba.dubbo.rpc.Protocol
	private final Class<?> type;
	
	//type类型是个接口类型的,需要实现类做支撑,实现类可以在type路径下的配置文件中配置,对应的class上有注解
	//当type路径下的配置文件信息被加载时检测到则存放到cachedAdaptiveClass
	//检测代码:
	//  if (clazz.isAnnotationPresent(Adaptive.class)) {
	//     if(cachedAdaptiveClass == null) {
	//			cachedAdaptiveClass = clazz;
	//		} else if (! cachedAdaptiveClass.equals(clazz)) {
	//			throw new IllegalStateException("More than 1 adaptive class found: "
	//					+ cachedAdaptiveClass.getClass().getName()
	//					+ ", " + clazz.getClass().getName());
	//		}
	//	}
	//当配置文件中不存在含有注解的类信息时,会通过字节码技术创建一个类加载到当前线程中
	//如com.alibaba.dubbo.rpc.Protocol类通过字节码创建的实现类
		package com.alibaba.dubbo.rpc;
		import com.alibaba.dubbo.common.extension.ExtensionLoader;
		public class Protocol$Adpative implements com.alibaba.dubbo.rpc.Protocol {
			public void destroy() {
				throw new UnsupportedOperationException("method public...");
			}
			public int getDefaultPort() {
				throw new UnsupportedOperationException("method public abstract..,");
			}
			public com.alibaba.dubbo.rpc.Invoker refer(java.lang.Class arg0, com.alibaba.dubbo.common.URL arg1) throws java.lang.Class {
				if (arg1 == null) 
					throw new IllegalArgumentException("url == null");
				com.alibaba.dubbo.common.URL url = arg1;
				String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol();
				if(extName == null) 
					throw new IllegalStateException("Fail to get extension(com.alibaba.dubbo.rpc.Protocol) name from url(" + url.toString() + ") use keys([protocol])");
				com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getExtension(extName);
				return extension.refer(arg0, arg1);
			}
			public com.alibaba.dubbo.rpc.Exporter export(com.alibaba.dubbo.rpc.Invoker arg0) throws com.alibaba.dubbo.rpc.Invoker {
				if (arg0 == null) 
					throw new IllegalArgumentException("com.alibaba.dubbo.rpc.Invoker argument == null");
				if (arg0.getUrl() == null) 
					throw new IllegalArgumentException("com.alibaba.dubbo.rpc.Invoker argument getUrl() == null");com.alibaba.dubbo.common.URL url = arg0.getUrl();
				String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol() ;
				if(extName == null) 
					throw new IllegalStateException("Fail to get extension(com.alibaba.dubbo.rpc.Protocol) name from url(" + url.toString() + ") use keys([protocol])");
				com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getExtension(extName);
				return extension.export(arg0);
			}
		}
	
	private volatile Class<?> cachedAdaptiveClass = null;

	private final Holder<Object> cachedAdaptiveInstance = new Holder<Object>();
	
	private final ExtensionFactory objectFactory;
	
	//从type代表的文件中解析出来配置信息,存放到cachedNames
	//如:[{com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol:"thrift"},{com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol:"dubbo"}]
	private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();
	
	//从type代表的文件中解析出来配置信息,存放到cachedClasses
	//如:[{"thrift":com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol},{"dubbo":com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol}]
	private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String,Class<?>>>();
	
	//从type代表的文件中解析出来配置信息,判断其中的类上是否含有注解Activate
	private final Map<String, Activate> cachedActivates = new ConcurrentHashMap<String, Activate>();
	
	//从type代表的文件中解析出来配置信息,实例化后存放到Holder中,在存放到map中
	private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();

	private String cachedDefaultName;

	private volatile Throwable createAdaptiveInstanceError;
	//clazz.getConstructor(type); 如果从文件解析出的class含有一个构造函数,函数的入参是type类型的,则存放到cachedWrapperClasses
	private Set<Class<?>> cachedWrapperClasses;

	private Map<String, IllegalStateException> exceptions = new ConcurrentHashMap<String, IllegalStateException>();








protocol有几种可能是dubbo可能是inJvm的，而这个protocol的外围可能被包围，被包装。不论protocol是什么
stubProxyFactoryWrapper是个包装类，是对JavassistProxyFactory的包装
===================================================================================================================================
几个基本点：
    正常情况下网络服务至少要两个线程，或者一个线程一个线程组，一个线程用来接收到来的请求，一个用来处理到来的请求。
    服务的提供第一点要初始化确定要提供什么服务，服务名称.方法名称。大量的服务名称.方法名构成一个集合需要被处理和管理
    因为是网络请求，一般情况下到来的数据需要经过一系列的filter然后到达最终的处理handler,在经过filter时也需要分发事件给listener做一些监视和统计处理
        还要注意的是，filter和listener的添加在服务名.方法名在被处理化的时候和他们继承在一起，这是filter和listener的入口点，当一个service.method被调用时
        他的handler不能立即被调用，需要先调用注册在service.method实例中的filter和listener
    最后是线程池的服务（线程池本身存在一个线程中）线程池共享service.method的集合
=====================================================================================================================================
如果传入的Type类型中的方法上有@Adaptive注解，但是在实现类中并未有指定实现，则动态创建
@SPI("javassist")
public interface ProxyFactory {
    @Adaptive({Constants.PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker) throws RpcException;
    @Adaptive({Constants.PROXY_KEY})
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;
}
动态创建的代码如下：
package com.alibaba.dubbo.rpc;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
public class ProxyFactory$Adpative implements com.alibaba.dubbo.rpc.ProxyFactory {
    public java.lang.Object getProxy(com.alibaba.dubbo.rpc.Invoker arg0) throws com.alibaba.dubbo.rpc.Invoker {
        com.alibaba.dubbo.common.URL url = arg0.getUrl();
        String extName = url.getParameter("proxy", "javassist");
        com.alibaba.dubbo.rpc.ProxyFactory extension = (com.alibaba.dubbo.rpc.ProxyFactory) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.ProxyFactory.class).getExtension(extName);
        return extension.getProxy(arg0);
    }
    public com.alibaba.dubbo.rpc.Invoker getInvoker(java.lang.Object arg0,java.lang.Class arg1, com.alibaba.dubbo.common.URL arg2)throws java.lang.Object {
        com.alibaba.dubbo.common.URL url = arg2;
        String extName = url.getParameter("proxy", "javassist");
        com.alibaba.dubbo.rpc.ProxyFactory extension = (com.alibaba.dubbo.rpc.ProxyFactory) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.ProxyFactory.class).getExtension(extName);
        return extension.getInvoker(arg0, arg1, arg2);
    }
}
有此代码，调用compire后生成实例
=========================================================================================================================================
ProxyFactory
得到ExtensionLoader的实例后调用其getAdaptiveExtension()方法获取到实例后执行，ExtendsionLoader中type类型代表的实例
很绕的一个东西，我们首先由一个Type类型，是一个接口，接口中有方法的描述，然后在创建一个ExtensionLoader实例，实例中有Type类型接口，有Type类型实现的实例，ExtendsLoader相当于
Type类型的代理，又是一个对Type和Type类型实现的装饰。
    ProxyFactory的实例的主要作用：proxyFactory有个动态代码创建的ProxyFactory$Adaptive的实例，用这个实例调用StubProxyFactoryWrapper的实例stubProxyFactoryWrapper，stubProxyFactoryWrapper实例作为JavassistProxyFactory实例javassistProxyFactory的包装
        当调用stubProxyFactoryWrapper时会调用javassistProxyFactory实例中的方法getInvoker将要代理的实例如com.palace.seeds.dubbox.provider.HelloImp封装成一个Invoker返回
        总的来说ProxyFactory就是讲要代理的实例封装成一个Invoker
    Protocol的主要作用是调用export,export的主要作用是将生成的invoker进行包装。获取com.alibaba.dubbo.rpc.Protocol类型的ExtensionLoader的实例。获取的时候会加载两个包装如下：
        class com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper, class com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper，将生com.alibaba.dubbo.rpc.Protocol对应的实现类进行包装，com.alibaba.dubbo.rpc.Protocol的
        实现类的实例作为com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper实例的参数，然后在作为com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper的参数，包装完成后返回作为getExtension的返回去处理ProxyFactory生成的invoker实例
        然后invoker作为参数调用ProtocolFilterWrapper中的方法，ProtocolFilterWrapper是一个Filter的类型，它的ExtensionLoader实例，会加载一系列的Fileter，这些Filter是配置文件中创建的或者是动态代码创建的，然后把这些filter应用在invoker上，
        在调用ProtocolListenerWrapper中的方法，和ProtocolFilterWrapper类似，这里是为invoker添加listener,ProtocolListenerWrapper作为一种类型会创建他的ExtensionLoader实例，然后从配置文件中加载Listener或者是动态代码创建，然后应用在invoker上
        然后生成一个ListenerExporterWrapper的实例返回代码如下：
        =================================================================================================================================
        return new ListenerExporterWrapper<T>(
                protocol.export(invoker), 获取
                Collections.unmodifiableList(
                        ExtensionLoader.getExtensionLoader(
                                                            ExporterListener.class 加载ExporterListener.class的实现，实现可能有多个
                                                        ).getActivateExtension(
                                                                                invoker.getUrl(),    调用跟当前协议有关的ExporterListener.class的实现
                                                                                Constants.EXPORTER_LISTENER_KEY
                                                                            )
                        )
                    );
        ===================================================================================================================================
    以上是Protocol.export的作用
    9）
    Exporter<?> exporter = protocol.export(
            1）
            proxyFactory.getInvoker(ref, (Class) interfaceClass, local)//返回一个invoker
       );
    2）
    ProxyFactory$Adaptive
    public com.alibaba.dubbo.rpc.Invoker getInvoker(java.lang.Object arg0,java.lang.Class arg1, com.alibaba.dubbo.common.URL arg2)throws java.lang.Object {
        com.alibaba.dubbo.common.URL url = arg2;
        String extName = url.getParameter("proxy", "javassist");
                                                                                            这个地方根据com.alibaba.dubbo.rpc.ProxyFactory.class加载其实现类，其实现类有
                                                                                                    stub=com.alibaba.dubbo.rpc.proxy.wrapper.StubProxyFactoryWrapper，jdk=com.alibaba.dubbo.rpc.proxy.jdk.JdkProxyFactory，javassist=com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory
                                                                                                    其中StubProxyFactoryWrapper是对JdkProxyFactory，和JavassistProxyFactory的包装，请看解析1，
                                                                                                    这里解释了为什么下马的步骤4）的实现类为StubProxyFactoryWrapper，而步骤6）中的实例为JavassistProxyFactory
        com.alibaba.dubbo.rpc.ProxyFactory extension = (com.alibaba.dubbo.rpc.ProxyFactory) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.ProxyFactory.class).getExtension(extName);
                3)
        return extension.getInvoker(arg0, arg1, arg2);
    }
    4)
    StubProxyFactoryWrapper是对JavassistProxyFactory的包装，下一步骤看到调用进入JavassistProxyFactory
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        5)
        return proxyFactory.getInvoker(proxy, type, url);
    }
    6)
    JavassistProxyFactory
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        // TODO Wrapper类不能正确处理带$的类名
        7)
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);
        8)
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }
[class com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper, class com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper]
    10）这个是com.alibaba.dubbo.rpc.Protocol的Adpative
    public java.lang.Object getProxy(com.alibaba.dubbo.rpc.Invoker arg0) throws com.alibaba.dubbo.rpc.Invoker {
        com.alibaba.dubbo.common.URL url = arg0.getUrl();
        String extName = url.getParameter("proxy", "javassist");
                                                                                                            和步骤2)中的代码一样，获取com.alibaba.dubbo.rpc.Proxy.class类型代表的ExtesionLoader，并且和步骤2中一样也有
                                                                                                            装饰类，com.alibaba.dubbo.rpc.Proxy.class的实现类的实例也被包装，而且是两个包装第一个包装类将com.alibaba.dubbo.rpc.Proxy.class的实例包装
                                                                                                            后生成一个实例，然后又创建了一个包装类，并将上一个包装类的实例作为参数传递调第二个包装类中，然后返回第二个包装类的实例
                                                                                                            两个包装类是： [class com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper, class com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper]
        com.alibaba.dubbo.rpc.Proxy extension = (com.alibaba.dubbo.rpc.Proxy) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Proxy.class).getExtension(extName);
        return extension.getProxy(arg0);
    }
    解析1
    ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.ProxyFactory.class).getExtension(extName);
    此name参数为javassist，从javassist=com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory配置得知其值为com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory
      @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Extension name == null");
        if ("true".equals(name)) {
            return getDefaultExtension();
        }
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            cachedInstances.putIfAbsent(name, new Holder<Object>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    name=javassist,这个参数代表的值被包装
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }
    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw findException(name);
        }
        try {
            根据name值javassist得到的class为com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory，然后实例化
            T instance = (T) EXTENSION_INSTANCES.get(clazz);
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, (T) clazz.newInstance());
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }
            injectExtension(instance);
            调试时可知道cachedWrapperClasses的值是com.alibaba.dubbo.rpc.proxy.wrapper.StubProxyFactoryWrapper，然后创建com.alibaba.dubbo.rpc.proxy.wrapper.StubProxyFactoryWrapper的实例
            创建完成后将上面com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory的实例作为参数传递到com.alibaba.dubbo.rpc.proxy.wrapper.StubProxyFactoryWrapper的实例中返回
            代码如下：
            Set<Class<?>> wrapperClasses = cachedWrapperClasses;
            if (wrapperClasses != null && wrapperClasses.size() > 0) {
                for (Class<?> wrapperClass : wrapperClasses) {
                    instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
                }
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance(name: " + name + ", class: " +
                    type + ")  could not be instantiated: " + t.getMessage(), t);
        }
    }
    com.alibaba.dubbo.rpc.Protocol类型代表的实例创建ExtensionLoader中实例中的动态代码
    package com.alibaba.dubbo.rpc;
    import com.alibaba.dubbo.common.extension.ExtensionLoader;
    public class Protocol$Adpative implements com.alibaba.dubbo.rpc.Protocol {
    public void destroy() {
        throw new UnsupportedOperationException("method public abstract void com.alibaba.dubbo.rpc.Protocol.destroy() of interface com.alibaba.dubbo.rpc.Protocol is not adaptive method!");
    }
    public int getDefaultPort() {
        throw new UnsupportedOperationException("method public abstract int com.alibaba.dubbo.rpc.Protocol.getDefaultPort() of interface com.alibaba.dubbo.rpc.Protocol is not adaptive method!");
    }
    public com.alibaba.dubbo.rpc.Exporter export(com.alibaba.dubbo.rpc.Invoker arg0)throws com.alibaba.dubbo.rpc.Invoker {
        com.alibaba.dubbo.common.URL url = arg0.getUrl();
        String extName = ((url.getProtocol() == null) ? "dubbo" : url.getProtocol());
        com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getExtension(extName);
        return extension.export(arg0);
    }
    public com.alibaba.dubbo.rpc.Invoker refer(java.lang.Class arg0,com.alibaba.dubbo.common.URL arg1) throws java.lang.Class {
        com.alibaba.dubbo.common.URL url = arg1;
        String extName = ((url.getProtocol() == null) ? "dubbo": url.getProtocol());
        com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol) ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.rpc.Protocol.class).getExtension(extName);
        return extension.refer(arg0, arg1);
    }
}
injvm://127.0.0.1/com.palace.seeds.dubbox.api.IHello?anyhost=true&application=demo-provider&dubbo=2.4.8&interface=com.palace.seeds.dubbox.api.IHello&methods=sayHello&organization=dubbox&owner=programmer&pid=5656&side=provider&timestamp=1482029781928
{
generic=class com.alibaba.dubbo.rpc.filter.GenericFilter,
deprecated=class com.alibaba.dubbo.rpc.filter.DeprecatedFilter,
monitor=class com.alibaba.dubbo.monitor.support.MonitorFilter,
cache=class com.alibaba.dubbo.cache.filter.CacheFilter,
validation=class com.alibaba.dubbo.validation.filter.ValidationFilter,
activelimit=class com.alibaba.dubbo.rpc.filter.ActiveLimitFilter,
trace=class com.alibaba.dubbo.rpc.protocol.dubbo.filter.TraceFilter,
exception=class com.alibaba.dubbo.rpc.filter.ExceptionFilter,
consumercontext=class com.alibaba.dubbo.rpc.filter.ConsumerContextFilter,
genericimpl=class com.alibaba.dubbo.rpc.filter.GenericImplFilter,
echo=class com.alibaba.dubbo.rpc.filter.EchoFilter,
token=class com.alibaba.dubbo.rpc.filter.TokenFilter,
future=class com.alibaba.dubbo.rpc.protocol.dubbo.filter.FutureFilter,
compatible=class com.alibaba.dubbo.rpc.filter.CompatibleFilter,
classloader=class com.alibaba.dubbo.rpc.filter.ClassLoaderFilter,
context=class com.alibaba.dubbo.rpc.filter.ContextFilter,
accesslog=class com.alibaba.dubbo.rpc.filter.AccessLogFilter,
executelimit=class com.alibaba.dubbo.rpc.filter.ExecuteLimitFilter,
timeout=class com.alibaba.dubbo.rpc.filter.TimeoutFilter
}
{
activelimit=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[actives], group=[consumer], order=0, before=[]),
classloader=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider], order=-30000, before=[]),
accesslog=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[accesslog], group=[provider], order=0, before=[]),
context=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider], order=-10000, before=[]),
token=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[token], group=[provider], order=0, before=[]),
exception=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider], order=0, before=[]),
validation=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[validation], group=[consumer, provider], order=10000, before=[]),
future=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[consumer], order=0, before=[]),
timeout=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider], order=0, before=[]),
executelimit=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[executes], group=[provider], order=0, before=[]),
consumercontext=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[consumer], order=-10000, before=[]),
deprecated=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[deprecated], group=[consumer], order=0, before=[]),
generic=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider], order=-20000, before=[]),
monitor=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider, consumer], order=0, before=[]),
trace=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider], order=0, before=[]),
cache=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[cache], group=[consumer, provider], order=0, before=[]),
echo=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[], group=[provider], order=-110000, before=[]),
genericimpl=@com.alibaba.dubbo.common.extension.Activate(after=[], value=[generic], group=[consumer], order=20000, before=[])
}
[
com.alibaba.dubbo.rpc.filter.EchoFilter@201548d3,
com.alibaba.dubbo.rpc.filter.ClassLoaderFilter@55602519,
com.alibaba.dubbo.rpc.filter.GenericFilter@1b23b819,
com.alibaba.dubbo.rpc.filter.ContextFilter@13038e01,
com.alibaba.dubbo.rpc.protocol.dubbo.filter.TraceFilter@460b7f3a,
com.alibaba.dubbo.monitor.support.MonitorFilter@43bf0e5d,
com.alibaba.dubbo.rpc.filter.TimeoutFilter@1ccddcc3,
com.alibaba.dubbo.rpc.filter.ExceptionFilter@f5894fb
]
com.alibaba.dubbo.rpc.protocol.injvm.InjvmProtocol@72bc71dd
ProxyFactory-->StubProxyFactoryWrapper-->JavassistProxyFactory
public class JavassistProxyFactory extends AbstractProxyFactory {
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.getProxy(interfaces).newInstance(new InvokerInvocationHandler(invoker));
    }
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) {
        // TODO Wrapper类不能正确处理带$的类名
        final Wrapper wrapper = Wrapper.getWrapper(proxy.getClass().getName().indexOf('$') < 0 ? proxy.getClass() : type);
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName,
                                      Class<?>[] parameterTypes,
                                      Object[] arguments) throws Throwable {
                return wrapper.invokeMethod(proxy, methodName, parameterTypes, arguments);
            }
        };
    }
}
{
spi=class com.alibaba.dubbo.common.extension.factory.SpiExtensionFactory,
spring=class com.alibaba.dubbo.config.spring.extension.SpringExtensionFactory
}
class com.alibaba.dubbo.common.extension.factory.AdaptiveExtensionFactory
class com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
class com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
jar:file:/C:/Users/wzj/.m2/repository/com/alibaba/dubbo/2.4.8/dubbo-2.4.8.jar!/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol
==============================================================================================================================
com.alibaba.dubbo.remoting.exchange.support.header.HeaderExchanger@11908def
{
grizzly=class com.alibaba.dubbo.remoting.transport.grizzly.GrizzlyTransporter,
netty=class com.alibaba.dubbo.remoting.transport.netty.NettyTransporter,
mina=class com.alibaba.dubbo.remoting.transport.mina.MinaTransporter
} 





  * 
  */
}
