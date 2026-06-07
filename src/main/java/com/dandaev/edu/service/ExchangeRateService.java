package com.dandaev.edu.service;

import java.math.BigDecimal;
import java.util.List;

import com.dandaev.edu.dto.request.ExchangeRatePathRequestDto;
import com.dandaev.edu.dto.response.ExchangeRateResponseDto;

public interface ExchangeRateService {
	List<ExchangeRateResponseDto> getAllExchangeRates ();
	ExchangeRateResponseDto getExchangeRate (String baseCode, String targetCode);
	ExchangeRateResponseDto createExchangeRate (ExchangeRatePathRequestDto request);
	ExchangeRateResponseDto updateExchangeRate (String baseCode, String targetCode, BigDecimal newRate);
	ExchangeRateResponseDto exchange (String from, String to, BigDecimal amount);
}
