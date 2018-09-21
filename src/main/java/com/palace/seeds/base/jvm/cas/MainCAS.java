package com.palace.seeds.base.jvm.cas;

import java.lang.reflect.Field;

import sun.misc.Unsafe;


public class MainCAS {

	public String name="xiaoming";
	public long age=100;
	
	public void run() throws Exception, SecurityException{
		Unsafe unsafe=null;
    	Class<?> clazz = Unsafe.class; 
        Field f = clazz.getDeclaredField("theUnsafe"); 
        f.setAccessible(true); 
        unsafe = (Unsafe) f.get(clazz); 
        
		Class claaa = MainCAS.class;
        long parkBlockerOffset = unsafe.objectFieldOffset(claaa.getDeclaredField("age"));
        
        if(unsafe.compareAndSwapLong(this, parkBlockerOffset, 300, 200)){
        	System.out.println(this.age);
        }
        System.out.println(parkBlockerOffset);
	}
	
	public static void main(String[] args) throws Exception{
		MainCAS mainCas = new MainCAS();
		mainCas.run();
	}
	
}
