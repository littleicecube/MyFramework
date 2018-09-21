package com.palace.seeds.simple;

import java.io.File;

import org.junit.Test;

import com.palace.seeds.utils.MovPrint;

public class MainTest {

	@Test
	public void doubleTest(){
		double d = 1234578d;
		System.out.println((d/1000)+"");
	}
	
	@Test
	public void hexTest(){
		int memoryMapIdx=2048;
		int bitmapIdx=0;
		//0x40 00 00 00 00 00 00 00
		long pRes = Long.parseLong("4000000000000000",16);
		//sr:4611686018427387904;4611686018427387904:1000000 00000000 0000000000000000 0000000000000000 0000000000000000
		MovPrint.pLong(pRes+"",pRes);
	    long res = 0x4000000000000000L | (long) bitmapIdx << 32 | memoryMapIdx;
	}
	
	
	@Test
	public void testAssert(){
		assert 1==2 ;
		System.out.println("1==1");
	}
	/**
	 * -536870912
	 * -536870911
	 * private static int runStateOf(int c)     { return c & ~CAPACITY; }
	 * 
	 */
	int COUNT_BITS = 29;
	@Test
	public void testLeftMove(){
		int RUNNING    = -1 << COUNT_BITS;
		System.out.println("running:		"+Integer.toBinaryString(RUNNING));
		int SHUTDOWN   =  0 << COUNT_BITS;
		System.out.println("shutdown:		"+Integer.toBinaryString(SHUTDOWN));
	    int STOP       =  1 << COUNT_BITS;
	    System.out.println("stop:			"+Integer.toBinaryString(STOP));
	    int TIDYING    =  2 << COUNT_BITS;
	    System.out.println("yidying:		"+Integer.toBinaryString(TIDYING));
	    int TERMINATED =  3 << COUNT_BITS;
	    System.out.println("terminated:		"+Integer.toBinaryString(TERMINATED));
	    //左移29位，加上1共30位，然后减1，剩下29位且都是1
	    int CAPACITY   = (1 << COUNT_BITS) - 1;
	    System.out.println("CAPACITY:"+CAPACITY+"#####~CAPACITY:"+~CAPACITY);
	    System.out.println("CAPACITY:		"+Integer.toBinaryString(CAPACITY));
	    System.out.println("~CAPACITY:		"+Integer.toBinaryString(~CAPACITY));
	    System.out.println("===========================");
	    System.out.println("RUNNING:"+RUNNING+";"+Integer.toBinaryString(RUNNING));
	    System.out.println("RUNNING&~CAPACITY:"+(RUNNING&~CAPACITY));
	    System.out.println("RUNNING&~CAPACITY_BINARY:"+Integer.toBinaryString(RUNNING&~CAPACITY));
		System.out.println("RUNNING+1:"+(RUNNING+1)+";"+Integer.toBinaryString(RUNNING+1));
		System.out.println("RUNNING+1~CAPACITY:"+((RUNNING+1)&~CAPACITY));
		System.out.println("RUNNING+1~CAPACITY_BINARY:"+Integer.toBinaryString((RUNNING+1)&~CAPACITY));
	    System.out.println("***************************");
	    
	    System.out.println(RUNNING&~CAPACITY);
	    
	    System.out.println("============================");
		int i = -1;
		System.out.println(Integer.toBinaryString(i));
		String iv = Integer.toBinaryString(i<<29);
		System.out.println(iv);
	}
	
	
	@Test
	public void commonTest(){
		  for (int i = 512; i > 0; i <<= 1) {
			  System.out.println(Long.toBinaryString(i));
			  System.out.println(i);
			  System.out.println("");
	        }
	}
	@Test
	public void codeTest(){
		 //p = (p != t && t != (t = tail)) ? t : q;
		 //p = (t != (t = tail)) ? t : head;
		
		Object sr = new Object();
		Object ds = new Object(); 
		System.out.println(sr==ds);
	
		System.out.println(sr != (sr));
		
		Object res=null;
		System.out.println((sr == (sr=ds))?true:false);
		System.out.println(sr);
		
		/*String name="name";
		String name11="";
		System.out.println(name==name11);
		
		
		String tt="name11";
		String key="name11";
		key="naaaaa";
		int aa=12;
		int dd=123;
		System.out.println(dd);
		String  r = ((aa=192)!=aa)?"true":"false";
		System.out.println(aa!=aa);
		System.out.println(r);
		System.out.println(dd);
		
		System.out.println(tt.equals((name=key)));
		String res = (name!=(name="name"))?"no":"yes";
		res = (name!=(name="name"))?"no":"yes";
		System.out.println(res);*/
		
	}
	
	
	public static void main(String[] args) {
		
		int r= 65536>>>16;
		System.out.println(Integer.toBinaryString(65536));
		System.out.println(Integer.toBinaryString(65535));
		System.out.println(Integer.toBinaryString(131072));
		System.out.println(131072&65535);
		System.out.println(Integer.toBinaryString(8));
		System.out.println(r);
		System.out.println(0&65535);
		
		//10000000000000000
	}
	
	
	@Test
	public void testFile(){
		File file = new File("e:/wzj/file/test");
		System.out.println(file.getAbsolutePath());
		
	}
}
