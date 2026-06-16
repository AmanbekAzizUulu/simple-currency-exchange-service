package com.dandaev.edu.controller;

import java.io.IOException;
import java.util.List;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.CurrencyDaoImpl;
import com.dandaev.edu.dto.request.CurrencyRequestDto;
import com.dandaev.edu.dto.response.CurrencyResponseDto;
import com.dandaev.edu.mapper.JsonMapper;
import com.dandaev.edu.service.CurrencyService;
import com.dandaev.edu.service.CurrencyServiceImpl;
import com.dandaev.edu.validator.RequestParamValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CurrenciesServlet extends HttpServlet {
	private CurrencyDao currencyDaoImpl;
	private CurrencyService currencyService;

	@ Override
	public void init () throws ServletException {
		currencyDaoImpl = new CurrencyDaoImpl();
		currencyService = new CurrencyServiceImpl(currencyDaoImpl);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		List<CurrencyResponseDto> list = currencyService.getAllCurrencies();
		resp.setContentType("application/json");
		resp.getWriter().write(JsonMapper.MAPPER.writeValueAsString(list));
	}

	@ Override
	protected void doPost (HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String name = req.getParameter("name");
		String code = req.getParameter("code");
		String sign = req.getParameter("sign");

		RequestParamValidator.requireNotEmpty("name", name);
		RequestParamValidator.requireNotEmpty("code", code);
		RequestParamValidator.requireNotEmpty("sign", sign);

		CurrencyRequestDto requestDto = new CurrencyRequestDto(code, name, sign);
		CurrencyResponseDto responseDto = currencyService.createCurrency(requestDto);

		writeResponseJson(resp, responseDto);
	}


	private void writeResponseJson(HttpServletResponse resp, CurrencyResponseDto responseDto) throws IOException{
		resp.setStatus(HttpServletResponse.SC_CREATED);
		resp.setContentType("application/json");
		resp.getWriter().write(JsonMapper.MAPPER.writeValueAsString(responseDto));
	}
}
