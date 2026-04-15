package com.textseries.model;

import lombok.Data;

@Data
public class QuestionRequestDTO {

    private String question;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctAnswer;  
    private Long testId;           
}