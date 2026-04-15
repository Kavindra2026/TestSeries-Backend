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
public class Test {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String testName;   
    private String subject;
    private int totalQuestions;
    private int timeLimit;
    private boolean active;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}