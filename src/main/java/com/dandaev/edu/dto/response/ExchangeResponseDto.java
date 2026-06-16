package com.dandaev.edu.dto.response;

import java.math.BigDecimal;

import lombok.Builder;

@ Builder
public record ExchangeResponseDto(CurrencyResponseDto baseCurrency, CurrencyResponseDto targetCurrency, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
}
