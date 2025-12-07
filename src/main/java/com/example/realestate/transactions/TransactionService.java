package com.example.realestate.transactions;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.realestate.transactions.web.AddTransactionRequest;
import com.example.realestate.transactions.web.TransactionStreamPublisher;

@Service
public class TransactionService {

	private final TransactionRepository repository;
	private final TransactionStreamPublisher streamPublisher;

	public TransactionService(TransactionRepository repository, TransactionStreamPublisher streamPublisher) {
		this.repository = repository;
		this.streamPublisher = streamPublisher;
	}

	@Transactional
	public Transaction save(AddTransactionRequest request) {
		if (request.getId() != null) {
			Optional<Transaction> existing = repository.findById(request.getId());
			if (existing.isPresent()) {
				return existing.get();
			}
		}
		Transaction transaction = new Transaction();
		transaction.setId(request.getId());
		transaction.setCity(request.getCity());
		transaction.setCityCode(request.getCityCode());
		transaction.setLatitude(request.getLatitude());
		transaction.setLongitude(request.getLongitude());
		transaction.setTime(request.getTime());
		transaction.setPrice(request.getPrice());
		transaction.setType(request.getType());
		Transaction saved = repository.save(transaction);
		streamPublisher.publish(saved);
		return saved;
	}

	@Transactional(readOnly = true)
	public List<Transaction> latest(int limit, TransactionType type, String query) {
		String normalizedQuery = query == null ? "" : query;
		return repository.findLatestFiltered(type, normalizedQuery,
				PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "time")));
	}
}
