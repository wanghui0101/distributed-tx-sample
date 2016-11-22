package com.github.distributed.tx.sample.mq.distributed;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DistributedMessage<T extends Serializable> implements Serializable {

	private String messageId;
	private T content;
	
	public DistributedMessage(String messageId, T content) {
		this.messageId = messageId;
		this.content = content;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}
	
	@Override
	public String toString() {
		return "DistributedMessage[messageId=" + messageId + ", content=" + content + "]";
	}
	
}
