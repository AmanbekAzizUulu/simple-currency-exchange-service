package com.dandaev.edu.service;

import java.util.List;

import com.dandaev.edu.dto.request.CurrencyRequestDto;
import com.dandaev.edu.dto.response.CurrencyResponseDto;

public interface CurrencyService {
	List<CurrencyResponseDto> getAllCurrencies ();
	CurrencyResponseDto getCurrencyByCode (String code);
	CurrencyResponseDto createCurrency (CurrencyRequestDto request);
}
