package com.palace.seeds.net.netty;

public class analysis2 {

	
	
	
	
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
	    static final boolean HAS_UNSAFE = PlatformDependent.hasUnsafe();
	
	    enum SizeClass {
	        Tiny,
	        Small,
	        Normal
	    }
	
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
 * 
 * 
 * 	缓存PoolChunk中分配的PoolSubpage到 Arena.PoolSubpage<T>[]中
 *  private long allocateSubpage(int normCapacity) {
 *     //根据normCapacity经过流量整形后 Arena.PoolSubpage<T>[]元素
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
 * 
 *	如果PoolSubpage中的bitmap表示的内存空间被用完后,就从 Arena.PoolSubpage<T>[]缓存中移除,
 *	下次内存分配是会从PoolChunk中分配新的PoolSubpage并缓存到Arena.PoolSubpage<T>[]
    private void removeFromPool() {
        assert prev != null && next != null;
        prev.next = next;
        next.prev = prev;
        next = null;
        prev = null;
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
 * 内存组织的过程:
 * 把内存按一定大小分配为一页一页的形式.默认情况下配置的一个页的大小是8192=8KB用PoolSubpage来描述,分配完成后我们需要把这些内存页按照完全二叉树的形式组织起来
 * 形成一个集合用PoolChunk来描述,这颗完全二叉树的高度默认情况下是11,他的叶子节点11层就有2^11=2048个节点,这2048个叶子几点代表了2048个内存页,多个PoolChunk
 * 又形成一个集合用PoolArena来描述
 * 可知:
 * PoolSubpage = 2^13 = 8192 = 8KB	//一页内存的大小
 * PoolChunk = 2^11 * PoolSubpage = 2^11 * 2^13 = 16MB //整棵树可以表示的内存大小
 * PoolArena的最小个数是从
 * MinNumArena1 = Runtime.getRuntime().availableProcessors() * 2;
 * MinNumArena2 = Runtime.getRuntime().maxMemory() / 16MB / 2 / 3;
 * 取最小值  MinNumArena = Math.min(MinNumArena1,MinNumArena2);
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
                // in each level traverse left to right and set value to the depth of subtree
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

0																		0
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
        //如果最最顶层的值小于11表示整棵树都被分配完,没有可用的节点了
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
            //设置其父节点的层级值为其两个子节点中最小的层级值,id = 2018,arr[2048]= 12,id^(异或)1 =2049 arr[2049]=11,那么其父节点arr[1024]=11,有原来的10变成11
            //当2049这节点也被分配后,则arr[2048]=12,arr[2049]=12,那么arr[1024] = 12,表示1024对应的两个子节点都已经被分配完
            setValue(parentId, val);
            id = parentId;
        }
    }
 * 
 * 5)创建一个PoolSubpage,用来表示被分配的叶子节点,以及内存的使用情况
 *     PoolSubpage(PoolSubpage<T> head, PoolChunk<T> chunk, int memoryMapIdx, int runOffset, int pageSize, int elemSize) {
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
            //如果 elemSize >=128 ,那么pageSize / elemSize =8192/elemSize < 64,经过 bitmapLength = maxNumElems >>> 6 = maxNumElems/64后,bitmapLength = 0,然而
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
 *     //bitmapIdx << 32  bitmapIdx放在高32位,memoryMapIdx放在低32位
        return 0x4000000000000000L | (long) bitmapIdx << 32 | memoryMapIdx;
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
