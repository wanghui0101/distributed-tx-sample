package com.github.distributed.tx.sample.confirmation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.github.distributed.tx.sample.commons.message.DistributedTransactionMessage;
import com.github.distributed.tx.sample.commons.message.DistributedTransactionMessageDao;

@Component
public class ReliableMessageConfirmation implements ApplicationContextAware {
	
	private static final Logger logger = LoggerFactory.getLogger(ReliableMessageConfirmation.class);

	@Autowired
	private DistributedTransactionMessageDao distributedTransactionMessageDao;
	
	private ApplicationContext applicationContext;
	
	@Scheduled(fixedRate = 10 * 1000) // 10s执行一次
	public void confirm() {
		confirmInitializedMessages(distributedTransactionMessageDao.queryInitializedMessages());
	}

	private void confirmInitializedMessages(List<DistributedTransactionMessage> initializedMessages) {
		for (DistributedTransactionMessage msg : initializedMessages) {
			if (msg.getInitializedAt().getTime() - System.currentTimeMillis() > 60 * 1000) {
				String beanName = msg.getAppInterfaceForConfirmation();
				String beanMethod = msg.getAppInterfaceMethodForConfirmation();
				Object bean = applicationContext.getBean(beanName); // app的dubbo接口
				Method[] methods = bean.getClass().getDeclaredMethods();
				for (Method method : methods) {
					if (method.getName().equals(beanMethod)) {
						try {
							checkMethod(method);
							Object result = method.invoke(bean, msg.getMessageId());
							confirmMessageIfNecessary((boolean) result, msg);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							logger.error(ExceptionUtils.getStackTrace(e));
						}
						break;
					}
				}
			}
		}
	}

	private void checkMethod(Method method) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?> returnType = method.getReturnType();
		Assert.isTrue(parameterTypes != null && parameterTypes.length == 1 
				&& parameterTypes[0].getName().equals(String.class.getName()) 
				&& (returnType.getName().equals(Boolean.class.getName()) || returnType.getName().equals(boolean.class.getName())));
	}

	private void confirmMessageIfNecessary(boolean result, DistributedTransactionMessage msg) {
		if (result) {
			msg.setStatus(DistributedTransactionMessage.Status.CONFIRMED);
			msg.setConfirmedAt(new Timestamp(new Date().getTime()));
			distributedTransactionMessageDao.save(msg);
		} else {
			distributedTransactionMessageDao.delete(msg.getId());
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
