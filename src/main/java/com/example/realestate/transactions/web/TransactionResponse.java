package com.example.realestate.transactions.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.example.realestate.transactions.Transaction;
import com.example.realestate.transactions.TransactionType;

public class TransactionResponse {

	private UUID id;
	private String city;
	private String cityCode;
	private double latitude;
	private double longitude;
	private Instant time;
	private BigDecimal price;
	private TransactionType type;

	public static TransactionResponse from(Transaction transaction) {
		TransactionResponse response = new TransactionResponse();
		response.id = transaction.getId();
		response.city = transaction.getCity();
		response.cityCode = transaction.getCityCode();
		response.latitude = transaction.getLatitude();
		response.longitude = transaction.getLongitude();
		response.time = transaction.getTime();
		response.price = transaction.getPrice();
		response.type = transaction.getType();
		return response;
	}

	public UUID getId() {
		return id;
	}

	public String getCity() {
		return city;
	}

	public String getCityCode() {
		return cityCode;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Instant getTime() {
		return time;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public TransactionType getType() {
		return type;
	}
}
