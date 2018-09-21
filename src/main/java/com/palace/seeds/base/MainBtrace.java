package com.palace.seeds.base;

import com.palace.seeds.utils.WThread;

public class MainBtrace {

	
	public static void main(String[] args) {
		MainBtrace mb=new MainBtrace();
		for( int i=0;i<6000;i++){
			mb.call(i);
			WThread.sleep(1000);
		}
		
		
		
	}
	
	public  void call(int i){
		System.out.println("curr:"+i);
	}
}
