package com.github.distributed.tx.sample.commons.message;

import java.sql.Timestamp;

public class DistributedTransactionMessage {

	private String id;
	private String messageId;
	private String app;
	private String appInterfaceForConfirmation;
	private String appInterfaceMethodForConfirmation;
	private String exchange;
	private String routingKey;
	private String body;
	private String status;
	private int maxRetryTimes;
	private int retriedTimes;
	private Timestamp initializedAt;
	private Timestamp confirmedAt;
	private Timestamp ackedAt;
	private Timestamp consumedAt;
	
	public static class Status {
		public static final String INITIALIZED = "initialized"; // 初始化
		public static final String CONFIRMED = "confirmed"; // 已确认
		public static final String ACKED = "acked"; // 已签收
		public static final String CONSUMED = "consumed"; // 已消费
		public static final String DEAD = "dead"; // 死信
	}
	
	public DistributedTransactionMessage() {
	}
	
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

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getAppInterfaceForConfirmation() {
		return appInterfaceForConfirmation;
	}

	public void setAppInterfaceForConfirmation(String appInterfaceForConfirmation) {
		this.appInterfaceForConfirmation = appInterfaceForConfirmation;
	}

	public String getAppInterfaceMethodForConfirmation() {
		return appInterfaceMethodForConfirmation;
	}

	public void setAppInterfaceMethodForConfirmation(
			String appInterfaceMethodForConfirmation) {
		this.appInterfaceMethodForConfirmation = appInterfaceMethodForConfirmation;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public int getMaxRetryTimes() {
		return maxRetryTimes;
	}

	public void setMaxRetryTimes(int maxRetryTimes) {
		this.maxRetryTimes = maxRetryTimes;
	}

	public int getRetriedTimes() {
		return retriedTimes;
	}

	public void setRetriedTimes(int retriedTimes) {
		this.retriedTimes = retriedTimes;
	}

	public Timestamp getInitializedAt() {
		return initializedAt;
	}

	public void setInitializedAt(Timestamp initializedAt) {
		this.initializedAt = initializedAt;
	}

	public Timestamp getConfirmedAt() {
		return confirmedAt;
	}

	public void setConfirmedAt(Timestamp confirmedAt) {
		this.confirmedAt = confirmedAt;
	}

	public Timestamp getAckedAt() {
		return ackedAt;
	}

	public void setAckedAt(Timestamp ackedAt) {
		this.ackedAt = ackedAt;
	}

	public Timestamp getConsumedAt() {
		return consumedAt;
	}

	public void setConsumedAt(Timestamp consumedAt) {
		this.consumedAt = consumedAt;
	}
	
}
