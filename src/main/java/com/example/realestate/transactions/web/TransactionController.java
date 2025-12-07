package com.example.realestate.transactions.web;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.realestate.transactions.Transaction;
import com.example.realestate.transactions.TransactionService;
import com.example.realestate.transactions.TransactionType;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transactions")
@Validated
public class TransactionController {

	private final TransactionService service;
	private final TransactionStreamPublisher streamPublisher;

	public TransactionController(TransactionService service, TransactionStreamPublisher streamPublisher) {
		this.service = service;
		this.streamPublisher = streamPublisher;
	}

	@PostMapping
	public ResponseEntity<TransactionResponse> add(@Valid @RequestBody AddTransactionRequest request) {
		Transaction saved = service.save(request);
		return ResponseEntity.created(URI.create("/api/transactions/" + saved.getId()))
				.body(TransactionResponse.from(saved));
	}

	@GetMapping("/latest")
	public List<TransactionResponse> latest(@RequestParam(defaultValue = "50") int limit,
			@RequestParam(name = "type", required = false) TransactionType type,
			@RequestParam(name = "q", required = false) String query) {
		int safeLimit = Math.min(Math.max(limit, 1), 200);
		return service.latest(safeLimit, type, query).stream().map(TransactionResponse::from).toList();
	}

	@GetMapping(path = "/stream", produces = "text/event-stream")
	public SseEmitter stream() {
		return streamPublisher.register();
	}
}
