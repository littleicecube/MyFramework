package com.palace.experience.RemoteCall;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Before;
import org.junit.Test;

public class RemoteCallService {

	public static String group = "groupA";
	public static String topic = "consumerTopicA";
	public static String nameServer = "192.168.0.14:9876";
	
	static DefaultMQProducer pro = new DefaultMQProducer(group);
	
	@Before
	public void before() {
		pro.setNamesrvAddr(nameServer);
		try {
			pro.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}
	public static boolean nextNode(String tag,Object obj) {
		try {
			return SendStatus.SEND_OK.equals(pro.send(new Message(topic, tag,obj.toString().getBytes())));
		} catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Test
	public void theConsumer() {
		DefaultMQPushConsumer con = new DefaultMQPushConsumer("");
		con.setNamesrvAddr(nameServer);
		con.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		con.setConsumeMessageBatchMaxSize(10);
		try {
			con.subscribe("consumerTopicA", "*");
			con.registerMessageListener(new MessageListenerConcurrently() {
				@Override
				public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
					for(MessageExt ext : msgs) {
						try {
							Map<String,Object> result = doConsume(ext);
							if(result != null && MapUtils.getInteger(result,"status") == 0) {
								//添加下一个节点的数据
								if(nextNode("tagB",result)) {
									return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
								}
							}
							//处理业务失败,需要重复处理
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}catch(Exception e) {
							e.printStackTrace();
							return ConsumeConcurrentlyStatus.RECONSUME_LATER;
						}
					}
					return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
				}

			});
		} catch (MQClientException e) {
			e.printStackTrace();
		}
	}
	//处理业务,调用远程方法
	private Map<String,Object> doConsume(MessageExt ext) {
		// TODO Auto-generated method stub
		return null;
	}
}
