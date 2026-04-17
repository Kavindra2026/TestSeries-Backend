package com.textseries.dto;

import lombok.Data;
  
import java.util.Map;

@Data
public class SubmitRequestDTO {

	private Long testId;
    private Map<Long, String> answers;
 
   
}