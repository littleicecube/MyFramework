package com.palace.seeds.base.collection;

import java.util.HashMap;
import java.util.TreeMap;

import org.junit.Test;

public class MainMap {

	TreeMap<Long,String> treeMap = new TreeMap<Long,String>();
	@Test
	public void treeMap() {
		treeMap.put(111112222l, "111112222l");
		treeMap.put(123l, "123");
		treeMap.put(456l, "456");
		treeMap.put(789l, "789");
		treeMap.put(111l, "111");
		treeMap.put(11l, "11");
		System.out.println(treeMap.get(123l));
		System.out.println(treeMap.higherEntry(123l).getValue());
		System.out.println(treeMap.floorEntry(111112222l).getValue());
		System.out.println(treeMap.floorEntry(111l).getValue());
		System.out.println(treeMap.floorEntry(789l).getValue());
	}
	
	
	HashMap<String,Object> map =new  HashMap<String,Object>();
	@Test
	public void testMap() {
		HashMap<String, Object> simMap = new HashMap<String, Object>();
		simMap.put("name", "xiaoming");
		simMap.put("age", 12);
		simMap.remove("name");
	}
}
