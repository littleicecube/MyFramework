package com.jdk.newFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.Maps;


public class CompletableFutureSim {

	/**
	 * 并发执行等待结果
	 * @throws Exception
	 */
	@Test
	public void func() throws Exception {
		  
		CompletableFuture<List<Map<String, Object>>> f1 = CompletableFuture.supplyAsync(()->{
			System.out.println("first mapList");
			try {
				Thread.currentThread().sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			Map map =Maps.newHashMap();
			map.put("key1", "value1");
			mapList.add(map);
			return mapList;
		});
		
		CompletableFuture<List<Map<String, Object>>> f2 = CompletableFuture.supplyAsync(()->{
			System.out.println("first mapList");
			try {
				Thread.currentThread().sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
			Map map =Maps.newHashMap();
			map.put("key2", "value2");
			mapList.add(map);
			return mapList;
		});
		Stream.of(f1,f2)
		.map(CompletableFuture::join);
		
		
	}
}