package com.dandaev.edu.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.ExchangeRateDao;
import com.dandaev.edu.dto.request.ExchangeRatePathRequestDto;
import com.dandaev.edu.dto.response.CurrencyResponseDto;
import com.dandaev.edu.dto.response.ExchangeRateResponseDto;
import com.dandaev.edu.exception.conflict.ExchangeRateAlreadyExistsException;
import com.dandaev.edu.exception.database.DataAccessException;
import com.dandaev.edu.exception.database.UniqueConstraintViolationException;
import com.dandaev.edu.exception.internal_server_error.InternalServerErrorException;
import com.dandaev.edu.exception.not_found.BaseCurrencyNotFoundException;
import com.dandaev.edu.exception.not_found.ExchangeRateNotFoundException;
import com.dandaev.edu.exception.not_found.TargetCurrencyNotFoundException;
import com.dandaev.edu.model.Currency;
import com.dandaev.edu.model.ExchangeRate;

public class ExchangeRateServiceImpl implements ExchangeRateService {
	private final ExchangeRateDao exchangeRateDao;
	private final CurrencyDao currencyDao;

	public ExchangeRateServiceImpl (ExchangeRateDao exchangeRateDao, CurrencyDao currencyDao) {
		this.exchangeRateDao = exchangeRateDao;
		this.currencyDao = currencyDao;
	}

	@Override
    public ExchangeRateResponseDto getExchangeRate(String baseCode, String targetCode) {
        ExchangeRate rate = exchangeRateDao.findByPair(baseCode, targetCode).orElseThrow(() -> new ExchangeRateNotFoundException(baseCode, targetCode));

		Currency base = currencyDao.findById(rate.getBaseCurrencyId()).orElseThrow(() -> new BaseCurrencyNotFoundException(baseCode));
        Currency target = currencyDao.findById(rate.getTargetCurrencyId()).orElseThrow( () -> new TargetCurrencyNotFoundException(targetCode));

		return toDto(rate, base, target);
    }

	@ Override
	public ExchangeRateResponseDto createExchangeRate (ExchangeRatePathRequestDto request) {
		Currency base = currencyDao.findByCode(request.baseCurrencyCode().toUpperCase()).orElseThrow( () -> new BaseCurrencyNotFoundException(request.baseCurrencyCode()));
		Currency target = currencyDao.findByCode(request.targetCurrencyCode().toUpperCase()).orElseThrow( () -> new TargetCurrencyNotFoundException(request.targetCurrencyCode()));

		ExchangeRate rate = ExchangeRate.builder()
										.baseCurrencyId(base.getId())
										.targetCurrencyId(target.getId())
										.rate(request.rate())
										.build();
		try {
			exchangeRateDao.persist(rate);
		} catch (UniqueConstraintViolationException e) {
			throw new ExchangeRateAlreadyExistsException(request.baseCurrencyCode() + "/"+ request.targetCurrencyCode());
		} catch (DataAccessException e) {
			throw new InternalServerErrorException("Failed to save exchange rate", e);
		}
		return toDto(rate, base, target);
	}

	@Override
    public ExchangeRateResponseDto updateExchangeRate(String baseCode, String targetCode, BigDecimal newRate) {
        ExchangeRate updated = exchangeRateDao.updateRate(baseCode, targetCode, newRate);

		Currency base = currencyDao.findById(updated.getBaseCurrencyId()).orElseThrow(() -> new BaseCurrencyNotFoundException(updated.getId() ));
        Currency target = currencyDao.findById(updated.getTargetCurrencyId()).orElseThrow( () -> new TargetCurrencyNotFoundException(updated.getId()));

        return toDto(updated, base, target);
    }

	private ExchangeRateResponseDto toDto (ExchangeRate rate, Currency base, Currency target) {
		return ExchangeRateResponseDto.builder()
		                              .id(rate.getId())
		                              .baseCurrency(toCurrencyResponseDto(base))
		                              .targetCurrency(toCurrencyResponseDto(target))
									  .rate(rate.getRate())
		                              .build();
	}

	@ Override
	public List<ExchangeRateResponseDto> getAllExchangeRates () {
		List<ExchangeRate> allExchangeRates = exchangeRateDao.findAll();
		List<ExchangeRateResponseDto> allExchangeRateResponseDtos = new ArrayList<ExchangeRateResponseDto>();

		for (ExchangeRate exchangeRate : allExchangeRates) {
			Currency base = currencyDao.findById(exchangeRate.getBaseCurrencyId()).orElseThrow( () -> new BaseCurrencyNotFoundException(exchangeRate.getBaseCurrencyId()));
			Currency target = currencyDao.findById(exchangeRate.getTargetCurrencyId()).orElseThrow( () -> new TargetCurrencyNotFoundException(exchangeRate.getTargetCurrencyId()));

			ExchangeRateResponseDto dto = toDto(exchangeRate, base, target);

			allExchangeRateResponseDtos.add(dto);
		}

		return allExchangeRateResponseDtos;
	}

	@ Override
	public ExchangeRateResponseDto exchange (String from, String to, BigDecimal amount) {
		return null;
	}

	private CurrencyResponseDto toCurrencyResponseDto (Currency currency) {
		return new CurrencyResponseDto(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());
	}

}
