package com.dandaev.edu.controller;

import java.io.IOException;
import java.math.BigDecimal;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.CurrencyDaoImpl;
import com.dandaev.edu.dao.ExchangeRateDao;
import com.dandaev.edu.dao.ExchangeRateDaoImpl;
import com.dandaev.edu.dto.response.ExchangeResponseDto;
import com.dandaev.edu.mapper.JsonMapper;
import com.dandaev.edu.service.ExchangeRateService;
import com.dandaev.edu.service.ExchangeRateServiceImpl;
import com.dandaev.edu.validator.BigDecimalValidator;
import com.dandaev.edu.validator.RequestParamValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExchangeServlet extends HttpServlet {
	private ExchangeRateDao exchangeRateDao;
	private CurrencyDao currencyDao;
	private ExchangeRateService exchangeService;

	@ Override
	public void init () throws ServletException {
		currencyDao = new CurrencyDaoImpl();
		exchangeRateDao = new ExchangeRateDaoImpl();

		exchangeService = new ExchangeRateServiceImpl(exchangeRateDao, currencyDao);
	}

	@ Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String from = req.getParameter("from");
		String to = req.getParameter("to");
		String amountStr = req.getParameter("amount");

		RequestParamValidator.requireNotEmpty(from, "from");
		RequestParamValidator.requireNotEmpty(to, "to");
		RequestParamValidator.requireNotEmpty(amountStr, "amount");

		BigDecimal amount = BigDecimalValidator.parseAndValidate(amountStr, "amount");

		ExchangeResponseDto result = exchangeService.exchange(from, to, amount);

		writeJsonResponse(resp, result);
	}

	private void writeJsonResponse (HttpServletResponse resp, ExchangeResponseDto data) throws IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		resp.getWriter().write(JsonMapper.MAPPER.writeValueAsString(data));
	}
}
