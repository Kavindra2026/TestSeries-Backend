package com.textseries.dto;
 

import lombok.Data;
import java.util.List;

@Data
public class QuizResponseDTO {

    private List<QuestionDTO> questions;
    private int timeLimit; // minutes
}