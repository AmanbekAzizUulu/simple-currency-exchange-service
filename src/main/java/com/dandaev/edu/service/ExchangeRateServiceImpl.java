// @formatter:off
package com.dandaev.edu.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.CurrencyDaoImpl;
import com.dandaev.edu.dao.ExchangeRateDao;
import com.dandaev.edu.dto.request.ExchangeRateRequestDto;
import com.dandaev.edu.dto.response.CurrencyResponseDto;
import com.dandaev.edu.dto.response.ExchangeRateResponseDto;
import com.dandaev.edu.dto.response.ExchangeResponseDto;
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
	private static final int MONEY_SCALE = 2;
	private static final int RATE_SCALE = 6;
	private static final RoundingMode MONEY_ROUNDING = RoundingMode.HALF_EVEN;

	private static final String CURRENCY_CODE_FOR_CROSS_RATE_EXCHANGE = "USD";

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyDaoImpl.class);

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
	public ExchangeRateResponseDto createExchangeRate (ExchangeRateRequestDto request) {
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
        try {
			ExchangeRate updated = exchangeRateDao.updateRate(baseCode, targetCode, newRate);

			Currency base = currencyDao.findById(updated.getBaseCurrencyId()).orElseThrow(() -> new BaseCurrencyNotFoundException(updated.getId() ));
			Currency target = currencyDao.findById(updated.getTargetCurrencyId()).orElseThrow( () -> new TargetCurrencyNotFoundException(updated.getId()));

			return toDto(updated, base, target);
		} catch (DataAccessException e) {
        	throw new ExchangeRateNotFoundException(baseCode, targetCode);
    	}
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
	public ExchangeResponseDto exchange (String from, String to, BigDecimal amount) {
		Currency base = currencyDao.findByCode(from).orElseThrow(() -> new BaseCurrencyNotFoundException(from));
		Currency target = currencyDao.findByCode(to).orElseThrow(() -> new TargetCurrencyNotFoundException(to));

		BigDecimal rate;

		// direct exchange
		Optional<ExchangeRate> direct = exchangeRateDao.findByPair(from, to);
		if (direct.isPresent()) {
			rate = direct.get().getRate();
			return toExchangeResponseDto(rate, amount, base, target);
		}

		// inverse rate
		Optional<ExchangeRate> inverse = exchangeRateDao.findByPair(to, from);
		if (inverse.isPresent()) {
			rate = BigDecimal.ONE.divide(inverse.get().getRate(),RATE_SCALE,RoundingMode.HALF_UP);
			return toExchangeResponseDto(rate, amount, base, target);
		}

		// cross rate
		Optional<ExchangeRate> crossCurrencyToFrom = exchangeRateDao.findByPair(CURRENCY_CODE_FOR_CROSS_RATE_EXCHANGE, from);
		Optional<ExchangeRate> crossCurrencyToTo = exchangeRateDao.findByPair(CURRENCY_CODE_FOR_CROSS_RATE_EXCHANGE, to);

		if (crossCurrencyToFrom.isPresent() && crossCurrencyToTo.isPresent()) {
			BigDecimal rateTo = crossCurrencyToTo.get().getRate();
			BigDecimal rateFrom = crossCurrencyToFrom.get().getRate();

			if (rateFrom.compareTo(BigDecimal.ZERO) == 0) {
				throw new InternalServerErrorException("USD→from rate is zero");
			}

			rate = rateTo.divide(rateFrom, RATE_SCALE, RoundingMode.HALF_UP);
			LOGGER.debug("Cross rate USD→{}={}, USD→{}={}, calculated rate={}", from, rateFrom, to, rateTo, rate);

			return toExchangeResponseDto(rate, amount, base, target);
		}

		// в случае если не найден курс обмена
		throw new ExchangeRateNotFoundException(from, to);
	}

	private CurrencyResponseDto toCurrencyResponseDto (Currency currency) {
		return new CurrencyResponseDto(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());
	}


	private ExchangeResponseDto toExchangeResponseDto (BigDecimal rate, BigDecimal amount, Currency base, Currency target) {
		BigDecimal convertedAmount = amount.multiply(rate).setScale(MONEY_SCALE, MONEY_ROUNDING);
		BigDecimal displayAmount = amount.setScale(MONEY_SCALE, MONEY_ROUNDING);

		return ExchangeResponseDto.builder()
		                          .baseCurrency(toCurrencyResponseDto(base))
		                          .targetCurrency(toCurrencyResponseDto(target))
		                          .rate(rate)
		                          .amount(displayAmount)
		                          .convertedAmount(convertedAmount)
		                          .build();
	}
}
