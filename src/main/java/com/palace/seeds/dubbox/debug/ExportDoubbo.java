package com.palace.seeds.dubbox.debug;

public class ExportDoubbo {
	
	public static interface IAccountService{
		public boolean add(String key,int val);
	}
	public  static class AccountService implements IAccountService{
		@Override
		public boolean add(String key, int val) {
			System.out.println("save key:"+key+",val:"+val+",succ");
			return true;
		}
	}
	
	/**
	A) 要让AccountService的add方法作为服务,则定义一个URL用来描述要导出服务,导出方法,导出协议以及超时重试等配置信息
 	URL exportServiceMethodUrl = new URL("dubbo","127.0.0.1",8899,"com.palace.seeds.dubbox.api.IHello");
	exportServiceMethodUrl.addParameter("methods", "add");
	exportServiceMethodUrl.addParameter("interface", "com.palace.seeds.dubbox.debug.ExportDebug.IAccountService");
	exportServiceMethodUrl.addParameter("timeout", 1000);
	exportServiceMethodUrl.addParameter("scope", "remote");
	exportServiceMethodUrl.addParameter("retries", 2);
	B)导出的服务需要放到第三方服务上,当客户端来调用导出的服务时,可以先去第三方服务器上查询导出信息,那么需要定义一个URL用来描述第三方服务器的信息
	URL registryURL = new URL("registry","127.0.0.1",2181,"com.alibaba.dubbo.registry.RegistryService");
	registryURL.addParameter("registry", "zookeeper");
	C)将导出的服务和第三方服务器进行融合,表明在A中描述的服务导出信息需要存储在当前指定的三方服务器上
	registryURL.addParameterAndEncoded("export",exportServiceMethodUrl);
	导出的过程:
	D)从Protocol.class的ExtensionLoader中根据注册协议registry配置配置获取对应的实现类registry=com.alibaba.dubbo.registry.integration.RegistryProtocol
	由之前可知Protocol.class类型的实现类都会经过配置中的ProtocolFilterWrapper(过滤),ProtocolListenerWrapper(监听)两个实现类包装
	那么registry对应的实现类包装后的结果为:new ProtocolListenerWrapper(new ProtocolFilterWrapper(new RegistryProtocol()));
	String extName ="registry";
	Export ProtocolListenerWrapper = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(extName);
	调用顺序为:
		ProtocolListenerWrapper.export()-->ProtocolFilterWrapper.export()-->RegistryProtocol.export();
		
	ProtocolListenerWrapper:: public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		此时协议类型为RegistryProtocol
		if (Constants.REGISTRY_PROTOCOL.equals(invoker.getUrl().getProtocol())) {
			故执行此处代码ProtocolFilterWrapper.export
			return protocol.export(invoker);
		}
		return new ListenerExporterWrapper<T>(protocol.export(invoker), 
				Collections.unmodifiableList(ExtensionLoader.getExtensionLoader(ExporterListener.class)
						.getActivateExtension(invoker.getUrl(), Constants.EXPORTER_LISTENER_KEY)));
	}
	ProtocolFilterWrapper:: public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		此时协议类型为RegistryProtocol
		if (Constants.REGISTRY_PROTOCOL.equals(invoker.getUrl().getProtocol())) {
			故执行此处代码RegistryProtocol.export
			return protocol.export(invoker);
		}
		return protocol.export(buildInvokerChain(invoker, Constants.SERVICE_FILTER_KEY, Constants.PROVIDER));
	}
	
    RegistryProtocol::public <T> Exporter<T> export(final Invoker<T> originInvoker) throws RpcException {
        //执行代码E)启用协议和网络服务
        final ExporterChangeableWrapper<T> exporter = doLocalExport(originInvoker);
        //根据url中的配置通过ExtensionLoader获取一个注册中心的实现,这里用的是zookeeper
        final Registry registry = getRegistry(originInvoker);
        final URL registedProviderUrl = getRegistedProviderUrl(originInvoker);
        //当当前要暴露的服务注册到注册中心F)
        registry.register(registedProviderUrl);
        // 订阅override数据
        // FIXME 提供者订阅时，会影响同一JVM即暴露服务，又引用同一服务的的场景，因为subscribed以服务名为缓存的key，导致订阅信息覆盖。
        final URL overrideSubscribeUrl = getSubscribedOverrideUrl(registedProviderUrl);
        final OverrideListener overrideSubscribeListener = new OverrideListener(overrideSubscribeUrl);
        overrideListeners.put(overrideSubscribeUrl, overrideSubscribeListener);
        registry.subscribe(overrideSubscribeUrl, overrideSubscribeListener);
        //保证每次export都返回一个新的exporter实例
        return new Exporter<T>() {
            public Invoker<T> getInvoker() {
                return exporter.getInvoker();
            }
            public void unexport() {
            	try {
            		exporter.unexport();
            	} catch (Throwable t) {
                	logger.warn(t.getMessage(), t);
                }
                try {
                	registry.unregister(registedProviderUrl);
                } catch (Throwable t) {
                	logger.warn(t.getMessage(), t);
                }
                try {
                	overrideListeners.remove(overrideSubscribeUrl);
                	registry.unsubscribe(overrideSubscribeUrl, overrideSubscribeListener);
                } catch (Throwable t) {
                	logger.warn(t.getMessage(), t);
                }
            }
        };
    }
	
	E)在RegistryProtocol.export()中就需要根导出据协议doubbo的配置dubbo=com.alibaba.dubbo.rpc.protocol.dubbo.DubboProtocol,从Protocol.class的ExtensionLoader中
	获取对应的实现,由之前可知Protocol.class类型的实现类都会经过配置中的ProtocolFilterWrapper(过滤),ProtocolListenerWrapper(监听)两个实现类包装
	那么doubbo对应的实现类包装后的结果为:new ProtocolListenerWrapper(new ProtocolFilterWrapper(new DubboProtocol()));
	String extName ="dubbo";
	Export ProtocolListenerWrapper = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(extName);
	调用顺序为:
		ProtocolListenerWrapper.export()-->ProtocolFilterWrapper.export()-->DubboProtocol.export();
	DubboProtocol.export()中启动了网络服务,DubboProtocol的默认网络通信框架是netty,那么就需要启动netty服务,启动netty服务后就作为一个静态全局的服务存在当前应用中,
	当有第二个dubbo类型的服务需要对外提供时就不需要启动多个netty服务了
	那么
	ProtocolListenerWrapper:: public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		if (Constants.REGISTRY_PROTOCOL.equals(invoker.getUrl().getProtocol())) {
			return protocol.export(invoker);
		}
		当前协议类型DubboProtocol,故执行此处代码
		return new ListenerExporterWrapper<T>(protocol.export(invoker), 
				Collections.unmodifiableList(ExtensionLoader.getExtensionLoader(ExporterListener.class)
						.getActivateExtension(invoker.getUrl(), Constants.EXPORTER_LISTENER_KEY)));
	}
	ProtocolFilterWrapper:: public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		if (Constants.REGISTRY_PROTOCOL.equals(invoker.getUrl().getProtocol())) {
			return protocol.export(invoker);
		}
		当前协议类型DubboProtocol,故执行此处代码
		return protocol.export(buildInvokerChain(invoker, Constants.SERVICE_FILTER_KEY, Constants.PROVIDER));
	}
	DubboProtocol::public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
		URL url = invoker.getUrl();
		获取当前要导出服务的唯一标识
		String key = serviceKey(url);
		DubboExporter<T> exporter = new DubboExporter<T>(invoker, key, exporterMap);
		将服务存放到DubboProtocol实例全局唯一的map中
		exporterMap.put(key, exporter);
		开启netty网络服务
		openServer(url);
		return exporter;
	}
	
	 */
	
	 
}
	
