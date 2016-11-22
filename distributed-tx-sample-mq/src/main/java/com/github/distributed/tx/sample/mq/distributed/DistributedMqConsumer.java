package com.github.distributed.tx.sample.mq.distributed;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.distributed.tx.sample.api.core.ReliableMessageApi;
import com.github.distributed.tx.sample.mq.MqConsumer;

public abstract class DistributedMqConsumer implements MqConsumer<DistributedMessage<String>> {

	private static final Logger logger = LoggerFactory.getLogger(DistributedMqConsumer.class);
	
	@Autowired
	private ReliableMessageApi reliableMessageApi;
	
	public final void handle(DistributedMessage<String> message) {
		try {
			acknowledgeMessage(message.getMessageId());
			if (checkIdempotence(message)) {
				consume(message);
			}
			processAfterConsumption(message.getMessageId());
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
		}
	}
	
	protected void acknowledgeMessage(String messageId) {
		reliableMessageApi.acknowledge(messageId);
	}
	
	protected void processAfterConsumption(String messageId) {
		reliableMessageApi.finish(messageId);
	}
	
	/**
	 * 检查消息幂等性
	 * 返回true，表示需要进行消费; 返回false，表示已消费过，不需要再重复消费
	 * @param message
	 * @return
	 */
	protected abstract boolean checkIdempotence(DistributedMessage<String> message);
}
