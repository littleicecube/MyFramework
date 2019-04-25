package com.palace.experience.blog;

import org.junit.Test;

import com.palace.seeds.utils.MovPrint;

public class aa {

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
