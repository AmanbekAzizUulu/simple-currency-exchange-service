package com.dandaev.edu.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.CurrencyDaoImpl;
import com.dandaev.edu.dao.ExchangeRateDao;
import com.dandaev.edu.dao.ExchangeRateDaoImpl;
import com.dandaev.edu.dto.request.ExchangeRateRequestDto;
import com.dandaev.edu.dto.response.ExchangeRateResponseDto;
import com.dandaev.edu.mapper.JsonMapper;
import com.dandaev.edu.service.ExchangeRateService;
import com.dandaev.edu.service.ExchangeRateServiceImpl;
import com.dandaev.edu.validator.BigDecimalValidator;
import com.dandaev.edu.validator.ExchangeRateValidator;
import com.dandaev.edu.validator.RequestParamValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExchangeRatesServlet extends HttpServlet {
	private CurrencyDao currencyDao;
	private ExchangeRateDao exchangeRateDao;
	private ExchangeRateService exchangeRateService;

	@ Override
	public void init () throws ServletException {
		currencyDao = new CurrencyDaoImpl();
		exchangeRateDao = new ExchangeRateDaoImpl();
		exchangeRateService = new ExchangeRateServiceImpl(exchangeRateDao, currencyDao);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<ExchangeRateResponseDto> rates = exchangeRateService.getAllExchangeRates();
		writeJson(resp, HttpServletResponse.SC_OK, rates);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String baseCode = req.getParameter("baseCurrencyCode");
		String targetCode = req.getParameter("targetCurrencyCode");
		String rateStr = req.getParameter("rate");

		RequestParamValidator.requireNotEmpty(baseCode, "baseCurrencyCode");
		RequestParamValidator.requireNotEmpty(targetCode, "targetCurrencyCode");
		RequestParamValidator.requireNotEmpty(rateStr, "rate");

		BigDecimal rate = BigDecimalValidator.parseAndValidate(rateStr, "rate");

		ExchangeRateValidator.validateRate(rate);

		ExchangeRateRequestDto request = new ExchangeRateRequestDto(baseCode, targetCode, rate);
		ExchangeRateResponseDto response = exchangeRateService.createExchangeRate(request);

		writeJson(resp, HttpServletResponse.SC_CREATED, response);
	}

	private void writeJson (HttpServletResponse resp, int status, Object data) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		resp.getWriter().write(JsonMapper.MAPPER.writeValueAsString(data));
	}

}
