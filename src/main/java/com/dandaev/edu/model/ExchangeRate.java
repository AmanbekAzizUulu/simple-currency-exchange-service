package com.dandaev.edu.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ Data
@ NoArgsConstructor
@ AllArgsConstructor
@ Builder
@ ToString
public class ExchangeRate {
	private Long id;
	private Long baseCurrencyId;
	private Long targetCurrencyId;
	private BigDecimal rate;
}
