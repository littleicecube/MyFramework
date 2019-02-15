package com.palace.seeds.base.jvm.reentrantLock;

import java.util.LinkedList;
import java.util.Queue;

public class ProducerAndConsumer {
	
	int maxSize = 300;
	Queue queue = new LinkedList<>();
	
	public void add(Object obj) throws Exception {
		synchronized (queue) {
			if(queue.size() == maxSize) {
				queue.wait();
			}else {
				queue.add(obj);
			}
		}
	}
	
	public void take() throws Exception {
		synchronized (queue) {
			if(queue.isEmpty()) {
				queue.wait();
			}else {
				Object obj = queue.remove();
				//do something
			}
		}
	}
}
