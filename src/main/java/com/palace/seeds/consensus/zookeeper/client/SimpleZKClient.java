package com.palace.seeds.consensus.zookeeper.client;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

import com.palace.seeds.utils.WThread;

public class SimpleZKClient {

	
	/**
	 * zk.getChild("/path",new watcher())是通过tcp和sever进行交互，来获取目录结构，如果相应正常则
	 * 获取指定目录的子节点成功，watcher也添加到节点上，如果抛出异常，节点没有添加成功，watcher没有添加成功，那么
	 * 在编程的时候就需要重试，直到收到明确的响应结果为准，且一个watcher只能被调用一次，如果watcher被调用过则会
	 * 从server的linstener中删除，以后的事件不会被通知，如果需要接受后续通知就需要添加新的watcher
	 * 
	 */
	String  strNodes="192.168.0.121:2181";
	int sessionTimeout = 30000;
	@Test
	public void zkTest() throws Exception {
		ZooKeeper zk = new ZooKeeper(strNodes, sessionTimeout,new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				if(event != null) {
					System.out.println("###zk all event:"+event.toString());
				}
			}
		});
		zk.exists("/simpleRoot",true, new StatCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				if(stat !=null) {
					System.out.println("###stat:"+stat.getCzxid());
				}
			}
		}, null);
		
		Thread.currentThread().sleep(1000000);
	}
	@Test
	public void existNodeTest() throws Exception {
		ZooKeeper zk =new ZooKeeper(strNodes,sessionTimeout,null);
		zk.exists("/simpleRoot",true, new StatCallback() {
			@Override
			public void processResult(int rc, String path, Object ctx, Stat stat) {
				System.out.println("###exitTest:"+path);
				
			}
		},null);
	}
	@Test
	public void createNodeTest() throws Exception {
		ZooKeeper zk = new ZooKeeper(strNodes, sessionTimeout,new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				if(event !=null) {
					System.out.println("###CreateNode:"+event);
					 
				}
				
			}
		});
		
		zk.delete("/simpleRoot8", -1);
		//zk.create("/simpleRoot11", "simpleRootData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		//WThread.sleep(2000000);
		
	}
	
	 
	
	@Test
	public void createSecodeNodeTest() throws IOException, Exception, InterruptedException {
		ZooKeeper zk = new ZooKeeper(strNodes,sessionTimeout,getZKCreateWatcher());
		zk.create("/simpleRoot/secodeDir3", "{dirLevl:\"2\"}".getBytes(),ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}
	
	@Test
	public void getNodeTest() throws Exception {
		//The watcher argument specifies the watcher that will be notified of any changes in state. 
		//This notification can come at any point before or after the constructor call has returned. 
		//创建一个session和zookeeper server,但是session不一定建立成功，watcher指定一个监听器，这个监听器将接收所有的通知
		//这些消息可能在任何时候被通知，在ZooKeeper构造函数在被创建之前或之后
		final ZooKeeper zk = new ZooKeeper(strNodes, sessionTimeout,new Watcher() {
			
			@Override
			public void process(WatchedEvent event) {
				System.out.println("###zookeeperLog:"+event);
				
			}
		});
		//The watch willbe triggered by a successful operation that deletes
		//the node of the given path or creates/delete a child under the node

		List<String> nodeList = zk.getChildren("/simpleRoot", new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				if(event != null) {
					addWatch(zk);
					System.out.println("####GetNodeLog:"+event.getPath());
				}
			}
		});
		
		for(String str : nodeList) {
			System.out.println(str);
		}
		WThread.sleep(40000000);
	}
	
	public static void  addWatch(final ZooKeeper zk) {
		try {
			zk.getChildren("/simpleRoot", new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					addWatch(zk);
					System.out.println("####GetNodeLogNew:"+event.getPath());
				}
			});
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Watcher getZKCreateWatcher() {
		return new Watcher() {
			@Override
			public void process(WatchedEvent event) {
				if(event != null) {
					System.out.println("###"+Thread.currentThread().getName()+"zkStart:"+event.getPath());
				}
				
			}
		};
	}
	
}
