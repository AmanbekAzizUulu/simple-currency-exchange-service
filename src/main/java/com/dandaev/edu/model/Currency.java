package com.dandaev.edu.model;

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
public class Currency {
	private Long id;
	private String code;
	private String fullName;
	private String sign;
}
