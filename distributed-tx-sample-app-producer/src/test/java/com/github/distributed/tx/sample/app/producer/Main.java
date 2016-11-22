package com.github.distributed.tx.sample.app.producer;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

	public static void main(String[] args) {
		ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(
				"app-producer.xml", "app-producer-dubbo.xml");
		AppProducer appProducer = (AppProducer) ctx.getBean("appProducer");
		appProducer.sample();
		ctx.close();
		System.exit(0);
	}
	
}
