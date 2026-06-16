package com.dandaev.edu.filter;

import java.io.IOException;

import com.dandaev.edu.exception.ApplicationException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

public class ExceptionHandlingFilter implements Filter {

	@ Override
	public void doFilter (ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		try {
			chain.doFilter(request, response);
		} catch (ApplicationException e) {
			writeJsonError((HttpServletResponse) response, e.getHttpStatus(), e.getMessage());
		} catch (Exception e) {
			writeJsonError((HttpServletResponse) response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
		}
	}

	private void writeJsonError (HttpServletResponse resp, int status, String message) throws IOException {
		if (!resp.isCommitted()) {
			resp.resetBuffer(); 
			resp.setStatus(status);
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");

			String json = String.format("{\"message\": \"%s\"}", escapeJson(message));

			resp.getWriter().write(json);
		}
	}

	private String escapeJson (String message) {
		if (message == null) {
			return "";
		}
		return message.replace("\\", "\\\\")
		              .replace("\"", "\\\"")
		              .replace("\n", "\\n")
		              .replace("\r", "\\r");
	}
}
