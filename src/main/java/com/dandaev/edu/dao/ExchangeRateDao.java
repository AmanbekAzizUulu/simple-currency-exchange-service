package com.dandaev.edu.dao;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.dandaev.edu.model.ExchangeRate;

public interface ExchangeRateDao {
	List<ExchangeRate> findAll ();
	Optional<ExchangeRate> findByPair (String baseCode, String targetCode);
	ExchangeRate persist (ExchangeRate exchangeRate);
	ExchangeRate updateRate (String baseCode, String targetCode, BigDecimal newRate);
}
