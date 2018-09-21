package com.palace.seeds.base.vola;

import java.lang.invoke.VolatileCallSite;

public class MainVola {

	volatile String name="xiaoming";
	public void print(){
		String interName=name;
		System.out.println(interName);
	}
	public static void main(String[] args) {
		
	}
}
