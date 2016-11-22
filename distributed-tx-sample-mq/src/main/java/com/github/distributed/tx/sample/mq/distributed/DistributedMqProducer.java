package com.github.distributed.tx.sample.mq.distributed;

import java.io.Serializable;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.distributed.tx.sample.mq.MqProducer;

public class DistributedMqProducer<T extends Serializable> implements MqProducer<DistributedMessage<T>> {

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Override
	public void send(String exchange, String routingKey, DistributedMessage<T> message) {
		amqpTemplate.convertAndSend(exchange, routingKey, message);
	}

}
