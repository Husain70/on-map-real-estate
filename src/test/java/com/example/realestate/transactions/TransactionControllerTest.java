package com.example.realestate.transactions;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import com.example.realestate.transactions.web.AddTransactionRequest;
import com.example.realestate.transactions.web.TransactionResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TransactionControllerTest {

	@LocalServerPort
	private int port;

	@Test
        void addAndFetchLatest() {
                RestClient client = RestClient.builder().baseUrl(baseUrl()).build();

		AddTransactionRequest request = new AddTransactionRequest();
		request.setId(UUID.randomUUID());
		request.setCity("Riyadh");
		request.setCityCode("011");
		request.setLatitude(24.7);
		request.setLongitude(46.6);
		request.setTime(Instant.now());
		request.setPrice(BigDecimal.valueOf(900_000));
		request.setType(TransactionType.VILLA);

		ResponseEntity<TransactionResponse> createResponse = client.post()
				.uri("/api/transactions")
				.body(request)
				.retrieve()
				.toEntity(TransactionResponse.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(createResponse.getBody()).isNotNull();
		assertThat(createResponse.getBody().getId()).isNotNull();

		ResponseEntity<TransactionResponse[]> latestResponse = client.get()
				.uri("/api/transactions/latest?limit=5&type=VILLA&q=riy")
				.retrieve()
				.toEntity(TransactionResponse[].class);

		assertThat(latestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(latestResponse.getBody()).isNotNull();
                List<TransactionResponse> responses = List.of(latestResponse.getBody());
                assertThat(responses).extracting(TransactionResponse::getCity).contains("Riyadh");
        }

        @Test
        void latestEndpointOrdersAndFilters() {
                RestClient client = RestClient.builder().baseUrl(baseUrl()).build();

                AddTransactionRequest older = new AddTransactionRequest();
                older.setCity("Dammam");
                older.setCityCode("013");
                older.setLatitude(26.4207);
                older.setLongitude(50.0888);
                older.setTime(Instant.parse("2024-01-01T00:00:00Z"));
                older.setPrice(BigDecimal.valueOf(500_000));
                older.setType(TransactionType.APARTMENT);

                AddTransactionRequest newer = new AddTransactionRequest();
                newer.setCity("Jeddah");
                newer.setCityCode("012");
                newer.setLatitude(21.4858);
                newer.setLongitude(39.1925);
                newer.setTime(Instant.parse("2024-06-01T00:00:00Z"));
                newer.setPrice(BigDecimal.valueOf(1_250_000));
                newer.setType(TransactionType.VILLA);

                client.post().uri("/api/transactions").body(older).retrieve().toBodilessEntity();
                client.post().uri("/api/transactions").body(newer).retrieve().toBodilessEntity();

                ResponseEntity<TransactionResponse[]> latestResponse = client.get()
                                .uri("/api/transactions/latest?limit=1&type=VILLA&q=jed")
                                .retrieve()
                                .toEntity(TransactionResponse[].class);

                assertThat(latestResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
                assertThat(latestResponse.getBody()).isNotNull();
                List<TransactionResponse> responses = List.of(latestResponse.getBody());
                assertThat(responses).hasSize(1);
                TransactionResponse only = responses.get(0);
                assertThat(only.getCity()).isEqualTo("Jeddah");
                assertThat(only.getTime()).isEqualTo(newer.getTime());
        }

	private String baseUrl() {
		return "http://localhost:" + port;
	}
}
