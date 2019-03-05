package com.palace.seeds.net.netty;

import org.junit.Test;

public class SimpleServerPipeline {
	
	//handler中编写要执行的处理逻辑
	interface SimpleChannelHandler{	}
	abstract class SimpleChannelInboundHandler implements SimpleChannelHandler{
		void channelRegistered(SimpleAbstractChannelHandlerContext ctx) {};
	}
	abstract class SimpleChannelOutboundHandler implements SimpleChannelHandler{
	    void deregister(SimpleAbstractChannelHandlerContext ctx) {};
	}
	
	abstract class SimpleAbstractChannelHandlerContext{
	    volatile SimpleAbstractChannelHandlerContext next;
	    volatile SimpleAbstractChannelHandlerContext prev;
	    
	    public SimpleAbstractChannelHandlerContext fireChannelRegistered() {
	        invokeChannelRegistered( );
	        return this;
	    }

	    void invokeChannelRegistered(final SimpleAbstractChannelHandlerContext next) {
            next.invokeChannelRegistered();
	    }

	    private void invokeChannelRegistered() {
	         ((SimpleChannelInboundHandler) handler()).channelRegistered(this);
	    }
	    private void invokeChannelDeregister() {
	         ((SimpleChannelOutboundHandler) handler()).deregister(this);
	    }
	    public SimpleChannelHandler handler() {return null;};
	}
	
	class SimpleDefaultChannelHandlerContext extends SimpleAbstractChannelHandlerContext {

	    private final SimpleChannelHandler handler;

	    SimpleDefaultChannelHandlerContext( SimpleChannelHandler handler){
	        this.handler = handler;
	    }

	    public SimpleChannelHandler handler() {
	        return handler;
	    }

	    private   boolean isInbound(SimpleChannelHandler handler) {
	        return handler instanceof SimpleChannelInboundHandler;
	    }

	    private  boolean isOutbound(SimpleChannelHandler handler) {
	        return handler instanceof SimpleChannelOutboundHandler;
	    }
	}
	
	class SimpleChannelPipeline{
	    final SimpleAbstractChannelHandlerContext head ;
	    final SimpleAbstractChannelHandlerContext tail;
	    
	    SimpleChannelPipeline(){
	    	this.head =  new SimpleDefaultChannelHandlerContext(
    			new SimpleChannelInboundHandler() {
    			}
	    	);
	    	this.tail =  new SimpleDefaultChannelHandlerContext(
    			new SimpleChannelInboundHandler() {
    			}
	    	);
	    	this.head.next = this.tail;
	    	this.tail.prev = this.head;
	    }
	    
	    public final SimpleChannelPipeline addBefore(SimpleChannelHandler handler) {
	    	SimpleDefaultChannelHandlerContext  handlerContext = new SimpleDefaultChannelHandlerContext(handler);
	    	head.next = handlerContext;
	    	handlerContext.prev = head;
	    	handlerContext.next = this.tail;
	    	this.tail.prev = handlerContext;
	        return this;
	    }
	}
	
	@Test
	public void server() throws Exception  {
		
	}
}
