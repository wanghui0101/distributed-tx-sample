package com.github.distributed.tx.sample.recovery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.github.distributed.tx.sample.commons.message.DistributedTransactionMessage;
import com.github.distributed.tx.sample.commons.message.DistributedTransactionMessageDao;
import com.github.distributed.tx.sample.mq.MqProducer;
import com.github.distributed.tx.sample.mq.distributed.DistributedMessage;

@Component
public class ReliableMessageRecovery {

	@Autowired
	private DistributedTransactionMessageDao distributedTransactionMessageDao;
	
	@Autowired
	private MqProducer<DistributedMessage<String>> mqProducer;
	
	@Scheduled(fixedRate = 10 * 1000) // 10s执行一次
	public void recover() {
		recoverConfirmedMessages(distributedTransactionMessageDao.queryConfirmedMessages());
		recoverAckedMessages(distributedTransactionMessageDao.queryAckedMessages());
	}

	private void recoverConfirmedMessages(List<DistributedTransactionMessage> confirmedMessages) {
		for (DistributedTransactionMessage msg : confirmedMessages) {
			if (msg.getConfirmedAt().getTime() - System.currentTimeMillis() > 60 * 1000) { // 60s后才处理
				redeliverMessageIfNecessary(msg);
			}
		}
	}
	
	private void recoverAckedMessages(List<DistributedTransactionMessage> ackedMessages) {
		for (DistributedTransactionMessage msg : ackedMessages) {
			if (msg.getAckedAt().getTime() - System.currentTimeMillis() > 60 * 1000) { // 60s后才处理
				redeliverMessageIfNecessary(msg);
			}
		}
	}
	
	private void redeliverMessageIfNecessary(DistributedTransactionMessage msg) {
		if (checkMustRedeliver(msg)) {
			String exchange = msg.getExchange();
			String routingKey = msg.getRoutingKey();
			String body = msg.getBody();
			doDeliver(exchange, routingKey, new DistributedMessage<String>(msg.getMessageId(), body));
		}
	}

	private boolean checkMustRedeliver(DistributedTransactionMessage msg) {
		int maxRetryTimes = msg.getMaxRetryTimes();
		int retriedTimes = msg.getRetriedTimes();
		boolean mustRedeliver = retriedTimes < maxRetryTimes;
		if (mustRedeliver) {
			retriedTimesPlusOne(msg);
		} else {
			deadMessage(msg);
		}
		return mustRedeliver;
	}

	private void retriedTimesPlusOne(DistributedTransactionMessage msg) {
		msg.setRetriedTimes(msg.getRetriedTimes() + 1);
		distributedTransactionMessageDao.save(msg);
	}
	
	private void deadMessage(DistributedTransactionMessage msg) {
		msg.setStatus(DistributedTransactionMessage.Status.DEAD);
		distributedTransactionMessageDao.save(msg);
	}

	private void doDeliver(String exchange, String routingKey, DistributedMessage<String> distributedMessage) {
		mqProducer.send(exchange, routingKey, distributedMessage);
	}
}
