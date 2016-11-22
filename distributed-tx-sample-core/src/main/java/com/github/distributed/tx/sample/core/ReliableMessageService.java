package com.github.distributed.tx.sample.core;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.distributed.tx.sample.api.core.ReliableMessage;
import com.github.distributed.tx.sample.api.core.ReliableMessageApi;
import com.github.distributed.tx.sample.commons.message.DistributedTransactionMessage;
import com.github.distributed.tx.sample.commons.message.DistributedTransactionMessageDao;
import com.github.distributed.tx.sample.mq.MqProducer;
import com.github.distributed.tx.sample.mq.distributed.DistributedMessage;

@Service("reliableMessageApi")
public class ReliableMessageService implements ReliableMessageApi {
	
	@Autowired
	private DistributedTransactionMessageDao distributedTransactionMessageDao;
	
	@Autowired
	private MqProducer<DistributedMessage<String>> mqProducer;

	@Transactional
	@Override
	public boolean initialize(ReliableMessage reliableMessage) {
		DistributedTransactionMessage dtm = buildMessage(reliableMessage);
		saveDistributedTransactionMessage(dtm);
		return true;
	}
	
	private DistributedTransactionMessage buildMessage(ReliableMessage reliableMessage) {
		DistributedTransactionMessage dtm = new DistributedTransactionMessage();
		dtm.setMessageId(reliableMessage.getId());
		dtm.setApp(reliableMessage.getApp());
		dtm.setAppInterfaceForConfirmation(reliableMessage.getAppInterfaceForConfirmation());
		dtm.setAppInterfaceMethodForConfirmation(reliableMessage.getAppInterfaceMethodForConfirmation());
		dtm.setExchange(reliableMessage.getExchange());
		dtm.setRoutingKey(reliableMessage.getRoutingKey());
		dtm.setBody(reliableMessage.getBody());
		dtm.setStatus(DistributedTransactionMessage.Status.INITIALIZED);
		dtm.setMaxRetryTimes(reliableMessage.getRetryTimesIfConsumedFail());
		dtm.setRetriedTimes(0);
		dtm.setInitializedAt(new Timestamp(new Date().getTime()));
		return dtm;
	}
	
	private void saveDistributedTransactionMessage(DistributedTransactionMessage dtm) {
		distributedTransactionMessageDao.save(dtm);
	}

	@Transactional
	@Override
	public boolean confirm(String messageId) {
		DistributedTransactionMessage dtm = getDistributedTransactionMessage(messageId);
		confirmDistributedTransactionMessage(dtm);
		deliverDistributedTransactionMessage(dtm);
		return true;
	}

	private DistributedTransactionMessage getDistributedTransactionMessage(String messageId) {
		return distributedTransactionMessageDao.findByMessageId(messageId);
	}
	
	private void confirmDistributedTransactionMessage(DistributedTransactionMessage dtm) {
		dtm.setStatus(DistributedTransactionMessage.Status.CONFIRMED);
		dtm.setConfirmedAt(new Timestamp(new Date().getTime()));
		saveDistributedTransactionMessage(dtm);
	}
	
	private void deliverDistributedTransactionMessage(DistributedTransactionMessage dtm) {
		String exchange = dtm.getExchange();
		String routingKey = dtm.getRoutingKey();
		String body = dtm.getBody();
		doDeliver(exchange, routingKey, new DistributedMessage<String>(dtm.getMessageId(), body));
	}

	private void doDeliver(String exchange, String routingKey, DistributedMessage<String> distributedMessage) {
		mqProducer.send(exchange, routingKey, distributedMessage);
	}

	@Override
	public boolean acknowledge(String messageId) {
		DistributedTransactionMessage dtm = getDistributedTransactionMessage(messageId);
		dtm.setStatus(DistributedTransactionMessage.Status.ACKED);
		dtm.setAckedAt(new Timestamp(new Date().getTime()));
		saveDistributedTransactionMessage(dtm);
		return true;
	}

	@Override
	public boolean finish(String messageId) {
		DistributedTransactionMessage dtm = getDistributedTransactionMessage(messageId);
		dtm.setStatus(DistributedTransactionMessage.Status.CONSUMED);
		dtm.setConsumedAt(new Timestamp(new Date().getTime()));
		saveDistributedTransactionMessage(dtm);
		return true;
	}

}
