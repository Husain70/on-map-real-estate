package com.example.realestate.transactions.web;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.realestate.transactions.Transaction;

@Component
public class TransactionStreamPublisher {

	private static final Logger log = LoggerFactory.getLogger(TransactionStreamPublisher.class);
	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	public SseEmitter register() {
		SseEmitter emitter = new SseEmitter(0L);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		emitter.onError(ex -> emitters.remove(emitter));
		emitters.add(emitter);
		return emitter;
	}

	public void publish(Transaction tx) {
		TransactionResponse payload = TransactionResponse.from(tx);
		emitters.forEach(emitter -> {
			try {
				emitter.send(SseEmitter.event().name("transaction").data(payload));
			} catch (IOException e) {
				log.debug("Removing stale SSE emitter: {}", e.getMessage());
				emitter.complete();
				emitters.remove(emitter);
			}
		});
	}
}
