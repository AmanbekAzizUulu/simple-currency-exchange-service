package com.dandaev.edu.service;

import java.util.List;
import java.util.stream.Collectors;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dto.request.CurrencyRequestDto;
import com.dandaev.edu.dto.response.CurrencyResponseDto;
import com.dandaev.edu.exception.bad_request.InvalidCurrencyCodeException;
import com.dandaev.edu.exception.bad_request.InvalidCurrencyFullNameException;
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
		String name = request.name();
		String code = request.code().toUpperCase();
		String sign = request.sign();

		Currency currency = Currency.builder().code(code).fullName(name).sign(sign).build();
		try {
			if (!isValidCurrencyFullName(name)) {
				throw new InvalidCurrencyFullNameException("Invalid currency full name: " + request.name());
			}
			if (!isValidCurrencySign(sign)) {
				throw new InvalidCurrencySignException("Invalid currency sign: " + request.sign());
			}
			if (!isValidCurrencyCode(code)) {
				throw new InvalidCurrencyCodeException("Invalid currency code: " + request.code());
			}
			currencyDao.persist(currency);
		} catch (UniqueConstraintViolationException e) {
			throw new CurrencyAlreadyExistsException(code);
		} catch (DataAccessException e) {
			System.out.println("DataAccessException");
			throw new InternalServerErrorException("Database error", e);
		}
		return mapToDto(currency);
	}

	private CurrencyResponseDto mapToDto (Currency currency) {
		return new CurrencyResponseDto(currency.getId(), currency.getFullName(), currency.getCode(), currency.getSign());
	}

	private boolean isValidCurrencyCode (String code) {
		if (code == null) {
			return false;
		}
		String c = code.trim();
		return c.matches("^[A-Z]{3}$");
	}

	private boolean isValidCurrencySign (String sign) {
		if (sign == null) {
			return false;
		}
		String s = sign.trim();
		if (s.isEmpty() || s.length() > 3) {
			return false;
		}
		return s.matches("^[\\p{L}\\p{Sc}\\p{N}./,\\-\\s()]+$");
	}

	private boolean isValidCurrencyFullName (String fullName) {
		if (fullName == null) {
			return false;
		}
		String name = fullName.trim();
		if (name.length() < 2 || name.length() > 100) {
			return false;
		}
		return name.matches("^[\\p{L}\\s'\\-()]+$");
	}
}
