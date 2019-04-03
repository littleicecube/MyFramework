package com.palace.seeds.dubbox.debug;

import java.lang.reflect.Field;

import org.junit.Test;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.registry.Registry;
import com.alibaba.dubbo.registry.RegistryFactory;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.ProxyFactory;

public class ExportDebug {
	
	public static interface IAccountService{
		public boolean add(String key,int val);
	}
	public  static class AccountService implements IAccountService{
		@Override
		public boolean add(String key, int val) {
			// TODO Auto-generated method stub
			return false;
		}
	}
	
	@Test
	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	public void testExportProtocol() {
		//获取代理工厂类型的扩展信息,并生动态生成其适配器类
		ExtensionLoader  proxyLaoder = ExtensionLoader.getExtensionLoader(ProxyFactory.class);
		setAdaptive(proxyLaoder, ProxyFactory$Adpative.class);
		ProxyFactory proxyFactory = (ProxyFactory) proxyLaoder.getAdaptiveExtension();
		
		//创建一个注册协议
		URL registryURL = new URL("registry","127.0.0.1",2181,"com.alibaba.dubbo.registry.zookeeper.ZookeeperRegistry");
		registryURL = registryURL.addParameter("registry", "zookeeper");
		
		//创建一个URL用来描述一个需要对外提供服务的service和method
		URL url = new URL("dubbo","127.0.0.1",8899,"com.palace.seeds.dubbox.debug.ExportDebug.IAccountService");
		url.addParameterAndEncoded("methods", "add")
		.addParameter("interface", "com.palace.seeds.dubbox.debug.ExportDebug.IAccountService")
		.addParameter("registry", "zookeeper")
		.addParameter("timeout", 1000)
		.addParameter("scope", "remote")
		.addParameter("retries", 2);
		
		
	    //创建一个Protocol.class类型的ExtensionLoader
        ExtensionLoader loader = ExtensionLoader.getExtensionLoader(Protocol.class);
        //Protocol.class在其配置文件中并没有对应的额适配器类,Protocol.class的适配器类是通过字节码技术动态生成的类,经过Compiler.class生成对应的class类信息,然后反射创建实例加载到内存中
        //为方便调试,将其动态生成的适配器类通过整理后编写在Protocol$Adpative.class,在通过反射设置到Protocol.class对应的classLoader实例中
        setAdaptive(loader,Protocol$Adpative.class);
        
        //创建一个RegistryFactory.class类型的ExtensionLoader
        ExtensionLoader registryFactoryloader = ExtensionLoader.getExtensionLoader(RegistryFactory.class);
        setAdaptive(registryFactoryloader,RegistryFactory$Adpative.class);
        
		//获取要提供服务类的接口信息
		Class<IAccountService> interfaceClass = IAccountService.class;
		//创建接口实现的类的实例,用来提供真正的服务
		IAccountService ref = new AccountService();
		//把要提供服务的类的接口以及其实通过代理类进行封装包装成一个Invoker
        Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class) interfaceClass, registryURL.addParameterAndEncoded(Constants.EXPORT_KEY, url.toFullString()));
        //获取Protocol.class的适配器实例
        Protocol protocol = (Protocol) loader.getAdaptiveExtension();
        //invoker中包含的协议类型是Dubbo的,故此处代码调用DubboProtocol中的export实现
		Exporter<?> exporter = protocol.export(invoker);
	}
	
	
	public static void setAdaptive(ExtensionLoader loader,Class clazz) {
		try {
			Field field = ExtensionLoader.class.getDeclaredField("cachedAdaptiveClass");
			field.setAccessible(true);
			field.set(loader, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//Protocol.class类型动态生成的适配器代码
	public static class Protocol$Adpative implements  Protocol {
		public void destroy() {
			throw new UnsupportedOperationException("method public ");
		}
		public int getDefaultPort() {
			throw new UnsupportedOperationException("method public ");
		}
		public  Invoker refer( Class arg0, com.alibaba.dubbo.common.URL arg1) {
			if (arg1 == null) 
				throw new IllegalArgumentException("url == null");
			com.alibaba.dubbo.common.URL url = arg1;
			String extName = url.getProtocol() == null ? "dubbo" : url.getProtocol();
			if(extName == null) 
				throw new IllegalStateException("Fail to get extension( Protocol) name from url(" + url.toString() + ") use keys([protocol])");
			 Protocol extension = ( Protocol) ExtensionLoader.getExtensionLoader( Protocol.class).getExtension(extName);
			return extension.refer(arg0, arg1);
		}
		public  Exporter export( Invoker arg0) {
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

	
	//ProxyFactory.class动态生成的适配器代码
	public static class ProxyFactory$Adpative implements com.alibaba.dubbo.rpc.ProxyFactory {
		public Invoker getInvoker( Object arg0,  Class arg1, com.alibaba.dubbo.common.URL arg2) {
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
		public  Object getProxy( Invoker arg0) {
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
	
	//
	public static class RegistryFactory$Adpative implements com.alibaba.dubbo.registry.RegistryFactory{
		public Registry getRegistry(URL arg0) {
			if (arg0 == null) {
				throw new IllegalArgumentException("url == null");
			}
			URL url = arg0;
			String extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol());
			if (extName == null) {
				throw new IllegalStateException("Fail use keys([protocol])");
			}
			RegistryFactory extension = (RegistryFactory) ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(extName);
			return extension.getRegistry(arg0);
		}
	}
}
	
