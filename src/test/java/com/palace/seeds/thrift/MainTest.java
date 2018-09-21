package com.palace.seeds.thrift;


public class MainTest {

	public static void main(String[] args) {
		new MainTest().call();
	}
	
	public void call(){
		String name=new String("name is");
		this.alter(name);
		System.out.println(name);
		
		
		StringBuilder sb=new StringBuilder("asdfasdfa");
		cha(sb);
		System.out.println(sb);
	}
	
	
	public void cha(StringBuilder sb){
		sb=new StringBuilder("qwerty");
	}
	public void alter(String str){
		
		str=new String(str+"aaaa");
	}
	
	
	
}
