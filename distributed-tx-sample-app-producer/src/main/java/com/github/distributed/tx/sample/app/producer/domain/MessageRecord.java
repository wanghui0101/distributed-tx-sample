package com.github.distributed.tx.sample.app.producer.domain;

public class MessageRecord {

	public static class ExecuteResult {
		public static final String SUCCESS = "success";
		public static final String FAILURE = "failure";
	}

	private String id;
	private String messageId;
	private String messageContent;
	private String executeResult;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}

	public String getExecuteResult() {
		return executeResult;
	}

	public void setExecuteResult(String executeResult) {
		this.executeResult = executeResult;
	}

}
