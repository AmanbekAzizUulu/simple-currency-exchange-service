package com.dandaev.edu.dto.request;

import lombok.Builder;

@ Builder
public record CurrencyRequestDto(String code, String fullName, String sign) {
}
