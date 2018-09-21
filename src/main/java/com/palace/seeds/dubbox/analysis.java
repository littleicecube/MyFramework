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
  * com.alibaba.dubbo.cache.CacheFactory
	com.alibaba.dubbo.common.compiler.Compiler
	com.alibaba.dubbo.common.extension.ExtensionFactory
	com.alibaba.dubbo.common.logger.LoggerAdapter
	在dubbo的jar中存在这样一个路径
	dubbo-2.4.8\META-INF\dubbo\internal
	在路径在存在这些文件：
	com.alibaba.dubbo.common.serialize.Serialization
	com.alibaba.dubbo.common.status.StatusChecker
	com.alibaba.dubbo.common.store.DataStore
	com.alibaba.dubbo.common.threadpool.ThreadPool
	com.alibaba.dubbo.container.Container
	com.alibaba.dubbo.container.page.PageHandler
	com.alibaba.dubbo.monitor.MonitorFactory
	com.alibaba.dubbo.registry.RegistryFactory
	com.alibaba.dubbo.remoting.Codec
	com.alibaba.dubbo.remoting.Dispather
	com.alibaba.dubbo.remoting.exchange.Exchanger
	com.alibaba.dubbo.remoting.http.HttpBinder
	com.alibaba.dubbo.remoting.p2p.Networker
	com.alibaba.dubbo.remoting.telnet.TelnetHandler
	com.alibaba.dubbo.remoting.Transporter
	com.alibaba.dubbo.remoting.zookeeper.ZookeeperTransporter
	com.alibaba.dubbo.rpc.cluster.Cluster
	com.alibaba.dubbo.rpc.cluster.ConfiguratorFactory
	com.alibaba.dubbo.rpc.cluster.LoadBalance
	com.alibaba.dubbo.rpc.cluster.Merger
	com.alibaba.dubbo.rpc.cluster.RouterFactory
	com.alibaba.dubbo.rpc.Filter
	com.alibaba.dubbo.rpc.InvokerListener
	com.alibaba.dubbo.rpc.Protocol
	com.alibaba.dubbo.rpc.protocol.thrift.ClassNameGenerator
	com.alibaba.dubbo.rpc.ProxyFactory
	com.alibaba.dubbo.validation.Validation
	以上的类都是doubble在初始化，运行过程中的要需要加载类的全限定名，这些全限定名作为一个文件名，当类要加载时，作为参数传入。
	在调用ExtensionLoader.loadFile(Map<String, Class<?>> extensionClasses, String dir)方法时加载这个文件，读取文件中的内容
	一般文件中都会存在一个adaptive，作为对应类的实现。
	例如：com.alibaba.dubbo.common.compiler.Compiler ，这是一个接口，接口中会存在实现，默认情况下时用一个adaptive作为实现，默认实现
	会包含一个或多个实现如：jdk=XXX,javassist=XXX,被还存在com.alibaba.dubbo.common.compiler.Compiler的ExtensionLoader中。
	adaptive=com.alibaba.dubbo.common.compiler.support.AdaptiveCompiler
	jdk=com.alibaba.dubbo.common.compiler.support.JdkCompiler
	javassist=com.alibaba.dubbo.common.compiler.support.JavassistCompiler
	
	
	public abstract class AbstractInterfaceConfig extends AbstractMethodConfig {
	    // 服务监控
	    protected MonitorConfig        monitor;
	    // 应用信息
	    protected ApplicationConfig    application;
	    // 注册中心
	    protected List<RegistryConfig> registries;
	    // 服务接口的本地实现类名
	    protected String               local;
	    // 服务接口的本地实现类名
	    protected String               stub;
	    // 代理类型
	    protected String               proxy;
	    // 集群方式
	    protected String               cluster;
	    // 过滤器
	    protected String               filter;
	    // 监听器
	    protected String               listener;
	    // 负责人
	    protected String               owner;
	    // 连接数限制,0表示共享连接，否则为该服务独享连接数
	    protected Integer              connections;
	    // 连接数限制
	    protected String               layer;
	    // 模块信息
	    protected ModuleConfig         module;
	    // callback实例个数限制
	    private Integer                callbacks;
	    // 连接事件
	    protected String              onconnect;
	    // 断开事件
	    protected String              ondisconnect;
	    // 服务暴露或引用的scope,如果为local，则表示只在当前JVM内查找.
	     private String scope;
	}
	
	
	每个被声明的<dubbo:service interface="com.palace.seeds.dubbox.api.IWorld" ref="worldService"  protocol="dubbo"/>的service都会被
	解析成一个ServiceBean，ServiceBean又继承自ServiceConfig,其基本结构如上：
	主要包含服务名称，是server端还是client端，本地代理一般service中都是interface，注册中心可以注册到多个中心去，监控配置等信息
	public class ServiceBean<T> extends ServiceConfig<T>
	
	
	
	
	

	
=========================两个重要的ExtensionLoader

private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();
用来加载相关的协议默认在运行时会动态会成一个Adpative代码如下：
package com.alibaba.dubbo.rpc;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
public class Protocol$Adpative implements com.alibaba.dubbo.rpc.Protocol {
	public void destroy(){
		throw new UnsupportedOperationException( "method public abstract void com.alibaba.dubbo.rpc.Protocol.destroy() of interface com.alibaba.dubbo.rpc.Protocol is not adaptive method!" );
	}

	public int getDefaultPort(){
		throw new UnsupportedOperationException( "method public abstract int com.alibaba.dubbo.rpc.Protocol.getDefaultPort() of interface com.alibaba.dubbo.rpc.Protocol is not adaptive method!" );
	}

	public com.alibaba.dubbo.rpc.Invoker refer( java.lang.Class arg0, com.alibaba.dubbo.common.URL arg1 ) throws java.lang.Class{
		com.alibaba.dubbo.common.URL	url	= arg1;
		String	extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol() );
		com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol)ExtensionLoader.getExtensionLoader( com.alibaba.dubbo.rpc.Protocol.class ).getExtension( extName );
		return(extension.refer( arg0, arg1 ) );
	}

	public com.alibaba.dubbo.rpc.Exporter export( com.alibaba.dubbo.rpc.Invoker arg0 ) throws com.alibaba.dubbo.rpc.Invoker{
		com.alibaba.dubbo.common.URL	url	= arg0.getUrl();
		String	extName = (url.getProtocol() == null ? "dubbo" : url.getProtocol() );
		com.alibaba.dubbo.rpc.Protocol extension = (com.alibaba.dubbo.rpc.Protocol)ExtensionLoader.getExtensionLoader( com.alibaba.dubbo.rpc.Protocol.class ).getExtension( extName );
		return(extension.export( arg0 ) );
	}
}

默认情况下Protocol.class的extensionLoader在创建的时候会从dubbo-2.4.8\META-INF\dubbo\internal\com.alibaba.dubbo.rpc.Protocol文件中加载
一些类的全限定名名称如：
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
当调用Protocol.class的ExtensionLoader时会根据传入的url中获取protocolType从而从上面的内容中获取要加载类的全限定名称，然后调用getExtension( extName )，根据协议的类型在
决定实例化那个协议，在protocol.class extensionLoader中含有cachedWrapperClasses类，每个实例化的协议都要被这两个类进行二次包装，把protocol对应的实例包装到ProtocolFilterWrapper
的实例中，再把这个实例 包装到ProtocolListenerWrapper实例中，这样包装完成后的调用关系就成了，protocol.exporter-->ProtocoloFilterWrapper.expoter--->ProtocolListener.expoter

正常情况下的调用
  Exporter<?> exporter = protocol.export(invoker);其中协议是Register类型的，根据上面的描述RegistryProtocol会被ProtocolFileterWrapper,ProtocolListener包装
 那么调用就变成protocol.exporter-->ProtocoloFilterWrapper.exporter--->ProtocolListener.exporter-->RegistryProtocol.exportor
 -->（因为dubbo协议也被ProtocolListenser,ProtocolFilter进行了包装）ProtocolFilter.exporter-->ProtocolListener.exporter-->dubbo.exporter



private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();
这个ExtensionLoader和Protocol.class的ExtensionLoader类似，在初始化运行过程中，因为dubbo-2.4.8\META-INF\dubbo\internal\没有对应的Adpative，所以会动态
编译生成一个Adpative，然后在根据传入的url中获取proxyType决定调用那个代理工具，这个和上面不同的是，本来存在两个代理类，jdk和javassist，但是其中还存在一个stub，这是个包装将jdk，javassist
再次进行了包装，作为一个工厂类的服务对外提供。
stub=com.alibaba.dubbo.rpc.proxy.wrapper.StubProxyFactoryWrapper
jdk=com.alibaba.dubbo.rpc.proxy.jdk.JdkProxyFactory
javassist=com.alibaba.dubbo.rpc.proxy.javassist.JavassistProxyFactory








  * 
  */
}
