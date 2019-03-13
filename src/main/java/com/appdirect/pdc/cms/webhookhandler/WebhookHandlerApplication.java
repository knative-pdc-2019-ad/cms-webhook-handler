package com.appdirect.pdc.cms.webhookhandler;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.pubsub.core.PubSubOperations;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.cloud.gcp.pubsub.integration.outbound.PubSubMessageHandler;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.google.cloud.pubsub.v1.AckReplyConsumer;

@SpringBootApplication
@Slf4j
public class WebhookHandlerApplication {

	@Value("${spring.cloud.gcp.topic}")
	private String topic;

	@Value("${spring.cloud.gcp.subscription}")
	private String subscription;

	public static void main(String[] args) {
		SpringApplication.run(WebhookHandlerApplication.class, args);
	}


	@Bean
	@ServiceActivator(inputChannel = "pubsubOutputChannel")
	public MessageHandler messageSender(PubSubOperations pubsubTemplate) {
		return new PubSubMessageHandler(pubsubTemplate, topic);
	}

	@MessagingGateway(defaultRequestChannel = "pubsubOutputChannel")
	public interface PubsubOutboundGateway {

		void sendToPubsub(String text);
	}


	// Inbound channel adapter.
	@Bean
	public MessageChannel pubsubInputChannel() {
		return new DirectChannel();
	}
	// end::pubsubInputChannel[]

	// tag::messageChannelAdapter[]
	@Bean
	public PubSubInboundChannelAdapter messageChannelAdapter(
		@Qualifier("pubsubInputChannel") MessageChannel inputChannel,
		PubSubOperations pubSubTemplate) {
		PubSubInboundChannelAdapter adapter =
			new PubSubInboundChannelAdapter(pubSubTemplate, subscription);
		adapter.setOutputChannel(inputChannel);
		adapter.setAckMode(AckMode.MANUAL);

		return adapter;
	}
	// end::messageChannelAdapter[]

	// tag::messageReceiver[]
	@Bean
	@ServiceActivator(inputChannel = "pubsubInputChannel")
	public MessageHandler messageReceiver() {
		return message -> {
			log.info("Message arrived! Payload: " + message.getPayload());
			AckReplyConsumer consumer =
				(AckReplyConsumer) message.getHeaders().get(GcpPubSubHeaders.ACKNOWLEDGEMENT);
			consumer.ack();
		};
	}
	// end::messageReceiver[]

}
