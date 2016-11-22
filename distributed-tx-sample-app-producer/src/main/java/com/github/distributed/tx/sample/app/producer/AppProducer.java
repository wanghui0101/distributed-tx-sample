package com.github.distributed.tx.sample.app.producer;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.distributed.tx.sample.api.core.ReliableMessage;
import com.github.distributed.tx.sample.api.core.ReliableMessageApi;
import com.github.distributed.tx.sample.app.producer.dao.MessageRecordDao;
import com.github.distributed.tx.sample.app.producer.dao.UserDao;
import com.github.distributed.tx.sample.app.producer.domain.MessageRecord;
import com.github.distributed.tx.sample.app.producer.domain.User;
import com.github.distributed.tx.sample.commons.json.JsonMapper;

@Service("appProducer")
public class AppProducer {
	
	@Autowired
	private ReliableMessageApi reliableMessageApi;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private MessageRecordDao messageRecordDao;

	@Transactional
	public void sample() {
		ReliableMessage reliableMessage = buildReliableMessage();
		initializeReliableMessage(reliableMessage);
		doSample(reliableMessage);
		recordLocalExecutionResult(reliableMessage);
		confirmReliableMessage(reliableMessage);
	}
	
	private void doSample(ReliableMessage reliableMessage) {
		User user = new User();
		user.setName("wanghui");
		user.setCreateAt(new Timestamp(new Date().getTime()));
		userDao.save(user);
	}
	
	private ReliableMessage buildReliableMessage() {
		ReliableMessage msg = new ReliableMessage();
		msg.setExchange("dt.exchange");
		msg.setRoutingKey("dt.exchange.queue");
		msg.setBody("123");
		msg.setAppInterfaceForConfirmation("app.queryBusinessExecutionResult");
		msg.setAppInterfaceMethodForConfirmation("query");
		return msg;
	}

	private void initializeReliableMessage(ReliableMessage reliableMessage) {
		reliableMessageApi.initialize(reliableMessage);
	}
	
	private void recordLocalExecutionResult(ReliableMessage reliableMessage) {
		MessageRecord messageRecord = new MessageRecord();
		messageRecord.setMessageId(reliableMessage.getId());
		messageRecord.setMessageContent(new JsonMapper().toJson(reliableMessage));
		messageRecord.setExecuteResult(MessageRecord.ExecuteResult.SUCCESS);
		messageRecordDao.save(messageRecord);
	}
	
	private void confirmReliableMessage(ReliableMessage reliableMessage) {
		reliableMessageApi.confirm(reliableMessage.getId());
	}
	
}
