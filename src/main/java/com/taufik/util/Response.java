package com.taufik.util;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class Response {
	private String response;
	private String responseValue;
}
