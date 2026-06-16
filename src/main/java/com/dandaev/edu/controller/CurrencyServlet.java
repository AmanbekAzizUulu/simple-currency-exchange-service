package com.dandaev.edu.controller;

import java.io.IOException;

import com.dandaev.edu.dao.CurrencyDao;
import com.dandaev.edu.dao.CurrencyDaoImpl;
import com.dandaev.edu.dto.response.CurrencyResponseDto;
import com.dandaev.edu.mapper.JsonMapper;
import com.dandaev.edu.service.CurrencyService;
import com.dandaev.edu.service.CurrencyServiceImpl;
import com.dandaev.edu.validator.CurrencyPathValidator;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CurrencyServlet extends HttpServlet {
	private CurrencyDao currencyDaoImpl;
	private CurrencyService currencyService;

	@ Override
	public void init () throws ServletException {
		currencyDaoImpl = new CurrencyDaoImpl();
		currencyService = new CurrencyServiceImpl(currencyDaoImpl);
	}

	@ Override
	protected void doGet (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();

		String code = CurrencyPathValidator.extractAndValidate(pathInfo);
		CurrencyResponseDto dto = currencyService.getCurrencyByCode(code);

		writeResponseJson(resp, dto);
	}

	private void writeResponseJson (HttpServletResponse resp, CurrencyResponseDto responseDto) throws IOException {
		resp.setStatus(HttpServletResponse.SC_OK);
		resp.setContentType("application/json");
		resp.getWriter().write(JsonMapper.MAPPER.writeValueAsString(responseDto));
	}
}
