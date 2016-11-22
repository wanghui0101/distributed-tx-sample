package com.github.distributed.tx.sample.mq;

public interface MqConsumer<T> {

	 void consume(T message) throws Exception;
}
