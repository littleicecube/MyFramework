package com.palace.seeds.net.netty;

public class analysis {

	
	
	
	
/**
 *   p.addLast(new ChannelInitializer<Channel>() {
            @Override
            public void initChannel(Channel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                ChannelHandler handler = config.handler();
                if (handler != null) {
                    pipeline.addLast(handler);
                }

                // We add this handler via the EventLoop as the user may have used a ChannelInitializer as handler.
                // In this case the initChannel(...) method will only be called after this method returns. Because
                // of this we need to ensure we add our handler in a delayed fashion so all the users handler are
                // placed in front of the ServerBootstrapAcceptor.
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
 * 
 * 
 * 
 * final class PoolChunkList<T> implements PoolChunkListMetric {
    private static final Iterator<PoolChunkMetric> EMPTY_METRICS = Collections.<PoolChunkMetric>emptyList().iterator();
    private final PoolChunkList<T> nextList;
    private final int minUsage;
    private final int maxUsage;
    private final int maxCapacity;
    private PoolChunk<T> head;
    
    
   }
   
  	void add(PoolChunk<T> chunk) {
        if (chunk.usage() >= maxUsage) {
            nextList.add(chunk);
            return;
        }
        add0(chunk);
    }
 * 
 * 当分成功分配一个PoolChunk后,则把这个PoolChunk添加到
 * void add0(PoolChunk<T> chunk) {
        chunk.parent = this;
        if (head == null) {
            head = chunk;
            chunk.prev = null;
            chunk.next = null;
        } else {
            chunk.prev = null;
            chunk.next = head;
            head.prev = chunk;
            head = chunk;
        }
    }
 * 
 * 
 * 
 * 
 * 
 * 
 * abstract class PoolArena<T> implements PoolArenaMetric {
	    static final int numTinySubpagePools = 512 >>> 4;
	    final PooledByteBufAllocator parent;
	    private final int maxOrder;
	    final int pageSize;
	    final int pageShifts;
	    final int chunkSize;
	    final int subpageOverflowMask;
	    final int numSmallSubpagePools;
	    
	    private final PoolSubpage<T>[] tinySubpagePools;
	    private final PoolSubpage<T>[] smallSubpagePools;
	
	    private final PoolChunkList<T> q050;
	    private final PoolChunkList<T> q025;
	    private final PoolChunkList<T> q000;
	    private final PoolChunkList<T> qInit;
	    private final PoolChunkList<T> q075;
	    private final PoolChunkList<T> q100;
	
	    private final List<PoolChunkListMetric> chunkListMetrics;
	    
	    private long allocationsNormal;
	    // We need to use the LongCounter here as this is not guarded via synchronized block.
	    private final LongCounter allocationsTiny = PlatformDependent.newLongCounter();
	    private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
	    private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
	    private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
	
	    private long deallocationsTiny;
	    private long deallocationsSmall;
	    private long deallocationsNormal;
	    final AtomicInteger numThreadCaches = new AtomicInteger();
    }
 * 
 * PoolArena的基本数据结构
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
 * ===============内存相关:
 *从操作系统中分配一块指定大小的内存成功后,会返回一个内存地址,内存地址会被一个结构记录下来共后续使用
 *Arena,PoolChunk,PollSubPage都不是实际的内存块,而是内存分配情况的记录和组织,方便我们知道哪些内存被分配,分配了多少内存,
 *分配的内存的大小以及当内存不在使用时对其进行释放
 *
 * PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
 * 创建一个allocator用来分配并组织内存,在初始化的时候会根据当前机器的内存和cpu配置信息创建一定个数的Arena
 * 生成一个ArenaArr数组用来保存每个Arena实例
 * MinNumArena1 = Runtime.getRuntime().availableProcessors() * 2;
 * MinNumArena2 = Runtime.getRuntime().maxMemory() / 16MB / 2 / 3;
 * PoolArena的最大个数:
 * MaxNumArena = Math.max(0,Math.min(MinNumArena1,MinNumArena2);
 * 
 * 某个线程在分配内存时会从allocator的Arena数组中选择一个被线程引用最少的Arena将其用户PoolThreadCache
 * 封装后和当前线程通过ThreadLoacl绑定
 * 
 *	
 *首先明确的是内存块的相关概念：
 *1）内存分配的基本单文是byte（字节）1204代表1024个字节，8096代表8096个字节;
 *2）Arena-->Chunk-->PoolSubpage
 *3)默认情况下PoolSubpage==2^13==8192=8KB
 *4)默认情况下maxOrder=11
 *5)默认情况下chunkSize==(pageSize<<maxOrder)==(2^13<<11)==(8192<<11)==16MB
 *
 *	Arena:
 *		内存最顶层的描述是Arena，Arena里是具体的内存分配情况，由一个个的PoolChunk组成,多个PoolChunk组成PoolChunkList,
 *	每个PoolChunk是由一个个PoolSubPageSize组成，一个PoolSubPageSize的大小默认情况下是8192KB == 2^13
 *
 *	PoolChunk：
 *		一个PoolChunk是一棵完全二叉树的描述，默认情况下数的高度11 == maxOrder,共有2^11个叶子节点用来代表PoolSubPage
 *	那么一个PoolChunk代表的内存大小是PoolChunkSize == PoolSubPageSize*2^maxOrder == 2^13*2^11 == 2^24 == 16M;
 *	其中每个PoolSubPage的大小PoolSubPageSize == 2^13 ==8KB;每次分配的内存大小不可能都是8KB的，还会有其他的较小的size,
 *	那么这部分内存也是申请一个PoolSubPage,在按照某种方式进行组织。
 *
 *	PoolSubPage：
 *		是一个叶子节点描述了一个PoolSubPage在树中的相对位置（2048-4096)和对应PoolSubPage在内存中相对
 *	的起始地址：0+(PoolSubPageNumber-1)*PoolSubPageSize,一个个PoolSubPage会以链表的方式组织起来。
 *	我们知道我们使用的内存不可能大小都是8KB的，大多数情况下都只有几B,那么一个几B的内存需求分配一个8KB大小的PoolSubPageSize
 *	肯定不合适，因此对PoolSubPage又进一步做了划分，根据经验又定义了TinySubPage,SmallSubPage两种Page，但是并不存在这
 *	两种Page的类描述，这两种Page都是PoolSubPage,只是根据PoolSubPage中的pageSize来划分是那种Page
 * 
 * 
 * 
 * 
 * Arena
 	abstract class PoolArena<T> implements PoolArenaMetric {
	    final PooledByteBufAllocator parent;  	//当前Arena所属的allocator
	    private final int maxOrder;				//Arena中PollChunk树的高度,默认是11
	    final int pageSize;						//PollChunk中Subpage大小默认是8KB
	    final int pageShifts;					
	    final int chunkSize;					//PollChunk中Subpage大小默认是2^11 * 8KB = 16MB					
	    final int subpageOverflowMask;			//
	    final int numSmallSubpagePools;			//内存大小
	    private final PoolSubpage<T>[] tinySubpagePools;	//内存<512B的PoolSubpage组成的list
	    private final PoolSubpage<T>[] smallSubpagePools;	//内存  >=512B && 内存 <8KB 的PoolSubpage组成的list
	
	    private final PoolChunkList<T> q050;	//叶子节点被分配大于50%小于75%
	    private final PoolChunkList<T> q025;	//叶子节点被分配大于25%小于50%
	    private final PoolChunkList<T> q000;	//叶子节点没被使用
	    private final PoolChunkList<T> qInit;	//叶子节点刚刚被分配
	    private final PoolChunkList<T> q075;	//叶子节点被分配大于75%小于50%
	    private final PoolChunkList<T> q100;	//叶子节点被分配大于100%
	
	    private final List<PoolChunkListMetric> chunkListMetrics;
	    private long allocationsNormal;
	    
	    private final LongCounter allocationsTiny = PlatformDependent.newLongCounter();
	    private final LongCounter allocationsSmall = PlatformDependent.newLongCounter();
	    private final LongCounter allocationsHuge = PlatformDependent.newLongCounter();
	    private final LongCounter activeBytesHuge = PlatformDependent.newLongCounter();
	
	    private long deallocationsTiny;
	    private long deallocationsSmall;
	    private long deallocationsNormal;
	
	    private final LongCounter deallocationsHuge = PlatformDependent.newLongCounter();
		//被线程引用的次数
	    final AtomicInteger numThreadCaches = new AtomicInteger();
    
 	}
 	
 	
 	
 PoolChunk:	
 	final class PoolChunk<T> implements PoolChunkMetric {

	    final PoolArena<T> arena;					//所属的Arena
	    final T memory;
	    final boolean unpooled;
	
	    private final byte[] memoryMap;				
	    private final byte[] depthMap;
	    private final PoolSubpage<T>[] subpages;	//分配的PoolSubpage列表
	    private final int subpageOverflowMask;
	    private final int pageSize;					//PoolSubpage大小默认8KB
	    private final int pageShifts;		
	    private final int maxOrder;					//树高度默认11
	    private final int chunkSize;				//PoolChunk大小默认16MB
	    private final int log2ChunkSize;			//
	    private final int maxSubpageAllocs;
	    private final byte unusable;
	
	    private int freeBytes;
	
	    PoolChunkList<T> parent;
	    PoolChunk<T> prev;
	    PoolChunk<T> next;
	 }
	    
 * 
 * 
 *
 * 内存组织的过程:
 * 把内存按一定大小分配为一页一页的形式.默认情况下配置的一个页的大小是8192=8KB用PoolSubpage来描述,分配完成后我们需要把这些内存页按照完全二叉树的形式组织起来
 * 形成一个集合用PoolChunk来描述,这颗完全二叉树的高度默认情况下是11,他的叶子节点11层就有2^11=2048个节点,这2048个叶子几点代表了2048个内存页,多个PoolChunk
 * 又形成一个集合用PoolArena来描述
 * 可知:
 * PoolSubpage = 2^13 = 8192 = 8KB	//一页内存的大小
 * PoolChunk = 2^11 * PoolSubpage = 2^11 * 2^13 = 16MB //整棵树可以表示的内存大小

 * 
 * 
 * 那么一段内存的分配过程:
 * 1)选择一个PoolArena和当前线程绑定
 * 2)从PoolArena中分配一个PoolChunk
 * 3)从PoolChunk中分配一个可用的叶子节点PoolSubpage
 * 我们知道一个PoolSubpage的大小是8KB,但是我们不是每次都需要分配8KB的大小我们可能只需要2KB的大小或者500B的大小,那么需要对PoolSubpage在做一个细分
 * PoolSubpage.elemSize代表了PoolSubpage中最小的单元
 * 在netty中最小可分配单元是2^4=16B.其他的几种大小有
 * 16B,	32B,	64B,	128B,	256B,	512B,	1024B(1KB),	2048B(2KB),	4096B(4KB),	8192B(8KB)
 * 2^4,	2^5,	2^6,	2^7, 	2^8, 	2^9,	2^10,		2^11,	 	2^12,	   	2^13
 * 
 * 如果我们需要分配一个10B大小的内存,那么经过整形会分配一个最小的内存单元即16B,当前线程会选择一个和其绑定的PoolArena,在从PoolArena中创建一个PoolChunk
 * 在从PoolChunk中选择一个可用的叶子节点,然后创建一个PoolSubpage和叶子节点进行对应.初始化叶子节点设置叶子节点的大小PoolSubpage.pageSize = 8KB
 * 设置PoolSubpage.elemSize = 16B,那么一个PoolSubpage就有 = 8KB/16B = 8192/16 = 512个16B大小的节点,在PoolSubpage中有一个位图成员
 * 变量(PoolSubpage.bitmap)用来表示这512个16B大小的内存的分配情况,所以可以在代码中看到入下的初始化计算
 * 		bitmap = new long[pageSize >>> 10]; // pageSize / 16 / 64
 * (pageSize = 8KB = 8192)/16(最小的内存单元)/64(一个Long类型的字段有64位) = 8
 * 即需要一个长度为8的数组,这个数组是Long类型的.java中Long类型是64位的,故8 * 64 = 512,用来表示PoolSubpage中512个大小为16B的内存分配情况
 * 
 * 如果我们要分配一个500B的内存,经过整形会分配一个512B的内存,当前线程选择和其绑定的PoolArena,从PoolArena中判断是否有未用完的PoolChunk,如果没有在创建一个新的PoolChunk
 * 在从PoolChunk中选择一个PoolSubpage.elemSize为512B的PoolSubpage,如果没有则在PoolChunk中分配一个新的叶子节点即PoolSubpage,初始化设置其PoolSubpage.elemSize = 512
 * 由上可知PoolSubpage.elemSize =512的PoolSubpage需要 pageSize/elemSize = 8192/512 = 16个位图就能表示完,用到数组的第0个节点中的低16位用来表示这个PoolSubpage的分配情况
 * 
 * 
 * 
 * 
 * 
 * 
  
     如果存在未用完的PoolSubpage,则直接从 Arena.PoolSubpage<T>[]中查询并分配
  	synchronized (head) {
        final PoolSubpage<T> s = head.next;
        if (s != head) {
            assert s.doNotDestroy && s.elemSize == normCapacity;
            long handle = s.allocate();
            assert handle >= 0;
            s.chunk.initBufWithSubpage(buf, handle, reqCapacity);
            incTinySmallAllocation(tiny);
            return;
        }
    }
            如果缓存不存在,则从PoolChunk中分配新的 PoolSubpage,并缓存到Arena.PoolSubpage<T>[]中
    synchronized (this) {
        allocateNormal(buf, reqCapacity, normCapacity);
    }
  
  
  	缓存PoolChunk中分配的PoolSubpage到 Arena.PoolSubpage<T>[]中
   	private long allocateSubpage(int normCapacity) {
      //根据normCapacity经过流量整形后 Arena.PoolSubpage<T>[]元素
        PoolSubpage<T> head = arena.findSubpagePoolHead(normCapacity);
        synchronized (head) {
            int d = maxOrder; // subpages are only be allocated from pages i.e., leaves
            int id = allocateNode(d);
            if (id < 0) {
                return id;
            }
            //h
            final PoolSubpage<T>[] subpages = this.subpages;
            final int pageSize = this.pageSize;

            freeBytes -= pageSize;

            int subpageIdx = subpageIdx(id);
            PoolSubpage<T> subpage = subpages[subpageIdx];
            if (subpage == null) {
            //将行生成的PoolSubpage添加到 Arena.PoolSubpage<T>[]的数据中,下次如果 PoolSubpage中bitmap没有被用完时,直接从PoolSubpage分配
                subpage = new PoolSubpage<T>(head, this, id, runOffset(id), pageSize, normCapacity);
                subpages[subpageIdx] = subpage;
            } else {
                subpage.init(head, normCapacity);
            }
            return subpage.allocate();
        }
    }
    
            设置缓存的值
    head的引用来自于 Arena.PoolSubpage<T>[]
  	private void addToPool(PoolSubpage<T> head) {
        assert prev == null && next == null;
        prev = head;
        next = head.next;
        next.prev = this;
        head.next = this;
    }
  
 	如果PoolSubpage中的bitmap表示的内存空间被用完后,就从 Arena.PoolSubpage<T>[]缓存中移除,
 	下次内存分配是会从PoolChunk中分配新的PoolSubpage并缓存到Arena.PoolSubpage<T>[]
    private void removeFromPool() {
        assert prev != null && next != null;
        prev.next = next;
        next.prev = prev;
        next = null;
        prev = null;
    }
  
  
  
  
  
  
     private void allocateNormal(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        if (q050.allocate(buf, reqCapacity, normCapacity) || q025.allocate(buf, reqCapacity, normCapacity) ||
            q000.allocate(buf, reqCapacity, normCapacity) || qInit.allocate(buf, reqCapacity, normCapacity) ||
            q075.allocate(buf, reqCapacity, normCapacity)) {
            return;
        }
        //1)
       PoolChunk<T> c = newChunk(pageSize, maxOrder, pageShifts, chunkSize);
       //2)
       long handle = c.allocate(normCapacity);
        assert handle > 0;
        c.initBuf(buf, handle, reqCapacity);
        qInit.add(c);
    }
 * 
 * 1)创建一个PoolChunk
 *     PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize) {
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

        // Generate the memory map.
        memoryMap = new byte[maxSubpageAllocs << 1];
        depthMap = new byte[memoryMap.length];
        int memoryMapIndex = 1;
        for (int d = 0; d <= maxOrder; ++ d) { // move down the tree one level at a time
            int depth = 1 << d;
            for (int p = 0; p < depth; ++ p) {
            	//设置值为当前所在的层级,最小层级值为0,最大层级值为11
                memoryMap[memoryMapIndex] = (byte) d;
                depthMap[memoryMapIndex] = (byte) d;
                memoryMapIndex ++;
            }
        }
        subpages = newSubpageArray(maxSubpageAllocs);
    }
 * 
 * 
 * 其中for循环用来创建一颗完全二叉树
 * 
 * 
 * 
 * 
0	2^0=1			arr[1]=0												
1	2^1=2			arr[2]=1		arr[3]=1									
2	2^2=4			arr[4]=2		arr[5]=2		arr[6]=2		arr[7]=2	
3	2^3=8			arr[8]=3		arr[9]=3		arr[10]=3		arr[11]=3	
4	2^4=16			arr[16]=4		arr[17]=4		arr[18]=4		arr[19]=4	
5	2^5=32			arr[32]=5		arr[33]=5		arr[34]=5		arr[35]=5	
6	2^6=64			arr[64]=6		arr[65]=6		arr[66]=6		arr[67]=6	
7	2^7=128			arr[128]=7		arr[129]=7		arr[130]=7		arr[131]=7	
8	2^8=256			arr[256]=8		arr[257]=8		arr[258]=8		arr[259]=8	
9	2^9=512			arr[512]=9		arr[513]=9		arr[514]=9		arr[515]=9	
10	2^10=1024		arr[1024]=10	arr[1025]=10	arr[1026]=10	arr[1027]=10
11	2^11=2048		arr[2048]=11	arr[2049]=11	arr[2050]=11	arr[2051]=11

0																		1
																		|
															  +---------------------+
															  |						|
1															  2						3
															  |						|
														-------------		-----------------
														|			|		|				|
2														4			5		6				7

.																		.
.																		.					
.																		.
.
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
 * 
 * 
 * 2)从PoolChunk中分配一个PoolSubpage
 *     private long allocateSubpage(int normCapacity) {
 *      //查看是否存在还未用完的PoolSubpage
        PoolSubpage<T> head = arena.findSubpagePoolHead(normCapacity);
        synchronized (head) {
            int d = maxOrder; // subpages are only be allocated from pages i.e., leaves
            //3)从PoolChunk中的完全二叉树中分配一个可用的节点
            int id = allocateNode(d);
            if (id < 0) {
                return id;
            }

            final PoolSubpage<T>[] subpages = this.subpages;
            final int pageSize = this.pageSize;

            freeBytes -= pageSize;

            int subpageIdx = subpageIdx(id);
            PoolSubpage<T> subpage = subpages[subpageIdx];
            if (subpage == null) {
            	//5)创建一个PoolSubpage实例用来表示树的节点
                subpage = new PoolSubpage<T>(head, this, id, runOffset(id), pageSize, normCapacity);
                subpages[subpageIdx] = subpage;
            } else {
                subpage.init(head, normCapacity);
            }
            //7)从叶子节点中分配可用的内存信息
            return subpage.allocate();
        }
    }
 * 
 * 3)分配一个可用的节点,从第一层开始比较
 * 
 * 
 * 	######
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
		
		
 * 
 *     private int allocateNode(int d) {
 *     	//int d是maxOrder(树的高度11) = 11
 *      //id是memoryMap数组的下标 id = 1 表示从最顶层开始
        int id = 1;
        int initial = - (1 << d); // has last d bits = 0 and rest all = 1
        //value(id)表示根据数组的下标获取数组的值,数组的值表示当前所在的层级
        //如果最最顶层的值大于11表示整棵树都被分配完,没有可用的节点了
        byte val = value(id);
        if (val > d) { // unusable
            return -1;
        }
        //从上图可知,树的每一行开始的下标都是以偶数开始的如(^平方)2^1=2,2^2=4,2^3=8......
        //根据下标从数组中获取其所在的层级,然后和树的最大高度11比较,小于树的高度则表示还没有被分配
        //如果大于树的高度则异或计算出下标相邻的奇偶位,并获取对应的下标,在根据下标从数组中获取其所在层级和树的高度比较
        //如果id的奇数位下标对应的数组值(所在层级)也大于树的最大高度11,则表示奇数位下的节点也被分配完了
        while (val < d || (id & initial) == 0) { // id & initial == 1 << d for all ids at depth d, for < d it is 0
        	//id左移一位,运算id的二次方值
            id <<= 1;
            //获取数组下标对应的值
            val = value(id);
            //如果数组下标对应的值大于marOrder(树的高度11),表示这个节点已经被分配
            if (val > d) {
            	//异或计算,id = 2^8 = 256 ,id^1= 257; id = 257 = 2^8+1 , id^1 = 256;
            	//id ^1的作用是,如果id是奇数id^1计算出的值是其相邻的偶数值,如果id是偶数id^1计算出的值是其相邻的奇数值
                id ^= 1;
                //得出id相邻奇偶下标获取其所在的层级
                val = value(id);
            }
        }
        byte value = value(id);
        setValue(id, unusable); // mark as unusable
        //4)
        updateParentsAlloc(id);
        return id;
    }
    
 * 
 * 4)标记被分配节点的父节点,用来表示父节点下的子节点的分配情况
 * updateParentsAlloc和allocateNode(分配节点)相反,updateParentsAlloc是从底部往上循环设置
 *     private void updateParentsAlloc(int id) {
 *     //假设id = 2048
        while (id > 1) {
        	//计算其对应的父节点 1024
            int parentId = id >>> 1;
            //获取当前节点的层级值为12
            byte val1 = value(id);
            //获取当前节点相邻的奇偶节点对应的层级值,val2 = arr[2049] = 11
            byte val2 = value(id ^ 1);
            //获取当前节点对应的层级值和其相邻的奇偶节点对应层级值中的最小层级值
            byte val = val1 < val2 ? val1 : val2;
            //设置其父节点的层级值为其两个子节点中最小的层级值,id = 2018,arr[2048]= 12,id^(异或)1 =2049 arr[2049]=11,那么其父节点arr[1024]=11,由原来的10变成11
            //当2049这节点也被分配后,则arr[2048]=12,arr[2049]=12,那么arr[1024] = 12,表示1024对应的两个子节点都已经被分配完
            setValue(parentId, val);
            id = parentId;
        }
    }
 * 
 * 5)创建一个PoolSubpage,用来表示被分配的叶子节点,以及内存的使用情况
 * PoolSubpage(PoolSubpage<T> head, PoolChunk<T> chunk, int memoryMapIdx, int runOffset, int pageSize, int elemSize) {
        this.chunk = chunk;
        this.memoryMapIdx = memoryMapIdx;
        this.runOffset = runOffset;
        this.pageSize = pageSize;
        //创建一个位图用来表示内存的使用情况,一个PoolSubpage最小单元是16B,故可以有8192/16 = 512个,512/64(Long类型长度64位) = 8个Long类型的实例
        bitmap = new long[pageSize >>> 10]; // pageSize / 16 / 64
        //6)
        init(head, elemSize);
    }
 * 
 * 6)初始化PoolSubpage时使用情况
 *     void init(PoolSubpage<T> head, int elemSize) {
        doNotDestroy = true;
        this.elemSize = elemSize;
        if (elemSize != 0) {
        	//计算出元素的个数,8192B/16B = 512
            maxNumElems = numAvail = pageSize / elemSize;
            nextAvail = 0;
            //元素的个数右移6位等于除以2^6=64(Long类型长度64位),计算出Long类型的个数.Long数组的长度
            bitmapLength = maxNumElems >>> 6;
            //2^6-1 = 63 = 00011111(低5位为1)
            //&63表示只取低5位,高位在&后都为0,由于maxNumElems都是2的几次方关系,那么(maxNumElems & 63) != 0成立表示maxNumElems小于64
            //假设maxNumElems共16位,由于maxNumElems都是2的几次方关系,如果maxNumElems<64,那么低5位肯定不都为0,而高位由于&后都是0,那么(maxNumElems & 63) != 0成立
            //假设maxNumElems共16位,由于maxNumElems都是2的几次方关系,如果maxNumElems>=64,那么低5位肯定都为0,那么低5位&后是0,高位&后也是0,那么(maxNumElems & 63) != 0不成立
            //
            //如果 elemSize >=128 ,那么pageSize/elemSize=8192/elemSize < 64,经过 bitmapLength = maxNumElems >>> 6 = maxNumElems/64后,bitmapLength = 0,然而
            //这个时候bitmapLength的长度肯定不是0,而是最小长度1,故需要用来判断maxNumElems是否大于63来决定bitmapLength的长度
            if ((maxNumElems & 63) != 0) {
                bitmapLength ++;
            }

            for (int i = 0; i < bitmapLength; i ++) {
                bitmap[i] = 0;
            }
        }
        addToPool(head);
    }
 * 
 * 7)从叶子节点中分配可用的内存信息
 *   long allocate() {
        if (elemSize == 0) {
            return toHandle(0);
        }
        if (numAvail == 0 || !doNotDestroy) {
            return -1;
        }
		//在位图中寻找下一个可用的位图索引
		//8)寻找下一个没有被分配的索引
        final int bitmapIdx = getNextAvail();
        //除以64获取所在数组的小标
        int q = bitmapIdx >>> 6;
        //取低5位中最大值
        int r = bitmapIdx & 63;
        assert (bitmap[q] >>> r & 1) == 0;
        bitmap[q] |= 1L << r;

        if (-- numAvail == 0) {
            removeFromPool();
        }
		//)
        return toHandle(bitmapIdx);
    }
 * 
 * 
 * 8)寻找下一个没有被分配的索引
 * private int getNextAvail() {
        int nextAvail = this.nextAvail;
        if (nextAvail >= 0) {
            this.nextAvail = -1;
            return nextAvail;
        }
        return findNextAvail();
    }

    private int findNextAvail() {
        final long[] bitmap = this.bitmap;
        final int bitmapLength = this.bitmapLength;
        for (int i = 0; i < bitmapLength; i ++) {
        	//遍历每一个Long类型
            long bits = bitmap[i];
            //如果取反不为0,表示还有空余的索引未用,只有Long类型的64位都是1都被用完后,~bits取反后才为0
            if (~bits != 0) {
                return findNextAvail0(i, bits);
            }
        }
        return -1;
    }
 * 
 *用来寻找Long的64位中哪一位不是0,找到则返回对应的索引值
 * private int findNextAvail0(int i, long bits) {
        final int maxNumElems = this.maxNumElems;
        final int baseVal = i << 6;

        for (int j = 0; j < 64; j ++) {
            if ((bits & 1) == 0) {
                int val = baseVal | j;
                if (val < maxNumElems) {
                    return val;
                } else {
                    break;
                }
            }
            bits >>>= 1;
        }
        return -1;
    }
 * 
 * )返回分配的内存信息
 *     private long toHandle(int bitmapIdx) {
 *     //0x4000000000000000L=>(64位)0100000000000000000000000000000000000000000000000000000000000000
 *     //bitmapIdx << 32  bitmapIdx放在高32位,bitmapIdx的最大值8*64=512
 *     //memoryMapIdx放在低32位,memoryMapIdx的最大值4096
        return 0x4000000000000000L | (long) bitmapIdx << 32 | memoryMapIdx;
    }
    
    
    log2ChunkSize = 24
    假设id = 2014 => depth = 10 => 24-10=14 => 1<<14 = 2个page
     private int runLength(int id) {
        // represents the size in #bytes supported by node 'id' in the tree
        return 1 << log2ChunkSize - depth(id);
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
 
	
	
*/	
	
	
}
