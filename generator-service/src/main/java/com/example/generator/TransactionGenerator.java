package com.example.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@Component
public class TransactionGenerator {

	private static final Logger log = LoggerFactory.getLogger(TransactionGenerator.class);

	private final WebClient webClient;
	private final RetrySpecFactory retryFactory;
	private final Random random = new Random();

	@Value("${generator.target-url:http://localhost:8080/api/transactions}")
	private String targetUrl;

	private final List<City> cities = List.of(
			new City("Riyadh", "011", 24.7136, 46.6753),
			new City("Jeddah", "012", 21.4858, 39.1925),
			new City("Dammam", "013", 26.4207, 50.0888),
			new City("Medina", "014", 24.5247, 39.5692),
			new City("Mecca", "015", 21.3891, 39.8579),
			new City("Tabuk", "016", 28.3839, 36.5662),
			new City("Abha", "017", 18.2465, 42.5117),
			new City("Taif", "018", 21.2703, 40.4158),
			new City("Buraydah", "019", 26.3594, 43.9818),
			new City("Hail", "020", 27.5114, 41.7208));

	public TransactionGenerator(WebClient webClient, RetrySpecFactory retryFactory) {
		this.webClient = webClient;
		this.retryFactory = retryFactory;
	}

	@Scheduled(fixedRateString = "${generator.delay-ms:5000}")
	public void generate() {
		City city = randomCity();
		TransactionPayload payload = new TransactionPayload();
		payload.setId(UUID.randomUUID());
		payload.setCity(city.name());
		payload.setCityCode(city.code());
		payload.setLatitude(jitter(city.lat()));
		payload.setLongitude(jitter(city.lng()));
		payload.setTime(Instant.now());
		payload.setPrice(randomPrice());
		payload.setType(randomType());

		webClient.post()
				.uri(targetUrl)
				.body(BodyInserters.fromValue(payload))
				.retrieve()
				.toBodilessEntity()
				.retryWhen(retryFactory.build())
				.onErrorResume(ex -> {
					log.warn("Generator failed to send transaction: {}", ex.getMessage());
					return Mono.empty();
				})
				.subscribe(resp -> log.info("Generated transaction {}", payload.getId()));
	}

	private City randomCity() {
		return cities.get(random.nextInt(cities.size()));
	}

	private String randomType() {
		return switch (random.nextInt(3)) {
			case 0 -> "LAND";
			case 1 -> "VILLA";
			default -> "APARTMENT";
		};
	}

	private BigDecimal randomPrice() {
		double min = 250_000;
		double max = 4_000_000;
		double value = min + (max - min) * random.nextDouble();
		return BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP);
	}

	private double jitter(double value) {
		double delta = (random.nextDouble() - 0.5) * 0.05;
		return value + delta;
	}

	private record City(String name, String code, double lat, double lng) {
	}
}
