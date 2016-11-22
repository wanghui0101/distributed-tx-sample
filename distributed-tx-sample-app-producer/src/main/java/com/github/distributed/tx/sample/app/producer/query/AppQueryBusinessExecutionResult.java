package com.github.distributed.tx.sample.app.producer.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.distributed.tx.sample.api.core.QueryBusinessExecutionResultApi;
import com.github.distributed.tx.sample.app.producer.dao.MessageRecordDao;
import com.github.distributed.tx.sample.app.producer.domain.MessageRecord;

@Component("app.queryBusinessExecutionResult")
public class AppQueryBusinessExecutionResult implements QueryBusinessExecutionResultApi {

	@Autowired
	private MessageRecordDao messageRecordDao;
	
	@Override
	public boolean query(String messageId) {
		MessageRecord record = messageRecordDao.findByMessageId(messageId);
		return record != null && record.getExecuteResult().equals(MessageRecord.ExecuteResult.SUCCESS);
	}

}
