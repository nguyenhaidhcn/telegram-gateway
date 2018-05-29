package com.snap.gateway.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class Sender {
	private static final Logger log = LoggerFactory.getLogger(Sender.class);
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Value("${OrderResponse.Topic}")
	private String defaultDestination;
	
	/**
	 * Send a message to destination
	 * @param destination
	 * @param message
	 */
	public void send(String destination, String message) {
		jmsTemplate.send(destination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(message);
            }
		});
	}
	
	/**
	 * Send to default destination
	 * @param message
	 */
	public void send(String message) {
		log.info("Send message '{}' to topic '{}'", message, defaultDestination);
		this.send(defaultDestination, message);
	}
}
