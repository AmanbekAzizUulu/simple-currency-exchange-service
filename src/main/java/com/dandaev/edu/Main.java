package com.dandaev.edu;

import java.util.List;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.CurrencyDaoImpl;
import com.dandaev.edu.dao.ExchangeRateDao;
import com.dandaev.edu.dao.ExchangeRateDaoImpl;
import com.dandaev.edu.dto.request.CurrencyRequestDto;
import com.dandaev.edu.dto.response.CurrencyResponseDto;
import com.dandaev.edu.dto.response.ExchangeRateResponseDto;
import com.dandaev.edu.model.Currency;
import com.dandaev.edu.model.ExchangeRate;
import com.dandaev.edu.service.CurrencyServiceImpl;
import com.dandaev.edu.service.ExchangeRateServiceImpl;

public class Main {
	public static void main (String[] args) {
		Currency currency = null;
		ExchangeRate exchangeRate = null;

		CurrencyDao currencyDaoImpl = new CurrencyDaoImpl();
		CurrencyServiceImpl currencyServiceImpl = new CurrencyServiceImpl(currencyDaoImpl);

		ExchangeRateDao exchangeRateDao = new ExchangeRateDaoImpl();
		ExchangeRateServiceImpl exchangeRateServiceImpl = new ExchangeRateServiceImpl(exchangeRateDao, currencyDaoImpl);

		List<CurrencyResponseDto> allCurrenciesDtos = currencyServiceImpl.getAllCurrencies();
		List<ExchangeRateResponseDto> allExchangeRateDtos = exchangeRateServiceImpl.getAllExchangeRates();

		for (CurrencyResponseDto currencyResponseDto : allCurrenciesDtos) {
			currency = Currency.builder().fullName(currencyResponseDto.fullName()).code(currencyResponseDto.code()).sign(currencyResponseDto.sign()).id(currencyResponseDto.id()).build();
			System.out.println(currency);
		}

		for (ExchangeRateResponseDto exchangeRateDto : allExchangeRateDtos) {
			exchangeRate = ExchangeRate.builder().id(exchangeRateDto.id()).baseCurrencyId(exchangeRateDto.baseCurrency().id()).targetCurrencyId(exchangeRateDto.targetCurrency().id()).rate(exchangeRateDto.rate()).build();
			System.out.println(exchangeRate);
		}

		CurrencyResponseDto createdCurrency = currencyServiceImpl.createCurrency(CurrencyRequestDto.builder().code("TEST").fullName("Test Full Name").sign("!@#$").build());

		System.out.println(createdCurrency);
	}
}
