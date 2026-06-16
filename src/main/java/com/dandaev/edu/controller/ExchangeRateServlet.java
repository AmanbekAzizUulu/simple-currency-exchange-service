package com.dandaev.edu.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.CurrencyDaoImpl;
import com.dandaev.edu.dao.ExchangeRateDao;
import com.dandaev.edu.dao.ExchangeRateDaoImpl;
import com.dandaev.edu.dto.response.ExchangeRateResponseDto;
import com.dandaev.edu.mapper.JsonMapper;
import com.dandaev.edu.service.ExchangeRateService;
import com.dandaev.edu.service.ExchangeRateServiceImpl;
import com.dandaev.edu.validator.BigDecimalValidator;
import com.dandaev.edu.validator.ExchangeRatePathValidator;
import com.dandaev.edu.validator.ExchangeRateValidator;
import com.dandaev.edu.validator.RequestParamValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExchangeRateServlet extends HttpServlet {
	private ExchangeRateService exchangeRateService;
	private CurrencyDao currencyDao;
	private ExchangeRateDao exchangeRateDao;

	@ Override
	public void init () throws ServletException {
		exchangeRateDao = new ExchangeRateDaoImpl();
		currencyDao = new CurrencyDaoImpl();

		exchangeRateService = new ExchangeRateServiceImpl(exchangeRateDao, currencyDao);
	}

	@ Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String[] codes = ExchangeRatePathValidator.extractAndValidate(req.getPathInfo());

		ExchangeRateResponseDto responseDto = exchangeRateService.getExchangeRate(codes[0], codes[1]);

		writeJson(resp, HttpServletResponse.SC_OK, responseDto);
	}

	@ Override
	protected void doPatch (HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String[] codes = ExchangeRatePathValidator.extractAndValidate(req.getPathInfo());
		String urlBody = req.getReader().lines().collect(Collectors.joining());

		Map<String, String> params = parseFormUrlEncoded(urlBody);

		String rateStr = params.get("rate");

		RequestParamValidator.requireNotEmpty("rate", rateStr);

		BigDecimal newRate = BigDecimalValidator.parseAndValidate(rateStr, "rate");
		ExchangeRateValidator.validateRate(newRate);

		ExchangeRateResponseDto updatedDto = exchangeRateService.updateExchangeRate(codes[0], codes[1], newRate);
		writeJson(resp, HttpServletResponse.SC_OK, updatedDto);
	}

	private Map<String, String> parseFormUrlEncoded (String urlBody) {
		Map<String, String> result = new HashMap<>();

		if (urlBody == null || urlBody.isEmpty()) {
			return result;
		}

		String[] pairs = urlBody.split("&");

		for (String pair : pairs) {
			String[] keyValue = pair.split("=", 2);
			String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
			String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8) : "";
			result.put(key, value);
		}

		return result;
	}

	private void writeJson (HttpServletResponse resp, int status, Object data) throws IOException {
		resp.setStatus(status);
		resp.setContentType("application/json");
		resp.getWriter().write(JsonMapper.MAPPER.writeValueAsString(data));
	}
}
