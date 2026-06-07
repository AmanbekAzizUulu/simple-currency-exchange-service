package com.dandaev.edu.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@ Builder
public record ExchangeRateResponseDto(Long id, CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency, BigDecimal rate) {
}
