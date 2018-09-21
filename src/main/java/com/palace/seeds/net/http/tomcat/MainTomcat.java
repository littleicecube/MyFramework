package com.palace.seeds.net.http.tomcat;

public class MainTomcat {

	
	/**
	 * 阻塞方式创建的请求：
	 * 1:监听socket的请求
	 * 2:将socket封装为一个SocketWrapper的实例，将配置的socket参数应用在当前的socket上
	 * 3:将封装的SocketWrapper实例，作为参数封装成一个待处理的SocketProcessor的实例放到线程池的队列中
	 * 4:线程池中的一个线程获取线程队列中的一个待处理processor作为handler.process(xxx)的参数处理
	 * 5:将socketProcessor作为参数封装成一个Http11Processor的实例，在Http11Processor的代码中处理http协议请求，
	 * 	由于http协议的解析是个重量级的操作，需要创建缓冲区，将socket中读取的数据解析封装成byte数据或字符串，并创建request和response
	 * 	用于javaee的实现，因此创建的Http11Processor的实例会被循环利用存放在队列中，首先从队列中取Http11Processor的实例，
	 * 		如果有空闲的则将当前socketProcessor作为参数传递进去
	 * 		如果没有则创建新的Http11Processor的实例，并存放到队列中
	 * 
	 * Nio方式创建的请求：
	 * 
	 */
	
	
	/**
	 * 1)每当一个连接到来时，都会创建一个socket用来代表这个连接
	 * 2)
	 * 	a.socket被传递到一个线程中，在线程中处理数据
	 * 	b.socket被保存在队列中，由线程池来处理socket
	 * 	c.创建一个selector，将socket挂载到selector上，并监听socket事件，一般情况下，socket被创建
	 * 		以后都会和一些数据有绑定关系，因此socket会被包装，包装中包含一个绑定数据，绑定的数据还可以被注册到
	 * 		selector上，用于后续的处理
	 * 
	 * 
	 * 
	 * 
	 */
	
	
	
/*	 if (rxBufSize != null)
         socket.setReceiveBufferSize(rxBufSize.intValue());
     if (txBufSize != null)
         socket.setSendBufferSize(txBufSize.intValue());
     if (ooBInline !=null)
         socket.setOOBInline(ooBInline.booleanValue());
     if (soKeepAlive != null)
         socket.setKeepAlive(soKeepAlive.booleanValue());
     if (performanceConnectionTime != null && performanceLatency != null &&
             performanceBandwidth != null)
         socket.setPerformancePreferences(
                 performanceConnectionTime.intValue(),
                 performanceLatency.intValue(),
                 performanceBandwidth.intValue());
     if (soReuseAddress != null)
         socket.setReuseAddress(soReuseAddress.booleanValue());
     if (soLingerOn != null && soLingerTime != null)
         socket.setSoLinger(soLingerOn.booleanValue(),
                 soLingerTime.intValue());
     if (soTimeout != null && soTimeout.intValue() >= 0)
         socket.setSoTimeout(soTimeout.intValue());
     if (tcpNoDelay != null)
         socket.setTcpNoDelay(tcpNoDelay.booleanValue());
         
 */
	
	
	
}

