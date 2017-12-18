package com.taufik.util;

import java.util.List;

import org.springframework.stereotype.Component;

import com.taufik.controller.FormulatrixRepoController;

import lombok.Data;

@Component
@Data
public class ResponseData extends Response{
	private List<DataGet> data;
	
	@Data
	public static class DataGet {
		private Integer no;
		private String itemContent;
	}
}
