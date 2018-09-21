package com.palace.seeds.dubbox.provider;

import com.palace.seeds.dubbox.api.IWorld;
public class WorldImp implements IWorld{

	public String sayHello(String name){
		System.out.println(name+" say world ");
		return name;
	}
	
 
}
