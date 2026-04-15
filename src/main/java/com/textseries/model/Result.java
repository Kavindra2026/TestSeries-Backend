package com.textseries.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentName;
    private String email;
    private double score;
    private int totalQuestions;
    private int correctAnswers;
    private int wrongAnswers;
    
    private String category;  
    private LocalDateTime submittedAt; 
}