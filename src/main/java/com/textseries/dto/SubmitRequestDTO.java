package com.textseries.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class SubmitRequestDTO {

	private String studentName;
	private Long testId;
    private Map<Long, String> answers;

    private LocalDateTime startTime; // 🔥 new
}