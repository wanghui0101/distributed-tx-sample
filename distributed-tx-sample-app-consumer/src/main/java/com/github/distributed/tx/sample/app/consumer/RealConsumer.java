package com.github.distributed.tx.sample.app.consumer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.github.distributed.tx.sample.mq.distributed.DistributedMessage;
import com.github.distributed.tx.sample.mq.distributed.DistributedMqConsumer;

@Component("realConsumer")
public class RealConsumer extends DistributedMqConsumer {
	
	private Map<String, String> consumedMessages = new ConcurrentHashMap<String, String>();
	
	@Override
	protected boolean checkIdempotence(DistributedMessage<String> message) {
		String messageId = message.getMessageId();
		String messageContent = message.getContent();
		boolean toContinue = !consumedMessages.containsKey(messageId);
		if (toContinue) {
			consumedMessages.put(messageId, messageContent);
		}
		return toContinue;
	}

	@Override
	public void consume(DistributedMessage<String> message) throws Exception {
		System.out.println("真不容易，终于消费了: " + message);
	}

}
