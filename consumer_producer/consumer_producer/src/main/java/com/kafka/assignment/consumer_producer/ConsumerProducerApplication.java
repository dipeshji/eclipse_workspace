package com.kafka.assignment.consumer_producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ConsumerProducerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConsumerProducerApplication.class, args);

		ConfigurableApplicationContext context = SpringApplication.run(ConsumerProducerApplication.class);
		KafkaProducer producer = context.getBean(KafkaProducer.class);

		try {
			for (int i = 0; i <= 50; i++) {
				producer.sendMessage("message number" + i);
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			context.close();
		}
	}

}
