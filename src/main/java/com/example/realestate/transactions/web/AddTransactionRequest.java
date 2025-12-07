package com.example.realestate.transactions.web;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import com.example.realestate.transactions.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AddTransactionRequest {

	private UUID id;

	@NotBlank
	private String city;

	@NotBlank
	@Pattern(regexp = "^[0-9]{3}$", message = "cityCode must be 3 digits")
	private String cityCode;

	@NotNull
	@Min(value = -90, message = "latitude must be >= -90")
	@Max(value = 90, message = "latitude must be <= 90")
	private Double latitude;

	@NotNull
	@Min(value = -180, message = "longitude must be >= -180")
	@Max(value = 180, message = "longitude must be <= 180")
	private Double longitude;

	private Instant time;

	@NotNull
	@DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal price;

	@NotNull
	private TransactionType type;

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public TransactionType getType() {
		return type;
	}

	public void setType(TransactionType type) {
		this.type = type;
	}
}
