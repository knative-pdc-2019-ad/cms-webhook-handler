package com.appdirect.pdc.cms.webhookhandler.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.appdirect.pdc.cms.webhookhandler.WebhookHandlerApplication;

@RestController
@RequestMapping("/webhook")
@Slf4j
public class WebhookController {

	@Autowired
	private WebhookHandlerApplication.PubsubOutboundGateway messagingGateway;

	@PostMapping(value = "/handle/{isvId}/{type}", consumes = {"application/json"})
	public ResponseEntity<String> handle(@PathVariable("isvId") String isvId,
																			 @PathVariable("type") String type,
																			 @RequestBody String data) {
		log.info("Received request for ISV={} for type={}", isvId, type);
		messagingGateway.sendToPubsub(data);
		return ResponseEntity.ok("success");
	}
}
