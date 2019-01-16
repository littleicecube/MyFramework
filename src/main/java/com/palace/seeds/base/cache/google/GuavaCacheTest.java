package com.palace.seeds.base.cache.google;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.palace.seeds.model.User;
import com.palace.seeds.utils.WThread;

public class GuavaCacheTest {

	/**
	 * 内容缓存的时间只有n秒，操作n秒后，不论什么情况，缓存的数据都会被清空，重新去加载
	 */
	/**
	 * 设置缓存的过期时间为5秒钟，过期类型为写入，其意义为：
	 * 		如果当前访问时间距离内容被创建的时间大于5秒，那么缓存的内容将失效，去重新加载内容
	 * @throws Exception
	 */
	@Test
	public void cacheWriteTest() throws Exception {
		LoadingCache<String, User> cache = CacheBuilder.newBuilder()
			.concurrencyLevel(8)
			.expireAfterWrite(5, TimeUnit.SECONDS)
			.initialCapacity(100)
			.removalListener(new RemovalListener<String, User>() {
				@Override
				public void onRemoval(RemovalNotification<String, User> notification) {
					
				}
			}).build(new CacheLoader<String,User>() {
				@Override
				public User load(String key) throws Exception {
					if("xiaoming".equals(key)) {
						return new User("xiaomingc",12,"bj");
					}
					if("xiaozhang".equals(key)) {
						return new User("xiaozhang", 15,"tj");
					}
					return null;
				}
			});
		
		User  u = cache.get("xiaoming");
		System.out.println(u.toString());
		while(true) {
			WThread.sleep(3000l);
			System.out.println(cache.get("xiaoming"));
		}
		
	}
	
	/**
	 * 设置缓存的过期时间为5秒钟，过期类型为访问命中，其意义为：
	 * 		连续访问的时间间隔在5内，那么缓存就一直不会失效，如果当前访问时间距离上一次访问时间的
	 * 		时间间隔超过5秒，那么缓存就会失效
	 * @throws Exception
	 */
	@Test
	public void cacheAccTest() throws Exception {
		LoadingCache<String, User> cache = CacheBuilder.newBuilder()
			.concurrencyLevel(8)
			.expireAfterAccess(5, TimeUnit.SECONDS)
			.initialCapacity(100)
			.removalListener(new RemovalListener<String, User>() {
				@Override
				public void onRemoval(RemovalNotification<String, User> notification) {
					
				}
			}).build(new CacheLoader<String,User>() {
				@Override
				public User load(String key) throws Exception {
					if("xiaoming".equals(key)) {
						return new User("xiaomingc",12,"bj");
					}
					if("xiaozhang".equals(key)) {
						return new User("xiaozhang", 15,"tj");
					}
					return null;
				}
			});
		
		User  u = cache.get("xiaoming");
		System.out.println(u.toString());
		while(true) {
			WThread.sleep(3000l);
			System.out.println(cache.get("xiaoming"));
		}
		
	}
}
