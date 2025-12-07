package com.example.generator;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeneratorConfig {

	@Bean
	public WebClient webClient() {
		return WebClient.builder()
				.exchangeStrategies(ExchangeStrategies.builder()
						.codecs(c -> c.defaultCodecs().maxInMemorySize(256 * 1024))
						.build())
				.build();
	}

	@Bean
	public RetrySpecFactory retrySpecFactory() {
		return new RetrySpecFactory(Duration.ofSeconds(1), 3);
	}
}
