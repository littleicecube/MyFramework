package com.palace.seeds.simple;

import org.junit.Test;

public class BinaryTest {

	@Test
	public void test(){
		int r = 123;
		int r1 = 234;
		Integer r2 =1230,rr=1230;
		Integer ii=12,iii=12;
		System.out.println(r2==rr);
		System.out.println(ii==iii);
		Integer r3 = new Integer(12);
		Integer r4 = new Integer(12);
		System.out.println(r==123);
		System.out.println(r1==234);
		System.out.println(r2==1230);
		System.out.println(r3==r4);
	}
	
	public int pageSize = 8*1024*8;
	@Test
	public void testAssert(){
		String binaryStr = Integer.toBinaryString(pageSize);
		//10000000000000000
		System.out.println("8K:"+binaryStr);
	}
	 
}
