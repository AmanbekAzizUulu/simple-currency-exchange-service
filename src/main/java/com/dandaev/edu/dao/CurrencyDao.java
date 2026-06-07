package com.dandaev.edu.dao;

import java.util.List;
import java.util.Optional;

import com.dandaev.edu.model.Currency;

public interface CurrencyDao {
	List<Currency> findAll ();
	Optional<Currency> findById (Long id);
	Optional<Currency> findByCode (String code);
	Currency persist (Currency currency);
}
