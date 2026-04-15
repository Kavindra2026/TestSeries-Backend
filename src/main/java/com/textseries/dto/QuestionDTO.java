package com.textseries.dto;

import lombok.Data;

@Data
public class QuestionDTO {
	
	
	private Long id;
	private String question;
	
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;

}
