package com.kafka.assignment.consumer_producer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

	@KafkaListener(topics = "assignment-topic", groupId = "group-id")
	public void listenGroupFoo(String message) {
		System.out.println("Received Message in group group-id: " + message);
	}
}
