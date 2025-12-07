package com.example.realestate.transactions.generator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.realestate.transactions.TransactionType;
import com.example.realestate.transactions.web.AddTransactionRequest;

@Component
@ConditionalOnProperty(value = "transactions.generator.enabled", havingValue = "true")
public class TransactionGenerator {

	private static final Logger log = LoggerFactory.getLogger(TransactionGenerator.class);

	private final RestTemplate restTemplate;
	private final Random random = new Random();

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

	@Value("${transactions.generator.target-url:http://localhost:8080/api/transactions}")
	private String targetUrl;

	public TransactionGenerator(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Scheduled(fixedDelayString = "${transactions.generator.delay-ms:5000}")
	public void generate() {
		City city = randomCity();
		AddTransactionRequest payload = new AddTransactionRequest();
		payload.setId(UUID.randomUUID());
		payload.setCity(city.name());
		payload.setCityCode(city.code());
		payload.setLatitude(jitter(city.lat()));
		payload.setLongitude(jitter(city.lng()));
		payload.setTime(Instant.now());
		payload.setPrice(randomPrice());
		payload.setType(randomType());

		try {
			restTemplate.postForEntity(targetUrl, payload, Void.class);
			log.info("Generated transaction for {} {}", payload.getCity(), payload.getPrice());
		} catch (Exception ex) {
			log.warn("Failed to push generated transaction: {}", ex.getMessage());
		}
	}

	private City randomCity() {
		return cities.get(random.nextInt(cities.size()));
	}

	private TransactionType randomType() {
		return TransactionType.values()[random.nextInt(TransactionType.values().length)];
	}

	private BigDecimal randomPrice() {
		double min = 250_000;
		double max = 4_000_000;
		double value = min + (max - min) * random.nextDouble();
		return BigDecimal.valueOf(value).setScale(0, RoundingMode.HALF_UP);
	}

	private double jitter(double value) {
		double delta = (random.nextDouble() - 0.5) * 0.05; // small offset ~ +/-0.025
		return value + delta;
	}

	private record City(String name, String code, double lat, double lng) {
	}
}
