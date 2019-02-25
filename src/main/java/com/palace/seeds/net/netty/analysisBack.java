package com.palace.seeds.net.netty;

public class analysisBack {

	
	
	
	
/**
 * ==================
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * ====================关于多线程
 * 
 * 当用户程序调用内核方法时，需要发生软中断，然后调用内核方法，这个时候操作系统需要将当前用户在程序的栈结构保存起来，然后转到内核代码中，
 * 调用完成后，还需要还原用户线程的栈和其他相关结构这是一个非常耗时的操作。
 * 这样的话，调度程序和内核程序是两个独立的进程的程序，调度程序负责给内核程序和用户程序分配时间片，用户程序和内核程序需要在这些时间片上执行代码。
 * 出现的情况是当用户程序在执行用户程序中的代码时，这些指令占用cpu时间，当用户程序需要调用其他进程中的方法时，比如调用内核中的方法时，需要切换到
 * 内核的执行代码上，但是当前执行还是用的原来的用户程序的cpu时间，只是程序代码发生里切换。
 * 当在程序中用到了阻塞锁时，多个线程争用同一把锁，其中一个线程没有争用到锁的情况下就需要调用内核程序中的代码进行休眠，这里有两个耗时的操作
 * 一是：用户程序调用内核程序互斥操作产生的 切换开销。
 * 二是：线程的阻塞等待 休眠时间。
 * 在netty中大量应用了cas，用来同步，cas操作是在用户进程执行的时间片中执行的用户代码，不涉及到用户程序到 内核程序的切换，免去了内核切换的开销。
 * 但是这样类似于自旋锁，需要不断的去监测变量的值是否满足需求，需要代码上的循环，无效的循环也是一种开销，和互斥 调用各有利弊需要具体 分析。
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * ===============流程相关：
 * 线程的关系：
 * （1）主线程也是启动线程，主要执行ServerBootstrap中相关代码，用来启动bossGroup事件组。
 * （2）bossGroup中存在一个线程组，用来处理数据
 * （3）workerGroup中存在一个线程组，用来处理数据
 * 以上三个线程组是netty存在的主要的线程。bossGroup和workerGroup中存在selector，selector可以看做是一个类，类中存在一个
 * list或者数组或者是队列，主线程可以创建一个实例，实例的引用可以保留在主线程中，然后把实例注册到bossGroup中的selector上，selector轮询
 * 事件在轮询自己的集合处理到该数据时，可能会修改该实例中数据状态，而主线程中可以对该实例像bossGroup中一样不断轮询改实例，不断检查其状态值
 * 如果符合条件，在主线程中取消对其轮询，或进行其他操作，从而达到异步处理的目的。
 * bossGroup中的数据也可以和wokerGoroup中的数据进行结合和主线程和bossGroup中的数据处理方式一样，从而达到异步处理的效果。
 * future处理的条件：
 * 		1)两个不同的线程
 * 		2)两个线程都在轮询处理数据
 * 		3)共享的数据句柄
 * 
 * 基本流程：
 * 1)
 *    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
 *    EventLoopGroup workerGroup = new NioEventLoopGroup();
 * 2)   
 * 创建bossGroup用来接收请求的到来，workerGroup用来读取和发送数据,每个group中的存在一个selector，用来轮训事件.
 * 在bossGroup中主要用来轮询请求的到来，在workerGroup中主要用来轮询读，写，连接关闭事件.
 * 3)
 * 每个group中存在一个eventLoop来处理数据，每当selector筛选到数据后，都会根据规则从group中选中一个eventLoop用来处理数据
 * selector轮询时，不光轮询selector中的集合数据还会按照一定的几率轮询eventLoop中的集合数据，eventLoop中的集合数据，属于任务
 * 数据，netty中会把一些代码，封装成一个task存放到集合中，当轮询运行时执行。
 * 4)
 * 正常情况下打开一个server端的socket，serverSocket，然后：
 * while(true){
 * 	Socket socket = serverSocket.accept();
 * 	new Thread(new Runnable(){
 * 		socket.read(buff);
 * 		}
 * 	).start();
 * 和selector结合以后就变成：serverSocketChannel.register(selector,Selector.OPERATOR_ACCEPT,serverSocketChannel);
 * serverSocket打开一个channel，然后将其注册到selector上，并监听他的accept事件，
 * 和netty结合以后就变成：bossGroup绑定端口时，首先需要创建一个NioServerSocketChannel的实例，并指定端口为配置的端口号，然后将
 * 生成的NioServerSocketChannel实例注册到bossGroup中的selector上，然后通过bossGroup中的eventLoop去轮询获取到来的连接事件
 * 获取到新的连接selectionKey[],
 * private void processSelectedKeysOptimized(SelectionKey[] selectedKeys) {
        for (int i = 0;; i ++) {
            final SelectionKey k = selectedKeys[i];===(1)===
            //从key中获取到绑定的channel
            final Object a = k.attachment();
            if (a instanceof AbstractNioChannel) {
            	//处理得到的key和其绑定的channel
                processSelectedKey(k, (AbstractNioChannel) a);===(2)===
            } else {
                NioTask<SelectableChannel> task = (NioTask<SelectableChannel>) a;
                processSelectedKey(k, task);
            }
 			....
        }
    }
 * 
 * 
 * 从上面的方法中看出SelectionKeys[]是个数组，需要循环遍历其中的每个SelectionKey,从key中获取绑定的NioServerSocektChannel ch
 * 从ch中获取对应的unsafe()来读取消息内容。
 * private void processSelectedKey(SelectionKey k, AbstractNioChannel ch) {
        final AbstractNioChannel.NioUnsafe unsafe = ch.unsafe();===(3)===
        try {
            if ((readyOps & (SelectionKey.OP_READ | SelectionKey.OP_ACCEPT)) != 0 || readyOps == 0) {
                unsafe.read();
            }
        } catch (CancelledKeyException ignored) {
            unsafe.close(unsafe.voidPromise());
        }
    }
 * 
 * 
 *  调用doReadMessages(readBuf)将接收到的数据读取并存放到readBuf中
 *  private final List<Object> readBuf = new ArrayList<Object>();
 *  public void read() {
            final ChannelConfig config = config();
            final ChannelPipeline pipeline = pipeline();
            final RecvByteBufAllocator.Handle allocHandle = unsafe().recvBufAllocHandle();
            allocHandle.reset(config);
            boolean closed = false;
            Throwable exception = null;
            try {
                do {
                    int localRead = doReadMessages(readBuf);===(4)===
                    if (localRead == 0) {
                        break;
                    }
                    if (localRead < 0) {
                        closed = true;
                        break;
                    }

                    allocHandle.incMessagesRead(localRead);
                } while (allocHandle.continueReading());
            } catch (Throwable t) {
                exception = t;
            }
            ....
        }
    }
 * 
 * 
 * 将读取到的SocketChannel封装成一个NioSocketChannel，然后注册到workerGroup中
 * protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = SocketUtils.accept(javaChannel());===(5)===获取SocketChannel
        try {
            if (ch != null) {
                buf.add(new NioSocketChannel(this, ch));===(6)===
                return 1;
            }
        } catch (Throwable t) {
            ch.close();
        }
        return 0;
    }
 * 
 * 
 * 从ServerSocektChannel中accept到一个SocketChannel然后返回
 * public static SocketChannel accept(final ServerSocketChannel serverSocketChannel) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<SocketChannel>() {
                @Override
                public SocketChannel run() throws IOException {
                    return serverSocketChannel.accept();
                }
            });
        } catch (PrivilegedActionException e) {
            throw (IOException) e.getCause();
        }
    }
 * 从===(1)===，===(2)===，===(3)===， ===(4)===， ===(5)===， ===(6)===代表了bossGroup接收accept请求并注册到workerGroup的过程。
 * 
 *NioServerSocketChannel.register(selector,Selector.OPERATOR_ACCEPT,serverSocketChannel);NioServerSocketChannel将自己注册到selector上
 *这样每个selectionKey中都会附件绑定一个NioServerSocketChannel,如果只用一个NioServerSocketChannel实例，那么每个selectionKey都是绑定的同一个NioServerSocketChannel
 *往下的步骤就是：
 *1）从selectionKey中得到绑定的NioServerSocketChannel实例，然后调用其中的read方法
 *2）在read方法中，调用NioServerSocketChannel->ServerSocketChannel->accept()方法获取一个SocketChannel实例，该实例代表了连接的建立
 *3）封装socketChannel，new NioSocketChannel(socketChannel,NioServerSocketChannel);每个NioSocketChannel实例创建的时候内部都会创建一个config，pipeline
 *4）将NioSocketChannel注册到workerGroup上类似于NioServerSocketChannel实例注册到bossGrop上。
 *
 * 
 * 
 * 
 * 
 * =============================Bootstrap的启动流程
 * 
 * 主线程中创建NioServerSocketChannel的实例，实例中创建pipeline和config的实例，创建完NioServerSocketChannel的实例后为实例中的pipeline添加一个
 * ChannelInitializer初始化执行方法，这个初始化方法中又为pipeline添加了一个ServerBootstrapAcceptor阀门
 *    p.addLast(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler handler = config.handler();
                if (handler != null) {
                    pipeline.addLast(handler);
                }
                ch.eventLoop().execute(new Runnable() {
                    @Override
                    public void run() {
                        pipeline.addLast(new ServerBootstrapAcceptor(
                                currentChildGroup, currentChildHandler, currentChildOptions, currentChildAttrs));
                    }
                });
            }
        });
 * 
 * 添加完阀门到pipleline后，只是完成了代码的组织关系，然后将NioServerSocketChannel的代码封装成一个taks添加到EventLoop中，等待EventLoop中的singleThread
 * 线程来执行
 * eventLoop.execute(new Runnable() {
        @Override
        public void run() {
            register0(promise);
        }
    });
 * 
 * 从register0()的代码中我们可以看到其触发了NioServerSocketChannel pipeline中的rigester方法，然后进行注册事件的分发，在pipleline中会一个一个阀门的
 * 调用下去，然后知道上面初始化方法中添加的阀门。为什么说上面注册的阀门，因为BoostrapGroup中的eventloop只是 进行请求的接受，接收完请求后创建一个NioSocektChannel实例，
 * 这个实例是在wokerGroup中的selector注册的，那么怎么才能将boostrapGroup和wokerGroup衔接起来，就是通过boostrapGroup中nioServerSocketChannel实例
 * 的pipeline,这个pipeline中有个ServerBootstrapAcceptor阀门，当nioServerSocketChannle中生成nioSocketChannel实例后通过管道分发到ServerBootstrapAcceptor
 * 阀门将nioSocketChannel注册到wokerGroup中的selector上。
 *        
*	  private void register0(ChannelPromise promise) {
	    try {
	        // check if the channel is still open as it could be closed in the mean time when the register
	        // call was outside of the eventLoop
	        if (!promise.setUncancellable() || !ensureOpen(promise)) {
	            return;
	        }
	        boolean firstRegistration = neverRegistered;
	        doRegister();
	        neverRegistered = false;
	        registered = true;
	
	        // Ensure we call handlerAdded(...) before we actually notify the promise. This is needed as the
	        // user may already fire events through the pipeline in the ChannelFutureListener.
	        pipeline.invokeHandlerAddedIfNeeded();
	
	        safeSetSuccess(promise);
	        pipeline.fireChannelRegistered();
	        // Only fire a channelActive if the channel has never been registered. This prevents firing
	        // multiple channel actives if the channel is deregistered and re-registered.
	        if (isActive()) {
	            if (firstRegistration) {
	                pipeline.fireChannelActive();
	            } else if (config().isAutoRead()) {
	                // This channel was registered before and autoRead() is set. This means we need to begin read
	                // again so that we process inbound data.
	                //
	                // See https://github.com/netty/netty/issues/4805
	                beginRead();
	            }
	        }
	    } catch (Throwable t) {
	        // Close the channel directly to avoid FD leak.
	        closeForcibly();
	        closeFuture.setClosed();
	        safeSetFailure(promise, t);
	    }
    }
 * 
 * 
 * 1）EventLoopGroup中的线程不是java util 包中的Executor线程池的形式，而是一个线程数组，数据是按照
 * 		一定的分配规则被分配到不同数组下标对应的线程上
 * 2）NioServerSocketChannel和eventloop的关系，NioServerSocketChannel的实例是在BootStrap中被创建的
 * 		bossGroup和workerGroup也都存在于Bootstrap中，Bootstrap中的代码负责把创建的NioServerSocketChannel
 * 实例和bossGroup进行关联，从boosGroup中选取一个线程即eventloop，用来执行NioServerSocketChannel中的代码对外提供服务
 * 
 * 
 * 
 * ===============NioServerSocketChannel和NioSocketChannel
 * NioServerSocketChannel，NioSocketChannel都间接继承自AbstractNioChannel，为了代码的复用作者也是煞费苦心，在AbstractNioChannel中都有一个unsafe变量
 * 这个变量中的方法用来进行实际的读写操作，而NioServerSocketChannel中的读写操作是和NioSocketChannel的操作是不相同的，故抽象的放在Abstract中，实际unsafe是什么类型
 * 由对应的实现来进行赋值。
 * NioServerSocketChannel和NioSocketChannel内部都有一个config，piple实例，每当一个NioServerSocketChannel和NioSocketChannel实例被创建的时候，内部都会
 * 创建一个新的config，piple实例，
 * 
 * 
 * ===============select:
 * 
 *关于定时任务的执行：
 *如果没有定时任务，delayNanos(currentTimeNanos)返回的是1秒中的毫秒表示
 *  long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
 * 那么这个计算的值为1，就不会走
 * if (timeoutMillis <= 0) {
        if (selectCnt == 0) {
            selector.selectNow();
            selectCnt = 1;
        }
        break;
    }
 *如果有定时任务，那么定时任务肯定有一个创建时间，和一个多少时间延迟后执行的阈值。delayNanos(currentTimeNanos)计算的时候，首先那当前时间和任务的创建件时间
 *做差结果为tmp，然后在哪延迟时间和计算的差值做做差，如果大于零表示还没有到执行时间，然后在将差值和通过下面的计算
 *long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
 *如果timeoutMillis==0表示延迟的时间在1秒内，也就是说在1秒内的某个时间段内这个任务需要被执行，
 *如果timeoutMillis>0表示延迟的时间超过1秒，也就是说需要超过1秒后的某个时间这个任务需要被执行
 *如果任务需要在1秒的某个时间需要执行，那么首先进行selector.selectNow()来选址io任务，如果要执行的任务在1秒后的某个时间要执行，则调用selector.select(timeoutMillis);
 *时间阻塞方法，进行io任务的选择。
 *
 *主要作用时，如果没有任务则进行超时select如：selector.select(timeoutMillis);，如果有任务要执行立即select如：selector.selectNow(),优先处理io然后在处理任务。
 *
 *
 *     private void select(boolean oldWakenUp) throws IOException {
        Selector selector = this.selector;
        try {
            int selectCnt = 0;
            long currentTimeNanos = System.nanoTime();
            long selectDeadLineNanos = currentTimeNanos + delayNanos(currentTimeNanos);
            for (;;) {
                long timeoutMillis = (selectDeadLineNanos - currentTimeNanos + 500000L) / 1000000L;
                if (timeoutMillis <= 0) {
                    if (selectCnt == 0) {
                        selector.selectNow();
                        selectCnt = 1;
                    }
                    break;
                }

                // If a task was submitted when wakenUp value was true, the task didn't get a chance to call
                // Selector#wakeup. So we need to check task queue again before executing select operation.
                // If we don't, the task might be pended until select operation was timed out.
                // It might be pended until idle timeout if IdleStateHandler existed in pipeline.
                if (hasTasks() && wakenUp.compareAndSet(false, true)) {
                    selector.selectNow();
                    selectCnt = 1;
                    break;
                }

                int selectedKeys = selector.select(timeoutMillis);
                selectCnt ++;

                if (selectedKeys != 0 || oldWakenUp || wakenUp.get() || hasTasks() || hasScheduledTasks()) {
                    // - Selected something,
                    // - waken up by user, or
                    // - the task queue has a pending task.
                    // - a scheduled task is ready for processing
                    break;
                }
                if (Thread.interrupted()) {
                    // Thread was interrupted so reset selected keys and break so we not run into a busy loop.
                    // As this is most likely a bug in the handler of the user or it's client library we will
                    // also log it.
                    //
                    // See https://github.com/netty/netty/issues/2426
                    if (logger.isDebugEnabled()) {
                        logger.debug("Selector.select() returned prematurely because " +
                                "Thread.currentThread().interrupt() was called. Use " +
                                "NioEventLoop.shutdownGracefully() to shutdown the NioEventLoop.");
                    }
                    selectCnt = 1;
                    break;
                }

                long time = System.nanoTime();
                if (time - TimeUnit.MILLISECONDS.toNanos(timeoutMillis) >= currentTimeNanos) {
                    // timeoutMillis elapsed without anything selected.
                    selectCnt = 1;
                } else if (SELECTOR_AUTO_REBUILD_THRESHOLD > 0 &&
                        selectCnt >= SELECTOR_AUTO_REBUILD_THRESHOLD) {
                    // The selector returned prematurely many times in a row.
                    // Rebuild the selector to work around the problem.
                    logger.warn(
                            "Selector.select() returned prematurely {} times in a row; rebuilding Selector {}.",
                            selectCnt, selector);

                    rebuildSelector();
                    selector = this.selector;

                    // Select again to populate selectedKeys.
                    selector.selectNow();
                    selectCnt = 1;
                    break;
                }

                currentTimeNanos = time;
            }

            if (selectCnt > MIN_PREMATURE_SELECTOR_RETURNS) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Selector.select() returned prematurely {} times in a row for Selector {}.",
                            selectCnt - 1, selector);
                }
            }
        } catch (CancelledKeyException e) {
            if (logger.isDebugEnabled()) {
                logger.debug(CancelledKeyException.class.getSimpleName() + " raised by a Selector {} - JDK bug?",
                        selector, e);
            }
            // Harmless exception - log anyway
        }
    }
 * 
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *    @Override
    protected void wakeup(boolean inEventLoop) {
        if (!inEventLoop && wakenUp.compareAndSet(false, true)) {
            selector.wakeup();
        }
    }
 * 
 * 
 * 
 * 
 * ===============内存相关:
 * 内存组织的过程:
 * 把内存按一定大小分配为一页一页的形式.默认情况下配置的一个页的大小是8192=8KB,分配完成后我们需要把这个内存页按照完全二叉树的形式组织起来
 * 形成一个集合叫
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 位溢出：例如8192是2^13，在32位系统中：
 * 2^13		00000000000000000010000000000000
 * 2^13-1	00000000000000000001111111111111
 * ~（2^3-1）  11111111111111111110000000000000
 * ~（2^3-1）作为掩码和原有的值做与操作，如果原有的值中保存超过原有指定的值的大小，在做相与操作后，低位全部是0，高位是1，只要
 * 这个值大于零，那么就表示超过原有大小
 * 
 *  DEFAULT_TINY_CACHE_SIZE   = SystemPropertyUtil.getInt("io.netty.allocator.tinyCacheSize", 512);
 *  DEFAULT_SMALL_CACHE_SIZE  = SystemPropertyUtil.getInt("io.netty.allocator.smallCacheSize", 256);
 *  DEFAULT_NORMAL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.normalCacheSize", 64);
 * 
 *首先明确的是内存块的相关概念：
 *1）内存分配的基本单文是byte（字节）1204代表1024个字节，8096代表8096个字节;
 *2）Arena-->Chunk-->|-->Page;
 *					|-->TinyPage;
 *					|-->SmallPage;
 *3)默认情况下pageSize==2^13==8192=8KB
 *4)默认情况下maxOrder=11
 *5)默认情况下chunkSize==(pageSize<<maxOrder)==(2^13<<11)==(8192<<11)==16MB
 *
 *1）Arena和线程相关从%0--%25，%25--%45...
 *2）Arena中的内存会以chunkSize为单位将内存分配
 *3）一个chunk会将内存根据8192kb的大小将内存再次分配，并按照一个完全二叉树的形式将内存组织起来。默认情况下，一个chunk大小的
 *内存会被分配成2048个page,以完全二叉树的形式组织起来时，叶子节点共有2048个。
 *我们知道
 *我们假设从创建一个4096个节点长度的数组，并从数组下标1开始为数组赋值，数组下标0暂时空出来，这个数组下标和指数存在一个关系
 *2^n次方表明n次方这个层级有1到2^n方个节点，并且这个层级在数组中的开始下标就是从2^n方开始，maxorder=11，其基本关系图如下：
 *
 *2^0=1个节点,从1开始数到1,在数组中从下标1到1						
 *2^1=2个节点,从1开始数到2,在数组中从下标2到3						
 *2^2=4个节点,从1开始数到4,在数组中从下标4到7						
 *2^3=8个节点,从1开始数到8,在数组中从下标8到15						
 *2^4=16个节点,从1开始数到16,在数组中从下标16到31					
 *2^5=32个节点,从1开始数到32,在数组中从下标32到63					
 *2^6=64个节点,从1开始数到64,在数组中从下标64到127					
 *2^7=128个节点,从1开始数到128,在数组中从下标128到255				
 *2^8=256个节点,从1开始数到256,在数组中从下标256到511				
 *2^9=512个节点,从1开始数到512,在数组中从下标512到1023				
 *2^10=1024个节点,从1开始数到1024,在数组中从下标1024到2047			
 *2^11=2048个节点,从1开始数到2048,在数组中从下标2048到4095			
 *2^12=4096个节点,从1开始数到4096	,在数组中从下标4096到				
 *其中每层的指数代表了层次，从上面的计算可以看出，每层的和等于之前所有层数和在加+1，这样第12层就比上面11层所有的数据之和还要多1个，因此
 *在0节点没有用，而且每层的节点是以2^n开始的。
 
 2^0=1		0	arr[1]												....arr[1]
 2^1=2		1	arr[2]		arr[3]									....arr[3]
 2^2=4		2	arr[4]		arr[5]		arr[6]		arr[7]			....arr[7]
 2^3=8		3	arr[8]		arr[9]		arr[10]		arr[11]			....arr[15]
 2^4=16		4	arr[16]		arr[17]		arr[18]		arr[19]			....arr[31]
 2^5=32		5	arr[32]		arr[33]		arr[34]		arr[35]			....arr[63]
 2^6=64		6	arr[64]		arr[65]		arr[66]		arr[67]			....arr[127]
 2^7=128	7	arr[128]	arr[129]	arr[130]	arr[131]		....arr[255]
 2^8=256	8	arr[256]	arr[257]	arr[258]	arr[259]		....arr[511]
 2^9=512	9	arr[512]	arr[513]	arr[514]	arr[515]		....arr[1023]
 2^10=1024	10	arr[1024]	arr[1025]	arr[1026]	arr[1027]		....arr[2047]
 2^11=2048	11	arr[2048]	arr[2049]	arr[2050]	arr[2051]		....arr[4095]
 2^12=4096	12	arr[4096]	arr[4097]	arr[4098]	arr[4099]		....arr[8191]
 
   	memoryMap = new byte[maxSubpageAllocs << 1];
    depthMap = new byte[memoryMap.length];
    int memoryMapIndex = 1;
    for (int d = 0; d <= maxOrder; ++ d) {
        int depth = 1 << d;
        for (int p = 0; p < depth; ++ p) {
            memoryMap[memoryMapIndex] = (byte) d;
            depthMap[memoryMapIndex] = (byte) d;
            memoryMapIndex ++;
        }
    }
 	本段代码中的memoryMap作用创建内存的数据映射，maxorder==11表明要创建一个11层的完全二叉树结构，这个二叉树是以数组的形式表现的
  	第一层数据从数组下标1开始，之后的每层数据依次从数组中分配下标，数组下标对应的内容代表了层级，这样就可以直接根据数据从下标中定位出
  	数据所在的层级。
  	


	//					sr:2048:100000000000
	//-2048:11111111111111111111100000000000
    
 *
 *
 *
 
 
7 																   128																......	127
																	|
									 +--------------------------------------------------------------+
									 |																|
8									256															   257								......	511
									 |																|		
					  +------------------------------+								 +-------------------------------+
					  |								 |								 |								 |
9					 512							513								514								515				......	1023	
					  |								 |								 |								 |	
			 +----------------+				 +---------------+				 +---------------+				 +---------------+
			 |				  |				 |				 |				 |				 |				 |				 |
10			1024			1025			1026			1027			1028			1029			1030			1031	......	2047		
			 |				  |				 |				 |				 |				 |				 |				 |
		+--------+	 	 +-------+		 +--------+		 +-------+		 +-------+		 +-------+		 +-------+		 +-------+
		|		 |		 |		 |		 |		  |		 |		 |		 |		 |		 |		 |		 |		 |		 |		 |
11		2048	2049	2050	2051	2052	2053	2054	2055	2056	2057	2058	2059	2060	2061	2062	2063 ...... 4095	

 *
 *    private int allocateNode(int d) {
        int id = 1;
        int initial = - (1 << d); // has last d bits = 0 and rest all = 1
        byte val = value(id);
        if (val > d) { // unusable
            return -1;
        }
        while (val < d || (id & initial) == 0) { // id & initial == 1 << d for all ids at depth d, for < d it is 0
            id <<= 1;
            val = value(id);
            if (val > d) {
                id ^= 1;
                val = value(id);
            }
        }
        byte value = value(id);
        assert value == d && (id & initial) == 1 << d : String.format("val = %d, id & initial = %d, d = %d",
                value, id & initial, d);
        setValue(id, unusable); // mark as unusable
        updateParentsAlloc(id);
        return id;
    }
    
    id是数组的下标，进入循环，
    private void updateParentsAlloc(int id) {
        while (id > 1) {
        	将id无符号右移一位，算出上层的数据起点下标
            int parentId = id >>> 1;
			计算当前id对应的层级
            byte val1 = value(id);
			计算id加1或减1后对应的层级
            byte val2 = value(id ^ 1);
			给出一个节点A，如果A是奇数，A^1位偶数，取出这两个数所在的层级，选出一个层级
			给出一个节点A，如果A是偶数，A^1位奇数，取出这两个数所在的层级，选出一个层级
            byte val = val1 < val2 ? val1 : val2;
			设置父级节点的层级为计算出的层级
            setValue(parentId, val);
			将父级赋值给id在while循环中不断的向上层级递归，计算并设置更高父级节点的层级
            id = parentId;
        }
    }

 *节点分配代码，首先分配第11层的第2048个节点将其数组的下标重新赋值为12，11。。。。。。
 *代码过于精妙不在用语言描述，看代码吧。。。。。
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *PooledByteBuf的类成员变量，从变量中可以看出是对内部管理的内存的使用情况的度量。
 *  protected PoolChunk<T> chunk;
    protected long handle;
    protected T memory;
    protected int offset;
    protected int length;
    int maxLength;
    PoolThreadCache cache;
    private ByteBuffer tmpNioBuf;
    private ByteBufAllocator allocator;
 *
 * 
 * 
 * 
 * 
 * 
 * 
 * PoolArena的构造方法
 *     protected PoolArena(PooledByteBufAllocator parent, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
        this.parent = parent;
        this.pageSize = pageSize;
        this.maxOrder = maxOrder;
        this.pageShifts = pageShifts;
        this.chunkSize = chunkSize;
        subpageOverflowMask = ~(pageSize - 1);
        
		//作为一个静态成员变量numTinySubpagePools的大小是固定的32
        //static final int numTinySubpagePools = 512 >>> 4;
         
        tinySubpagePools = newSubpagePoolArray(numTinySubpagePools);
        for (int i = 0; i < tinySubpagePools.length; i ++) {
            tinySubpagePools[i] = newSubpagePoolHead(pageSize);
        }

		//默认情况下
        numSmallSubpagePools = pageShifts - 9;
        smallSubpagePools = newSubpagePoolArray(numSmallSubpagePools);
        for (int i = 0; i < smallSubpagePools.length; i ++) {
            smallSubpagePools[i] = newSubpagePoolHead(pageSize);
        }

        q100 = new PoolChunkList<T>(null, 100, Integer.MAX_VALUE, chunkSize);
        q075 = new PoolChunkList<T>(q100, 75, 100, chunkSize);
        q050 = new PoolChunkList<T>(q075, 50, 100, chunkSize);
        q025 = new PoolChunkList<T>(q050, 25, 75, chunkSize);
        q000 = new PoolChunkList<T>(q025, 1, 50, chunkSize);
        qInit = new PoolChunkList<T>(q000, Integer.MIN_VALUE, 25, chunkSize);

        q100.prevList(q075);
        q075.prevList(q050);
        q050.prevList(q025);
        q025.prevList(q000);
        q000.prevList(null);
        qInit.prevList(qInit);

        List<PoolChunkListMetric> metrics = new ArrayList<PoolChunkListMetric>(6);
        metrics.add(qInit);
        metrics.add(q000);
        metrics.add(q025);
        metrics.add(q050);
        metrics.add(q075);
        metrics.add(q100);
        chunkListMetrics = Collections.unmodifiableList(metrics);
    }
 * 
 * 
 * 
 * 
 * 
 * 
 * 每个线程会缓存一个PoolArena,PoolArena分配内存时会根据传入的要分配内存的大小，创建一个PooledByteBuf,PooledByteBuf
 * 并不代表内存，作为下面的方法的参数buf
 * 
 * PoolArena中的方法
 *   private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        if (q050.allocate(buf, reqCapacity, normCapacity) || q025.allocate(buf, reqCapacity, normCapacity) ||
            q000.allocate(buf, reqCapacity, normCapacity) || qInit.allocate(buf, reqCapacity, normCapacity) ||
            q075.allocate(buf, reqCapacity, normCapacity)) {
            return;
        }

        // Add a new chunk.
        PoolChunk<T> c = newChunk(pageSize, maxOrder, pageShifts, chunkSize);(1)
        long handle = c.allocate(normCapacity);
        c.initBuf(buf, handle, reqCapacity);（3）内存的初始化
        qInit.add(c);
    }
 * 
 * 在（1）中进行内存真正的分配调用方法如下：
 *  protected PoolChunk<ByteBuffer> newChunk(int pageSize, int maxOrder, int pageShifts, int chunkSize) {
        return new PoolChunk<ByteBuffer>(
                this, allocateDirect(chunkSize),
                pageSize, maxOrder, pageShifts, chunkSize);（2）
    }

 * （2）的内容：
 * 分配一个Chunk的大小，Chunk的长度由之前的参数决定，然后创建一个Chunk长度大小的数组，Chunk的长度作为数组的下标，下标作为向量使用数组的内容作为二叉数据的高度
 * 例如如果要分配1024字节的大小的数据，那么以1024作为数组的下标，而数据的内容就是二叉数据的高度，例如：deptMap[1024]==9;
 * 其中的memory参数是真正的分配的内存区域，再此讨论的是DirctMemory,PoolChunk对分配的内存区域进行了包装。
 * 
 * 默认情况下maxorder是11，那么树的
 * 深度11:如果深度是11，那么将会有2^11==2048个page，每一个page都有对应的编号，因为11是最大深度，11层代表的就是叶子节点，就是一个page，(默认情况下一个pageSize为2^13即8192(8MB)
 * 深度10：将会有2^10==1024个子节点和深度11结合起来就是深度10的每个节点下面存在两个叶子节点即两个page节点
 * 深度9:将会有2^9==512个子节点，和深度10结合起来就是深度9的每个节点下面存在两个子节点但是不是叶子几点，在结合11深度，那么深度9的每个节点下会有2*2==4个page节点
 * ...
 * 
 * 如此，当要求分配一个page大小的内存时，需要在深度11层选择一个节点就可以，当分配的内容大于一个page的大小，那么需要在10层分配一个节点，deptMap[待分配内存大小]==树深度
 * 因为有了deptMap数组的存在，可以直接定位要分配的内存所在数的深度，需要明白的是，这样只是定位到了要分配数据所在树的深度，这个深度层次内，可能存在多个节点，每个节点可能已经
 * 被分配，也可能没有被分配，因此也需要记录，每层中每个节点的分配，释放的使用情况。
 * 
 * 
 * PoolChunk的构造方法
    PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
        unpooled = false;
        this.arena = arena;
        this.memory = memory;
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        this.maxOrder = maxOrder;
        this.chunkSize = chunkSize;
        unusable = (byte) (maxOrder + 1);
        log2ChunkSize = log2(chunkSize);
        subpageOverflowMask = ~(pageSize - 1);
        freeBytes = chunkSize;

        assert maxOrder < 30 : "maxOrder should be < 30, but is: " + maxOrder;
        maxSubpageAllocs = 1 << maxOrder;
        
 		如注释中说的那样生成内存映射数据，是一二叉树的形式进行组织
        // Generate the memory map.
        memoryMap = new byte[maxSubpageAllocs << 1];
        depthMap = new byte[memoryMap.length];
        int memoryMapIndex = 1;
        for (int d = 0; d <= maxOrder; ++ d) { // move down the tree one level at a time
            int depth = 1 << d;
            for (int p = 0; p < depth; ++ p) {
                // in each level traverse left to right and set value to the depth of subtree
                memoryMap[memoryMapIndex] = (byte) d;
                depthMap[memoryMapIndex] = (byte) d;
                memoryMapIndex ++;
            }
        }
		//生成subpage长度的数据，
        subpages = newSubpageArray(maxSubpageAllocs);
    }
 * 
 * （3）真正的内存分配了，PoolChunk对真正分配的内存进行了低层次的包装，PooledByteBuf对PoolChunk再此进行了概括性的封装
 * 
 * 
 * 
 * 
 * 
 * 	######
	log2PoolChunkSize = log2(PoolChunkSize);计算PoolChunkSize的2的幂，我们知道
	一个PoolChunkSize == 2^13*2^11 ==2^24故log2PoolChunkSize = 24;
	如果id == 2049; depth(id) == 11; log2PoolChunkSize-11 = 13; 1<< 13 == 8192	是1个PageSize的大小
	如果id == 1024; depth(id) == 10; log2PoolChunkSize-10 = 14; 1<< 14 == 8192*2	是2个PageSize的大小
	如果id == 512 ; depth(id) == 9 ; log2PoolChunkSize-9  = 15; 1<< 15 == 8192*4	是4个PageSize的大小
	
	结合树的结构我们知道512节点为9层节点，每个节点下有两个10如1024，1025层节点，每个10层节点下有
	两个11层节点如2048,2049;2050,2051;
	runLength的作用是根据id获取节点所在层级，根据层级计算出当前层级下，一个节点下共有几个page并
	根据pageSize结算出，当前层级下一个层级节点代表的byte数组的大小==pageSize*pageNumber;
	private int runLength(int id) {
        return 1 << log2PoolChunkSize - depth(id);
    }
	
	######
	异或运算：相同为0，不同为1
	用例1：清零
		2^10 xor 2^10 == 0 ;
		经常用在汇编中用来清零本身
		
	用例2：计算溢出
		2^10 xor (2^10+1) == 1;
		2^10 xor (2^10+2) == 2;
		......
		2^10 xor (2^8) == 2^8;
		用来清除最高位和最高位之后的数值，保留除最高位到0位的数值
	
	前提：
		一个PoolChunkSize == 2^24大小的一维byte数组，表示一个高度11 == maxOrder的完全二叉树；
	
	private int runOffset(int id) {
        int shift = id ^ 1 << depth(id);
        return shift * runLength(id);
    }
	id == 2049; 11 == depth(2049); 2048 == 1 << depth(2049); shift == 1 == 2049 xor 2048;
	runLength(2049);知道所在层级下只有一个page,故length 8192 == 1*pageSize;
	1*8192 == shift * 8192;故2049节点在一维数组中的相对位置为8192；
	
	id == 1027; 10 == depth(1027); 1024 == 1 << depth(1027); shift == 3 == 1027 xor 1024;
	runLength(1027);知道所在层级下有2个page,故length 8192*2 == 2*pageSize;
	3*2*8192 == shift*2*8192; 故1027节点在一维数组中的相对位置为3*8192；
	
	id == 513; 9 == depth(513); 512 == 1 << depth(512); shift == 1 == 513 xor 512;
	runLength(513);知道所在层级下有2*2个Page，故length == 2*2*pageSize;
	1*2*2*8192 == shift * 2*2*8192;故节点在一维数据中的相对位置为1*2*2*pageSize;

	
	
	######
	PoolChunk代表一颗完全二叉树，用来组织16M的内存,这16M内存又被分为2048个page即PoolSubpage 
	其中每个PoolSubpage的代表内存的大小是8192KB
	PoolChunk = 16MB = 2^24 = 2^11 * 2^13 = 2048 * 8192 = pageNumber*pageSize;
	 一共有2048个Page，那么每当分配一个Page后，就需要计算
	这个配置在subpages中的下标，以及Page在16M内存中的相对起始位置
	//根据异或，去除高位计算Page在subpages中的下标
	private int subpageIdx(int memoryMapIdx) {
        return memoryMapIdx ^ maxSubpageAllocs; 
    }
	1）根据id计算出所在层级，然后和层级的基地址进行异或计算出在层级中的偏移位置
	2）根据runLength(id)，根据id获取层级数就可以根据完全二叉树和PageSize的大小计算出一个层级下一个节点代表的叶子节点总数的大小
	3）结合1）中的偏移量和2）中一个节点的大小就可以计算出当前节点在chunkSize中的偏移量也就是节点起始位置
	4）当前情况下返回的id都是叶子节点，没有非叶子节点的偏移计算
	
	private int runOffset(int id) {
        int shift = id ^ 1 << depth(id);
        return shift * runLength(id);
    }

	######
	一个PoolChunk是一个完全二叉数，树的高度是maxOrder(默认是11),maxOrder高度的完全二叉树
	共有2^maxOrder个叶子节点，也就是说，一个PoolChunk中有2^maxOrder叶子节点是有效的，这个树
	中共有2^0+2^1+2^2+2^3+2^4...+2^11=4096个节点，其中叶子节点有2^11=2048个节点，每个page
	的大小是2^13==8192，整棵树的叶子节点*Pagesize == 2^11*2^13 == 2^24==16M,另外2048个节点是非
	叶子节点，用来组织叶子节点的使用情况。

	从底往上遍历，最初从叶子节点往非叶子节点遍历
	id == 2048代表一个叶子节点，所在的层级为12，临近节点2049(id xor 1)所在层级为11，修改其父节点id>>1 == 1024的层级为11
	id == 1024当前所在层级为11(上层循环由10修改为11)，临近节点1025(id xor 1)所在节点为10，修改其父节点id>>1 == 512的层级为10
	id == 512当前所在层级为10(上层循环由9修改为10)，临近节点513(id xor 1)所在节点为9，修改其父节点id>>1 == 256的层级为9
	...
	id == 4当前所在层级为3(上层循环由2修改为3)，临近节点5所在节点为2，修改其父节点2id>>1 == 的层级为3
	id == 2当前所在层级为2(上层循环由1修改为2)，临近节点3所在节点为1，修改其父节点id>>1 == 1的层级2
	循环退出

	id == 2049代表一个叶子节点，所在的层级为12，临近节点2048(id xor 1)所在层级为12，修改其父节点id>>1 == 1024的层级为12
	id == 1024当前所在层级为12(上层循环由11修改为12)，临近节点1025(id xor 1)所在节点为10，修改其父节点id>>1 == 512的层级为10
	id == 512当前所在层级为10(上层循环由9修改为10)，临近节点513(id xor 1)所在节点为9，修改其父节点id>>1 == 256的层级为9
	...
	id == 4当前所在层级为3(上层循环由2修改为3)，临近节点5所在节点为2，修改其父节点id>>1 == 2的层级为3
	id == 2当前所在层级为2(上层循环由1修改为2)，临近节点3所在节点为1，修改其父节点id>>1 == 1的层级2
	循环退出



	id == 2050代表一个叶子节点，所在的层级为12，临近节点2051(id xor 1)所在层级为11，修改其父节点id>>1 == 1025的层级为11
	id == 1025当前所在层级为11(上层循环由10修改为11)，临近节点1024(id xor 1)所在节点为12，修改其父节点id>>1 == 512的层级为11
	id == 512当前所在层级为11(上层循环由10修改为11)，临近节点513(id xor 1)所在节点为9，修改其父节点id>>1 == 256的层级为9
	...
	id == 4当前所在层级为3(上层循环由2修改为3)，临近节点5所在节点为2，修改其父节点id>>1 == 2的层级为3
	id == 2当前所在层级为2(上层循环由1修改为2)，临近节点3所在节点为1，修改其父节点id>>1 == 1的层级2
	循环退出


	id == 2051代表一个叶子节点，所在的层级为12，临近节点2050(id xor 1)所在层级为12，修改其父节点id>>1 == 1025的层级为12
	id == 1025当前所在层级为12(上层循环由11修改为12)，临近节点1024(id xor 1)所在节点为12，修改其父节点id>>1 == 512的层级为12
	id == 512当前所在层级为12(上层循环由11修改为12)，临近节点513(id xor 1)所在节点为9，修改其父节点id>>1 == 256的层级为9
	...
	id == 4当前所在层级为3(上层循环由2修改为3)，临近节点5所在节点为2，修改其父节点id>>1 == 2的层级为3
	id == 2当前所在层级为2(上层循环由1修改为2)，临近节点3所在节点为1，修改其父节点id>>1 == 1的层级2
	循环退出

	总结：除叶子节点外，每个节点下包含两个子节点，其中任意一个子节点被分配后，父节点就设置成为和子节点一样的层级数，最后的一个
	子节点4095被分配后，整棵树的层级都设置为12.
	叶子节点被分配后层级数有11改为12，如果两个相邻的叶子节点都改为12后那么其父节点也会被设置成12。


	private void updateParentsAlloc(int id) {
		while (id > 1) {
			int parentId = id >>> 1;
			byte val1 = value(id);
			byte val2 = value(id ^ 1);
			byte val = val1 < val2 ? val1 : val2;
			setValue(parentId, val);
			id = parentId;
		}
	}


	
	######
	此段代码配合上面一段代码
	总括：
		当要分配新的叶子几点时，从上往下遍历，查看每个几点的层级是否大于12
			如果大于12代表这个父节点下的所有子节点都已经被分配，那么需要在当前层级向右平移一个节点
			如果小于等于12代表这个层级下还存在未分配的子节点，则继续向下遍历
		当一个叶子节点被分配后，会根据叶子节点向上回溯更改对应的父节点的层级变化，如果一个节点下的所有
			子节点都已经被分配则设置父节点的层级为12，叶子节点一点被分配就设置其层级为12
		
	private int allocateNode(int d) {
		int id = 1;
		int initial = - (1 << d); // has last d bits = 0 and rest all = 1
		//当前id ==1 如果起层级为12代表整棵树都已经被分配完了
		byte val = value(id);
		if (val > d) { // unusable
			return -1;
		}
		
		//从根节点往下遍历
		while (val < d || (id & initial) == 0) { // id & initial == 1 << d for all ids at depth d, for < d it is 0
			id <<= 1;
			val = value(id);
			//如果当前节点的层级大于12==d，那么代表当前节点下的所有自己点都已经被分配完了，
			//需要在当前层级往后移动一个节点进行分配
			if (val > d) {
				id ^= 1;
				val = value(id);
			}
		}
		byte value = value(id);
		assert value == d && (id & initial) == 1 << d : String.format("val = %d, id & initial = %d, d = %d",
				value, id & initial, d);
		setValue(id, unusable); // mark as unusable
		updateParentsAlloc(id);
		return id;
	}


     return 0x4000000000000000L | (long) bitmapIdx << 32 | memoryMapIdx;
 * 	
 * 
 * 
 * 
 * 
 * 
 * 
 
 
 
 
 								  / 	\
								 /	 	 \
 								/	  	  \
 							   /	   	   \
 							  /			    \
257	258 					 /			 	 \																								512
							/			 	  \
						   /			   	   \
						  /						\
						513						514							515							516			......	1024
						/ \						/ \							/ \							/ \
					   /   \				   /   \					   /   \					   /   \
					  /		\				  /	 	\					  /	    \					  /		\
					 /		 \				 /		 \					 /       \					 /		 \
					/		  \				/		  \					/         \					/		  \
				   /		   \		   /		   \			   /           \			   /		   \
				1025		  1026		 1027		  1028			 2019		  2020			 2021		  2022
				/	\		  /	  \		 /  \		  /	  \			 /	\		  /  \			 /  \		  /	 \
			2048	2049   2050	 2051  2052  2053	2054  2055	   2056	2057	2058 2059	  2060 2061		2062 2063
			
			
																   128
																	|
									 +--------------------------------------------------------------+
									 |																|
8									256															   257
									 |																|		
					  +------------------------------+								 +-------------------------------+
					  |								 |								 |								 |
9					 512							513								514								515
					  |								 |								 |								 |	
			 +----------------+				 +---------------+				 +---------------+				 +---------------+
			 |				  |				 |				 |				 |				 |				 |				 |
10			1024			1025			1026			1027			1028			1029			1030			1031			1032		
			 |				  |				 |				 |				 |				 |				 |				 |
		+--------+	 	 +-------+		 +--------+		 +-------+		 +-------+		 +-------+		 +-------+		 +-------+
		|		 |		 |		 |		 |		  |		 |		 |		 |		 |		 |		 |		 |		 |		 |		 |
11		2048	2049	2050	2051	2052	2053	2054	2055	2056	2057	2058	2059	2060	2061	2062	2063	
			
			
			
	
	
*/	
	
	
}
