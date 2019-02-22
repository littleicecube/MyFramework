package com.palace.seeds.mq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.junit.Test;

import com.palace.seeds.utils.WThread;

public class RocketMQMain {

	String nameServer = "192.168.0.14:9876";
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void producer() {
		try {
			DefaultMQProducer producer = new DefaultMQProducer("groupName_one");
			producer.setNamesrvAddr(nameServer);
			producer.start();
			for(int i =0 ;i< 100;i++) {
				String key = System.currentTimeMillis()+"i";
				Message msg = new Message("topic_one", "tag"+key,(key+"msgVal").getBytes());
				//new Message(topic, tags, keys, flag, body, waitStoreMsgOK)
				SendResult result = producer.send(msg);
				SendStatus status = result.getSendStatus();
				//System.out.println(status);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		//WThread.sleep(600000);
	}
	
	@Test
	public void consumer() {
		try {
			DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("groupName_one");
			consumer.setNamesrvAddr(nameServer);
			consumer.subscribe("topic_one", "*");
			consumer.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
					System.out.println("##############");
					for(MessageExt ext : msgs) {
						String msg = new String(ext.getBody());
						System.out.println(msg+"###"+ext.toString());
						if(msg.substring(0,1).equals("3")) {
							return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
						}
						return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
					}
					System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					return null;
				}
			});
			consumer.start();
		}catch(Exception e) {
			e.printStackTrace();
		}
	
		WThread.sleep(120000);
	}
}
