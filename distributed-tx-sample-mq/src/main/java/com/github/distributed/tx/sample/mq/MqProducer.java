package com.github.distributed.tx.sample.mq;

public interface MqProducer<T> {

	void send(String exchange, String routingKey, T message);
	
}
