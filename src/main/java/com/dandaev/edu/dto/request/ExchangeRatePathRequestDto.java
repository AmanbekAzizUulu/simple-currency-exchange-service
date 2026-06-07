package com.dandaev.edu.dto.request;

import java.math.BigDecimal;

import lombok.Builder;

@ Builder
public record ExchangeRatePathRequestDto(String baseCurrencyCode, String targetCurrencyCode, BigDecimal rate) {
}
