package com.example.generator;

import java.time.Duration;

import reactor.util.retry.Retry;

public class RetrySpecFactory {

	private final Duration backoff;
	private final int maxAttempts;

	public RetrySpecFactory(Duration backoff, int maxAttempts) {
		this.backoff = backoff;
		this.maxAttempts = maxAttempts;
	}

	public Retry build() {
		return Retry.fixedDelay(maxAttempts, backoff)
				.filter(ex -> true);
	}
}
