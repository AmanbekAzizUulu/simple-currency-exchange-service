package com.dandaev.edu.service;

import java.math.BigDecimal;
import java.util.List;

import com.dandaev.edu.dto.request.ExchangeRateRequestDto;
import com.dandaev.edu.dto.response.ExchangeRateResponseDto;
import com.dandaev.edu.dto.response.ExchangeResponseDto;

public interface ExchangeRateService {
	List<ExchangeRateResponseDto> getAllExchangeRates ();
	ExchangeRateResponseDto getExchangeRate (String baseCode, String targetCode);
	ExchangeRateResponseDto createExchangeRate (ExchangeRateRequestDto request);
	ExchangeRateResponseDto updateExchangeRate (String baseCode, String targetCode, BigDecimal newRate);
	ExchangeResponseDto exchange (String from, String to, BigDecimal amount);
}
