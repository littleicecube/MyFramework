package com.palace.seeds.base.jvm.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class MainTest {
	
	final static ConcurrentHashMap<String,Object> map =new ConcurrentHashMap<String,Object>();
	
	
	public static void concurrentHashMapThread2(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<100;i++){
					//map.put("key"+i,new Object());
					map.put("same", "obj");
				}
			}
		},"map-thread-2").start();
	}
	public static void concurrentHashMapThread1(){
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<100;i++){
					//map.put("key"+i,new Object());
					map.put("same", "obj");
				}
			}
		},"map-thread-1").start();
	}
	public static void main(String[] args) {
		concurrentHashMapThread1();
		concurrentHashMapThread2();
	}
	
	
	@Test
	public void concurrentHashMapTest(){
		final ConcurrentHashMap<String,Object> map =new ConcurrentHashMap<String,Object>();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<100;i++){
					map.put("key"+i,new Object());
				}
			}
		},"map-thread-1").start();
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i<100;i++){
					map.put("key"+i,new Object());
				}
			}
		},"map-thread-2").start();
	}
	
	@Test
	public void testArrayList(){
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<15;i++){
			if(i==9){
				list.add("name"+i);
			}else{
				list.add("name"+i);
			}
		}
		
		Iterator<String> ite = list.iterator();
		while(ite.hasNext()){
			String name = ite.next();
			if(name.equals("name4"))
			ite.remove();
			System.out.println(name);
		}
		list.remove(1);
	}

	
	@Test
	public void LinkedListTest(){
		LinkedList<String> list=new LinkedList<String>();
		list.add("name");
		
	}
	
}
