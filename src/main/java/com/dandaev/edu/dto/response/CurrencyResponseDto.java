package com.dandaev.edu.dto.response;

import lombok.Builder;

@ Builder
public record CurrencyResponseDto(Long id, String fullName, String code, String sign) {
}
