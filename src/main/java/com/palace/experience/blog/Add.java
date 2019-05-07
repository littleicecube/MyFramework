package com.palace.experience.blog;

import org.junit.Test;

import com.palace.seeds.utils.MovPrint;

public class Add {
	
/**
 
1K = 2^10 = 1024
1M = 2^20 = 2^10 * 2^10 = 1024 * 1024
1G = 2^30 = 2^10 * 2^10 * 2^10 = 2014 * 1024 * 1024
4G = 2^32 = 2^10 * 2^10 * 2^10 * 2^2 = 1024 * 1024 * 1024 * 4 
 一根地址线寻址一个地址,地址有8bit称为一个字节 1B
32根地址线有2^32种可能,每一种可能寻址一个字节即1B,那么32根地址线共可以寻址2^32 * 1B = 4GB
	  
	  
32位程序下的线性地址到物理地址的翻译过程:
 存在一个线性地址如:
 
+---------------------------+---------------------------+-----------------------------------+
|			高10位			|		中间10位				|			低12位					|
+---------------------------+---------------------------+-----------------------------------+
	 
		 +----------+
		 |			|	
		 +----------+				+-----------+				
		 |			|				|			|	
		 +----------+				+-----------+
		 |			|				|			|
		 +----------+				+-----------+
	 	 |			|				|			|
	 	 +----------+				+-----------+
	 	
	 	
									 	 +----------+
										 |			|	
										 +----------+
										 |			|	
										 +----------+
										 |			|	
										 +----------+
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	 	
	
 * 
 */

	@Test
	public void move() {
		//
		long addr = 0x0fffe333;
		long tmp = (addr>>12)<<2;
		System.out.println(tmp&0xffc);
		
		long oth = (addr>>10)&0xffc;
		System.out.println(oth);
		System.out.println(Integer.parseInt("ffc", 16));
		//msg:;sr:4092;len:12:111111111100
		MovPrint.pInt(Integer.parseInt("ffc", 16));
	}
	
/*	
 * ((unsigned long*)
			(((address>>10)&0xffc) + (oxfffff000 & *((unsigned long *) ((address>>20) & oxffc)))))
			

(
	(unsigned long*)(
		(
			(address>>10) & 0xffc
		) + (
				oxfffff000 & *(		(unsigned long *) ((address>>20) & oxffc)	)
			)
	)

)
			
			
			*
			*/

}
