package com.dandaev.edu.dto.response;

import lombok.Builder;

@ Builder
public record CurrencyResponseDto(Long id, String name, String code, String sign) {
}
