package com.palace.seeds.dubbox.debug;

import org.junit.Test;

import com.alibaba.dubbo.common.extension.ExtensionFactory;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.rpc.Protocol;

public class ExtensionLoaderDebug {

	/**
	 * 
	一个接口可能有多种实现，程序在启动时需要把他们都加载到内存，根据运行时的信息决定使用指定的实现或者默认的实现，ExtensionLoader主要是作为某一种类型实现的集合,并对加载的类做
	一定的扩展操作，比如存在几种不同类型的序列化协议的实现如：DubboProtocol,ThriftProtocol
	那么程序在启动后就会创建一个实例来加载这两种配置.Protocol的全限定名(com.alibaba.dubbo.rpc.Protocol)
	代码就会从路径new File(dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol)加载配置文件中的信息到实例中
	ExtensionLoader protocolLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
	
	A)
	被加载的类可以直接通过其名称获取到其实例,比如要获取DubboProtocol类型的协议实现
	Protocol dubboImp = protocolLoader.getExtension("dubbo");
	B)
	被加载的类也可以通过一个适配器间接获取如
	ExtensionLoader<Protocol> loader = ExtensionLoader.getExtensionLoader(Protocol.class);
	创建一个适配器
	Protocol protocolAdaptive = loader.getAdaptiveExtension();
	通过给适配器传入参数来获取指定的协议的实例
	protocolAdaptive.export(invoker);
	public  Exporter export( Invoker arg0) throws  Invoker {
		com.alibaba.dubbo.common.URL url = arg0.getUrl();
		String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol() ;
		首先根据协议类型获取对应实现的集合,在根据名称获取最终的协议实现类
		Protocol extension = ( Protocol) ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(extName);
		return extension.export(arg0);
	}
	适配器可以是预留的比如ExtensionFactory类型的在其配置文件new File(dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.common.extension.ExtensionFactory)中就会指定其适配器的实现
	adaptive=com.alibaba.dubbo.common.extension.factory.AdaptiveExtensionFactory
	而大多数类型的适配器是通过字节码技术动态生成的,造成在调试的时候无法debug进去,如Protocol类型的适配器代码就是如下代码生成的
	private Class<?> createAdaptiveExtensionClass() {
		根据Protocol接口的定义创建其实现类的字节码信息
		String code = createAdaptiveExtensionClassCode();
		获取当前类加载器
		ClassLoader classLoader = findClassLoader();
		获取Compiler类型的配置信息,通过其适配器getAdaptiveExtension()配置获取其默认的实现
		com.alibaba.dubbo.common.compiler.Compiler compiler = ExtensionLoader.getExtensionLoader(com.alibaba.dubbo.common.compiler.Compiler.class).getAdaptiveExtension();
		获取到Compiler的实现类型后编译上一步创建的字节码生成Protocol类型的适配器Class信息
		return compiler.compile(code, classLoader);
	}
	Protocol对应的动态生成的适配器code代码如下:
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
	 
	C)
	 被加载的类实例化完成后还有可能做进一步操作
	 1)通过反射其中的方法,为实例设置一个信息
	 injectExtension(instance);
	 private T injectExtension(T instance) {
		try {
			if (objectFactory != null) {
				for (Method method : instance.getClass().getMethods()) {
					如果instance存在已'set'开头的方法,只有一个入参,且是public类型的方法则反射为其设置参数
					if (method.getName().startsWith("set")
							&& method.getParameterTypes().length == 1
							&& Modifier.isPublic(method.getModifiers())) {
						Class<?> pt = method.getParameterTypes()[0];
						try {
							从方法名中获取参数信息
							String property = method.getName().length() > 3 ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) : "";
							从工厂类中加载入参信息
							Object object = objectFactory.getExtension(pt, property);
							if (object != null) {
								method.invoke(instance, object);
							}
						} catch (Exception e) {
							logger.error("fail to inject via method " + method.getName()
									+ " of interface " + type.getName() + ": " + e.getMessage(), e);
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return instance;
	}
	 
	 
	 2)对实例进行进一步的包装
	injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
	private T injectExtension(T instance) {
		从File(dubbo-2.4.8/META-INF/dubbo/internal/*)中读取是包装类型的class信息
		Set<Class<?>> wrapperClasses = cachedWrapperClasses;
		if (wrapperClasses != null && wrapperClasses.size() > 0) {
			for (Class<?> wrapperClass : wrapperClasses) {
				根据wrapperClass实例化包装类并将传入的instance作为入参对其进行包装
				如果有多个包装类配置信息,则循环嵌套进行包装
				instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
			}
		}
	}
	 */
 
	@Test
	public void testExtensionLoader() {
		//ExtensionLoader.objectFactory是每个ExtensionLoader实例在实例化的时候需要为其赋值的引用
		//ExtensionFactory.class类型的配置信息被加载的时候也会创建一个loader,其loader.objectFactory是null
		//Protocol.class类型的配置信息被加载的时候也会创建一个loader,但是其loader.objectFactory的值是ExtensionFactory.class类型的loader
		//Compiler.class类型的配置信息被加载的时候也会创建一个loader,其loader.objectFactory的值也是ExtensionFactory.class类型的loader
		//也就是说ExtensionFactory.class类型的实例化loader是其他所有的类型实例的根
		ExtensionLoader<ExtensionFactory> loader = ExtensionLoader.getExtensionLoader(ExtensionFactory.class);
		//我们知道ExtensionFactory.class类型的配置信息中是存在一个适配器类型的配置信息,adaptive=com.alibaba.dubbo.common.compiler.support.AdaptiveCompiler
		//当ExtensionFactory.class的loader在执行loader.getAdaptiveExtension()时就会直接实例化配置信息com.alibaba.dubbo.common.compiler.support.AdaptiveCompiler
		//并对适配器的实例化进行反射操作为其注入一些配置新,ExtensionFactory.class类型中不存在包装类配置信息,故不会对默认实例进行包装
		loader.getAdaptiveExtension();
		
	}
	/**
	class ExtensionLoader{
		private final Class<?> type;
		private volatile Class<?> cachedAdaptiveClass = null;
		private final Holder<Object> cachedAdaptiveInstance = new Holder<Object>();
		private final ExtensionFactory objectFactory;
		private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<Class<?>, String>();
		private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<Map<String,Class<?>>>();
		private final Map<String, Activate> cachedActivates = new ConcurrentHashMap<String, Activate>();
		private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<String, Holder<Object>>();
		private String cachedDefaultName;
		private Set<Class<?>> cachedWrapperClasses;
		private final Map<String, Activate> cachedActivates = new ConcurrentHashMap<String, Activate>();
		private String cachedDefaultName;
	}
	**/
	@Test
	public void testExtensionLoaderProtocol() {
		//1)
		ExtensionLoader<Protocol> protocolLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
		//2)
		protocolLoader.getExtension("dubbo");
		protocolLoader.getAdaptiveExtension();
	}
	/**
	Protocol.class类型信息的加载过程
	1)
	创建Protocol.class的实例化信息 protocolLoader = new ExtensionLoader(Protocol.class)
	创建Protocol的根Factory信息,protocolLoader.objectFactory = ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension()
	2)
	获取协议protocolLoader中配置名称为dubbo的实例化信息
	public T getExtension(String name) {
		Object 	instance = createExtension(name);
		return (T) instance;
	}
	private T createExtension(String name) {
		//根据名称dubbo获取DubboProtocol并创建实例,如果是首次加载,则获取路径中配置的信息到当前实例中 File(dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol)
		Class<?> clazz = getExtensionClasses().get(name);
		T instance = clazz.newInstance();
		//先调用注入方法,通过instance中的set方法为其注入参数
		injectExtension(instance);
		//遍历所有的包装方法
		Set<Class<?>> wrapperClasses = cachedWrapperClasses;
		if (wrapperClasses != null && wrapperClasses.size() > 0) {
			for (Class<?> wrapperClass : wrapperClasses) {
				//wrapperClass.getConstructor(type).newInstance(instance)创建包装方法的实例信息,并将DubboProtocol的实例
				//由配置可知会先创建ProtocolListenerWrapper实例将instance作为构造参数传入,然后执行参数注入方法
				//在创建ProtocolFilterWrapper的实例,并将上一次的实例作为构造参数传入,然后执行参数注入方法
				instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
			}
		}
		return instance;
	}

	获取类型的扩展信息
	private Map<String, Class<?>> getExtensionClasses() {
		classes = loadExtensionClasses();
		return classes;
	}
	开始加载路径下的配置信息
	private Map<String, Class<?>> loadExtensionClasses() {
		Map<String, Class<?>> classes = loadExtensionClasses();
		Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
		    
    	//DUBBO_INTERNAL_DIRECTORY = DUBBO_DIRECTORY + "internal/";
        loadFile(extensionClasses, DUBBO_INTERNAL_DIRECTORY);
    	//DUBBO_DIRECTORY = "META-INF/dubbo/";
        loadFile(extensionClasses, DUBBO_DIRECTORY);
    	//SERVICES_DIRECTORY = "META-INF/services/";
        loadFile(extensionClasses, SERVICES_DIRECTORY);
        return extensionClasses;
		return classes;
	}
	
	路径File(dubbo-2.4.8/META-INF/dubbo/internal/com.alibaba.dubbo.rpc.Protocol)中的配置信息
	路径中的配置信息摘抄如下:
	注册配置,主要作用是把server端要对外提供的方法信息注册到第三方服务中供client获取
	registry=com.alibaba.dubbo.registry.integration.RegistryProtocol
	对实例进行包装,比如对DubboProtocol的实例信息包装
	filter=com.alibaba.dubbo.rpc.protocol.ProtocolFilterWrapper
	对实例包装或嵌套包装其他实例信息
	listener=com.alibaba.dubbo.rpc.protocol.ProtocolListenerWrapper
	dubbo协议的实现
	dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
	不存在别名
	com.alibaba.dubbo.rpc.protocol.http.HttpProtocol
	thrift协议的实现
	thrift=com.alibaba.dubbo.rpc.protocol.thrift.ThriftProtocol
	
	private void loadFile(Map<String, Class<?>> extensionClasses, String dir) {
	  //读取路径配置中的每一行内容如:dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol
	  while ((line = reader.readLine()) != null) {
		//将com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol加载到内存
		Class<?> clazz = Class.forName(line, true, classLoader);
		//如果类上配置有Adaptive注解则存放到适配器的引用cachedAdaptiveClass中
		if (clazz.isAnnotationPresent(Adaptive.class)) {
			cachedAdaptiveClass = clazz;
		}else{
			try{
				//如果被解析的类中不存在type为参数的构造方法,说明这不是个包装类会抛异常,然后在异常中处理
				//如果存在则添加到包装类集合中,到最后被实例化时,多个包装类嵌套包装形成调用链
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
				//缓存配置的其他类信息
				extensionClasses.put(n, clazz);
			}
		}
	  }
	}	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	**/
	
}
