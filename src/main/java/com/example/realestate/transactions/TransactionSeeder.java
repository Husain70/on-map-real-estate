package com.example.realestate.transactions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "transactions.seed.enabled", havingValue = "true")
public class TransactionSeeder implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(TransactionSeeder.class);

	private final TransactionRepository repository;
	private final Random random = new Random(42L);

	@Value("${transactions.seed.count:25}")
	private int seedCount;

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

	public TransactionSeeder(TransactionRepository repository) {
		this.repository = repository;
	}

	@Override
	public void run(String... args) {
		if (repository.count() > 0) {
			log.info("Seed skipped; transactions already present.");
			return;
		}

		for (int i = 0; i < seedCount; i++) {
			City city = randomCity();
			Transaction tx = new Transaction();
			tx.setId(UUID.randomUUID());
			tx.setCity(city.name());
			tx.setCityCode(city.code());
			tx.setLatitude(jitter(city.lat()));
			tx.setLongitude(jitter(city.lng()));
			tx.setTime(Instant.now().minusSeconds(random.nextInt(3600)));
			tx.setPrice(randomPrice());
			tx.setType(randomType());
			repository.save(tx);
		}
		log.info("Seeded {} fake transactions.", seedCount);
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
		double delta = (random.nextDouble() - 0.5) * 0.05;
		return value + delta;
	}

	private record City(String name, String code, double lat, double lng) {
	}
}
