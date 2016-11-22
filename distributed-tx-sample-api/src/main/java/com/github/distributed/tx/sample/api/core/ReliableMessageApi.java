package com.github.distributed.tx.sample.api.core;

public interface ReliableMessageApi {

	boolean initialize(ReliableMessage reliableMessage);
	
	boolean confirm(String messageId);
	
	boolean acknowledge(String messageId);
	
	boolean finish(String messageId);
	
}
