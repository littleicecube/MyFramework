package com.palace.seeds.net.netty;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.palace.seeds.utils.MovPrint;

import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.internal.SystemPropertyUtil;

public class Main {
	
	    private static   int DEFAULT_NUM_HEAP_ARENA;
	    private static   int DEFAULT_NUM_DIRECT_ARENA;
	    
	    //
	    static final int numTinySubpagePools = 512 >>> 4; //32 = 512/16
	    // 32 * 512 = 16 * 1024
	    int DEFAULT_TINY_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.tinyCacheSize", 512);
        int DEFAULT_SMALL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.smallCacheSize", 256);
        int DEFAULT_NORMAL_CACHE_SIZE = SystemPropertyUtil.getInt("io.netty.allocator.normalCacheSize", 64);

	    private static final int MIN_PAGE_SIZE = 4096;
	    private static final int MAX_CHUNK_SIZE = (int) (((long) Integer.MAX_VALUE + 1) / 2);
	    
	@Test
	public void reg() throws Exception {
		 Selector.open().selectedKeys();
		ServerSocketChannel.open().socket().bind(new InetSocketAddress(8848));
	}

    @Test
    public void init() {
        final Runtime runtime = Runtime.getRuntime();
        final int defaultMinNumArena = runtime.availableProcessors() * 2;
        
        final int defaultChunkSize = 8192;
        long un = runtime.maxMemory() / defaultChunkSize / 2 / 3;
        DEFAULT_NUM_HEAP_ARENA = Math.max(0,
                SystemPropertyUtil.getInt(
                        "io.netty.allocator.numHeapArenas",
	                        (int) Math.min(
	                                defaultMinNumArena,
	                                runtime.maxMemory() / defaultChunkSize / 2 / 3)));
        System.err.println(DEFAULT_NUM_HEAP_ARENA);
 
	}
    
    @Test
    public void dd() {
    	int v1 = 1<<10;
    	int v2 = 1<<8;
    	int v3 = v1 ^ v2;
    	System.err.println(v1+";"+v2+";"+v3+",b:"+Integer.toBinaryString(v3));
    	System.err.println();
    	System.err.println(32>>>4);
    	System.err.println(16>>>4);
    	System.err.println("513<<1:"+(513<<1));
    }

	@Test
	public void allocator() {
		ByteBuffer.allocate(12345);
		ByteBuffer.allocateDirect(12334);
	}
	//AdaptiveRecvByteBufAllocator  NioServerSocketChannel config中的内存分配器
	
	AtomicInteger counter = new AtomicInteger(0);
	AtomicInteger allocateSize = new AtomicInteger(0);
	long chunkSize = 8192<<11;
	
	
	
	@Test
	public void releaseMemory() {
		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
		for(int i=0;i<257;i++) {
			allocator.ioBuffer(8192*2);
		}
		
	}
	
	
	//超过poolchunk大小2<<13<<11的内存块就不在采用池化的方法,而是直接分配对应大小的数据
	@Test
	public void poolChunkForMoreThanOnePoolTrunk() {
		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
		for(int i=0;i<257;i++) {
			allocator.ioBuffer((8192<<11)*2);
		}
	}
	
	@Test
	public void poolChunkForMoreThanOnePage() {
		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
		for(int i=0;i<257;i++) {
			allocator.ioBuffer(8192*2);
		}
	}
	@Test
	public void poolChunkForTiny() {
		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
		for(int i=1;i<=258;i++) {
			String msg = "分配内存:"+i+",大小:"+i*32;
			System.err.println(msg);
			if(i == 256) {
				allocator.ioBuffer(32);
			}else if(i == 4){
				allocator.ioBuffer(32);
			}else {
				allocator.ioBuffer(32);
			}
		}
	}
	@Test
	public void poolChunkForSmall(){
		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
		for(int i=0;i<4;i++) {
			int c = counter.incrementAndGet();
			System.out.println("开始第"+c+"次分配");
			allocator.ioBuffer(2048);
			System.out.println("第"+c+"次分配完成");
		}
		System.out.println("第"+counter.get()/4+"页");
		for(int i=0;i<4;i++) {
			int c = counter.incrementAndGet();
			System.out.println("开始第"+c+"次分配");
			allocator.ioBuffer(2048);
			System.out.println("第"+c+"次分配完成");
		}

		
		for(int j=0;j<(2<<13)-2;j++) {
			System.out.println("第"+counter.get()/4+"页");
			if(counter.get()/4 == 511) {
				System.out.println("break;");
			}
			for(int i=0;i<4;i++) {
				int c = counter.incrementAndGet();
				System.out.println("开始第"+c+"次分配");
				allocator.ioBuffer(2048);
				System.out.println("第"+c+"次分配完成");
			}
		}
	}
	@Test
	public void poolChunkForPoolChunk(){
		PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;
		for(int i = 0;i<8192;i++) {
			int c = counter.incrementAndGet();
			System.out.println("第"+c/2+"页,第"+c+"次分配开始");
			allocator.ioBuffer(4096);
			int allcate = allocateSize.addAndGet(4096);
			int percent =  new BigDecimal(allcate).divide(new BigDecimal(chunkSize)).multiply(new BigDecimal(100)).intValue();
			if(percent == 99) {
				System.out.println("percent:24");
			}
			System.out.println("第"+c/2+"页,第"+c+"次分配完成,已分配chunkSize percent:"+(percent));
		}
		System.out.println("end");
	}
	
	
	@Test
	public void printMovTest() {
		String str = "3FFFFFFF";
		Integer.parseInt(str,16);
		String b = Integer.toBinaryString(Integer.parseInt(str,16));
		System.out.println(str+":"+b+":"+b.length());
		String str1 = "4000000000000000";
		String b1 = Long.toBinaryString(Long.parseLong(str1,16));
		System.out.println(str1+":"+b1+":"+b1.length());
		
		MovPrint.pInt(1073741824);
		//2^9=512
		MovPrint.pLong(Long.parseLong("FFFFFE00",16));
	}
	@Test
	public void testMov() {
		System.out.println(4092>>>1);
		int i = 1024;
		System.out.println(i >>>= 2);
	}
	@Test
	public void testF() {
		int reqCapacity = 3096;
		System.out.println(Integer.toBinaryString(reqCapacity));
		int normalizedCapacity = reqCapacity;  
        normalizedCapacity --;  
        System.out.println(Integer.toBinaryString(normalizedCapacity));
        System.out.println(Integer.toBinaryString(normalizedCapacity >>>  1));
        normalizedCapacity |= normalizedCapacity >>>  1;
        System.out.println(Integer.toBinaryString(normalizedCapacity));
        System.out.println(Integer.toBinaryString(normalizedCapacity >>>  2));
        normalizedCapacity |= normalizedCapacity >>>  2;  
        System.out.println(Integer.toBinaryString(normalizedCapacity));
        System.out.println(Integer.toBinaryString(normalizedCapacity >>>  4));
        normalizedCapacity |= normalizedCapacity >>>  4;  
        System.out.println(Integer.toBinaryString(normalizedCapacity));
        System.out.println(Integer.toBinaryString(normalizedCapacity >>>  8));
        normalizedCapacity |= normalizedCapacity >>>  8;  
        System.out.println(Integer.toBinaryString(normalizedCapacity));
        System.out.println(Integer.toBinaryString(normalizedCapacity >>>  16));
        normalizedCapacity |= normalizedCapacity >>> 16;  
        System.out.println(Integer.toBinaryString(normalizedCapacity));
        normalizedCapacity ++;  
        System.out.println(Integer.toBinaryString(normalizedCapacity));
	}
	
	@Test
	public void maxLen(){
		System.out.println(Long.MIN_VALUE);
		double res = (2/4d);
		System.out.println(res);
	}
	
	@Test
	public void testAnd() {
		int normCapacity = 145;
		
		//0x0xFF FF FE 00 => E00 =>1110 0000 0000
		//高23位都是1,低9位都是0,
		//如果参数和其&后值不等于0,表示传入参数的高23位存在1,低9位都被设置为0了,那么参数肯定大于2^9=512
		//如果参数和其&后值等于0,表示传的参数的高23位不存在1,低9位都被设置为0了,那么参数肯定等于0
        if((normCapacity & 0xFFFFFE00) == 0) {
        	System.out.println("normCapacity小于512");
        }else {
        	System.out.println("normCapacity大于512");
        }
        
	}

	
	@Test
	public void printMov(){
		
		MovPrint.pInt(~3);
		MovPrint.pInt(-3);
		long val = 4611686018427389952l;
		MovPrint.pLong(val);
		
		//sr:2048;:100000000000
		MovPrint.pInt(2048);
		//					   sr:2048;:100000000000
		//sr:-2048;:11111111111111111111100000000000
		MovPrint.pInt(-2048);
		//msg:;sr:-2049;len:32:11111111111111111111011111111111
		MovPrint.pInt(~2048);
		//0x4000000000002048
		MovPrint.pLong(Long.parseLong("4000000000002048",16));
		//0x4000000000000000
		MovPrint.pLong(Long.parseLong("4000000000000000",16));
	}
	
	@Test
	public void binaryTest(){
		int id = 2050;
		//异或运算 相同为1，不同为0
		//按位异或运算将两个运算分量的对应位按位遵照以下规则进行计算：
	    //0 ^ 0 = 0, 0 ^ 1 = 1, 1 ^ 0 = 1, 1 ^ 1 = 0
		//id和1进行异或就是修改id的最低位进行加一或减一操作
		int res =  id ^ 1;
		
		System.out.println("2048:"+Integer.toBinaryString(2048)+"###2048^1:"+Integer.toBinaryString(2048^1)+"###:"+(2048^1));
		System.out.println("2049:"+Integer.toBinaryString(2049)+"###2049^1:"+Integer.toBinaryString(2049^1)+"###:"+(2049^1));
		System.out.println("2050:"+Integer.toBinaryString(2050)+"###2050^1:"+Integer.toBinaryString(2050^1)+"###:"+(2050^1));
		System.out.println("2051:"+Integer.toBinaryString(2051)+"###2051^1:"+Integer.toBinaryString(2051^1)+"###:"+(2051^1));
		System.out.println("2052:"+Integer.toBinaryString(2052)+"###2052^1:"+Integer.toBinaryString(2052^1)+"###:"+(2052^1));
		System.out.println("2053:"+Integer.toBinaryString(2053)+"###2053^1:"+Integer.toBinaryString(2053^1)+"###:"+(2053^1));
	}
	
	@Test
	public void integerMaxValTest() {
		/**
		 * Integer.MAX_VALUE 是Integer式4字节，最高位是符号位，故Integer.MAX_VALUE=2^31
		 * 
		 * 01111111111111111111111111111111 = 2^31次方
		 * 10000000000000000000000000000000 = 2^31+1,此时最高位符号位变成1，这个值在4字节下变成负数
		 * (long)(2^3+1)对4字节进行了扩展，最高位成了数据的一部分
		 * 
		 * (int)(((long)(2^31+1))/2)  又还原到原来的Integer.MAX_VALUE，装了一圈又回来了。。。。。
		 * 
		 * 2^31 = 2G
		 * 
		 */
	}

	//toBinaryString()返回的是原码，如果是整数会省略掉高位的0，所以经常显示高位是个1，
	//以为是个负数，toBinaryString的参数是负数的话，显示的是补码，高位大都是1
	public static void main(String[] args) {
		//13个零 8192=8Kb
		System.out.println("8192二进制位："+Long.toBinaryString(8192));
		System.out.println("zeros:"+Integer.numberOfLeadingZeros(8192));
		int size = Integer.SIZE - 1 - Integer.numberOfLeadingZeros(8192);
		//13
		System.out.println("shiftSize:"+size);
		//1000000000000000000000000 24个零，8192左移11位，16777216=16Mb
		System.out.println("smailSubPagePool:"+Long.toBinaryString(16777216));
		int intVal = Integer.MAX_VALUE+1;
		System.out.println("intgerMaxVal+1:"+intVal);
		System.out.println(Long.toBinaryString(intVal));
		System.out.println(Long.toBinaryString(-8192));
		System.out.println(intVal/2);
		System.out.println(Long.toBinaryString(intVal/2));
		System.out.println((int) (((long) Integer.MAX_VALUE + 1) / 2));
		
		int val = (int) (((long) Integer.MAX_VALUE + 1) / 2);
		System.out.println(Long.toBinaryString(val));
		System.out.println(val/1024/1024);
		System.out.println(2049/1024);
		long bitmap = 0x3FFFFFFF;
		System.out.println("0x3FFFFFFF二进制位："+Long.toBinaryString(bitmap));
		System.out.println("0x3FFFFFFF二进制位长度："+Long.toBinaryString(bitmap).length());

	}
}
