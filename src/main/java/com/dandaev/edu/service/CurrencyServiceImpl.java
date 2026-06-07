package com.dandaev.edu.service;

import java.util.List;
import java.util.stream.Collectors;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dto.request.CurrencyRequestDto;
import com.dandaev.edu.dto.response.CurrencyResponseDto;
import com.dandaev.edu.exception.bad_request.InvalidCurrencyCodeException;
import com.dandaev.edu.exception.bad_request.InvalidCurrencySignException;
import com.dandaev.edu.exception.conflict.CurrencyAlreadyExistsException;
import com.dandaev.edu.exception.database.DataAccessException;
import com.dandaev.edu.exception.database.UniqueConstraintViolationException;
import com.dandaev.edu.exception.internal_server_error.InternalServerErrorException;
import com.dandaev.edu.exception.not_found.CurrencyNotFoundException;
import com.dandaev.edu.model.Currency;

public class CurrencyServiceImpl implements CurrencyService {
	private final CurrencyDao currencyDao;

	public CurrencyServiceImpl (CurrencyDao currencyDao) {
		this.currencyDao = currencyDao;
	}

	@ Override
	public List<CurrencyResponseDto> getAllCurrencies () {
		return currencyDao.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	@ Override
	public CurrencyResponseDto getCurrencyByCode (String code) {
		Currency currency = currencyDao.findByCode(code).orElseThrow( () -> new CurrencyNotFoundException("Currency not found: " + code));
		return mapToDto(currency);
	}

	@ Override
	public CurrencyResponseDto createCurrency (CurrencyRequestDto request) {
		String fullName = request.fullName();
		String code = request.code().toUpperCase();
		String sign = request.sign();

		Currency currency = Currency.builder().code(code).fullName(fullName).sign(sign).build();
		try {
			if (!isValidCurrencyCode(code)) {
				throw new InvalidCurrencyCodeException("Invalid currency code: " + request.code());
			}
			if (!isValidSign(sign)) {
				throw new InvalidCurrencySignException("Invalid currency sign: " + request.code());
			}
			if (!isValidFullName(fullName)) {
				throw new InvalidCurrencySignException("Invalid currency full name: " + request.code());
			}
			currencyDao.persist(currency);
		} catch (UniqueConstraintViolationException e) {
			throw new CurrencyAlreadyExistsException(code);
		} catch (DataAccessException e) {
			throw new InternalServerErrorException("Failed to save currency", e);
		}
		return mapToDto(currency);
	}

	private CurrencyResponseDto mapToDto (Currency currency) {
		return new CurrencyResponseDto(currency.getId(), currency.getCode(), currency.getFullName(), currency.getSign());
	}

	private boolean isValidCurrencyCode (String code) {
		if (code == null || code.trim().isEmpty()) {
			return false;
		}
		String trimmed = code.trim();
		return trimmed.length() == 3 && trimmed.matches("[A-Z]{3}");
	}

	private boolean isValidSign (String sign) {
		if (sign == null || sign.trim().isEmpty()) {
			return false;
		}
		String s = sign.trim();
		if (s.length() > 3) {
			return false;
		}
		return s.matches("^[A-Za-z€$£¥₽₹₸₴₦₱\\s'.]{1,3}$");
	}

	private boolean isValidFullName (String fullName) {
		if (fullName == null || fullName.trim()
		                                .isEmpty()) {
			return false;
		}
		String name = fullName.trim();
		if (name.length() < 2 || name.length() > 100) {
			return false;
		}
		return name.matches("^[\\p{L}\\s'\\-]+$");
	}
}
