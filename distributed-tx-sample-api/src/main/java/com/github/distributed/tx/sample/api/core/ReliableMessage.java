package com.github.distributed.tx.sample.api.core;

import java.io.Serializable;
import java.util.UUID;

@SuppressWarnings("serial")
public class ReliableMessage implements Serializable {
	
	private String id;
	private String appInterfaceForConfirmation;
	private String appInterfaceMethodForConfirmation;
	private String exchange;
	private String routingKey;
	private String body;
	private int retryTimesIfConsumedFail;
	
	public ReliableMessage() {
		setDefaultValue();
	}
	
	private void setDefaultValue() {
		this.id = UUID.randomUUID().toString();
		this.retryTimesIfConsumedFail = 5;
	}
	
	public void setId(String id) {
		this.id = id;
	}

	public void setAppInterfaceForConfirmation(String appInterfaceForConfirmation) {
		this.appInterfaceForConfirmation = appInterfaceForConfirmation;
	}

	public void setAppInterfaceMethodForConfirmation(
			String appInterfaceMethodForConfirmation) {
		this.appInterfaceMethodForConfirmation = appInterfaceMethodForConfirmation;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setRetryTimesIfConsumedFail(int retryTimesIfConsumedFail) {
		this.retryTimesIfConsumedFail = retryTimesIfConsumedFail;
	}

	public String getId() {
		return id;
	}

	public String getApp() {
		return "app";
	}

	public String getAppInterfaceForConfirmation() {
		return appInterfaceForConfirmation;
	}

	public String getAppInterfaceMethodForConfirmation() {
		return appInterfaceMethodForConfirmation;
	}

	public String getExchange() {
		return exchange;
	}

	public String getRoutingKey() {
		return routingKey;
	}

	public String getBody() {
		return body;
	}

	public int getRetryTimesIfConsumedFail() {
		return retryTimesIfConsumedFail;
	}

}
